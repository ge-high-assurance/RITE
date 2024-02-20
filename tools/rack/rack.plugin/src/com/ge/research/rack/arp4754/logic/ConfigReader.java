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

import com.ge.research.rack.analysis.structures.SparqlConnectionInfo;
import com.ge.research.rack.analysis.utils.CustomStringUtils;
import com.ge.research.rack.analysis.utils.RackQueryUtils;
import com.ge.research.rack.arp4754.constants.ARP4754Queries;
import com.ge.research.rack.arp4754.structures.Configuration;
import com.ge.research.rack.utils.CSVUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * @author Saswata Paul
 *     <p>This class has structures and functions needed to read a configuration file and store the
 *     data
 */
public class ConfigReader {

    /**
     * Gets the config from a .config file
     *
     * @return
     */
    public static Configuration getConfigFromFile(String configFilePath) {

        String queries[] = {
            "derivedItemRequirement",
            "derivedSystemRequirement",
            "interface",
            "interfaceInput",
            "interfaceOutput",
            "item",
            "itemRequirement",
            "systemRequirement",
            "system",
            "systemDesignDescription"
        };

        Configuration projectConfig = new Configuration();
        System.out.println(configFilePath);

        try {
            File file = new File(configFilePath); // creates a new file instance
            FileReader fr = new FileReader(file); // reads the file
            try (BufferedReader br = new BufferedReader(fr)) {
                // Read a file line
                String line;
                while ((line = br.readLine()) != null) {
                    // System.out.println(line);
                    String content = line.trim();
                    // Extract and store info in appropriate field
                    String[] config = content.split("\\::");

                    if (config.length != 2) { // ill formed
                        System.out.println("ERROR: Ill-formed .config file at " + configFilePath);
                        return projectConfig;
                    }
                    // System.out.println(config[0] + "--" +config[1]);

                    boolean notfound = true;
                    int index = 0;
                    while (notfound && index < queries.length) {
                        if (config[0].equalsIgnoreCase(queries[index])) {
                            notfound = false;
                            projectConfig.put(queries[index], config[1]);
                        }

                        index++;
                    }
                }

                br.close();
            }

            fr.close(); // closes the stream and release the resources
        } catch (Exception e) {
            e.printStackTrace();
        }

        return projectConfig;
    }

    /**
     * TODO Gets the config stored in RACK
     *
     * @return
     */
    public static Configuration getConfigFromRACK(String rackDir) {

        String queries[] = {
            "derivedItemRequirement",
            "derivedSystemRequirement",
            "interface",
            "interfaceInput",
            "interfaceOutput",
            "item",
            "itemRequirement",
            "systemRequirement",
            "system",
            "systemDesignDescription",
            "requirementCompleteCorrectReview",
            "requirementTraceableReview"
        };

        Configuration projectConfig = new Configuration();

        // Query RACK
        queryRackForARP4754Config(rackDir);

        // Read the filedata
        List<String[]> configData =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                ARP4754Queries.All.GET_CONFIG.getQId(), rackDir));

        // read file column
        String[] configCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                ARP4754Queries.All.GET_CONFIG.getQId(), rackDir));

        for (String str : queries) {
            int idCol = CustomStringUtils.getCSVColumnIndex(configCols, str + "Alias");
            projectConfig.put(str, configData.get(0)[idCol]);
        }

        return projectConfig;
    }

    /** Queries RACK for the Configuration data */
    private static void queryRackForARP4754Config(String rackDir) {

        try {
            //            // ***************** DO NOT DELETE ***************** TURNED OFF FOR
            // TESTING
            //            // Connect to RACK using RACK preferences
            //            SparqlConnectionInfo newConnPars =
            // RackQueryUtils.initiateQueryConnection();

            // FOR TESTING ONLY : Connect to RACK using hardcoded preferences
            SparqlConnectionInfo newConnPars = RackQueryUtils.hardcodedQueryConnectionForTesting();

            // Execute the query to get config data
            RackQueryUtils.executeSingleQueryFromStore(
                    ARP4754Queries.All.GET_CONFIG.getQId(), rackDir, newConnPars);
        } catch (Exception e) {
            System.out.println("ERROR: Was unable to successfuly query RACK!!\n");
            e.printStackTrace();
        }
    }
}
