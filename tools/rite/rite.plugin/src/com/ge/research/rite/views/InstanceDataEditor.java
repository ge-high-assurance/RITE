/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2025, GE Aerospace and Galois, Inc.
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
package com.ge.research.rite.views;

import com.ge.research.rite.utils.*;
import com.ge.research.rite.utils.IngestionTemplateUtil;
import com.ge.research.semtk.api.nodeGroupExecution.client.NodeGroupExecutionClient;
import com.ge.research.semtk.sparqlX.SparqlConnection;
import com.google.inject.*;
import com.opencsv.CSVWriter;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.List;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained
 * from the model. The sample creates a dummy model on the fly, but a real implementation would
 * connect to the model available either in this or another plug-in (e.g. the workspace). The view
 * is connected to the model using a content provider.
 *
 * <p>The view uses a label provider to define how model objects should be presented in the view.
 * Each view can present the same model objects using different labels and icons, if needed.
 * Alternatively, a single label provider can be shared between views in order to ensure that
 * objects of the same type are presented in the same way everywhere.
 *
 * <p>
 */
public class InstanceDataEditor extends ViewPart {

    /** The ID of the view as specified by the extension. */
    public static final String ID = "rite.views.SampleTableView";

    public static Image plusImg = null;
    public static Image minusImg = null;
    protected static String filePath = "";
    protected static String selectedNodegroup = "";
    protected static boolean isCore = false;
    public static boolean bUri = false;
    public static ArrayList<String> columns = new ArrayList<>();
    @Inject IWorkbench workbench;
    private TableViewer tableViewer;
    public static Table table;
    protected static ArrayList<String> fileCols = new ArrayList<>();
    protected static ArrayList<String> diffCols = new ArrayList<>();
    private Action action1;
    private Action action2;
    private Action action3;
    private Action action4;

    public static void init(
            String selectedNodegroup,
            boolean isUri,
            String filePath,
            ArrayList<String> fileCols,
            ArrayList<String> diffCols) {
        InstanceDataEditor.selectedNodegroup = selectedNodegroup;
        bUri = isUri;
        InstanceDataEditor.filePath = filePath;
        InstanceDataEditor.fileCols = fileCols;
        InstanceDataEditor.diffCols = diffCols;
    }

    @Override
    public void createPartControl(Composite parent) {

        Display display = Display.getCurrent();
        Set<String> keys = IngestionTemplateUtil.csvTemplates.keySet();
        if (!keys.contains(selectedNodegroup)) {
            return;
        }

        plusImg = ViewUtils.getIcon("plus.png");
        minusImg = ViewUtils.getIcon("minus.png");

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.verticalSpacing = 10;
        parent.setLayout(layout);

        StyledText label = new StyledText(parent, SWT.BORDER);
        GridData labelGridData = new GridData();
        label.setLayoutData(labelGridData);
        String[] splits = selectedNodegroup.split("ingest_");
        String nodegroupLabel = "";
        if (splits.length == 2) {
            nodegroupLabel = splits[1];
        } else {
            nodegroupLabel = selectedNodegroup;
        }
        label.setText(Core.CREATE_INSTANCE_DATA_LABEL + nodegroupLabel);

        StyleRange labelStyle = new StyleRange();
        labelStyle.start = 0;

        labelStyle.length = Core.CREATE_INSTANCE_DATA_LABEL.length();
        labelStyle.fontStyle = SWT.ITALIC;

        StyleRange ngStyle = new StyleRange();
        ngStyle.start = labelStyle.length;
        ngStyle.length = nodegroupLabel.length();
        ngStyle.fontStyle = SWT.BOLD | SWT.ITALIC;

        label.setStyleRange(labelStyle);
        label.setStyleRange(ngStyle);

        label.setForeground(display.getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
        label.setBackground(display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
        Rectangle area = parent.getClientArea();
        label.setLocation(area.x + 5, area.y);

        label.pack();

        renderColumnCheckBoxes(parent, area.x + 5, area.y + 50);

        table = new Table(parent, SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL);
        table.setLocation(area.x + 5, area.y + 350);
        GridData tableGridData = new GridData();
        tableGridData.horizontalAlignment = GridData.FILL;
        tableGridData.verticalAlignment = GridData.FILL;
        tableGridData.grabExcessHorizontalSpace = true;
        tableGridData.grabExcessVerticalSpace = true;
        table.setLayoutData(tableGridData);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setFocus();

        table.setHeaderBackground(display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
        table.setHeaderForeground(display.getSystemColor(SWT.COLOR_TITLE_FOREGROUND));

        table.setEnabled(true);

        // populate csv file into table if it exists, else create a new one

        TableColumn col1 = new TableColumn(table, SWT.LEFT);
        col1.setImage(plusImg);
        col1.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent event) {
                        new TableItem(table, SWT.NONE);
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });
        table.showColumn(col1);

        for (String column : fileCols) {
            TableColumn col = new TableColumn(table, SWT.CENTER);
            col.setText(column);
            col.pack();
            table.showColumn(col);
        }

        List<String[]> rows = CSVUtil.getRows(InstanceDataEditor.filePath);

        for (int i = 0; i < rows.size() + 10; i++) {
            TableItem item = new TableItem(table, SWT.H_SCROLL | SWT.V_SCROLL);

            if (i < rows.size()) {
                int index = 1;
                String[] entries = rows.get(i);
                for (int j = 0; j < entries.length; j++) {
                    // cols from csv file
                    item.setText(index, entries[j]);
                    index++;
                }
            }
        }
        col1.pack();

        TableEditor editor = new TableEditor(table);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        table.addListener(
                SWT.MouseDown,
                event -> {
                    Rectangle clientArea = table.getClientArea();
                    Point pt = new Point(event.x, event.y);
                    int index = table.getTopIndex();
                    while (index < table.getItemCount()) {
                        boolean visible = false;
                        final TableItem item = table.getItem(index);
                        for (int i = 1; i < table.getColumnCount(); i++) {
                            Rectangle rect = item.getBounds(i);
                            if (rect.contains(pt)) {
                                final int column = i;
                                final Text text = new Text(table, SWT.NONE);
                                Listener textListener =
                                        e -> {
                                            switch (e.type) {
                                                case SWT.FocusOut:
                                                    item.setText(column, text.getText());
                                                    text.dispose();
                                                    break;
                                                case SWT.Traverse:
                                                    switch (e.detail) {
                                                        case SWT.TRAVERSE_RETURN:
                                                            item.setText(column, text.getText());
                                                            // FALL THROUGH
                                                        case SWT.TRAVERSE_ESCAPE:
                                                            text.dispose();
                                                            e.doit = false;
                                                    }
                                                    break;
                                            }
                                        };
                                text.addListener(SWT.FocusOut, textListener);
                                text.addListener(SWT.Traverse, textListener);
                                editor.setEditor(text, item, i);
                                text.setText(item.getText(i));
                                text.selectAll();
                                text.setFocus();
                                return;
                            }
                            if (!visible && rect.intersects(clientArea)) {
                                visible = true;
                            }
                        }
                        if (!visible) return;
                        index++;
                    }
                });

        tableViewer = new TableViewer(table);

        TableColumn[] cols = table.getColumns();
        for (int i = 0; i < cols.length; i++) {
            cols[i].pack();
        }
        table.pack();
        makeActions();
        hookContextMenu();

        Listener dispose =
                new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        InstanceDataEditor.plusImg.dispose();
                        InstanceDataEditor.minusImg.dispose();
                    }
                };
        tableViewer.getControl().addListener(SWT.Dispose, dispose);
    }

    private void renderColumnCheckBoxes(Composite parent, int x, int y) {

        Set<String> keys = IngestionTemplateUtil.csvTemplates.keySet();
        if (!keys.contains(InstanceDataEditor.selectedNodegroup)) {
            return;
        }
        Display display = Display.getCurrent();
        Table table = new Table(parent, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData checkBoxesGridData = new GridData();
        checkBoxesGridData.horizontalAlignment = GridData.FILL;
        checkBoxesGridData.verticalAlignment = GridData.FILL;
        checkBoxesGridData.grabExcessHorizontalSpace = true;
        checkBoxesGridData.grabExcessVerticalSpace = true;
        table.setLayoutData(checkBoxesGridData);
        table.setLocation(x, y);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setFocus();
        table.setHeaderBackground(display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
        table.setHeaderForeground(display.getSystemColor(SWT.COLOR_TITLE_FOREGROUND));

        TableColumn col = new TableColumn(table, SWT.LEFT);
        col.setText("Nodegroup ID");

        for (String colName : InstanceDataEditor.fileCols) {
            TableColumn column = new TableColumn(table, SWT.LEFT);
            column.setText(colName);
        }
        for (String colName : InstanceDataEditor.diffCols) {
            TableColumn column = new TableColumn(table, SWT.LEFT);
            column.setText(colName);
        }

        TableItem item = new TableItem(table, SWT.LEFT);
        item.setText(0, InstanceDataEditor.selectedNodegroup);
        for (int i = 1;
                i <= InstanceDataEditor.fileCols.size() + InstanceDataEditor.diffCols.size();
                i++) {
            item.setBackground(i, display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
            TableEditor editor = new TableEditor(table);
            Button checkBtn = new Button(table, SWT.CHECK);
            checkBtn.setSize(100, 100);
            checkBtn.pack();
            editor.minimumWidth = checkBtn.getSize().x;
            editor.minimumHeight = checkBtn.getSize().y;
            editor.setEditor(checkBtn, item, i);

            if (i <= InstanceDataEditor.fileCols.size()) {
                checkBtn.setSelection(true);
                checkBtn.setData("column", InstanceDataEditor.fileCols.get(i - 1));

            } else {
                checkBtn.setSelection(false);
                checkBtn.setData(
                        "column",
                        InstanceDataEditor.diffCols.get(
                                i - 1 - InstanceDataEditor.fileCols.size()));
            }

            checkBtn.addSelectionListener(
                    new SelectionListener() {
                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            Button btn = (Button) event.getSource();
                            if (btn.getSelection() == false) {
                                // toggle and add it to diffCols
                                String column = (String) checkBtn.getData("column");
                                InstanceDataEditor.fileCols.remove(column);
                                InstanceDataEditor.diffCols.add(column);
                                TableColumn[] columns = InstanceDataEditor.table.getColumns();
                                for (TableColumn col : columns) {
                                    if (col.getText().equals(column)) {
                                        col.dispose();
                                    }
                                }

                            } else {
                                String column = (String) checkBtn.getData("column");
                                InstanceDataEditor.diffCols.remove(column);
                                InstanceDataEditor.fileCols.add(column);
                                TableColumn col =
                                        new TableColumn(InstanceDataEditor.table, SWT.NONE);
                                col.setText(column);
                                col.pack();
                                InstanceDataEditor.table.showColumn(col);
                            }
                        }

                        @Override
                        public void widgetDefaultSelected(SelectionEvent event) {}
                    });
        }

        TableColumn[] cols = table.getColumns();
        for (TableColumn column : cols) {
            column.pack();
        }
        table.pack();
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(action1);
        manager.add(action2);
        manager.add(action3);
        manager.add(action4);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void makeActions() {

        action1 =
                new Action() {
                    public void run() {
                        try {
                            TableItem[] items = table.getItems();

                            for (int i = 0; i < items.length; i++) {
                                if (items[i].getChecked() == true) {
                                    items[i].dispose();
                                }
                            }
                        } catch (Exception e) {
                            RackConsole.getConsole().error("Unable to delete selected items");
                        }
                    }
                };
        action1.setText("Delete selected rows");
        action1.setToolTipText("Clear rows from CSV");

        action2 =
                new Action() {
                    public void run() {
                        try {

                            // gather csv and ingest the instance into rack
                            ArrayList<ArrayList<String>> csv = getCSV();
                            NodeGroupExecutionClient client = ConnectionUtil.getNGEClient();
                            SparqlConnection override = ConnectionUtil.getSparqlConnection();

                            com.ge.research.semtk.resultSet.Table tab =
                                    new com.ge.research.semtk.resultSet.Table(fileCols);
                            for (ArrayList<String> row : csv) {
                                tab.addRow(row);
                            }
                            RackConsole.getConsole()
                                    .print(
                                            "Uploading CSV to "
                                                    + RackPreferencePage.getDefaultDataGraph()
                                                    + "as class"
                                                    + OntologyTreeView.getSelectedClassUri()
                                                    + "... ");
                            RackConsole.getConsole().printOK();
                            String sCSV = tab.toCSVString();
                            if (bUri == false) {
                                client.dispatchIngestFromCsvStringsByIdSync(
                                        selectedNodegroup, sCSV, override);
                            } else {
                                client.dispatchIngestFromCsvStringsByClassTemplateSync(
                                        OntologyTreeView.getSelectedClassUri(),
                                        "identifier",
                                        sCSV,
                                        override);
                            }
                        } catch (Exception e) {
                            RackConsole.getConsole().printFAIL();
                            String ingestText = bUri ? "class" : "nodegroup";
                            RackConsole.getConsole()
                                    .error(
                                            "Cannot ingest data for "
                                                    + ingestText
                                                    + ": "
                                                    + selectedNodegroup);
                        }
                    }
                };

        action2.setText("Ingest CSV into RACK");
        action2.setToolTipText("Upload CSV for the current nodegroup to RACK");

        action3 =
                new Action() {
                    public void run() {
                        try {
                            File file = new File(InstanceDataEditor.filePath);
                            if (!file.getParentFile().exists()) {
                                file.getParentFile().mkdirs();
                            }
                            CSVWriter writer =
                                    new CSVWriter(new FileWriter(InstanceDataEditor.filePath));
                            ArrayList<ArrayList<String>> csv = getCSV();
                            ArrayList<String[]> csvOut = new ArrayList<>();
                            String[] arr = new String[fileCols.size()];
                            csvOut.add(fileCols.toArray(arr));
                            for (ArrayList<String> row : csv) {
                                String[] arr2 = new String[fileCols.size()];
                                csvOut.add(row.toArray(arr2));
                            }
                            writer.writeAll(csvOut);
                            writer.close();
                            RackConsole.getConsole()
                                    .print("CSV saved to " + InstanceDataEditor.filePath);
                        } catch (Exception e) {
                            RackConsole.getConsole()
                                    .error(
                                            "Unable to save CSV file: "
                                                    + InstanceDataEditor.filePath);
                        }
                    }
                };

        action3.setText("Save to CSV");
        action3.setToolTipText("Save current state of the table to CSV");

        action4 =
                new Action() {
                    public void run() {
                        int selectedRow = table.getSelectionIndex();
                        // if selected row is invalid, do nothing
                        if (selectedRow < 0) {
                            return;
                        }
                        ArrayList<ArrayList<String>> rows = getRawCSV();
                        ArrayList<String> row = rows.get(selectedRow);

                        // if selected row is empty, do nothing
                        if (CSVUtil.isRowEmpty(row)) {
                            return;
                        }

                        // now find out a new identifier element and append it to the end of the
                        // table

                        int lastIndex = -1;
                        for (int i = rows.size() - 1; i >= 0; i--) {
                            if (!CSVUtil.isRowEmpty(row)) {
                                lastIndex = i;
                                break;
                            }
                        }

                        if (lastIndex < 0) {
                            return; // do nothing
                        }

                        lastIndex++;

                        Random random = new Random();
                        int rand = random.nextInt(Integer.MAX_VALUE - 10);
                        for (int i = 0; i < 10; i++) {

                            ArrayList<String> newRow = new ArrayList<>();
                            for (String entry : row) {
                                if (entry.isEmpty()) {
                                    newRow.add("");
                                } else {
                                    newRow.add(entry + "_" + rand + i);
                                }
                            }
                            TableItem item = null;
                            if (lastIndex < table.getItemCount() && lastIndex >= 0) {
                                item = table.getItem(lastIndex);
                                lastIndex++;
                            } else {
                                // invalidate lastIndex
                                lastIndex = -1;
                                item = new TableItem(table, SWT.NONE);
                            }

                            if (item != null) {
                                for (int j = 1; j <= newRow.size(); j++) {
                                    item.setText(j, newRow.get(j - 1));
                                }
                            }
                        }
                    }
                };

        action4.setText("Autopopulate ten rows");
        action4.setToolTipText("Autopopulate 10 rows based on current row's values");
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu2");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(
                new IMenuListener() {
                    public void menuAboutToShow(IMenuManager manager) {
                        InstanceDataEditor.this.fillContextMenu(manager);
                    }
                });

        Menu menu = menuMgr.createContextMenu(table);
        table.setMenu(menu);
        getSite().registerContextMenu(menuMgr, tableViewer);
    }

    private boolean isRowEmpty(TableItem item) {
        for (int i = 1; i < fileCols.size() + 1; i++) {
            if (!item.getText(i).equals("")) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<ArrayList<String>> getCSV() {
        ArrayList<ArrayList<String>> csv = new ArrayList<>();
        TableItem[] items = table.getItems();
        for (int i = 0; i < items.length; i++) {
            ArrayList<String> row = new ArrayList<>();
            if (!isRowEmpty(items[i])) {
                for (int j = 1; j < fileCols.size() + 1; j++) {
                    row.add(items[i].getText(j));
                }
                csv.add(row);
            }
        }
        return csv;
    }

    private ArrayList<ArrayList<String>> getRawCSV() {
        ArrayList<ArrayList<String>> csv = new ArrayList<>();
        TableItem[] items = table.getItems();
        int lastIndex = -1;
        for (int j = table.getItemCount() - 1; j >= 0; j--) {
            ArrayList<String> row = new ArrayList<>();
            for (int k = 1; k < fileCols.size() + 1; k++) {
                row.add(items[j].getText(k));
            }
            if (!CSVUtil.isRowEmpty(row)) {
                lastIndex = j;
                break;
            }
        }
        if (lastIndex < 0) {
            lastIndex = table.getItemCount();
        }
        for (int i = 0; i <= lastIndex; i++) {
            ArrayList<String> row = new ArrayList<>();
            for (int j = 1; j < fileCols.size() + 1; j++) {
                row.add(items[i].getText(j));
            }
            csv.add(row);
        }
        return csv;
    }

    @Override
    public void setFocus() {}
}
