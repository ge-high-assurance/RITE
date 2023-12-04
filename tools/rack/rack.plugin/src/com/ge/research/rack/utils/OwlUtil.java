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
package com.ge.research.rack.utils;

import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class OwlUtil {

    public static ArrayList<String> getOwlClasses(String owlFile) {
        ArrayList<String> classes = new ArrayList<>();
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        model.getDocumentManager().setProcessImports(true);

        try {
            model.read(new FileInputStream(owlFile), null);
        } catch (FileNotFoundException e) {
            ErrorMessageUtil.error("Cannot read Owl File: " + owlFile + ", file not found");
        }

        StmtIterator it = model.listStatements();

        while (it.hasNext()) {

            Statement stmt = it.next();
            Triple triple = stmt.asTriple();
            if (triple.getObject().toString().equals("http://www.w3.org/2002/07/owl#Class")) {
                if (triple.getSubject() instanceof org.apache.jena.graph.Node_URI) {
                    classes.add(triple.getSubject().getURI());
                }
            }
        }
        return classes;
    }

    //    private static ArrayList<String> getProjectClasses(String dir, SubMonitor subMonitor) {
    //
    //        ArrayList<String> classes = new ArrayList<>();
    //        File ontDir = new File(dir + "/OwlModels/");
    //        if (!ontDir.exists() || !ontDir.isDirectory()) {
    //            IProgressMonitor monitor = new NullProgressMonitor();
    //            ProjectUtils.buildProjects(
    //                    ResourcesPlugin.getWorkspace(), monitor, new HashSet<String>());
    //        }
    //
    //        if (!ontDir.exists()) {
    //            ErrorMessageUtil.error("Cannot find OwlModels folder for project: " +
    // dir);
    //            return classes;
    //        }
    //
    //        ArrayList<String> owlFilePaths =
    //                getOwlFilesFromYAML(ontDir.getAbsolutePath() + "/import.yaml");
    //
    //        if (owlFilePaths == null) {
    //            ErrorMessageUtil
    //                    .error(
    //                            "Unable to extract ontology classes for project at "
    //                                    + dir
    //                                    + ", cannot proceed further");
    //            return null;
    //        }
    //
    //        for (String owlFilePath : owlFilePaths) {
    //            if (subMonitor.isCanceled()) {
    //                return null;
    //            }
    //            File owlFile = new File(owlFilePath);
    //            if (!owlFile.exists() || !owlFile.isFile()) {
    //                ErrorMessageUtil.error(owlFilePath + " is not an owl file");
    //                continue;
    //            }
    //            classes.addAll(getOwlClasses(owlFilePath));
    //        }
    //
    //        return classes;
    //    }

    //    private static ArrayList<String> getOwlFilesFromYAML(String path) {
    //        ArrayList<String> owlFilePaths = new ArrayList<>();
    //        File owlYaml = new File(path);
    //        if (!owlYaml.exists()) {
    //            ErrorMessageUtil.error("No import.yaml at " + path);
    //            return null;
    //        }
    //        Map<String, Object> owlFiles = null;
    //        try {
    //            owlFiles = (Map<String, Object>) ProjectUtils.readYaml(owlYaml.getAbsolutePath());
    //        } catch (Exception e) {
    //            ErrorMessageUtil.error("Unable to read import.yaml");
    //            return null;
    //        }
    //
    //        if (owlFiles == null || !(owlFiles instanceof Map) ||
    // (!owlFiles.containsKey("files"))) {
    //            ErrorMessageUtil.error("Ill formed import.yaml at " + path + ", please
    // check");
    //            return null;
    //        }
    //
    //        Object files = owlFiles.get("files");
    //
    //        if (files == null || !(files instanceof ArrayList)) {
    //            ErrorMessageUtil.error("Ill formed import.yaml at " + path + ", please
    // check");
    //            return null;
    //        }
    //        files = (ArrayList<String>) owlFiles.get("files");
    //
    //        if (files == null) {
    //            ErrorMessageUtil
    //                    .error(
    //                            "import.yaml at "
    //                                    + path
    //                                    + " must contain a list of filenames, cannot process
    // files, probably there are no files");
    //            return null;
    //        }
    //
    //        for (String filename : (ArrayList<String>) files) {
    //            owlFilePaths.add(owlYaml.getParent() + "/" + filename);
    //        }
    //        return owlFilePaths;
    //    }
}
