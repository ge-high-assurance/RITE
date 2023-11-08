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

import com.ge.research.rack.IngestInstanceDataHandler;
import com.ge.research.rack.utils.ConnectionUtil;
import com.ge.research.rack.utils.RackConsole;
import com.ge.research.semtk.resultSet.TableResultSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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

import java.util.ArrayList;
import java.util.Arrays;

/** Author: Paul Meng */
public class ClearRackDialog extends Dialog {
    private Font font, boldFont;
    public static String nodegroupId = "";
    private static Table modelGraphsTable;
    private static Table dataGraphsTable;
    private static Button deleteAllNodegroupsBtn;
    private static String DELETE_ALL_NODEGROUPS = "Delete all nodegroups";
    private static String CLEAR_RACK_TITLE = "Clear Ontologies, Datagraphs or Nodegroups from RACK";

    public ClearRackDialog(Shell parent, String nodegroup) {
        super(parent);
        nodegroupId = nodegroup;
        font = new Font(null, "Helvetica", 12, SWT.NORMAL);
        boldFont = new Font(null, "Helvetica", 12, SWT.BOLD);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);

        shell.setText(CLEAR_RACK_TITLE);
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

        Button selectAll = new Button(closeButtons, SWT.PUSH);
        selectAll.setText("Select All");
        selectAll.setFont(font);

        Button deselectAll = new Button(closeButtons, SWT.PUSH);
        deselectAll.setText("Deselect All");
        deselectAll.setFont(font);

        Button refresh = new Button(closeButtons, SWT.PUSH);
        refresh.setText("Refresh");
        refresh.setFont(font);

        Button confirm = new Button(closeButtons, SWT.PUSH);
        confirm.setText("Confirm");
        confirm.setFont(font);

        // Set font for button text
        confirm.setFont(font);

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

        confirm.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {

                        try {
                            if (IngestInstanceDataHandler.isRunning()) {
                                RackConsole.getConsole()
                                        .error(
                                                "A manifest import operation is in progress, please wait before clearing contents on RACK");
                                return;
                            }

                            TableItem[] items = modelGraphsTable.getItems();
                            ArrayList<String> modelGraphs = new ArrayList<>();
                            for (TableItem item : items) {
                                if (item.getChecked()) {
                                    modelGraphs.add(item.getText(0));
                                }
                            }

                            items = dataGraphsTable.getItems();
                            ArrayList<String> dataGraphs = new ArrayList<>();
                            for (TableItem item : items) {
                                if (item.getChecked()) {
                                    dataGraphs.add(item.getText(0));
                                }
                            }

                            RackConsole.getConsole().clearConsole();

                            if (deleteAllNodegroupsBtn.getSelection() == true) {
                                if (!MessageDialog.openConfirm(
                                        Display.getDefault().getActiveShell(),
                                        "Confirm",
                                        "Confirm that all nodegroups will deleted")) {
                                    return;
                                }
                            }

                            if (modelGraphs.size() > 0) {
                                RackConsole.getConsole().print("Clearing model graphs...");
                                for (String modelGraph : modelGraphs) {
                                    RackConsole.getConsole()
                                            .print("Clearing model graph " + modelGraph + " ... ");
                                    ConnectionUtil.getOntologyUploadClient(modelGraph).dropGraph();
                                    RackConsole.getConsole().printOK();
                                }
                            }

                            if (dataGraphs.size() > 0) {
                                RackConsole.getConsole().print("Clearing data graphs...");
                                for (String dataGraph : dataGraphs) {
                                    RackConsole.getConsole()
                                            .print("Clearing data graph " + dataGraph + " ... ");
                                    ConnectionUtil.getDataGraphClient(dataGraph).dropGraph();
                                    RackConsole.getConsole().printOK();
                                }
                            }

                            if (deleteAllNodegroupsBtn.getSelection() == true) {
                                RackConsole.getConsole().print("Deleting all nodegroups ...");
                                ConnectionUtil.getNGSClient().deleteAllStoredNodeGroups();
                                RackConsole.getConsole().printOK();
                            }

                            RackConsole.getConsole().print("Done");
                            mainComposite.getShell().close();

                        } catch (Exception e) {
                            RackConsole.getConsole().printFAIL();
                            RackConsole.getConsole()
                                    .error(
                                            "An error occurred while clearing content from RACK, check connection parameters to RACK or RACK-BOX instance");
                        }
                    }
                });

        selectAll.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent event) {

                        Arrays.stream(modelGraphsTable.getItems())
                                .forEach(item -> item.setChecked(true));
                        Arrays.stream(dataGraphsTable.getItems())
                                .forEach(item -> item.setChecked(true));
                        deleteAllNodegroupsBtn.setSelection(true);
                    }
                });

        deselectAll.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        Arrays.stream(modelGraphsTable.getItems())
                                .forEach(item -> item.setChecked(false));
                        Arrays.stream(dataGraphsTable.getItems())
                                .forEach(item -> item.setChecked(false));
                        deleteAllNodegroupsBtn.setSelection(false);
                    }
                });

        refresh.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {

                        TableResultSet graphInfo;
                        Arrays.stream(modelGraphsTable.getItems()).forEach(item -> item.dispose());
                        Arrays.stream(dataGraphsTable.getItems()).forEach(item -> item.dispose());
                        try {
                            graphInfo = ConnectionUtil.getGraphInfo();

                            int numRows = graphInfo.getTable().getNumRows();
                            for (int i = 0; i < numRows; i++) {
                                TableItem item =
                                        new TableItem(modelGraphsTable, SWT.CENTER | SWT.CHECK);
                                ArrayList<String> entry = graphInfo.getResults().getRow(i);
                                item.setText(0, entry.get(0));
                                if (entry.get(0)
                                        .contains(RackPreferencePage.getDefaultDataGraph())) {
                                    item.setChecked(true);
                                }
                            }

                            numRows = graphInfo.getTable().getNumRows();
                            for (int i = 0; i < numRows; i++) {
                                TableItem item =
                                        new TableItem(dataGraphsTable, SWT.CENTER | SWT.CHECK);
                                ArrayList<String> entry = graphInfo.getResults().getRow(i);
                                item.setText(0, entry.get(0));
                                if (entry.get(0)
                                        .contains(RackPreferencePage.getDefaultDataGraph())) {
                                    item.setChecked(true);
                                }
                            }
                        } catch (Exception e) {
                            RackConsole.getConsole()
                                    .error(
                                            "An error occured when refreshing model graphs and data graphs from RACK, please check connection to RACK or RACK-BOX instance");
                        }
                    }
                });

        return mainComposite;
    }

    private void renderNumTriples(Composite parent) {

        try {
            TableResultSet graphInfo = ConnectionUtil.getGraphInfo();
            Composite mainComposite = new Composite(parent, SWT.NONE);
            mainComposite.setLayout(new FillLayout(SWT.VERTICAL));
            Composite graphsComposite = new Composite(mainComposite, SWT.NONE);
            RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
            graphsComposite.setLayout(rowLayout);

            modelGraphsTable = new Table(graphsComposite, SWT.CHECK);
            modelGraphsTable.setHeaderVisible(true);
            modelGraphsTable.setLinesVisible(false);
            modelGraphsTable.setFocus();
            modelGraphsTable.setSize(1000, 1000);

            dataGraphsTable = new Table(graphsComposite, SWT.CHECK);
            dataGraphsTable.setHeaderVisible(true);
            dataGraphsTable.setLinesVisible(false);
            dataGraphsTable.setFocus();
            dataGraphsTable.setSize(1000, 1000);

            Composite nodegroupsBtnComposite = new Composite(mainComposite, SWT.NONE);
            nodegroupsBtnComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
            deleteAllNodegroupsBtn = new Button(nodegroupsBtnComposite, SWT.CHECK);
            deleteAllNodegroupsBtn.setText(DELETE_ALL_NODEGROUPS);

            TableColumn modelGraphHeader = new TableColumn(modelGraphsTable, SWT.CENTER);
            modelGraphHeader.setText("Model graphs");
            modelGraphHeader.setWidth(300);
            int numRows = graphInfo.getTable().getNumRows();
            for (int i = 0; i < numRows; i++) {
                TableItem item = new TableItem(modelGraphsTable, SWT.CENTER | SWT.CHECK);
                ArrayList<String> entry = graphInfo.getResults().getRow(i);
                item.setText(0, entry.get(0));
                if (entry.get(0).contains(RackPreferencePage.getDefaultDataGraph())) {
                    item.setChecked(true);
                }
            }

            TableColumn dataGraphHeader = new TableColumn(dataGraphsTable, SWT.CENTER);
            dataGraphHeader.setText("Data graphs");
            dataGraphHeader.setWidth(300);
            numRows = graphInfo.getTable().getNumRows();
            for (int i = 0; i < numRows; i++) {
                TableItem item = new TableItem(dataGraphsTable, SWT.CENTER | SWT.CHECK);
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
            modelGraphsTable.pack();
            dataGraphsTable.pack();
            graphsComposite.pack();
            nodegroupsBtnComposite.pack();
            mainComposite.pack();
        } catch (Exception e) {
            RackConsole.getConsole().error("Unable to fetch data graphs on RACK");
        }
    }
}
