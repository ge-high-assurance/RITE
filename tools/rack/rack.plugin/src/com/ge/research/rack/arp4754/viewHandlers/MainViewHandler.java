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

import com.ge.research.rack.analysis.structures.PlanObjective;
import com.ge.research.rack.analysis.structures.PlanTable;
import com.ge.research.rack.analysis.utils.CustomFileUtils;
import com.ge.research.rack.analysis.utils.CustomStringUtils;
import com.ge.research.rack.arp4754.logic.DataProcessor;
import com.ge.research.rack.arp4754.structures.DAPlan;
import com.ge.research.rack.arp4754.utils.DAPlanUtils;
import com.ge.research.rack.arp4754.utils.ViewUtils;
import com.ge.research.rack.arp4754.viewManagers.Arp4754ViewsManager;

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
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Saswata Paul
 */
public class MainViewHandler extends com.ge.research.rack.analysis.handlers.MainViewHandler
        implements Initializable {

    @Override
    protected String bucketLabel() {
        return new String("Process-");
    }

    /**
     * Create the label for table listview for a given process object
     *
     * @param procObj
     * @return
     */
    protected Label getProcessLabel(PlanTable<PlanObjective> procObj) {

        Label procLabel = new Label();
        procLabel.setStyle("-fx-font-weight: bold;");

        if (procObj.getMetrics().equalsIgnoreCase("TBD")) {
            procLabel.setText(
                    procObj.getId()
                            + ": "
                            + procObj.getDescription().replace("\"", "")
                            + " ("
                            + procObj.getMetrics()
                            + ")");
            procLabel.setTextFill(Color.LIGHTGREY);
        } else {
            procLabel.setText(
                    procObj.getId()
                            + ": "
                            + procObj.getDescription().replace("\"", "")
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
    @Override
    protected void getReportData() throws Exception {
        // Create tempdir to store the csv files
        String tempDir = CustomFileUtils.getRackDir();
        DataProcessor rdpObj = new DataProcessor();
        Arp4754ViewsManager.reportDataObj = (DAPlan) rdpObj.getData(tempDir);
    }

    @Override
    protected String swLabel() {
        // default value; Subclass to override
        return "System ID: " + Arp4754ViewsManager.reportDataObj.getSystem();
    }

    /** populates the chart */
    @Override
    protected void populateChartStatus() {
        super.populateChartStatus();

        // to store the highest value for scaling
        int high = -1;

        // get list of processes
        List<PlanTable<PlanObjective>> allProcessObjs =
                DAPlanUtils.sortProcessList(Arp4754ViewsManager.reportDataObj.getProcesses());

        // Group bars by table id
        XYChart.Series<String, Integer> passData = new XYChart.Series<String, Integer>();
        passData.setName("Complete");
        List<Data<String, Integer>> passBars = new ArrayList<Data<String, Integer>>();
        for (PlanTable<PlanObjective> procObj : allProcessObjs) {
            Data<String, Integer> passBar =
                    ViewUtils.createIntDataBar(procObj.getId(), procObj.getNumObjPassed());
            passData.getData().add(passBar);
            passBars.add(passBar);
            if (high < procObj.getNumObjPassed()) {
                high = procObj.getNumObjPassed() + 1;
            }
        }

        XYChart.Series<String, Integer> failData = new XYChart.Series<String, Integer>();
        failData.setName("Complete");
        List<Data<String, Integer>> failBars = new ArrayList<Data<String, Integer>>();
        for (PlanTable<PlanObjective> procObj : allProcessObjs) {
            Data<String, Integer> failBar =
                    ViewUtils.createIntDataBar(
                            procObj.getId(),
                            (procObj.getTabObjectives().size()
                                    - procObj.getNumObjPassed()
                                    - procObj.getNumObjPartial()
                                    - procObj.getNumObjNoData()));
            failData.getData().add(failBar);
            failBars.add(failBar);
            if (high
                    < (procObj.getTabObjectives().size()
                            - procObj.getNumObjPassed()
                            - procObj.getNumObjPartial()
                            - procObj.getNumObjNoData())) {
                high =
                        (procObj.getTabObjectives().size()
                                        - procObj.getNumObjPassed()
                                        - procObj.getNumObjPartial()
                                        - procObj.getNumObjNoData())
                                + 1;
            }
        }

        XYChart.Series<String, Integer> partialData = new XYChart.Series<String, Integer>();
        partialData.setName("Partial Data");
        List<Data<String, Integer>> partialBars = new ArrayList<Data<String, Integer>>();
        for (PlanTable<PlanObjective> procObj : allProcessObjs) {
            Data<String, Integer> partialBar =
                    ViewUtils.createIntDataBar(
                            procObj.getId(), procObj.getNumObjPartial());
            partialData.getData().add(partialBar);
            partialBars.add(partialBar);
            if (high < procObj.getNumObjPartial()) {
                high = procObj.getNumObjPartial() + 1;
            }
        }

        XYChart.Series<String, Integer> noData = new XYChart.Series<String, Integer>();
        noData.setName("No Data");
        List<Data<String, Integer>> noBars = new ArrayList<Data<String, Integer>>();
        for (PlanTable<PlanObjective> procObj : allProcessObjs) {
            Data<String, Integer> noBar =
                    ViewUtils.createIntDataBar(procObj.getId(), procObj.getNumObjNoData());
            noData.getData().add(noBar);
            noBars.add(noBar);
            if (high < procObj.getNumObjNoData()) {
                high = procObj.getNumObjNoData() + 1;
            }
        }

        chartStatus.getData().add(passData);
        chartStatus.getData().add(failData);
        chartStatus.getData().add(noData);
        chartStatus.getData().add(partialData);

        for (Data<String, Integer> bar : passBars) {
            bar.getNode().getStyleClass().add("color-passed");
            ViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }
        for (Data<String, Integer> bar : failBars) {
            bar.getNode().getStyleClass().add("color-failed");
            ViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }
        for (Data<String, Integer> bar : partialBars) {
            bar.getNode().getStyleClass().add("color-partial-data");
            ViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
        }
        for (Data<String, Integer> bar : noBars) {
            bar.getNode().getStyleClass().add("color-no-data");
            ViewUtils.assignTooltip(bar.getNode(), bar.getYValue().toString());
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

        // List<Label> labelList = new ArrayList<Label>();
        // List<Label> sortedLabelList = new ArrayList<Label>();

        // get list of processes
        List<PlanTable<PlanObjective>> allProcessObjs =
                DAPlanUtils.sortProcessList(Arp4754ViewsManager.reportDataObj.getProcesses());

        for (PlanTable<PlanObjective> procObj : allProcessObjs) {
            Label procLabel = getProcessLabel(procObj);
            int position = Integer.parseInt(getBucketIdFromLabelText(procLabel.getText())) - 1;
            System.out.println("Position " + position + "->" + procLabel.getText());
            if (key.equalsIgnoreCase("All")) {
                // listProcess.getItems().add(position, procLabel);
                // labelList.add(procLabel);
                listBuckets.getItems().add(procLabel);
            } else if (key.equalsIgnoreCase("Passed") && procObj.isPassed()) {
                // listProcess.getItems().add(position, procLabel);
                // labelList.add(procLabel);
                listBuckets.getItems().add(procLabel);
            } else if (key.equalsIgnoreCase("Failed") && !procObj.isPassed()) {
                // listProcess.getItems().add(position, procLabel);
                // labelList.add(procLabel);
                listBuckets.getItems().add(procLabel);
            } else if (key.equalsIgnoreCase("Inconclusive") && procObj.isNoData()) {
                // actLabel.setVisible(true);
                // listProcess.getItems().add(tabLabel);
                // listProcess.getItems().add(position, procLabel);
                // labelList.add(procLabel);
                listBuckets.getItems().add(procLabel);
            }
        }

        // sort the list of labels
        // for(int i = 0; i<labelList.size();i++) {
        // find the appropriate label for this index
        //    for(Label label : labelList) {
        //        if(i == (Integer.parseInt(getProcessIdFromLabelText(label.getText()))-1)){
        //            sortedLabelList.add(label);
        //        }
        //    }
        // }

        // add the labels to listview
        // listProcess.getItems().addAll(sortedLabelList);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        super.initialize(arg0, arg1);

        try {
            final ImageView icon = ViewUtils.loadGeIcon();
            icon.setPreserveRatio(true);
            headerLabel.setGraphic(icon);
        } catch (Exception e) {
            // Do nothing
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
        // set SINGLE Selection Model for ListView of Processes
        listBuckets.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // If the report variables have already been initialized
        if (Arp4754ViewsManager.reportDataObj != null
                && Arp4754ViewsManager.reportDataObj.getProcesses() != null) {
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
        Label selectedLabel = listBuckets.getSelectionModel().getSelectedItem();

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
