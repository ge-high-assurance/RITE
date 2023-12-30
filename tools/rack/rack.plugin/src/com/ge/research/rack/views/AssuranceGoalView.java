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
import com.ge.research.rack.autoGsn.viewHandlers.AutoGsnUnifiedMainViewHandler;
import com.ge.research.rack.utils.*;
import com.ge.research.rack.utils.TreeNode;
import com.google.inject.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import java.awt.Frame;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;


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
public class AssuranceGoalView extends ViewPart {

    /** The ID of the view as specified by the extension. */
    public static final String ID = "rackplugin.views.AssuranceGoalView";

    @Inject IWorkbench workbench;
    public static HashMap<TreeNode, ArrayList<TreeNode>> graph = null;
    public static HashMap<String, TreeNode> index = null;
//    private TreeViewer viewer;
//    private DrillDownAdapter drillDownAdapter;
//    private Action action1;
//    private Action action2;
//    private Action doubleClickAction;
    
//    private AutoGsnUnifiedMainViewHandler obj;

//    class TreeObject implements IAdaptable {
//        private String name;
//        private TreeParent parent;
//
//        public TreeObject(TreeNode node) {
//            String label = node.getLabel();
//            String name =
//                    (label != null && !label.equals(""))
//                            ? (node.getId() + ": " + label)
//                            : (node.getId());
//            this.name = name;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setParent(TreeParent parent) {
//            this.parent = parent;
//        }
//
//        public TreeParent getParent() {
//            return parent;
//        }
//
//        @Override
//        public String toString() {
//            return getName();
//        }
//
//        @Override
//        public <T> T getAdapter(Class<T> key) {
//            return null;
//        }
//    }
//
//    class TreeParent extends TreeObject {
//        private ArrayList<TreeObject> children;
//        private ArrayList<TreeObject> siblings;
//        private GSN gsnType;
//
//        public TreeParent(TreeNode node) {
//            super(node);
//            children = new ArrayList<>();
//            siblings = new ArrayList<>();
//        }
//
//        public void addChild(TreeObject child) {
//            children.add(child);
//            child.setParent(this);
//        }
//
//        public void addSibling(TreeObject sibling) {
//            siblings.add(sibling);
//        }
//
//        public void removeSibling(TreeObject sibling) {
//            siblings.remove(sibling);
//        }
//
//        public void removeChild(TreeObject child) {
//            children.remove(child);
//            child.setParent(null);
//        }
//
//        public TreeObject[] getChildren() {
//            return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
//        }
//
//        public boolean hasChildren() {
//            return children.size() > 0;
//        }
//
//        public void setGsnType(GSN type) {
//            this.gsnType = type;
//        }
//
//        public GSN getGsnType() {
//            return this.gsnType;
//        }
//    }
//
//    class ViewContentProvider implements ITreeContentProvider {
//        private TreeParent invisibleRoot;
//
//        public Object[] getElements(Object parent) {
//            if (parent.equals(getViewSite())) {
//                if (invisibleRoot == null) initialize();
//                return getChildren(invisibleRoot);
//            }
//            return getChildren(parent);
//        }
//
//        public Object getParent(Object child) {
//            if (child instanceof TreeObject) {
//                return ((TreeObject) child).getParent();
//            }
//            return null;
//        }
//
//        public Object[] getChildren(Object parent) {
//            if (parent instanceof TreeParent) {
//                return ((TreeParent) parent).getChildren();
//            }
//            return new Object[0];
//        }
//
//        public boolean hasChildren(Object parent) {
//            if (parent instanceof TreeParent) return ((TreeParent) parent).hasChildren();
//            return false;
//        }
//
//        /*
//         * We will set up a dummy model to initialize tree heararchy. In a real code,
//         * you will connect to a real model and expose its hierarchy.
//         */
//        private void initialize() {
//            index = LoadAssuranceCaseHandler.index;
//            invisibleRoot = new TreeParent(new TreeNode(""));
//
//            Set<String> nodes = index.keySet();
//            for (String node : nodes) {
//                TreeNode treeNode = index.get(node);
//                if (treeNode == null || (treeNode.getGsnType() == null)) {
//                    continue;
//                }
//                if (treeNode.isTopLevelNode()) {
//                    TreeParent parent = new TreeParent(treeNode);
//                    parent.setGsnType(treeNode.getGsnType());
//                    invisibleRoot.addChild(parent);
//                    drawTree(parent, treeNode);
//                }
//            }
//        }
//    }
//
//    public void drawTree(TreeParent parent, TreeNode node) {
//        ArrayList<TreeNode> inContextOf = node.getContexts();
//        ArrayList<TreeNode> supportedBy = node.getSupports();
//
//        for (TreeNode contextNode : inContextOf) {
//            TreeParent subTree = new TreeParent(contextNode);
//            subTree.setGsnType(contextNode.getGsnType());
//            parent.addChild(subTree);
//        }
//
//        for (TreeNode supportedByNode : supportedBy) {
//
//            TreeParent subTree = new TreeParent(supportedByNode);
//            subTree.setGsnType(supportedByNode.getGsnType());
//            parent.addChild(subTree);
//            drawTree(subTree, supportedByNode);
//        }
//    }
//
//    class ViewLabelProvider extends LabelProvider {
//
//        public String getText(Object obj) {
//            return obj.toString();
//        }
//
//        public Image getImage(Object obj) {
//
//            try {
//                switch (((TreeParent) obj).getGsnType()) {
//                    case GOAL:
//                        return ViewUtils.getIcon("goal.png");
//                    case STRATEGY:
//                        return ViewUtils.getIcon("strategy.png");
//                    case JUSTIFICATION:
//                        return ViewUtils.getIcon("justification.png");
//                    case SOLUTION:
//                        return ViewUtils.getIcon("solution.png");
//                    case CONTEXT:
//                        return ViewUtils.getIcon("context.png");
//                    case ASSUMPTION:
//                        return ViewUtils.getIcon("justification.png");
//                    case UNDEVGOAL:
//                        return ViewUtils.getIcon("undevGoal.png");
//                    case UNDEVSTRATEGY:
//                        return ViewUtils.getIcon("undevStrategy.png");
//                    default:
//                        return ViewUtils.getIcon("context.png");
//                }
//            } catch (Exception e) {
//                return null;
//            }
//        }
//    }

    @Override
    public void createPartControl(Composite parent) {

//    	obj = new AutoGsnUnifiedMainViewHandler();
//    	obj.initialize();
    	
        Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
        final Frame frame = SWT_AWT.new_Frame(composite);
        
        final JScrollPane parentPanel = new JScrollPane();
        parentPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        parentPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        var vp = new JViewport();
        final JFXPanel fxPanel = new JFXPanel();

        Platform.runLater(
            new Runnable() {

            	@Override
            	public void run() {
            		try {

            			Bundle bundle =
            					org.eclipse.core.runtime.Platform.getBundle("rack.plugin");
            			URL fxmlUrl =
            					FileLocator.find(
            							bundle,
            							new Path(
            									"resources/fxml/autoGsn/AutoGsnUnifiedDrillGoalView2.fxml"
            									//"resources/demo.fxml"
            									),
            							null);
            			fxmlUrl = FileLocator.toFileURL(fxmlUrl);

            			// Creating an FXMLLoader object that can be returned for use where needed
            			FXMLLoader loader = new FXMLLoader(fxmlUrl);

            			System.out.println(
            					"Time before loading fxml:" + System.currentTimeMillis());
            			Parent root = loader.load();
            			System.out.println(
            					"Time after loading fxml:" + System.currentTimeMillis());
            			// stage.setTitle("Automatic GSN Inference");
            			System.out.println(
            					"Time before creating new scene:" + System.currentTimeMillis());

            			Scene scene = new Scene(root);
            			fxPanel.setScene(scene);

            			vp.add(fxPanel);
            			parentPanel.setViewport(vp);

            			frame.add(parentPanel);
            			frame.setSize(1300, 600);
            			frame.setVisible(true);
            		} catch (Exception ex) {
            			ErrorMessageUtil.error("createPartControl-exception " + ex);

            			ex.printStackTrace();
            		}
            	}
            }
        );
    }


//    private void showMessage(String message) {
//        MessageDialog.openInformation(
//                viewer.getControl().getShell(), "Assurance Case GSN", message);
//    }

    @Override
    public void setFocus() {
        // viewer.getControl().setFocus();
    }
    
    @FXML
    private void btnUpAction(ActionEvent event) throws Exception {
    	ErrorMessageUtil.print("btnUpAction");
    }

    @FXML
    private void btnRootAction(ActionEvent event) throws Exception {
    	ErrorMessageUtil.print("btnRootAction");
    }

    @FXML
    private void btnHomeAction(ActionEvent event) throws Exception {
    	ErrorMessageUtil.print("btnHomeAction");
    }

    @FXML
    private void btnTreePopAction(ActionEvent event) throws Exception {
    	ErrorMessageUtil.print("btnTreePopAction");
    }

    @FXML
    private void comboPassFailAction(ActionEvent event) throws Exception {
    	ErrorMessageUtil.print("comboPassFailAction");
    }


    @FXML
    private void listSubGoalsSelectAction(MouseEvent event) {
    	ErrorMessageUtil.print("listSubGoalsSelectAction");
    }}
