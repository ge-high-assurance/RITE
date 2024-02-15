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

import com.ge.research.rack.analysis.handlers.TableViewHandler;
import com.ge.research.rack.analysis.structures.PlanObjective;
import com.ge.research.rack.analysis.structures.PlanTable;
import com.ge.research.rack.analysis.utils.CustomStringUtils;
import com.ge.research.rack.analysis.utils.ReportViewUtils;
import com.ge.research.rack.do178c.structures.PsacNode;
import com.ge.research.rack.do178c.utils.PsacNodeUtils;
import com.ge.research.rack.do178c.viewManagers.ReportViewsManager;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.List;

/**
 * @author Saswata Paul
 */
public class ReportTableViewHandlerNew extends TableViewHandler implements Initializable {

    /**
     * Creates and returns a label for the objective lists
     *
     * @param objObj
     * @return
     */
    @Override
    public Label getObjectiveLabel(PlanObjective objObj) {

        Label objLabel = super.getObjectiveLabel(objObj);

        // double passPercent =
        //        ((double) objObj.getNumPassed()
        //                        / (objObj.getNumPassed()
        //                                + objObj.getNumFailed()
        //                                + objObj.getNumNoData()))
        //                * 100.00;

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

    /** Populates the objective status chart */
    @Override
    public void populateObjStatusChart() {
        super.populateObjStatusChart();

        List<Integer> artStats = ReportViewUtils.getTableArtifactStats(currentTableObject);

        //        Data docBar = new XYChart.Data("Documents", artStats.get(0));
        //        Data reqBar = new XYChart.Data("Requirements", artStats.get(1));
        //        Data hzrdBar = new XYChart.Data("Hazards", artStats.get(2));
        //        Data tstBar = new XYChart.Data("Test Results", artStats.get(3));
        //        Data logBar = new XYChart.Data("Review Logs", artStats.get(4));
        //        Data anlsBar = new XYChart.Data("Analyses", artStats.get(5));

        Data<String, Integer> docBar =
                ReportViewUtils.createIntDataBar("Documents", artStats.get(0));
        Data<String, Integer> reqBar =
                ReportViewUtils.createIntDataBar("Requirements", artStats.get(1));
        Data<String, Integer> hzrdBar =
                ReportViewUtils.createIntDataBar("Hazards", artStats.get(2));
        Data<String, Integer> tstBar =
                ReportViewUtils.createIntDataBar("Test Results", artStats.get(3));
        Data<String, Integer> logBar =
                ReportViewUtils.createIntDataBar("Review Logs", artStats.get(4));
        Data<String, Integer> anlsBar =
                ReportViewUtils.createIntDataBar("Analyses", artStats.get(5));

        XYChart.Series<String, Integer> tableStat = new XYChart.Series<String, Integer>();

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

    @Override
    protected PlanTable getCurrentTableObject(String tableID) {
        return PsacNodeUtils.getTableById((PsacNode) ReportViewsManager.reportDataObj, tableID);
    }

    @Override
    protected Color getTableInfoFillColor() {
        return ReportViewUtils.getTableColor(currentTableObject);
    }

    @Override
    protected String getMainViewPath() {
        return "resources/fxml/do178c/DO178CMainView.fxml";
    }

    @Override
    protected void switchObjectiveView(Label selectedLabel) {
        // get selection
        String selectedObjective =
                CustomStringUtils.separateElementIdFromDescription(selectedLabel.getText());
        System.out.println("The selected Objective: " + selectedObjective);

        // switch to objective view only if the objective has some data
        if (!PsacNodeUtils.getObjectiveById(
                        (PsacNode) ReportViewsManager.reportDataObj,
                        currentTableId,
                        selectedObjective)
                .isNoData()) {
            // Set the stage with the other fxml
            FXMLLoader objectiveViewLoader =
                    ReportViewsManager.setNewFxmlToStage(
                            "resources/fxml/do178c/DO178CObjectiveView.fxml");

            // initialize variables in the ReportTableView page
            ReportObjectiveViewHandlerNew objectiveViewLoaderClassObj =
                    objectiveViewLoader.getController();
            objectiveViewLoaderClassObj.prepareView(currentTableId, selectedObjective);
        }
    }

    @Override
    protected void setNewFxmlToStage(String path) {
        ReportViewsManager.setNewFxmlToStage(path);
    }

    @Override
    protected void increaseGlobalFontSize(boolean enable) {
        System.out.println("increase font btn pressed");
        ReportViewsManager.increaseGlobalFontSize(enable);
    }
}
