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

import com.ge.research.rack.autoGsn.utils.CustomFileUtils;
import com.ge.research.rack.autoGsn.utils.CustomStringUtils;
import com.ge.research.rack.do178c.oem.DataProcessor;
import com.ge.research.rack.do178c.structures.PsacNode;
import com.ge.research.rack.do178c.utils.PsacNodeUtils;
import com.ge.research.rack.do178c.utils.ReportViewUtils;
import com.ge.research.rack.do178c.viewManagers.ReportViewsManager;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 */
public class ReportMainViewHandlerNew {

    // -------- FXML GUI variables below --------------
    @FXML private Label headerLabel;

    @FXML private Button btnFetch;
    @FXML private Button btnFontInc;
    @FXML private Button btnFontDec;

    @FXML private Label labelSwInfo;

    @FXML private Label labelWait;
    @FXML private ProgressIndicator progInd;

    @FXML private BarChart<String, Integer> chartTableStatus;
    @FXML private NumberAxis yAxisChartTableStatus;
    @FXML private GridPane gridPaneLegend;

    @FXML private ListView<Label> listTables;

    // --------------------------------

    /**
     * Given "TableAx: yzaysy", returns "Ax"
     *
     * @param tabLabelText
     * @return
     */
    private String getTableIdFromLabelText(String tabLabelText) {
        String TableAx = CustomStringUtils.separateElementIdFromDescription(tabLabelText);
        String Ax = TableAx.replace("Table ", "");
        return Ax;
    }

    /**
     * Create the label for table listview for a given table object
     *
     * @param tabObj
     * @return
     */
    private Label getTableLabel(PsacNode.Table tabObj) {

        double passPercent =
                ((double) tabObj.getNumObjPassed()
                                / (tabObj.getNumObjPassed()
                                        + tabObj.getNumObjFailed()
                                        + tabObj.getNumObjPartial()
                                        + tabObj.getNumObjNodata()))
                        * 100.00;

        Label tabLabel = new Label();
        String tabText =
                "Table "
                        + tabObj.getId()
                        + ": "
                        + tabObj.getDescription().replace("\"", "")
                        + " ("
                        + String.format("%.2f", passPercent)
                        + "% objectives passed)";

        tabLabel.setText(tabText);

        //        if (tabObj.getNoData()) {
        //            tabLabel.setTextFill(Color.GRAY);
        //        } else if (tabObj.getPartialData()) {
        //            tabLabel.setTextFill(Color.ORANGE);
        //        } else if (tabObj.getPassed()) {
        //            tabLabel.setTextFill(Color.GREEN);
        //        } else if (!tabObj.getPassed()) {
        //            tabLabel.setTextFill(Color.RED);
        //        }

        tabLabel.setTextFill(ReportViewUtils.getTableColor(tabObj));

        tabLabel.setStyle("-fx-font-weight: bold;");

        return tabLabel;
    }

    /**
     * Get Report data
     *
     * @param inputs
     * @return
     * @throws Exception
     */
    private void getReportData() throws Exception {

        // Create tempdir to store the csv files
        String tempDir = CustomFileUtils.getRackDir();

        DataProcessor rdpObj = new DataProcessor();

        ReportViewsManager.reportDataObj = rdpObj.getPSACData(tempDir);
    }

    /**
     * Decides whether the fetch button should be enabled or, if already enabled, if it should be
     * disabled with the dependents
     */
    private void enableFetchButton() {
        // TODO: Add condition
        if (true) {
            btnFetch.setDisable(false);
        } else {
            // TODO: add action
            System.out.println("Unspecified for now");
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

    /** populates the top label with sw info */
    private void populateLabelSwInfo() {
        // enable elements
        labelSwInfo.setVisible(true);

        String swInfo = "Software ID: " + ReportViewsManager.reportDataObj.getMainOFP();
        labelSwInfo.setText(swInfo);
    }

    /** populates the chart */
    private void populateChartTableStatus() {
        // clear the chart
        chartTableStatus.getData().clear();

        // enable the chart
        chartTableStatus.setDisable(false);
        yAxisChartTableStatus.setDisable(false);
        gridPaneLegend.setDisable(false);

        // to store the highest value for scaling
        int high = -1;

        // Group bars by table id

        var passData = new XYChart.Series<String, Integer>();
        passData.setName("Complete");
        var passBars = new ArrayList<Data<String, Integer>>();
        for (PsacNode.Table tabObj : ReportViewsManager.reportDataObj.getReportTables()) {
            //            Data passBar = new XYChart.Data(tabObj.getId(), tabObj.getNumObjPassed());
            var passBar =
                    ReportViewUtils.createIntDataBar(tabObj.getId(), tabObj.getNumObjPassed());
            passData.getData().add(passBar);
            passBars.add(passBar);
            if (high < tabObj.getNumObjPassed()) {
                high = tabObj.getNumObjPassed() + 1;
            }
        }

        var failData = new XYChart.Series<String, Integer>();
        failData.setName("Complete");
        var failBars = new ArrayList<Data<String, Integer>>();
        for (PsacNode.Table tabObj : ReportViewsManager.reportDataObj.getReportTables()) {
            //            Data failBar = new XYChart.Data(tabObj.getId(), tabObj.getNumObjFailed());
            var failBar =
                    ReportViewUtils.createIntDataBar(tabObj.getId(), tabObj.getNumObjFailed());
            failData.getData().add(failBar);
            failBars.add(failBar);
            if (high < tabObj.getNumObjFailed()) {
                high = tabObj.getNumObjFailed() + 1;
            }
        }

        var partialData = new XYChart.Series<String, Integer>();
        partialData.setName("Partial Data");
        var partialBars = new ArrayList<Data<String, Integer>>();
        for (PsacNode.Table tabObj : ReportViewsManager.reportDataObj.getReportTables()) {
            //            Data partialBar = new XYChart.Data(tabObj.getId(),
            // tabObj.getNumObjPartial());
            var partialBar =
                    ReportViewUtils.createIntDataBar(tabObj.getId(), tabObj.getNumObjPartial());
            partialData.getData().add(partialBar);
            partialBars.add(partialBar);
            if (high < tabObj.getNumObjPartial()) {
                high = tabObj.getNumObjPartial() + 1;
            }
        }

        var noData = new XYChart.Series<String, Integer>();
        noData.setName("No Data");
        var noBars = new ArrayList<Data<String, Integer>>();
        for (PsacNode.Table tabObj : ReportViewsManager.reportDataObj.getReportTables()) {
            //            Data noBar = new XYChart.Data(tabObj.getId(), tabObj.getNumObjNodata());
            var noBar = ReportViewUtils.createIntDataBar(tabObj.getId(), tabObj.getNumObjNodata());
            noData.getData().add(noBar);
            noBars.add(noBar);
            if (high < tabObj.getNumObjNodata()) {
                high = tabObj.getNumObjNodata() + 1;
            }
        }

        chartTableStatus.getData().add(passData);
        chartTableStatus.getData().add(failData);
        chartTableStatus.getData().add(noData);
        chartTableStatus.getData().add(partialData);

        for (var bar : passBars) {
            bar.getNode().getStyleClass().add("color-passed");
            ReportViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }
        for (var bar : failBars) {
            bar.getNode().getStyleClass().add("color-failed");
            ReportViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }
        for (var bar : partialBars) {
            bar.getNode().getStyleClass().add("color-partial-data");
            ReportViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }
        for (var bar : noBars) {
            bar.getNode().getStyleClass().add("color-no-data");
            ReportViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }

        // scaling
        // *** This can help make integral ticks on Y axis ***
        yAxisChartTableStatus.setLowerBound(0);
        yAxisChartTableStatus.setUpperBound(high);
        yAxisChartTableStatus.setTickUnit(1);
    }

    /**
     * populates the listview
     *
     * @param key
     */
    private void populateListTables(String key) {
        // clear old data
        listTables.getItems().clear();

        // enable elements
        listTables.setDisable(false);

        for (PsacNode.Table tabObj : ReportViewsManager.reportDataObj.getReportTables()) {
            Label tabLabel = getTableLabel(tabObj);
            if (key.equalsIgnoreCase("All")) {
                listTables.getItems().add(tabLabel);
            } else if (key.equalsIgnoreCase("Passed")
                    && (tabObj.getPassed() != null)
                    && tabObj.getPassed()) {
                listTables.getItems().add(tabLabel);
            } else if (key.equalsIgnoreCase("Failed")
                    && (tabObj.getPassed() != null)
                    && !tabObj.getPassed()) {
                listTables.getItems().add(tabLabel);
            } else if (key.equalsIgnoreCase("Inconclusive")
                    && (tabObj.getNoData() != null)
                    && tabObj.getNoData()) {
                //    				actLabel.setVisible(true);
                listTables.getItems().add(tabLabel);
            }
        }
    }

    /** Calls necessary functions to populate view elements from data */
    private void populateViewElements() {
        populateLabelSwInfo();

        populateChartTableStatus();

        populateListTables("All");
    }

    @FXML
    private void initialize() {
        try {
            final ImageView icon = ReportViewUtils.loadGeIcon();
            icon.setPreserveRatio(true);
            headerLabel.setGraphic(icon);
        } catch (Exception e) {
        }

        // disable the display environments
        chartTableStatus.setDisable(true);
        yAxisChartTableStatus.setDisable(true);
        gridPaneLegend.setDisable(true);
        listTables.setDisable(true);
        labelSwInfo.setVisible(false);
        labelSwInfo.setText("");
        enableWaitMessage(false);

        // set properties
        // set SINGLE Selection Model for ListView of Tables
        listTables.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // If the report variables have already been initialized
        if (ReportViewsManager.reportDataObj.getReportTables() != null) {
            populateViewElements();

        } else {
            // Initialize the variables
            ReportViewsManager.initializeViewVariables();
        }

        enableFetchButton();
    }

    @FXML
    private void btnFetchAction(ActionEvent event) throws Exception {
        // initialize the manager variables again
        ReportViewsManager.initializeViewVariables();

        // reset all the views again
        initialize();

        // disable the fetch button
        btnFetch.setDisable(true);
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

                        long startTime = System.currentTimeMillis();

                        // Get report data
                        getReportData();

                        long finishTime = System.currentTimeMillis();

                        System.out.println(
                                "Time taken to fetch data and create report: "
                                        + (finishTime - startTime)
                                        + " ms.");

                        return null;
                    }
                };
        task.setOnSucceeded(
                evt -> {
                    // populate views
                    populateViewElements();

                    // disable wait label
                    enableWaitMessage(false);
                    // enable the fetch button
                    btnFetch.setDisable(false);
                });
        new Thread(task).start();
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
    private void listTableSelectionAction(MouseEvent event) {

        // The selected label
        Label selectedLabel = listTables.getSelectionModel().getSelectedItem();

        if (selectedLabel != null) {

            // get selection
            String selectedTable = getTableIdFromLabelText(selectedLabel.getText());
            System.out.println("The selected Table: " + selectedTable);

            if (!PsacNodeUtils.getTableById(ReportViewsManager.reportDataObj, selectedTable)
                    .getNoData()) {
                // Set the stage with the other fxml
                FXMLLoader tableViewLoader =
                        ReportViewsManager.setNewFxmlToStage(
                                "resources/fxml/do178c/DO178CTableView.fxml");

                // initialize variables in the ReportTableView page
                ReportTableViewHandlerNew tableViewLoaderClassObj = tableViewLoader.getController();
                tableViewLoaderClassObj.prepareView(selectedTable);
            }
        }
    }
}
