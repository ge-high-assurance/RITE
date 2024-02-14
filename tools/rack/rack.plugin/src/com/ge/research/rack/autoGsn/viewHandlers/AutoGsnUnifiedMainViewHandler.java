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

import com.ge.research.rack.analysis.structures.SparqlConnectionInfo;
import com.ge.research.rack.analysis.utils.CustomFileUtils;
import com.ge.research.rack.analysis.utils.CustomStringUtils;
import com.ge.research.rack.analysis.utils.RackQueryUtils;
import com.ge.research.rack.analysis.utils.ReportViewUtils;
import com.ge.research.rack.autoGsn.logic.DataProcessor;
import com.ge.research.rack.autoGsn.logic.GsnPathInferenceEngine;
import com.ge.research.rack.autoGsn.structures.GsnNode;
import com.ge.research.rack.autoGsn.structures.MultiClassPackets.GoalIdAndClass;
import com.ge.research.rack.autoGsn.utils.AutoGsnGuiUtils;
import com.ge.research.rack.autoGsn.utils.QueryResultUtils;
import com.ge.research.rack.autoGsn.viewManagers.AutoGsnViewsManager;
import com.ge.research.rack.views.RackPreferencePage;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 */
public class AutoGsnUnifiedMainViewHandler {

    private GsnNode recentlyGeneratedGsn;

    // -------- FXML GUI variables below --------------

    @FXML private TextField pathCore;
    @FXML private TextField pathPattern;
    @FXML private TextField pathProject;

    @FXML private Button btnFetchGoals;
    @FXML private Button btnGenerate;
    @FXML private Button btnTraverse;

    @FXML private ListView<Label> listGoals;

    @FXML private Label labelSelectPreferences;
    @FXML private Label labelSelectionGuide;

    @FXML private Label labelWait;
    @FXML private ProgressIndicator progInd;

    @FXML private ComboBox<String> comboClassFilter;
    //    @FXML private ComboBox<String> comboStatusFilter;

    @FXML private GridPane gridPaneCharts;
    //    @FXML private BarChart chartBarStatus;
    @FXML private BarChart chartBarClass;

    // --------------------------------

    /** Calls the necessary logic functions to get the path info and the goal id info */
    private void getPathInfoAndGoalInfo() {
        try {
            // create connPars
            // ***************** DO NOT DELETE ***************** TURNED OFF FOR TESTING
            // Connect to RACK using RACK preferences
            SparqlConnectionInfo newConnPars = RackQueryUtils.initiateQueryConnection();

            // get protocol, server, and ontology port
            String protocol = RackPreferencePage.getProtocol();
            String server = RackPreferencePage.getServer();
            int ontologyPort = Integer.parseInt(RackPreferencePage.getOntologyPort());

            // get Rack output directory for temp files
            AutoGsnViewsManager.rackDir = CustomFileUtils.getRackDir();

            // fetch pattern info, create queries, and fetch data
            GsnPathInferenceEngine pathInferEngineObj = new GsnPathInferenceEngine();
            pathInferEngineObj.fetchPatternInfo(
                    newConnPars, AutoGsnViewsManager.rackDir, protocol, server, ontologyPort);

            AutoGsnViewsManager.pathInferEngObj = pathInferEngineObj;

            // get the list of goals from the data and assign to manager objects
            AutoGsnViewsManager.allRootGoals =
                    QueryResultUtils.getGoalIdsAndClasses(
                            pathInferEngineObj.getPatInfo(), AutoGsnViewsManager.rackDir);

            // get list of unique goal class ids
            AutoGsnViewsManager.allGoalClassIds =
                    AutoGsnGuiUtils.getAllClasses(AutoGsnViewsManager.allRootGoals);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * conditionally enables the traverse button iff: 1. Listview is populated 2. A goal is selected
     * 3. The id of the goal is same as the rootId of the recently created Gsn
     */
    private void enableTraverseButton() {
        // get selections (Although only one will be selected)
        ObservableList<Label> selected = listGoals.getSelectionModel().getSelectedItems();

        if ((selected.size() == 1) && (recentlyGeneratedGsn != null)) {
            String actualSelection = selected.get(0).getText();

            String selectedGoalId =
                    CustomStringUtils.separateElementIdFromDescription(actualSelection);

            if (selectedGoalId.equalsIgnoreCase(recentlyGeneratedGsn.getGoal().getId())) {
                btnTraverse.setDisable(false);
            }
        }
    }

    /**
     * Decides whether the fetch button should be enabled or, if already enabled, if it should be
     * disabled with the dependents
     */
    private void enableFetchButton() {
        System.out.println("3");
        // If preferences are there
        if (RackPreferencePage.areGSNPreferencesComplete()) {
            System.out.println("4");
            btnFetchGoals.setDisable(false);
        } else {
            System.out.println("5");
            labelSelectPreferences.setVisible(true);
        }
    }

    /**
     * Enables/disables the wait message
     *
     * @param val
     */
    private void enableWaitMessage(Boolean val) {
        labelWait.setVisible(val);
        progInd.setVisible(val);
    }

    /** Populates the charts with statistics */
    private void populateCharts() {
        // clear the charts
        chartBarClass.getData().clear();

        // populate the class bar chart

        XYChart.Series classData = new XYChart.Series();

        // Create a list to store the Data objects so that we can
        // color them later
        List<Data> classBarList = new ArrayList<Data>();

        // By this step, all classes will have been added
        // to the comboClassFilter
        // So, for each class in the filter, do a count and add to chartPie
        for (String uniqueClass : comboClassFilter.getItems()) {
            String classKey = uniqueClass.toString();
            if (!classKey.equalsIgnoreCase("All")) {
                int classCount =
                        AutoGsnGuiUtils.getClassCount(AutoGsnViewsManager.allRootGoals, classKey);
                Data classBar = new XYChart.Data(classKey, classCount);
                classData.getData().add(classBar);
                classBarList.add(classBar);
            }
        }
        chartBarClass.getData().add(classData);

        // set random colors to the class bars
        for (Data bar : classBarList) {
            //            bar.getNode().setStyle("-fx-bar-fill: " + AutoGsnGuiUtils.randomColor() +
            // ";");
            bar.getNode().setStyle("-fx-bar-fill: #635452;");
            ReportViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }
    }

    /**
     * Populates the list of Goals according to class
     *
     * @param classKey
     */
    private void populateListGoals(String classKey) {
        // Clear the list of goals
        listGoals.getItems().clear();

        // TODO: complete this by fetching goalIDS from query data
        for (GoalIdAndClass goalInfo : AutoGsnViewsManager.allRootGoals) {
            if (classKey.equalsIgnoreCase("all") || classKey.equals(goalInfo.getGoalClass())) {
                Label rootGoalLabel = new Label();
                rootGoalLabel.setText(goalInfo.getGoalId());
                listGoals.getItems().add(rootGoalLabel);
            }
        }

        // set Single Selection Model for ListView of Goals
        listGoals.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    /** Populates the combobox with goal categories */
    private void populateComboClassFilter() {
        // clear comboFiler
        comboClassFilter.getItems().clear();

        // add the categories to the combo
        comboClassFilter.getItems().add("All");
        for (String clss : AutoGsnViewsManager.allGoalClassIds) {
            comboClassFilter.getItems().add(clss);
        }
    }

    /** Calls necessary functions to populate view elements from data */
    private void populateViewElements() {
        // populate the list of goals with all goals
        populateListGoals("All");

        // populate charts and combofilters
        populateComboClassFilter();
        //        populateComboStatusFilter();
        populateCharts();
    }

    @FXML
    private void initialize() {

        // Disabled buttons
        btnFetchGoals.setDisable(true);
        btnGenerate.setDisable(true);
        btnTraverse.setDisable(true);
        comboClassFilter.setDisable(true);
        labelSelectionGuide.setVisible(false);
        labelSelectPreferences.setVisible(false);
        enableWaitMessage(false);
        enableFetchButton();

        // disable the display environments
        listGoals.setDisable(true);
        chartBarClass.setDisable(true);

        // If the gsnVObjs, allRootGoals, and allRootGoalIds have already been initialized
        if ((AutoGsnViewsManager.gsnVObjs != null)
                && (AutoGsnViewsManager.allRootGoals.size() > 0)
                && AutoGsnViewsManager.allGoalClassIds.size() > 0) {
            System.out.println("1");

            // Then populate the list
            populateListGoals("All");

            // Enable buttons and displays
            btnFetchGoals.setDisable(false);
            btnGenerate.setDisable(false);
            comboClassFilter.setDisable(false);

            listGoals.setDisable(false);
            chartBarClass.setDisable(false);

            // populate displays
            populateComboClassFilter();
            populateCharts();
            labelSelectionGuide.setVisible(true);
        } else {
            System.out.println("2");
            // Initialize the variables
            AutoGsnViewsManager.initializeViewVariables();
        }

        enableFetchButton();
    }

    @FXML
    private void btnFetchGoalsAction(ActionEvent event) throws Exception {

        // initialize the manager variables again
        AutoGsnViewsManager.initializeViewVariables();

        // initialize all the buttons again
        initialize();

        // Store the paths in gsnVObjs
        AutoGsnViewsManager.gsnVObjs.setPatternGsnPath(
                RackPreferencePage.getGsnProjectPatternSadl());
        AutoGsnViewsManager.gsnVObjs.setProjectOverlayPath(
                RackPreferencePage.getGsnProjectOverlaySadl());

        // disable the buttons
        btnFetchGoals.setDisable(true);
        btnGenerate.setDisable(true);
        btnTraverse.setDisable(true);
        // enable wait label
        enableWaitMessage(true);

        /*
         *  sending data processor to a different thread
         *  This is required to be able to disable the
         *  fetch button while it is already working
         */
        Task<Void> task =
                new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {

                        // Get all possible GSNs
                        getPathInfoAndGoalInfo();

                        return null;
                    }
                };
        task.setOnSucceeded(
                evt -> {
                    // populate all views
                    populateViewElements();

                    // display the selection instructions
                    labelSelectionGuide.setVisible(true);

                    // enable buttons and displays
                    btnGenerate.setDisable(false);
                    comboClassFilter.setDisable(false);
                    //                    comboStatusFilter.setDisable(false);

                    listGoals.setDisable(false);
                    chartBarClass.setDisable(false);
                    //                    chartBarStatus.setDisable(false);

                    // disable wait label
                    //                    labelWait.setVisible(false);
                    enableWaitMessage(false);
                    // enable the fetch button
                    btnFetchGoals.setDisable(false);
                    enableTraverseButton();
                    labelSelectionGuide.setVisible(true);
                });
        new Thread(task).start();
    }

    @FXML
    private void btnGenerateAction(ActionEvent event) throws Exception {

        // get selections (Although only one will be selected)
        ObservableList<Label> selected = listGoals.getSelectionModel().getSelectedItems();

        List<String> actualSelections = new ArrayList<String>();

        if (selected.size() > 0) {
            for (int i = 0; i < selected.size(); i++)
                actualSelections.add(selected.get(i).getText());
        }

        // Strip "::" from each element in actual selections to get goal Ids only
        List<String> actualSelectionIds = new ArrayList<String>();
        for (String goalIdAndDescp : actualSelections) {
            actualSelectionIds.add(
                    CustomStringUtils.separateElementIdFromDescription(goalIdAndDescp));
        }

        // get the class of the selected goal
        String goalClassId =
                AutoGsnGuiUtils.getGoalClassInfo(
                        actualSelectionIds.get(0), AutoGsnViewsManager.allRootGoals);

        // get the instance id
        String instanceId = actualSelectionIds.get(0).replaceFirst("^G-", "");

        // Send selections to interfacing to create the gsns and block all buttons until completion
        // disable the buttons
        btnFetchGoals.setDisable(true);
        btnGenerate.setDisable(true);
        btnTraverse.setDisable(true);
        enableWaitMessage(true);
        labelSelectionGuide.setVisible(false);

        /*
         *  sending data processor to a different thread
         *  This is required to be able to disable the
         *  fetch button while it is already working
         */
        Task<Void> task =
                new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {

                        // Create object of dataprocessor
                        DataProcessor dataProcessorObj = new DataProcessor();

                        // create GSN Node and dependencies
                        dataProcessorObj.createSpecificGsnNode(
                                instanceId,
                                goalClassId,
                                AutoGsnViewsManager.pathInferEngObj.getPatInfo(),
                                AutoGsnViewsManager.rackDir);

                        // assign to recently generated
                        recentlyGeneratedGsn = dataProcessorObj.getAllGsnNodes().get(0);

                        // send to interfacing util to create artifacts
                        AutoGsnGuiUtils.createArtifactsFromSelectedGsnNodes(
                                AutoGsnViewsManager.gsnVObjs,
                                dataProcessorObj.getAllGsnNodes(),
                                dataProcessorObj.getInstanceDataDependencies(),
                                actualSelectionIds);

                        return null;
                    }
                };
        task.setOnSucceeded(
                evt -> {
                    enableWaitMessage(false);
                    // enable buttons
                    btnFetchGoals.setDisable(false);
                    btnGenerate.setDisable(false);
                    enableTraverseButton();
                    labelSelectionGuide.setVisible(true);
                });
        new Thread(task).start();
    }

    @FXML
    private void comboFilterAction(ActionEvent event) throws Exception {

        String newClassKey = (String) comboClassFilter.getValue();

        if (newClassKey == null) {
            newClassKey = "All";
        }

        // Clear and repopulate the listGoals depending on key
        populateListGoals(newClassKey);
    }

    @FXML
    private void btnTraverseAction(ActionEvent event) throws Exception {

        // get selections
        ObservableList<Label> selected = listGoals.getSelectionModel().getSelectedItems();

        // check selections: if multiple or none, throw error, else send selection
        if ((selected.size() != 1)) {
            // TODO: error message
            Tooltip tooltip =
                    new Tooltip("Exactly a single goal must be selected for this feature!!");
            tooltip.setShowDelay(Duration.seconds(0));
            btnTraverse.setTooltip(tooltip);
        } else {

            // get selection id
            String selectionId =
                    CustomStringUtils.separateElementIdFromDescription(
                            listGoals.getSelectionModel().getSelectedItem().getText());

            // Set the stage with the other fxml
            FXMLLoader drillViewLoader =
                    AutoGsnViewsManager.setNewFxmlToStage(
                            "resources/fxml/autoGsn/AutoGsnUnifiedDrillGoalView.fxml");

            // initialize variables in the AutoGsnDrillGoalView page
            AutoGsnUnifiedDrillGoalViewHandler drillViewLoaderClassObj =
                    drillViewLoader.getController();
            drillViewLoaderClassObj.prepareView(0, selectionId, recentlyGeneratedGsn);
        }
    }

    @FXML
    private void listGoalsSelectAction(MouseEvent event) {

        //        // activate/deactivate the GoalView button
        //        // if exactly one selection, activate, otherwise deactivate
        //
        //        // get selections
        //        ObservableList<Label> selected = listGoals.getSelectionModel().getSelectedItems();
        //
        //        if (selected.size() == 1) {
        //            btnTraverse.setDisable(false);
        //        } else {
        //            btnTraverse.setDisable(true);
        //        }

        enableTraverseButton();
    }
}
