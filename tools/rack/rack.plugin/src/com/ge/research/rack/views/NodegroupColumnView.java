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

import com.ge.research.rack.utils.IngestionTemplateUtil;
import com.google.inject.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;

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
public class NodegroupColumnView extends ViewPart {

    /** The ID of the view as specified by the extension. */
    public static final String ID = "rackplugin.views.ManageCSVColumns";

    @Inject IWorkbench workbench;

    //    private TableViewer viewer;
    //    private Action action1;
    //    private Action action2;
    //    private Action doubleClickAction;
    private static Table table;
    public static ArrayList<String> selectedNodegroups = null;

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
        Display display = Display.getCurrent();

        Set<String> keys = IngestionTemplateUtil.csvTemplates.keySet();
        if (!keys.contains(InstanceDataEditor.selectedNodegroup)) {
            return;
        }
        table = new Table(parent, SWT.NONE);

        table.setSize(1130, 600);
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

                            /* Display.getDefault().asyncExec(() -> {
                             try {
                            	IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                            	IViewPart instanceDataView = window.getActivePage().findView(InstanceDataEditor.ID);
                            	//instanceDataView.dispose();
                            	window.getActivePage().hideView(instanceDataView);

                            	window.getActivePage().showView(InstanceDataEditor.ID);
                             }
                             catch(Exception e) {
                            	 e.printStackTrace();
                             }

                            });*/
                        }

                        @Override
                        public void widgetDefaultSelected(SelectionEvent event) {}
                    });
        }

        pack();
    }

    public static void pack() {
        ArrayList<TableColumn> columns = new ArrayList<>(Arrays.asList(table.getColumns()));
        for (TableColumn column : columns) {
            column.pack();
        }
        table.pack();
    }

    //    private void hookContextMenu() {
    //        MenuManager menuMgr = new MenuManager("#PopupMenu");
    //        menuMgr.setRemoveAllWhenShown(true);
    //        menuMgr.addMenuListener(
    //                new IMenuListener() {
    //                    public void menuAboutToShow(IMenuManager manager) {
    //                        NodegroupColumnView.this.fillContextMenu(manager);
    //                    }
    //                });
    //        Menu menu = menuMgr.createContextMenu(viewer.getControl());
    //        viewer.getControl().setMenu(menu);
    //        getSite().registerContextMenu(menuMgr, viewer);
    //    }

    //    private void contributeToActionBars() {
    //        IActionBars bars = getViewSite().getActionBars();
    //        fillLocalPullDown(bars.getMenuManager());
    //        fillLocalToolBar(bars.getToolBarManager());
    //    }

    //    private void fillLocalPullDown(IMenuManager manager) {
    //        manager.add(action1);
    //        manager.add(new Separator());
    //        manager.add(action2);
    //    }

    //    private void fillContextMenu(IMenuManager manager) {
    //        manager.add(action1);
    //        manager.add(action2);
    //        // Other plug-ins can contribute there actions here
    //        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    //    }

    //    private void fillLocalToolBar(IToolBarManager manager) {
    //        manager.add(action1);
    //        manager.add(action2);
    //    }

    //    private void makeActions() {
    //        action1 =
    //                new Action() {
    //                    public void run() {
    //                        showMessage("Action 1 executed");
    //                    }
    //                };
    //        action1.setText("Action 1");
    //        action1.setToolTipText("Action 1 tooltip");
    //        action1.setImageDescriptor(
    //                PlatformUI.getWorkbench()
    //                        .getSharedImages()
    //                        .getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    //
    //        action2 =
    //                new Action() {
    //                    public void run() {
    //                        showMessage("Action 2 executed");
    //                    }
    //                };
    //        action2.setText("Action 2");
    //        action2.setToolTipText("Action 2 tooltip");
    //        action2.setImageDescriptor(
    //
    // workbench.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    //        doubleClickAction =
    //                new Action() {
    //                    public void run() {
    //                        IStructuredSelection selection = viewer.getStructuredSelection();
    //                        Object obj = selection.getFirstElement();
    //                        showMessage("Double-click detected on " + obj.toString());
    //                    }
    //                };
    //    }

    //    private void hookDoubleClickAction() {
    //        viewer.addDoubleClickListener(
    //                new IDoubleClickListener() {
    //                    public void doubleClick(DoubleClickEvent event) {
    //                        doubleClickAction.run();
    //                    }
    //                });
    //    }

    //    private void showMessage(String message) {
    //        MessageDialog.openInformation(
    //                viewer.getControl().getShell(), "View CSV Templates", message);
    //    }

    @Override
    public void setFocus() {
        // viewer.getControl().setFocus();
    }
}
