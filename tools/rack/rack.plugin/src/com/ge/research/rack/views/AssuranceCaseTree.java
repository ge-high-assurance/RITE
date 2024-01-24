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

import com.ge.research.rack.LoadAssuranceCaseHandler;
import com.ge.research.rack.utils.*;
import com.ge.research.rack.utils.TreeNode;
import com.google.inject.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
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
public class AssuranceCaseTree extends ViewPart {

    /** The ID of the view as specified by the extension. */
    public static final String ID = "rackplugin.views.AssuranceCaseTree";

    @Inject IWorkbench workbench;
    public static HashMap<TreeNode, ArrayList<TreeNode>> graph = null;
    public static HashMap<String, TreeNode> index = null;
    private TreeViewer viewer;
    private DrillDownAdapter drillDownAdapter;
    private Action action1;
    private Action action2;
    private Action doubleClickAction;

    class TreeObject implements IAdaptable {
        private String name;
        private TreeParent parent;

        public TreeObject(TreeNode node) {
            String label = node.getLabel();
            String name =
                    (label != null && !label.equals(""))
                            ? (node.getId() + ": " + label)
                            : (node.getId());
            this.name = name;
        }

        public String getName() {
            return name;
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
        private ArrayList siblings;
        private GSN gsnType;

        public TreeParent(TreeNode node) {
            super(node);
            children = new ArrayList();
            siblings = new ArrayList();
        }

        public void addChild(TreeObject child) {
            children.add(child);
            child.setParent(this);
        }

        public void addSibling(TreeObject sibling) {
            siblings.add(sibling);
        }

        public void removeSibling(TreeObject sibling) {
            siblings.remove(sibling);
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

        public void setGsnType(GSN type) {
            this.gsnType = type;
        }

        public GSN getGsnType() {
            return this.gsnType;
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

        /*
         * We will set up a dummy model to initialize tree heararchy. In a real code,
         * you will connect to a real model and expose its hierarchy.
         */
        private void initialize() {
            index = LoadAssuranceCaseHandler.index;
            invisibleRoot = new TreeParent(new TreeNode(""));

            Set<String> nodes = index.keySet();
            for (String node : nodes) {
                TreeNode treeNode = index.get(node);
                if (treeNode == null || (treeNode.getGsnType() == null)) {
                    continue;
                }
                if (treeNode.isTopLevelNode()) {
                    TreeParent parent = new TreeParent(treeNode);
                    parent.setGsnType(treeNode.getGsnType());
                    invisibleRoot.addChild(parent);
                    drawTree(parent, treeNode);
                }
            }
        }
    }

    public void drawTree(TreeParent parent, TreeNode node) {
        ArrayList<TreeNode> inContextOf = node.getContexts();
        ArrayList<TreeNode> supportedBy = node.getSupports();

        for (TreeNode contextNode : inContextOf) {
            TreeParent subTree = new TreeParent(contextNode);
            subTree.setGsnType(contextNode.getGsnType());
            parent.addChild(subTree);
        }

        for (TreeNode supportedByNode : supportedBy) {

            TreeParent subTree = new TreeParent(supportedByNode);
            subTree.setGsnType(supportedByNode.getGsnType());
            parent.addChild(subTree);
            drawTree(subTree, supportedByNode);
        }
    }

    class ViewLabelProvider extends LabelProvider {

        public String getText(Object obj) {
            return obj.toString();
        }

        public Image getImage(Object obj) {

            try {
                switch (((TreeParent) obj).getGsnType()) {
                    case GOAL:
                        return ViewUtils.getIcon("goal.png");
                    case STRATEGY:
                        return ViewUtils.getIcon("strategy.png");
                    case JUSTIFICATION:
                        return ViewUtils.getIcon("justification.png");
                    case SOLUTION:
                        return ViewUtils.getIcon("solution.png");
                    case CONTEXT:
                        return ViewUtils.getIcon("context.png");
                    case ASSUMPTION:
                        return ViewUtils.getIcon("justification.png");
                    case UNDEVGOAL:
                        return ViewUtils.getIcon("undevGoal.png");
                    case UNDEVSTRATEGY:
                        return ViewUtils.getIcon("undevStrategy.png");
                    default:
                        return ViewUtils.getIcon("context.png");
                }
            } catch (Exception e) {
                return null;
            }
        }
    }

    @Override
    public void createPartControl(Composite parent) {

        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        drillDownAdapter = new DrillDownAdapter(viewer);

        viewer.setContentProvider(new ViewContentProvider());
        viewer.setInput(getViewSite());
        viewer.setLabelProvider(new ViewLabelProvider());

        // Create the help context id for the viewer's control
        if (index == null) {
            return;
        }

        // workbench.getHelpSystem().setHelp(viewer.getControl(), "rack.plugin.viewer");
        getSite().setSelectionProvider(viewer);
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(
                new IMenuListener() {
                    public void menuAboutToShow(IMenuManager manager) {
                        AssuranceCaseTree.this.fillContextMenu(manager);
                    }
                });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(action1);
        manager.add(new Separator());
        manager.add(action2);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(action1);
        manager.add(action2);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(action1);
        manager.add(action2);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
    }

    private void makeActions() {
        action1 =
                new Action() {
                    public void run() {
                        showMessage("Action 1 executed");
                    }
                };
        action1.setText("Action 1");
        action1.setToolTipText("Action 1 tooltip");
        /*
         * action1.setImageDescriptor( PlatformUI.getWorkbench() .getSharedImages()
         * .getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
         */

        action2 =
                new Action() {
                    public void run() {
                        showMessage("Action 2 executed");
                    }
                };
        action2.setText("Action 2");
        action2.setToolTipText("Action 2 tooltip");
        // action2.setImageDescriptor(
        // workbench.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
        doubleClickAction =
                new Action() {
                    public void run() {
                        IStructuredSelection selection = viewer.getStructuredSelection();
                        Object obj = selection.getFirstElement();
                        showMessage("Double-click detected on " + obj.toString());
                    }
                };
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(
                new IDoubleClickListener() {
                    public void doubleClick(DoubleClickEvent event) {
                        doubleClickAction.run();
                    }
                });
    }

    private void showMessage(String message) {
        MessageDialog.openInformation(
                viewer.getControl().getShell(), "Assurance Case GSN", message);
    }

    @Override
    public void setFocus() {
        // viewer.getControl().setFocus();
    }
}
