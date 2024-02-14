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

import com.ge.research.rack.analysis.structures.PlanTable;
import com.ge.research.rack.analysis.utils.CustomFileUtils;
import com.ge.research.rack.analysis.utils.ReportViewUtils;
import com.ge.research.rack.do178c.oem.DataProcessor;
import com.ge.research.rack.do178c.structures.Objective;
import com.ge.research.rack.do178c.structures.PsacNode;
import com.ge.research.rack.do178c.utils.PsacNodeUtils;
import com.ge.research.rack.do178c.viewManagers.ReportViewsManager;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Saswata Paul
 */
public class ReportMainViewHandlerNew extends com.ge.research.rack.analysis.handlers.MainViewHandler
        implements Initializable {

    @Override
    protected String bucketLabel() {
        return new String("Table ");
    }

    /**
     * Create the label for table listview for a given table object
     *
     * @param tabObj
     * @return
     */
    private Label getTableLabel(PlanTable<Objective> tabObj) {

        double passPercent =
                ((double) tabObj.getNumObjPassed()
                                / (tabObj.getNumObjPassed()
                                        + tabObj.getNumObjFailed()
                                        + tabObj.getNumObjPartial()
                                        + tabObj.getNumObjNoData()))
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
    @Override
    protected void getReportData() throws Exception {
        // Create tempdir to store the csv files
        String tempDir = CustomFileUtils.getRackDir();
        DataProcessor rdpObj = new DataProcessor();
        ReportViewsManager.reportDataObj = (PsacNode) rdpObj.getData(tempDir);
    }

    /** populates the chart */
    @Override
    protected void populateChartStatus() {
        super.populateChartStatus();

        // to store the highest value for scaling
        int high = -1;

        // Group bars by table id

        XYChart.Series<String, Integer> passData = new XYChart.Series<String, Integer>();
        passData.setName("Complete");
        List<Data<String, Integer>> passBars = new ArrayList<Data<String, Integer>>();
        for (PlanTable<Objective> tabObj :
                ((PsacNode) ReportViewsManager.reportDataObj).getReportTables()) {
            //            Data passBar = new XYChart.Data(tabObj.getId(), tabObj.getNumObjPassed());
            Data<String, Integer> passBar =
                    ReportViewUtils.createIntDataBar(tabObj.getId(), tabObj.getNumObjPassed());
            passData.getData().add(passBar);
            passBars.add(passBar);
            if (high < tabObj.getNumObjPassed()) {
                high = tabObj.getNumObjPassed() + 1;
            }
        }

        XYChart.Series<String, Integer> failData = new XYChart.Series<String, Integer>();
        failData.setName("Complete");
        List<Data<String, Integer>> failBars = new ArrayList<Data<String, Integer>>();
        for (PlanTable<Objective> tabObj :
                ((PsacNode) ReportViewsManager.reportDataObj).getReportTables()) {
            //            Data failBar = new XYChart.Data(tabObj.getId(), tabObj.getNumObjFailed());
            Data<String, Integer> failBar =
                    ReportViewUtils.createIntDataBar(tabObj.getId(), tabObj.getNumObjFailed());
            failData.getData().add(failBar);
            failBars.add(failBar);
            if (high < tabObj.getNumObjFailed()) {
                high = tabObj.getNumObjFailed() + 1;
            }
        }

        XYChart.Series<String, Integer> partialData = new XYChart.Series<String, Integer>();
        partialData.setName("Partial Data");
        List<Data<String, Integer>> partialBars = new ArrayList<Data<String, Integer>>();
        for (PlanTable<Objective> tabObj :
                ((PsacNode) ReportViewsManager.reportDataObj).getReportTables()) {
            //            Data partialBar = new XYChart.Data(tabObj.getId(),
            // tabObj.getNumObjPartial());
            Data<String, Integer> partialBar =
                    ReportViewUtils.createIntDataBar(tabObj.getId(), tabObj.getNumObjPartial());
            partialData.getData().add(partialBar);
            partialBars.add(partialBar);
            if (high < tabObj.getNumObjPartial()) {
                high = tabObj.getNumObjPartial() + 1;
            }
        }

        XYChart.Series<String, Integer> noData = new XYChart.Series<String, Integer>();
        noData.setName("No Data");
        List<Data<String, Integer>> noBars = new ArrayList<Data<String, Integer>>();
        for (PlanTable<Objective> tabObj :
                ((PsacNode) ReportViewsManager.reportDataObj).getReportTables()) {
            //            Data noBar = new XYChart.Data(tabObj.getId(), tabObj.getNumObjNodata());
            Data<String, Integer> noBar =
                    ReportViewUtils.createIntDataBar(tabObj.getId(), tabObj.getNumObjNoData());
            noData.getData().add(noBar);
            noBars.add(noBar);
            if (high < tabObj.getNumObjNoData()) {
                high = tabObj.getNumObjNoData() + 1;
            }
        }

        chartStatus.getData().add(passData);
        chartStatus.getData().add(failData);
        chartStatus.getData().add(noData);
        chartStatus.getData().add(partialData);

        for (Data<String, Integer> bar : passBars) {
            bar.getNode().getStyleClass().add("color-passed");
            ReportViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }
        for (Data<String, Integer> bar : failBars) {
            bar.getNode().getStyleClass().add("color-failed");
            ReportViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }
        for (Data<String, Integer> bar : partialBars) {
            bar.getNode().getStyleClass().add("color-partial-data");
            ReportViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }
        for (Data<String, Integer> bar : noBars) {
            bar.getNode().getStyleClass().add("color-no-data");
            ReportViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }

        // scaling
        // *** This can help make integral ticks on Y axis ***
        yAxisChartStatus.setLowerBound(0);
        yAxisChartStatus.setUpperBound(high);
        yAxisChartStatus.setTickUnit(1);
    }

    /**
     * populates the listview
     *
     * @param key
     */
    @Override
    protected void populateLists(String key) {
        super.populateLists(key);

        for (PlanTable<Objective> tabObj :
                ((PsacNode) ReportViewsManager.reportDataObj).getReportTables()) {
            Label tabLabel = getTableLabel(tabObj);
            if (key.equalsIgnoreCase("All")) {
                listBuckets.getItems().add(tabLabel);
            } else if (key.equalsIgnoreCase("Passed") && tabObj.isPassed()) {
                listBuckets.getItems().add(tabLabel);
            } else if (key.equalsIgnoreCase("Failed") && !tabObj.isPassed()) {
                listBuckets.getItems().add(tabLabel);
            } else if (key.equalsIgnoreCase("Inconclusive") && tabObj.isNoData()) {
                //    				actLabel.setVisible(true);
                listBuckets.getItems().add(tabLabel);
            }
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        try {
            final ImageView icon = ReportViewUtils.loadGeIcon();
            icon.setPreserveRatio(true);
            headerLabel.setGraphic(icon);
        } catch (Exception e) {
        }

        // disable the display environments
        chartStatus.setDisable(true);
        yAxisChartStatus.setDisable(true);
        gridPaneLegend.setDisable(true);
        listBuckets.setDisable(true);
        labelSwInfo.setVisible(false);
        labelSwInfo.setText("");
        enableWaitMessage(false);

        // set properties
        // set SINGLE Selection Model for ListView of Tables
        listBuckets.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // If the report variables have already been initialized
        if (((PsacNode) ReportViewsManager.reportDataObj).getReportTables() != null) {
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
        initialize(null, null);

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
        Label selectedLabel = listBuckets.getSelectionModel().getSelectedItem();

        if (selectedLabel != null) {

            // get selection
            String selectedTable = getBucketIdFromLabelText(selectedLabel.getText());
            System.out.println("The selected Table: " + selectedTable);

            if (!PsacNodeUtils.getTableById(
                            (PsacNode) ReportViewsManager.reportDataObj, selectedTable)
                    .isNoData()) {
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
