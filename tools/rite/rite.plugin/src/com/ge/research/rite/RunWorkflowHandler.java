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

import com.ge.research.rite.views.RackPreferencePage;
import com.ge.research.rite.views.SessionView;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
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

public class RunWorkflowHandler extends RiteHandler {

    /** Cached list of folders containing workflows (that is, a workflow path) */
    List<String> workflowDirs = null;

    /** Cached map (name->workflow XML) of workflows found */
    Map<String, Element> workflows = null;

    /** XML parser (for both workflows and forms) */
    DocumentBuilder parser;

    /**
     * History list of XML documents presented to the user; history.getItem(0) is the current
     * document
     */
    public Stack<Document> history = new Stack<>();

    // FIXME - is this maintained across different instances of the View?

    // Perhaps should belong to the View?

    /** The associated Eclipse View for showing running workflows */
    SessionView view;

    /** The current Document being shown (or null) */
    Document currentDisplayedDoc;

    /** The name of the current workflow */
    String workflowName;

    // Invariant:
    // workflowName == null && currentDisplayedDoc == null ==> No workflow in execution
    // workflowName != null && currentDisplayedDoc == null ==> Starting named workflow
    // workflowName != null && currentDisplayedDoc != null ==> Given workflow in progress; name in
    // currentDisplayedDoc must match workflowName
    // workflowName == null && currentDisplayedDoc != null ==> Given workflow in progress; name is
    // in currentDisplayedDoc

    public RunWorkflowHandler() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            parser = dbf.newDocumentBuilder();
        } catch (Exception e) {
            MessageDialog.openError(null, "Error", "Failed to initialize document parser\n" + e);
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
            super.execute(event);
            // Find the corresponding view (currently there only ever is at most one)
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
                Bundle bundle = Platform.getBundle("rite.plugin");
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
        if (!Objects.equals(dirs, workflowDirs)) {
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

                for (var filename : dir.list((f, s) -> s.endsWith(".xml"))) {
                    var fullname = dir + java.io.File.separator + filename;
                    if (!new File(fullname).isFile()) continue;
                    new File(fullname)
                            .setExecutable(
                                    true); // Just because git sometimes does not retain exectuable
                    // flag
                    try {
                        String wname =
                                filename.substring(
                                        0, filename.length() - 4); // 4 is the length of '.xml'
                        var doc = parseXMLFile(fullname);
                        var n = doc.getDocumentElement();
                        if (n.getNodeName().equals("workflow")) {
                            var name = n.getAttribute("name");
                            if (n.hasAttribute("name") && !wname.equals(name)) {
                                MessageDialog.openError(
                                        null,
                                        "Error",
                                        "File name does not match workflow name: "
                                                + wname
                                                + " vs. "
                                                + name);
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
        return """
            <?xml version="1.0" encoding="UTF-8" standalone="no"?>
            <form workflow="#example">
               <connection>
                  <data-graph>#data_graph</data-graph>
                  <model-graph>#model_graph</model-graph>
                 <url>http://localhost:3030/RACK</url>
               </connection>
            </form>
            """
                .replace("#example", workflowName)
                .replace("#url", RackPreferencePage.getConnURL())
                .replace("#data_graph", RackPreferencePage.getDefaultDataGraph())
                .replace("#model_graph", RackPreferencePage.getDefaultModelGraph());
    }

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
            MessageDialog.openError(
                    null, "Error", "No command string found for workflow: " + workflowName);
            return;
        }
        command = command.strip();
        int k = command.indexOf(' ');
        String exec = k < 0 ? command : command.substring(0, k);
        if (!new File(exec).isAbsolute()) {
            try {
                Bundle bundle = Platform.getBundle("rite.plugin");
                URL url = FileLocator.find(bundle, new Path("resources/workflows"), null);
                var dir = FileLocator.toFileURL(url).getFile();
                command = dir + command;
                exec = dir + exec;
            } catch (Exception e) {
                MessageDialog.openError(
                        null, "Error", "Failed to find command " + command + "\n" + e);
                return;
            }
        }
        var fcommand = command;
        new File(exec).setExecutable(true);

        String currentXML;
        if (currentDisplayedDoc == null) {
            currentXML = initialXML(workflowName);
        } else {
            view.collectXML(currentDisplayedDoc);
            currentXML = getStringFromDocument(currentDisplayedDoc);
        }
        // MessageDialog.openInformation(null, "Sending", currentXML);

        final var output = new String[1];
        final var process_ = new Process[1];
        final var error = new String[1];
        final var canceled = new boolean[] {false};
        final var dialog_ = new MessageDialog[1];

        // launch workflow before the communication Job so that there is no race
        // between creating the process and cancelling it
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(fcommand);
        } catch (Exception e) {
            MessageDialog.openError(
                    null, "Error", "Failed to launch process: " + fcommand + "\n" + e);
            return;
        }
        if (process == null || !process.isAlive()) {
            MessageDialog.openError(null, "Error", "Failed to launch process: " + fcommand);
            return;
        }
        process_[0] = process;

        // MessageDialog.openInformation(null, "Sending", currentXML);
        var job =
                new Job("Workflow Communication: " + workflowName) {
                    protected IStatus run(IProgressMonitor monitor) {
                        String responseText = "";
                        try (PrintStream writer =
                                        new java.io.PrintStream(process_[0].getOutputStream());
                                BufferedReader reader =
                                        new java.io.BufferedReader(
                                                new java.io.InputStreamReader(
                                                        process_[0].getInputStream()))) {

                            // send

                            // System.out.println("WROTE " + currentXML);
                            writer.print(currentXML);
                            writer.close(); // So that the workflow script knows the stdin is
                            // complete

                            // Need to watch for user cancellation in the Progress View concurrently
                            // with
                            // waiting for input from the process's stdout
                            Thread cancelWatcher =
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            while (!monitor.isCanceled())
                                                try {
                                                    Thread.sleep(250);
                                                } catch (Exception e) {
                                                }
                                            // The main job is stuck watching for input
                                            // so we cancel by brute force
                                            canceled[0] = true;
                                            process_[0].destroy();
                                            return;
                                        }
                                    };
                            cancelWatcher.start();

                            // read response
                            try {
                                responseText =
                                        reader.lines()
                                                .collect(
                                                        Collectors.joining(System.lineSeparator()));
                            } catch (Exception e) {
                                if (error[0] != null) error[0] = "Failed to read response: " + e;
                                return Status.CANCEL_STATUS;
                            }
                            // System.out.println("READ " + responseText);
                            output[0] = responseText;
                            return Status.OK_STATUS;
                        } catch (Exception e) {
                            error[0] = "Communication failure\n" + responseText + "\n" + e;
                            return Status.CANCEL_STATUS;
                        } finally {
                            if (process_[0] != null) {
                                process_[0].destroy();
                                process_[0] = null;
                            }
                        }
                    }
                };
        var listener =
                new JobChangeAdapter() {
                    public void done(IJobChangeEvent event) {
                        if (event.getResult() == Status.OK_STATUS
                                || event.getResult() == Status.CANCEL_STATUS) {
                            PlatformUI.getWorkbench()
                                    .getDisplay()
                                    .syncExec(
                                            new Runnable() {
                                                public void run() {
                                                    try {
                                                        if (event.getResult()
                                                                        == Status.CANCEL_STATUS
                                                                || canceled[0]) {
                                                            canceled[0] = true;
                                                            if (process_[0] != null) {
                                                                process_[0].destroy();
                                                                process_[0] = null;
                                                            }
                                                            if (dialog_[0] != null) {
                                                                dialog_[0].close();
                                                                dialog_[0] = null;
                                                            }
                                                            MessageDialog.openError(
                                                                    null,
                                                                    "Error",
                                                                    "Response canceled");
                                                            view.showView();
                                                        } else if (event.getResult()
                                                                == Status.OK_STATUS) {
                                                            if (dialog_[0] != null) {
                                                                dialog_[0].close();
                                                                dialog_[0] = null;
                                                            }
                                                            if (error[0] != null) {
                                                                MessageDialog.openError(
                                                                        null, "Error", error[0]);
                                                                return;
                                                            }
                                                            var responseText = output[0];
                                                            // MessageDialog.openInformation(null,
                                                            // "Received", responseText);

                                                            // Parse and display response
                                                            try {
                                                                currentDisplayedDoc =
                                                                        parseXML(responseText);
                                                                view.displayXML(
                                                                        currentDisplayedDoc);
                                                                history.push(currentDisplayedDoc);
                                                                String newname =
                                                                        workflowNameFromDoc(
                                                                                currentDisplayedDoc);
                                                                if (!workflowName.equals(newname)) {
                                                                    MessageDialog.openError(
                                                                            null,
                                                                            "Error",
                                                                            "Returned XML contains a different workflow name than the filename: "
                                                                                    + newname
                                                                                    + " vs. "
                                                                                    + workflowName);
                                                                    workflowName = newname;
                                                                }
                                                            } catch (Exception e) {
                                                                if (!canceled[0])
                                                                    MessageDialog.openError(
                                                                            null,
                                                                            "Error",
                                                                            "Communication failure\n"
                                                                                    + responseText
                                                                                    + "\n"
                                                                                    + e);
                                                            }
                                                        }
                                                    } finally {
                                                        view.enableButtons(true);
                                                    }
                                                }
                                            });
                        }
                    }
                };
        job.addJobChangeListener(listener);
        job.setPriority(Job.SHORT);
        if (currentDisplayedDoc != null) {
            view.enableButtons(false);
            switch (RackPreferencePage.getCancelBehavior()) {
                default:
                case "none": // no dialog
                    dialog_[0] = null;
                    job.schedule(); // start as soon as possible
                    break;
                case "block":
                    { // blocking cancel
                        // This implementation creates a dialog that blocks the UI thread, but after
                        // opening the dialog, goes on to start the computational thread and execute
                        // the communication with the workflow process.
                        MessageDialog dialog =
                                new MessageDialog(
                                        null,
                                        "Cancel",
                                        null,
                                        """
                                Cancel the workflow?

                                If you dismiss this dialog, you can still cancel the workflow using the Progress View.
                                You can set a preference to always suppress this cancel dialog.
                                """,
                                        MessageDialog.QUESTION,
                                        new String[] {"Dismiss (no cancel)", "Yes"},
                                        1) {
                                    protected Control createDialogArea(Composite parent) {
                                        setShellStyle(
                                                SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
                                        setBlockOnOpen(false);
                                        return super.createDialogArea(parent);
                                    }

                                    protected void buttonPressed(int buttonID) {
                                        if (buttonID == 1) {
                                            if (process_[0] != null) process_[0].destroy();
                                            canceled[0] = true;
                                        }
                                        super.buttonPressed(buttonID);
                                    }
                                };
                        dialog_[0] = dialog;
                        dialog.open();
                        job.schedule(); // start as soon as possible
                        break;
                    }
                case "noblock":
                    { // non blocking dialog
                        // This implementation creates a non-blocking cancel dialog -- that is,
                        // while
                        // the dialog is open, the IU thread allows interaction with other UI
                        // elements.
                        // However, it also appears that control flow here waits for user action
                        // before
                        // proceeding from the open() call. Hence we schedule the job before opening
                        // the dialog.
                        MessageDialog dialog =
                                new MessageDialog(
                                        PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                                        "Cancel",
                                        null,
                                        """
                                Cancel the workflow?

                                If you dismiss this dialog, you can still cancel the workflow using the Progress View.
                                You can set a preference to always suppress this cancel dialog.
                                """,
                                        MessageDialog.QUESTION,
                                        new String[] {"Dismiss (no cancel)", "Yes"},
                                        1) {

                                    @Override
                                    protected void buttonPressed(int buttonID) {
                                        if (buttonID == 1) {
                                            if (process_[0] != null) {
                                                process_[0].destroy();
                                                process_[0] = null;
                                            }
                                            canceled[0] = true;
                                            view.enableButtons(true);
                                        }
                                        super.buttonPressed(buttonID);
                                    }

                                    @Override
                                    protected void setShellStyle(int newShellStyle) {
                                        super.setShellStyle(
                                                SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
                                        setBlockOnOpen(false);
                                    }
                                };
                        dialog_[0] = dialog;
                        job.schedule(); // start as soon as possible
                        // It is possible the job will complete before the dialog ever is opened.
                        // Even with the guard in the next statement, there could be a race in that
                        // the
                        // job could complete (and close the dialog) before the dialog is opened.
                        if (process_[0] != null) dialog.open();
                    }
            }
        } else {
            job.schedule(); // start as soon as possible
        }
    }

    /**
     * Called when the Save button is pressed; launches a dialog allowing the user to choose a
     * destination file and saves the current XML Document to that file (in String form).
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

    /**
     * Called when the 'Load' button is pressed; launches a FileDialog enabling a choice of existing
     * file and loads that file as the current XML document
     */
    public void load(Shell shell) {
        var fd = new FileDialog(shell, SWT.OPEN);
        var file = fd.open();
        if (file != null) {
            try {
                currentDisplayedDoc = null; // in case there is an exception
                String text =
                        new String(
                                java.nio.file.Files.readAllBytes(Paths.get(file)),
                                StandardCharsets.UTF_8);
                Document displayDoc = parseXML(text);
                history.push(displayDoc);
                view.displayXML(displayDoc);
                currentDisplayedDoc = displayDoc;
                workflowName = workflowNameFromDoc(currentDisplayedDoc);
            } catch (java.io.IOException e) {
                MessageDialog.openError(null, "Error", "Failed to read file " + file + "\n" + e);
                view.displayEmpty();
            } catch (SAXException e) {
                MessageDialog.openError(null, "Error", "Failed to parse file " + file + "\n" + e);
                view.displayEmpty();
            }
        }
    }

    public void select(Shell shell) {
        try {
            execute(null);
        } catch (Exception e) {
            MessageDialog.openError(null, "Error", "Failed to open selection dialog" + e);
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
            view.displayEmpty();
        }
    }

    /** Called when the 'Back' button is pressed */
    public void back() {
        if (currentDisplayedDoc == null) {
            MessageDialog.openInformation(
                    null, "", "No workflow selected (previous workflow aborted)");
            return;
        }
        if (history.size() <= 1) {
            MessageDialog.openInformation(null, "", "No further history");
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
