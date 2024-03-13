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
package com.ge.research.rack.arp4754.wireframe.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.ge.research.rack.arp4754.wireframe.Arp4754AWireframeDAPWriter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;

public class Arp4754WireframeMainViewHandler {

    // MAIN PLAN TAB
    @FXML private Tab tabMain;
    @FXML private TextField txtCompany;
    @FXML private TextField txtCreator;
    @FXML private TextField txtUseCaseLabel;
    @FXML private TextField txtUseCaseDescription;
    @FXML private MenuButton menuAssuranceLevel;
    @FXML private Button btnComplete;

    // OBJECTIVES QUERIES TAB
    @FXML private Tab tabObjectives;
    @FXML private ListView<String> lvQueries;
    @FXML private Button btnAddQuery;
    @FXML private Button btnRemoveQuery;

    // CONFIGURATION TAB
    @FXML private Tab tabConfiguration;
    @FXML private TextField txtID;
    @FXML private MenuButton menuDerivedItemReqs;
    @FXML private MenuButton menuDerivedSystemReqs;
    @FXML private MenuButton menuInterface;
    @FXML private MenuButton menuInterfaceInput;
    @FXML private MenuButton menuInterfaceOutput;
    @FXML private MenuButton menuItem;
    @FXML private MenuButton menuItemReqs;
    @FXML private MenuButton menuSystemReqs;
    @FXML private MenuButton menuSystem;
    @FXML private MenuButton menuSystemDesignDesc;
    @FXML private MenuButton menuReqCompleteReview;
    @FXML private MenuButton menuReqTraceabilityReview;
    @FXML private Button btnFetch;

    @FXML private MenuItem menuDerivedItemReqsCustom;
    @FXML private MenuItem menuDerivedSystemReqsCustom;
    @FXML private MenuItem menuInterfaceCustom;
    @FXML private MenuItem menuInterfaceInputCustom;
    @FXML private MenuItem menuInterfaceOutputCustom;
    @FXML private MenuItem menuItemCustom;
    @FXML private MenuItem menuItemReqsCustom;
    @FXML private MenuItem menuSystemReqsCustom;
    @FXML private MenuItem menuSystemCustom;
    @FXML private MenuItem menuSystemDesignDescCustom;
    @FXML private MenuItem menuReqCompleteReviewCustom;
    @FXML private MenuItem menuReqTraceabilityReviewCustom;

    List<String> requirements = new LinkedList<String>();
    List<String> systems = new LinkedList<String>();
    List<String> documents = new LinkedList<String>();
    List<String> reviews = new LinkedList<String>();
    List<String> interfaces = new LinkedList<String>();

    @FXML
    private void initialize() {
        // Default item values
        menuAssuranceLevel.setText("LEVEL A");
        menuInterface.setText("INTERFACE");
        menuInterfaceInput.setText("Input");
        menuInterfaceOutput.setText("Output");
    }

    @FXML
    private void assuranceLevelAction(ActionEvent event) throws Exception {
        menuAssuranceLevel.setText(((MenuItem) event.getSource()).getText());
    }
    
    private boolean checkField(String str, String field, String tab) {
    	if (str != null && !str.isEmpty() && !str.isBlank()) {
    		return true;
    	}

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validate Input");
        alert.setHeaderText("Missing the " + field + " entry in the " + tab + " tab.");
        alert.showAndWait();
    	return false;
    }
    
    private boolean checkObjectives() {
    	if (lvQueries.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validate Objectives");
            alert.setHeaderText("No objectives are defined in the Objectives tab.");
            alert.showAndWait();
        	return false;
    	}

    	return true;
    }

    @FXML
    private void btnCompleteAction(ActionEvent event) throws Exception {
        // Check all items and generate DAP and configuration SADL files
    	boolean check =
    		checkField(txtUseCaseLabel.getText(), "Use Case", "Plan") &&
    		checkField(txtUseCaseDescription.getText(), "Description", "Plan") &&
    		checkField(menuAssuranceLevel.getText(), "Assurance Level", "Plan") &&
    		checkObjectives() &&
    		checkField(txtID.getText(), "ID", "Configuration") &&
    		checkField(menuDerivedItemReqs.getText(), "Derived Item Requirements", "Configuration") &&
    		checkField(menuDerivedSystemReqs.getText(), "Derived System Requirements", "Configuration") &&
    		checkField(menuInterface.getText(), "Interface", "Configuration") &&
    		checkField(menuInterfaceInput.getText(), "Interface Input", "Configuration") &&
    		checkField(menuInterfaceOutput.getText(), "Interface Output", "Configuration") &&
    		checkField(menuItem.getText(), "Item", "Configuration") &&
    		checkField(menuItemReqs.getText(), "Item Requirements", "Configuration") &&
    		checkField(menuSystemReqs.getText(), "System Requirements", "Configuration") &&
    		checkField(menuSystem.getText(), "System", "Configuration") &&
    		checkField(menuSystemDesignDesc.getText(), "System Design Description", "Configuration") &&
    		checkField(menuReqCompleteReview.getText(), "Requirements Complete Review", "Configuration") &&
    		checkField(menuReqTraceabilityReview.getText(), "Requirements Traceability Review", "Configuration");
    		
    		if (check) {
    			DirectoryChooser dc = new DirectoryChooser();
    			File directory = dc.showDialog(Arp4754WireframeMainViewManager.stage);
    			if (directory == null) {
    				return;
    			}
    			
    			Arp4754AWireframeDAPWriter writer = new Arp4754AWireframeDAPWriter(directory, menuSystem.getText());
    			writer.setID(txtUseCaseLabel.getText());
    			writer.setDescription(txtUseCaseDescription.getText());
    			writer.setLevel(menuAssuranceLevel.getText());
    			writer.setObjectives(lvQueries.getItems());
    			
    			writer.setConfigID(txtID.getText());
    			writer.setDerivedItemReqs(menuDerivedItemReqs.getText());
    			writer.setDerivedSysReqs(menuDerivedSystemReqs.getText());
    			writer.setInterface(menuInterface.getText());
    			writer.setInterfaceInput(menuInterfaceInput.getText());
    			writer.setInterfaceOutput(menuInterfaceOutput.getText());
    			writer.setItem(menuItem.getText());
    			writer.setItemReqs(menuItemReqs.getText());
    			writer.setSysReqs(menuSystemReqs.getText());
    			writer.setSystemDesignDescription(menuSystemDesignDesc.getText());
    			writer.setReqCompleteReview(menuReqCompleteReview.getText());
    			writer.setReqTraceabilityReview(menuReqTraceabilityReview.getText());

    			if (writer.write()) {
    	            Alert alert = new Alert(Alert.AlertType.ERROR);
    	            alert.setTitle("Writing SADL Files");
    	            alert.setHeaderText(writer.getError());
    	            alert.showAndWait();
    			} else {
    				Arp4754WireframeMainViewManager.close();
    			}
    		}
    }

    @FXML
    private void btnAddQueryAction(ActionEvent event) throws Exception {
        // Add a new objective query
        // Set the stage with the other fxml
        FXMLLoader objViewLoader =
        		Arp4754WireframeObjectivesViewManager.setNewFxmlToStage(
                        "resources/fxml/arp4754/WireframeObjectivesView.fxml");

        // initialize variables in the objectives query page
        Arp4754WireframeObjectivesViewHandler handler =
        		objViewLoader.getController();
        
        Arp4754WireframeObjectivesViewManager.show();
        String txt = handler.getText();
        if (txt == null) {
        	return;
        }

        boolean notdone = true;
        int index = 0;
        List<String> list = lvQueries.getItems();
        while (notdone && index < list.size()) {
            if (txt.compareTo(list.get(index)) <= 0) {
                notdone = false;
            } else {
                index++;
            }
        }

        if (notdone) {
            lvQueries.getItems().add(txt);
        } else {
            lvQueries.getItems().add(index, txt);
        }
    }

    @FXML
    private void btnRemoveQueryAction(ActionEvent event) throws Exception {
        // Remove the selected objective queries
        List<String> items = lvQueries.getSelectionModel().getSelectedItems();
        if (!items.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Remove Queries");
            alert.setHeaderText("Are you sure you want to remove the selected queries?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get().equals(ButtonType.OK)) {
                for (String item : items) {
                    lvQueries.getItems().remove(item);
                }
            }
        }
    }

    private String addCustomConfiguration(String header, String str) {
        // Get a custom configuration item label from user
        TextInputDialog td = new TextInputDialog(str);
        td.setHeaderText(header);
        Optional<String> txt = td.showAndWait();
        if (txt.isPresent()) {
            return txt.get();
        }

        return null;
    }

    @FXML
    private void addDerivedItemReq(ActionEvent event) throws Exception {
        String str =
                addCustomConfiguration("Derived Item Requirements", menuDerivedItemReqs.getText());
        if (str != null) {
            menuDerivedItemReqs.setText(str);
        }
    }

    @FXML
    private void addDerivedSystemReqs(ActionEvent event) throws Exception {
        String str =
                addCustomConfiguration(
                        "Derived System Requirements", menuDerivedSystemReqs.getText());
        if (str != null) {
            menuDerivedSystemReqs.setText(str);
        }
    }

    @FXML
    private void addInterface(ActionEvent event) throws Exception {
        String str = addCustomConfiguration("Interface", menuInterface.getText());
        if (str != null) {
            menuInterface.setText(str);
        }
    }

    @FXML
    private void addInterfaceInput(ActionEvent event) throws Exception {
        String str = addCustomConfiguration("Interface Input", menuInterfaceInput.getText());
        if (str != null) {
            menuInterfaceInput.setText(str);
        }
    }

    @FXML
    private void addInterfaceOutput(ActionEvent event) throws Exception {
        String str = addCustomConfiguration("Interface Output", menuInterfaceOutput.getText());
        if (str != null) {
            menuInterfaceOutput.setText(str);
        }
    }

    @FXML
    private void addItem(ActionEvent event) throws Exception {
        String str = addCustomConfiguration("Item", menuItem.getText());
        if (str != null) {
            menuItem.setText(str);
        }
    }

    @FXML
    private void addItemReqs(ActionEvent event) throws Exception {
        String str = addCustomConfiguration("Item Requirements", menuItemReqs.getText());
        if (str != null) {
            menuItemReqs.setText(str);
        }
    }

    @FXML
    private void addSystemReqs(ActionEvent event) throws Exception {
        String str = addCustomConfiguration("System Requirement", menuSystemReqs.getText());
        if (str != null) {
            menuSystemReqs.setText(str);
        }
    }

    @FXML
    private void addSystem(ActionEvent event) throws Exception {
        String str = addCustomConfiguration("System", menuSystem.getText());
        if (str != null) {
            menuSystem.setText(str);
        }
    }

    @FXML
    private void addSystemDesignDesc(ActionEvent event) throws Exception {
        String str =
                addCustomConfiguration("System Design Description", menuSystemDesignDesc.getText());
        if (str != null) {
            menuSystemDesignDesc.setText(str);
        }
    }

    @FXML
    private void addReqCompleteReview(ActionEvent event) throws Exception {
        String str =
                addCustomConfiguration(
                        "Requirements Completion Review", menuReqCompleteReview.getText());
        if (str != null) {
            menuReqCompleteReview.setText(str);
        }
    }

    @FXML
    private void addReqTraceabilityReview(ActionEvent event) throws Exception {
        String str =
                addCustomConfiguration(
                        "Requirements Traceability Review", menuReqTraceabilityReview.getText());
        if (str != null) {
            menuReqTraceabilityReview.setText(str);
        }
    }

    private void menuEntryAction(ActionEvent event) {
        // Update configuration for specific item
        MenuItem item = (MenuItem) event.getSource();

        if (item == menuDerivedItemReqsCustom || menuDerivedItemReqs.getItems().contains(item)) {
            menuDerivedItemReqs.setText(item.getText());
        } else if (item == menuDerivedSystemReqsCustom
                || menuDerivedSystemReqs.getItems().contains(item)) {
            menuDerivedSystemReqs.setText(item.getText());
        } else if (item == menuInterfaceCustom || menuInterface.getItems().contains(item)) {
            menuInterface.setText(item.getText());
        } else if (item == menuInterfaceInputCustom
                || menuInterfaceInput.getItems().contains(item)) {
            menuInterfaceInput.setText(item.getText());
        } else if (item == menuInterfaceOutputCustom
                || menuInterfaceOutput.getItems().contains(item)) {
            menuInterfaceOutput.setText(item.getText());
        } else if (item == menuItemCustom || menuItem.getItems().contains(item)) {
            menuItem.setText(item.getText());
        } else if (item == menuItemReqsCustom || menuItemReqs.getItems().contains(item)) {
            menuItemReqs.setText(item.getText());
        } else if (item == menuSystemReqsCustom || menuSystemReqs.getItems().contains(item)) {
            menuSystemReqs.setText(item.getText());
        } else if (item == menuSystemCustom || menuSystem.getItems().contains(item)) {
            menuSystem.setText(item.getText());
        } else if (item == menuSystemDesignDescCustom
                || menuSystemDesignDesc.getItems().contains(item)) {
            menuSystemDesignDesc.setText(item.getText());
        } else if (item == menuReqCompleteReviewCustom
                || menuReqCompleteReview.getItems().contains(item)) {
            menuReqCompleteReview.setText(item.getText());
        } else if (item == menuReqTraceabilityReviewCustom
                || menuReqTraceabilityReview.getItems().contains(item)) {
            menuReqTraceabilityReview.setText(item.getText());
        }
    }

    private MenuItem newMenuItem(String str) {
        // Default new MenuItem
        MenuItem mi = new MenuItem();
        mi.setText(str);
        mi.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent action) {
                        menuEntryAction(action);
                    }
                });
        return mi;
    }

    private void resetMenus() {
        // Fill in default Configuration name
        if (txtID.getText().isEmpty() || txtID.getText().isBlank()) {
            String cname = txtUseCaseLabel.getText();
            if (cname != null && !cname.isEmpty() && !cname.isBlank()) {
                txtID.setText(cname + "Config");
            }
        }

        // Clear menus
        menuDerivedItemReqs.getItems().clear();
        menuDerivedSystemReqs.getItems().clear();
        menuInterface.getItems().clear();
        // menuInterfaceInput.getItems().clear();
        // menuInterfaceOutput.getItems().clear();
        menuItem.getItems().clear();
        menuItemReqs.getItems().clear();
        menuSystemReqs.getItems().clear();
        menuSystem.getItems().clear();
        menuSystemDesignDesc.getItems().clear();
        menuReqCompleteReview.getItems().clear();
        menuReqTraceabilityReview.getItems().clear();

        // Re-populate menus with updated object lists
        for (String str : requirements) {
            menuDerivedItemReqs.getItems().add(newMenuItem(str));
            menuDerivedSystemReqs.getItems().add(newMenuItem(str));
            menuItemReqs.getItems().add(newMenuItem(str));
            menuSystemReqs.getItems().add(newMenuItem(str));
        }

        for (String str : documents) {
            menuSystemDesignDesc.getItems().add(newMenuItem(str));
        }

        for (String str : systems) {
            menuItem.getItems().add(newMenuItem(str));
            menuSystem.getItems().add(newMenuItem(str));
        }

        for (String str : reviews) {
            menuReqCompleteReview.getItems().add(newMenuItem(str));
            menuReqTraceabilityReview.getItems().add(newMenuItem(str));
        }

        menuInterface.getItems().add(newMenuItem("INTERFACE"));
        for (String str : interfaces) {
            menuInterface.getItems().add(newMenuItem(str));
        }

        menuDerivedItemReqs.getItems().add(menuDerivedItemReqsCustom);
        menuDerivedSystemReqs.getItems().add(menuDerivedSystemReqsCustom);
        menuInterface.getItems().add(menuInterfaceCustom);
        // menuInterfaceInput.getItems().add(menuInterfaceInputCustom);
        // menuInterfaceOutput.getItems().add(menuInterfaceOutputCustom);
        menuItem.getItems().add(menuItemCustom);
        menuItemReqs.getItems().add(menuItemReqsCustom);
        menuSystemReqs.getItems().add(menuSystemReqsCustom);
        menuSystem.getItems().add(menuSystemCustom);
        menuSystemDesignDesc.getItems().add(menuSystemDesignDescCustom);
        menuReqCompleteReview.getItems().add(menuReqCompleteReviewCustom);
        menuReqTraceabilityReview.getItems().add(menuReqTraceabilityReviewCustom);
    }

    private String removeComments(String line) {
        int start = 0;
        while (true) {
            int index = line.indexOf("//", start);
            if (index < 0) {
                // No comments found
                return new String(line);
            }

            int uri = line.indexOf("\"http://", start);
            if (uri < 0) {
                // No http found; Return uncommented string
                return line.substring(start, index);
            }

            if (uri < index) {
                int next = line.indexOf('"', uri + 8);
                if (next < 0) {
                    // This is actually an error
                    return new String(line);
                }

                start = next + 1;
            } else {
                // http inside comments
                return line.substring(start, index);
            }
        }
    }

    private boolean parseObjects(String toks[], boolean reset) {
        int index = 0;
        boolean changed = false;
        List<String> tempRequirements = new LinkedList<String>();
        List<String> tempSystems = new LinkedList<String>();
        List<String> tempDocuments = new LinkedList<String>();
        List<String> tempReviews = new LinkedList<String>();
        List<String> tempInterfaces = new LinkedList<String>();
        while (index < toks.length - 6) {
            if (toks[index + 1].compareToIgnoreCase("is") == 0
                    && toks[index + 2].compareToIgnoreCase("a") == 0
                    && toks[index + 3].compareToIgnoreCase("type") == 0
                    && toks[index + 4].compareToIgnoreCase("of") == 0) {
                if (toks[index + 5].indexOf("REQUIREMENT") == 0) {
                    tempRequirements.add(toks[index]);
                    changed = true;
                } else if (toks[index + 5].indexOf("SYSTEM") == 0) {
                    tempSystems.add(toks[index]);
                    changed = true;
                } else if (toks[index + 5].indexOf("DOCUMENT") == 0) {
                    tempDocuments.add(toks[index]);
                    changed = true;
                } else if (toks[index + 5].indexOf("REVIEW") == 0) {
                    tempReviews.add(toks[index]);
                    changed = true;
                } else if (toks[index + 5].indexOf("INTERFACE") == 0) {
                    tempInterfaces.add(toks[index]);
                    changed = true;
                }

                index += 6;
            } else {
                index++;
            }
        }

        if (changed) {
            if (reset) {
                requirements.clear();
                systems.clear();
                documents.clear();
                interfaces.clear();
                reviews.clear();
            }

            requirements.addAll(tempRequirements);
            systems.addAll(tempSystems);
            documents.addAll(tempDocuments);
            interfaces.addAll(tempInterfaces);
            reviews.addAll(tempReviews);
            return false;
        }

        return reset;
    }

    private boolean checkSADL(File sadlfile, boolean reset) {
        String str = new String();
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(sadlfile));
            line = reader.readLine();
            while (line != null) {
                stringBuilder.append(removeComments(line));
                stringBuilder.append(' ');
                line = reader.readLine();
            }

            str = stringBuilder.toString();
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading SADL file " + sadlfile.getName());
            return reset;
        }

        return parseObjects(str.split("\s+"), reset);
    }

    @FXML
    private void btnFetchAction(ActionEvent event) throws Exception {
        DirectoryChooser dc = new DirectoryChooser();
        java.io.File selected = dc.showDialog(Arp4754WireframeMainViewManager.stage);
        if (selected != null && selected.exists() && selected.canRead() && selected.isDirectory()) {
            File[] sadlfiles =
                    selected.listFiles(
                            new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String name) {
                                    return name.toLowerCase().endsWith(".sadl");
                                }
                            });

            boolean reset = true;
            for (File fn : sadlfiles) {
                reset = checkSADL(fn, reset);
            }

            if (!reset) {
                resetMenus();
            }
        }
    }
}
