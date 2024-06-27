/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2023, General Electric Company and Galois, Inc.
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
package com.ge.research.rack.arp4754.utils;

import com.ge.research.rack.arp4754.structures.Configuration;
import com.ge.research.rack.do178c.utils.RackQueryUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 */
public class DataProcessorUtils {

    /**
     * Takes a key that represents a variable name and a config file and returns the CSV/query ID
     * for that variable name
     *
     * @param key
     * @param config
     * @return TODO: COmplete for all possible variables/verbiage in arp4754
     */
    public static String getVarCSVID(String key, Configuration config) {
        String id = "";
        switch (key) {
            case "planData":
                id = "getDAP";
                break;
            case "allDerivedItemRequirement":
                id = config.getDerivedItemReq();
                break;
            case "allDerivedSystemRequirement":
                id = config.getDerivedSysReq();
                break;
            case "allInterface":
                id = config.getIntrface();
                break;
            case "allInterfaceInput":
                id = config.getIntrfaceInput();
                break;
            case "allInterfaceOutput":
                id = config.getIntrfaceOutput();
                break;
            case "allItem":
                id = config.getItem();
                break;
            case "allSystem":
                id = config.getSystem();
                break;
            case "allSystemDesignDescription":
                id = config.getSystemDesignDescription();
                break;
            case "allItemRequirement":
                id = config.getItemReq();
                break;
            case "allSystemRequirement":
                id = config.getSysReq();
                break;
            case "allInterfaceWithInputOutput":
                id = config.getIntrface() + "_with_IO";
                break;
            case "allItemRequirementWIthItem":
                id = config.getItemReq() + "_with_" + config.getItem();
                break;
            case "allSystemRequirementWIthSystem":
                id = config.getSysReq() + "_with_" + config.getSystem();
                break;
            case "allSystemWIthInterface":
                id = config.getSystem() + "_with_" + config.getIntrface();
                break;
            case "allItemRequirementWIthSystemRequirement":
                id = config.getItemReq() + "_with_" + config.getSysReq();
                break;
            case "allRequirementCompleteCorrectReview":
                id = config.getRequirementCompleteCorrectReview();
                break;
            case "allRequirementTraceableReview":
                id = config.getRequirementTraceableReview();
                break;
            case "allRequirementWithCompleteCorrectReview": // TODO: Itemrqe needs to be turned to
                // Generic rquirements
                id = config.getItemReq() + "_with_" + config.getRequirementCompleteCorrectReview();
                break;
            case "allRequirementWithTraceableReview":
                id = config.getItemReq() + "_with_" + config.getRequirementTraceableReview();
                break;

            default:
                id = "";
                break;
        }
        return id;
    }

    /**
     * This function uses the config to create the IDs for all the queries needed to get the data
     * and then executes those queries
     *
     * <p>NOTE: Currently, the queries are manually created and stored on RACK. In future, the
     * queries themselves must also be synthesized
     */
    public static void createAndExecuteDataQueries(Configuration config, String rackDir) {

        // Create all required query IDs
        List<String> allQueryIds = new ArrayList<String>();

        allQueryIds.add(getVarCSVID("planData", config));
        allQueryIds.add(getVarCSVID("allDerivedItemRequirement", config));
        allQueryIds.add(getVarCSVID("allDerivedSystemRequirement", config));
        allQueryIds.add(getVarCSVID("allInterface", config));
        allQueryIds.add(getVarCSVID("allInterfaceInput", config));
        allQueryIds.add(getVarCSVID("allInterfaceOutput", config));
        allQueryIds.add(getVarCSVID("allItem", config));
        allQueryIds.add(getVarCSVID("allItemRequirement", config));
        allQueryIds.add(getVarCSVID("allSystem", config));
        allQueryIds.add(getVarCSVID("allSystemRequirement", config));
        allQueryIds.add(getVarCSVID("allSystemDesignDescription", config));
        allQueryIds.add(getVarCSVID("allInterfaceWithInputOutput", config));
        allQueryIds.add(getVarCSVID("allItemRequirementWIthItem", config));
        allQueryIds.add(getVarCSVID("allSystemRequirementWIthSystem", config));
        allQueryIds.add(getVarCSVID("allSystemWIthInterface", config));
        allQueryIds.add(getVarCSVID("allItemRequirementWIthSystemRequirement", config));
        allQueryIds.add(getVarCSVID("allRequirementCompleteCorrectReview", config));
        allQueryIds.add(getVarCSVID("allRequirementTraceableReview", config));
        allQueryIds.add(getVarCSVID("allRequirementWithCompleteCorrectReview", config));
        allQueryIds.add(getVarCSVID("allRequirementWithTraceableReview", config));
        allQueryIds.add("DOCUMENT");

        // Execute each predefined query
        RackQueryUtils.createConnectionAndExecuteMultiQueriesFromStore(allQueryIds, rackDir);
    }
}
