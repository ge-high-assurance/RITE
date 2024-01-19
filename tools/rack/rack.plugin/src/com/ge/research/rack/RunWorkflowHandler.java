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

import com.ge.research.rack.views.SessionView;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class RunWorkflowHandler extends AbstractHandler {

	/** Cached list of folders containing workflows (that is, a workflow path) */
    List<String> workflowDirs = null;
    
    /** Cached map (name->workflow XML) of workflows found */
    Map<String, Element> workflows = null;
    
    /** XML parser (for both workflows and forms) */
    DocumentBuilder parser;
    
    /** History list of XML documents presented to the user; history.getItem(0) is the current document */
    public Stack<Document> history = new Stack<>(); // FIXME - is this maintained across different instances of the View? Perhaps should belong to the View?
    
    /** The associated Eclipse View for showing running workflows */
    SessionView view;
    
    /** The current Document being shown (or null) */
    Document currentDisplayedDoc;
    
    /** The name of the current workflow */
    String workflowName;
    // Invariant:
    // workflowName == null && currentDisplayedDoc == null ==> No workflow in execution
    // workflowName != null && currentDisplayedDoc == null ==> Starting named workflow
    // workflowName != null && currentDisplayedDoc != null ==> Given workflow in progress; name in currentDisplayedDoc must match workflowName
    // workflowName == null && currentDisplayedDoc != null ==> Given workflow in progress; name is in currentDisplayedDoc
    

    public RunWorkflowHandler() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            parser = dbf.newDocumentBuilder();
        } catch (Exception e) {
            MessageDialog.openError(
                    null, "Error", "Failed to initialize document parser\n" + e);
            parser = null;
            // FIXME - not sure other code is robust if 'parser' is null
            return;
        }
    }

    /** Parses a String to produce an XML Document */
    private Document parseXML(String xml) throws SAXException, IOException {
        return parser.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
    }
    
    /** Reads a file, producing an XML Document, throws exceptions on parsing failure */
    private Document parseXMLFile(String fullname) throws SAXException, IOException {
        return parser.parse(new File(fullname));
    }

    /** Renders XML Document as String (inverse of parseXML) */
    public String getStringFromDocument(Document doc) {
        try {
           DOMSource domSource = new DOMSource(doc);
           StringWriter writer = new StringWriter();
           StreamResult result = new StreamResult(writer);
           TransformerFactory tf = TransformerFactory.newInstance();
           Transformer transformer = tf.newTransformer();
           transformer.transform(domSource, result);
           return writer.toString();
        } catch (TransformerException ex) {
           ex.printStackTrace();
           return null;
        }
    } 


    /** Called when the 'Run Workflow' menu button is pressed */ 
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
        	// Find the corresponding view (currently there only ever is at most one)
            try {
                view = (SessionView) PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow()
                                        .getActivePage()
                                        .showView(SessionView.ID);
                view.handler = this;

            } catch (Exception e) {
                MessageDialog.openError(null, "Error", "Failed to find corresponding View\n" + e);
                throw e;
            }
            
            x:
            while (true) {
            	// Initialize the list of workflows
                findWorkflows();
                var keys = new ArrayList<String>(new TreeSet<String>(workflows.keySet()));
                var wd = new SessionView.WorkflowDialog(keys);

                // put up a dialog to select a workflow
                int code = wd.open();
                switch (code) {
                    case IDialogConstants.OK_ID:
                        if (wd.selected == null || wd.selected.isEmpty()) {
                            MessageDialog.openInformation(null, "", "No workflow selected");
                            break;
                        }
                        runWorkflow(wd.selected);
                        break x;
                    case IDialogConstants.RETRY_ID: // Refresh
                        wd.close();
                        workflowDirs = null;
                        break; // break from the switch, repeat the while
                    case IDialogConstants.CANCEL_ID:
                        break x;
                    default:
                        MessageDialog.openError(
                                null,
                                "Error",
                                "Non-implemented option in RunWorkflowHandler: " + code);
                        break x;
                }
            }

        } catch (Exception e) {

            // Errors should already be reported
            // The Exception is to allow an abort

        }
        return null;
    }

    /** Returns a List of directories in which to find workflows */
    public List<String> getWorkflowDirs() throws Exception {
        var path = "resources/workflows";
        var dirs = new LinkedList<String>();
        for (var p : path.split(File.pathSeparator)) {
            try {
                Bundle bundle = Platform.getBundle("rack.plugin");
                URL url = FileLocator.find(bundle, new Path(p), null);
                var dir = FileLocator.toFileURL(url).getFile();
                dirs.add(dir);
            } catch (Exception e) {
                MessageDialog.openError(null, "Error", "Failed to open resource " + p + "\n" + e);
                throw e;
            }
        }
        return dirs;
    }

    /** Sets the list of discovered workflows */
    public void findWorkflows() throws Exception {
        var dirs = getWorkflowDirs();
        if (!Objects.equals(dirs,workflowDirs)) {
            workflows = new HashMap<>();
            for (var d : dirs) {
                var dir = new java.io.File(d);
                if (!dir.exists()) {
                    MessageDialog.openError(null, "Error", "Folder " + dir + " does not exist");
                    continue;
                }
                if (!dir.isDirectory()) {
                    MessageDialog.openError(null, "Error", dir + " is not a folder");
                    continue;
                }

                for (var filename : dir.list((f,s)->s.endsWith(".xml"))) {
                    var fullname = dir + java.io.File.separator + filename;
                	if (!new File(fullname).isFile()) continue;
                	new File(fullname).setExecutable(true); // Just because git sometimes does not retain exectuable flag
                    try {
                    	String wname = filename.substring(0, filename.length()-4); // 4 is the length of '.xml'
                        var doc = parseXMLFile(fullname);
                        var n = doc.getDocumentElement();
                        if (n.getNodeName().equals("workflow")) {
                            var name = n.getAttribute("name");
                            if (!wname.equals(name)) {
                                MessageDialog.openError(
                                        null, "Error", "File name does not match workflow name: " + wname + " vs. " + name);
                            }
                            var prev = workflows.put(wname, n);
                            if (prev != null) {
                                MessageDialog.openError(
                                        null, "Error", "Duplicate workflow name: " + name);
                            }
                        } else {
                            MessageDialog.openError(
                                    null,
                                    "Error",
                                    "Workflow XML does not contain an outer node of type 'workflow': "
                                            + fullname);
                        }

                    } catch (Exception e) {
                        MessageDialog.openError(
                                null, "Error", "Failed to parse " + fullname + "\n" + e);
                    }
                }
            }
            workflowDirs = dirs;
            MessageDialog.openInformation(
                    null,
                    "",
                    workflows.size() + " workflows read in " + workflowDirs.size() + " folders");
        }
    }

    /** Initiate the named workflow */
    public void runWorkflow(String name) throws Exception {
    	currentDisplayedDoc = null;
        var top = workflows.get(name);
        if (top == null) {
            // This error should not be reachable, since only well-formed workflows should be in
            // 'this.workflows'
            MessageDialog.openError(null, "Error", "No XML document found for workflow " + name);
            return;
        }
        
        // An empty-as-possible XML string as a starting document
        workflowName = name;

        next();
    }
    
    public String workflowNameFromDoc(Document doc) {
    	return doc.getDocumentElement().getAttribute("workflow");
    }
    
    public String initialXML(String workflowName) {
    	return
    		"""
            <?xml version="1.0" encoding="UTF-8" standalone="no"?>
            <form workflow="example">
               <connection>
                  <data-graph>http://...</data-graph>
                  <model-graph>http://...</model-graph>
                 <url>http://localhost:3030/RACK</url>
               </connection>
            </form>    				
            """
    			.replace("example",workflowName);
    }
    // FIXME - need to fill in the data-graph and model-graph
    
    public void next() {
        if (currentDisplayedDoc == null && workflowName == null) {
            MessageDialog.openInformation(
                    null, "", "No workflow selected (previous workflow aborted)");
            return;
        }

    	if (workflowName == null) workflowName = workflowNameFromDoc(currentDisplayedDoc);
        var top = workflows.get(workflowName);
        String command = top.getAttribute("command");
        if (command == null || command.isEmpty()) {
            MessageDialog.openError(null, "Error", "No command string found for workflow: " + workflowName);
            return;
        }
        command = command.strip();
        if (!new File(command).isAbsolute()) { // FIXME - this test is inappropriate for commands with arguments
            try {
                Bundle bundle = Platform.getBundle("rack.plugin");
                URL url = FileLocator.find(bundle, new Path("resources/workflows"), null);
                var dir = FileLocator.toFileURL(url).getFile();
                command = dir + command;
            } catch (Exception e) {
                MessageDialog.openError(null, "Error", "Failed to find command " + command + "\n" + e);
                return;
            }
        }

        // launch workflow
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            MessageDialog.openError(null, "Error", "Failed to launch process: " + command + "\n" + e);
            return;
        }
        if (process == null || !process.isAlive()) {
            MessageDialog.openError(null, "Error", "Failed to launch process: " + command);
            return;
        }

        String responseText = "";
        try ( PrintStream writer = new java.io.PrintStream(process.getOutputStream());
        	  BufferedReader reader =
                        new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))
        		) {

            String currentXML;
    		if (currentDisplayedDoc == null) {
    			currentXML = initialXML(workflowName);
    		} else {
    			view.collectXML();
    			currentXML = getStringFromDocument(currentDisplayedDoc);
    		}
    		
    		// send
            writer.print(currentXML);
            writer.close(); // So that the workflow script knows the stdin is complete

            // read response
            try {
                responseText = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            } catch (Exception e) {
                MessageDialog.openError(null, "Error", "Failed to read response");
                throw e;
            }

            // Parse and display response
            currentDisplayedDoc = parseXML(responseText);
            view.displayXML(currentDisplayedDoc);
    		history.push(currentDisplayedDoc);
    		String newname = workflowNameFromDoc(currentDisplayedDoc);
    		if (!workflowName.equals(newname)) {
                MessageDialog.openError(null, "Error", "Returned XML contains a different workflow name than the filename: " + newname + " vs. " + workflowName);
                workflowName = newname;
    		}

        } catch (Exception e) {
            MessageDialog.openError(null, "Error", "Communication failure\n" + responseText + "\n" + e);
        } finally {
        	if (process != null) {
        		process.destroy();
        		process = null;
        	}
        }
    }

    /** Called when the Save button is pressed;
     * launches a dialog allowing the user to choose a destination file and saves the
     * current XML Document to that file (in String form).
     */
    public void save(Shell shell) {
        var fd = new FileDialog(shell, SWT.SAVE);
        var file = fd.open();
        if (file != null) {
        	var d = history.peek();
        	var s = getStringFromDocument(d);
        	try {
        	    java.nio.file.Files.write(Paths.get(file), s.getBytes());
        	} catch (java.io.IOException e) {
        		MessageDialog.openError(null, "Error", "Failed to save file " + file + "\n" + e);
        	}
        }
    }

    /** Called when the 'Load' button is pressed; launches a FileDialog enabling a choice of existing file and
     * loads that file as the current XML document */
    public void load(Shell shell) {
        var fd = new FileDialog(shell, SWT.OPEN);
        var file = fd.open();
        if (file != null) try {
        	currentDisplayedDoc = null; // in case there is an exception
        	String text = new String(java.nio.file.Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);
        	Document displayDoc = parseXML(text);
        	history.push(displayDoc);
            view.displayXML(displayDoc);
            currentDisplayedDoc = displayDoc;
            workflowName = workflowNameFromDoc(currentDisplayedDoc);
        } catch (java.io.IOException e) {
        	MessageDialog.openError(null, "Error", "Failed to read file " + file + "\n" + e);
        } catch (SAXException e) {
        	MessageDialog.openError(null, "Error", "Failed to parse file " + file + "\n" + e);
        }
    }

    /** Called when the 'Restart' button is pressed */
    public void retry() {
        if (workflowName == null && currentDisplayedDoc == null) {
            MessageDialog.openInformation(
                    null, "", "No workflow selected (previous workflow aborted)");
            return;
        }
    	workflowName = workflowNameFromDoc(currentDisplayedDoc);
        view.clearXMLDisplay();
        try {
            runWorkflow(workflowName);
        } catch (Exception e) {
            MessageDialog.openError(null, "Error", "Failed to restart workflow\n" + e);
        }
    }

    /** Called when the 'Back' button is pressed */
    public void back() {
        if (currentDisplayedDoc == null) {
            MessageDialog.openInformation(
                    null, "", "No workflow selected (previous workflow aborted)");
            return;
        }
        if (history.size() == 0) {
            MessageDialog.openInformation(
                    null, "", "No further history");
            return;
        }
        history.pop();
        currentDisplayedDoc = history.peek();
    	workflowName = workflowNameFromDoc(currentDisplayedDoc);
        view.displayXML(currentDisplayedDoc);
    }

    /** Called when the 'Abort' button is pressed */
    public void abort() {
        view.clearXMLDisplay();
        currentDisplayedDoc = null;
        workflowName = null;
        view.displayEmpty();
    }
}
