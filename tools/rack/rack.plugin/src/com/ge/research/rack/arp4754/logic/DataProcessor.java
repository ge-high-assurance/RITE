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
package com.ge.research.rack.arp4754.logic;

import java.util.List;

import com.ge.research.rack.arp4754.structures.Configuration;
import com.ge.research.rack.arp4754.utils.DataProcessorUtils;
import com.ge.research.rack.do178c.constants.ReportQueries;
import com.ge.research.rack.do178c.utils.RackQueryUtils;
import com.ge.research.rack.utils.CSVUtil;

/**
 * @author 212807042
 *
 */
public class DataProcessor {
	
	/**
	 *  Class variables to store the raw data fetched from CSV files
	 *  
	 *  NOTE: There should be an associated query for each
	 */
	
	// The configuration
	private Configuration config = new Configuration ();
	
	// All the independent element id data (TODO subset, add for more supporting objectives)
    private List<String[]> allDerivedItemRequirement;   

    private List<String[]> allDerivedSystemRequirement;   

    private List<String[]> allInterface;   

    private List<String[]> allInterfaceInput;   

    private List<String[]> allInterfaceOutput;   

    private List<String[]> allItem;   

    private List<String[]> allItemRequirement;   

    private List<String[]> allSystemRequirement;

    
    // The connections between the elements (TODO subset, add for more supporting objectives)
    // NOTE: The connections should be just one-level to enable automatic query generation in future
    private List<String[]> allInterfaceWithInputOutput;

    private List<String[]> allItemRequirementWIthItem;
    
    private List<String[]> allSystemRequirementWIthSystem;

    private List<String[]> allSystemWIthInterface;
    
    // The Development Assurance Plan data
    private List<String[]> planData;

    
	// function to use the configReader to read the config and the read the data from the .csv files
    private void readCSVs( String rackDir, String configPath) {
    	// Get configuration
    	config = ConfigReader.getConfigFromFile(configPath);
    	
    	// TODO: Future: Generate queries and query ids dynamically 
    	
    	// Use the string from configurations to create the Query/CSV IDS and use that to read the files
    	allDerivedItemRequirement = 
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID("allDerivedItemRequirement", config) , rackDir));

    	
    	
    	
    	System.out.println(allDerivedItemRequirement.get(0)[0]);
    	
    	
    }
    
    
    
	// Entry point that does sequence of operations    
    public void getPlanData(String rackDir, String configFileName) {
    	String configPath = rackDir + "/" + configFileName;
    	
    	readCSVs(rackDir, configPath);
    	
    }
}
