package com.ge.research.rack.arp4754.wireframe;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Arp4754AWireframeDAPWriter {
	
	// Plan SADL parameters
	private String id = "";
	private String desc = "";
	private String level = "";
	private List<String> objectives = new LinkedList<String>();
	
	// Config SADL parameters

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
	
	public void setDescription(String str) {
		desc = str;
	}
	
	public void setLevel(String str) {
		level = str;
	}
	
	public void setObjectives(List<String> strs) {
		objectives = strs;
	}
	
	private String getObjective(String objective) {
		if (objective.matches("Objective-[1-8]-[1-8]-.*")) {
			return objective.substring(0, 13);
		}
		
		return null;
	}
	
	public String getError() {
		return err;
	}
	
	private String writeConfig(String fn) {
		String dap = 
			"uri \"http://sadl.org/" + fn + "\" alias " + system.toLowerCase() + "config.\n\n";

		return dap;
	}

	private String writeDAP(String fn) {
		String dap = 
			"uri \"http://sadl.org/" + fn + "\" alias " + system.toLowerCase() + "dap.\n\n";
		
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
