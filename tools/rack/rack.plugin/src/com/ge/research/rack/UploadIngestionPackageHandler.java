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

import com.ge.research.rack.utils.ConnectionUtil;
import com.ge.research.rack.utils.RackConsole;
import com.ge.research.rack.utils.RackManifestIngestionBuilderUtil;
import com.ge.research.rack.utils.RackManifestIngestionBuilderUtil.IngestionBuilderException;
import com.ge.research.rack.views.RackPreferencePage;
import com.ge.research.semtk.services.client.RestClientConfig;
import com.ge.research.semtk.services.client.UtilityClient;
import com.google.common.base.Strings;

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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

public class UploadIngestionPackageHandler extends AbstractHandler {

    private static final boolean CLEAR_FOOTPRINT_GRAPHS_ON_LOAD = true;

    private static final String ERROR_LINE = "Error";
    private static final String ZIP = "zip";
    private static final String UPLOAD_DEBOUNCED = "Ingestion package already uploading";
    private static final String UPLOAD_STAGED = "Ingestion package upload staged";

    private static final String UPLOAD_QUEUED = "Ingestion package %s upload queued";
    private static final String UPLOAD_FAILED = "Ingestion package %s upload failed";

    private static final String CREATION_QUEUED = "Ingestion package %s creation queued";
    private static final String CREATION_FAILED = "Ingestion package %s creation failed";

    private static final String TMP_PCKG_FILE_PREFIX = "rack-ingestion-";

    private static final String NO_SELECTED_PROJECT =
            "Selected resources(s) are not valid ingestion package project(s)";

    private static final String INVALID_PROJECT =
            "The selected item is not a valid ingestion package project";

    private static final String GENERATING_PROJECT = "Compressing ingestion package: %s";
    // private static final String GENERATED_PROJECT = "Compressed ingestion package: %s";

    private static final SimpleDateFormat PACKAGE_NAME_FORMAT =
            new SimpleDateFormat("'%s-'yyyyMMddHHmmss'.zip'");

    private static String[] FILTER_NAMES = new String[] {"Zip Files", "All Files (*)"};
    private static String[] FILTER_EXTENSIONS = new String[] {"*.zip", "*"};

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

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        return execute(event, true, false);
    }

    public Object execute(ExecutionEvent event, boolean shouldUpload, boolean keepZip) {

        final TreePath[] eventResourcePaths =
                Optional.ofNullable(event)
                        .map(HandlerUtil::getCurrentSelection)
                        .map(s -> ((TreeSelection) s).getPaths())
                        .filter(cast -> null != cast)
                        .orElseGet(() -> new TreePath[0]);

        if (eventResourcePaths.length != 1) {
            RackConsole.getConsole().error(NO_SELECTED_PROJECT);
            MessageDialog.openError(null, "Ingestion failed", NO_SELECTED_PROJECT);
            RackConsole.getConsole().activate();
            return null;
        }

        final Optional<File> selectedProject =
                Stream.of(eventResourcePaths)
                        .map(p -> (IResource) p.getLastSegment())
                        .map(f -> new File(f.getLocation().toFile().getAbsolutePath()))
                        .findAny();

        if (selectedProject.isEmpty()) {
            RackConsole.getConsole().error(NO_SELECTED_PROJECT);
            MessageDialog.openError(null, "Ingestion failed", NO_SELECTED_PROJECT);
            RackConsole.getConsole().activate();
        }

        try {

            if (shouldUpload && !startRun()) {
                RackConsole.getConsole().error(UPLOAD_DEBOUNCED);
                MessageDialog.openError(null, "Ingestion failed", UPLOAD_DEBOUNCED);
                RackConsole.getConsole().activate();
                return null;
            }

            final Path selectedProjectPath = selectedProject.get().toPath();

            // If the selection is a zip file, upload otherwise zip and upload
            final Path ingestionZipPath;

            if (selectedProject.get().isFile()) {

                ingestionZipPath = selectedProjectPath;
                keepZip = true; // Don't delete an existing zip file

            } else if (keepZip) {

                final String newFilepath =
                        promptForSaveFilepath(
                                selectedProjectPath, HandlerUtil.getActiveShell(event));

                // If the user clicks cancel on prompt
                if (shouldUpload && Strings.isNullOrEmpty(newFilepath)) {
                    endRun();
                    return null;
                }

                ingestionZipPath = Paths.get(newFilepath);
            } else {
                // use a temporary file
                ingestionZipPath = Files.createTempFile(TMP_PCKG_FILE_PREFIX, ".zip");
            }

            // End run is called in the async callback
            new IngestionPackageUploadJob(
                            selectedProjectPath,
                            ingestionZipPath,
                            shouldUpload,
                            keepZip,
                            () -> {
                                if (shouldUpload) endRun();
                            })
                    .schedule();

        } catch (final Exception e) {

            RackConsole.getConsole().error(e.getMessage());
            RackConsole.getConsole().activate();
            endRun();
        }

        return null;
    }

    /** Opens a file save dialog at the project's parent dir with a default name populated * */
    private String promptForSaveFilepath(final Path selectedProjectPath, final Shell activeShell) {

        final FileDialog fileDialog = new FileDialog(activeShell, SWT.SAVE);
        fileDialog.setFilterNames(FILTER_NAMES);
        fileDialog.setFilterExtensions(FILTER_EXTENSIONS);
        fileDialog.setFilterPath(selectedProjectPath.getParent().toString());

        final String defaultZipName =
                String.format(
                        PACKAGE_NAME_FORMAT.format(new Date()), selectedProjectPath.getFileName());

        fileDialog.setFileName(defaultZipName);

        return Optional.ofNullable(fileDialog.open()).orElse("");
    }

    /** Support both .zip and folder directories */
    public static String getNodegroupFilepaths(final String basedir) {

        final File dir = new File(basedir);

        if (!dir.isDirectory() && !ZIP.equals(FilenameUtils.getExtension(basedir))) {
            RackConsole.getConsole().error(INVALID_PROJECT);
        }

        return dir.getAbsolutePath();
    }

    private static class IngestionPackageUploadJob extends Job {

        private static final String UPLOAD_NAME = "Upload Ingestion Package %s";
        // below paths are expected to be when a zip resource is selected
        private final Path ingestionPackageSource; // either .zip or folder path
        private final Path ingestionPackageZipFilepath; // .zip path containing ingestion resources
        private final boolean upload;
        private final boolean keepZip;

        public IngestionPackageUploadJob(
                final Path ingestionPackageSource,
                final Path ingestionPackageFilepath,
                final boolean upload,
                final boolean keepZip,
                final Runnable asyncCallback) {

            super(String.format(UPLOAD_NAME, ingestionPackageSource));
            this.ingestionPackageSource = ingestionPackageSource;
            this.ingestionPackageZipFilepath = ingestionPackageFilepath;
            this.upload = upload;
            this.keepZip = keepZip;
            addChangeListeners(asyncCallback);
        }

        private void addChangeListeners(final Runnable asyncCallback) {

            final JobChangeAdapter jobChangeAdapter =
                    new JobChangeAdapter() {

                        @Override
                        public void done(IJobChangeEvent event) {
                            if (event.getResult() != Status.OK_STATUS) {
                                RackConsole.getConsole()
                                        .error(
                                                String.format(
                                                        upload ? UPLOAD_FAILED : CREATION_FAILED,
                                                        ingestionPackageZipFilepath));
                            }
                            asyncCallback.run();
                        }

                        @Override
                        public void scheduled(IJobChangeEvent event) {
                            RackConsole.getConsole()
                                    .print(
                                            String.format(
                                                    upload ? UPLOAD_QUEUED : CREATION_QUEUED,
                                                    ingestionPackageZipFilepath));
                        }
                    };

            this.addJobChangeListener(jobChangeAdapter);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {

            try {
                /* If the selected resource is a folder zip before upload */
                if (ingestionPackageSource.toFile().isDirectory()) {
                    // ingestionPackageZipFilepath.toFile().mkdir();
                    ingestionPackageZipFilepath.toFile().setReadable(true, false);
                    ingestionPackageZipFilepath.toFile().setWritable(true, false);
                    zipIt(ingestionPackageSource, ingestionPackageZipFilepath);
                }

                if (upload) {
                    RackConsole.getConsole().print(UPLOAD_STAGED);
                    uploadIngestionZip(ingestionPackageZipFilepath, monitor);
                }
                if (!keepZip) {
                    Files.delete(ingestionPackageZipFilepath);
                }

            } catch (final Exception e) {
                RackConsole.getConsole().error(e.getMessage());
                return Status.CANCEL_STATUS;
            }
            return Status.OK_STATUS;
        }
    }

    private static Path zipIt(Path folder, Path zipFilepath)
            throws IOException, IngestionBuilderException {

        zipFilepath.toFile().setReadable(true, false);
        zipFilepath.toFile().setWritable(true, false);

        try (FileOutputStream fos = new FileOutputStream(zipFilepath.toFile());
                ZipOutputStream zipStream = new ZipOutputStream(fos)) {

            RackConsole.getConsole()
                    .print(String.format(GENERATING_PROJECT, zipFilepath.toString()));

            new RackManifestIngestionBuilderUtil().zipManifestResources(folder, zipStream);
        }

        return zipFilepath;
    }

    private static void uploadIngestionZip(Path zipFilepath, IProgressMonitor monitor)
            throws Exception {

        if (monitor.isCanceled()) {
            return;
        }

        final UtilityClient semtkUtilityClient =
                new UtilityClient(
                        new RestClientConfig(
                                ConnectionUtil.getProtocol(),
                                ConnectionUtil.getServer(),
                                ConnectionUtil.getUtilityPort(),
                                ConnectionUtil.getServiceInfoEndpoint()));

        try (final BufferedReader reader =
                semtkUtilityClient.execLoadIngestionPackage(
                        zipFilepath.toFile(),
                        RackPreferencePage.getConnURL(),
                        RackPreferencePage.getConnType(),
                        CLEAR_FOOTPRINT_GRAPHS_ON_LOAD,
                        RackPreferencePage.getDefaultModelGraph(),
                        RackPreferencePage.getDefaultDataGraph())) {

            String semTkOutputLine;
            while (null != (semTkOutputLine = reader.readLine())) {
                if (monitor.isCanceled()) {
                    reader.close();
                    return;
                }
                if (semTkOutputLine.contains(ERROR_LINE)) {
                    RackConsole.getConsole().errorEcho(semTkOutputLine);
                } else {
                    RackConsole.getConsole().printEcho(semTkOutputLine);
                }
            }
        }
    }
}
