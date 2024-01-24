/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2023, General Electric Company and Galois, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ge.research.rack;

import com.ge.research.rack.analysis.utils.RackQueryUtils;
import com.ge.research.rack.utils.RackConsole;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UploadNodegroupsHandler extends AbstractHandler {

    // FIXME: Reference NodegroupsView ID from class when added
    public static final String NODEGROUPS_ID = "rackplugin.views.NodegroupsView";

    private static final String JSON_EXTENSION = "json";
    private static final String[] SCOPED_EXTENSIONS = new String[] {JSON_EXTENSION};

    private static final String UPLOAD_QUEUED = "Nodegroup file %s queued for upload";
    private static final String UPLOAD_FAILED = "Nodegroup file %s upload failed";

    private static final String INVALID_PROJECT =
            "The selected item is not a valid nodegroup project";

    private static final String NO_NODEGROUP_FILES =
            "Selected resource has no .json nodegroup files, try refreshing project";

    // Debounce runs between all threads
    private static boolean isRunning;
    private static Object lock = new Object();

    private static boolean startRun() {
        synchronized (lock) {
            return isRunning ? false : (isRunning = true);
        }
    }

    private static void endRun() {
        synchronized (lock) {
            isRunning = false;
        }
    }

    /**
     * Recursively searches for all json files in a provided base directory
     *
     * @param basedir base directory to search
     * @return list of absolute file paths to found json files
     */
    public static List<String> getNodegroupFilepaths(final String basedir) {

        final File dir = new File(basedir);

        if (!dir.exists()) {
            RackConsole.getConsole().error(INVALID_PROJECT);
        }

        if (dir.isFile()) {
            return JSON_EXTENSION.equals(FilenameUtils.getExtension(basedir))
                    ? List.of(dir.getAbsolutePath())
                    : List.of();
        }

        return FileUtils.listFiles(dir, SCOPED_EXTENSIONS, true).stream()
                .map(f -> f.getAbsolutePath())
                .collect(Collectors.toList());
    }

    private void showNodegroupsView() {
        try {

            final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

            final IViewPart view = window.getActivePage().findView(NODEGROUPS_ID);
            window.getActivePage().hideView(view);
            window.getActivePage().showView(NODEGROUPS_ID);

        } catch (Exception e) {
            // Silently fail if the view does not exist or cannot be opened
        }
    }

    /**
     * Recursively searches for and uploads all json files as nodegroups in a right-clicked
     * directory.
     *
     * <p>Fails silently when a json file cannot be uploaded as a nodegroup and before proceeding to
     * the next file.
     */
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {

        if (!startRun()) {
            return null;
        }

        final Optional<TreePath[]> paths =
                Optional.ofNullable(event)
                        .map(HandlerUtil::getCurrentSelection)
                        .map(s -> ((TreeSelection) s).getPaths())
                        .filter(cast -> null != cast);

        if (paths.isEmpty() || paths.get().length < 1) {
            RackConsole.getConsole().error(NO_NODEGROUP_FILES);
            endRun();
            return null;
        }

        // Multi-Select Enabled
        final Set<String> selected_file_resources =
                Stream.of(paths.get())
                        .map(p -> (IResource) p.getLastSegment())
                        .map(f -> f.getLocation().toFile().getAbsolutePath())
                        // Recursively search for nodegroup files
                        .flatMap(dp -> getNodegroupFilepaths(dp).stream())
                        .collect(Collectors.toSet());

        if (selected_file_resources.isEmpty()) {

            RackConsole.getConsole().error(NO_NODEGROUP_FILES);
            endRun();

        } else {

            new NodegroupsUploadJob(
                            selected_file_resources,
                            () -> {
                                // End the run once all nodegroup files have been uploaded
                                endRun();
                                showNodegroupsView();
                            })
                    .schedule();
        }

        return null;
    }

    private static class NodegroupsUploadJob extends Job {

        private static final String UPLOAD_NAME = "Upload RACK Nodegroups %s";
        private final Set<String> nodegroupFilepaths;

        public NodegroupsUploadJob(
                final Set<String> nodegroupFilepaths, final Runnable asyncCallback) {

            super(String.format(UPLOAD_NAME, nodegroupFilepaths));
            this.nodegroupFilepaths = nodegroupFilepaths;

            this.addJobChangeListener(
                    new JobChangeAdapter() {

                        @Override
                        public void done(IJobChangeEvent event) {
                            if (event.getResult() != Status.OK_STATUS) {
                                RackConsole.getConsole()
                                        .error(String.format(UPLOAD_FAILED, nodegroupFilepaths));
                            }
                            asyncCallback.run();
                        }

                        @Override
                        public void scheduled(IJobChangeEvent event) {
                            RackConsole.getConsole()
                                    .println(String.format(UPLOAD_QUEUED, nodegroupFilepaths));
                        }
                    });
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {

                final String taskName = String.format(UPLOAD_NAME, "");

                monitor.beginTask(taskName, nodegroupFilepaths.size());

                nodegroupFilepaths.stream()
                        .forEach(
                                nodegroupFilepath -> {
                                    final String nodegroupId =
                                            RackQueryUtils.getQueryIdFromFilePath(
                                                    nodegroupFilepath);

                                    RackQueryUtils.uploadQueryNodegroupToRackStore(
                                            nodegroupId, nodegroupFilepath);

                                    monitor.worked(1);
                                });

                monitor.done();

            } catch (final Exception e) {
                return Status.CANCEL_STATUS;
            }
            return Status.OK_STATUS;
        }
    }
}
