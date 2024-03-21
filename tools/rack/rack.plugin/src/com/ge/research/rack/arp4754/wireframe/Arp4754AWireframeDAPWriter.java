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
package com.ge.research.rack.arp4754.wireframe;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private List<Boolean> processes = new LinkedList<Boolean>();
    ;

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

    public void setProcesses(List<Boolean> bools) {
        processes = bools;
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

        // if (objective.matches("Objective-[1-8]-[1-8]-.*")) {
        //	return objective.substring(0, 13);
        // }
        //
        // return null;
    }

    public String getError() {
        return err;
    }

    private String comments() {
        return "//-- Company: "
                + company
                + "\n//-- Creator: "
                + creator
                + "\n//-- Use Case: "
                + id
                + "\n//-- Description: "
                + desc
                + "\n\n";
    }

    private String writeConfig(String fn) {
        String dap =
                "uri \"http://sadl.org/" + fn + "\" alias " + system.toLowerCase() + "config.\n\n";

        dap += comments();
        dap += "import \"http://sadl.org/PLAN-CORE.sadl\".\n\n";
        dap += configID + " is a CONFIGURATION\n";
        dap += "    with identifier \"" + configID + "\"\n";
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

        int ii = 0;
        while (ii < 8 && ii < processes.size()) {
            if (processes.get(ii)) {
                dap +=
                        "import \"http://sadl.org/PLAN-CORE-Process"
                                + Integer.toString(ii + 1)
                                + ".sadl\".\n";
            }

            ii++;
        }

        dap += "\n" + id + " is a SYSTEM\n";
        dap += "    with identifier \"" + id + "\"\n";
        dap += "    with description \"" + id + " Use Case\"\n";
        dap += "    with developmentAssuranceLevel " + level.replaceAll("\\s", "") + ".\n\n";

        dap += "Adept-DAP is DevelopmentAssurancePlan\n";
        dap += "    with identifier \"OEM-DAP\"\n";
        dap += "    with description \"" + desc + "\"\n";
        dap += "    with system " + id;

        ii = 0;
        while (ii < 8 && ii < processes.size()) {
            if (processes.get(ii)) {
                dap += "\n    with process Process-" + Integer.toString(ii + 1);
            }

            ii++;
        }

        dap += ".\n\n";

        for (String objective : objectives) {
            String obj = getObjective(objective);
            if (obj != null) {
                dap += obj + " has query \"" + objective + "\".\n";
            }
        }

        return dap;
    }

    @SuppressWarnings({"unchecked"})
    private String getJSON() {
        JSONObject jo = new JSONObject();

        Map<String, JSONArray> map = new HashMap<String, JSONArray>();
        for (String objective : objectives) {
            String key = getObjective(objective);
            if (key != null) {
                JSONArray objlist = map.get(key);
                if (objlist == null) {
                    objlist = new JSONArray();
                    map.put(key, objlist);
                }

                objlist.add(objective);
            }
        }

        Iterator<String> iter = map.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            JSONArray jarray = map.get(key);
            jo.put(key, jarray);
        }

        JSONArray bools = new JSONArray();
        for (Boolean process : processes) {
            bools.add(process);
        }

        jo.put("processes", bools);
        jo.put("reqTraceabilityReview", reqTraceabilityReview);
        jo.put("reqCompleteReview", reqCompleteReview);
        jo.put("sysDesignDesc", sysDesignDesc);
        jo.put("sysReqs", sysReqs);
        jo.put("system", system);
        jo.put("itemReqs", itemReqs);
        jo.put("item", item);
        jo.put("interfaceOutput", interfaceOutput);
        jo.put("interfaceInput", interfaceInput);
        jo.put("interface", interfaceField);
        jo.put("derivedSysReqs", derivedSysReqs);
        jo.put("derivedItemReqs", derivedItemReqs);
        jo.put("configID", configID);
        jo.put("level", level);
        jo.put("description", desc);
        jo.put("id", id);
        jo.put("creator", creator);
        jo.put("company", company);

        return jo.toJSONString();
    }

    public boolean writeJSON() {
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

            String fn = system.toLowerCase() + "_arp4754.json";

            File json = new File(full + fn);

            if (json.exists()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Overwrite ");
                alert.setHeaderText("The DAP JSON file already exist. Overwrite?");
                Optional<ButtonType> result = alert.showAndWait();
                if (!result.isPresent() || !result.get().equals(ButtonType.OK)) {
                    return false;
                }
            }

            FileWriter fw = new FileWriter(json);
            fw.write(getJSON());
            fw.flush();
            fw.close();
        } catch (Exception ex) {
            err = "Error creating JSON file";
            return true;
        }

        return false;
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
