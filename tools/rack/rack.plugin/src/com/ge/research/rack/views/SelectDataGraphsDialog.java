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

import com.ge.research.rack.utils.CSVUtil;
import com.ge.research.rack.utils.ConnectionUtil;
import com.ge.research.rack.utils.Core;
import com.ge.research.rack.utils.ProjectUtils;
import com.ge.research.rack.utils.RackConsole;
import com.ge.research.semtk.api.nodeGroupExecution.client.NodeGroupExecutionClient;
import com.ge.research.semtk.resultSet.TableResultSet;
import com.ge.research.semtk.sparqlX.SparqlConnection;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import java.io.File;
import java.util.ArrayList;

/** Author: Paul Meng */
public class SelectDataGraphsDialog extends Dialog {
    private Font font;
    //private Font boldFont;
    public static String nodegroupId = "";
    private static Table table;

    public SelectDataGraphsDialog(Shell parent, String nodegroup) {
        super(parent);
        nodegroupId = nodegroup;
        font = new Font(null, "Helvetica", 12, SWT.NORMAL);
        //boldFont = new Font(null, "Helvetica", 12, SWT.BOLD);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);

        shell.setText("Select Data Graphs: " + nodegroupId);
        shell.setFont(font);
    }

    public void run() {
        setBlockOnOpen(true);
        open();
    }

    public void bringToFront(Shell shell) {
        shell.setActive();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout(1, false));
        mainComposite.setSize(700, 700);
        renderNumTriples(mainComposite);
        // save and close buttons
        Composite closeButtons = new Composite(mainComposite, SWT.NONE);
        closeButtons.setLayout(new RowLayout(SWT.HORIZONTAL));
        closeButtons.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, true, true, 1, 1));

        Button cancel = new Button(closeButtons, SWT.PUSH);
        cancel.setText("Cancel");
        cancel.setFont(font);

        Button save = new Button(closeButtons, SWT.PUSH);
        save.setText("Execute Query Nodegroup");
        save.setFont(font);

        // Set font for button text
        save.setFont(font);

        // Set the preferred size
        Point bestSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        getShell().setSize(bestSize);

        cancel.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        mainComposite.getShell().close();
                    }
                });

        save.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {

                        try {
                            TableItem[] items = table.getItems();
                            ArrayList<String> dataGraphs = new ArrayList<>();
                            for (TableItem item : items) {
                                if (item.getChecked()) {
                                    dataGraphs.add(item.getText(0));
                                }
                            }

                            NodeGroupExecutionClient client = ConnectionUtil.getNGEClient();
                            SparqlConnection conn =
                                    ConnectionUtil.getSparqlConnection(
                                            dataGraphs.get(0), dataGraphs);
                            RackConsole.getConsole()
                                    .print("Querying nodegroup: " + nodegroupId + " ... ");
                            // run a query from the store by id
                            com.ge.research.semtk.resultSet.Table results =
                                    client.execDispatchSelectByIdToTable(
                                            nodegroupId, conn, null, null);
                            RackConsole.getConsole().printOK();
                            QueryResultsView.results = results;
                            String csv_string = results.toCSVString();
                            String dir = RackPreferencePage.getInstanceDataFolder();
                            ProjectUtils.validateInstanceDataFolder();
                            String queryResultsDir = dir + "/" + Core.QUERY_RESULTS_FOLDER + "/";
                            File file = new File(queryResultsDir);
                            if (!file.exists() || !file.isDirectory()) {
                                RackConsole.getConsole()
                                        .print("No QueryResults folder found, creating one");
                                file.mkdirs();
                            }
                            CSVUtil.writeToCSV(
                                    csv_string,
                                    queryResultsDir + nodegroupId + "_queryresults.csv");
                            System.out.println("Results for queried nodegroup: ");
                            System.out.println(csv_string);

                            Display.getDefault()
                                    .asyncExec(
                                            () -> {
                                                ViewUtils.showView(QueryResultsView.ID);
                                            });

                        } catch (Exception e) {
                            RackConsole.getConsole().printFAIL();
                            RackConsole.getConsole().error("Unable to show query result view");
                        }

                        mainComposite.getShell().close();
                    }
                });
        return mainComposite;
    }

    private void renderNumTriples(Composite parent) {

        try {
            TableResultSet graphInfo = ConnectionUtil.getGraphInfo();
            Composite mainComposite = new Composite(parent, SWT.NONE);
            mainComposite.setLayout(new FillLayout());
            table = new Table(mainComposite, SWT.CHECK);
            table.setHeaderVisible(true);
            table.setLinesVisible(false);
            table.setFocus();
            table.setSize(1000, 1000);
            TableColumn dataGraphHeader = new TableColumn(table, SWT.CENTER);
            dataGraphHeader.setText("Data graph");
            dataGraphHeader.setWidth(600);
            int numRows = graphInfo.getTable().getNumRows();
            for (int i = 0; i < numRows; i++) {
                TableItem item = new TableItem(table, SWT.CENTER | SWT.CHECK);
                ArrayList<String> entry = graphInfo.getResults().getRow(i);
                item.setText(0, entry.get(0));
                if (entry.get(0).contains(RackPreferencePage.getDefaultDataGraph())) {
                    item.setChecked(true);
                }
            }

            parent.addDisposeListener(
                    new DisposeListener() {
                        @Override
                        public void widgetDisposed(DisposeEvent e) {
                            parent.dispose();
                        }
                    });
            dataGraphHeader.pack();
            table.pack();
            mainComposite.pack();
        } catch (Exception e) {
            RackConsole.getConsole().error("Unable to fetch data graphs on RACK");
        }
    }
}
