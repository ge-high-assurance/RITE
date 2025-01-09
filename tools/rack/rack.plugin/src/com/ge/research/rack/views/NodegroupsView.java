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

import com.ge.research.rack.RefreshHandler;
import com.ge.research.rack.utils.ConnectionUtil;
import com.ge.research.rack.utils.ErrorMessageUtil;
import com.ge.research.rack.utils.NodegroupUtil;
import com.google.inject.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;

public class NodegroupsView extends ViewPart implements INodegroupView {

    /** The ID of the view as specified by the extension. */
    public static final String ID = "rackplugin.views.NodegroupsView";

    private static final String TEMPLATE_VIEW_ERROR = "ERROR showing nodegroup template view";
    private static final String UPDATE_NODEGROUP_LIST_ERROR = "Unable to update nodegroup list";
    private static final String NODEGROUP_DELETE_ERROR = "Unable to delete selected nodegroup %s";
    private static final String NODEGROUP_DELETE_SUCCESS = "Deleted selected nodegroup %s";

    private static final String VIEW_CSV_ACTION = "View CSV Ingestion Templates";
    private static final String QUERY_NODEGROUP_ACTION = "Query Nodegroup";
    private static final String DELETE_NODEGROUP_ACTION = "Delete Nodegroup(s)";

    private static final String SEARCH_NODEGROUPS_TEXT = "Search nodegroups";

    @Inject IWorkbench workbench;

    private Action viewCsvIngestionTemplatesAction;
    private Action queryNodegroupAction;
    private Action deleteNodegroupAction;

    private Table table;
    private Button selectAllButton;
    private ScrolledComposite topComposite;
    private Composite composite;
    private Composite floatContainer;

    public String getProjectPath() {
        return "";
    }

    public boolean isCore() {
        return true;
    }

    public boolean isUri() {
        return false;
    }

    @Override
    public void setFocus() {
        if (null != table) {
            table.setFocus();
        }
    }

    @Override
    public void createPartControl(Composite parent) {

        // Notes:
        // The table starts with an extra column. This appears to be a known WON'T FIX.
        // The extra column can be resized to zero width.
        // The code below sets each of the nested composite to fill out horizontally.
        // Then the table does also. This appears to be necessary when there is an encapsulating
        // ScrolledComposite.

        final Display display = Display.getCurrent();

        final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        sc.setLayout(new FillLayout());
        this.topComposite = sc;
        composite = new Composite(sc, SWT.NONE);
        sc.setExpandHorizontal(true);
        sc.setContent(composite);

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.verticalSpacing = 10;
        var gdata = new GridData();
        gdata.grabExcessHorizontalSpace = true;
        gdata.grabExcessVerticalSpace = true;
        gdata.horizontalAlignment = SWT.FILL;
        gdata.verticalAlignment = SWT.FILL;
        gdata.widthHint = SWT.FILL;
        composite.setLayout(layout);
        composite.setLayoutData(gdata);

        if (!ConnectionUtil.ping()) {
            return;
        }

        Composite searchBarComposite = new Composite(composite, SWT.NONE);
        GridLayout layout2 = new GridLayout();
        layout2.numColumns = 2;
        layout.horizontalSpacing = 10;
        searchBarComposite.setLayout(layout2);

        Label searchLabel = new Label(searchBarComposite, SWT.None);
        searchLabel.setText("Search");
        Text searchBar = new Text(searchBarComposite, SWT.None);
        searchBar.setText(SEARCH_NODEGROUPS_TEXT);

        searchBar.addListener(
                SWT.MouseDown,
                new Listener() {

                    @Override
                    public void handleEvent(Event event) {
                        if (searchBar.getText().equals(SEARCH_NODEGROUPS_TEXT)) {
                            searchBar.setText("");
                        }
                    }
                });

        searchBar.addListener(
                SWT.KeyUp,
                new Listener() {

                    @Override
                    public void handleEvent(Event event) {
                        String searchText = searchBar.getText();
                        filterNodegroups(searchText);
                    }
                });

        searchBar.addListener(
                SWT.FocusOut,
                new Listener() {

                    @Override
                    public void handleEvent(Event arg0) {
                        if (searchBar.getText().isEmpty()) {
                            searchBar.setText(SEARCH_NODEGROUPS_TEXT);
                            filterNodegroups("");
                        }
                    }
                });

        floatContainer = new Composite(composite, SWT.NONE);
        var floatLayout = new GridLayout(1, true);
        var floatData = new GridData();
        floatData.horizontalAlignment = SWT.FILL;
        floatData.grabExcessHorizontalSpace = true;
        floatContainer.setLayoutData(floatData);
        floatContainer.setLayout(floatLayout);

        table = new Table(floatContainer, SWT.MULTI | SWT.NO_SCROLL);
        table.removeAll();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setHeaderBackground(display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
        table.setHeaderForeground(display.getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
        table.setFocus();
        var tableData = new GridData();
        tableData.horizontalAlignment = SWT.FILL;
        // Setting FILL here allows the table to fill out the horizontal width of the view window.
        // But it also makes the ScrolledComposite think horizontal scrollbars are never needed
        // so we have to rely on the table's own scrollbars.
        tableData.grabExcessHorizontalSpace = true;
        table.setLayoutData(tableData);

        makeActions();
        hookContextMenu();

        Stream.of("Nodegroup ID", "Comments", "Creation Data", "Creator")
                .forEach(
                        header -> {
                            final TableColumn col = new TableColumn(table, SWT.LEFT);
                            col.setText(header);
                            table.showColumn(col);
                            col.addControlListener(
                                    new ControlAdapter() {
                                        @Override
                                        public void controlResized(ControlEvent e) {
                                            resetScrollbarSize();
                                        }
                                    });
                        });

        refreshNodegroupList();
    }

    private void resetScrollbarSize() {
        int minwidth = 20; // To account for margins
        for (int k = 0; k < table.getColumnCount(); k++) minwidth += table.getColumn(k).getWidth();

        topComposite.setMinSize(composite.computeSize(minwidth, SWT.DEFAULT));
    }

    private void refreshNodegroupList() {

        try {
            RefreshHandler.refreshNodegroups();
            NodegroupUtil.nodegroups.getResults().getRows().stream()
                    .sorted((row1, row2) -> row1.get(0).compareTo(row2.get(0)))
                    .forEach(
                            row -> {
                                final TableItem item = new TableItem(table, SWT.NONE);
                                row.remove(4); // Remove application data "semTK"
                                item.setData(row);
                                for (int j = 0; j < row.size(); j++) {
                                    item.setText(j, row.get(j));
                                }
                            });

            table.setEnabled(table.getItemCount() > 0);
            Arrays.stream(table.getColumns()).forEach(TableColumn::pack);

            table.pack();
            floatContainer.pack();
            composite.pack();
            resetScrollbarSize();
            topComposite.pack();

        } catch (final Exception e) {
            ErrorMessageUtil.warning(UPDATE_NODEGROUP_LIST_ERROR);
        }
    }

    private void filterNodegroups(String searchTerm) {

        try {
            table.removeAll();
            NodegroupUtil.nodegroups.getResults().getRows().stream()
                    .sorted((row1, row2) -> row1.get(0).compareTo(row2.get(0)))
                    .forEach(
                            row -> {
                                row.remove(4); // Remove application data "semTK"
                                boolean bMatch = false;
                                for (int j = 0; j < row.size(); j++) {
                                    if (row.get(j).contains(searchTerm)) {
                                        bMatch = true;
                                        break;
                                    }
                                }
                                if (bMatch || searchTerm.isEmpty()) {
                                    final TableItem item = new TableItem(table, SWT.NONE);
                                    item.setData(row);
                                    for (int j = 0; j < row.size(); j++) {
                                        item.setText(j, row.get(j));
                                    }
                                }
                            });

            table.setEnabled(table.getItemCount() > 0);
            // Arrays.stream(table.getColumns()).forEach(TableColumn::pack);
            table.pack();
            floatContainer.pack();
            composite.pack();
            resetScrollbarSize();
            // topComposite.pack(); // With this pack, the scrollbars disappear when the filter is
            // changed.
            // though they reappear if the view window is resized.

        } catch (final Exception e) {
            ErrorMessageUtil.warning(UPDATE_NODEGROUP_LIST_ERROR);
        }
    }

    private void hookContextMenu() {
        final MenuManager menuMgr = new MenuManager("#PopupMenu1");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(
                new IMenuListener() {
                    public void menuAboutToShow(IMenuManager manager) {
                        NodegroupsView.this.fillContextMenu(manager);
                    }
                });

        final Menu menu = menuMgr.createContextMenu(table);
        table.setMenu(menu);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(viewCsvIngestionTemplatesAction);
        manager.add(queryNodegroupAction);
        manager.add(deleteNodegroupAction);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void makeActions() {
        viewCsvIngestionTemplatesAction = new ViewCsvIngestionTemplatesAction();
        viewCsvIngestionTemplatesAction.setText(VIEW_CSV_ACTION);

        queryNodegroupAction = NodegroupActionFactory.getQueryNodegroupAction(this);

        queryNodegroupAction.setText(QUERY_NODEGROUP_ACTION);

        deleteNodegroupAction = new DeleteSelectedNodeGroupsAction();
        deleteNodegroupAction.setText(DELETE_NODEGROUP_ACTION);
    }

    public ArrayList<String> getSelectedNodegroups() {
        return Arrays.stream(table.getSelection())
                .map(ng -> (List<?>) ng.getData())
                .filter(d -> !d.isEmpty())
                .map(d -> (String) d.get(0))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private class DeleteSelectedNodeGroupsAction extends Action {

        @Override
        public void run() {

            int n = table.getSelectionCount();
            if (!MessageDialog.openConfirm(
                    null,
                    "Delete Nodegroups",
                    "Deleting " + n + " nodegroup" + (n == 1 ? "" : "s"))) {
                return;
            }

            try {
                getSelectedNodegroups().stream()
                        .map(ngid -> new DeleteSelectedNodegroupJob(ngid))
                        .forEach(
                                thr -> {
                                    try {
                                        thr.schedule();
                                        thr.join();
                                    } catch (final InterruptedException e) {
                                        ErrorMessageUtil.error(NODEGROUP_DELETE_ERROR, e);
                                    }
                                });

            } catch (final Exception e) {
                ErrorMessageUtil.error(NODEGROUP_DELETE_ERROR, e);
            }

            refreshNodegroupList();
        }
    }

    private class ViewCsvIngestionTemplatesAction extends Action {

        @Override
        public void run() {
            try {
                if (table.getSelectionCount() != 1) {
                    MessageDialog.openError(
                            null,
                            "RITE Error",
                            "View action is permitted only for exactly one selection");
                    return;
                }
                NodegroupTemplateView.selectedNodegroups = getSelectedNodegroups();
                final IWorkbenchWindow window =
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow();

                final IViewPart view = window.getActivePage().findView(NodegroupTemplateView.ID);

                window.getActivePage().hideView(view);
                window.getActivePage().showView(NodegroupTemplateView.ID);

            } catch (final Exception e) {
                ErrorMessageUtil.error(TEMPLATE_VIEW_ERROR, e);
            }
        }
    }

    private static class DeleteSelectedNodegroupJob extends Job {

        private static final String JOB_NAME = "Delete Nodegroup %s";
        private final String nodegroupId;

        public DeleteSelectedNodegroupJob(final String nodegroupId) {
            super(String.format(JOB_NAME, nodegroupId));
            this.nodegroupId = nodegroupId;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {
                ErrorMessageUtil.println("Deleting nodegroup: " + nodegroupId + " ... ");
                NodegroupUtil.client.deleteStoredNodeGroup(nodegroupId);
                ErrorMessageUtil.printOK();

            } catch (final Exception e) {
                ErrorMessageUtil.printFAIL();
                ErrorMessageUtil.error(String.format(NODEGROUP_DELETE_ERROR, nodegroupId), e);

                return Status.CANCEL_STATUS;
            }

            ErrorMessageUtil.println(String.format(NODEGROUP_DELETE_SUCCESS, nodegroupId));

            return Status.OK_STATUS;
        }
    }
}
