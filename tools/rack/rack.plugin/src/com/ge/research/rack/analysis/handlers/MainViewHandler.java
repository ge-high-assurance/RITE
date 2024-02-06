/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2024, General Electric Company and Galois, Inc.
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
package com.ge.research.rack.analysis.handlers;

import com.ge.research.rack.analysis.utils.CustomStringUtils;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Saswata Paul
 */
public class MainViewHandler implements Initializable {

    // -------- FXML GUI variables below --------------
    @FXML protected Label headerLabel;

    @FXML protected Button btnFetch;
    @FXML protected Button btnFontInc;
    @FXML protected Button btnFontDec;

    @FXML protected Label labelSwInfo;

    @FXML protected Label labelWait;
    @FXML protected ProgressIndicator progInd;

    @FXML protected BarChart<String, Integer> chartStatus;
    @FXML protected NumberAxis yAxisChartStatus;
    @FXML protected GridPane gridPaneLegend;

    @FXML protected ListView<Label> listBuckets;

    protected String bucketLabel() {
        // default value; Subclasses to override
        return new String("Table ");
    }

    protected String swLabel() {
        // default value; Subclass to override
        return new String("Software ID");
    }

    /**
     * Given "TableAx: yzaysy", returns "Ax"
     *
     * @param procLabelText
     * @return
     */
    protected String getBucketIdFromLabelText(String labelText) {
        String bucketX = CustomStringUtils.separateElementIdFromDescription(labelText);
        String x = bucketX.replace(bucketLabel(), "");
        return x;
    }

    protected void getReportData() throws Exception {
        // Subclass to define
    }

    /**
     * Decides whether the fetch button should be enabled or, if already enabled, if it should be
     * disabled with the dependents
     */
    protected void enableFetchButton() {
        // TODO: Add condition
        // if (true) {
        btnFetch.setDisable(false);
        // } else {
        // TODO: add action
        //    System.out.println("Unspecified for now");
        // }
    }

    /**
     * Enables/disables the wait message
     *
     * @param val
     */
    protected void enableWaitMessage(Boolean val) {
        labelWait.setVisible(val);
        progInd.setVisible(val);
    }

    /** populates the top label with sw info */
    protected void populateLabelSwInfo() {
        // enable elements
        labelSwInfo.setVisible(true);
        labelSwInfo.setText(swLabel());
    }

    /** populates the chart */
    protected void populateChartStatus() {
        // Subclass to expand

        // clear the chart
        chartStatus.getData().clear();

        // enable the chart
        chartStatus.setDisable(false);
        yAxisChartStatus.setDisable(false);
        gridPaneLegend.setDisable(false);
    }

    protected void populateLists(String key) {
        // Subclass to expand

        // clear old data
        listBuckets.getItems().clear();

        // enable elements
        listBuckets.setDisable(false);
    }

    /** Calls necessary functions to populate view elements from data */
    protected void populateViewElements() {
        populateLabelSwInfo();
        populateChartStatus();
        populateLists("All");
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Subclass to refine
    }
}
