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

import com.ge.research.rack.JavaFXAppLaunchManager;
import com.ge.research.rack.autoGsn.constants.GsnCoreElements;
import com.ge.research.rack.autoGsn.structures.GsnNode;
import com.ge.research.rack.autoGsn.structures.MultiClassPackets;
import com.ge.research.rack.autoGsn.structures.MultiClassPackets.TreeItemAndBoolean;
import com.ge.research.rack.autoGsn.utils.AutoGsnGuiUtils;
import com.ge.research.rack.autoGsn.utils.GsnNodeUtils;
import com.ge.research.rack.autoGsn.viewManagers.AutoGsnViewsManager;
import com.ge.research.rack.autoGsn.viewManagers.GsnTreeViewManager;
import com.ge.research.rack.report.utils.ReportViewUtils;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * @author Saswata Paul
 */
public class AutoGsnUnifiedDrillGoalViewHandler {

    // -------- Local variables to store data

    // All relevant GSN for this traverse instance
    private List<GsnNode> allRelevantGsn;

    // Current level
    private int currentGoalLevel = 0;

    // Current goalId
    private String currentGoalId;

    // previous goal
    private String previousGoalId;

    // list of subgoals/solutions of current goal
    private List<GsnNode> currentSubGoals;

    // Id of the Main Top-level Gsn goal to be navigated in the view
    private String rootNodeId;

    // Main top-level GsnNode
    private GsnNode rootGsn;

    // -------- FXML GUI variables below --------------

    @FXML private Button btnHome;
    @FXML private Button btnUp;
    @FXML private Button btnDown;
    @FXML private Button btnRoot;
    @FXML private Button btnGsnTree;
    @FXML private Button btnTreePop;

    @FXML private Label labelGoalText;
    @FXML private Label labelStratText;
    @FXML private Label labelGoalStatus;
    @FXML private Label labelSubElementsHeading;

    @FXML private BarChart chartGoalStatus;
    @FXML private NumberAxis chartGoalStatusNumberAxis;

    @FXML private ListView<Label> listSubGoals;

    @FXML private ComboBox comboPassFail;

    @FXML private TreeView treeGsn;

    // --------------------------------

    /**
     * Creates the text label for a node and highlights the background to focus it if it is the
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
        nodeLabel.setFont(new Font(8)); // font size will be increased in popup view

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

    public void populateTree() {
        //    	rootGsn = gsn;
        //    	currentGoalId = goalId;

        // get the GSN tree
        // TreeItem tree = populateTreeViewGsnCascade(rootGsn, currentGoalId);
        TreeItemAndBoolean treeItemWithFlag =
                populateTreeViewGsnCascadeWithExpansion(rootGsn, currentGoalId, false);

        // Assign the tree to the treeview
        treeGsn.setRoot(treeItemWithFlag.getTreeItem());
    }

    /**
     * This is a very crude function to set previousGaolId
     *
     * <p>TODO: BAD CODE. CHANGE LATER
     *
     * @param node
     * @param childId
     */
    private void findParentId(GsnNode node, String childId) {
        if (node.getSupportedBy() != null) {
            Boolean found = false;
            for (int i = 0; i < node.getSupportedBy().size(); i++) {
                GsnNode support = node.getSupportedBy().get(i);
                if (support.getNodeId().equalsIgnoreCase(childId)) {
                    found = true;
                    previousGoalId = node.getNodeId();
                    break;
                }
            }
            if (!found) {
                for (int i = 0; i < node.getSupportedBy().size(); i++) {
                    GsnNode support = node.getSupportedBy().get(i);
                    findParentId(support, childId);
                }
            }
        }
    }

    /**
     * Callse findParent twice to find grand parent
     *
     * <p>TODO: BAD CODE. CHANGE LATER
     */
    private void decidePreviousGoalId() {
        // find and assign parent strategy to previousGoalId
        findParentId(rootGsn, currentGoalId);

        System.out.println("1 " + previousGoalId);

        // find and assign grandparent goal to previousGoalId
        findParentId(rootGsn, previousGoalId);

        System.out.println("2 " + previousGoalId);
    }

    /** Show/hide previous goal button */
    private void showOrHidePreviousBtn() {
        if (!currentGoalId.equalsIgnoreCase(rootNodeId)) {
            btnUp.setDisable(false);
        } else {
            btnUp.setDisable(true);
        }
    }

    /** populates the barchart with goal status */
    private void populateStatusChart(int numPassed, int numFailed) {
        // clear the chart
        chartGoalStatus.getData().clear();

        // populate the status chart

        XYChart.Series dataSeries1 = new XYChart.Series();

        Data passBar = new XYChart.Data("Passed", numPassed);
        Data failBar = new XYChart.Data("Failed", numFailed);

        dataSeries1.getData().add(passBar);
        dataSeries1.getData().add(failBar);

        chartGoalStatus.getData().add(dataSeries1);

        //        passBar.getNode().setStyle("-fx-bar-fill: #599C7E;");
        //        failBar.getNode().setStyle("-fx-bar-fill: #E06666;");

        passBar.getNode().setStyle("-fx-bar-fill: green;");
        ReportViewUtils.assignTooltip(passBar.getNode(), passBar.getYValue().toString());
        failBar.getNode().setStyle("-fx-bar-fill: red;");
        ReportViewUtils.assignTooltip(failBar.getNode(), failBar.getYValue().toString());

        // *** This can help make integral ticks on Y axis ***
        chartGoalStatusNumberAxis.setLowerBound(0);
        chartGoalStatusNumberAxis.setUpperBound((Math.max(numPassed, numFailed)) + 1);
        chartGoalStatusNumberAxis.setTickUnit(1);
    }

    /**
     * Populates the list of subgoals depending on key
     *
     * @param key
     */
    private void populateSubGoals(String key) {
        // clear the list of subgoals
        listSubGoals.getItems().clear();

        if ((currentSubGoals != null) && (currentSubGoals.size() > 0)) {
            for (GsnNode subGoalNode : currentSubGoals) {
                Label subGoalLabel = new Label();

                if (key.equalsIgnoreCase("passed") && subGoalNode.getIsGreen()) {
                    subGoalLabel.setText(subGoalNode.getNodeId());
                    subGoalLabel.setTextFill(AutoGsnGuiUtils.getNodeColor(subGoalNode));
                    listSubGoals.getItems().add(subGoalLabel);
                } else if (key.equalsIgnoreCase("failed") && !subGoalNode.getIsGreen()) {
                    subGoalLabel.setText(subGoalNode.getNodeId());
                    subGoalLabel.setTextFill(AutoGsnGuiUtils.getNodeColor(subGoalNode));
                    listSubGoals.getItems().add(subGoalLabel);
                } else if (key.equalsIgnoreCase("all")) {
                    subGoalLabel.setText(subGoalNode.getNodeId());
                    subGoalLabel.setTextFill(AutoGsnGuiUtils.getNodeColor(subGoalNode));
                    listSubGoals.getItems().add(subGoalLabel);
                }
            }
        }
    }

    /**
     * Takes a Goal id, populates the view for that goal, and updates currentGoalID, previousGoalId
     *
     * @param goalId
     */
    private void populateFields(String goalId) {

        // only goalIds starting with "G-" will warrant action
        if (goalId.startsWith("G-")) {
            // Find the goal Node
            GsnNode goalNode = GsnNodeUtils.getSubGsnNode(rootGsn, goalId);

            // the "goalNode" returned in the leaf solution case may be null
            // instead of a goal. Therefore, to ensure that the correct action is
            // taken, we must check once if a goal node was actually returned
            if (goalNode != null) {
                // update previousgoalId if the level change was downstream (level x to x+1)
                System.out.println("gl" + goalNode.getNodeLevel() + " cl " + currentGoalLevel);
                if (goalNode.getNodeLevel() > currentGoalLevel) {
                    System.out.println("updated previous");
                    previousGoalId = currentGoalId;
                }

                // update currentLevel
                currentGoalLevel = goalNode.getNodeLevel();

                // updating currentGoalId
                currentGoalId = goalId;

                // geting the strategy node for the goal
                GsnNode stratNode =
                        goalNode.getSupportedBy()
                                .get(0); // If the goalNode exists, then this MSUT have been created

                // Populate Goal label, set color
                labelGoalText.setText(
                        "ID: "
                                + goalNode.getNodeId()
                                + "\nCLAIM: "
                                + goalNode.getGoal().getDescription().replace("Claim: ", ""));
                labelGoalText.setTextFill(AutoGsnGuiUtils.getNodeColor(goalNode));

                // Populate Strategy label, set color
                labelStratText.setText(
                        "ID: "
                                + stratNode.getNodeId()
                                + "\nARGUMENT: "
                                + stratNode
                                        .getStrategy()
                                        .getDescription()
                                        .replace("Argument: ", ""));
                labelStratText.setTextFill(AutoGsnGuiUtils.getNodeColor(stratNode));

                // Clear list of currentSubGoals
                currentSubGoals = new ArrayList<GsnNode>();

                // For calculating goal status
                int numPassed = 0, numFailed = 0;

                // Populate new subGoals
                if ((stratNode.getSupportedBy() != null)
                        && (stratNode.getSupportedBy().size() > 0)) {
                    for (GsnNode subGoalNode : stratNode.getSupportedBy()) {
                        if (subGoalNode.getIsGreen()) {
                            numPassed = numPassed + 1;
                        } else {
                            numFailed = numFailed + 1;
                        }

                        // set the heading
                        if (subGoalNode.getNodeType() == GsnCoreElements.Class.Goal) {
                            // if even one subgoal is a goal, change the text of header
                            labelSubElementsHeading.setText("Sub Goals of the Goal in Focus");
                        } else {
                            // if even one subgoal is a solution, change the text of header
                            labelSubElementsHeading.setText("Solutions for the Goal in Focus");
                        }

                        // add to list of currentSubGoals
                        currentSubGoals.add(subGoalNode);
                    }

                    // call the function to populate the list with all subgoals
                    populateSubGoals("All");
                }

                populateStatusChart(numPassed, numFailed);

                populateTree();

                // decide previous goal button
                showOrHidePreviousBtn();

            } else {
                // TODO
                // Currently, if the selection in listview does not do anything,
                // then the item is highlighted in blue, meking it illegible
                // Could not get rid of this in a principled manner, so
                // for now, calling populateGoals on the currentGoalId without updating
                populateFields(currentGoalId);
            }
        }
    }

    /**
     * Used to initialize the variables from the caller view's fxml controller
     *
     * @param initialLevel
     * @param gsn
     */
    public void prepareView(int initialLevel, String goalId, GsnNode mainGsn) {
        currentGoalLevel = initialLevel;
        rootNodeId = goalId;
        currentGoalId = goalId;

        // get and store the root gsn
        rootGsn = mainGsn;

        populateFields(rootNodeId); // send the id of the topmost goal node

        showOrHidePreviousBtn();
    }

    // Will not add values to anything in this initialize
    // Just common properties
    // This is called automatically when loading the fxml
    @FXML
    private void initialize() {
        // set SINGLE Selection Model for ListView of Goals
        listSubGoals.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // turn on text wrapping
        labelGoalText.setWrapText(true);
        labelStratText.setWrapText(true);

        // add the categories to the combo
        comboPassFail.getItems().add("All");
        comboPassFail.getItems().add("Passed");
        comboPassFail.getItems().add("Failed");
    }

    @FXML
    private void btnUpAction(ActionEvent event) throws Exception {
        // find the grand parent goal of current goal
        decidePreviousGoalId();

        populateFields(previousGoalId); // send the id of the previous goal
    }

    @FXML
    private void btnRootAction(ActionEvent event) throws Exception {
        populateFields(rootNodeId); // send the id of the topmost goal node
    }

    @FXML
    private void listSubGoalsSelectAction(MouseEvent event) {

        // The selected label
        Label selectedLabel = listSubGoals.getSelectionModel().getSelectedItem();

        if (selectedLabel != null) {
            // send selection to populateFields()
            String selectedId = selectedLabel.getText();
            populateFields(selectedId);
        }
    }

    @FXML
    private void btnTreePopAction(ActionEvent event) throws Exception {

        // Launch the GsnTreeView
        JavaFXAppLaunchManager.gsnTreeViewLaunch();

        // This is a little tricky
        // Here, we have to CREATE A NEW STAGE and set the fxml in the new stage
        // So that we can get acces to the GsnTreeViewHandler object using
        // FxmlLoader and set the variables

        // Set the stage with the other fxml
        FXMLLoader gsnTreeViewLoader =
                GsnTreeViewManager.createNewStageAndSetFxml(
                        "resources/fxml/autoGsn/GsnTreeView.fxml", currentGoalId);

        // initialize variables in the AutoGsnDrillGoalView page
        GsnTreeViewHandler gsnTreeViewLoaderClassObj = gsnTreeViewLoader.getController();
        //        gsnTreeViewLoaderClassObj.prepareTreeView(treeGsn);
        gsnTreeViewLoaderClassObj.prepareTreeView(rootGsn, currentGoalId);
    }

    @FXML
    private void comboPassFailAction(ActionEvent event) throws Exception {

        String key = (String) comboPassFail.getValue();

        // Clear and repopulate the listGoals depending on key
        listSubGoals.getItems().clear();
        populateSubGoals(key);
    }

    @FXML
    private void btnHomeAction(ActionEvent event) throws Exception {

        // Set the stage with the other fxml
        AutoGsnViewsManager.setNewFxmlToStage("resources/fxml/autoGsn/AutoGsnUnifiedMainView.fxml");
    }
}
