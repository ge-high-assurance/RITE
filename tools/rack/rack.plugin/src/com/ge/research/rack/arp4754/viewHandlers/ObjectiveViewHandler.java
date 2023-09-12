/**
 * 
 */
package com.ge.research.rack.arp4754.viewHandlers;

import java.util.List;

import com.ge.research.rack.arp4754.viewManagers.Arp4754ViewsManager;
import com.ge.research.rack.do178c.structures.DataItem;
import com.ge.research.rack.do178c.structures.PsacNode;
import com.ge.research.rack.do178c.structures.Requirement;
import com.ge.research.rack.do178c.structures.ReviewLog;
import com.ge.research.rack.do178c.structures.SwComponent;
import com.ge.research.rack.do178c.structures.Test;
import com.ge.research.rack.do178c.structures.PsacNode.Activity;
import com.ge.research.rack.do178c.utils.LogicUtils;
import com.ge.research.rack.do178c.utils.PsacNodeUtils;
import com.ge.research.rack.do178c.utils.ReportViewUtils;
import com.ge.research.rack.do178c.viewHandlers.ReportTableViewHandlerNew;
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

/**
 * @author Saswata Paul
 *
 */
public class ObjectiveViewHandler {
	   private String currentProcessId;

	    private PsacNode.Table currentProcessObject;

	    private String currentObjId;

	    private PsacNode.Objective currentObjObject;

	    private String interfaceChildrenRelation;

	    private String itemChildrenRelation;

	    private String requirementChildrenRelation;

	    private String systemChildrenRelation;

	    // -------- FXML GUI variables below --------------
	    @FXML private Label headerLabel;

	    @FXML private Label labelProcessInfo;
	    @FXML private Label labelObjInfo;

	    @FXML private Tab tabDocument;

	    @FXML private Tab tabInterface;
	    @FXML private ListView<Label> interfaceList;
	    @FXML private ComboBox comboInterface;
	    @FXML private TextField searchInterface;
	    @FXML private BarChart interfaceChart;
	    @FXML private NumberAxis yAxisInterfaceChart;
	    @FXML private Label interfaceChildrenLabel;
	    @FXML private ListView interfaceChildrenList;
	    

	    @FXML private Tab tabItem;
	    @FXML private ListView<Label> itemList;
	    @FXML private ComboBox comboItem;
	    @FXML private TextField searchItem;
	    @FXML private BarChart itemChart;
	    @FXML private NumberAxis yAxisItemChart;
	    @FXML private Label itemChildrenLabel;
	    @FXML private ListView itemChildrenList;

	    
	    @FXML private Tab tabRequirement;
	    @FXML private ListView<Label> requirementList;
	    @FXML private ComboBox comboRequirement;
	    @FXML private TextField searchRequirement;
	    @FXML private BarChart requirementChart;
	    @FXML private NumberAxis yAxisRequirementChart;
	    @FXML private Label requirementChildrenLabel;
	    @FXML private ListView requirementChildrenList;

	    
	    @FXML private Tab tabSystem;
	    @FXML private ListView<Label> systemList;
	    @FXML private ComboBox comboSystem;
	    @FXML private TextField searchSystem;
	    @FXML private BarChart systemChart;
	    @FXML private NumberAxis yAxisSystemChart;
	    @FXML private Label systemChildrenLabel;
	    @FXML private ListView systemChildrenList;

	    @FXML private Tab tabTest;

	    @FXML private Tab tabReview;

	    @FXML private Tab tabAnalysis;

	    @FXML private Tab tabVerification;
	    
	    
	    //------------
	    /** deactivates the childern list and label */
	    public void deactivateInterfaceChildren(Boolean key) {
	        interfaceChildrenList.setDisable(key);
//	        interfaceChildrenLabel.setDisable(key);
	        // clear the list and label
//	        interfaceChildrenLabel.setText("");
	        interfaceChildrenList.getItems().clear();
	    }

	    /** deactivates the childern list and label */
	    public void deactivateItemChildren(Boolean key) {
	        itemChildrenList.setDisable(key);
//	        itemChildrenLabel.setDisable(key);
	        // clear the list and label
//	        itemChildrenLabel.setText("");
	        itemChildrenList.getItems().clear();
	    }
	    
	    /** deactivates the childern list and label */
	    public void deactivateRequirementChildren(Boolean key) {
	        requirementChildrenList.setDisable(key);
//	        requirementChildrenLabel.setDisable(key);
	        // clear the list and label
//	        requirementChildrenLabel.setText("");
	        requirementChildrenList.getItems().clear();
	    }
	    
	    /** deactivates the childern list and label */
	    public void deactivateSystemChildren(Boolean key) {
	        systemChildrenList.setDisable(key);
//	        systemChildrenLabel.setDisable(key);
	        // clear the list and label
//	        systemChildrenLabel.setText("");
	        systemChildrenList.getItems().clear();
	    }
	    
	    /**
	     * Used to initialize the variables from the caller view's fxml controller
	     *
	     * @param procId
	     * @param objId
	     */
	    public void prepareView(String procId, String objId) {

	    	
	    	
	    	
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
	        tabDocument.setDisable(true);
	        tabInterface.setDisable(true);
	        tabItem.setDisable(true);
	        tabRequirement.setDisable(true);
	        tabSystem.setDisable(true);
	        tabTest.setDisable(true);
	        tabReview.setDisable(true);
	        tabAnalysis.setDisable(true);
	        tabVerification.setDisable(true);

	        // disable all child lists
	        deactivateInterfaceChildren(true);
	        deactivateItemChildren(true);
	        deactivateRequirementChildren(true);
	        deactivateSystemChildren(true);

	    }

	    @FXML
	    private void btnProcessAction(ActionEvent event) throws Exception {
	        // Set the stage with the other fxml
	        FXMLLoader processViewLoader =
	                Arp4754ViewsManager.setNewFxmlToStage("resources/fxml/arp4754/ProcessView.fxml");

	        // initialize variables in the ReportTableView page
	        ProcessViewHandler processViewLoaderClassObj = processViewLoader.getController();
	        processViewLoaderClassObj.prepareView(currentProcessId);
	    }

	    @FXML
	    private void btnHomeAction(ActionEvent event) throws Exception {
	        // Set the stage with the other fxml
	        Arp4754ViewsManager.setNewFxmlToStage("resources/fxml/arp4754/MainView.fxml");
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
}
