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
package com.ge.research.rack.arp4754.viewHandlers;

import com.ge.research.rack.arp4754.logic.DataProcessor;
import com.ge.research.rack.arp4754.structures.DAPlan;
import com.ge.research.rack.arp4754.utils.DAPlanUtils;
import com.ge.research.rack.arp4754.utils.ViewUtils;
import com.ge.research.rack.arp4754.viewManagers.Arp4754ViewsManager;
import com.ge.research.rack.autoGsn.utils.CustomFileUtils;
import com.ge.research.rack.autoGsn.utils.CustomStringUtils;
import com.ge.research.rack.do178c.utils.ReportViewUtils;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.paint.Color;

/**
 * @author Saswata Paul
 */
public class MainViewHandler {

    // -------- FXML GUI variables below --------------
    @FXML private Label headerLabel;

    @FXML private Button btnFetch;
    @FXML private Button btnFontInc;
    @FXML private Button btnFontDec;

    @FXML private Label labelSwInfo;

    @FXML private Label labelWait;
    @FXML private ProgressIndicator progInd;

    @FXML private BarChart chartProcessStatus;
    @FXML private NumberAxis yAxisChartProcessStatus;
    @FXML private GridPane gridPaneLegend;

    @FXML private ListView<Label> listProcess;

    // ----------

    /**
     * Given "TableAx: yzaysy", returns "Ax"
     *
     * @param procLabelText
     * @return
     */
    private String getProcessIdFromLabelText(String procLabelText) {
        String Processx = CustomStringUtils.separateElementIdFromDescription(procLabelText);
        String x = Processx.replace("Process-", "");
        return x;
    }

    /**
     * Create the label for table listview for a given process object
     *
     * @param procObj
     * @return
     */
    private Label getProcessLabel(DAPlan.Process procObj) {

        Label procLabel = new Label();
        procLabel.setStyle("-fx-font-weight: bold;");

        if (procObj.getMetrics().equalsIgnoreCase("TBD")) {
            procLabel.setText(
                    procObj.getId()
                            + ": "
                            + procObj.getDesc().replace("\"", "")
                            + " ("
                            + procObj.getMetrics()
                            + ")");
            procLabel.setTextFill(Color.LIGHTGREY);
        } else {
            procLabel.setText(
                    procObj.getId()
                            + ": "
                            + procObj.getDesc().replace("\"", "")
                            + " ("
                            + String.format("%.2f", procObj.getComplianceStatus())
                            + "% objectives passed)");
            procLabel.setTextFill(ViewUtils.getProcessColor(procObj));
        }

        return procLabel;
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
        Arp4754ViewsManager.reportDataObj = rdpObj.getPlanData(tempDir);
        ;
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

        String swInfo = "System ID: " + Arp4754ViewsManager.reportDataObj.getSystem();
        labelSwInfo.setText(swInfo);
    }

    /** populates the chart */
    private void populateChartProcessStatus() {
        // clear the chart
        chartProcessStatus.getData().clear();

        // enable the chart
        chartProcessStatus.setDisable(false);
        yAxisChartProcessStatus.setDisable(false);
        gridPaneLegend.setDisable(false);

        // to store the highest value for scaling
        int high = -1;

        // get list of processes
        List<DAPlan.Process> allProcessObjs =
                DAPlanUtils.sortProcessList(Arp4754ViewsManager.reportDataObj.getProcesses());

        // Group bars by table id

        XYChart.Series passData = new XYChart.Series();
        passData.setName("Complete");
        List<Data> passBars = new ArrayList<Data>();
        for (DAPlan.Process procObj : allProcessObjs) {
            Data passBar =
                    ReportViewUtils.createIntDataBar(
                            procObj.getId(), procObj.getNumObjectivesPassed());
            passData.getData().add(passBar);
            passBars.add(passBar);
            if (high < procObj.getNumObjectivesPassed()) {
                high = procObj.getNumObjectivesPassed() + 1;
            }
        }

        XYChart.Series failData = new XYChart.Series();
        failData.setName("Complete");
        List<Data> failBars = new ArrayList<Data>();
        for (DAPlan.Process procObj : allProcessObjs) {
            Data failBar =
                    ReportViewUtils.createIntDataBar(
                            procObj.getId(),
                            (procObj.getObjectives().size()
                                    - procObj.getNumObjectivesPassed()
                                    - procObj.getNumObjectivesPartialData()
                                    - procObj.getNumObjectivesNoData()));
            failData.getData().add(failBar);
            failBars.add(failBar);
            if (high
                    < (procObj.getObjectives().size()
                            - procObj.getNumObjectivesPassed()
                            - procObj.getNumObjectivesPartialData()
                            - procObj.getNumObjectivesNoData())) {
                high =
                        (procObj.getObjectives().size()
                                        - procObj.getNumObjectivesPassed()
                                        - procObj.getNumObjectivesPartialData()
                                        - procObj.getNumObjectivesNoData())
                                + 1;
            }
        }

        XYChart.Series partialData = new XYChart.Series();
        partialData.setName("Partial Data");
        List<Data> partialBars = new ArrayList<Data>();
        for (DAPlan.Process procObj : allProcessObjs) {
            Data partialBar =
                    ReportViewUtils.createIntDataBar(
                            procObj.getId(), procObj.getNumObjectivesPartialData());
            partialData.getData().add(partialBar);
            partialBars.add(partialBar);
            if (high < procObj.getNumObjectivesPartialData()) {
                high = procObj.getNumObjectivesPartialData() + 1;
            }
        }

        XYChart.Series noData = new XYChart.Series();
        noData.setName("No Data");
        List<Data> noBars = new ArrayList<Data>();
        for (DAPlan.Process procObj : allProcessObjs) {
            Data noBar =
                    ReportViewUtils.createIntDataBar(
                            procObj.getId(), procObj.getNumObjectivesNoData());
            noData.getData().add(noBar);
            noBars.add(noBar);
            if (high < procObj.getNumObjectivesNoData()) {
                high = procObj.getNumObjectivesNoData() + 1;
            }
        }

        chartProcessStatus.getData().add(passData);
        chartProcessStatus.getData().add(failData);
        chartProcessStatus.getData().add(noData);
        chartProcessStatus.getData().add(partialData);

        for (Data bar : passBars) {
            bar.getNode().getStyleClass().add("color-passed");
            ReportViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }
        for (Data bar : failBars) {
            bar.getNode().getStyleClass().add("color-failed");
            ReportViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }
        for (Data bar : partialBars) {
            bar.getNode().getStyleClass().add("color-partial-data");
            ReportViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }
        for (Data bar : noBars) {
            bar.getNode().getStyleClass().add("color-no-data");
            ReportViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }

        // scaling
        // *** This can help make integral ticks on Y axis ***
        yAxisChartProcessStatus.setLowerBound(0);
        yAxisChartProcessStatus.setUpperBound(high);
        yAxisChartProcessStatus.setTickUnit(1);
    }

    /**
     * populates the listview
     *
     * @param key
     */
    private void populateListProcesses(String key) {
        // clear old data
        listProcess.getItems().clear();

        // enable elements
        listProcess.setDisable(false);

        //        List<Label> labelList = new ArrayList<Label>();
        //        List<Label> sortedLabelList = new ArrayList<Label>();

        // get list of processes
        List<DAPlan.Process> allProcessObjs =
                DAPlanUtils.sortProcessList(Arp4754ViewsManager.reportDataObj.getProcesses());

        for (DAPlan.Process procObj : allProcessObjs) {
            Label procLabel = getProcessLabel(procObj);
            int position = Integer.parseInt(getProcessIdFromLabelText(procLabel.getText())) - 1;
            System.out.println("Position " + position + "->" + procLabel.getText());
            if (key.equalsIgnoreCase("All")) {
                //                listProcess.getItems().add(position, procLabel);
                //            	labelList.add(procLabel);
                listProcess.getItems().add(procLabel);
            } else if (key.equalsIgnoreCase("Passed") && procObj.isPassed()) {
                //                listProcess.getItems().add(position, procLabel);
                //            	labelList.add(procLabel);
                listProcess.getItems().add(procLabel);
            } else if (key.equalsIgnoreCase("Failed") && !procObj.isPassed()) {
                //                listProcess.getItems().add(position, procLabel);
                //            	labelList.add(procLabel);
                listProcess.getItems().add(procLabel);
            } else if (key.equalsIgnoreCase("Inconclusive") && procObj.isNoData()) {
                //    				actLabel.setVisible(true);
                //                listProcess.getItems().add(tabLabel);
                //              listProcess.getItems().add(position, procLabel);
                //            	labelList.add(procLabel);
                listProcess.getItems().add(procLabel);
            }
        }

        //        // sort the list of labels
        //        for(int i = 0; i<labelList.size();i++) {
        //        	// find the appropriate label for this index
        //        	for(Label label : labelList) {
        //        		if(i == (Integer.parseInt(getProcessIdFromLabelText(label.getText()))-1)){
        //        			sortedLabelList.add(label);
        //        		}
        //        	}
        //        }

        //        // add the labels to listview
        //        listProcess.getItems().addAll(sortedLabelList);

    }

    /** Calls necessary functions to populate view elements from data */
    private void populateViewElements() {
        populateLabelSwInfo();

        populateChartProcessStatus();

        populateListProcesses("All");
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
        chartProcessStatus.setDisable(true);
        yAxisChartProcessStatus.setDisable(true);
        gridPaneLegend.setDisable(true);
        listProcess.setDisable(true);
        labelSwInfo.setVisible(false);
        labelSwInfo.setText("");
        enableWaitMessage(false);

        // set properties
        // set SINGLE Selection Model for ListView of Processes
        listProcess.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // If the report variables have already been initialized
        if (Arp4754ViewsManager.reportDataObj.getProcesses() != null) {
            populateViewElements();

        } else {
            // Initialize the variables
            Arp4754ViewsManager.initializeViewVariables();
        }

        enableFetchButton();
    }

    @FXML
    private void btnFetchAction(ActionEvent event) throws Exception {
        // initialize the manager variables again
        Arp4754ViewsManager.initializeViewVariables();

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
        Arp4754ViewsManager.increaseGlobalFontSize(true);
    }

    @FXML
    private void btnFontDecAction(ActionEvent event) throws Exception {
        System.out.println("decrease font btn pressed");
        Arp4754ViewsManager.increaseGlobalFontSize(false);
    }

    @FXML
    private void listProcessSelectionAction(MouseEvent event) {

        // The selected label
        Label selectedLabel = listProcess.getSelectionModel().getSelectedItem();

        if (selectedLabel != null) {

            // get selection
            String selectedProc =
                    CustomStringUtils.separateElementIdFromDescription(selectedLabel.getText());
            System.out.println("The selected Table: " + selectedProc);

            if (!DAPlanUtils.getProcessObjectFromList(
                            Arp4754ViewsManager.reportDataObj.getProcesses(), selectedProc)
                    .isNoData()) {
                // Set the stage with the other fxml
                FXMLLoader processViewLoader =
                        Arp4754ViewsManager.setNewFxmlToStage(
                                "resources/fxml/arp4754/ProcessView.fxml");

                // initialize variables in the ReportTableView page
                ProcessViewHandler processViewLoaderClassObj = processViewLoader.getController();
                processViewLoaderClassObj.prepareView(selectedProc);
            }
        }
    }
}
