/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2024, GE Aerospace and Galois, Inc.
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
package com.ge.research.rite;

import com.ge.research.rite.utils.*;
import com.ge.research.rite.views.AssuranceCaseTree;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

public class LoadAssuranceCaseHandler extends RiteHandler {

    public static HashMap<String, TreeNode> index = new HashMap<>();

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        super.execute(event);
        index.clear();
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        TreePath[] paths = ((TreeSelection) selection).getPaths();
        if (paths.length == 1) {
            File file = (File) paths[0].getLastSegment();
            if (file.getFileExtension().equals("sadl")) {
                String filename = file.getName();
                String fileNoExtension = filename.replace(".sadl", "");
                // Check for file name in OwlModels
                String projectdir = file.getProject().getLocation().toFile().getAbsolutePath();
                String owlFilePath = projectdir + "/OwlModels/" + fileNoExtension + ".owl";
                java.io.File owlFile = new java.io.File(owlFilePath);

                if (!owlFile.exists() || !owlFile.isFile()) {

                    // Connect with SADL and force it to generate the OWL files for the else
                    // case
                    IProgressMonitor monitor = new NullProgressMonitor();
                    ProjectUtils.buildProjects(
                            ResourcesPlugin.getWorkspace(), monitor, new HashSet<String>());
                }

                processOwlFile(owlFile);
            }
        }

        try {
            IViewPart view = window.getActivePage().findView(AssuranceCaseTree.ID);
            window.getActivePage().hideView(view);
            window.getActivePage().showView(AssuranceCaseTree.ID);
        } catch (Exception e) {
            RackConsole.getConsole().error("Cannot show assurance case tree");
        }

        return null;
    }

    void updateIndex(String key, TreeNode value) {
        index.remove(key);
        index.put(key, value);
    }

    void processGSNRelationship(OntModel model, Statement stmt) {

        try {

            Triple triple = stmt.asTriple();
            Node predNode = triple.getPredicate();
            TreeNode subjNode = null;
            TreeNode objNode = null;
            Node subj = triple.getSubject();
            OntResource ontSubj = model.getOntResource(stmt.getSubject());
            String subjLocal = subj.getLocalName();
            String subjName = (subjLocal != null) ? subjLocal : subj.toString();

            if (!index.containsKey(subjName)) {
                subjNode = new TreeNode(subjName);
                updateIndex(subjName, subjNode);
            } else {
                subjNode = index.get(subjName);
            }

            GSN subjType = GSN.getType(ontSubj.getRDFType().toString());
            if (subjType != null) {
                subjNode.setGsnType(subjType);
            }

            if (subj.isURI()) {
                // set the comment for the GSN node, can get comment only for resources with URI
                subjNode.setComment(model.getOntResource(subj.getURI()).getComment(null));
            }

            if (predNode.toString()
                    .equalsIgnoreCase("http://sadl.org/sadlimplicitmodel#description")) {
                subjNode.setDescription(triple.getObject().toString());
                return;
            }

            Node obj = triple.getObject();
            String objLocal = obj.getLocalName();
            String objName = (objLocal != null) ? objLocal : obj.toString();
            RDFNode objRDFNode = stmt.getObject();
            OntResource ontObj = model.getOntResource(objRDFNode.asResource());

            if (!index.containsKey(objName)) {
                objNode = new TreeNode(objName);
                updateIndex(objName, objNode);
            } else {
                objNode = index.get(objName);
            }

            GSN objType = GSN.getType(ontObj.getRDFType().toString());
            if (objType != null) {
                objNode.setGsnType(objType);
            }
            if (obj.isURI()) {
                // set the comment for the GSN node, can get comment only for resources with URI
                objNode.setComment(model.getOntResource(obj.getURI()).getComment(null));
            }

            if (predNode.toString().equalsIgnoreCase("http://sadl.org/gsn.sadl#inContextOf")) {
                objNode.setAsChild();
                objNode.setGsnType(GSN.CONTEXT);
                subjNode.addInContextOf(objNode);
            }

            if (predNode.toString().equalsIgnoreCase("http://sadl.org/gsn.sadl#supportedBy")) {
                objNode.setAsChild();
                subjNode.addSupportedBy(objNode);
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public void processOwlFile(java.io.File owlFile) {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        model.getDocumentManager().setProcessImports(true);

        try {
            model.read(new FileInputStream(owlFile), null);
        } catch (FileNotFoundException e) {
            RackConsole.getConsole().error("Cannot read Owl File:" + owlFile + ", file not found");
        }

        StmtIterator it = model.listStatements();

        while (it.hasNext()) {

            Statement stmt = it.next();
            processGSNRelationship(model, stmt);
        }

        System.out.println("Done!");
    }
}
