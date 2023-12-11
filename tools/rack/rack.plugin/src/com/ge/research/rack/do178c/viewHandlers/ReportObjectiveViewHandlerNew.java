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

import com.ge.research.rack.do178c.structures.DataItem;
import com.ge.research.rack.do178c.structures.PsacNode;
import com.ge.research.rack.do178c.structures.PsacNode.Activity;
import com.ge.research.rack.do178c.structures.Requirement;
import com.ge.research.rack.do178c.structures.ReviewLog;
import com.ge.research.rack.do178c.structures.SwComponent;
import com.ge.research.rack.do178c.structures.Test;
import com.ge.research.rack.do178c.utils.LogicUtils;
import com.ge.research.rack.do178c.utils.PsacNodeUtils;
import com.ge.research.rack.do178c.utils.ReportViewUtils;
import com.ge.research.rack.do178c.viewManagers.ReportViewsManager;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * @author Saswata Paul
 */
public class ReportObjectiveViewHandlerNew {

    private String currentTableId;

    private PsacNode.Table currentTableObject;

    private String currentObjId;

    private PsacNode.Objective currentObjObject;

    private String reqChildrenRelation;

    private String tstChildrenRelation;

    private String swCompChildrenRelation;

    // -------- FXML GUI variables below --------------
    @FXML private Label headerLabel;

    @FXML private Label labelTableInfo;
    @FXML private Label labelObjInfo;

    @FXML private Tab tabAct;
    @FXML private ListView<Label> actList;

    @FXML private Tab tabDoc;
    @FXML private ListView<Label> docList;

    @FXML private Tab tabReq;
    @FXML private ListView<Label> reqList;
    @FXML private ComboBox comboReq;
    @FXML private TextField searchReq;
    @FXML private BarChart reqChart;
    @FXML private NumberAxis yAxisReqChart;
    @FXML private Label reqChildrenLabel;
    @FXML private ListView reqChildrenList;

    @FXML private Tab tabHzrd;

    @FXML private Tab tabTest;
    @FXML private ListView<Label> tstList;
    @FXML private ComboBox comboTst;
    @FXML private TextField searchTst;
    @FXML private BarChart tstChart;
    @FXML private NumberAxis yAxisTstChart;
    @FXML private Label tstChildrenLabel;
    @FXML private ListView tstChildrenList;

    @FXML private Tab tabAnls;

    @FXML private Tab tabRev;
    @FXML private ListView<Label> revList;
    @FXML private ComboBox comboRev;
    @FXML private BarChart revChart;
    @FXML private NumberAxis yAxisRevChart;

    @FXML private Tab tabSwComp;
    @FXML private ListView<Label> swCompList;
    @FXML private ComboBox comboSwComp;
    @FXML private TextField searchSwComp;
    @FXML private BarChart swCompChart;
    @FXML private NumberAxis yAxisSwCompChart;
    @FXML private Label swCompChildrenLabel;
    @FXML private ListView swCompChildrenList;

    @FXML private Button btnTab;
    @FXML private Button btnHome;
    @FXML private Button btnFontInc;
    @FXML private Button btnFontDec;

    // --------------------------------

    /** deactivates the requirement childern list and label */
    public void deactivateReqChildren(Boolean key) {
        reqChildrenList.setDisable(key);
        reqChildrenLabel.setDisable(key);
        // clear the list and label
        reqChildrenLabel.setText("");
        reqChildrenList.getItems().clear();
    }

    /** deactivates the test childern list and label */
    public void deactivateTstChildren(Boolean key) {
        tstChildrenList.setDisable(key);
        tstChildrenLabel.setDisable(key);
        // clear the list and label
        tstChildrenLabel.setText("");
        tstChildrenList.getItems().clear();
    }

    /** deactivates the swComponent childern list and label */
    public void deactivateSwCompChildren(Boolean key) {
        swCompChildrenList.setDisable(key);
        swCompChildrenLabel.setDisable(key);
        // clear the list and label
        swCompChildrenLabel.setText("");
        swCompChildrenList.getItems().clear();
    }

    /**
     * Populates the list of requirement children appropriately
     *
     * @param reqLine
     */
    public void populateReqChildren(String reqLine) {
        // get the requirement id
        String[] reqIdWithSpace = reqLine.split("\\|");
        String reqId = reqIdWithSpace[0].trim();

        if (reqChildrenRelation.equals("Satisfies")) {
            // activate the label and list and put string in label
            deactivateReqChildren(false);
            reqChildrenLabel.setText("Satisfies:");

            for (Requirement reqObj : currentObjObject.getObjOutputs().getRequirements()) {
                if (reqObj.getId().equalsIgnoreCase(reqId)) { // find the requirement object

                    //                	// add source
                    //
                    //	reqSrcLabel.setText(ReportViewUtils.getSrcLabelText(reqObj.getSourceDocument()));

                    if (reqObj.getSatisfies().size() > 0) {
                        for (Requirement satisfiesObj : reqObj.getSatisfies()) {
                            Label reqChildLabel = new Label();
                            reqChildLabel.setText(satisfiesObj.getId());
                            reqChildrenList.getItems().add(reqChildLabel);
                        }
                    }
                }
            }
        } else if (reqChildrenRelation.equals("Logs")) {
            // activate the label and list and put string in label
            deactivateReqChildren(false);
            reqChildrenLabel.setText("Logs:");

            for (Requirement reqObj : currentObjObject.getObjOutputs().getRequirements()) {

                if (reqObj.getId().equalsIgnoreCase(reqId)) { // find the requirement object

                    //                	// add source
                    //
                    //	reqSrcLabel.setText(ReportViewUtils.getSrcLabelText(reqObj.getSourceDocument()));

                    if (reqObj.getLogs().size() > 0) {
                        for (ReviewLog logObj : reqObj.getLogs()) {
                            Label reqChildLabel = new Label();
                            reqChildLabel.setText(logObj.getId());
                            reqChildrenList.getItems().add(reqChildLabel);
                        }
                    }
                }
            }
        } else if (reqChildrenRelation.equals("Tests")) {
            // activate the label and list and put string in label
            deactivateReqChildren(false);
            reqChildrenLabel.setText("Tests:");

            for (Requirement reqObj : currentObjObject.getObjOutputs().getRequirements()) {

                if (reqObj.getId().equalsIgnoreCase(reqId)) { // find the requirement object

                    //                	// add source
                    //
                    //	reqSrcLabel.setText(ReportViewUtils.getSrcLabelText(reqObj.getSourceDocument()));

                    if (reqObj.getTests().size() > 0) {
                        for (Test tstObj : reqObj.getTests()) {
                            Label reqChildLabel = new Label();
                            reqChildLabel.setText(tstObj.getId());
                            reqChildrenList.getItems().add(reqChildLabel);
                        }
                    }
                }
            }
        }
    }

    /**
     * Populates the list of requirement children appropriately
     *
     * @param tstLine
     */
    public void populateTstChildren(String tstLine) {
        // get the test id
        String[] tstIdWithSpace = tstLine.split("\\|");
        String tstId = tstIdWithSpace[0].trim();

        if (tstChildrenRelation.equals("Verifies")) {
            // activate the label and list and put string in label
            deactivateTstChildren(false);
            tstChildrenLabel.setText("Verifies:");

            for (Test tstObj : currentObjObject.getObjOutputs().getTests()) {
                if (tstObj.getId().equalsIgnoreCase(tstId)) { // find the Test object

                    //                	// add source
                    //
                    //	tstSrcLabel.setText(ReportViewUtils.getSrcLabelText(tstObj.getSourceDocument()));

                    if (tstObj.getVerifies().size() > 0) {
                        for (String Verifies : tstObj.getVerifies()) {
                            Label tstChildLabel = new Label();
                            tstChildLabel.setText(Verifies);
                            tstChildrenList.getItems().add(tstChildLabel);
                        }
                    }
                }
            }
        } else if (tstChildrenRelation.equals("Logs")) {
            // activate the label and list and put string in label
            deactivateTstChildren(false);
            tstChildrenLabel.setText("Logs:");

            for (Test tstObj : currentObjObject.getObjOutputs().getTests()) {
                if (tstObj.getId().equalsIgnoreCase(tstId)) { // find the test object

                    //                	// add source
                    //
                    //	tstSrcLabel.setText(ReportViewUtils.getSrcLabelText(tstObj.getSourceDocument()));

                    if (tstObj.getLogs().size() > 0) {
                        for (ReviewLog logObj : tstObj.getLogs()) {
                            Label tstChildLabel = new Label();
                            tstChildLabel.setText(logObj.getId());
                            tstChildrenList.getItems().add(tstChildLabel);
                        }
                    }
                }
            }
        }
    }

    public void populateSwCompChildren(String swCompLine) {
        // get the test id
        String[] swCompIdWithSpace = swCompLine.split("\\|");
        String swCompId = swCompIdWithSpace[0].trim();

        if (swCompChildrenRelation.equalsIgnoreCase("wasImpactedBy")) {
            // activate the label and list and put string in label
            deactivateSwCompChildren(false);
            swCompChildrenLabel.setText("wasImpactedBy:");

            for (SwComponent swCompObj : currentObjObject.getObjOutputs().getSwComponents()) {
                if (swCompObj.getId().equalsIgnoreCase(swCompId)) { // find the swComp object

                    //                	// add source
                    //
                    //	swCompSrcLabel.setText(ReportViewUtils.getSrcLabelText(swCompObj.getSourceDocument()));

                    if (swCompObj.getWasImpactedBy().size() > 0) {
                        for (Requirement req : swCompObj.getWasImpactedBy()) {
                            Label swCompChildLabel = new Label();
                            swCompChildLabel.setText(req.getId());
                            swCompChildrenList.getItems().add(swCompChildLabel);
                        }
                    }
                }
            }
        }
    }

    /** populates the chart */
    private void populateReqChart(List<Requirement> reqList) {
        // clear the chart
        reqChart.getData().clear();

        // enable the chart
        reqChart.setDisable(false);
        yAxisReqChart.setDisable(false);

        // to store the highest value for scaling
        int high = -1;

        // TODO: write logic for other objectives
        if (currentObjId.equals("A2-1") || currentObjId.equals("A2-4")) {
            XYChart.Series reqStat = new XYChart.Series();

            int numTrace = LogicUtils.getNumReqsWithTrace(reqList);

            Data traceBar = ReportViewUtils.createIntDataBar("Trace", numTrace);
            Data noTraceBar =
                    ReportViewUtils.createIntDataBar("No Trace", reqList.size() - numTrace);

            reqStat.getData().add(traceBar);
            reqStat.getData().add(noTraceBar);

            reqChart.getData().add(reqStat);

            traceBar.getNode().getStyleClass().add("color-passed");
            ReportViewUtils.assignTooltip(traceBar.getNode(), traceBar.getYValue().toString());
            noTraceBar.getNode().getStyleClass().add("color-failed");
            ReportViewUtils.assignTooltip(noTraceBar.getNode(), noTraceBar.getYValue().toString());

            reqChart.setTitle("Total Requirements = " + reqList.size());

            // scaling
            int maxScale = Math.max(numTrace, (reqList.size() - numTrace));
            // *** This can help make integral ticks on Y axis ***
            yAxisReqChart.setLowerBound(0);
            yAxisReqChart.setUpperBound(maxScale);
            yAxisReqChart.setTickUnit(1);
        }

        // TODO: write logic for other objectives
        if (currentObjId.equals("A7-3") || currentObjId.equals("A7-4")) {
            XYChart.Series reqStat = new XYChart.Series();

            List<Integer> testStats = LogicUtils.getRequirementTestStatus(reqList);

            int numPassed = testStats.get(0);
            int numFailed = testStats.get(1);
            int numNoData = testStats.get(2);

            Data passBar = ReportViewUtils.createIntDataBar("  Passed\nCoverage", numPassed);
            Data failBar = ReportViewUtils.createIntDataBar("  Failed\nCoverage", numFailed);
            Data noBar = ReportViewUtils.createIntDataBar("No Test", numNoData);

            reqStat.getData().add(passBar);
            reqStat.getData().add(failBar);
            reqStat.getData().add(noBar);

            reqChart.getData().add(reqStat);

            passBar.getNode().getStyleClass().add("color-passed");
            ReportViewUtils.assignTooltip(passBar.getNode(), passBar.getYValue().toString());
            failBar.getNode().getStyleClass().add("color-failed");
            ReportViewUtils.assignTooltip(failBar.getNode(), failBar.getYValue().toString());
            noBar.getNode().getStyleClass().add("color-no-data");
            ReportViewUtils.assignTooltip(noBar.getNode(), noBar.getYValue().toString());

            reqChart.setTitle("Total Requirements = " + reqList.size());

            // scaling
            int maxScale = Math.max(numNoData, Math.max(numPassed, numFailed));
            // *** This can help make integral ticks on Y axis ***
            yAxisReqChart.setLowerBound(0);
            yAxisReqChart.setUpperBound(maxScale);
            yAxisReqChart.setTickUnit(1);
        }

        // TODO: write logic for other objectives
        if (currentObjId.equals("A3-1")
                || currentObjId.equals("A3-2")
                || currentObjId.equals("A3-3")
                || currentObjId.equals("A3-4")
                || currentObjId.equals("A3-5")
                || currentObjId.equals("A3-6")
                || currentObjId.equals("A4-1")
                || currentObjId.equals("A4-2")
                || currentObjId.equals("A4-3")
                || currentObjId.equals("A4-4")
                || currentObjId.equals("A4-5")
                || currentObjId.equals("A4-6")) {
            XYChart.Series reqStat = new XYChart.Series();

            List<Integer> testStats = LogicUtils.getRequirementLogStatus(reqList);

            int numPassed = testStats.get(0);
            int numFailed = testStats.get(1);
            int numNoData = testStats.get(2);

            Data passBar = ReportViewUtils.createIntDataBar("Passed", numPassed);
            Data failBar = ReportViewUtils.createIntDataBar("Failed", numFailed);
            Data noBar = ReportViewUtils.createIntDataBar("No Log", numNoData);

            reqStat.getData().add(passBar);
            reqStat.getData().add(failBar);
            reqStat.getData().add(noBar);

            reqChart.getData().add(reqStat);

            passBar.getNode().getStyleClass().add("color-passed");
            ReportViewUtils.assignTooltip(passBar.getNode(), passBar.getYValue().toString());
            failBar.getNode().getStyleClass().add("color-failed");
            ReportViewUtils.assignTooltip(failBar.getNode(), failBar.getYValue().toString());
            noBar.getNode().getStyleClass().add("color-no-data");
            ReportViewUtils.assignTooltip(noBar.getNode(), noBar.getYValue().toString());

            reqChart.setTitle("Total Requirements = " + reqList.size());

            // scaling
            int maxScale = Math.max(numNoData, Math.max(numPassed, numFailed));
            // *** This can help make integral ticks on Y axis ***
            yAxisReqChart.setLowerBound(0);
            yAxisReqChart.setUpperBound(maxScale);
            yAxisReqChart.setTickUnit(1);
        }
    }

    /** populates the chart */
    private void populateTstChart(List<Test> tstList) {
        // clear the chart
        tstChart.getData().clear();

        // enable the chart
        tstChart.setDisable(false);
        yAxisTstChart.setDisable(false);

        // to store the highest value for scaling
        int high = -1;

        // TODO: write logic for other objectives
        if (currentObjId.equals("A7-3") || currentObjId.equals("A7-4")) {
            XYChart.Series tstStat = new XYChart.Series();

            int numPassed = 0;
            int numFailed = 0;

            for (Test tst : tstList) {
                if (tst.getResult() != null) {
                    if (tst.getResult().equalsIgnoreCase("Passed")) {
                        numPassed = numPassed + 1;
                    } else {
                        numFailed = numFailed + 1;
                    }
                } else {
                    numFailed = numFailed + 1;
                }
            }

            Data passBar = ReportViewUtils.createIntDataBar("Passed", numPassed);
            Data failBar = ReportViewUtils.createIntDataBar("Failed", numFailed);

            tstStat.getData().add(passBar);
            tstStat.getData().add(failBar);

            tstChart.getData().add(tstStat);

            passBar.getNode().getStyleClass().add("color-passed");
            ReportViewUtils.assignTooltip(passBar.getNode(), passBar.getYValue().toString());
            failBar.getNode().getStyleClass().add("color-failed");
            ReportViewUtils.assignTooltip(failBar.getNode(), failBar.getYValue().toString());

            tstChart.setTitle("Total Tests = " + tstList.size());

            // scaling
            int maxScale = Math.max(numPassed, numFailed);
            // *** This can help make integral ticks on Y axis ***
            yAxisTstChart.setLowerBound(0);
            yAxisTstChart.setUpperBound(maxScale);
            yAxisTstChart.setTickUnit(1);
        }

        // TODO: write logic for other objectives
        if (currentObjId.equals("A7-1")) {
            XYChart.Series tstStat = new XYChart.Series();

            List<Integer> testStats = LogicUtils.getTestLogStatus(tstList);

            int numPassed = testStats.get(0);
            int numFailed = testStats.get(1);
            int numNoData = testStats.get(2);

            Data passBar = ReportViewUtils.createIntDataBar("Passed", numPassed);
            Data failBar = ReportViewUtils.createIntDataBar("Failed", numFailed);
            Data noBar = ReportViewUtils.createIntDataBar("No Log", numNoData);

            tstStat.getData().add(passBar);
            tstStat.getData().add(failBar);
            tstStat.getData().add(noBar);

            tstChart.getData().add(tstStat);

            passBar.getNode().getStyleClass().add("color-passed");
            ReportViewUtils.assignTooltip(passBar.getNode(), passBar.getYValue().toString());
            failBar.getNode().getStyleClass().add("color-failed");
            ReportViewUtils.assignTooltip(failBar.getNode(), failBar.getYValue().toString());
            noBar.getNode().getStyleClass().add("color-no-data");
            ReportViewUtils.assignTooltip(noBar.getNode(), noBar.getYValue().toString());

            tstChart.setTitle("Total Tests = " + tstList.size());

            // scaling
            int maxScale = Math.max(numNoData, Math.max(numPassed, numFailed));
            // *** This can help make integral ticks on Y axis ***
            yAxisTstChart.setLowerBound(0);
            yAxisTstChart.setUpperBound(maxScale);
            yAxisTstChart.setTickUnit(1);
        }
    }

    public void populateSwCompChart(List<SwComponent> swCompList) {
        // clear the chart
        swCompChart.getData().clear();

        // enable the chart
        swCompChart.setDisable(false);
        yAxisSwCompChart.setDisable(false);

        if (currentObjId.equals("A5-5")) {
            XYChart.Series swCompStat = new XYChart.Series();

            int numWithSubDDTrace = LogicUtils.getSwCompSubDDTraceStats(swCompList);
            ;
            int numWithoutSubDDTrace = swCompList.size() - numWithSubDDTrace;

            Data passBar = ReportViewUtils.createIntDataBar("SubDD Trace", numWithSubDDTrace);
            Data failBar = ReportViewUtils.createIntDataBar("No SubDD Trace", numWithoutSubDDTrace);

            swCompStat.getData().add(passBar);
            swCompStat.getData().add(failBar);

            swCompChart.getData().add(swCompStat);

            passBar.getNode().getStyleClass().add("color-passed");
            ReportViewUtils.assignTooltip(passBar.getNode(), passBar.getYValue().toString());
            failBar.getNode().getStyleClass().add("color-failed");
            ReportViewUtils.assignTooltip(failBar.getNode(), failBar.getYValue().toString());

            swCompChart.setTitle("Total SwComponents = " + swCompList.size());

            // scaling
            int maxScale = Math.max(numWithSubDDTrace, numWithoutSubDDTrace);
            // *** This can help make integral ticks on Y axis ***
            yAxisSwCompChart.setLowerBound(0);
            yAxisSwCompChart.setUpperBound(maxScale);
            yAxisSwCompChart.setTickUnit(1);
        }

        // below hardcoded
        if (currentObjId.equals("A5-1")
                || currentObjId.equals("A5-2")
                || currentObjId.equals("A5-3")
                || currentObjId.equals("A5-4")) {

            XYChart.Series swCompStat = new XYChart.Series();

            int numWithLogs = 0; // hardcoded
            int numWithoutLogs = swCompList.size() - numWithLogs;

            Data passBar = ReportViewUtils.createIntDataBar("Has Logs", numWithLogs);
            Data failBar = ReportViewUtils.createIntDataBar("No Logs", numWithoutLogs);

            swCompStat.getData().add(passBar);
            swCompStat.getData().add(failBar);

            swCompChart.getData().add(swCompStat);

            passBar.getNode().getStyleClass().add("color-passed");
            ReportViewUtils.assignTooltip(passBar.getNode(), passBar.getYValue().toString());
            failBar.getNode().getStyleClass().add("color-failed");
            ReportViewUtils.assignTooltip(failBar.getNode(), failBar.getYValue().toString());

            swCompChart.setTitle("Total SwComponents = " + swCompList.size());

            // scaling
            int maxScale = Math.max(numWithLogs, numWithoutLogs);
            // *** This can help make integral ticks on Y axis ***
            yAxisSwCompChart.setLowerBound(0);
            yAxisSwCompChart.setUpperBound(maxScale);
            yAxisSwCompChart.setTickUnit(1);
        }
    }

    public void initializeComboReq() {

        comboReq.setPromptText("Filter");

        // TODO: write logic for other objectives
        if (currentObjId.equals("A2-1") || currentObjId.equals("A2-4")) {

            // add the categories to the combo
            comboReq.getItems().add("All");
            comboReq.getItems().add("Trace");
            comboReq.getItems().add("No Trace");
        }
        // TODO: write logic for other objectives
        if (currentObjId.equals("A7-3") || currentObjId.equals("A7-4")) {

            // add the categories to the combo
            comboReq.getItems().add("All");
            comboReq.getItems().add("Passed Coverage");
            comboReq.getItems().add("Failed Coverage");
            comboReq.getItems().add("No Tests");
        }

        if (currentObjId.equals("A3-1")
                || currentObjId.equals("A3-2")
                || currentObjId.equals("A3-3")
                || currentObjId.equals("A3-4")
                || currentObjId.equals("A3-5")
                || currentObjId.equals("A3-6")
                || currentObjId.equals("A4-1")
                || currentObjId.equals("A4-2")
                || currentObjId.equals("A4-3")
                || currentObjId.equals("A4-4")
                || currentObjId.equals("A4-5")
                || currentObjId.equals("A4-6")) {

            // add the categories to the combo
            comboReq.getItems().add("All");
            comboReq.getItems().add("Logs");
            comboReq.getItems().add("No Logs");
        }
    }

    public void initializeComboTst() {

        comboTst.setPromptText("Filter");

        // TODO: write logic for other objectives
        if (currentObjId.equals("A7-3") || currentObjId.equals("A7-4")) {

            // add the categories to the combo
            comboTst.getItems().add("All");
            comboTst.getItems().add("Passed");
            comboTst.getItems().add("Failed");
        }

        // TODO: write logic for other objectives
        if (currentObjId.equals("A7-1")) {

            // add the categories to the combo
            comboTst.getItems().add("All");
            comboTst.getItems().add("Logs");
            comboTst.getItems().add("No Logs");
        }
    }

    public void initializeComboSwComp() {

        comboSwComp.setPromptText("Filter");

        // TODO: write logic for other objectives
        if (currentObjId.equals("A5-5")) {

            // add the categories to the combo
            comboSwComp.getItems().add("All");
            comboSwComp.getItems().add("Has SubDD Trace");
            comboSwComp.getItems().add("No SubDD Trace");
        }
        if (currentObjId.equals("A5-1")
                || currentObjId.equals("A5-2")
                || currentObjId.equals("A5-3")
                || currentObjId.equals("A5-4")) {

            // add the categories to the combo
            comboSwComp.getItems().add("All");
            comboSwComp.getItems().add("Has Logs");
            comboSwComp.getItems().add("No Logs");
        }
    }

    public void populateListReq(String filterKey, String searchKey) {

        // TODO: write logic for other objectives
        if (currentObjId.equals("A2-1") || currentObjId.equals("A2-4")) {

            if ((currentObjObject.getObjOutputs().getRequirements() != null)
                    && (currentObjObject.getObjOutputs().getRequirements().size() > 0)) {
                tabReq.setDisable(false);

                // clear the list and chart
                reqList.getItems().clear();
                reqChart.getData().clear();

                // store the requirement releaationship for this objective
                reqChildrenRelation = "Satisfies";

                // populate the chart
                populateReqChart(currentObjObject.getObjOutputs().getRequirements());

                for (Requirement req : currentObjObject.getObjOutputs().getRequirements()) {
                    String reqText = req.getId() + " | Satisfies: ";
                    if (req.getSatisfies().size() > 0) {
                        for (Requirement satisfies : req.getSatisfies()) {
                            reqText = reqText + satisfies.getId() + ", ";
                        }
                    }
                    if (filterKey.equalsIgnoreCase("All")
                            && ((searchKey == null) || (reqText.contains(searchKey)))) {
                        Label reqLabel = new Label();

                        String onHover =
                                "(" + req.getType() + ") " + req.getDescription().replace("\"", "");
                        reqLabel.setText(reqText);
                        //                        reqLabel.setTooltip(new Tooltip(onHover));
                        reqList.getItems().add(reqLabel);

                    } else if (filterKey.equalsIgnoreCase("Trace")
                            && ((searchKey == null) || (reqText.contains(searchKey)))) {
                        if (req.getSatisfies().size() > 0) {
                            Label reqLabel = new Label();

                            String onHover =
                                    "("
                                            + req.getType()
                                            + ") "
                                            + req.getDescription().replace("\"", "");
                            reqLabel.setText(reqText);
                            //                            reqLabel.setTooltip(new Tooltip(onHover));
                            reqList.getItems().add(reqLabel);
                        }
                    } else if (filterKey.equalsIgnoreCase("No Trace")
                            && ((searchKey == null) || (reqText.contains(searchKey)))) {
                        if (req.getSatisfies().size() < 1) {
                            Label reqLabel = new Label();

                            String onHover =
                                    "("
                                            + req.getType()
                                            + ") "
                                            + req.getDescription().replace("\"", "");
                            reqLabel.setText(reqText);
                            //                            reqLabel.setTooltip(new Tooltip(onHover));
                            reqList.getItems().add(reqLabel);
                        }
                    }
                }
            }
        }

        if (currentObjId.equals("A7-3") || currentObjId.equals("A7-4")) {

            if ((currentObjObject.getObjOutputs().getRequirements() != null)
                    && (currentObjObject.getObjOutputs().getRequirements().size() > 0)) {
                tabReq.setDisable(false);

                // clear the list and chart
                reqList.getItems().clear();
                reqChart.getData().clear();

                // store the requirement releaationship for this objective
                reqChildrenRelation = "Tests";

                // populate the chart
                populateReqChart(currentObjObject.getObjOutputs().getRequirements());

                for (Requirement req : currentObjObject.getObjOutputs().getRequirements()) {
                    if (filterKey.equalsIgnoreCase("All")
                            && ((searchKey == null) || (req.getId().contains(searchKey)))) {
                        Label reqLabel = new Label();
                        String reqText = req.getId() + " | Tests: ";
                        if (req.getTests().size() > 0) {
                            for (Test tst : req.getTests()) {
                                reqText = reqText + tst.getId() + ", ";
                            }
                        }
                        String onHover =
                                "(" + req.getType() + ") " + req.getDescription().replace("\"", "");
                        reqLabel.setText(reqText);
                        //                        reqLabel.setTooltip(new Tooltip(onHover));
                        reqList.getItems().add(reqLabel);

                    } else if ((filterKey.equalsIgnoreCase("Passed Coverage")
                                    || filterKey.equalsIgnoreCase("Failed Coverage"))
                            && ((searchKey == null) || (req.getId().contains(searchKey)))) {
                        if (req.getTests().size() > 0) {
                            Label reqLabel = new Label();
                            String reqText = req.getId() + " | Tests: ";

                            boolean failFlag = false;
                            for (Test tst : req.getTests()) {
                                reqText = reqText + tst.getId() + ", ";
                                if (!tst.getResult().equalsIgnoreCase("Passed")) {
                                    failFlag = true;
                                }
                            }
                            if (failFlag && filterKey.equalsIgnoreCase("Failed Coverage")) {
                                String onHover =
                                        "("
                                                + req.getType()
                                                + ") "
                                                + req.getDescription().replace("\"", "");
                                reqLabel.setText(reqText);
                                //                                reqLabel.setTooltip(new
                                // Tooltip(onHover));
                                reqList.getItems().add(reqLabel);
                            }
                            if (!failFlag && filterKey.equalsIgnoreCase("Passed Coverage")) {
                                String onHover =
                                        "("
                                                + req.getType()
                                                + ") "
                                                + req.getDescription().replace("\"", "");
                                reqLabel.setText(reqText);
                                //                                reqLabel.setTooltip(new
                                // Tooltip(onHover));
                                reqList.getItems().add(reqLabel);
                            }
                        }
                    } else if (filterKey.equalsIgnoreCase("No Tests")
                            && ((searchKey == null) || (req.getId().contains(searchKey)))) {
                        if (req.getTests().size() < 1) {
                            Label reqLabel = new Label();
                            String reqText = req.getId() + " | Tests: ";

                            String onHover =
                                    "("
                                            + req.getType()
                                            + ") "
                                            + req.getDescription().replace("\"", "");
                            reqLabel.setText(reqText);
                            //                            reqLabel.setTooltip(new Tooltip(onHover));
                            reqList.getItems().add(reqLabel);
                        }
                    }
                }
            }
        }

        if (currentObjId.equals("A3-1")
                || currentObjId.equals("A3-2")
                || currentObjId.equals("A3-3")
                || currentObjId.equals("A3-4")
                || currentObjId.equals("A3-5")
                || currentObjId.equals("A3-6")
                || currentObjId.equals("A4-1")
                || currentObjId.equals("A4-2")
                || currentObjId.equals("A4-3")
                || currentObjId.equals("A4-4")
                || currentObjId.equals("A4-5")
                || currentObjId.equals("A4-6")) {

            if ((currentObjObject.getObjOutputs().getRequirements() != null)
                    && (currentObjObject.getObjOutputs().getRequirements().size() > 0)) {
                tabReq.setDisable(false);

                // clear the list and chart
                reqList.getItems().clear();
                reqChart.getData().clear();

                // store the requirement releaationship for this objective
                reqChildrenRelation = "Logs";

                // populate the chart
                populateReqChart(currentObjObject.getObjOutputs().getRequirements());

                for (Requirement req : currentObjObject.getObjOutputs().getRequirements()) {
                    if (filterKey.equalsIgnoreCase("All")
                            && ((searchKey == null) || (req.getId().contains(searchKey)))) {
                        Label reqLabel = new Label();
                        String reqText = req.getId() + " | Logs: ";
                        if (req.getLogs().size() > 0) {
                            for (ReviewLog tst : req.getLogs()) {
                                reqText = reqText + tst.getId() + ", ";
                            }
                        }
                        String onHover =
                                "(" + req.getType() + ") " + req.getDescription().replace("\"", "");
                        reqLabel.setText(reqText);
                        //                        reqLabel.setTooltip(new Tooltip(onHover));
                        reqList.getItems().add(reqLabel);

                    } else if (filterKey.equalsIgnoreCase("Logs")
                            && ((searchKey == null) || (req.getId().contains(searchKey)))) {
                        if (req.getLogs().size() > 0) {
                            Label reqLabel = new Label();
                            String reqText = req.getId() + " | Logs: ";

                            for (ReviewLog tst : req.getLogs()) {
                                reqText = reqText + tst.getId() + ", ";
                            }
                            String onHover =
                                    "("
                                            + req.getType()
                                            + ") "
                                            + req.getDescription().replace("\"", "");
                            reqLabel.setText(reqText);
                            //                            reqLabel.setTooltip(new Tooltip(onHover));
                            reqList.getItems().add(reqLabel);
                        }
                    } else if (filterKey.equalsIgnoreCase("No Logs")
                            && ((searchKey == null) || (req.getId().contains(searchKey)))) {
                        if (req.getLogs().size() < 1) {
                            Label reqLabel = new Label();
                            String reqText = req.getId() + " | Logs: ";

                            String onHover =
                                    "("
                                            + req.getType()
                                            + ") "
                                            + req.getDescription().replace("\"", "");
                            reqLabel.setText(reqText);
                            //                            reqLabel.setTooltip(new Tooltip(onHover));
                            reqList.getItems().add(reqLabel);
                        }
                    }
                }
            }
        }
    }

    public void populateListSwComp(String filterKey, String searchKey) {
        if (currentObjId.equals("A5-5")) {

            if ((currentObjObject.getObjOutputs().getSwComponents() != null)
                    & (currentObjObject.getObjOutputs().getSwComponents().size() > 0)) {
                tabSwComp.setDisable(false);

                // clear the list and chart
                swCompList.getItems().clear();
                swCompChart.getData().clear();

                swCompChildrenRelation = "wasImpactedBy";

                // TODO: Populate the chart
                populateSwCompChart(currentObjObject.getObjOutputs().getSwComponents());

                for (SwComponent swComp : currentObjObject.getObjOutputs().getSwComponents()) {
                    if (filterKey.equalsIgnoreCase("All")
                            && ((searchKey == null)
                                    || (swComp.getId().contains(searchKey)))) { // All
                        System.out.println("Selected " + filterKey + ", using all");
                        Label swCompLabel = new Label();
                        String swCompText =
                                swComp.getId()
                                        + " | wasImpactedBy: "
                                        + swComp.getWasImpactedByAsString();
                        swCompLabel.setText(swCompText);
                        swCompList.getItems().add(swCompLabel);
                    } else if (filterKey.equalsIgnoreCase("Has SubDD Trace")
                            && ((searchKey == null)
                                    || (swComp.getId().contains(searchKey)))) { // SubDD Trace
                        System.out.println("Selected " + filterKey + ", using trace");
                        Boolean hasSubDDTrace = false;
                        for (Requirement req : swComp.getWasImpactedBy()) {
                            if (req.getType().equalsIgnoreCase("SubDD_Req")) {
                                hasSubDDTrace = true;
                                break;
                            }
                        }
                        if (hasSubDDTrace) {
                            Label swCompLabel = new Label();
                            String swCompText =
                                    swComp.getId()
                                            + " | wasImpactedBy: "
                                            + swComp.getWasImpactedByAsString();
                            swCompLabel.setText(swCompText);
                            swCompList.getItems().add(swCompLabel);
                        }
                    } else if (filterKey.equalsIgnoreCase("No SubDD Trace")
                            && ((searchKey == null)
                                    || (swComp.getId().contains(searchKey)))) { // No SubDD Trace
                        System.out.println("Selected " + filterKey + ", using no trace");
                        Boolean hasSubDDTrace = false;
                        for (Requirement req : swComp.getWasImpactedBy()) {
                            if (req.getType().equalsIgnoreCase("SubDD_Req")) {
                                hasSubDDTrace = true;
                                break;
                            }
                        }
                        if (!hasSubDDTrace) {
                            Label swCompLabel = new Label();
                            String swCompText =
                                    swComp.getId()
                                            + " | wasImpactedBy: "
                                            + swComp.getWasImpactedByAsString();
                            swCompLabel.setText(swCompText);
                            swCompList.getItems().add(swCompLabel);
                        }
                    }
                }
            }
        }

        /** mostly hardcoded, not good code */
        if (currentObjId.equals("A5-1")
                || currentObjId.equals("A5-2")
                || currentObjId.equals("A5-3")
                || currentObjId.equals("A5-4")) {

            if ((currentObjObject.getObjOutputs().getSwComponents() != null)
                    & (currentObjObject.getObjOutputs().getSwComponents().size() > 0)) {
                tabSwComp.setDisable(false);

                // clear the list and chart
                swCompList.getItems().clear();
                swCompChart.getData().clear();

                swCompChildrenRelation = "wasImpactedBy";

                // TODO: Populate the chart
                populateSwCompChart(currentObjObject.getObjOutputs().getSwComponents());

                for (SwComponent swComp : currentObjObject.getObjOutputs().getSwComponents()) {
                    if (filterKey.equalsIgnoreCase("All")
                            && ((searchKey == null)
                                    || (swComp.getId().contains(searchKey)))) { // All
                        Label swCompLabel = new Label();
                        String swCompText = swComp.getId() + " | Logs: ";
                        swCompLabel.setText(swCompText);
                        swCompList.getItems().add(swCompLabel);
                    } else if (filterKey.equalsIgnoreCase("Has Logs")
                            && ((searchKey == null)
                                    || (swComp.getId().contains(searchKey)))) { // SubDD Trace
                        // nothing
                    } else if (filterKey.equalsIgnoreCase("No Logs")
                            && ((searchKey == null)
                                    || (swComp.getId().contains(searchKey)))) { // No SubDD Trace
                        Label swCompLabel = new Label();
                        String swCompText = swComp.getId() + " | Logs: ";
                        swCompLabel.setText(swCompText);
                        swCompList.getItems().add(swCompLabel);
                    }
                }
            }
        }
    }

    public void populateListTst(String filterKey, String searchKey) {
        if (currentObjId.equals("A7-3") || currentObjId.equals("A7-4")) {

            if ((currentObjObject.getObjOutputs().getTests() != null)
                    && (currentObjObject.getObjOutputs().getTests().size() > 0)) {
                tabTest.setDisable(false);

                // clear the list and chart
                tstList.getItems().clear();
                tstChart.getData().clear();

                // store the tst releationship for this objective
                tstChildrenRelation = "Verifies";

                // populate the chart
                populateTstChart(currentObjObject.getObjOutputs().getTests());

                for (Test tst : currentObjObject.getObjOutputs().getTests()) {
                    if (filterKey.equalsIgnoreCase("All")
                            && ((searchKey == null) || (tst.getId().contains(searchKey)))) {
                        Label tstLabel = new Label();
                        String tstText =
                                tst.getId()
                                        + " | "
                                        + tst.getResult()
                                        + " | Verifies: "
                                        + tst.getVerifiesAsString();
                        tstLabel.setText(tstText);
                        tstList.getItems().add(tstLabel);
                    } else if (filterKey.equalsIgnoreCase("Passed")) {
                        if (tst.getResult().equalsIgnoreCase("Passed")) {
                            Label tstLabel = new Label();
                            String tstText =
                                    tst.getId()
                                            + " | "
                                            + tst.getResult()
                                            + " | Verifies: "
                                            + tst.getVerifiesAsString();
                            tstLabel.setText(tstText);
                            tstList.getItems().add(tstLabel);
                        }
                    } else if (filterKey.equalsIgnoreCase("Failed")
                            && ((searchKey == null) || (tst.getId().contains(searchKey)))) {
                        if (!tst.getResult().equalsIgnoreCase("Passed")) {
                            Label tstLabel = new Label();
                            String tstText =
                                    tst.getId()
                                            + "| "
                                            + tst.getResult()
                                            + " | Verifies: "
                                            + tst.getVerifiesAsString();
                            tstLabel.setText(tstText);
                            tstList.getItems().add(tstLabel);
                        }
                    }
                }
            }
        }

        if (currentObjId.equals("A7-1")) {

            if ((currentObjObject.getObjOutputs().getTests() != null)
                    && (currentObjObject.getObjOutputs().getTests().size() > 0)) {
                tabTest.setDisable(false);

                // clear the list and chart
                tstList.getItems().clear();
                tstChart.getData().clear();

                // store the tst releationship for this objective
                tstChildrenRelation = "Logs";

                // populate the chart
                populateTstChart(currentObjObject.getObjOutputs().getTests());

                for (Test tst : currentObjObject.getObjOutputs().getTests()) {
                    if (filterKey.equalsIgnoreCase("All")
                            && ((searchKey == null) || (tst.getId().contains(searchKey)))) {
                        Label tstLabel = new Label();
                        String tstText = tst.getId() + " | Logs: ";
                        if (tst.getLogs().size() > 0) {
                            for (ReviewLog log : tst.getLogs()) {
                                tstText = tstText + log.getId() + ", ";
                            }
                        }
                        tstLabel.setText(tstText);
                        tstList.getItems().add(tstLabel);

                    } else if (filterKey.equalsIgnoreCase("Logs")
                            && ((searchKey == null) || (tst.getId().contains(searchKey)))) {
                        if (tst.getLogs().size() > 0) {
                            Label tstLabel = new Label();
                            String tstText = tst.getId() + " | Logs: ";

                            for (ReviewLog log : tst.getLogs()) {
                                tstText = tstText + log.getId() + ", ";
                            }
                            tstLabel.setText(tstText);
                            tstList.getItems().add(tstLabel);
                        }
                    } else if (filterKey.equalsIgnoreCase("No Logs")
                            && ((searchKey == null) || (tst.getId().contains(searchKey)))) {
                        if (tst.getLogs().size() < 1) {
                            Label tstLabel = new Label();
                            String tstText = tst.getId() + " | Tests: ";

                            tstLabel.setText(tstText);
                            tstList.getItems().add(tstLabel);
                        }
                    }
                }
            }
        }
    }

    public void populateListLog(String filterKey) {
        if (currentObjId.equals("A7-1")
                || currentObjId.equals("A3-1")
                || currentObjId.equals("A3-2")
                || currentObjId.equals("A3-3")
                || currentObjId.equals("A3-4")
                || currentObjId.equals("A3-5")
                || currentObjId.equals("A3-6")
                || currentObjId.equals("A4-1")
                || currentObjId.equals("A4-2")
                || currentObjId.equals("A4-3")
                || currentObjId.equals("A4-4")
                || currentObjId.equals("A4-5")
                || currentObjId.equals("A4-6")) {

            if ((currentObjObject.getObjOutputs().getTests() != null)
                    && (currentObjObject.getObjOutputs().getTests().size() > 0)) {
                tabRev.setDisable(false);

                // TDOD: enter code here
            }
        }
    }

    public void populateListAct() {
        if ((currentObjObject.getObjActivities() != null)
                && (currentObjObject.getObjActivities().size() > 0)) {
            tabAct.setDisable(false);

            // clear the list
            actList.getItems().clear();

            for (Activity act : currentObjObject.getObjActivities()) {
                Label actLabel = new Label();
                actLabel.setText(act.getId() + ": " + act.getDescription().replace("\"", ""));
                actList.getItems().add(actLabel);
                // add sub-activities, if any
                if (act.getPerforms().size() > 0) {
                    for (Activity subAct : act.getPerforms()) {
                        Label subActLabel = new Label();
                        subActLabel.setText(
                                "-> "
                                        + subAct.getId()
                                        + ": "
                                        + subAct.getDescription().replace("\"", ""));
                        actList.getItems().add(subActLabel);
                    }
                }
            }
        }
    }

    public void populateListDoc() {
        if ((currentObjObject.getObjOutputs().getDocuments() != null)
                && (currentObjObject.getObjOutputs().getDocuments().size() > 0)) {
            tabDoc.setDisable(false);

            // clear the list
            docList.getItems().clear();

            for (DataItem doc : currentObjObject.getObjOutputs().getDocuments()) {
                Label docLabel = new Label();
                docLabel.setText(doc.getId());
                docList.getItems().add(docLabel);
            }
        }
    }

    /**
     * Used to initialize the variables from the caller view's fxml controller
     *
     * @param tableId
     * @param objId
     */
    public void prepareView(String tableId, String objId) {
        // set the current table ID
        currentTableId = tableId;

        // set the current table object
        currentTableObject = PsacNodeUtils.getTableById(ReportViewsManager.reportDataObj, tableId);

        // set the current objective ID
        currentObjId = objId;

        // set the current objective object
        currentObjObject =
                PsacNodeUtils.getObjectiveById(ReportViewsManager.reportDataObj, tableId, objId);

        // Set the label text
        labelTableInfo.setText(
                "Table " + currentTableId + ": " + currentTableObject.getDescription());

        // set the label color
        labelTableInfo.setTextFill(ReportViewUtils.getTableColor(currentTableObject));

        // Set the info label
        labelObjInfo.setText(
                "Objective "
                        + currentObjId
                        + ": "
                        + currentObjObject.getDescription().replace("\"", ""));

        // set the label color
        labelObjInfo.setTextFill(ReportViewUtils.getObjectiveColor(currentObjObject));

        // populate the listviews in the tabs
        populateListReq("All", null);
        populateListAct();
        populateListDoc();
        populateListTst("All", null);
        populateListSwComp("All", null);

        // initialize the combos in the tabs
        initializeComboReq();
        initializeComboTst();
        initializeComboSwComp();

        // code for the searchbars
        searchReq
                .textProperty()
                .addListener(
                        new ChangeListener<String>() {
                            @Override
                            public void changed(
                                    ObservableValue<? extends String> observable,
                                    String oldValue,
                                    String newValue) {

                                System.out.println("Search Key " + newValue);

                                // disable req children list
                                deactivateReqChildren(true);

                                if ((newValue != null)
                                        && (newValue != "")
                                        && (newValue.length() > 0)) { // newvalue is not null

                                    // call listview to display all elements whose IDS have newValue
                                    // as substring
                                    populateListReq("All", newValue);

                                } else { // newvalue is null
                                    // call listview to display all elements (default settings)
                                    populateListReq("All", null);
                                }
                                // reset the combo to "All"
                                comboReq.getSelectionModel().selectFirst();
                            }
                        });

        // code for the searchbars
        searchTst
                .textProperty()
                .addListener(
                        new ChangeListener<String>() {
                            @Override
                            public void changed(
                                    ObservableValue<? extends String> observable,
                                    String oldValue,
                                    String newValue) {

                                System.out.println("Search Key " + newValue);

                                // disable tst children list
                                deactivateTstChildren(true);

                                if ((newValue != null)
                                        && (newValue != "")
                                        && (newValue.length() > 0)) { // newvalue is not null

                                    // call listview to display all elements whose IDS have newValue
                                    // as substring
                                    populateListTst("All", newValue);

                                } else { // newvalue is null
                                    // call listview to display all elements (default settings)
                                    populateListTst("All", null);
                                }
                                // reset the combo to "All"
                                comboTst.getSelectionModel().selectFirst();
                            }
                        });

        // code for the searchbars
        searchSwComp
                .textProperty()
                .addListener(
                        new ChangeListener<String>() {
                            @Override
                            public void changed(
                                    ObservableValue<? extends String> observable,
                                    String oldValue,
                                    String newValue) {

                                System.out.println("Search Key " + newValue);

                                // disable tst children list
                                deactivateTstChildren(true);

                                if ((newValue != null)
                                        && (newValue != "")
                                        && (newValue.length() > 0)) { // newvalue is not null

                                    // call listview to display all elements whose IDS have newValue
                                    // as substring
                                    populateListTst("All", newValue);

                                } else { // newvalue is null
                                    // call listview to display all elements (default settings)
                                    populateListTst("All", null);
                                }
                                // reset the combo to "All"
                                comboTst.getSelectionModel().selectFirst();
                            }
                        });
    }

    @FXML
    private void initialize() {
        // initialize the header label
        try {
            final ImageView icon = ReportViewUtils.loadGeIcon();
            icon.setPreserveRatio(true);
            headerLabel.setGraphic(icon);
        } catch (Exception e) {
        }

        // wrap label text
        labelObjInfo.setWrapText(true);

        // disable all tabs
        tabAct.setDisable(true);
        tabDoc.setDisable(true);
        tabReq.setDisable(true);
        tabAct.setDisable(true);
        tabHzrd.setDisable(true);
        tabTest.setDisable(true);
        tabAnls.setDisable(true);
        tabRev.setDisable(true);
        tabSwComp.setDisable(true);

        // disable all child lists
        deactivateReqChildren(true);
        deactivateTstChildren(true);
        deactivateSwCompChildren(true);
    }

    @FXML
    private void comboReqAction(ActionEvent event) throws Exception {

        // clear the search bar and children
        searchReq.clear();
        deactivateReqChildren(true);

        String key = (String) comboReq.getValue();
        // Clear and repopulate the list depending on key
        populateListReq(key, null);
    }

    @FXML
    private void reqListSelectionAction(MouseEvent event) {

        // The selected label
        Label selectedLabel = reqList.getSelectionModel().getSelectedItem();

        if (selectedLabel != null) {

            // get selection
            String selectedReqLine = selectedLabel.getText();
            System.out.println("The selected req line: " + selectedReqLine);

            // Contextmenu for reqList
            ContextMenu reqListContext = new ContextMenu();
            MenuItem menuItemShowSource = new MenuItem("Show Entity Source");
            MenuItem menuItemShowDescription = new MenuItem("Show Entity Description");
            MenuItem menuItemGoToSource = new MenuItem("Go to Entity Source");
            reqListContext.getItems().add(menuItemShowSource);
            reqListContext.getItems().add(menuItemShowDescription);
            reqListContext.getItems().add(menuItemGoToSource);
            reqList.setContextMenu(reqListContext);
            // show source of requirement in reqchildren if right click context selected
            menuItemShowSource.setOnAction(
                    (rightClickEvent) -> {
                        // get the requirement id
                        String[] reqIdWithSpace = selectedReqLine.split("\\|");
                        String reqId = reqIdWithSpace[0].trim();

                        // activate the label and list and put string in label
                        deactivateReqChildren(false);
                        reqChildrenLabel.setText("Requirement Source:");

                        // find the requirement object
                        for (Requirement reqObj :
                                currentObjObject.getObjOutputs().getRequirements()) {
                            // set children list to the sources, if any exist
                            if (reqObj.getId().equals(reqId)
                                    && reqObj.getSourceDocument() != null) {
                                for (String src : reqObj.getSourceDocument()) {
                                    reqChildrenList.getItems().add(src);
                                }
                            }
                        }
                    });
            menuItemShowDescription.setOnAction(
                    (rightClickEvent) -> {
                        // get the requirement id
                        String[] reqIdWithSpace = selectedReqLine.split("\\|");
                        String reqId = reqIdWithSpace[0].trim();

                        // activate the label and list and put string in label
                        deactivateReqChildren(false);
                        reqChildrenLabel.setText("Requirement Description:");

                        // find the requirement object
                        for (Requirement reqObj :
                                currentObjObject.getObjOutputs().getRequirements()) {
                            // set children list to the sources, if any exist
                            if (reqObj.getId().equals(reqId) && reqObj.getDescription() != null) {
                                reqChildrenList.getItems().add(reqObj.getDescription());
                            }
                        }
                    });
            menuItemGoToSource.setOnAction(
                    (rightClickEvent) -> {
                        // Open URL on browser

                        ReportViewUtils.openUrlInDefaultApp(
                                "https://github.com/ge-high-assurance/RITE/blob/dasc23DO178C/examples/OEM/DummySource/RequirementData.pdf");
                    });

            // call the function to populate children
            populateReqChildren(selectedReqLine);
        }
    }

    @FXML
    private void comboTstAction(ActionEvent event) throws Exception {

        // clear the search bar and children
        searchTst.clear();
        deactivateTstChildren(true);

        String key = (String) comboTst.getValue();
        // Clear and repopulate the list depending on key
        populateListTst(key, null);
    }

    @FXML
    private void tstListSelectionAction(MouseEvent event) {

        // The selected label
        Label selectedLabel = tstList.getSelectionModel().getSelectedItem();

        if (selectedLabel != null) {

            // get selection
            String selectedTstLine = selectedLabel.getText();
            System.out.println("The selected tst line: " + selectedTstLine);

            // Contextmenu for reqList
            ContextMenu tstListContext = new ContextMenu();
            MenuItem menuItemShowSource = new MenuItem("Show Entity Source");
            MenuItem menuItemGoToSource = new MenuItem("Go to Entity Source");
            tstListContext.getItems().add(menuItemShowSource);
            tstListContext.getItems().add(menuItemGoToSource);
            tstList.setContextMenu(tstListContext);
            // show source of requirement in reqchildren if right click context selected
            menuItemShowSource.setOnAction(
                    (rightClickEvent) -> {
                        // get the requirement id
                        String[] tstIdWithSpace = selectedTstLine.split("\\|");
                        String tstId = tstIdWithSpace[0].trim();

                        // activate the label and list and put string in label
                        deactivateTstChildren(false);
                        tstChildrenLabel.setText("Test Source:");

                        // find the requirement object
                        for (Test tstObj : currentObjObject.getObjOutputs().getTests()) {
                            // set children list to the sources, if any exist
                            if (tstObj.getId().equals(tstId)
                                    && tstObj.getSourceDocument() != null) {
                                for (String src : tstObj.getSourceDocument()) {
                                    tstChildrenList.getItems().add(src);
                                }
                            }
                        }
                    });
            menuItemGoToSource.setOnAction(
                    (rightClickEvent) -> {
                        // Open URL on browser

                        ReportViewUtils.openUrlInDefaultApp(
                                "https://github.com/ge-high-assurance/RITE/blob/dasc23DO178C/examples/OEM/DummySource/TestData.pdf");
                    });

            // call the function topopulate children
            populateTstChildren(selectedTstLine);
        }
    }

    @FXML
    private void comboSwCompAction(ActionEvent event) throws Exception {

        // clear the search bar and children
        searchSwComp.clear();
        deactivateSwCompChildren(true);

        String key = (String) comboSwComp.getValue();
        // Clear and repopulate the list depending on key
        populateListSwComp(key, null);
    }

    @FXML
    private void swCompListSelectionAction(MouseEvent event) {

        // The selected label
        Label selectedLabel = swCompList.getSelectionModel().getSelectedItem();

        if (selectedLabel != null) {

            // get selection
            String selectedSwCompLine = selectedLabel.getText();
            System.out.println("The selected swComp line: " + selectedSwCompLine);

            // Contextmenu for reqList
            ContextMenu swCompListContext = new ContextMenu();
            MenuItem menuItemShowSource = new MenuItem("Show Entity Source");
            swCompListContext.getItems().add(menuItemShowSource);
            swCompList.setContextMenu(swCompListContext);
            // show source of requirement in reqchildren if right click context selected
            menuItemShowSource.setOnAction(
                    (rightClickEvent) -> {
                        // get the requirement id
                        String[] swCompIdWithSpace = selectedSwCompLine.split("\\|");
                        String swCompId = swCompIdWithSpace[0].trim();

                        // activate the label and list and put string in label
                        deactivateSwCompChildren(false);
                        swCompChildrenLabel.setText("SwComponent Source:");

                        // find the requirement object
                        for (SwComponent swCompObj :
                                currentObjObject.getObjOutputs().getSwComponents()) {
                            // set children list to the sources, if any exist
                            if (swCompObj.getId().equals(swCompId)
                                    && swCompObj.getSourceDocument() != null) {
                                for (String src : swCompObj.getSourceDocument()) {
                                    swCompChildrenList.getItems().add(src);
                                }
                            }
                        }
                    });

            // call the function topopulate children
            populateSwCompChildren(selectedSwCompLine);
        }
    }

    @FXML
    private void btnTabAction(ActionEvent event) throws Exception {
        // Set the stage with the other fxml
        FXMLLoader tableViewLoader =
                ReportViewsManager.setNewFxmlToStage("resources/fxml/do178c/DO178CTableView.fxml");

        // initialize variables in the ReportTableView page
        ReportTableViewHandlerNew tableViewLoaderClassObj = tableViewLoader.getController();
        tableViewLoaderClassObj.prepareView(currentTableId);
    }

    @FXML
    private void btnHomeAction(ActionEvent event) throws Exception {

        // Set the stage with the other fxml
        ReportViewsManager.setNewFxmlToStage("resources/fxml/do178c/DO178CMainView.fxml");
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
}
