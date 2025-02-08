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
package com.ge.research.rite.do178c.oem;

import com.ge.research.rite.do178c.structures.PsacNode;
import com.ge.research.rite.do178c.structures.Requirement;
import com.ge.research.rite.do178c.structures.Test;
import com.ge.research.rite.do178c.utils.LogicUtils;

/**
 * @author Saswata Paul
 */
public class ComplianceTable7 {

    /**
     * A rudimentary logic
     *
     * <p>TODO: replace with specific and detailed logic
     *
     * @param objective
     * @return
     */
    public static PsacNode.Objective processObjectiveA7_1(PsacNode.Objective objective) {
        /**
         * This objective will pass if 1. There are some associated SBVT tests 2. all the associated
         * SBVT tests in the output have review log
         */
        //        Boolean tstPresenceFlag = false;
        //        Boolean tstNoLogFlag = false;
        //
        //        for (Test tst : objective.getObjOutputs().getTests()) {
        //            if (tst.getType().equals("SBVT_Test")) {
        //                tstPresenceFlag = true;
        //                if (tst.getLogs().size() < 1) { // no log info
        //                    tstNoLogFlag = true;
        //                    break;
        //                }
        //            }
        //        }
        //
        //        if (tstPresenceFlag && !tstNoLogFlag) {
        //            objective.setPassed(true);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        } else if (tstPresenceFlag && tstNoLogFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        } else if (!tstPresenceFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(false);
        //            objective.setNoData(true);
        //        }

        int numSBVTTests = objective.getObjOutputs().getTests().size();
        int numSBVTTestsWithLogs =
                LogicUtils.getNumTestsWithLogs(objective.getObjOutputs().getTests());
        ;

        // TODO: decide no data case
        if (numSBVTTests < 1) { // no SubDD
            objective.setPassed(false);
            objective.setPartialData(false);
            objective.setNoData(false);
            objective.setMetrics("0.0% tests have review logs");
        } else { // Some SubDD
            if (numSBVTTestsWithLogs == numSBVTTests) { // all have tests
                objective.setPassed(true);
                objective.setPartialData(false);
                objective.setNoData(false);
                objective.setMetrics("100.0% tests have review logs");
            } else if ((0 < numSBVTTestsWithLogs)
                    && (numSBVTTestsWithLogs < numSBVTTests)) { // some have no tests
                objective.setPassed(false);
                objective.setPartialData(true);
                objective.setNoData(false);
                double percent = ((double) (numSBVTTestsWithLogs) / numSBVTTests) * 100.00;
                objective.setMetrics(String.format("%.2f", percent) + "% tests have review logs");
            } else if (numSBVTTestsWithLogs == 0) { // none have any test
                objective.setPassed(false);
                objective.setPartialData(false);
                objective.setNoData(false);
                objective.setMetrics("0.0% tests have review logs");
            }
        }

        System.out.println(
                "Objective " + objective.getId() + " has passed = " + objective.getPassed());

        return objective;
    }

    public static PsacNode.Objective processObjectiveA7_3(PsacNode.Objective objective) {
        /**
         * This objective will pass if 1. There are some associated SRS_Req 2. all the associated
         * SRS_Req in the output have some test 3. All tests have passed for each requirement
         */
        //        Boolean srsPresenceFlag = false; // must be true at end
        //        Boolean srsNoTestForSomeFlag = false; // must be false at end
        //        Boolean srsTestFailedFlag= false; // must be false at end
        //        Boolean srsSomeTestFlag= false; // must be true at end
        //
        //        for (Requirement req : objective.getObjOutputs().getRequirements()) {
        //            if (req.getType().equals("SRS_Req")) {
        //                srsPresenceFlag = true;
        //                if (req.getTests().size() < 1) { // no test
        //                    srsNoTestForSomeFlag = true;
        //                    break;
        //                }
        //                else {
        //                	srsSomeTestFlag= true;
        //                	for(Test tst : req.getTests()) {
        //                		if(!(tst.getResult().equalsIgnoreCase("Passed"))) {
        //                			srsTestFailedFlag= true;
        //                			break;
        //                		}
        //                	}
        //                }
        //            }
        //        }
        //
        //        //TODO: the partial condition may need to be broadened
        //
        //        if(!srsSomeTestFlag) { // there are no tests at all
        //            // Case: Some SRS
        //            if (srsPresenceFlag) {
        //                objective.setPassed(false);
        //                objective.setPartialData(false);
        //                objective.setNoData(false);
        //            }
        //            // Case: No SRS
        //            else if (!srsPresenceFlag) {
        //                objective.setPassed(false);
        //                objective.setPartialData(false);
        //                objective.setNoData(true);
        //            }
        //        }
        //        else { // there are at least some tests
        //        	if (!srsTestFailedFlag) { // no failed tests
        //                // Case: Some SRS, some passed tests for each SRS
        //                if (srsPresenceFlag && !srsNoTestForSomeFlag) {
        //                    objective.setPassed(true);
        //                    objective.setPartialData(false);
        //                    objective.setNoData(false);
        //                }
        //                // Case: Some SRS, no test for some SRS
        //                else if (srsPresenceFlag && srsNoTestForSomeFlag) {
        //                    objective.setPassed(false);
        //                    objective.setPartialData(true);
        //                    objective.setNoData(false);
        //                }
        //                // Case: No SRS
        //                else if (!srsPresenceFlag) {
        //                    objective.setPassed(false);
        //                    objective.setPartialData(true);
        //                    objective.setNoData(false);
        //                }
        //        	}
        //        	else { // some failed tests
        //                // Case: Some SRS
        //                if (srsPresenceFlag) {
        //                    objective.setPassed(false);
        //                    objective.setPartialData(false);
        //                    objective.setNoData(false);
        //                }
        //                // Case: No SRS
        //                else if (!srsPresenceFlag) {
        //                    objective.setPassed(false);
        //                    objective.setPartialData(true);
        //                    objective.setNoData(false);
        //                }
        //        	}
        //        }

        int numSRS = objective.getObjOutputs().getRequirements().size();
        int numSRSWithAllPassedTests = 0;
        int numSRSWithNoTests = 0;

        for (Requirement req : objective.getObjOutputs().getRequirements()) {
            if (req.getTests().size() < 1) { // no test
                numSRSWithNoTests = numSRSWithNoTests + 1;
            } else {
                Boolean failTestFlag = false;
                for (Test tst : req.getTests()) {
                    if (!(tst.getResult().equalsIgnoreCase("Passed"))) {
                        failTestFlag = true;
                        break;
                    }
                }
                if (!failTestFlag) {
                    numSRSWithAllPassedTests = numSRSWithAllPassedTests + 1;
                }
            }
        }

        if (numSRS > 1) { // Some SRS
            if (numSRSWithNoTests == 0) { // all srs have tests
                if (numSRS == numSRSWithAllPassedTests) { // all SRS have passed tests
                    objective.setPassed(true);
                    objective.setPartialData(false);
                    objective.setNoData(false);
                    //			  double percent = ((double) (numSubDDWithTrace)/numSubDD) * 100.00;
                    objective.setMetrics("100.0% requirements have all passed test results");
                } else { // not all SRS have passed tests
                    objective.setPassed(false);
                    objective.setPartialData(false);
                    objective.setNoData(false);
                    double percent = ((double) (numSRSWithAllPassedTests) / numSRS) * 100.00;
                    objective.setMetrics(
                            String.format("%.2f", percent)
                                    + "% requirements have all passed test results");
                }
            } else if ((numSRSWithNoTests > 0)
                    && (numSRSWithNoTests < numSRS)) { // Not all SRS have tests
                objective.setPassed(false);
                objective.setPartialData(true);
                objective.setNoData(false);
                double percent = ((double) (numSRS - numSRSWithNoTests) / numSRS) * 100.00;
                objective.setMetrics(
                        String.format("%.2f", percent) + "% requirements have test results");
            } else if ((numSRSWithNoTests == numSRS)) { // No SRS has Tests
                objective.setPassed(false);
                objective.setPartialData(false);
                objective.setNoData(false);
                double percent = ((double) (numSRS - numSRSWithNoTests) / numSRS) * 100.00;
                objective.setMetrics(
                        String.format("%.2f", percent) + "% requirements have test results");
            }

        } else { // No SRS
            objective.setPassed(false);
            objective.setPartialData(false);
            objective.setNoData(true);
            objective.setMetrics("No requirements");
        }

        System.out.println(
                "Objective " + objective.getId() + " has passed = " + objective.getPassed());

        return objective;
    }

    public static PsacNode.Objective processObjectiveA7_4(PsacNode.Objective objective) {
        /**
         * This objective will pass if 1. There are some associated SubDD_Req 2. all the associated
         * SubDD_Req in the output have some test 3. All tests have passed for each requirement
         */
        //        Boolean subddPresenceFlag = false; // must be true at end
        //        Boolean subddNoTestForSomeFlag = false; // must be false at end
        //        Boolean subddTestFailedFlag= false; // must be false at end
        //        Boolean subddSomeTestFlag= false; // must be true at end
        //
        //        for (Requirement req : objective.getObjOutputs().getRequirements()) {
        //            if (req.getType().equals("SubDD_Req")) {
        //                subddPresenceFlag = true;
        //                if (req.getTests().size() < 1) { // no test
        //                    subddNoTestForSomeFlag = true;
        //                    break;
        //                }
        //                else {
        //                	subddSomeTestFlag= true;
        //                	for(Test tst : req.getTests()) {
        //                		if(!(tst.getResult().equalsIgnoreCase("Passed"))) {
        //                			subddTestFailedFlag= true;
        //                			break;
        //                		}
        //                	}
        //                }
        //            }
        //        }
        //
        //        //TODO: the partial condition may need to be broadened
        //
        //        if(!subddSomeTestFlag) { // there are no tests at all
        //            // Case: Some SRS
        //            if (subddPresenceFlag) {
        //                objective.setPassed(false);
        //                objective.setPartialData(false);
        //                objective.setNoData(false);
        //            }
        //            // Case: No SRS
        //            else if (!subddPresenceFlag) {
        //                objective.setPassed(false);
        //                objective.setPartialData(false);
        //                objective.setNoData(true);
        //            }
        //        }
        //        else { // there are at least some tests
        //        	if (!subddTestFailedFlag) { // no failed tests
        //                // Case: Some SRS, some passed tests for each SRS
        //                if (subddPresenceFlag && !subddNoTestForSomeFlag) {
        //                    objective.setPassed(true);
        //                    objective.setPartialData(false);
        //                    objective.setNoData(false);
        //                }
        //                // Case: Some SRS, no test for some SRS
        //                else if (subddPresenceFlag && subddNoTestForSomeFlag) {
        //                    objective.setPassed(false);
        //                    objective.setPartialData(true);
        //                    objective.setNoData(false);
        //                }
        //                // Case: No SRS
        //                else if (!subddPresenceFlag) {
        //                    objective.setPassed(false);
        //                    objective.setPartialData(true);
        //                    objective.setNoData(false);
        //                }
        //        	}
        //        	else { // some failed tests
        //                // Case: Some SRS
        //                if (subddPresenceFlag) {
        //                    objective.setPassed(false);
        //                    objective.setPartialData(false);
        //                    objective.setNoData(false);
        //                }
        //                // Case: No SRS
        //                else if (!subddPresenceFlag) {
        //                    objective.setPassed(false);
        //                    objective.setPartialData(true);
        //                    objective.setNoData(false);
        //                }
        //        	}
        //        }

        int numSubDD = objective.getObjOutputs().getRequirements().size();
        int numSubDDWithAllPassedTests = 0;
        int numSubDDWithNoTests = 0;

        for (Requirement req : objective.getObjOutputs().getRequirements()) {
            if (req.getTests().size() < 1) { // no test
                numSubDDWithNoTests = numSubDDWithNoTests + 1;
            } else {
                Boolean failTestFlag = false;
                for (Test tst : req.getTests()) {
                    if (!(tst.getResult().equalsIgnoreCase("Passed"))) {
                        failTestFlag = true;
                        break;
                    }
                }
                if (!failTestFlag) {
                    numSubDDWithAllPassedTests = numSubDDWithAllPassedTests + 1;
                }
            }
        }

        if (numSubDD > 1) { // Some SubDD
            if (numSubDDWithNoTests == 0) { // all SubDD have tests
                if (numSubDD == numSubDDWithAllPassedTests) { // all SubDD have passed tests
                    objective.setPassed(true);
                    objective.setPartialData(false);
                    objective.setNoData(false);
                    //			  double percent = ((double) (numSubDDWithTrace)/numSubDD) * 100.00;
                    objective.setMetrics("100.0% requirements have all passed test results");
                } else { // not all SubDD have passed tests
                    objective.setPassed(false);
                    objective.setPartialData(false);
                    objective.setNoData(false);
                    double percent = ((double) (numSubDDWithAllPassedTests) / numSubDD) * 100.00;
                    objective.setMetrics(
                            String.format("%.2f", percent)
                                    + "% requirements have all passed test results");
                }
            } else if ((numSubDDWithNoTests > 0)
                    && (numSubDDWithNoTests < numSubDD)) { // Not all SubDD have tests
                objective.setPassed(false);
                objective.setPartialData(true);
                objective.setNoData(false);
                double percent = ((double) (numSubDD - numSubDDWithNoTests) / numSubDD) * 100.00;
                objective.setMetrics(
                        String.format("%.2f", percent) + "% requirements have test results");
            } else if ((numSubDDWithNoTests == numSubDD)) { // No SubDD has Tests
                objective.setPassed(false);
                objective.setPartialData(false);
                objective.setNoData(false);
                double percent = ((double) (numSubDD - numSubDDWithNoTests) / numSubDD) * 100.00;
                objective.setMetrics(
                        String.format("%.2f", percent) + "% requirements have test results");
            }
        } else { // No SubDD
            objective.setPassed(false);
            objective.setPartialData(false);
            objective.setNoData(true);
            objective.setMetrics("No requirements");
        }

        System.out.println(
                "Objective " + objective.getId() + " has passed = " + objective.getPassed());

        return objective;
    }
}
