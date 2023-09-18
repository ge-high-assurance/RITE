/**
 * 
 */
package com.ge.research.rack.arp4754.viewHandlers;

import java.util.ArrayList;
import java.util.List;

import com.ge.research.rack.arp4754.structures.Category;
import com.ge.research.rack.arp4754.structures.DAPlan;
import com.ge.research.rack.arp4754.structures.Evidence;
import com.ge.research.rack.arp4754.utils.DAPlanUtils;
import com.ge.research.rack.arp4754.utils.ViewUtils;
import com.ge.research.rack.arp4754.viewManagers.Arp4754ViewsManager;
import com.ge.research.rack.do178c.structures.Requirement;
import com.ge.research.rack.do178c.utils.LogicUtils;
import com.ge.research.rack.do178c.utils.ReportViewUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
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

	    //-------------------------------------------------------------------
	    /** deactivates the requirement childern list and label */
	    public void deactivateReqChildren(Boolean key) {
	        requirementChildrenList.setDisable(key);
	        requirementChildrenLabel.setDisable(key);
	        // clear the list and label
	        requirementChildrenLabel.setText("");
	        requirementChildrenList.getItems().clear();
	    }
	    
	    //------------------------------------------------------------------
	    public void initializeComboReq() {

	        comboRequirement.setPromptText("Filter");

	        // TODO: write logic for other objectives
	        if (currentObjObject.getId().equalsIgnoreCase("objective-2-6")) {

	            // add the categories to the combo
	        	comboRequirement.getItems().add("All");
	        	comboRequirement.getItems().add("Has Trace");
	            comboRequirement.getItems().add("Has Allocation");
	            comboRequirement.getItems().add("Has Both");
	            
	        }

	    }
	    
	    //-------------------------------------------------------------------	
	    public void populateSubGraphs() {
	    	
    		System.out.println("Objective " + currentObjObject.getId());
	    	
	    	if(currentObjObject.getGraphs().getSysReqGraphData().getBuckets().size() > 0) {
	    			    		
	    		requirementChart.getData().clear();
		        requirementChart.setVisible(true);
	    		
	            XYChart.Series stat = new XYChart.Series();
	            int maxVal = 0;

	            for(Category bucket : currentObjObject.getGraphs().getSysReqGraphData().getBuckets()) {
	            	
		    		System.out.println(bucket.getName());
	            	
	            	
	            	Data bar = ReportViewUtils.createIntDataBar(bucket.getName(), bucket.getValue());
		            stat.getData().add(bar);
	            	if(bucket.getValue()>maxVal) {
	            		maxVal = bucket.getValue();
	            	}
	            }
	            
	            requirementChart.getData().add(stat);

	            requirementChart.setTitle(currentObjObject.getGraphs().getSysReqGraphData().getGraphTitle());
	            // scaling
	            // *** This can help make integral ticks on Y axis ***
	            yAxisRequirementChart.setLowerBound(0);
	            yAxisRequirementChart.setUpperBound(maxVal);
	            yAxisRequirementChart.setTickUnit(1);
	    	}

	    	if(currentObjObject.getGraphs().getSystemGraphData().getBuckets().size() > 0) {
	    		
	    		systemChart.getData().clear();
	    		systemChart.setVisible(true);
	    		
	    		
	            XYChart.Series stat = new XYChart.Series();
	            int maxVal = 0;

	            for(Category bucket : currentObjObject.getGraphs().getSystemGraphData().getBuckets()) {	            	

	            	System.out.println(bucket.getName());

	            	
	            	Data bar = ReportViewUtils.createIntDataBar(bucket.getName(), bucket.getValue());
		            stat.getData().add(bar);
	            	if(bucket.getValue()>maxVal) {
	            		maxVal = bucket.getValue();
	            	}
	            }
	            
	            systemChart.getData().add(stat);

	            systemChart.setTitle(currentObjObject.getGraphs().getSystemGraphData().getGraphTitle());
	            // scaling
	            // *** This can help make integral ticks on Y axis ***
	            yAxisSystemChart.setLowerBound(0);
	            yAxisSystemChart.setUpperBound(maxVal);
	            yAxisSystemChart.setTickUnit(1);
	    	}
	    	
	    	
	    	if(currentObjObject.getGraphs().getInterfaceGraphData().getBuckets().size() > 0) {
	    		
	    		interfaceChart.getData().clear();
	    		interfaceChart.setVisible(true);
	    		
	    		
	            XYChart.Series stat = new XYChart.Series();
	            int maxVal = 0;

	            for(Category bucket : currentObjObject.getGraphs().getInterfaceGraphData().getBuckets()) {	            	

	            	System.out.println(bucket.getName());

	            	
	            	Data bar = ReportViewUtils.createIntDataBar(bucket.getName(), bucket.getValue());
		            stat.getData().add(bar);
	            	if(bucket.getValue()>maxVal) {
	            		maxVal = bucket.getValue();
	            	}
	            }
	            
	            interfaceChart.getData().add(stat);

	            interfaceChart.setTitle(currentObjObject.getGraphs().getInterfaceGraphData().getGraphTitle());
	            // scaling
	            // *** This can help make integral ticks on Y axis ***
	            yAxisInterfaceChart.setLowerBound(0);
	            yAxisInterfaceChart.setUpperBound(maxVal);
	            yAxisInterfaceChart.setTickUnit(1);
	    	}
	    	
	    	if(currentObjObject.getGraphs().getItemReqGraphData().getBuckets().size() > 0) {
	    		
	    		requirementChart.getData().clear();
	    		requirementChart.setVisible(true);
	    		
	            XYChart.Series stat = new XYChart.Series();
	            int maxVal = 0;

	            for(Category bucket : currentObjObject.getGraphs().getItemReqGraphData().getBuckets()) {	            	

	            	System.out.println(bucket.getName());

	            	
	            	Data bar = ReportViewUtils.createIntDataBar(bucket.getName(), bucket.getValue());
		            stat.getData().add(bar);
	            	if(bucket.getValue()>maxVal) {
	            		maxVal = bucket.getValue();
	            	}
	            }
	            
	            requirementChart.getData().add(stat);

	            requirementChart.setTitle(currentObjObject.getGraphs().getItemReqGraphData().getGraphTitle());
	            // scaling
	            // *** This can help make integral ticks on Y axis ***
	            yAxisRequirementChart.setLowerBound(0);
	            yAxisRequirementChart.setUpperBound(maxVal);
	            yAxisRequirementChart.setTickUnit(1);
	    	}

	    }
	    
	    
	    //-------------------------------------------------------------------	
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
	        requirementChildrenLabel.setDisable(key);
	        // clear the list and label
	        requirementChildrenLabel.setText("");
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
            
            System.out.println(filterKey);

            if(currentObjObject.getId().equalsIgnoreCase("objective-2-6")) {
                // TODO: objective-based setting of children
                // store children releationship for this objective
                requirementChildrenRelation = "Allocated To Item";
    	    	
                for(Evidence itemReq : currentObjObject.getOutputs().getItemReqObjs()){
                    if (filterKey.equalsIgnoreCase("All")
                            && ((searchKey == null) 
                            || (itemReq.getId().contains(searchKey)))) {
                        Label evidenceLabel = new Label();

                        System.out.println("in all");

                        
                        String evidenceText =
                                itemReq.getId()
                                + "| Description: " + itemReq.getDescription() 
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
                    if (filterKey.equalsIgnoreCase("Has Trace")
                            && ((searchKey == null) 
                            || (itemReq.getId().contains(searchKey)))
                            && (itemReq.getTracesUp().size()>0)) {
                        Label evidenceLabel = new Label();

                        System.out.println("in trace");

                        
                        String evidenceText =
                                itemReq.getId()
                                + "| Description: " + itemReq.getDescription() 
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
                    if (filterKey.equalsIgnoreCase("Has Allocation")
                            && ((searchKey == null) 
                            || (itemReq.getId().contains(searchKey)))
                            && (itemReq.getAllocatedTo().size()>0)) {
                        Label evidenceLabel = new Label();

                        System.out.println("in allocated");

                        
                        String evidenceText =
                                itemReq.getId()
                                + "| Description: " + itemReq.getDescription() 
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
                    if (filterKey.equalsIgnoreCase("Has Both")
                            && ((searchKey == null) 
                            || (itemReq.getId().contains(searchKey)))
                            && (itemReq.getTracesUp().size()>0)
                            && (itemReq.getAllocatedTo().size()>0)) {
                        Label evidenceLabel = new Label();

                        System.out.println("in both");

                        
                        String evidenceText =
                                itemReq.getId()
                                + "| Description: " + itemReq.getDescription() 
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
		    		
		            initializeComboReq();
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
	    
	    //-------------------------------------------------------------------	
	    
	    
	    
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
	        //TODO: just like the subcharts, if the data to show is embedded in the objectives directory, we may be able to get rid of these very specific codes
	        populateTabDocument();
	        populateTabInterface();
	        populateTabItem();
	        populateTabRequirement();
	        populateTabSystem();
	        populateTabTest();
	        populateTabReview();
	        populateTabAnalysis();
	        populateTabVerification();
	        
	        // Populate the subgraphs as as appropriate
	        populateSubGraphs();
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
	        
	        // hide all child charts
	        interfaceChart.setVisible(false);
	        itemChart.setVisible(false);
	        requirementChart.setVisible(false);
	        systemChart.setVisible(false);
	        
	        
	        // code for the searchbars
	        searchRequirement
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
	                                    populateListRequirement("All", newValue);

	                                } else { // newvalue is null
	                                    // call listview to display all elements (default settings)
	                                	populateListRequirement("All", null);
	                                }
	                                // reset the combo to "All"
	                                comboRequirement.getSelectionModel().selectFirst();
	                            }
	                        });

	    }
	    
	    @FXML
	    private void reqListSelectionAction(MouseEvent event) {

	        // The selected label
	        Label selectedLabel = requirementList.getSelectionModel().getSelectedItem();

	        if (selectedLabel != null) {

	            // get selection
	            String selectedReqLine = selectedLabel.getText();
	            System.out.println("The selected req line: " + selectedReqLine);

	            //TODO: below is ad hoc code. Make generic
	            if(currentObjObject.getId().equalsIgnoreCase("objective-2-6")) {
		            // Contextmenu for reqList
		            ContextMenu reqListContext = new ContextMenu();
		            MenuItem menuItemShowTrace = new MenuItem("Show Traces");
		            MenuItem menuItemShowAllocation = new MenuItem("Show Allocations");
		            reqListContext.getItems().add(menuItemShowTrace);
		            reqListContext.getItems().add(menuItemShowAllocation);
		            requirementList.setContextMenu(reqListContext);
		            // show source of requirement in reqchildren if right click context selected
		            menuItemShowTrace.setOnAction(
		                    (rightClickEvent) -> {
		                        // get the requirement id
		                        String[] reqIdWithSpace = selectedReqLine.split("\\|");
		                        String reqId = reqIdWithSpace[0].trim();

		                        // activate the label and list and put string in label
		                        deactivateRequirementChildren(false);
		                        requirementChildrenLabel.setText("Traces:");

		                        // find the requirement object
		                        for (Evidence reqObj : currentObjObject.getOutputs().getItemReqObjs()) {
		                            // set children list to the sources, if any exist
		                            if (reqObj.getId().equals(reqId)
		                                    && reqObj.getTracesUp() != null) {
		                                for (Evidence trace : reqObj.getTracesUp()) {
		                                    requirementChildrenList.getItems().add(trace.getId());
		                                }
		                            }
		                        }
		                    });
		            menuItemShowAllocation.setOnAction(
		                    (rightClickEvent) -> {
		                        // get the requirement id
		                        String[] reqIdWithSpace = selectedReqLine.split("\\|");
		                        String reqId = reqIdWithSpace[0].trim();

		                        // activate the label and list and put string in label
		                        deactivateRequirementChildren(false);
		                        requirementChildrenLabel.setText("Allocations:");

		                        // find the requirement object
		                        for (Evidence reqObj : currentObjObject.getOutputs().getItemReqObjs()) {
		                            // set children list to the sources, if any exist
		                            if (reqObj.getId().equals(reqId)
		                                    && reqObj.getTracesUp() != null) {
		                                for (Evidence allocation : reqObj.getAllocatedTo()) {
		                                    requirementChildrenList.getItems().add(allocation.getId());
		                                }
		                            }
		                        }
		                    });

		        }
	            }

	    }
	    
	    @FXML
	    private void comboReqAction(ActionEvent event) throws Exception {

	        // clear the search bar and children
	    	searchRequirement.clear();
	        deactivateReqChildren(true);

	        String key = (String) comboRequirement.getValue();
	        // Clear and repopulate the list depending on key
	        populateListRequirement(key, null);
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
