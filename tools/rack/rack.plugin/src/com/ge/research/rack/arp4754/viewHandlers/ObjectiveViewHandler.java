/**
 * 
 */
package com.ge.research.rack.arp4754.viewHandlers;

import java.util.ArrayList;
import java.util.List;

import com.ge.research.rack.arp4754.structures.DAPlan;
import com.ge.research.rack.arp4754.structures.Evidence;
import com.ge.research.rack.arp4754.utils.DAPlanUtils;
import com.ge.research.rack.arp4754.utils.ViewUtils;
import com.ge.research.rack.arp4754.viewManagers.Arp4754ViewsManager;
import com.ge.research.rack.do178c.utils.ReportViewUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 * @author Saswata Paul
 *
 */
public class ObjectiveViewHandler {
	    private String currentProcessId;

	    private DAPlan.Process currentProcessObject;

	    private String currentObjId;

	    private DAPlan.Objective currentObjObject;

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
	    //-------------------------------------------------------------------
	    
	    
	    public void populateListInterface(String filterKey, String searchKey) {
	    		
            // clear the list and chart
            interfaceList.getItems().clear();
            interfaceChart.getData().clear();

            // store children releationship for this objective
            interfaceChildrenRelation = "I/O";
	    	
            
            // TODO: objective-based setting of children
            for(Evidence intrface : currentObjObject.getOutputs().getInterfaceObjs()){
                if (filterKey.equalsIgnoreCase("All")
                        && ((searchKey == null) || (intrface.getId().contains(searchKey)))) {
                    Label evidenceLabel = new Label();

                    String evidenceText =
                            intrface.getId()
                                    + " | Inputs: ";
                    for(Evidence input : intrface.getHasInputs()) {
                    	evidenceText = evidenceText + input.getId() + ", ";
                    }
                    evidenceText = evidenceText +  " | Outputs: ";
                    
                    for(Evidence output : intrface.getHasOutputs()) {
                    	evidenceText = evidenceText + output.getId() + ", ";
                    }

                    evidenceLabel.setText(evidenceText);
                    interfaceList.getItems().add(evidenceLabel);
                }
            }
	    }
	    

	    public void populateListItem(String filterKey, String searchKey) {
            // clear the list and chart
            itemList.getItems().clear();
            itemChart.getData().clear();

            
            // TODO: objective-based setting of children
            // store children releationship for this objective
            itemChildrenRelation = "";
	    	
            for(Evidence item : currentObjObject.getOutputs().getItemObjs()){
                if (filterKey.equalsIgnoreCase("All")
                        && ((searchKey == null) || (item.getId().contains(searchKey)))) {
                    Label evidenceLabel = new Label();

                    String evidenceText =
                            item.getId();

                    evidenceLabel.setText(evidenceText);
                    itemList.getItems().add(evidenceLabel);
                }
            }	    	
	    	
	    }

	    public void populateListRequirement(String filterKey, String searchKey) {
            // clear the list and chart
	    	requirementList.getItems().clear();
	    	requirementChart.getData().clear();

            if(currentObjObject.getId().equalsIgnoreCase("objective-2-2")) {
                // TODO: objective-based setting of children
                // store children releationship for this objective
            	requirementChildrenRelation = "AllocatedToSystem";
    	    	
                for(Evidence sysReq : currentObjObject.getOutputs().getSysReqObjs()){
                    if (filterKey.equalsIgnoreCase("All")
                            && ((searchKey == null) || (sysReq.getId().contains(searchKey)))) {
                        Label evidenceLabel = new Label();

                        String evidenceText =
                                sysReq.getId()
                                + " | Allocated To Systems: ";

                        for(Evidence system : sysReq.getAllocatedTo() ) {
                        	evidenceText = evidenceText + system.getId() +", ";
                        }
                        
                        evidenceLabel.setText(evidenceText);
                        requirementList.getItems().add(evidenceLabel);
                    }
                }	    	
            }
            

            if(currentObjObject.getId().equalsIgnoreCase("objective-2-6")) {
                // TODO: objective-based setting of children
                // store children releationship for this objective
                requirementChildrenRelation = "Allocated To System";
    	    	
                for(Evidence itemReq : currentObjObject.getOutputs().getItemReqObjs()){
                    if (filterKey.equalsIgnoreCase("All")
                            && ((searchKey == null) || (itemReq.getId().contains(searchKey)))) {
                        Label evidenceLabel = new Label();

                        String evidenceText =
                                itemReq.getId()
                                + " | Traces To: ";

                        for(Evidence sysReq : itemReq.getTracesUp() ) {
                        	evidenceText = evidenceText + sysReq.getId() +", ";
                        }

                    	evidenceText = evidenceText +" | Allocated To Items:  ";

                        for(Evidence item : itemReq.getAllocatedTo() ) {
                        	evidenceText = evidenceText + item.getId() +", ";
                        }
                        
                        evidenceLabel.setText(evidenceText);
                        requirementList.getItems().add(evidenceLabel);
                    }
                }	    	
            }
	    }
	    
	    
	    public void populateListSystem(String filterKey, String searchKey) {
    		
            // clear the list and chart
            systemList.getItems().clear();
            systemChart.getData().clear();

            // store children releationship for this objective
            systemChildrenRelation = "I/O";
	    	
            
            // TODO: objective-based setting of children
            for(Evidence system : currentObjObject.getOutputs().getSystemObjs()){
                if (filterKey.equalsIgnoreCase("All")
                        && ((searchKey == null) || (system.getId().contains(searchKey)))) {
                    Label evidenceLabel = new Label();

                    String evidenceText =
                            system.getId()
                                    + " | Interfaces: ";
                    for(Evidence intrface : system.getHasInterfaces()) {
                    	evidenceText = evidenceText + intrface.getId() + ", ";
                    }

                    evidenceLabel.setText(evidenceText);
                    systemList.getItems().add(evidenceLabel);
                }
            }
	    }
	    //-------------------------------------------------------------------	    

	    
	    public void populateTabDocument() {
	    	
	    }

	    public void populateTabInterface() {
	    	if(currentObjObject.getId().equalsIgnoreCase("objective-2-2")) {
		    	if(currentObjObject.getOutputs().getInterfaceObjs().size() > 0) {
		    		tabInterface.setDisable(false);
		    		populateListInterface("All", null);
		    	}	    		
	    	}
	    }

	    
	    public void populateTabItem() {
	    	if(currentObjObject.getId().equalsIgnoreCase("objective-2-6")) {
		    	if(currentObjObject.getOutputs().getItemObjs().size() > 0) {
		    		tabItem.setDisable(false);
		    		populateListItem("All", null);
		    	}	    		
	    	}
	    	
	    }
	    
	    
	    public void populateTabRequirement() {
	    	if (currentObjObject.getId().equalsIgnoreCase("objective-2-2")
	    			|| currentObjObject.getId().equalsIgnoreCase("objective-2-4")
	    			|| currentObjObject.getId().equalsIgnoreCase("objective-2-6")) {
		    	if((currentObjObject.getOutputs().getDerItemReqObjs().size() > 0)
		    			|| (currentObjObject.getOutputs().getDerSysReqObjs().size() > 0)
		    			|| (currentObjObject.getOutputs().getItemReqObjs().size() > 0)
		    			|| (currentObjObject.getOutputs().getSysReqObjs().size() > 0)) {
		    		tabRequirement.setDisable(false);
		    		populateListRequirement("All", null);
		    	}	    		
	    	}
	    	
	    }
	    
	    
	    
	    public void populateTabSystem() {
	    	if(currentObjObject.getId().equalsIgnoreCase("objective-2-2")) {
		    	if(currentObjObject.getOutputs().getSystemObjs().size() > 0) {
		    		tabSystem.setDisable(false);
		    		populateListSystem("All", null);
		    	}	    		
	    	}

	    	
	    }
	    
	    
	    
	    public void populateTabTest() {
	    	
	    }
	    
	    
	    
	    public void populateTabReview() {
	    	
	    }
	    
	    
	    
	    public void populateTabAnalysis() {
	    	
	    }
	    
	    
	    
	    public void populateTabVerification() {
	    	
	    }
	    
	    
	    
	    
	    
	    /**
	     * Used to initialize the variables from the caller view's fxml controller
	     *
	     * @param procId
	     * @param objId
	     */
	    public void prepareView(String procId, String objId) {

	    	// set the process and objective objects
	    	currentProcessId = procId;
	    	currentObjId = objId;
	        currentProcessObject = DAPlanUtils.getProcessObjectFromList(Arp4754ViewsManager.reportDataObj.getProcesses(), procId);
	        currentObjObject =  DAPlanUtils.getObjectiveObjectFromList(currentProcessObject.getObjectives(), objId);
	    	
	        
	        // Set the label text
	        labelProcessInfo.setText(
	                "Table " + currentProcessId + ": " + currentProcessObject.getDesc());

	        // set the label color
	        labelProcessInfo.setTextFill(ViewUtils.getProcessColor(currentProcessObject));

	        // Set the info label
	        labelObjInfo.setText(
	                "Objective "
	                        + currentObjId
	                        + ": "
	                        + currentObjObject.getDesc().replace("\"", ""));

	        // set the label color
	        labelObjInfo.setTextFill(ViewUtils.getObjectiveColor(currentObjObject));

	        
	        
	        // enable relevant tabs and populate them
	        populateTabDocument();
	        populateTabInterface();
	        populateTabItem();
	        populateTabRequirement();
	        populateTabSystem();
	        populateTabTest();
	        populateTabReview();
	        populateTabAnalysis();
	        populateTabVerification();
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
