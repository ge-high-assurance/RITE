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
import com.ge.research.rack.utils.IngestionTemplateUtil;
import com.ge.research.rack.utils.OntologyUtil;
import com.ge.research.rack.utils.RackConsole;
import com.ge.research.rack.utils.Triple;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Author: Paul Meng */
public class DiffGraphsDialog extends Dialog {
    private Font font, boldFont;
    public static String nodegroupId = "";
    private static Table modelGraphsTable;
    private static Table dataGraphsTable;
    private static Button deleteAllNodegroupsBtn;
    private static String DELETE_ALL_NODEGROUPS = "Delete all nodegroups";
    private static String DIFF_RACK_TITLE = "Diff source and target datagraph from RACK";

    public DiffGraphsDialog(Shell parent, String nodegroup) {
        super(parent);
        nodegroupId = nodegroup;
        font = new Font(null, "Helvetica", 12, SWT.NORMAL);
        boldFont = new Font(null, "Helvetica", 12, SWT.BOLD);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);

        shell.setText(DIFF_RACK_TITLE);
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
                                                "A manifest import operation is in progress, please wait");
                                return;
                            }

                            IngestionTemplateUtil.buildCsvTemplates();

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

                            String sourceDataGraph = modelGraphs.get(0);
                            String targetDataGraph = dataGraphs.get(0);

                            List<String> classUris = OntologyUtil.getClassNames();
                            List<String> classNames = new ArrayList<>();
                            for (String classUri : classUris) {
                                // RackConsole.getConsole().print(classUri);
                                String className =
                                        OntologyUtil.getoInfo()
                                                .getClass(classUri)
                                                .getNameString(true);
                                classNames.add(className);
                                // RackConsole.getConsole().print(className);
                            }
                            String queryPrefix =
                                    "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                                            + "prefix rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n"
                                            + "prefix PROV_S:<http://arcos.rack/PROV-S#>\n";
                            queryPrefix += "prefix PROCESS:<http://arcos.rack/PROCESS#>\n";
                            queryPrefix += "prefix DOCUMENT:<http://arcos.rack/DOCUMENT#>\n";

                            String querySelect =
                                    "select distinct ?s ?sid ?p ?o ?oid FROM <http://rack001/do-178c>\n"
                                            + "WHERE { ?s a"
                                            + " <http://arcos.rack/DOCUMENT#SPECIFICATION>.\n"
                                            + "	                ?s ?p ?o.\n"
                                            + "minus { ?s PROV_S:identifier ?o } .\n"
                                            + "minus { ?s a ?o } .\n"
                                            + "optional { ?s PROV_S:identifier ?sid } .\n"
                                            + "optional { ?o PROV_S:identifier ?oid } .\n"
                                            + "}\n"
                                            + "order by ?sid ?p ?oid ?o ?s\n";

                            String querySelectV2 =
                                    "select distinct ?s ?sid ?p ?o ?oid FROM <http://rack001/do-178cV2>\n"
                                            + "WHERE { ?s a"
                                            + " <http://arcos.rack/DOCUMENT#SPECIFICATION>.\n"
                                            + "	                ?s ?p ?o.\n"
                                            + "minus { ?s PROV_S:identifier ?o } .\n"
                                            + "minus { ?s a ?o } .\n"
                                            + "optional { ?s PROV_S:identifier ?sid } .\n"
                                            + "optional { ?o PROV_S:identifier ?oid } .\n"
                                            + "}\n"
                                            + "order by ?sid ?p ?oid ?o ?s";
                            String queryV1 = queryPrefix + querySelect;
                            String queryV2 = queryPrefix + querySelectV2;
                            NodeGroupExecutionClient client = ConnectionUtil.getNGEClient();
                            List<String> dataGraphsV1 = new ArrayList<String>();
                            dataGraphsV1.add(sourceDataGraph);
                            SparqlConnection connV1 =
                                    ConnectionUtil.getSparqlConnection(
                                            RackPreferencePage.getDefaultModelGraph(),
                                            dataGraphsV1.get(0),
                                            dataGraphsV1);

                            List<String> dataGraphsV2 = new ArrayList<String>();
                            dataGraphsV2.add(targetDataGraph);
                            SparqlConnection connV2 =
                                    ConnectionUtil.getSparqlConnection(
                                            RackPreferencePage.getDefaultModelGraph(),
                                            dataGraphsV2.get(0),
                                            dataGraphsV2);

                            com.ge.research.semtk.resultSet.Table resultsV1 =
                                    client.dispatchRawSparql(queryV1, connV1);
                            com.ge.research.semtk.resultSet.Table resultsV2 =
                                    client.dispatchRawSparql(queryV2, connV2);

                            int a = 0;
                            a++;

                            resultsV1.sortByColumnStr("sid");
                            resultsV2.sortByColumnStr("sid");

                            int headV1 = 0;
                            int headV2 = 0;

                            while (headV1 < resultsV1.getNumRows()
                                    && headV2 < resultsV2.getNumRows()) {

                                String subjectV1 = resultsV1.getCell(headV1, "sid");
                                String subjectV2 = resultsV2.getCell(headV2, "sid");

                                String predicateV1 = resultsV1.getCell(headV1, "p");
                                String predicateV2 = resultsV2.getCell(headV2, "p");

                                String objectV1 = resultsV1.getCell(headV1, "o");
                                String objectV2 = resultsV2.getCell(headV2, "o");

                                int comparison = subjectV1.compareTo(subjectV2);

                                Triple tripleV1 = new Triple(subjectV1, predicateV1, objectV1);
                                Triple tripleV2 = new Triple(subjectV2, predicateV2, objectV2);

                                if (comparison == 0) {
                                    // check for diff

                                    if (objectV1.contains("uri://semtk")
                                            && objectV2.contains("uri://semtk")) {
                                        headV1++;
                                        headV2++;
                                        continue;
                                    }

                                    if (!objectV1.equals(objectV2)) {
                                        String sCSV =
                                                "identifier,previousVersion_identifier,obsolete\n"
                                                        + subjectV2
                                                        + "_V2"
                                                        + ","
                                                        + subjectV1
                                                        + ","
                                                        + "false"
                                                        + "\n";
                                        client.dispatchIngestFromCsvStringsByClassTemplateSync(
                                                "http://arcos.rack/DOCUMENT#SPECIFICATION",
                                                "identifier",
                                                sCSV,
                                                connV2);
                                        
                                        
                                        
                                         sCSV =
                                                "identifier,modifiedPropertiesFromPreviousVersion\n"
                                                        + subjectV2 + "_V2"
                                                        + ","
                                                        + "title" + "\n";
                                         client.dispatchIngestFromCsvStringsByClassTemplateSync(
                                                 "http://arcos.rack/DOCUMENT#SPECIFICATION",
                                                 "identifier",
                                                 sCSV,
                                                 connV2);
                                         
                                         sCSV =
                                                 "identifier,modifiedPropertiesFromPreviousVersion\n"
                                                         + subjectV2 + "_V2"
                                                         + ","
                                                         + "description" + "\n";
                                          client.dispatchIngestFromCsvStringsByClassTemplateSync(
                                                  "http://arcos.rack/DOCUMENT#SPECIFICATION",
                                                  "identifier",
                                                  sCSV,
                                                  connV2);
                                         
                                         
                                         
                                         RackConsole.getConsole()
                                         .print(
                                                 "Diff found"
                                                         + "\n V1:"
                                                         + tripleV1.toString()
                                                         + "\nV2:"
                                                         + tripleV2.toString());
                                        return;
                                    }

                                    headV1++;
                                    headV2++;
                                }

                                if (comparison < 0) {
                                    RackConsole.getConsole()
                                            .print("\nDeleted in V2: " + tripleV1.toString());
                                    headV1++;
                                }

                                if (comparison > 0) {
                                    RackConsole.getConsole()
                                            .print("\nCreated in V2: " + tripleV2.toString());
                                    headV2++;
                                }
                            }

                            /*
                             * for (String className : classUris) { NodeGroupExecutionClient client =
                             * ConnectionUtil.getNGEClient(); String nodegroupId =
                             * IngestionTemplateUtil.getIngestionNodegroupId(className); List<String>
                             * dataGraphsV1 = new ArrayList<String>(); dataGraphsV1.add(sourceDataGraph);
                             * SparqlConnection connV1 = ConnectionUtil.getSparqlConnection(
                             * RackPreferencePage.getDefaultModelGraph(), dataGraphsV1.get(0),
                             * dataGraphsV1); // run a query from the store by id
                             *
                             * com.ge.research.semtk.resultSet.Table resultsV1 =
                             * client.execDispatchSelectByIdToTable( nodegroupId, connV1, null, null);
                             *
                             * List<String> dataGraphsV2 = new ArrayList<String>();
                             * dataGraphsV2.add(targetDataGraph); SparqlConnection connV2 =
                             * ConnectionUtil.getSparqlConnection(
                             * RackPreferencePage.getDefaultModelGraph(), dataGraphsV2.get(0),
                             * dataGraphsV2); // run a query from the store by id
                             *
                             * com.ge.research.semtk.resultSet.Table resultsV2 =
                             * client.execDispatchSelectByIdToTable( nodegroupId, connV2, null, null);
                             *
                             * resultsV1.sortByColumnStr("identifier"); // diff here if
                             * (resultsV1.getNumRows() > 0 && resultsV2.getNumRows() > 0) { for (int i = 0;
                             * i < resultsV1.getNumRows(); i++) { for (int j = 0; j <
                             * resultsV2.getNumRows(); j++) { List<String> sourceRow = resultsV1.getRow(i);
                             * List<String> targetRow = resultsV2.getRow(j); if
                             * (!sourceRow.get(0).equals(targetRow.get(0))) { continue; } String csvTemplate
                             * = IngestionTemplateUtil.csvTemplates.get( nodegroupId); List<String>
                             * templateHeader = Arrays.asList(csvTemplate.split(","));
                             *
                             * int index = 0; boolean sameEntry = true;
                             *
                             * for (String entry : templateHeader) { if (entry.equals("identifier")) { //
                             * compare against all identifier fields if (!sourceRow .get(index)
                             * .equals(targetRow.get(index))) { sameEntry = false; break; } } index++; }
                             *
                             * if (sameEntry == false) { continue; }
                             *
                             * // we are now comparing same entry
                             *
                             * if (sourceRow.size() != targetRow.size()) { RackConsole.getConsole() .print(
                             * "Diff size: " + sourceRow.toString() + " ** " + targetRow.toString());
                             * continue; }
                             *
                             * for (int column = 0; column < sourceRow.size(); column++) { if (!sourceRow
                             * .get(column) .equals(targetRow.get(column))) { // diff found
                             * RackConsole.getConsole().print("Diff found!"); RackConsole.getConsole()
                             * .print(sourceRow.toString()); RackConsole.getConsole()
                             * .print(targetRow.toString()); } } } } } // mainComposite.getShell().close();
                             * }
                             */

                            mainComposite.getShell().close();
                            RackConsole.getConsole().print("Done");
                        } catch (Exception e) {
                            e.printStackTrace();
                            RackConsole.getConsole().printFAIL();
                            RackConsole.getConsole()
                                    .error(
                                            "An error occurred, check connection parameters to RACK or RACK-BOX instance");
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
            modelGraphHeader.setText("Source data graph");
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
            dataGraphHeader.setText("Target Data graph");
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
