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
package com.ge.research.rite.arp4754.viewHandlers;

import com.ge.research.rite.arp4754.structures.DAPlan;
import com.ge.research.rite.arp4754.utils.DAPlanUtils;
import com.ge.research.rite.arp4754.utils.ViewUtils;
import com.ge.research.rite.arp4754.viewManagers.Arp4754ViewsManager;
import com.ge.research.rite.autoGsn.utils.CustomStringUtils;
import com.ge.research.rite.do178c.utils.ReportViewUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.List;

/**
 * @author Saswata Paul
 */
public class ProcessViewHandler {

    private String currentProcessId;

    private DAPlan.Process currentProcessObject;

    // -------- FXML GUI variables below --------------
    @FXML private Label headerLabel;

    @FXML private Label labelProcessInfo;

    @FXML private Button btnHome;
    @FXML private Button btnFontInc;
    @FXML private Button btnFontDec;

    @FXML private ListView<Label> listObjectives;

    @FXML private ComboBox comboFilter;

    @FXML private BarChart chartObjStatus;
    @FXML private NumberAxis yAxisChartObjStatus;

    // --------------------------------

    /**
     * Creates and returns a label for the objective lists
     *
     * @param objObj
     * @return
     */
    public Label getObjectiveLabel(DAPlan.Objective objObj) {

        Label objLabel = new Label();
        objLabel.setStyle("-fx-font-weight: bold;");

        if (objObj.getMetrics().equalsIgnoreCase("TBD")) {
            objLabel.setText(
                    objObj.getId()
                            + ": "
                            + objObj.getDesc().replace("\"", "")
                            + " ("
                            + objObj.getMetrics()
                            + ")");
            objLabel.setTextFill(Color.LIGHTGREY);
        } else {
            objLabel.setText(
                    objObj.getId()
                            + ": "
                            + objObj.getDesc().replace("\"", "")
                            + " ("
                            + String.format("%.2f", objObj.getComplianceStatus())
                            + "% complete)");
            objLabel.setTextFill(ViewUtils.getObjectiveColor(objObj));
        }

        return objLabel;
    }

    /** Populates the ojective status chart */
    public void populateObjStatusChart() {
        // clear the chart
        chartObjStatus.getData().clear();

        // enable the chart
        chartObjStatus.setDisable(false);
        yAxisChartObjStatus.setDisable(false);

        List<Integer> artStats = ViewUtils.getProcessArtifactStats(currentProcessObject);

        Data docBar = ReportViewUtils.createIntDataBar("Documents", artStats.get(0));
        Data reqBar = ReportViewUtils.createIntDataBar("Requirements", artStats.get(1));
        Data itemBar = ReportViewUtils.createIntDataBar("Items", artStats.get(2));
        Data interfaceBar = ReportViewUtils.createIntDataBar("Interfaces", artStats.get(3));
        Data systemBar = ReportViewUtils.createIntDataBar("Systems", artStats.get(4));
        Data verificationBar = ReportViewUtils.createIntDataBar("Verifications", artStats.get(5));
        Data testBar = ReportViewUtils.createIntDataBar("Tests", artStats.get(6));
        Data reviewBar = ReportViewUtils.createIntDataBar("Reviews", artStats.get(7));
        Data analysisBar = ReportViewUtils.createIntDataBar("Analyses", artStats.get(8));

        XYChart.Series tableStat = new XYChart.Series();

        tableStat.getData().add(docBar);
        tableStat.getData().add(reqBar);
        tableStat.getData().add(itemBar);
        tableStat.getData().add(interfaceBar);
        tableStat.getData().add(systemBar);
        tableStat.getData().add(verificationBar);
        tableStat.getData().add(testBar);
        tableStat.getData().add(reviewBar);
        tableStat.getData().add(analysisBar);

        chartObjStatus.getData().add(tableStat);

        docBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ReportViewUtils.assignTooltip(docBar.getNode(), docBar.getYValue().toString());
        reqBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ReportViewUtils.assignTooltip(reqBar.getNode(), reqBar.getYValue().toString());
        itemBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ReportViewUtils.assignTooltip(itemBar.getNode(), itemBar.getYValue().toString());
        interfaceBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ReportViewUtils.assignTooltip(interfaceBar.getNode(), interfaceBar.getYValue().toString());
        systemBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ReportViewUtils.assignTooltip(systemBar.getNode(), systemBar.getYValue().toString());
        verificationBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ReportViewUtils.assignTooltip(
                verificationBar.getNode(), verificationBar.getYValue().toString());
        testBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ReportViewUtils.assignTooltip(testBar.getNode(), testBar.getYValue().toString());
        reviewBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ReportViewUtils.assignTooltip(reviewBar.getNode(), reviewBar.getYValue().toString());
        analysisBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ReportViewUtils.assignTooltip(analysisBar.getNode(), analysisBar.getYValue().toString());

        // scaling
        int maxScale = Collections.max(artStats);
        // *** This can help make integral ticks on Y axis ***
        yAxisChartObjStatus.setLowerBound(0);
        yAxisChartObjStatus.setUpperBound(maxScale);
        yAxisChartObjStatus.setTickUnit(1);
    }

    /**
     * Populates the list of objectives
     *
     * @param filterKey
     */
    public void populateListObjectives(String filterKey) {
        // clear old data
        listObjectives.getItems().clear();

        if ((currentProcessObject.getObjectives() != null)
                && (currentProcessObject.getObjectives().size() > 0)) {

            // sort the objective list
            List<DAPlan.Objective> allObjectiveObjs =
                    DAPlanUtils.sortObjectiveList(currentProcessObject.getObjectives());
            //            List<DAPlan.Objective> allObjectiveObjs =
            // currentProcessObject.getObjectives();

            for (DAPlan.Objective objObj : allObjectiveObjs) {

                Label objLabel = getObjectiveLabel(objObj);

                if (filterKey.equalsIgnoreCase("All")) {
                    listObjectives.getItems().add(objLabel);
                } else if (filterKey.equalsIgnoreCase("Passed") && objObj.isPassed()) {
                    listObjectives.getItems().add(objLabel);
                } else if (filterKey.equalsIgnoreCase("Failed")
                        && !objObj.isPassed()
                        && !objObj.isNoData()
                        && !objObj.isPartialData()) {
                    listObjectives.getItems().add(objLabel);
                } else if (filterKey.equalsIgnoreCase("Partial") && objObj.isPartialData()) {
                    listObjectives.getItems().add(objLabel);
                } else if (filterKey.equalsIgnoreCase("No data") && objObj.isNoData()) {
                    listObjectives.getItems().add(objLabel);
                }
            }
        }
    }

    /**
     * Used to initialize the variables from the caller view's fxml controller
     *
     * @param procId
     */
    public void prepareView(String procId) {
        // set the current table ID
        currentProcessId = procId;

        // set the current table object
        currentProcessObject =
                DAPlanUtils.getProcessObjectFromList(
                        Arp4754ViewsManager.reportDataObj.getProcesses(), procId);

        // populate objectives list
        populateListObjectives("All");

        // populate the chart
        populateObjStatusChart();

        // Set the label text
        labelProcessInfo.setText(currentProcessId + ": " + currentProcessObject.getDesc());

        // set the label color
        labelProcessInfo.setTextFill(ViewUtils.getProcessColor(currentProcessObject));
    }

    @FXML
    private void initialize() {
        try {
            final ImageView icon = ReportViewUtils.loadGeIcon();
            icon.setPreserveRatio(true);
            headerLabel.setGraphic(icon);
        } catch (Exception e) {
        }

        // set SINGLE Selection Model for ListView of Goals
        listObjectives.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // add the categories to the combo
        comboFilter.getItems().add("All");
        comboFilter.getItems().add("Passed");
        comboFilter.getItems().add("Failed");
        comboFilter.getItems().add("No Data");
        comboFilter.getItems().add("Partial");
    }

    @FXML
    private void btnHomeAction(ActionEvent event) throws Exception {
        // Set the stage with the other fxml
        Arp4754ViewsManager.setNewFxmlToStage("resources/fxml/arp4754/MainView.fxml");
    }

    @FXML
    private void btnFontIncAction(ActionEvent event) throws Exception {
        System.out.println("increase font btn pressed");
        Arp4754ViewsManager.increaseGlobalFontSize(true);
    }

    @FXML
    private void btnFontDecAction(ActionEvent event) throws Exception {
        System.out.println("decrease font btn pressed");
        Arp4754ViewsManager.increaseGlobalFontSize(false);
    }

    @FXML
    private void comboFilterAction(ActionEvent event) throws Exception {
        String key = (String) comboFilter.getValue();

        // Clear and repopulate the listObjectives depending on key
        populateListObjectives(key);
    }

    @FXML
    private void listObjectivesSelectionAction(MouseEvent event) {
        // The selected label
        Label selectedLabel = listObjectives.getSelectionModel().getSelectedItem();

        if (selectedLabel != null) {

            // get selection
            String selectedObjective =
                    CustomStringUtils.separateElementIdFromDescription(selectedLabel.getText());
            System.out.println("The selected Objective: " + selectedObjective);

            // switch to objective view only if the objective has some data
            if (!DAPlanUtils.getObjectiveObjectFromList(
                            currentProcessObject.getObjectives(), selectedObjective)
                    .isNoData()) {
                // Set the stage with the other fxml
                FXMLLoader objectiveViewLoader =
                        Arp4754ViewsManager.setNewFxmlToStage(
                                "resources/fxml/arp4754/ObjectiveView.fxml");

                // initialize variables in the ReportTableView page
                ObjectiveViewHandler objectiveViewLoaderClassObj =
                        objectiveViewLoader.getController();
                objectiveViewLoaderClassObj.prepareView(currentProcessId, selectedObjective);
            }
        }
    }
}
