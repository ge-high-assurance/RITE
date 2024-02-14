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

import com.ge.research.rack.analysis.structures.PlanObjective;
import com.ge.research.rack.analysis.structures.PlanTable;
import com.ge.research.rack.analysis.utils.ReportViewUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TableViewHandler<T> implements Initializable {

    protected String currentTableId;
    protected PlanTable<T> currentTableObject;

    //    private PlanTable<PlanObjective> currentProcessObject;

    // -------- FXML GUI variables below --------------
    @FXML protected Label headerLabel;

    @FXML protected Label labelTableInfo;

    @FXML protected Button btnHome;
    @FXML protected Button btnFontInc;
    @FXML protected Button btnFontDec;

    @FXML protected ListView<Label> listObjectives;

    @FXML protected ComboBox comboFilter;

    @FXML protected BarChart<String, Integer> chartObjStatus;
    @FXML protected NumberAxis yAxisChartObjStatus;

    /**
     * Creates and returns a label for the objective lists
     *
     * @param objObj
     * @return
     */
    public Label getObjectiveLabel(T objObj) {
        // Subclass to refine
        Label objLabel = new Label();
        objLabel.setStyle("-fx-font-weight: bold;");

        return objLabel;
    }

    /** Populates the ojective status chart */
    public void populateObjStatusChart() {
        // Subclass to refine

        // clear the chart
        chartObjStatus.getData().clear();

        // enable the chart
        chartObjStatus.setDisable(false);
        yAxisChartObjStatus.setDisable(false);
    }

    protected List<T> getCurrentTableObjectives() {
        // Default value; Subclass may refine
        return currentTableObject.getTabObjectives();
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

            for (T objObj : getCurrentTableObjectives()) {
                // Label objLabel = new Label();
                // objLabel.setStyle("-fx-font-weight: bold;");
                //
                // objLabel.setText(objObj.getId() + ": " + objObj.getDescription());
                // objLabel.setTextFill(getObjectiveColor(objObj));

                Label objLabel = getObjectiveLabel(objObj);

                if (filterKey.equalsIgnoreCase("All")) {
                    listObjectives.getItems().add(objLabel);
                } else if (filterKey.equalsIgnoreCase("Passed")
                        && ((PlanObjective) objObj).isPassed()) {
                    listObjectives.getItems().add(objLabel);
                } else if (filterKey.equalsIgnoreCase("Failed")
                        && !((PlanObjective) objObj).isPassed()
                        && !((PlanObjective) objObj).isNoData()
                        && !((PlanObjective) objObj).isPartialData()) {
                    listObjectives.getItems().add(objLabel);
                } else if (filterKey.equalsIgnoreCase("Partial")
                        && ((PlanObjective) objObj).isPartialData()) {
                    listObjectives.getItems().add(objLabel);
                } else if (filterKey.equalsIgnoreCase("No data")
                        && ((PlanObjective) objObj).isNoData()) {
                    listObjectives.getItems().add(objLabel);
                }
            }
        }
    }

    protected PlanTable<T> getCurrentTableObject(String tableID) {
        // Default value; Subclass should override
        return new PlanTable<T>();
    }

    protected String tableInfoLabel() {
        // Default value; Subclass to refine
        return currentTableId;
    }

    protected Color getTableInfoFillColor() {
        // Default value; Subclass should override
        return Color.BLACK;
    }

    /**
     * Used to initialize the variables from the caller view's fxml controller
     *
     * @param procId
     */
    public void prepareView(String tableId) {
        // set the current table ID
        currentTableId = tableId;

        // set the current table object
        currentTableObject = getCurrentTableObject(tableId);

        // populate objectives list
        populateListObjectives("All");

        // populate the chart
        populateObjStatusChart();

        // Set the label text
        labelTableInfo.setText(tableInfoLabel() + ": " + currentTableObject.getDescription());

        // set the label color
        labelTableInfo.setTextFill(getTableInfoFillColor());
    }

    protected String getMainViewPath() {
        // Subclass should override
        return "";
    }

    protected void switchObjectiveView(Label selectedLabel) {
        // Subclass to override
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
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

    protected void setNewFxmlToStage(String path) {
        // Subclass to override
    }

    protected void increaseGlobalFontSize(boolean enable) {
        // Subclass to override
    }

    @FXML
    private void btnHomeAction(ActionEvent event) throws Exception {
        // Set the stage with the other fxml
        setNewFxmlToStage(getMainViewPath());
    }

    @FXML
    private void comboFilterAction(ActionEvent event) throws Exception {
        String key = (String) comboFilter.getValue();

        // Clear and repopulate the listObjectives depending on key
        populateListObjectives(key);
    }

    @FXML
    private void btnFontIncAction(ActionEvent event) throws Exception {
        System.out.println("increase font btn pressed");
        increaseGlobalFontSize(true);
    }

    @FXML
    private void btnFontDecAction(ActionEvent event) throws Exception {
        System.out.println("decrease font btn pressed");
        increaseGlobalFontSize(false);
    }

    @FXML
    private void listObjectivesSelectionAction(MouseEvent event) {
        // The selected label
        Label selectedLabel = listObjectives.getSelectionModel().getSelectedItem();
        if (selectedLabel != null) {
            switchObjectiveView(selectedLabel);
        }
    }
}
