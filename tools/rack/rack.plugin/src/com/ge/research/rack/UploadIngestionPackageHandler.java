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
import com.ge.research.rack.views.RackPreferencePage;
import com.ge.research.semtk.services.client.RestClientConfig;
import com.ge.research.semtk.services.client.UtilityClient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
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
import org.eclipse.ui.handlers.HandlerUtil;

public class UploadIngestionPackageHandler extends AbstractHandler {

    private static final boolean CLEAR_FOOTPRINT_GRAPHS_ON_LOAD = true;

    private static final String ZIP = "zip";
    private static final String UPLOAD_QUEUED = "Ingestion package %s queued for upload";
    private static final String UPLOAD_FAILED = "Ingestion package %s upload failed";

    private static final String NO_SELECTED_PROJECT =
            "Selected resources(s) are not valid ingestion package project(s)";

    private static final String INVALID_PROJECT =
            "The selected item is not a valid ingestion package project";

    private static final String GENERATING_PROJECT = "Compressing ingestion package: %s";

    private static final SimpleDateFormat PACKAGE_NAME_FORMAT =
            new SimpleDateFormat("'%s-'yyyyMMddHHmmss'.zip'");

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

    /** Support both .zip and project directories */
    public static String getNodegroupFilepaths(final String basedir) {

        final File dir = new File(basedir);

        if (!dir.isDirectory() && !ZIP.equals(FilenameUtils.getExtension(basedir))) {
            RackConsole.getConsole().error(INVALID_PROJECT);
        }

        return dir.getAbsolutePath();
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        if (!startRun()) {
            return null;
        }

        final Optional<TreePath[]> paths =
                Optional.ofNullable(event)
                        .map(HandlerUtil::getCurrentSelection)
                        .map(s -> ((TreeSelection) s).getPaths())
                        .filter(cast -> null != cast);

        if (paths.isEmpty() || paths.get().length != 1) {
            RackConsole.getConsole().error(NO_SELECTED_PROJECT);
            endRun();
            return null;
        }

        final Optional<File> selected_project =
                Stream.of(paths.get())
                        .map(p -> (IResource) p.getLastSegment())
                        .map(f -> new File(f.getLocation().toFile().getAbsolutePath()))
                        .findAny();

        if (selected_project.isEmpty()) {
        	
            RackConsole.getConsole().error(NO_SELECTED_PROJECT);
            endRun();
            
        } else {
            // End run is called in the async callback
            scheduleUploadNodegroupFile(selected_project.get().toPath(), () -> endRun());
        }

        return null;
    }

    private Job scheduleUploadNodegroupFile(
            final Path ingestionPackageFilepath, final Runnable callback) {

        final IngestionPackageUploadJob uploadIngestionPackage =
                new IngestionPackageUploadJob(ingestionPackageFilepath, callback);

        uploadIngestionPackage.schedule();

        return uploadIngestionPackage;
    }

    private static class IngestionPackageUploadJob extends Job {

        private static final String UPLOAD_NAME = "Upload Ingestion Package %s";
        private final Path ingestionPackageFilepath;

        public IngestionPackageUploadJob(
                final Path ingestionPackageFilepath, final Runnable asyncCallback) {

            super(String.format(UPLOAD_NAME, ingestionPackageFilepath));
            this.ingestionPackageFilepath = ingestionPackageFilepath;

            this.addJobChangeListener(
                    new JobChangeAdapter() {
                        @Override
                        public void done(IJobChangeEvent event) {
                            if (event.getResult() != Status.OK_STATUS) {
                                RackConsole.getConsole()
                                        .error(
                                                String.format(
                                                        UPLOAD_FAILED, ingestionPackageFilepath));
                            }
                            asyncCallback.run();
                        }

                        @Override
                        public void scheduled(IJobChangeEvent event) {
                            RackConsole.getConsole()
                                    .println(
                                            String.format(UPLOAD_QUEUED, ingestionPackageFilepath));
                        }
                    });
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {

            try {

                final Path zipPath;
                if (ingestionPackageFilepath.toFile().isFile()) {

                    zipPath = ingestionPackageFilepath;

                } else {
                	
                    final String packageName =
                            String.format(
                                    PACKAGE_NAME_FORMAT.format(new Date()),
                                    ingestionPackageFilepath);

                    zipPath = zipIt(ingestionPackageFilepath, Paths.get(packageName));
                }

                uploadIngestionZip(zipPath);

            } catch (final Exception e) {
                return Status.CANCEL_STATUS;
            }
            return Status.OK_STATUS;
        }
    }

    private static Path zipIt(final Path folder, final Path zipFilepath) throws IOException {
        try (final FileOutputStream fos = new FileOutputStream(zipFilepath.toFile());
                final ZipOutputStream zipStream = new ZipOutputStream(fos)) {

            RackConsole.getConsole()
                    .println(String.format(GENERATING_PROJECT, zipFilepath.toString()));

            Files.walkFileTree(
                    folder,
                    new SimpleFileVisitor<Path>() {

                        public FileVisitResult visitFile(
                                final Path filePath, final BasicFileAttributes attrs)
                                throws IOException {

                            final String uFilePath =
                                    FilenameUtils.separatorsToUnix(
                                            folder.relativize(filePath).toString());

                            zipStream.putNextEntry(new ZipEntry(uFilePath));

                            Files.copy(filePath, zipStream);
                            zipStream.closeEntry();
                            return FileVisitResult.CONTINUE;
                        }
                    });
            return zipFilepath;
        }
    }

    private static void uploadIngestionZip(final Path zipFilepath) throws Exception {

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
                if (semTkOutputLine.contains("Error")) {
                    RackConsole.getConsole().error(semTkOutputLine);
                } else {
                    RackConsole.getConsole().println(semTkOutputLine);
                }
            }
        }
    }
}
