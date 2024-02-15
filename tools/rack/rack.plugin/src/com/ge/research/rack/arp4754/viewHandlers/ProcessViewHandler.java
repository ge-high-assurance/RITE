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

import com.ge.research.rack.analysis.handlers.TableViewHandler;
import com.ge.research.rack.analysis.structures.PlanObjective;
import com.ge.research.rack.analysis.structures.PlanTable;
import com.ge.research.rack.analysis.utils.CustomStringUtils;
import com.ge.research.rack.arp4754.structures.DAPlan;
import com.ge.research.rack.arp4754.utils.DAPlanUtils;
import com.ge.research.rack.arp4754.utils.ViewUtils;
import com.ge.research.rack.arp4754.viewManagers.Arp4754ViewsManager;

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
public class ProcessViewHandler extends TableViewHandler implements Initializable {

    /**
     * Creates and returns a label for the objective lists
     *
     * @param objObj
     * @return
     */
    @Override
    public Label getObjectiveLabel(PlanObjective objObj) {

        Label objLabel = super.getObjectiveLabel(objObj);
        if (objObj.getMetrics().equalsIgnoreCase("TBD")) {
            objLabel.setText(
                    objObj.getId()
                            + ": "
                            + objObj.getDescription().replace("\"", "")
                            + " ("
                            + objObj.getMetrics()
                            + ")");
            objLabel.setTextFill(Color.LIGHTGREY);
        } else {
            objLabel.setText(
                    objObj.getId()
                            + ": "
                            + objObj.getDescription().replace("\"", "")
                            + " ("
                            + String.format("%.2f", objObj.getComplianceStatus())
                            + "% complete)");
            objLabel.setTextFill(ViewUtils.getObjectiveColor(objObj));
        }

        return objLabel;
    }

    /** Populates the objective status chart */
    @Override
    public void populateObjStatusChart() {
        super.populateObjStatusChart();

        List<Integer> artStats = ViewUtils.getProcessArtifactStats(currentTableObject);

        Data<String, Integer> docBar = ViewUtils.createIntDataBar("Documents", artStats.get(0));
        Data<String, Integer> reqBar = ViewUtils.createIntDataBar("Requirements", artStats.get(1));
        Data<String, Integer> itemBar = ViewUtils.createIntDataBar("Items", artStats.get(2));
        Data<String, Integer> interfaceBar =
                ViewUtils.createIntDataBar("Interfaces", artStats.get(3));
        Data<String, Integer> systemBar = ViewUtils.createIntDataBar("Systems", artStats.get(4));
        Data<String, Integer> verificationBar =
                ViewUtils.createIntDataBar("Verifications", artStats.get(5));
        Data<String, Integer> testBar = ViewUtils.createIntDataBar("Tests", artStats.get(6));
        Data<String, Integer> reviewBar = ViewUtils.createIntDataBar("Reviews", artStats.get(7));
        Data<String, Integer> analysisBar = ViewUtils.createIntDataBar("Analyses", artStats.get(8));

        XYChart.Series<String, Integer> tableStat = new XYChart.Series<String, Integer>();

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
        ViewUtils.assignTooltip(docBar.getNode(), docBar.getYValue().toString());
        reqBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ViewUtils.assignTooltip(reqBar.getNode(), reqBar.getYValue().toString());
        itemBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ViewUtils.assignTooltip(itemBar.getNode(), itemBar.getYValue().toString());
        interfaceBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ViewUtils.assignTooltip(interfaceBar.getNode(), interfaceBar.getYValue().toString());
        systemBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ViewUtils.assignTooltip(systemBar.getNode(), systemBar.getYValue().toString());
        verificationBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ViewUtils.assignTooltip(verificationBar.getNode(), verificationBar.getYValue().toString());
        testBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ViewUtils.assignTooltip(testBar.getNode(), testBar.getYValue().toString());
        reviewBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ViewUtils.assignTooltip(reviewBar.getNode(), reviewBar.getYValue().toString());
        analysisBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
        ViewUtils.assignTooltip(analysisBar.getNode(), analysisBar.getYValue().toString());

        // scaling
        int maxScale = Collections.max(artStats);
        // *** This can help make integral ticks on Y axis ***
        yAxisChartObjStatus.setLowerBound(0);
        yAxisChartObjStatus.setUpperBound(maxScale);
        yAxisChartObjStatus.setTickUnit(1);
    }

    @Override
    protected List<PlanObjective> getCurrentTableObjectives() {
        return DAPlanUtils.sortObjectiveList(currentTableObject.getTabObjectives());
    }

    @Override
    protected PlanTable getCurrentTableObject(String tableID) {
        return DAPlanUtils.getProcessObjectFromList(
                ((DAPlan) Arp4754ViewsManager.reportDataObj).getTables(), tableID);
    }

    @Override
    protected Color getTableInfoFillColor() {
        return ViewUtils.getProcessColor(currentTableObject);
    }

    @Override
    protected String getMainViewPath() {
        return "resources/fxml/arp4754/MainView.fxml";
    }

    @Override
    protected void switchObjectiveView(Label selectedLabel) {
        // get selection
        String selectedObjective =
                CustomStringUtils.separateElementIdFromDescription(selectedLabel.getText());
        System.out.println("The selected Objective: " + selectedObjective);

        // switch to objective view only if the objective has some data
        if (!DAPlanUtils.getObjectiveObjectFromList(
                        currentTableObject.getTabObjectives(), selectedObjective)
                .isNoData()) {
            // Set the stage with the other fxml
            FXMLLoader objectiveViewLoader =
                    Arp4754ViewsManager.setNewFxmlToStage(
                            "resources/fxml/arp4754/ObjectiveView.fxml");

            // initialize variables in the ReportTableView page
            ObjectiveViewHandler objectiveViewLoaderClassObj = objectiveViewLoader.getController();
            objectiveViewLoaderClassObj.prepareView(currentTableId, selectedObjective);
        }
    }

    @Override
    protected void setNewFxmlToStage(String path) {
        Arp4754ViewsManager.setNewFxmlToStage(path);
    }

    @Override
    protected void increaseGlobalFontSize(boolean enable) {
        System.out.println("increase font btn pressed");
        Arp4754ViewsManager.increaseGlobalFontSize(enable);
    }
}
