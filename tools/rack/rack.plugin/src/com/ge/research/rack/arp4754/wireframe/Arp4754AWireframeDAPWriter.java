package com.ge.research.rack.arp4754.wireframe;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Arp4754AWireframeDAPWriter {

	// General parameters
	private String company = "";
	private String creator = "";
	
	// Plan SADL parameters
	private String id = "";
	private String desc = "";
	private String level = "";
	private List<String> objectives = new LinkedList<String>();
	private HashMap<String, String> objectiveMap = new HashMap<String, String>();
	
	// Config SADL parameters
	private String configID;
	private String derivedItemReqs;
	private String derivedSysReqs;
	private String interfaceField;
	private String interfaceInput;
	private String interfaceOutput;
	private String item;
	private String itemReqs;
	private String sysReqs;
	private String sysDesignDesc;
	private String reqCompleteReview;
	private String reqTraceabilityReview;

	// General parameters
	private String system;
	private File path;
	private String err = null;
	
	public Arp4754AWireframeDAPWriter(File fn, String sys) {
		path = fn;
		system = sys;
	}
	
	public void setID(String str) {
		id = str;
	}
	
	public void setGeneral(String s1, String s2) {
		company = s1;
		creator = s2;
	}

	public void setDescription(String str) {
		desc = str;
	}
	
	public void setLevel(String str) {
		level = str;
	}
	
	public void setObjectives(List<String> strs, HashMap<String, String> map) {
		objectives = strs;
		objectiveMap = map;
	}
	
	public void setConfigID(String str) {
		configID = str;
	}

	public void setDerivedItemReqs(String str) {
		derivedItemReqs = str;
	}

	public void setDerivedSysReqs(String str) {
		derivedSysReqs = str;
	}

	public void setInterface(String str) {
		interfaceField = str;
	}

	public void setInterfaceInput(String str) {
		interfaceInput = str;
	}

	public void setInterfaceOutput(String str) {
		interfaceOutput = str;
	}

	public void setItem(String str) {
		item = str;
	}

	public void setItemReqs(String str) {
		itemReqs = str;
	}

	public void setSysReqs(String str) {
		sysReqs = str;
	}

	public void setSystemDesignDescription(String str) {
		sysDesignDesc = str;
	}

	public void setReqCompleteReview(String str) {
		reqCompleteReview = str;
	}

	public void setReqTraceabilityReview(String str) {
		reqTraceabilityReview = str;
	}

	private String getObjective(String objective) {
		return objectiveMap.get(objective);
		
		//if (objective.matches("Objective-[1-8]-[1-8]-.*")) {
		//	return objective.substring(0, 13);
		//}
		//
		//return null;
	}
	
	public String getError() {
		return err;
	}
	
	private String comments() {
		return
			"//-- Company: " + company +
			"\n//-- Creator: " + creator +
			"\n//-- Use Case: " + id + 
			"\n//-- Description: " + desc + "\n\n";
	}
	
	private String writeConfig(String fn) {
		String dap = 
			"uri \"http://sadl.org/" + fn + "\" alias " + system.toLowerCase() + "config.\n\n";

		dap += comments();
		dap += configID + " is a CONFIGURATION\n";
		dap += "    with identifier \"" + configID +"\"\n";
		dap += "    with derivedItemRequirementAlias \"" + derivedItemReqs + "\"\n";
		dap += "    with derivedsystemRequirementAlias \"" + derivedSysReqs + "\"\n";
		dap += "    with interfaceAlias \"" + interfaceField + "\"\n";
		dap += "    with interfaceInputAlias \"" + interfaceInput + "\"\n";
		dap += "    with interfaceOutputAlias \"" + interfaceOutput + "\"\n";
		dap += "    with itemAlias \"" + item + "\"\n";
		dap += "    with itemRequirementAlias \"" + itemReqs + "\"\n";
		dap += "    with systemRequirementAlias \"" + sysReqs + "\"\n";
		dap += "    with systemAlias \"" + system + "\"\n";
		dap += "    with systemDesignDescriptionAlias \"" + sysDesignDesc + "\"\n";
		dap += "    with requirementCompleteCorrectReviewAlias \"" + reqCompleteReview + "\"\n";
		dap += "    with requirementTraceableReviewAlias \"" + reqTraceabilityReview + "\".\n";

		return dap;
	}

	private String writeDAP(String fn) {
		String dap = 
			"uri \"http://sadl.org/" + fn + "\" alias " + system.toLowerCase() + "dap.\n\n";
		
		dap += comments();
		dap += "import \"http://sadl.org/PLAN-CORE-Process1.sadl\".\n";
		dap += "import \"http://sadl.org/PLAN-CORE-Process2.sadl\".\n";
		dap += "import \"http://sadl.org/PLAN-CORE-Process3.sadl\".\n";
		dap += "import \"http://sadl.org/PLAN-CORE-Process4.sadl\".\n";
		dap += "import \"http://sadl.org/PLAN-CORE-Process5.sadl\".\n";
		dap += "import \"http://sadl.org/PLAN-CORE-Process6.sadl\".\n";
		dap += "import \"http://sadl.org/PLAN-CORE-Process7.sadl\".\n";
		dap += "import \"http://sadl.org/PLAN-CORE-Process8.sadl\".\n\n";
		
		dap += id + " is a SYSTEM\n";
		dap += "    with identifier \"" + id + "\"\n";
		dap += "    with description \"" + id + " Use Case\"\n";
		dap += "    with developmentAssuranceLevel " + level.replaceAll("\s", "") + ".\n\n";
		
		dap += "Adept-DAP is DevelopmentAssurancePlan\n";
		dap += "    with identifier \"OEM-DAP\"\n";
		dap += "    with description \"" + desc + "\"\n";
		dap += "    with system " + id + "\n";
		dap += "    with process Process-1\n";
		dap += "    with process Process-2\n";
		dap += "    with process Process-3\n";
		dap += "    with process Process-4\n";
		dap += "    with process Process-5\n";
		dap += "    with process Process-6\n";
		dap += "    with process Process-7\n";
		dap += "    with process Process-8.\n\n";
		
		for (String objective : objectives) {
			String obj = getObjective(objective);
			if (obj != null) {
				dap += obj + " has query \"" + objective +"\".\n";
			}
		}

		return dap;
	}
	
	public boolean write() {
		try {
			if (!path.exists()) {
				if (!path.mkdir()) {
					err = "Unable to create directory " + path;
					return true;
				}
			}

			String full = new String(path.getAbsolutePath());
			if (!full.endsWith(File.separator)) {
				full += File.separator;
			}
			
			String planfn = system.toLowerCase() + "_arp4754_dap.sadl";
			String configfn = system.toLowerCase() + "_arp4754_config.sadl";

			File plan = new File(full + planfn);
			File config = new File(full + configfn);
			
			if (plan.exists() || config.exists()) {
	            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	            alert.setTitle("Overwrite ");
	            alert.setHeaderText("The DAP SADL files already exist. Overwrite?");
	            Optional<ButtonType> result = alert.showAndWait();
	            if (!result.isPresent() || !result.get().equals(ButtonType.OK)) {
	            	return false;
	            }
			}
			
			FileWriter fw = new FileWriter(plan);
			fw.write(writeDAP(planfn));
			fw.flush();
			fw.close();
			
			fw = new FileWriter(config);
			fw.write(writeConfig(configfn));
			fw.flush();
			fw.close();
			
		} catch (Exception ex) {
			err = "Error creating SADL files";
			return true;
		}
		
		return false;
	}

}
