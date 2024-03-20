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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

public class Arp4754WireframeObjectivesViewHandler {
	
	@FXML Label txtObjectiveDescription;
	@FXML TextField txtObjectiveQuery;
	@FXML Button btnObjectiveCancel;
	@FXML Button btnObjectiveDone;
	@FXML MenuButton menuObjective;
	boolean hasResult = false;

    @FXML
    private void initialize() {
    	
    }

    @FXML
    private void objectiveMenuAction(ActionEvent event) throws Exception {
    	String txt = ((MenuItem) event.getSource()).getText();
    	menuObjective.setText(txt);
    	txtObjectiveQuery.setText(txt + "-query");
    	hasResult = true;

    	if (txt.compareTo("Objective-1-1") == 0) {
    		txtObjectiveDescription.setText("System development and integral processes activities are defined.");
    	} else if (txt.compareTo("Objective-1-2") == 0) {
    		txtObjectiveDescription.setText("Transition criteria and inter-relationship among processes are defined.");
    	} else if (txt.compareTo("Objective-2-1") == 0) {
    		txtObjectiveDescription.setText("Aircraft-level functions, functional requirement, functional interfaces and assumptions are defined.");
    	} else if (txt.compareTo("Objective-2-2") == 0) {
    		txtObjectiveDescription.setText("Aircraft functions are allocated to systems.");
    	} else if (txt.compareTo("Objective-2-3") == 0) {
    		txtObjectiveDescription.setText("System requirements, including assumptions and system interfaces are defined.");
    	} else if (txt.compareTo("Objective-2-4") == 0) {
    		txtObjectiveDescription.setText("System derived requirements (including derived safety-related requirements) are defined and rationale explained.");
    	} else if (txt.compareTo("Objective-2-5") == 0) {
    		txtObjectiveDescription.setText("System architecture is defined.");
    	} else if (txt.compareTo("Objective-2-6") == 0) {
    		txtObjectiveDescription.setText("System requirements are allocated to the items.");
    	} else if (txt.compareTo("Objective-2-7") == 0) {
    		txtObjectiveDescription.setText("Appropriate item, system and aircraft integrations are performed.");
    	} else if (txt.compareTo("Objective-3-1") == 0) {
    		txtObjectiveDescription.setText("The aircraft/system functional hazard assessment is performed.");
    	} else if (txt.compareTo("Objective-3-2") == 0) {
    		txtObjectiveDescription.setText("The preliminary aircraft safety assessment is performed.");
    	} else if (txt.compareTo("Objective-3-3") == 0) {
    		txtObjectiveDescription.setText("The preliminary system safety assessment is performed.");
    	} else if (txt.compareTo("Objective-3-4") == 0) {
    		txtObjectiveDescription.setText("The common cause analyses are performed.");
    	} else if (txt.compareTo("Objective-3-5") == 0) {
    		txtObjectiveDescription.setText("The aircraft safety assessment is performed.");
    	} else if (txt.compareTo("Objective-3-6") == 0) {
    		txtObjectiveDescription.setText("The system safety assessment is performed.");
    	} else if (txt.compareTo("Objective-3-7") == 0) {
    		txtObjectiveDescription.setText("Independence requirements in functions, systems and items are captured.");
    	} else if (txt.compareTo("Objective-4-1") == 0) {
    		txtObjectiveDescription.setText("Aircraft, system, item requirements are complete and correct.");
    	} else if (txt.compareTo("Objective-4-2") == 0) {
    		txtObjectiveDescription.setText("Assumptions are justified and validated.");
    	} else if (txt.compareTo("Objective-4-3") == 0) {
    		txtObjectiveDescription.setText("Derived requirements are justified and validated.");
    	} else if (txt.compareTo("Objective-4-4") == 0) {
    		txtObjectiveDescription.setText("Requirements are traceable.");
    	} else if (txt.compareTo("Objective-4-6") == 0) {
    		txtObjectiveDescription.setText("Validation compliance substantiation is provided.");
    	} else if (txt.compareTo("Objective-5-1") == 0) {
    		txtObjectiveDescription.setText("Test or demonstration procedures are correct.");
    	} else if (txt.compareTo("Objective-5-2") == 0) {
    		txtObjectiveDescription.setText("Verification demonstrates intended function and confidence of no unintended function impacts to safety.");
    	} else if (txt.compareTo("Objective-5-3") == 0) {
    		txtObjectiveDescription.setText("Product implementation complies with aircraft and system requirements.");
    	} else if (txt.compareTo("Objective-5-4") == 0) {
    		txtObjectiveDescription.setText("Safety requirements are verified.");
    	} else if (txt.compareTo("Objective-5-5") == 0) {
    		txtObjectiveDescription.setText("Verification compliance substantiation is included.");
    	} else if (txt.compareTo("Objective-5-6") == 0) {
    		txtObjectiveDescription.setText("Assessment of deficiencies and their related impact on safety is identified.");
    	} else if (txt.compareTo("Objective-6-1") == 0) {
    		txtObjectiveDescription.setText("Configuration items are identified.");
    	} else if (txt.compareTo("Objective-6-2") == 0) {
    		txtObjectiveDescription.setText("Configuration baseline and derivatives are established.");
    	} else if (txt.compareTo("Objective-6-3") == 0) {
    		txtObjectiveDescription.setText("Problem reporting, change control, change review, and configuration status accounting are established.");
    	} else if (txt.compareTo("Objective-6-4") == 0) {
    		txtObjectiveDescription.setText("Archive and retrieval are established.");
    	} else if (txt.compareTo("Objective-7-1") == 0) {
    		txtObjectiveDescription.setText("Assurance is obtained that necessary plans are developed and maintained for all aspects of system certification.");
    	} else if (txt.compareTo("Objective-7-2") == 0) {
    		txtObjectiveDescription.setText("Development activities and processes are conducted in accordance with those plans.");
    	} else if (txt.compareTo("Objective-8-1") == 0) {
    		txtObjectiveDescription.setText("Compliance substantiation is provided.");
    	} else {
    		txtObjectiveDescription.setText("<Description>");
    		hasResult = false;
    	}
    }

    @FXML
    private void btnObectiveCancelAction(ActionEvent event) throws Exception {
        hasResult = false;
        Arp4754WireframeObjectivesViewManager.close();
    }

    @FXML
    private void btnObjectiveDoneAction(ActionEvent event) throws Exception {
        Arp4754WireframeObjectivesViewManager.close();
    }
    
    public String getText() {
    	if (hasResult) {
    		return txtObjectiveQuery.getText();
    	}

    	return null;
    }
    
    public String getObjective() {
		return menuObjective.getText();
    }

}
