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

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.widgets.WidgetFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// FIXME - it is non-obvious how to get the text box to have a reasonable size vertically
// FIXME - adding scrollbars is problematic also
// FIXME - set labels to bold text
// FIXME - add Load and Select buttons to the empty display
// FIXME - allow multiple instances of this View
// FIXME - add a select button on a View
// FIXME - allow multiple communicating processes

// FIXME - use computational thread? be able to abort a stuck process?

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

    public void clearXMLDisplay() {
        if (!this.outer.isDisposed()) {
        	this.outer.dispose();
        	this.outer = null;
        }
    }

    public void displayEmpty() {
        outer = new Composite(parent, SWT.NONE);
        GridLayout layout0 = new GridLayout();
        layout0.numColumns = 1;
// FIXME - needed?       layout0.verticalSpacing = 10;
        outer.setLayout(layout0);
        outer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        addLabel(outer, "There is no workflow in progress");
        parent.getParent().pack();
        composite = null;
        handler = null;
        inputs = null;
    }

    /** Refreshes the View area to display the given XML document */
    public void displayXML(Document doc) {
    	var top = doc.getDocumentElement();
    	var name = top.getAttribute("workflow");
        clearXMLDisplay();
        inputs = new java.util.LinkedList<>();
        outer = new Composite(parent, SWT.NONE);
        GridLayout layout0 = new GridLayout();
        layout0.numColumns = 1;
// FIXME - needed?       layout0.verticalSpacing = 10;
        outer.setLayout(layout0);
        outer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, SWT.CENTER, true, false));

    	if (name.isEmpty()) {
            addLabel(outer, "Received XML is invalid: no workflow name");
            MessageDialog.openError(null, "Error", "No workflow name given");
    	} else {
    		addLabel(outer, "Executing workflow " + name);
    	}

        composite = outer;
        //        final ScrolledComposite sc = new ScrolledComposite(outer, SWT.FILL | SWT.H_SCROLL
        // | SWT.V_SCROLL);
        //        sc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, SWT.CENTER, true, false));
        //
        //        composite = new Composite(sc, SWT.NONE);
        //        GridLayout layout1 = new GridLayout();
        //        layout1.numColumns = 1;
        //        layout1.verticalSpacing = 10;
        //        composite.setLayout(layout1);
        //        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        try {
            if (!"form".equals(top.getNodeName())) {
                addLabel(outer, "Received XML is invalid");
                MessageDialog.openError(null, "Error", "Incorrect top-level node name");
                return;
            }
            NodeList nsbox = top.getChildNodes(); // All children should be <box>
            for (int k = 0; k < nsbox.getLength(); k++) {
            	var box = nsbox.item(k);
            	NodeList ns = box.getChildNodes(); // All children should be <control>
            	if (k != 0) addSeparator(composite);
            	for (int i = 0; i < ns.getLength(); i++) {
            		Node n = ns.item(i);
            		if (!n.getNodeName().equals("control")) {
        				addLabel(outer, "Received XML is invalid");
        				MessageDialog.openInformation(null, "", "A <box> is expected to contain only <controL> elements: " + n.getNodeName());
            		} else if (n instanceof Element element) {
            			String type = element.getAttribute("type");
            			String label = element.getAttribute("label");
            			String text = element.getTextContent();
            			boolean readonly = element.hasAttribute("readonly");
            			if ("textbox".equals(type)) {
            				// FIXME: String lines = element.getAttribute("lines");
            				var in = addTextBox(composite, label, text, 10, readonly);
            				inputs.add(new Input(element, in));
            			} else if ("label".equals(type)) {
            				addLabel(composite, text);
            			} else if ("checkbox".equals(text)) {
            				boolean value = element.hasAttribute("checked");
            				var in = addCheckbox(composite, label, value, readonly);
            				inputs.add(new Input(element, in));
            			} else {
            				addLabel(outer, "Received XML is invalid");
            				MessageDialog.openInformation(null, "", "Unknown control type: " + type);
            			}
            		}
            	}
            }

        } catch (Exception e) {
            addLabel(outer, "Received XML is invalid");
            MessageDialog.openError(null, "Error", "Failure to display XML\n" + e);
        }

        addSeparator(outer);

        Composite buttons = new Composite(outer, SWT.NONE);
        GridLayout layout2 = new GridLayout();
        layout2.numColumns = 6; // number of buttons
        layout2.verticalSpacing = 10;
        buttons.setLayout(layout2);
        buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, SWT.LEFT, true, false));
        createButton(buttons, IDialogConstants.ABORT_ID, "Abort", false);
        createButton(buttons, IDialogConstants.FINISH_ID, "Save", false); // There is no built-in ID named SAVE
        createButton(buttons, IDialogConstants.OPEN_ID, "Load", false);
        createButton(buttons, IDialogConstants.RETRY_ID, "Restart", false);
        createButton(buttons, IDialogConstants.BACK_ID, "Back", false);
        createButton(buttons, IDialogConstants.OK_ID, "Next", true);

        parent.getParent().layout(true, true);
    }
    
    /** Runs through the list of pairs in 'inputs', inspecting each UI widget
     * for user input and adjusting the corresponding XML Element/Attribute accordingly.
     */
    public void collectXML() {
    	if (inputs != null) {
    		for (var p: inputs) {
    			if (p.widget() instanceof Text t) {
    				var s = t.getText();
    				s = s.replaceAll("\n+$","").replaceAll("^\n+", "");
    				p.element().setTextContent(s);
    			} else if (p.widget() instanceof Button b) {
    				var hasChecked = p.element().hasAttribute("checked");
    				var checked = b.getSelection();
    				if (checked && !hasChecked) {
    					p.element().setAttribute("checked","");
    				} else if (!checked && hasChecked) {
    					p.element().removeAttribute("checked");
    				}
    			}
    		}
    	}
    }

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
        return button;
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
            default:
        }
    }

    /** Adds a text input control to the parent Composite */
    public Text addTextBox(Composite parent, String label, String initialText, int lines, boolean readonly) {
        if (label != null) addLabel(parent, label);
        Text textBox =
                new Text(parent, SWT.LEFT | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        textBox.setText(initialText.isEmpty()? "\n\n\n\n\n\n" : initialText);
        textBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textBox.setEnabled(!readonly);
        return textBox;
    }

    /** Adds (readonly) text to the parent Composite */
    public void addLabel(Composite parent, String text) {
        Label textBox1 = new Label(parent, SWT.LEFT);
        textBox1.setText(text);
        textBox1.setBackground(parent.getBackground());
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
    public void addSeparator(Composite parent) {
        new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR)
                .setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
