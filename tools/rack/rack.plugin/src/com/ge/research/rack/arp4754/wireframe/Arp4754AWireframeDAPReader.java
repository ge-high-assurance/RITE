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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Arp4754AWireframeDAPReader {

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
    private String configID = "";
    private String derivedItemReqs = "";
    private String derivedSysReqs = "";
    private String interfaceField = "";
    private String interfaceInput = "";
    private String interfaceOutput = "";
    private String item = "";
    private String itemReqs = "";
    private String sysReqs = "";
    private String sysDesignDesc = "";
    private String reqCompleteReview = "";
    private String reqTraceabilityReview = "";

    // General parameters
    private String system = "";
    private File json = null;
    private String err = null;

    public Arp4754AWireframeDAPReader(File ff) {
        json = ff;
    }

    public String getID() {
        return id;
    }

    public String getCompany() {
        return company;
    }

    public String getCreator() {
        return creator;
    }

    public String getDescription() {
        return desc;
    }

    public String getLevel() {
        return level;
    }

    public List<Boolean> getProcesses() {
        return processes;
    }

    public void setObjectives(List<String> strs, HashMap<String, String> map) {
        objectives = strs;
        objectiveMap = map;
    }

    public String getConfigID() {
        return configID;
    }

    public String getDerivedItemReqs() {
        return derivedItemReqs;
    }

    public String getDerivedSysReqs() {
        return derivedSysReqs;
    }

    public String getInterface() {
        return interfaceField;
    }

    public String getInterfaceInput() {
        return interfaceInput;
    }

    public String getInterfaceOutput() {
        return interfaceOutput;
    }

    public String getItem() {
        return item;
    }

    public String getItemReqs() {
        return itemReqs;
    }

    public String getSystem() {
        return system;
    }

    public String getSysReqs() {
        return sysReqs;
    }

    public String getSystemDesignDescription() {
        return sysDesignDesc;
    }

    public String getReqCompleteReview() {
        return reqCompleteReview;
    }

    public String getReqTraceabilityReview() {
        return reqTraceabilityReview;
    }

    public HashMap<String, String> getObjectiveMap() {
        return objectiveMap;
    }

    public List<String> getObjectives() {
        return objectives;
    }

    public String getError() {
        return err;
    }

    private String getField(JSONObject obj, String key) {
        Object value = obj.get(key);
        if (value == null || !(value instanceof String)) {
            return null;
        }

        return (String) value;
    }

    private void readObjective(JSONObject obj, String key) {
        Object value = obj.get(key);
        if (value != null && value instanceof JSONArray) {
            JSONArray objs = (JSONArray) value;

            @SuppressWarnings("unchecked")
            Iterator<Object> iter = objs.iterator();
            while (iter.hasNext()) {
                Object item = iter.next();
                if (item instanceof String) {
                    objectives.add((String) item);
                    objectiveMap.put((String) item, key);
                }
            }
        }
    }

    public boolean hasObjectives() {
        return !processes.isEmpty();
    }

    public boolean readJSON() {
        if (!json.exists()) {
            err = "Cannot find JSON file " + json.getName();
            return true;
        }

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(json));
            JSONObject jsonObj = (JSONObject) obj;

            String str = getField(jsonObj, "company");
            if (str != null) {
                company = str;
            }

            str = getField(jsonObj, "creator");
            if (str != null) {
                creator = str;
            }

            str = getField(jsonObj, "id");
            if (str != null) {
                id = str;
            }

            str = getField(jsonObj, "description");
            if (str != null) {
                desc = str;
            }

            str = getField(jsonObj, "level");
            if (str != null) {
                level = str;
            }

            str = getField(jsonObj, "configID");
            if (str != null) {
                configID = str;
            }

            str = getField(jsonObj, "derivedItemReqs");
            if (str != null) {
                derivedItemReqs = str;
            }

            str = getField(jsonObj, "derivedSysReqs");
            if (str != null) {
                derivedSysReqs = str;
            }

            str = getField(jsonObj, "interface");
            if (str != null) {
                interfaceField = str;
            }

            str = getField(jsonObj, "interfaceInput");
            if (str != null) {
                interfaceInput = str;
            }

            str = getField(jsonObj, "interfaceOutput");
            if (str != null) {
                interfaceOutput = str;
            }

            str = getField(jsonObj, "item");
            if (str != null) {
                item = str;
            }

            str = getField(jsonObj, "itemReqs");
            if (str != null) {
                itemReqs = str;
            }

            str = getField(jsonObj, "system");
            if (str != null) {
                system = str;
            }

            str = getField(jsonObj, "sysReqs");
            if (str != null) {
                sysReqs = str;
            }

            str = getField(jsonObj, "sysDesignDesc");
            if (str != null) {
                sysDesignDesc = str;
            }

            str = getField(jsonObj, "reqCompleteReview");
            if (str != null) {
                reqCompleteReview = str;
            }

            str = getField(jsonObj, "reqTraceabilityReview");
            if (str != null) {
                reqTraceabilityReview = str;
            }

            JSONArray values = (JSONArray) jsonObj.get("processes");
            @SuppressWarnings("unchecked")
            Iterator<Object> iter = values.iterator();
            while (iter.hasNext()) {
                Object item = iter.next();
                if (item instanceof Boolean) {
                    processes.add(((Boolean) item).booleanValue());
                } else {
                    processes.add(false);
                }
            }

            int ii = processes.size();
            while (ii < 8) {
                processes.add(false);
                ii++;
            }

            readObjective(jsonObj, "Objective-1-1");
            readObjective(jsonObj, "Objective-1-2");
            readObjective(jsonObj, "Objective-2-1");
            readObjective(jsonObj, "Objective-2-2");
            readObjective(jsonObj, "Objective-2-3");
            readObjective(jsonObj, "Objective-2-4");
            readObjective(jsonObj, "Objective-2-5");
            readObjective(jsonObj, "Objective-2-6");
            readObjective(jsonObj, "Objective-2-7");
            readObjective(jsonObj, "Objective-3-1");
            readObjective(jsonObj, "Objective-3-2");
            readObjective(jsonObj, "Objective-3-3");
            readObjective(jsonObj, "Objective-3-4");
            readObjective(jsonObj, "Objective-3-5");
            readObjective(jsonObj, "Objective-3-6");
            readObjective(jsonObj, "Objective-3-7");
            readObjective(jsonObj, "Objective-4-1");
            readObjective(jsonObj, "Objective-4-2");
            readObjective(jsonObj, "Objective-4-3");
            readObjective(jsonObj, "Objective-4-4");
            readObjective(jsonObj, "Objective-4-6");
            readObjective(jsonObj, "Objective-5-1");
            readObjective(jsonObj, "Objective-5-2");
            readObjective(jsonObj, "Objective-5-3");
            readObjective(jsonObj, "Objective-5-4");
            readObjective(jsonObj, "Objective-5-5");
            readObjective(jsonObj, "Objective-5-6");
            readObjective(jsonObj, "Objective-6-1");
            readObjective(jsonObj, "Objective-6-2");
            readObjective(jsonObj, "Objective-6-3");
            readObjective(jsonObj, "Objective-6-4");
            readObjective(jsonObj, "Objective-7-1");
            readObjective(jsonObj, "Objective-7-2");
            readObjective(jsonObj, "Objective-8-1");

        } catch (Exception ex) {
            err = "Error reading JSON file " + json.getName();
            return true;
        }

        return false;
    }
}
