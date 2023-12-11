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
package com.ge.research.rack.do178c.viewHandlers;

import com.ge.research.rack.autoGsn.utils.CustomStringUtils;
import com.ge.research.rack.do178c.structures.PsacNode;
import com.ge.research.rack.do178c.utils.PsacNodeUtils;
import com.ge.research.rack.do178c.utils.ReportViewUtils;
import com.ge.research.rack.do178c.viewManagers.ReportViewsManager;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * @author Saswata Paul
 */
public class ReportTableViewHandlerNew {

    private String currentTableId;

    private PsacNode.Table currentTableObject;

    // -------- FXML GUI variables below --------------
    @FXML private Label headerLabel;

    @FXML private Label labelTableInfo;

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
    public Label getObjectiveLabel(PsacNode.Objective objObj) {

        double passPercent =
                ((double) objObj.getNumActPassed()
                                / (objObj.getNumActPassed()
                                        + objObj.getNumActFailed()
                                        + objObj.getNumActNoData()))
                        * 100.00;

        Label objLabel = new Label();
        objLabel.setStyle("-fx-font-weight: bold;");

        //        objLabel.setText(
        //                objObj.getId()
        //                        + ": "
        //                        + objObj.getDescription()
        //                        + " ("
        //                        + String.format("%.2f", passPercent)
        //                        + "% compliant)");
        objLabel.setText(
                objObj.getId()
                        + ": "
                        + objObj.getDescription().replace("\"", "")
                        + " ("
                        + objObj.getMetrics()
                        + ")");
        objLabel.setTextFill(ReportViewUtils.getObjectiveColor(objObj));
        return objLabel;
    }

    /**
     * Populates the list of objectives
     *
     * @param filterKey
     */
    public void populateListObjectives(String filterKey) {
        // clear old data
        listObjectives.getItems().clear();

        if ((currentTableObject.getTabObjectives() != null)
                && (currentTableObject.getTabObjectives().size() > 0)) {

            for (PsacNode.Objective objObj : currentTableObject.getTabObjectives()) {
                //                Label objLabel = new Label();
                //                objLabel.setStyle("-fx-font-weight: bold;");
                //
                //                objLabel.setText(objObj.getId() + ": " + objObj.getDescription());
                //                objLabel.setTextFill(getObjectiveColor(objObj));

                Label objLabel = getObjectiveLabel(objObj);

                if (filterKey.equalsIgnoreCase("All")) {
                    listObjectives.getItems().add(objLabel);
                } else if (filterKey.equalsIgnoreCase("Passed")
                        && (objObj.getPassed() != null)
                        && objObj.getPassed()) {
                    listObjectives.getItems().add(objLabel);
                } else if (filterKey.equalsIgnoreCase("Failed")
                        && (objObj.getPassed() != null)
                        && !objObj.getPassed()
                        && !objObj.getNoData()
                        && !objObj.getPartialData()) {
                    listObjectives.getItems().add(objLabel);
                } else if (filterKey.equalsIgnoreCase("Partial")
                        && (objObj.getPartialData() != null)
                        && objObj.getPartialData()) {
                    listObjectives.getItems().add(objLabel);
                } else if (filterKey.equalsIgnoreCase("No data")
                        && (objObj.getNoData() != null)
                        && objObj.getNoData()) {
                    listObjectives.getItems().add(objLabel);
                }
            }
        }
    }

    /** Populates the ojective status chart */
    public void populateObjStatusChart() {
        // clear the chart
        chartObjStatus.getData().clear();

        // enable the chart
        chartObjStatus.setDisable(false);
        yAxisChartObjStatus.setDisable(false);

        List<Integer> artStats = ReportViewUtils.getTableArtifactStats(currentTableObject);

        //        Data docBar = new XYChart.Data("Documents", artStats.get(0));
        //        Data reqBar = new XYChart.Data("Requirements", artStats.get(1));
        //        Data hzrdBar = new XYChart.Data("Hazards", artStats.get(2));
        //        Data tstBar = new XYChart.Data("Test Results", artStats.get(3));
        //        Data logBar = new XYChart.Data("Review Logs", artStats.get(4));
        //        Data anlsBar = new XYChart.Data("Analyses", artStats.get(5));

        Data docBar = ReportViewUtils.createIntDataBar("Documents", artStats.get(0));
        Data reqBar = ReportViewUtils.createIntDataBar("Requirements", artStats.get(1));
        Data hzrdBar = ReportViewUtils.createIntDataBar("Hazards", artStats.get(2));
        Data tstBar = ReportViewUtils.createIntDataBar("Test Results", artStats.get(3));
        Data logBar = ReportViewUtils.createIntDataBar("Review Logs", artStats.get(4));
        Data anlsBar = ReportViewUtils.createIntDataBar("Analyses", artStats.get(5));

        XYChart.Series tableStat = new XYChart.Series();

        tableStat.getData().add(docBar);
        tableStat.getData().add(reqBar);
        tableStat.getData().add(hzrdBar);
        tableStat.getData().add(tstBar);
        tableStat.getData().add(logBar);
        tableStat.getData().add(anlsBar);

        chartObjStatus.getData().add(tableStat);

        docBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ReportViewUtils.assignTooltip(docBar.getNode(), docBar.getYValue().toString());
        reqBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ReportViewUtils.assignTooltip(reqBar.getNode(), reqBar.getYValue().toString());
        hzrdBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ReportViewUtils.assignTooltip(hzrdBar.getNode(), hzrdBar.getYValue().toString());
        tstBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ReportViewUtils.assignTooltip(tstBar.getNode(), tstBar.getYValue().toString());
        logBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ReportViewUtils.assignTooltip(logBar.getNode(), logBar.getYValue().toString());
        anlsBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ReportViewUtils.assignTooltip(anlsBar.getNode(), anlsBar.getYValue().toString());

        // scaling
        int maxScale = Collections.max(artStats);
        // *** This can help make integral ticks on Y axis ***
        yAxisChartObjStatus.setLowerBound(0);
        yAxisChartObjStatus.setUpperBound(maxScale);
        yAxisChartObjStatus.setTickUnit(1);
    }

    /**
     * Used to initialize the variables from the caller view's fxml controller
     *
     * @param tableId
     */
    public void prepareView(String tableId) {
        // set the current table ID
        currentTableId = tableId;

        // set the current table object
        currentTableObject = PsacNodeUtils.getTableById(ReportViewsManager.reportDataObj, tableId);

        // populate objectives list
        populateListObjectives("All");

        // populate the chart
        populateObjStatusChart();

        // Set the label text
        labelTableInfo.setText(
                "Table " + currentTableId + ": " + currentTableObject.getDescription());

        // set the label color
        labelTableInfo.setTextFill(ReportViewUtils.getTableColor(currentTableObject));
    }

    @FXML
    private void initialize() {
        // initialize the header label
        try {
            String imagePath = "resources/images/headerWithLogoTransparent.png";

            Bundle bundle = Platform.getBundle("rack.plugin");
            URL imgUrl = FileLocator.find(bundle, new Path(imagePath), null);
            imgUrl = FileLocator.toFileURL(imgUrl);

            // the imgURL starts with "file:/", so stripping it here
            String imgUrlStr = imgUrl.toString().substring("file:/".length());

            // System.out.println(imgUrl.toString());

            ImageView icon = new ImageView(new Image(new FileInputStream(new File(imgUrlStr))));
            icon.setFitHeight(60);
            icon.setPreserveRatio(true);

            headerLabel.setGraphic(icon);
        } catch (Exception e) {
            // do nothing for now
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
        ReportViewsManager.setNewFxmlToStage("resources/fxml/report/ReportMainView_new.fxml");
    }

    @FXML
    private void btnFontIncAction(ActionEvent event) throws Exception {
        System.out.println("increase font btn pressed");
        ReportViewsManager.increaseGlobalFontSize(true);
    }

    @FXML
    private void btnFontDecAction(ActionEvent event) throws Exception {
        System.out.println("decrease font btn pressed");
        ReportViewsManager.increaseGlobalFontSize(false);
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
            if (!PsacNodeUtils.getObjectiveById(
                            ReportViewsManager.reportDataObj, currentTableId, selectedObjective)
                    .getNoData()) {
                // Set the stage with the other fxml
                FXMLLoader objectiveViewLoader =
                        ReportViewsManager.setNewFxmlToStage(
                                "resources/fxml/report/ReportObjectiveView_new_boeing.fxml");

                // initialize variables in the ReportTableView page
                ReportObjectiveViewHandlerNew objectiveViewLoaderClassObj =
                        objectiveViewLoader.getController();
                objectiveViewLoaderClassObj.prepareView(currentTableId, selectedObjective);
            }
        }
    }
}
