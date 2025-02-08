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
package com.ge.research.rite.autoGsn.utils;

import com.ge.research.rite.autoGsn.structures.MultiClassPackets;
import com.ge.research.rite.autoGsn.structures.MultiClassPackets.GoalIdAndClass;
import com.ge.research.rite.autoGsn.structures.PatternInfo;
import com.ge.research.rite.do178c.utils.RackQueryUtils;
import com.ge.research.rite.utils.CSVUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 *     <p>Contains functions to help fetch data from the csv files created by queries
 */
public class QueryResultUtils {

    /**
     * Creates a list of possible goal ids and their classes from the data fetched from RACK
     *
     * @param patInfo
     * @param outDir
     * @return
     */
    public static List<GoalIdAndClass> getGoalIdsAndClasses(PatternInfo patInfo, String outDir) {

        List<GoalIdAndClass> allGoals = new ArrayList<GoalIdAndClass>();

        // for each stratPat, find each instance of the goalclass
        for (PatternInfo.StratPat stratPat : patInfo.getStratPats()) {
            // find all instances
            List<String> classInstances = getAllInstancesOfGoalClass(stratPat, outDir);
            // add to the list
            for (String instanceId : classInstances) {
                allGoals.add(
                        new MultiClassPackets()
                        .new GoalIdAndClass(("G-" + instanceId), stratPat.getGoalClass()));
            }
        }
        return allGoals;
    }

    /**
     * Given a sratpat object, returns the filename for the CSV file that the qury results for this
     * binary connection would have been stored as
     *
     * @param stratPat
     * @return
     */
    public static String getCSVFileNameForConnection(PatternInfo.StratPat stratPat) {
        // create the csv file name for the stratPat
        if (stratPat.getPropIsForward()) {
            return (stratPat.getGoalClass()
                    + "_"
                    + stratPat.getProperty()
                    + "_"
                    + stratPat.getSubGoalClass()
                    + ".csv");
        } else {
            return (stratPat.getSubGoalClass()
                    + "_"
                    + stratPat.getProperty()
                    + "_"
                    + stratPat.getGoalClass()
                    + ".csv");
        }
    }

    public static List<String> getAllInstancesOfClassFromFile(String className, String filePath) {
        // new list to store instance ids
        List<String> instanceIds = new ArrayList<String>();

        // get headers info
        String[] fileHeaders = RackQueryUtils.readCSVHeader(filePath);

        // get class identifier column
        int classCol = CustomStringUtils.getCSVColumnIndex(fileHeaders, "identifier_" + className);

        // read the file without headers
        List<String[]> fileData = CSVUtil.getRows(filePath);

        //    	System.out.println(filePath);

        // read the rows one by one and store instances
        for (String[] row : fileData) {
            //        	System.out.println(Arrays.toString(row));
            if (row.length > classCol) {
                instanceIds.add(row[classCol]);
            }
        }

        // remove duplicates and return
        return CustomStringUtils.removeDuplicates(instanceIds);
    }

    /**
     * Given a sratpat object, returns the instances of the goalClass returned by the queries
     *
     * @param stratPat
     * @return
     */
    public static List<String> getAllInstancesOfGoalClass(
            PatternInfo.StratPat stratPat, String outDir) {

        /**
         * Find the file containing the class instances that are relevant to this StratPat
         *
         * <p>Note: If there is some goalclass instance for this strategy that is not connected to a
         * subGoalclass instance inside RACK, then that goalclass instance will not show up in the
         * csv files and no GSN will be created for it This is correct since we only extract
         * POSSIBLE GSNs from RACK
         */
        String fileName = QueryResultUtils.getCSVFileNameForConnection(stratPat);

        // create filepath
        String filePath = outDir + "/" + fileName;

        // get instances of the goal class and return

        return getAllInstancesOfClassFromFile(stratPat.getGoalClass(), filePath);
    }

    /**
     * Given a sratpat object, returns the instances of the subgoalClass returned by the queries
     *
     * @param stratPat
     * @return
     */
    public static List<String> getAllInstancesOfSubGoalClass(
            PatternInfo.StratPat stratPat, String outDir) {

        /**
         * Find the file containing the class instances that are relevant to this StratPat
         *
         * <p>Note: If there is some goalclass instance for this strategy that is not connected to a
         * subGoalclass instance inside RACK, then that goalclass instance will not show up in the
         * csv files and no GSN will be created for it This is correct since we only extract
         * POSSIBLE GSNs from RACK
         */
        String fileName = QueryResultUtils.getCSVFileNameForConnection(stratPat);

        // create filepath
        String filePath = outDir + "/" + fileName;

        // get instances of the goal class and return

        //    	System.out.println("Finding instances of subgoalclass :" +
        // stratPat.getSubGoalClass());

        return getAllInstancesOfClassFromFile(stratPat.getSubGoalClass(), filePath);
    }

    /**
     * Given a sratpat object, and an instance id for the goal class, returns the relevant instances
     * of the subgoalClass returned by the queries
     *
     * @param stratPat
     * @return
     */
    public static List<String> getRelevantInstancesOfSubGoalClass(
            PatternInfo.StratPat stratPat, String goalInstanceId, String outDir) {

        /**
         * Find the file containing the class instances that are relevant to this StratPat
         *
         * <p>Note: If there is some goalclass instance for this strategy that is not connected to a
         * subGoalclass instance inside RACK, then that goalclass instance will not show up in the
         * csv files and no GSN will be created for it This is correct since we only extract
         * POSSIBLE GSNs from RACK
         */
        String fileName = QueryResultUtils.getCSVFileNameForConnection(stratPat);

        // create filepath
        String filePath = outDir + "/" + fileName;

        // get instances of the goal class and return

        //    	System.out.println("Finding relevant instances of subgoalclass :" +
        // stratPat.getSubGoalClass());

        // new list to store instance ids
        List<String> relevantSubGoalInstanceIds = new ArrayList<String>();

        // get headers info
        String[] fileHeaders = RackQueryUtils.readCSVHeader(filePath);

        // get goal class identifier column
        int goalClassCol =
                CustomStringUtils.getCSVColumnIndex(
                        fileHeaders, "identifier_" + stratPat.getGoalClass());

        // get subGoal class identifier column
        int subGoalClassCol =
                CustomStringUtils.getCSVColumnIndex(
                        fileHeaders, "identifier_" + stratPat.getSubGoalClass());

        // read the file without headers
        List<String[]> fileData = CSVUtil.getRows(filePath);

        //    	System.out.println(filePath);

        // read the rows one by one and store instances
        for (String[] row : fileData) {
            //        	System.out.println(Arrays.toString(row));
            if ((row.length > goalClassCol) && (row.length > subGoalClassCol)) {
                if (row[goalClassCol].equals(goalInstanceId)) {
                    relevantSubGoalInstanceIds.add(row[subGoalClassCol]);
                }
            }
        }

        // remove duplicates and return
        relevantSubGoalInstanceIds = CustomStringUtils.removeDuplicates(relevantSubGoalInstanceIds);

        //        // test print
        //        for(String id: relevantSubGoalInstanceIds) {
        //        	System.out.println(" ------- "+ id);
        //        }

        return relevantSubGoalInstanceIds;
    }

    /**
     * To find description of a given goal instance
     *
     * @param instanceId
     * @param stratPat
     * @param outDir
     * @return
     */
    public static String getGoalInstanceDesc(
            String instanceId, PatternInfo.StratPat stratPat, String outDir) {

        /**
         * Find the file containing the class instances that are relevant to this StratPat
         *
         * <p>Note: If there is some goalclass instance for this strategy that is not connected to a
         * subGoalclass instance inside RACK, then that goalclass instance will not show up in the
         * csv files and no GSN will be created for it This is correct since we only extract
         * POSSIBLE GSNs from RACK
         */
        String fileName = QueryResultUtils.getCSVFileNameForConnection(stratPat);

        // create filepath
        String filePath = outDir + "/" + fileName;

        // get headers info
        String[] fileHeaders = RackQueryUtils.readCSVHeader(filePath);

        // get class identifier column
        int classCol =
                CustomStringUtils.getCSVColumnIndex(
                        fileHeaders, "identifier_" + stratPat.getGoalClass());

        // get class description column
        int classDescCol =
                CustomStringUtils.getCSVColumnIndex(
                        fileHeaders, "description_" + stratPat.getGoalClass());

        // read the file without headers
        List<String[]> fileData = CSVUtil.getRows(filePath);

        // read the rows one by one and find description
        for (String[] row : fileData) {
            if (row[classCol].equals(instanceId)) {
                if ((row[classDescCol] != null) && (row[classDescCol] != "")) {
                    return row[classDescCol];
                }
            }
        }

        return "No description was returned for object from RACK."; // if nothing was found
    }
}
