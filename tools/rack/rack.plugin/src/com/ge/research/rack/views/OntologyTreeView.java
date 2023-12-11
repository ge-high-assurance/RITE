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

import com.ge.research.rack.BuildIngestionNodegroupsHandler;
import com.ge.research.rack.utils.*;
import com.ge.research.semtk.resultSet.TableResultSet;
import com.google.inject.*;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;

import java.io.InputStream;
import java.util.*;
import java.util.ArrayList;

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
public class OntologyTreeView extends ViewPart implements INodegroupView {

    /** The ID of the view as specified by the extension. */
    public static final String ID = "rackplugin.views.TreeView";

    @Inject IWorkbench workbench;

    private TreeViewer viewer;
    private DrillDownAdapter drillDownAdapter;
    private Action action1;
    private static Image ontClassImg = null;
    private static Image ontFieldImg = null;
    private static String selectedUri = "";
    private static TreeParent selectedNode = null;
    private static String selectedName = "";

    class TreeObject implements IAdaptable {
        private String name;
        private TreeParent parent;
        private String tooltip = "";

        public TreeObject(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setTooltip(String tooltip) {
            this.tooltip = tooltip;
        }

        public String getTooltip() {
            return tooltip;
        }

        public void setParent(TreeParent parent) {
            this.parent = parent;
        }

        public TreeParent getParent() {
            return parent;
        }

        @Override
        public String toString() {
            return getName();
        }

        @Override
        public <T> T getAdapter(Class<T> key) {
            return null;
        }
    }

    class TreeParent extends TreeObject {
        private ArrayList children;
        public String uri = "";
        public String type = "";
        public String comment = "";
        public HashMap<String, String> propertyAnnotations = new HashMap<>();

        public TreeParent(String name) {
            super(name);
            children = new ArrayList();
        }

        public void addChild(TreeObject child) {
            children.add(child);
            child.setParent(this);
        }

        public void removeChild(TreeObject child) {

            children.remove(child);
            child.setParent(null);
        }

        public TreeObject[] getChildren() {
            return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
        }

        public boolean hasChildren() {
            return children.size() > 0;
        }
    }

    class ViewContentProvider implements ITreeContentProvider {
        private TreeParent invisibleRoot;

        public Object[] getElements(Object parent) {
            if (parent.equals(getViewSite())) {
                if (invisibleRoot == null) initialize();
                return getChildren(invisibleRoot);
            }
            return getChildren(parent);
        }

        public Object getParent(Object child) {
            if (child instanceof TreeObject) {
                return ((TreeObject) child).getParent();
            }
            return null;
        }

        public Object[] getChildren(Object parent) {
            if (parent instanceof TreeParent) {
                return ((TreeParent) parent).getChildren();
            }
            return new Object[0];
        }

        public boolean hasChildren(Object parent) {
            if (parent instanceof TreeParent) return ((TreeParent) parent).hasChildren();
            return false;
        }

        private void initialize() {

            try {
                BuildIngestionNodegroupsHandler.bookkeepNodegroups();
                OntologyUtil.computeTree();
                Display display = Display.getCurrent();
                InputStream ontClassStream =
                        this.getClass().getResourceAsStream("/icons/ontclass.png");
                InputStream ontFieldStream =
                        this.getClass().getResourceAsStream("/icons/ontfield.png");

                ontClassImg = new Image(display, ontClassStream);
                ontFieldImg = new Image(display, ontFieldStream);
            } catch (Exception e) {
                e.printStackTrace();
            }

            invisibleRoot = new TreeParent("");

            ArrayList<MyTree> topLevelChildren = OntologyUtil.root.children;

            // top level core (uppercase) should be drawn before overlay (lowercase) ontologies
            topLevelChildren.stream()
                    .filter(c -> null != c && null != c.name)
                    .sorted(
                            (n1, n2) -> {
                                final boolean n1Core = n1.name.toUpperCase().equals(n1.name);
                                final boolean n2Core = n2.name.toUpperCase().equals(n2.name);
                                if (n1Core && n2Core || (!n1Core && !n2Core)) {
                                    return n1.name.compareTo(n2.name);
                                }
                                return n1Core ? -1 : 1;
                            })
                    .forEach(
                            child -> {
                                drawTree(invisibleRoot, child);
                            });
        }
    }

    public void drawTree(TreeParent parent, MyTree root) {

        TreeParent child = new TreeParent(root.name);
        child.uri = root.uri;
        child.type = root.type;
        child.comment = root.comment;
        parent.addChild(child);

        Set<String> keys = root.properties.keySet();
        for (String key : keys) {
            String label = key + " : " + root.properties.get(key);
            TreeObject leaf = new TreeObject(label);
            leaf.setTooltip(MyTree.propertyAnnotations.get(key));
            child.addChild(leaf);
        }
        ArrayList<MyTree> children = root.children;

        children.stream()
                .filter(o -> Core.rackOntology.contains(o.uri))
                .forEach(
                        myTreeChild -> {
                            drawTree(child, myTreeChild);
                        });

        children.stream()
                .filter(o -> !Core.rackOntology.contains(o.uri))
                .forEach(
                        myTreeChild -> {
                            drawTree(child, myTreeChild);
                        });
    }

    class ViewLabelProvider extends LabelProvider {

        public String getText(Object obj) {
            return obj.toString();
        }

        public Image getImage(Object obj) {
            if (obj instanceof TreeParent) {
                return ontClassImg;
            } else if (obj instanceof TreeObject) {
                return ontFieldImg;
            }
            return null;
        }
    }

    @Override
    public void createPartControl(Composite parent) {

        FillLayout fillLayout = new FillLayout();
        parent.setLayout(fillLayout);

        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        drillDownAdapter = new DrillDownAdapter(viewer);

        viewer.setContentProvider(new ViewContentProvider());
        viewer.setInput(getViewSite());
        viewer.setLabelProvider(new ViewLabelProvider());

        renderNumTriples(parent);

        Listener hover =
                new Listener() {

                    @Override
                    public void handleEvent(Event event) {

                        Item item = viewer.getTree().getItem(new Point(event.x, event.y));
                        if (item == null) {
                            return;
                        }
                        Object itemData = item.getData();
                        if (itemData instanceof TreeParent) {
                            String comment = ((TreeParent) itemData).comment;
                            if (!comment.equals("")) {
                                viewer.getTree().setToolTipText(comment);
                                return;
                            }
                        }

                        if (itemData instanceof TreeObject) {
                            viewer.getTree().setToolTipText(((TreeObject) itemData).getTooltip());
                        } else {
                            viewer.getTree().setToolTipText("");
                        }
                    }
                };

        Listener contextMenu =
                new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        Item item = viewer.getTree().getItem(new Point(event.x, event.y));
                        if (item == null) {
                            return;
                        }
                        Object itemData = item.getData();
                        if (!(itemData instanceof TreeParent)) {
                            return;
                        }
                        if (((TreeParent) itemData).type.equals("inner")
                                && ((TreeParent) itemData).hasChildren()) {
                            selectedUri = ((TreeParent) itemData).uri;
                            selectedName = ((TreeParent) itemData).getName();
                        }
                        selectedNode = (TreeParent) itemData;
                    }
                };

        Listener dispose =
                new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        OntologyTreeView.ontClassImg.dispose();
                        OntologyTreeView.ontFieldImg.dispose();
                    }
                };

        viewer.getControl().addListener(SWT.MouseHover, hover);
        viewer.getControl().addListener(SWT.MouseUp, contextMenu);
        viewer.getControl().addListener(SWT.Dispose, dispose);

        if (!ConnectionUtil.ping()) {
            return;
        }

        // traverse tree
        ArrayList<Control> queue = new ArrayList<>();
        queue.add(viewer.getControl());

        makeActions();
        hookContextMenu();
    }

    private boolean hasTriplesCount(TableResultSet graphInfo) {
        try {
            int size = graphInfo.getResults().getNumRows();
            for (int i = 0; i < size; i++) {
                if (graphInfo.getResults().getRow(i).size() > 1) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private void renderNumTriples(Composite parent) {

        try {
            TableResultSet graphInfo = ConnectionUtil.getGraphInfo();
            if (!hasTriplesCount(graphInfo)) {
                return;
            }
            Composite scComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
            Composite mainComposite = new Composite(scComposite, SWT.NONE);
            mainComposite.setLayout(new FillLayout());
            Table table = new Table(mainComposite, SWT.NONE);
            table.setHeaderVisible(true);
            table.setLinesVisible(false);
            table.setFocus();
            TableColumn dataGraphHeader = new TableColumn(table, SWT.CENTER);
            dataGraphHeader.setText("Data graph");
            TableColumn numTriples = new TableColumn(table, SWT.CENTER);
            numTriples.setText("# Triples");
            int numRows = graphInfo.getTable().getNumRows();
            for (int i = 0; i < numRows; i++) {
                TableItem item = new TableItem(table, SWT.CENTER);
                ArrayList<String> entry = graphInfo.getResults().getRow(i);
                int j = 0;
                for (String cell : entry) {
                    item.setText(j++, cell);
                }
            }

            parent.addDisposeListener(
                    new DisposeListener() {

                        @Override
                        public void widgetDisposed(DisposeEvent e) {
                            parent.dispose();
                        }
                    });
            numTriples.pack();
            dataGraphHeader.pack();
            table.pack();
            mainComposite.pack();
            scComposite.pack();
        } catch (Exception e) {
            RackConsole.getConsole().error("Unable to fetch data graphs on RACK");
        }
    }

    private void makeActions() {
        action1 = NodegroupActionFactory.getCreateInstanceDataAction(this);
        action1.setText("Create Instance Data");
        action1.setToolTipText("Create Instance Data in CSV format");
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(
                new IMenuListener() {
                    public void menuAboutToShow(IMenuManager manager) {
                        if (selectedNode != null && selectedNode.type.equals("toplevel")) {
                            return;
                        }
                        OntologyTreeView.this.fillContextMenu(manager);
                    }
                });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(action1);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    @Override
    public void setFocus() {
        if (viewer != null) {
            viewer.getControl().setFocus();
        }
    }

    @Override
    public ArrayList<String> getSelectedNodegroups() {
        // TODO Auto-generated method stub
        return new ArrayList<String>(
                Arrays.asList(IngestionTemplateUtil.getIngestionNodegroupId(selectedUri)));
    }

    public static String getSelectedClassUri() {
        return selectedUri;
    }

    public boolean isUri() {
        return true;
    }
}
