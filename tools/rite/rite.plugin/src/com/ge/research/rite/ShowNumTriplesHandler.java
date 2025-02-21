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

import com.ge.research.rite.utils.ConnectionUtil;
import com.ge.research.rite.utils.RackConsole;
import com.ge.research.semtk.resultSet.TableResultSet;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import java.util.ArrayList;

public class ShowNumTriplesHandler extends RiteHandler {

    class NumTriplesWindow extends ApplicationWindow {
        public NumTriplesWindow() {
            super(null);
        }

        @Override
        protected void configureShell(Shell shell) {
            super.configureShell(shell);
            Display display = shell.getDisplay();
            Monitor primary = display.getPrimaryMonitor();
            Rectangle bounds = primary.getBounds();
            Rectangle rect = shell.getBounds();

            int x = bounds.x + (bounds.width - rect.width) / 2;
            int y = bounds.y + (bounds.height - rect.height) / 2;

            shell.setLocation(x, y);
            shell.setText("Current graphs on RACK");
            shell.setFont(new Font(null, "Helvetica", 12, SWT.NORMAL));
        }

        public void run() {
            setBlockOnOpen(true);
            open();
        }

        public void bringToFront(Shell shell) {
            shell.setActive();
        }

        @Override
        protected Control createContents(Composite parent) {
            Composite mainComposite = new Composite(parent, SWT.NONE);
            mainComposite.setLayout(new FillLayout());
            Table table = new Table(mainComposite, SWT.NONE);
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.setFocus();

            try {
                TableResultSet graphInfo = ConnectionUtil.getGraphInfo();
                table.setSize(500, 500);
                TableColumn dataGraphHeader = new TableColumn(table, SWT.CENTER);
                dataGraphHeader.setText("Data graph");
                dataGraphHeader.setWidth(300);
                dataGraphHeader.pack();
                TableColumn numTriples = new TableColumn(table, SWT.CENTER);
                numTriples.setText("# Triples");
                numTriples.pack();
                int numRows = graphInfo.getTable().getNumRows();
                for (int i = 0; i < numRows; i++) {
                    TableItem item = new TableItem(table, SWT.CENTER);
                    ArrayList<String> entry = graphInfo.getResults().getRow(i);
                    int j = 0;
                    for (String cell : entry) {
                        item.setText(j++, cell);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                RackConsole.getConsole().error("Unable to fetch data graphs on RACK");
                return mainComposite;
            }

            table.pack();
            mainComposite.pack();
            return mainComposite;
        }
    }
    ;

    private static NumTriplesWindow numTriplesWindow;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        super.execute(event);
        // hide and show ontology view, also refresh nodegroups views
        if (numTriplesWindow == null) {
            numTriplesWindow = new NumTriplesWindow();
            numTriplesWindow.run();
            numTriplesWindow = null;
        } else {
            numTriplesWindow.bringToFront(numTriplesWindow.getShell());
        }
        return null;
    }
}
