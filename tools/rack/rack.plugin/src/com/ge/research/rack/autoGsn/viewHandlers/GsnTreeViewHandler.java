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
package com.ge.research.rack.autoGsn.viewHandlers;

import com.ge.research.rack.autoGsn.structures.GsnNode;
import com.ge.research.rack.autoGsn.structures.MultiClassPackets;
import com.ge.research.rack.autoGsn.structures.MultiClassPackets.TreeItemAndBoolean;
import com.ge.research.rack.autoGsn.utils.AutoGsnGuiUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 * @author Saswata Paul
 */
public class GsnTreeViewHandler {

    // --- Local variables to store data
    GsnNode rootGsn;

    String currentGoalId;

    // -------- FXML GUI variables below --------------

    @FXML private TreeView treeViewGsnCascade;

    // --------------------------------

    /**
     * Creates the text label for a node and highlights the background to focus if it is the
     * currentGoal
     *
     * @param node
     * @return
     */
    private Label getNodeTreeLabel(GsnNode node) {
        Label nodeLabel = new Label();

        String nodeType = node.getNodeType().toString().toUpperCase();
        Color nodeColor = AutoGsnGuiUtils.getNodeColor(node);

        nodeLabel.setText(nodeType + ": " + node.getNodeId());
        nodeLabel.setTextFill(nodeColor);

        if (node.getNodeId().equalsIgnoreCase(currentGoalId)) {
            nodeLabel.setBackground(
                    new Background(
                            new BackgroundFill(Color.YELLOW, new CornerRadii(1.0), new Insets(0))));
        }

        return nodeLabel;
    }

    /**
     * For a given GSN node, creates a treeitem and recursively creates treeitems for all supporting
     * nodes
     *
     * @param node
     * @return
     */
    private TreeItem populateTreeViewGsnCascade(GsnNode node, String currentGoalId) {
        Node nodeImage = AutoGsnGuiUtils.getNodeImage(node);

        TreeItem elementItem = new TreeItem(getNodeTreeLabel(node), nodeImage);
        // if this is the currentgoal, expand it by default
        System.out.println(node.getNodeId() + " ------- " + currentGoalId);
        if (node.getNodeId().equalsIgnoreCase(currentGoalId)) {
            System.out.println("expanded");
            elementItem.setExpanded(true);
        }

        if (node.getSupportedBy() != null) {
            for (GsnNode child : node.getSupportedBy()) {
                elementItem.getChildren().add(populateTreeViewGsnCascade(child, currentGoalId));
            }
        }
        return elementItem;
    }

    /**
     * For a given GSN node, creates a treeitem and recursively creates treeitems for all supporting
     * nodes
     *
     * <p>This also decides if a node should be expanded or not
     *
     * @param node
     * @return
     */
    private TreeItemAndBoolean populateTreeViewGsnCascadeWithExpansion(
            GsnNode node, String currentGoalId, Boolean expandFlag) {
        Node nodeImage = AutoGsnGuiUtils.getNodeImage(node);

        TreeItem elementItem = new TreeItem(getNodeTreeLabel(node), nodeImage);

        Boolean myExpFlag = false;

        // if this is the currentgoal, expand it by default
        //		System.out.println(node.getNodeId() + " ------- " + currentGoalId);
        if (node.getNodeId().equalsIgnoreCase(currentGoalId)) {
            System.out.println("expanded");
            elementItem.setExpanded(true);
            myExpFlag = true;
        }

        if (node.getSupportedBy() != null) {

            // flag to check if any child was expanded
            Boolean someChildExpanded = false;
            for (GsnNode child : node.getSupportedBy()) {

                TreeItemAndBoolean childReturned =
                        populateTreeViewGsnCascadeWithExpansion(child, currentGoalId, false);

                // Add the child treeitem
                elementItem.getChildren().add(childReturned.getTreeItem());

                // check if the child returned true and set flag
                {
                    if (childReturned.getExpandFlag()) {
                        someChildExpanded = true;
                        System.out.println(node.getNodeId() + " somechild expanded");
                    }
                }
            }

            // check if some child returned true and set myExpflag
            {
                if (someChildExpanded) {
                    myExpFlag = true;
                    elementItem.setExpanded(true);
                    System.out.println(node.getNodeId() + " expanded");
                }
            }
        }

        // create TreeItemAndBoolean to return to parent
        MultiClassPackets.TreeItemAndBoolean returnPack =
                new MultiClassPackets().new TreeItemAndBoolean(elementItem, myExpFlag);

        return returnPack;
    }

    /**
     * Used to initialize the variables from the caller view's fxml controller
     *
     * <p>and create the treeview
     *
     * @param gsn
     * @param goalId
     */
    public void prepareTreeView(GsnNode gsn, String goalId) {
        rootGsn = gsn;
        currentGoalId = goalId;

        // get the GSN tree
        // TreeItem tree = populateTreeViewGsnCascade(rootGsn, currentGoalId);
        TreeItemAndBoolean treeItemWithFlag =
                populateTreeViewGsnCascadeWithExpansion(rootGsn, currentGoalId, false);

        // Assign the tree to the treeview
        treeViewGsnCascade.setRoot(treeItemWithFlag.getTreeItem());
    }

    /**
     * Used to directly initialize a readymade treeview if required from the caller view's fxml
     * controller
     *
     * @param readyTree
     */
    public void prepareTreeView(TreeView readyTree) {
        // Assign the tree to the treeview
        treeViewGsnCascade.setRoot(readyTree.getRoot());
    }

    @FXML
    private void initialize() {}
}
