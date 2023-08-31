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

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.ge.research.rack.arp4754.constants.ARP4754Queries;
import com.ge.research.rack.arp4754.structures.Configuration;
import com.ge.research.rack.do178c.constants.DO178CQueries;
import com.ge.research.rack.do178c.structures.SparqlConnectionInfo;
import com.ge.research.rack.do178c.utils.RackQueryUtils;

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
            case "allSystemRequirementWithItemRequirement":
                id = config.getSysReq() + "_with_" + config.getItemReq();
                break;
            default:
                id = "";
                break;
        }
        return id;
    }
    
}
