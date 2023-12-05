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
import com.ge.research.rack.utils.ProjectUtils;
import com.ge.research.rack.utils.RackConsole;
import com.ge.research.rack.views.OntologyTreeView;
import com.ge.research.semtk.sparqlX.client.SparqlQueryClient;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class UploadOwlModelsHandler extends AbstractHandler {

    public static ArrayList<String> getOwlFiles(String basedir) {
        File dir = new File(basedir);

        if (!dir.isDirectory()) {
            RackConsole.getConsole().error("The selected item is not a project");
        }

        String owlmodels = basedir + "/OwlModels";
        File subdir = new File(owlmodels);
        if (!subdir.exists()) {
            RackConsole.getConsole()
                    .error(
                            "No OwlModels folder present in the selected project! Please create an OwlModels folder with a import.yaml");
            return null;
        }

        File owlYaml = new File(owlmodels + "/import.yaml");
        if (!owlYaml.exists()) {
            RackConsole.getConsole()
                    .error(
                            "Selected project has no import.yaml, please create an import.yaml file to upload the owl ontologies");
            return null;
        }
        Map<String, Object> owlFiles = null;
        try {
            owlFiles = (Map<String, Object>) ProjectUtils.readYaml(owlYaml.getAbsolutePath());
        } catch (Exception e) {
            RackConsole.getConsole().error("Unable to read import.yaml");
            return null;
        }

        if (owlFiles == null || !(owlFiles instanceof Map) || (!owlFiles.containsKey("files"))) {
            RackConsole.getConsole().error("Ill formed import.yaml, please check");
            return null;
        }

        Object files = owlFiles.get("files");

        if (files == null || !(files instanceof ArrayList)) {
            RackConsole.getConsole().error("Ill formed import.yaml, please check");
            return null;
        }
        files = (ArrayList<String>) owlFiles.get("files");

        if (files == null) {
            RackConsole.getConsole()
                    .error(
                            "import.yaml must contain a list of filenames, cannot process files, probably there are no files");
            return null;
        }

        ArrayList<String> importingOwlFiles = new ArrayList<>();
        for (String file : (ArrayList<String>) files) {
            File f = new File(owlmodels + "/" + file);
            if (!f.exists()) {
                RackConsole.getConsole()
                        .warning("File " + file + " listed in import.yaml is missing");
            } else {
                importingOwlFiles.add(f.getAbsolutePath());
            }
        }

        return importingOwlFiles;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

        /*String commandId = event.getCommand().getId();
        String dir = "";
        if (commandId.equals("rackplugin.commands.uploadCoreOntology")) {
            dir = ProjectUtils.getCoreProjectPath();
        } else {
            dir = ProjectUtils.getOverlayProjectPath();
        } */

        List<String> selection = HandlerUtils.getCurrentSelection(event);
        String baseDir = selection.get(0);
        File baseDirFile = new File(baseDir);
        String filepath = baseDirFile.getAbsolutePath();
        File dir = new File(filepath);

        if (!dir.exists()) {
            RackConsole.getConsole()
                    .error(
                            "Project has no Owl Models, try building and uploading Owl Files again, note that the Owl Files to be uploaded need to be listed in an import.yaml");
            return null;
        }
        final String path = filepath;
        Thread thread =
                new Thread(
                        () -> {
                            try {

                                ArrayList<String> owlFiles = getOwlFiles(path);
                                if (owlFiles == null) {
                                    return;
                                }
                                SparqlQueryClient qAuthClient =
                                        ConnectionUtil.getOntologyUploadClient();
                                for (String owl : owlFiles) {
                                    File owlFile = new File(owl);
                                    RackConsole.getConsole()
                                            .println(
                                                    "Uploading Owl File: "
                                                            + owlFile.getName()
                                                            + "...");
                                    qAuthClient.uploadOwl(owlFile);
                                    RackConsole.getConsole().print("OK");
                                }

                                // Populate ontology tree

                            } catch (Exception e) {
                                RackConsole.getConsole()
                                        .error(
                                                "Ontology processing/upload failed, make sure you are connected to Sparql and have configured the correct Core and Overlay Projects");
                                return;
                            }

                            RackConsole.getConsole().println("Loaded All Owl models");
                        });
        thread.start();
        try {
            thread.join();
            IViewPart view = window.getActivePage().findView(OntologyTreeView.ID);
            window.getActivePage().hideView(view);
            window.getActivePage().showView(OntologyTreeView.ID);

        } catch (Exception e) {

        }
        return null;
    }
}
