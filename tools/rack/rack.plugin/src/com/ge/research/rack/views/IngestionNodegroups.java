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

import com.ge.research.rack.HandlerUtils;
import com.ge.research.rack.utils.ConnectionUtil;
import com.ge.research.rack.utils.NodegroupUtil;
import com.ge.research.rack.utils.ProjectUtils;
import com.ge.research.rack.utils.RackConsole;
import com.google.inject.*;

import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class IngestionNodegroups extends ViewPart implements INodegroupView {

    /** The ID of the view as specified by the extension. */
    public static final String ID = "rackplugin.views.IngestionNodegroups";

    @Inject IWorkbench workbench;
    private Action action1;
    private Action action2;
    private Action action3;
    private Table table;
    private int selection = 0;

    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
        @Override
        public String getColumnText(Object obj, int index) {
            return getText(obj);
        }

        @Override
        public Image getColumnImage(Object obj, int index) {
            return getImage(obj);
        }

        @Override
        public Image getImage(Object obj) {
            return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
        }
    }

    @Override
    public void createPartControl(Composite parent) {

        if (!ConnectionUtil.ping()) {
            return;
        }
        Composite composite = new Composite(parent, SWT.NONE);
        Display display = Display.getCurrent();
        composite.setLayout(new FillLayout());
        composite.setSize(1130 / 2, 600);
        table = new Table(composite, SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL);

        table.setSize(1130, 600);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setFocus();

        table.setHeaderBackground(display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
        table.setHeaderForeground(display.getSystemColor(SWT.COLOR_TITLE_FOREGROUND));

        makeActions();
        hookContextMenu();

        Listener dispose =
                new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        table.dispose();
                    }
                };

        table.addListener(SWT.Dispose, dispose);

        TableColumn col1 = new TableColumn(table, SWT.LEFT);
        col1.setText("Nodegroup ID");
        col1.setImage(ViewUtils.getIcon("unchecked.png"));
        col1.setWidth(200);
        col1.pack();
        col1.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        if (selection == 0) {
                            selection = 1;
                            TableItem[] items = table.getItems();
                            for (int i = 0; i < items.length; i++) {
                                items[i].setChecked(true);
                            }
                            col1.setImage(ViewUtils.getIcon("checked.png"));
                        } else {
                            selection = 0;
                            TableItem[] items = table.getItems();
                            for (int i = 0; i < items.length; i++) {
                                items[i].setChecked(false);
                            }
                            col1.setImage(ViewUtils.getIcon("unchecked.png"));
                        }
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });
        table.showColumn(col1);
        TableColumn col2 = new TableColumn(table, SWT.CENTER | SWT.WRAP);
        col2.setText("Comments");
        TableColumn col3 = new TableColumn(table, SWT.CENTER | SWT.WRAP);
        col3.setText("Creation Data");
        TableColumn col4 = new TableColumn(table, SWT.CENTER | SWT.WRAP);
        col4.setText("Creator");

        try {
            ProjectUtils.loadYamls();
            if (NodegroupUtil.nodegroups != null) {
                int size = NodegroupUtil.nodegroups.getResults().getNumRows();
                ArrayList<ArrayList<String>> rows = NodegroupUtil.nodegroups.getResults().getRows();
                rows.sort(
                        new Comparator<ArrayList<String>>() {
                            @Override
                            public int compare(ArrayList<String> row1, ArrayList<String> row2) {
                                return row1.get(0).compareTo(row2.get(0));
                            }
                        });
                for (int i = 0; i < size; i++) {
                    ArrayList<String> text = rows.get(i);
                    // filter ingestion nodegroups
                    Map<String, Object> yamlMap =
                            (Map<String, Object>) NodegroupUtil.ingestionNodegroupMapping;
                    Object oYamlNodegroups = yamlMap.get("nodegroups");
                    ArrayList<String> yamlNodegroups = (ArrayList<String>) oYamlNodegroups;
                    if (NodegroupUtil.ingestionNodegroups.contains(text.get(0))
                            && yamlNodegroups != null
                            && yamlNodegroups.contains(text.get(0))) {
                        TableItem item = new TableItem(table, SWT.NONE);

                        item.setData(text);
                        text.remove(4);
                        int j = 0;
                        for (String element : text) {
                            item.setText(j, element);
                            j++;
                        }
                    }
                }
            }

        } catch (Exception e) {

        }

        int numColumns = table.getColumnCount();
        for (int i = 0; i < numColumns; i++) {
            table.getColumn(i).pack();
        }

        if (table.getItemCount() == 0) {
            table.setEnabled(false);
        } else {
            table.setEnabled(true);
        }
        table.pack();
        composite.pack();
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu1");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(
                new IMenuListener() {
                    public void menuAboutToShow(IMenuManager manager) {
                        IngestionNodegroups.this.fillContextMenu(manager);
                    }
                });

        Menu menu = menuMgr.createContextMenu(table);
        table.setMenu(menu);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(action1);
        manager.add(action2);
        manager.add(action3);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void makeActions() {

        action1 = NodegroupActionFactory.getCreateInstanceDataAction(this);

        action1.setText("Create Instance Data CSV");
        action1.setToolTipText("Create Instance Data for selected Nodegroup and Ingest to RACK");

        action2 =
                new Action() {
                    public void run() {
                        try {
                            // view csv templates

                            // get selected nodes
                            NodegroupTemplateView.selectedNodegroups = getSelectedNodegroups();

                            IWorkbenchWindow window =
                                    PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                            IViewPart view =
                                    window.getActivePage().findView(NodegroupTemplateView.ID);
                            window.getActivePage().hideView(view);
                            window.getActivePage().showView(NodegroupTemplateView.ID);

                        } catch (Exception e) {
                            RackConsole.getConsole()
                                    .println("ERROR showing nodegroup template view");
                        }
                    }
                };
        action2.setText("View CSV Ingestion Templates");

        action3 = NodegroupActionFactory.getQueryNodegroupAction(this);
        action3.setText("Query Nodegroup");
    }

    public ArrayList<String> getSelectedNodegroups() {
        ArrayList<String> ngIds = new ArrayList<>();
        TableItem[] items = table.getItems();
        for (int i = 0; i < items.length; i++) {
            if (items[i].getChecked() == true) {
                // filter only ingestion nodegroups
                ArrayList<String> data = (ArrayList<String>) items[i].getData();
                if (data.get(0).startsWith("ingest_")) {
                    ngIds.add(data.get(0));
                }
            }
        }
        return ngIds;
    }

    private void deleteSelectedNodeGroups() throws Exception {

        // Collect nodegroup ids to be deleted
        ArrayList<String> ngIds = getSelectedNodegroups();
        // delete collected nodegroups by id
        Thread thread =
                new Thread(
                        () -> {
                            try {

                                for (String id : ngIds) {
                                    NodegroupUtil.client.deleteStoredNodeGroup(id);
                                }
                                NodegroupUtil.getAllNodegroups();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
        thread.start();
        thread.join();

        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        HandlerUtils.showNodegroupTable(window);
    }

    @Override
    public void setFocus() {
        if (table != null) {
            table.setFocus();
        }
    }

    public boolean isUri() {
        return false;
    }
}
