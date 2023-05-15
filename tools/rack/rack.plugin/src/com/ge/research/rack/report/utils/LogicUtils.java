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
package com.ge.research.rack.report.utils;

import com.ge.research.rack.report.structures.PsacNode;
import com.ge.research.rack.report.structures.Requirement;
import com.ge.research.rack.report.structures.ReviewLog;
import com.ge.research.rack.report.structures.Test;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 */
public class LogicUtils {

    /**
     * Checks if an anlysis ID is related to an activity
     *
     * @param analysisId
     * @param activityId
     * @return
     */
    public static Boolean analysisRelatesToActivity(String analysisId, String activityId) {
        return (analysisId.contains(activityId));
    }

    /**
     * Given a key and a list of requirement objects, returns the requirement object whose id = key,
     * if any, otherwise return null
     *
     * @param reqList
     * @param key
     * @return
     */
    public static Requirement findReqObjById(List<Requirement> reqList, String key) {

        for (Requirement req : reqList) {
            if (req.getId().equals(key)) {
                return req;
            }
        }

        return null;
    }

    /**
     * Given a key and a list of test objects, returns the list of test objects whose verifies
     * contains key,
     *
     * @param testList
     * @param key
     * @return
     */
    public static List<Test> findTestObjByVerifiesId(List<Test> testList, String key) {

        List<Test> relevant = new ArrayList<Test>();

        //        for (Test tst : testList) {
        ////           	System.out.println("Test: "+ tst.getId() + ", verifies: " +
        // tst.getVerifies() + ", key " + key);
        //            if (tst.getVerifies().equals(key)) {
        ////            	System.out.println(tst.getId() + " verifies " + key);
        //                relevant.add(tst);
        //            }
        //        }

        for (Test tst : testList) {
            //       	System.out.println("Test: "+ tst.getId() + ", verifies: " + tst.getVerifies()
            // + ", key " + key);
            if (tst.getVerifies().contains(key)) {
                //        	System.out.println(tst.getId() + " verifies " + key);
                relevant.add(tst);
            }
        }

        return relevant;
    }

    /**
     * Given a key and a list of rewviewlog objects, returns the list of reviewlog objects whose
     * reviews = key,
     *
     * @param revLogList
     * @param key
     * @return
     */
    public static List<ReviewLog> findRevLogObjById(List<ReviewLog> revLogList, String key) {

        List<ReviewLog> relevant = new ArrayList<ReviewLog>();

        for (ReviewLog log : revLogList) {
            //           	System.out.println("Test: "+ tst.getId() + ", verifies: " +
            // tst.getVerifies() + ", key " + key);
            if (log.getReviews().equals(key)) {
                //            	System.out.println(tst.getId() + " verifies " + key);
                relevant.add(log);
            }
        }

        return relevant;
    }

    /**
     * Given a list of requirements, returns the number of requirements with trace
     *
     * @param reqList
     * @return
     */
    public static int getNumReqsWithTrace(List<Requirement> reqList) {
        int num = 0;
        for (Requirement req : reqList) {
            if (req.getSatisfies().size() > 0) {
                num = num + 1;
            }
        }
        return num;
    }

    /**
     * Given a list of requirements, returns the number of requirements with logs
     *
     * @param reqList
     * @return
     */
    public static int getNumReqsWithLogs(List<Requirement> reqList) {
        int num = 0;
        for (Requirement req : reqList) {
            if (req.getLogs().size() > 0) {
                num = num + 1;
            }
        }
        return num;
    }

    /**
     * Given a list of tests, returns the number of tets with logs
     *
     * @param tstList
     * @return
     */
    public static int getNumTestsWithLogs(List<Test> tstList) {
        int num = 0;
        for (Test tst : tstList) {
            if (tst.getLogs().size() > 0) {
                num = num + 1;
            }
        }
        return num;
    }

    /**
     * Takes a list of requirements and checks the number that have failed, passed or have no tests
     *
     * @param reqList
     * @return
     */
    public static List<Integer> getRequirementTestStatus(List<Requirement> reqList) {
        Integer numFailed = 0;
        Integer numPassed = 0;
        Integer numNoTest = 0;

        for (Requirement req : reqList) {
            if (req.getTests().size() < 1) {
                numNoTest = numNoTest + 1;
            } else {
                boolean failFlag = false;
                for (Test tst : req.getTests()) {
                    if (!tst.getResult().equalsIgnoreCase("Passed")) {
                        failFlag = true;
                        break;
                    }
                }
                if (failFlag) {
                    numFailed = numFailed + 1;
                } else {
                    numPassed = numPassed + 1;
                }
            }
        }

        List<Integer> packet = new ArrayList<Integer>();
        packet.add(numPassed);
        packet.add(numFailed);
        packet.add(numNoTest);
        return packet;
    }

    /**
     * Takes a list of requirements and checks the number that have failed, passed or have no logss
     *
     * @param reqList
     * @return
     */
    public static List<Integer> getRequirementLogStatus(List<Requirement> reqList) {
        Integer numFailed = 0;
        Integer numPassed = 0;
        Integer numNoTest = 0;

        for (Requirement req : reqList) {
            if (req.getLogs().size() < 1) {
                numNoTest = numNoTest + 1;
            } else {
                boolean failFlag = false;
                for (ReviewLog log : req.getLogs()) {
                    if (!log.getResult().equalsIgnoreCase("Passed")) {
                        failFlag = true;
                        break;
                    }
                }
                if (failFlag) {
                    numFailed = numFailed + 1;
                } else {
                    numPassed = numPassed + 1;
                }
            }
        }

        List<Integer> packet = new ArrayList<Integer>();
        packet.add(numPassed);
        packet.add(numFailed);
        packet.add(numNoTest);
        return packet;
    }

    /**
     * Takes a list of tests and checks the number that have failed, passed or have no logss
     *
     * @param tstList
     * @return
     */
    public static List<Integer> getTestLogStatus(List<Test> tstList) {
        Integer numFailed = 0;
        Integer numPassed = 0;
        Integer numNoTest = 0;

        for (Test tst : tstList) {
            if (tst.getLogs().size() < 1) {
                numNoTest = numNoTest + 1;
            } else {
                boolean failFlag = false;
                for (ReviewLog log : tst.getLogs()) {
                    if (!log.getResult().equalsIgnoreCase("Passed")) {
                        failFlag = true;
                        break;
                    }
                }
                if (failFlag) {
                    numFailed = numFailed + 1;
                } else {
                    numPassed = numPassed + 1;
                }
            }
        }

        List<Integer> packet = new ArrayList<Integer>();
        packet.add(numPassed);
        packet.add(numFailed);
        packet.add(numNoTest);
        return packet;
    }

    /**
     * Gien a requirement list and a requirement, returns the index of all occurrences and null if
     * absent
     *
     * @param reqList
     * @param keyReq
     * @return
     */
    public static List<Integer> findIndexesInReqList(
            List<Requirement> reqList, Requirement keyReq) {
        List<Integer> indexes = new ArrayList<Integer>();

        for (int i = 0; i < reqList.size(); i++) {
            Requirement req = reqList.get(i);
            if (req.getId().equals(keyReq.getId())) {
                indexes.add(i);
            }
        }
        if (indexes.size() > 0) {
            return indexes;
        }

        return null;
    }

    /**
     * Checks if a list of requirements has duplicates
     *
     * @param reqList
     * @return
     */
    public static void checkDuplicates(List<Requirement> reqList) {
        for (Requirement req : reqList) {
            List<Integer> indexes = findIndexesInReqList(reqList, req);
            if (indexes != null) {
                if (indexes.size() > 1) {
                    System.out.println(req.getId() + " has duplicates");
                } else {
                    System.out.println(req.getId() + " has NO duplicates");
                }
            }
        }
    }

    /**
     * Checks if a given key test already exist in a list and returns the index, otherwise retruns
     * null
     *
     * @param testList
     * @param key
     * @return
     */
    public static Integer alreadyCreatedTest(List<Test> testList, String key) {
        for (int i = 0; i < testList.size(); i++) {
            if (testList.get(i).getId().equals(key)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Takes a psacNode and filters out all objectives that are not relevant to the software level
     * by assigning nodata = true and metric = "N/A for Level X" irrespective of what the psac has
     * or the output contents of that objective
     *
     * <p>NOTE: This is a temporary ad-hoc approach for filtering non-relevant objectives TODO:
     * Proper way would be to do it by checking the applicability level in the SADL template
     *
     * @param psacNode
     * @return
     */
    public static PsacNode filterNonRelevantObjectives(PsacNode psacNode) {

        for (PsacNode.Table tab : psacNode.getReportTables()) {
            for (PsacNode.Objective obj : tab.getTabObjectives()) {
                // level C filters
                if (StringUtils.substringBetween(psacNode.getMainOFP(), "(", ")")
                        .equals("Level C")) {
                    if (obj.getId().equals("A3-3")
                            || obj.getId().equals("A4-3")
                            || obj.getId().equals("A4-4")
                            || obj.getId().equals("A4-10")
                            || obj.getId().equals("A4-11")
                            || obj.getId().equals("A7-5")
                            || obj.getId().equals("A7-6")
                            || obj.getId().equals("A7-9")) {

                        obj.setNoData(true);
                        obj.setPassed(false);
                        obj.setPartialData(false);

                        obj.setMetrics("N/A for Level C");
                    }
                }
            }
        }

        return psacNode;
    }
}
