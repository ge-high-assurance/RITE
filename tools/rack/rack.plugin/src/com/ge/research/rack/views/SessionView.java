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

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.*;

import com.ge.research.rack.RunWorkflowHandler;

public class SessionView extends ViewPart {

    /** The ID of the view as specified by the extension. */
    public static final String ID = "rackplugin.views.SessionView";

    private Composite outer;
    private Composite composite;
    public RunWorkflowHandler handler;

    @Override
    public void setFocus() {}

    @Override
    public void createPartControl(Composite parent) {

        final Display display = Display.getCurrent();

        outer = new Composite(parent, SWT.NONE);
        GridLayout layout0 = new GridLayout();
        layout0.numColumns = 1;
        layout0.verticalSpacing = 10;
        outer.setLayout(layout0);
        outer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Composite buttons = new Composite(outer, SWT.NONE);
        GridLayout layout2 = new GridLayout();
        layout2.numColumns = 4; // number of buttons
        layout2.verticalSpacing = 10;
        buttons.setLayout(layout2);
        buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        createButton(buttons, IDialogConstants.ABORT_ID, "Abort", false);
        createButton(buttons, IDialogConstants.RETRY_ID, "Restart", false);
        createButton(buttons, IDialogConstants.BACK_ID, "Back", false);
        createButton(buttons, IDialogConstants.OK_ID, "Next", true);

        //      final ScrolledComposite sc = new ScrolledComposite(outer, SWT.H_SCROLL |
        // SWT.V_SCROLL);
        composite = clearXMLDisplay();
        //      sc.setContent(composite);

        // FIXME - restore the scrolling
        // FIXME - put buttons at bottom
        // FIXME - fix clearing and restarting the di8splay

        parent.layout(true, true);
    }

    protected Button createButton(Composite buttons, int id, String label, boolean defaultButton) {
        // increment the number of columns in the button bar
        //		((GridLayout) buttons.getLayout()).numColumns++;
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
        // buttons.put(Integer.valueOf(id), button);
        // setButtonLayoutData(button);
        return button;
    }

    protected void buttonPressed(int buttonId) {
        //MessageDialog.openInformation(null, "", "Button " + buttonId);
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
        default:
        }
    }



    public void addTextBox(String label, String initialText, int lines, boolean readonly) {
        if (label != null) addLabel(label, composite);
        Text textBox2 =
                new Text(composite, SWT.LEFT | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        textBox2.setText(initialText);
        textBox2.setTextLimit(10000);
        // FIXME - set size, readonly
    }

    public void addLabel(String text, Composite parent) {
        Label textBox1 = new Label(parent, SWT.LEFT);
        textBox1.setText(text);
        textBox1.setBackground(parent.getBackground());
        // TODO: Would like to set to bold
    }

    public void addSeparator() {
        // FIXME - make longer, make darker
        var rule = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
    }

    public void addButton(String label) {
        Button b = new Button(composite, SWT.PUSH);
        b.setText(label);
        b.addSelectionListener(
                new SelectionListener() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        //        		next();

                    }

                    @Override
                    public void widgetDefaultSelected(SelectionEvent e) {
                        //       		next();
                    }
                });
    }


    public Composite clearXMLDisplay() {
    	if (this.composite != null && !this.composite.isDisposed()) this.composite.dispose();
        Composite composite = new Composite(outer, SWT.NONE);
        GridLayout layout1 = new GridLayout();
        layout1.numColumns = 1;
        layout1.verticalSpacing = 10;
        composite.setLayout(layout1);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        return composite;
    }

    public void displayXML(Element top, String name) {
        composite = clearXMLDisplay();
        addLabel("Executing workflow " + name, composite);
        try {
            if (!"form".equals(top.getNodeName())) {
                MessageDialog.openError(null, "Error", "Incorrrect top-level node name");
                return;
            }
            NodeList ns = top.getElementsByTagName("control");
            for (int i = 0; i < ns.getLength(); i++) {
                Node n = ns.item(i);
                if (n instanceof Element element) {
                    String type = element.getAttribute("type");
                    String label = element.getAttribute("label");
                    if ("textinput".equals(type)) {
                        String lines = element.getAttribute("lines");
                        String readonly = element.getAttribute("readonly");
                        addTextBox(label, "Add text", 10, "true".equals(readonly));
                    } else if ("label".equals(type)) {
                        addLabel(label, composite);
                    } else {
                        MessageDialog.openInformation(null, "", "Unknown control type: " + type);
                    }
                }
            }
            addSeparator();
            outer.layout(true, true);

        } catch (Exception e) {
            MessageDialog.openError(null, "Error", "Failure to display XML\n" + e);
        }
    }
}
