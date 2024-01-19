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
import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class RunWorkflowHandler extends AbstractHandler {

    List<String> workflowDirs = null;
    Map<String, Element> workflows = null;
    DocumentBuilder db;
    public List<Document> history;
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {

            try {
                view =
                        (SessionView)
                                PlatformUI.getWorkbench()
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
                var wd = new WorkflowDialog(keys);

                // put up a dialog to seelct one
                int code = wd.open();
                switch (code) {
                    case IDialogConstants.OK_ID:
                        if (wd.selected == null || wd.selected.isEmpty()) {
                            MessageDialog.openInformation(null, "", "No workflow selected");
                            break; // FIXME - does a refresh of the list of workflows
                        }
                        previousSelection = wd.selected;
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

        } finally {
            try {

                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (process != null) process.destroy();

            } catch (Exception e) {
                // Just ignore any cascading exceptions
            }
        }
        return null;
    }

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

    public void findWorkflows() throws Exception {
        var dirs = getWorkflowDirs();
        if (!Objects.equals(dirs,workflowDirs)) {
            workflows = new HashMap<>();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
                db = dbf.newDocumentBuilder();
            } catch (Exception e) {
                MessageDialog.openError(
                        null, "Error", "Failed to initialize document parser\n" + e);
                return;
            }
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

                for (var filename : dir.list()) {
                    var fullname = dir + java.io.File.separator + filename;
                    try {
                        var doc = db.parse(new File(fullname));
                        var n = doc.getDocumentElement();
                        if (n.getNodeName().equals("workflow")) {
                            var name = n.getAttribute("name");
                            var prev = workflows.put(name, n);
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

    // FIXME - only implemented for a single communicating process

    Process process;
    PrintStream writer;
    BufferedReader reader;
    SessionView view;
    Document doc;
    Element top;
    String workflowName;
    String currentXML;
    int count;

    public void runWorkflow(String name) throws Exception {
        // MessageDialog.openInformation(null, "", "Running workflow " + name);
    	doc = null;
    	history = new LinkedList<>();
        top = workflows.get(name);
        if (top == null) {
            // This error should not be reachable, since only well-formed workflows should be in
            // 'this.workflows'
            MessageDialog.openError(null, "Error", "No XML document found for workflow " + name);
            return;
        }
        String workflowtype = top.getAttribute("type");
        String closingInput = top.getAttribute("close"); // Not relevant for default 'xmlonce'

        workflowName = name;
        launchWorkflow();

        // FIXME - need to check error output
        // FIXME - need to kill process on failure and perhaps on termination
        // FIXME - need to be able to refresh the list of workflows -- or maybe we do not need to
        // cache them
        // FIXME - failures just abort the whole interaction (without careful cleanup)
        // FIXME - catch if the target program crashes

        if (workflowtype.isEmpty() || workflowtype.equals("xmlonce")) {
        	count = 0;
        	// default - a process that communicates via xml, but restarts and terminates on each send-response pair
            
        	currentXML = initialXML(workflowName);
        	
        	next();

        } else {
            MessageDialog.openInformation(null, "", "Only the default workflow type is implemented");
        }
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

    public void launchWorkflow() throws Exception {
        String command = top.getAttribute("command");
        String initialInput = top.getAttribute("input");
        process = null;
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            MessageDialog.openError(null, "Error", "Failed to launch process: " + command);
            throw e;
        }

        writer = new java.io.PrintStream(process.getOutputStream());
        reader =
                new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));

        try {
            if (initialInput != null && !initialInput.isEmpty()) writer.print(initialInput);
        } catch (Exception e) {
            MessageDialog.openError(null, "Error", "Failed to write initialInput");
            throw e;
        }
    }

    public String getResponse() throws Exception {
        try {
            // FIXME - need to read up to some delimiter. e.g. </form>
            return reader.readLine();
            // return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception e) {
            MessageDialog.openError(null, "Error", "Failed to read response");
            throw e;
        }
    }

    public void send(String text) throws Exception {
        try {
            writer.print(text);
        } catch (Exception e) {
            MessageDialog.openError(null, "Error", "Failed to write closingInput");
            throw e;
        }
    }
    

    public void next() {
    	count++;
        if (top == null) {
            MessageDialog.openInformation(
                    null, "", "No workflow selected (previous workflow aborted)");
            return;
        }
        String responseText = "";
        try {
    		launchWorkflow();
    		if (doc == null) {
    			currentXML = initialXML(workflowName);
    		} else {
    			view.collectXML();
    			currentXML = getStringFromDocument(doc);
    		}
			send(currentXML);
    		writer.close();
    		responseText = getResponse();
            doc = db.parse(new java.io.ByteArrayInputStream(responseText.getBytes()));
            view.displayXML(doc.getDocumentElement(), workflowName);
    		history.add(0, doc);

//            var staysActive = "true".equals(top.getAttribute("stays-active"));
//            if (!staysActive) launchWorkflow();
//            if (process.isAlive()) {
//                send("<form></form>\n"); // FIXME - scrape the user input
//
//                String responseText = getResponse();
//                var doc = db.parse(new java.io.ByteArrayInputStream(responseText.getBytes()));
//                view.displayXML(doc.getDocumentElement(), workflowName);
//
//            } else {
//                MessageDialog.openInformation(null, "", "Workflow process terminated");
//            }
        } catch (Exception e) {
            MessageDialog.openError(null, "Error", "Communication failure\n" + responseText + "\n" + e);
        }
    }

    public void save(Shell shell) {
        if (top == null) {
            MessageDialog.openInformation(
                    null, "", "No workflow selected (previous workflow aborted)");
            return;
        }
        var fd = new FileDialog(shell, SWT.SAVE);
        var file = fd.open();
        if (file != null) {
        	var d = history.get(0);
        	var s = getStringFromDocument(d);
        	try {
        	    java.nio.file.Files.write(Paths.get(file), s.getBytes());
        	} catch (java.io.IOException e) {
        		MessageDialog.openError(null, "Error", "Failed to save file " + file + "\n" + e);
        	}
        }
    }

    public void load(Shell shell) {
        var fd = new FileDialog(shell, SWT.OPEN);
        var file = fd.open();
        try {
        	String text = new String(java.nio.file.Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);
        	Document d = db.parse(new java.io.ByteArrayInputStream(text.getBytes()));
        	history.clear();
        	history.add(d);
        	doc = d;
        	// FIXME - need to get new workflow name
            view.displayXML(doc.getDocumentElement(), workflowName);
        } catch (java.io.IOException e) {
        	MessageDialog.openError(null, "Error", "Failed to read file " + file + "\n" + e);
        } catch (SAXException e) {
        	MessageDialog.openError(null, "Error", "Failed to parse file " + file + "\n" + e);
        }
    }

    public void retry() {
        if (top == null) {
            MessageDialog.openInformation(
                    null, "", "No workflow selected (previous workflow aborted)");
            return;
        }
        view.clearXMLDisplay();
        try {
            runWorkflow(workflowName);
        } catch (Exception e) {
            MessageDialog.openError(null, "Error", "Failed to restart workflow\n" + e);
        }
    }

    public void back() {
        if (top == null) {
            MessageDialog.openInformation(
                    null, "", "No workflow selected (previous workflow aborted)");
            return;
        }
        if (history.size() <= 1) {
            MessageDialog.openInformation(
                    null, "", "No further history");
            return;
        }
        history.remove(0);
        doc = history.get(0);
        view.displayXML(doc.getDocumentElement(), workflowName);
    }

    public void abort() {
        view.clearXMLDisplay();
        top = null;
        workflowName = null;
        view.displayEmpty();
    }
    
    //method to convert Document to String
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

    public static String previousSelection = null;

    public static class WorkflowDialog extends Dialog {

        List<String> keys;

        public WorkflowDialog(List<String> keys) {
            super((Shell) null);
            this.keys = keys;
        }

        public String selected;
        Combo combo;

        protected Control createDialogArea(Composite parent) {
            Composite container = (Composite) super.createDialogArea(parent);
            new Label(container, SWT.LEFT)
                    .setText(
                            "Choose a workflow to run and then press OK.\n"
                                    + "The refresh button will refresh the internal cache of workflows by rereading all the workflow files.\n"
                                    + "The Cancel button exits the dialog with no action.");
            combo = new Combo(container, SWT.SIMPLE);
            for (var k : keys) combo.add(k);
            int k = previousSelection == null ? -1 : combo.indexOf(previousSelection);
            if (k >= 0) {
                combo.select(k); // does not notify the listeners
                selected = previousSelection;
            }

            combo.addSelectionListener(
                    new SelectionListener() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            int index = combo.getSelectionIndex();
                            selected = combo.getItem(index);
                        }

                        @Override
                        public void widgetDefaultSelected(SelectionEvent e) {
                            selected = null; // FIXME
                        }
                    });

            return container;
        }

        protected void createButtonsForButtonBar(Composite parent) {
            // Change parent layout data to fill the whole bar
            parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            createButton(parent, IDialogConstants.RETRY_ID, "Refresh", false);
            createButton(parent, IDialogConstants.CANCEL_ID, "Close", false);
            createButton(parent, IDialogConstants.OK_ID, "OK", true);
        }

        // Without this method, the refresh button has no effect
        protected void buttonPressed(int buttonId) {
            setReturnCode(buttonId);
            close();
        }
    }
}
