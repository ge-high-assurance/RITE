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
package com.ge.research.rack.views;

import com.ge.research.rack.RunWorkflowHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.widgets.WidgetFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// Crucial:
// FIXME - Send connection information every time

// Should do:
// FIXME - use computational thread? be able to abort a stuck process?
// FIXME - tone down the amount of switching to the console that happens
// FIXME - relayout the widgets when the view size changes
// FIXME - send scrollbar to bottom on each Next
// FIXME - check and document whether Save saves the current contents of the user-modifiable
// controls

// Nice to have:
// FIXME - handle stderr
// FIXME - a workflow path listing candidate folders for workflows
// FIXME - add a combo control
// FIXME - disable the back button when there is no history to revert to
// FIXME - be able to go forward after a back

// Future:
// FIXME - do something with the language attribute
// FIXME _ do something with id attributes
// FIXME - allow multiple instances of this View
// FIXME - allow multiple communicating processes

/** A helper class used in transferring user input from the IDE to the XML structure */
record Input(Element element, Widget widget) {}

/** An Eclipse View used to interact with a workflow script. */
public class SessionView extends ViewPart {

    /** The ID of the view; this must match the string given in the plugin.xml. */
    public static final String ID = "rackplugin.views.SessionView";

    /** The received parent by createPartControl */
    private Composite parent;

    /** The outermost composite, created here; the direct child of 'parent' */
    private Composite outer;

    /** The composite into which the various widgets representing the XML are placed */
    private Composite composite;

    /** The handler object for this view */
    public RunWorkflowHandler handler;

    /** List of the input widget and XML element pairs for the currently displayed material */
    public java.util.List<Input> inputs;

    @Override
    public void setFocus() {}

    @Override
    public void createPartControl(Composite parent) {
        this.parent = parent;
        displayEmpty();
    }

    public void showView() {
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ID);
        } catch (Exception e) {
            // Ignore
        }
    }

    public void clearXMLDisplay() {
        if (this.outer != null && !this.outer.isDisposed()) {
            this.outer.dispose();
            this.outer = null;
        }
        buttonList.clear();
    }

    public void displayEmpty() {
        if (this.handler == null) this.handler = new RunWorkflowHandler();

        outer = new Composite(parent, SWT.NONE);
        GridLayout layout0 = new GridLayout();
        layout0.numColumns = 1;
        outer.setLayout(layout0);
        outer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        addLabel(outer, "There is no workflow in progress");

        addSeparator(outer);

        Composite buttons = new Composite(outer, SWT.NONE);
        GridLayout layout2 = new GridLayout();
        layout2.numColumns = 2; // number of buttons
        buttons.setLayout(layout2);
        buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, SWT.LEFT, true, false));
        createButton(buttons, IDialogConstants.OPEN_ID, "Load", false);
        createButton(buttons, IDialogConstants.PROCEED_ID, "Select", false);

        parent.getParent().layout(true, true);
        showView();
        composite = null;
        inputs = null;
    }

    /**
     * Refreshes the View area to display the given XML document; returns true when workflow is
     * complete
     */
    public boolean displayXML(Document doc) {
        var top = doc.getDocumentElement();
        var name = top.getAttribute("workflow");
        boolean result = false;
        clearXMLDisplay();
        inputs = new java.util.LinkedList<>();
        outer = new Composite(parent, SWT.NONE);
        GridLayout layout0 = new GridLayout();
        layout0.numColumns = 1;
        outer.setLayout(layout0);
        outer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, SWT.CENTER, true, false));
        outer.setBackground(parent.getBackground());

        if (name.isEmpty()) {
            addLabel(outer, "Received XML is invalid: no workflow name");
            MessageDialog.openError(null, "Error", "No workflow name given");
        } else {
            addLabel(outer, "Executing workflow " + name);
        }
        // String debug = handler.getStringFromDocument(doc);

        ScrolledComposite sc = new ScrolledComposite(outer, SWT.H_SCROLL | SWT.V_SCROLL);
        sc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, SWT.TOP, true, true));
        sc.setExpandHorizontal(true);
        sc.setAlwaysShowScrollBars(true);
        sc.setBackground(parent.getBackground());

        composite = new Composite(sc, SWT.NONE);
        GridLayout layout1 = new GridLayout();
        layout1.numColumns = 1;
        composite.setLayout(layout1);
        composite.setLayoutData(new GridData(GridData.FILL, SWT.CENTER, true, true));
        composite.setBackground(composite.getParent().getBackground());
        sc.setContent(composite);

        try {
            if (!"form".equals(top.getNodeName())) {
                addLabel(outer, "Received XML is invalid");
                MessageDialog.openError(null, "Error", "Incorrect top-level node name");
                return false;
            }
            NodeList nsbox = top.getChildNodes(); // All children should be <box> (or #text)
            x:
            for (int k = 0; k < nsbox.getLength(); k++) {
                var elem = nsbox.item(k);
                switch (elem.getNodeName()) {
                    case "box":
                        var box = elem;
                        NodeList ns =
                                box.getChildNodes(); // All children should be <control> (or #text)
                        addSeparator(composite);
                        for (int i = 0; i < ns.getLength(); i++) {
                            Node n = ns.item(i);
                            if (n.getNodeName().equals("control") && n instanceof Element element) {
                                String type = element.getAttribute("type");
                                String label = element.getAttribute("label");
                                String text = element.getTextContent();
                                String id = element.getAttribute("id");
                                String language = element.getAttribute("language");
                                boolean readonly = element.hasAttribute("readonly");
                                if ("textbox".equals(type)) {
                                    String strlines = element.getAttribute("lines");
                                    int lines = 0;
                                    if (!strlines.isEmpty())
                                        try {
                                            lines = Integer.parseInt(strlines);
                                        } catch (NumberFormatException e) {
                                            MessageDialog.openError(
                                                    null,
                                                    "Error",
                                                    "Invalid integer string: " + strlines);
                                        }
                                    if (lines <= 0) lines = 10; // default
                                    var in = addTextBox(composite, label, text, lines, readonly);
                                    if (!readonly) inputs.add(new Input(element, in));
                                } else if ("label".equals(type)) {
                                    addLabel(composite, text);
                                    if (element.hasAttribute("error")) {
                                        MessageDialog.openInformation(
                                                null,
                                                "",
                                                "The workflow responded with an error:\n" + text);
                                    }
                                } else if ("checkbox".equals(type)) {
                                    boolean value = element.hasAttribute("checked");
                                    var in = addCheckbox(composite, text, value, readonly);
                                    if (!readonly) inputs.add(new Input(element, in));
                                } else {
                                    addLabel(composite, "Received XML is invalid");
                                    MessageDialog.openInformation(
                                            null, "", "Unknown control type: " + type);
                                    break x;
                                }
                            }
                        }
                        composite.layout(true, true);
                        break;
                    case "resource":
                        var resource = (Element) elem;
                        inputs.add(new Input(resource, null));
                        break;
                    case "parameter":
                    case "connection":
                    case "#text":
                        // Nothing to display for these tags
                        break;
                    default:
                        MessageDialog.openInformation(
                                null, "", "Unknown tag type: " + elem.getNodeName());
                }
            }
            outer.setSize(outer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            sc.setMinWidth(parent.getSize().x - sc.getVerticalBar().getSize().x - 10);
            sc.setMinHeight(1);
            composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

            if (top.hasAttribute("complete")) {
                addLabel(outer, "Workflow is complete");
                result = true;
            }

        } catch (Exception e) {
            addLabel(outer, "Received XML is invalid");
            MessageDialog.openError(null, "Error", "Failure to display XML\n" + e);
        }
        addSeparator(outer);

        buttonList.clear();
        Composite buttons = new Composite(outer, SWT.NONE);
        GridLayout layout2 = new GridLayout();
        layout2.numColumns = 7; // number of buttons
        buttons.setLayout(layout2);
        buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, SWT.LEFT, true, false));
        createButton(buttons, IDialogConstants.ABORT_ID, "Abort", false);
        createButton(buttons, IDialogConstants.PROCEED_ID, "Select", false);
        createButton(buttons, IDialogConstants.FINISH_ID, "Save",false);
        createButton(buttons, IDialogConstants.OPEN_ID, "Load", false);
        createButton(buttons, IDialogConstants.RETRY_ID, "Restart", false);
        createButton(buttons, IDialogConstants.BACK_ID, "Back", false);
        createButton(buttons, IDialogConstants.OK_ID, "Next", true).setEnabled(!result);

        parent.getParent().layout(true, true);
        return result;
    }

    /**
     * Runs through the list of pairs in 'inputs', inspecting each UI widget for user input and
     * adjusting the corresponding XML Element/Attribute accordingly.
     */
    public void collectXML(Document currentDisplayedDoc) {
        if (inputs != null) {
            for (var p : inputs) {
                if (p.element().getNodeName().equals("resource")) {
                    Element resource = p.element();
                    String path = resource.getAttribute("path");
                    String ws = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
                    var f = new java.io.File(ws, path);
                    if (!f.exists()) {
                        MessageDialog.openInformation(null, "", "Resource does not exist: " + path);
                    } else {
                        try {
                            var content = Files.readString(Path.of(ws, path));
                            resource.setTextContent(content);
                        } catch (Exception e) {
                            MessageDialog.openError(
                                    null,
                                    "Error",
                                    "Failed to read resource: " + path + " in " + ws + "\n" + e);
                        }
                    }
                } else if (p.widget() instanceof Text t) {
                    var s = t.getText();
                    s = s.replaceAll("\n+$", "").replaceAll("^\n+", "");
                    p.element().setTextContent(s);
                } else if (p.widget() instanceof Button b) {
                    var hasChecked = p.element().hasAttribute("checked");
                    var checked = b.getSelection();
                    if (checked && !hasChecked) {
                        p.element().setAttribute("checked", "checked");
                    } else if (!checked && hasChecked) {
                        p.element().removeAttribute("checked");
                    }
                }
            }
        }
        var top = currentDisplayedDoc.getDocumentElement();
        var connections = top.getElementsByTagName("connection");
        Node connection;
        if (connections.getLength() == 0) {
        	connection = currentDisplayedDoc.createElement("connection");
        	top.appendChild(connection);
        } else {
        	connection = connections.item(0);
        }
        Element element = (Element)connection;
    	var e = currentDisplayedDoc.createElement("data-graph");
    	e.setTextContent(RackPreferencePage.getDefaultDataGraph());
    	element.appendChild(e);
    	e = currentDisplayedDoc.createElement("model-graph");
    	e.setTextContent(RackPreferencePage.getDefaultModelGraph());
    	element.appendChild(e);
    	e = currentDisplayedDoc.createElement("url");
    	e.setTextContent(RackPreferencePage.getConnURL());
    	element.appendChild(e);
    }
    
    public List<Button> buttonList = new LinkedList<>();

    /** Adds a button to the given composite */
    protected Button createButton(Composite buttons, int id, String label, boolean defaultButton) {
        Button button =
                WidgetFactory.button(SWT.PUSH)
                        .text(label)
                        .font(JFaceResources.getDialogFont())
                        .data(Integer.valueOf(id))
                        .onSelect(
                                event ->
                                        buttonPressed(
                                                ((Integer) event.widget.getData()).intValue()))
                        .create(buttons);
        if (defaultButton) {
            Shell shell = buttons.getShell();
            if (shell != null) {
                shell.setDefaultButton(button);
            }
        }
        buttonList.add(button);
        return button;
    }
    
    public void enableButtons(boolean enable) {
    	for (var b: buttonList) b.setEnabled(enable);
    }

    /** Callback when a button is pressed */
    private void buttonPressed(int buttonId) {
        switch (buttonId) {
            case IDialogConstants.OK_ID:
                handler.next();
                break;
            case IDialogConstants.ABORT_ID:
                handler.abort();
                break;
            case IDialogConstants.BACK_ID:
                handler.back();
                break;
            case IDialogConstants.RETRY_ID:
                handler.retry();
                break;
            case IDialogConstants.OPEN_ID: // Load
                handler.load(this.getSite().getShell());
                break;
            case IDialogConstants.FINISH_ID: // SAVE
                handler.save(this.getSite().getShell());
                break;
            case IDialogConstants.PROCEED_ID: // Select (from workflow list)
                handler.select(this.getSite().getShell());
                break;
            default:
        }
    }

    /** Adds a text input control to the parent Composite */
    public Text addTextBox(
            Composite parent, String label, String initialText, int lines, boolean readonly) {
        if (label != null) addLabel(parent, label);
        Text textBox =
                new Text(parent, SWT.LEFT | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        textBox.setText(initialText);
        var gdata = new GridData(GridData.FILL_HORIZONTAL);
        gdata.minimumHeight = convertHeightInCharsToPixels(textBox, 1);
        if (readonly) {
            int n = (int) initialText.lines().count();
            if (n < lines) lines = n;
        }
        gdata.heightHint = convertHeightInCharsToPixels(textBox, lines);
        gdata.grabExcessVerticalSpace = false;
        textBox.setLayoutData(gdata);
        textBox.setEditable(!readonly);
        if (readonly) textBox.setForeground(new Color(150, 150, 150));
        return textBox;
    }

    public int convertHeightInCharsToPixels(Text text, int lines) {
        GC gc = new GC(text);
        gc.setFont(text.getFont());
        FontMetrics fontMetrics = gc.getFontMetrics();
        int height = fontMetrics.getHeight() * lines;
        gc.dispose();
        return height;
    }

    /** Adds (readonly) text to the parent Composite */
    public Label addLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.LEFT);
        FontDescriptor boldDescriptor =
                FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
        Font boldFont = boldDescriptor.createFont(label.getDisplay());
        label.setFont(boldFont);
        label.setText(text);
        label.setBackground(parent.getBackground());
        return label;
    }

    /** Adds a checkbox to the parent Composite */
    public Widget addCheckbox(Composite parent, String text, boolean value, boolean readonly) {
        var widget = new Button(parent, SWT.CHECK);
        widget.setText(text);
        widget.setSelection(value);
        widget.setBackground(parent.getBackground());
        widget.setEnabled(!readonly);
        return widget;
    }

    /** Adds a horizontal separator to the parent Composite */
    public Label addSeparator(Composite parent) {
        Label w = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        w.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return w;
    }

    // FIXME - not yet used
    public Button addButton(Composite parent, String label, boolean readonly) {
        Button b = new Button(parent, SWT.PUSH);
        b.setText(label);
        b.setBackground(parent.getBackground());
        b.setEnabled(!readonly);
        return b;
    }

    /** This class is a dialog to select a workflow from the list given in the constructor. */
    public static class WorkflowDialog extends Dialog {

        public static String previousSelection = null;
        private List<String> keys;

        public WorkflowDialog(List<String> keys) {
            super((Shell) null);
            this.keys = keys;
        }

        public String selected;
        private Combo combo;

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
            if (buttonId == IDialogConstants.OK_ID) previousSelection = this.selected;
            close();
        }
    }
}
