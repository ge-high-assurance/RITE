/**
 * 
 */
package com.ge.research.rack.arp4754.viewHandlers;

import java.util.Collections;
import java.util.List;

import com.ge.research.rack.arp4754.structures.DAPlan;
import com.ge.research.rack.arp4754.utils.DAPlanUtils;
import com.ge.research.rack.arp4754.utils.ViewUtils;
import com.ge.research.rack.arp4754.viewManagers.Arp4754ViewsManager;
import com.ge.research.rack.autoGsn.utils.CustomStringUtils;
import com.ge.research.rack.do178c.structures.PsacNode;
import com.ge.research.rack.do178c.utils.PsacNodeUtils;
import com.ge.research.rack.do178c.utils.ReportViewUtils;
import com.ge.research.rack.do178c.viewHandlers.ReportObjectiveViewHandlerNew;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * @author Saswata Paul
 *
 */
public class ProcessViewHandler {

    private String currentProcessId;

    private DAPlan.Process currentProcessObject;

    // -------- FXML GUI variables below --------------
    @FXML private Label headerLabel;

    @FXML private Label labelProcessInfo;

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
    public Label getObjectiveLabel(DAPlan.Objective objObj) {

        double passPercent = 0.0; // remove

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
                        + objObj.getDesc().replace("\"", "")
                        + " ("
                        + objObj.getMetrics()
                        + ")");
        objLabel.setTextFill(ViewUtils.getObjectiveColor(objObj));
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


    }

    /** Populates the ojective status chart */
    public void populateObjStatusChart() {
//        // clear the chart
//        chartObjStatus.getData().clear();
//
//        // enable the chart
//        chartObjStatus.setDisable(false);
//        yAxisChartObjStatus.setDisable(false);
//
//        List<Integer> artStats = ReportViewUtils.getProcessArtifactStats(currentProcessObject);
//
//        //        Data docBar = new XYChart.Data("Documents", artStats.get(0));
//        //        Data reqBar = new XYChart.Data("Requirements", artStats.get(1));
//        //        Data hzrdBar = new XYChart.Data("Hazards", artStats.get(2));
//        //        Data tstBar = new XYChart.Data("Test Results", artStats.get(3));
//        //        Data logBar = new XYChart.Data("Review Logs", artStats.get(4));
//        //        Data anlsBar = new XYChart.Data("Analyses", artStats.get(5));
//
//        Data docBar = ReportViewUtils.createIntDataBar("Documents", artStats.get(0));
//        Data reqBar = ReportViewUtils.createIntDataBar("Requirements", artStats.get(1));
//        Data hzrdBar = ReportViewUtils.createIntDataBar("Hazards", artStats.get(2));
//        Data tstBar = ReportViewUtils.createIntDataBar("Test Results", artStats.get(3));
//        Data logBar = ReportViewUtils.createIntDataBar("Review Logs", artStats.get(4));
//        Data anlsBar = ReportViewUtils.createIntDataBar("Analyses", artStats.get(5));
//
//        XYChart.Series tableStat = new XYChart.Series();
//
//        tableStat.getData().add(docBar);
//        tableStat.getData().add(reqBar);
//        tableStat.getData().add(hzrdBar);
//        tableStat.getData().add(tstBar);
//        tableStat.getData().add(logBar);
//        tableStat.getData().add(anlsBar);
//
//        chartObjStatus.getData().add(tableStat);
//
//        docBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
//        ReportViewUtils.assignTooltip(docBar.getNode(), docBar.getYValue().toString());
//        reqBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
//        ReportViewUtils.assignTooltip(reqBar.getNode(), reqBar.getYValue().toString());
//        hzrdBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
//        ReportViewUtils.assignTooltip(hzrdBar.getNode(), hzrdBar.getYValue().toString());
//        tstBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
//        ReportViewUtils.assignTooltip(tstBar.getNode(), tstBar.getYValue().toString());
//        logBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
//        ReportViewUtils.assignTooltip(logBar.getNode(), logBar.getYValue().toString());
//        anlsBar.getNode().setStyle("-fx-bar-fill: LightBlue;");
//        ReportViewUtils.assignTooltip(anlsBar.getNode(), anlsBar.getYValue().toString());
//
//        // scaling
//        int maxScale = Collections.max(artStats);
//        // *** This can help make integral ticks on Y axis ***
//        yAxisChartObjStatus.setLowerBound(0);
//        yAxisChartObjStatus.setUpperBound(maxScale);
//        yAxisChartObjStatus.setTickUnit(1);
    }

    /**
     * Used to initialize the variables from the caller view's fxml controller
     *
     * @param procId
     */
    public void prepareView(String procId) {
        // set the current table ID
        currentProcessId = procId;

        // set the current table object
        currentProcessObject = DAPlanUtils.getProcessObjectFromList(Arp4754ViewsManager.reportDataObj.getProcesses(), procId);

        // populate objectives list
        populateListObjectives("All");

        // populate the chart
        populateObjStatusChart();

        // Set the label text
        labelProcessInfo.setText(currentProcessId + ": " + currentProcessObject.getDesc());

        // set the label color
        labelProcessInfo.setTextFill(ViewUtils.getProcessColor(currentProcessObject));
    }

    @FXML
    private void initialize() {
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

    @FXML
    private void btnHomeAction(ActionEvent event) throws Exception {


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

    }

    @FXML
    private void listObjectivesSelectionAction(MouseEvent event) {


    }
	
}
