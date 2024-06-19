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
package com.ge.research.rack.do178c.constants;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Saswata Paul
 */
public class DO178CQueries {

    public enum All {
        GET_PSAC("psac_to_activities"),
        A1_1_DOCS("Objective-A1-1-query-check-output-for-PSAC-SDP-SVP-SCMPlan-SQAPlan"),
        A1_2_DOCS(
                "Objective-A1-1-query-check-output-for-PSAC-SDP-SVP-SCMPlan-SQAPlan"), // same query
        A1_3_DOCS(
                "Objective-A1-1-query-check-output-for-PSAC-SDP-SVP-SCMPlan-SQAPlan"), // same query
        A1_4_DOCS(
                "Objective-A1-1-query-check-output-for-PSAC-SDP-SVP-SCMPlan-SQAPlan"), // same query
        A2_1_SRS("Objective-A2-1-query-count-all-SRS-Reqs"),
        A2_1_CSID("Objective-A2-1-query-count-all-CSID-Reqs"),
        A2_1_PIDS("Objective-A2-1-query-count-all-PIDS-Reqs"),
        A2_1_SRS_TO_PIDS_CSID(
                "Objective-A2-1-query-count-all-SRS-Reqs-that-satisfy-PIDS-or-CSID_Reqs"),
        A2_2_DERSRS("Objective-A2-2-query-count-all-Derived-SRS-Reqs"),
        A2_2_DERSRS_TRACE("Objective-A2-2-query-count-all-Derived-SRS-Reqs-that-satisfy-some-req"),
        A2_4_SUBDD("Objective-A2-4-query-count-all-SubDD-Reqs"),
        A2_4_SUBDD_TO_SRS("Objective-A2-4-query-count-all-SubDD-Reqs-that-satisfy-SRS_Reqs"),
        A2_5_DERSUBDD("Objective-A2-5-query-count-all-Derived-SubDD-Reqs"),
        A2_5_DERSUBDD_TRACE(
                "Objective-A2-5-query-count-all-Derived-SubDD-Reqs-that-satisfy-some-req"),
        A5_5_SWCOMP_REQ_TRACE("Objective-A5-5-query-swcomponent-subDD-trace"),
        A7_3_4_SBVT_TEST("Objective-A7-3-4-query-SBVT-Test"),
        ALL_REVIEW_LOGS("Objective-multiple-query-all-review-logs"),
        ALL_SOURCES("PSAC-query-source-document");

        private String qId;

        All(String id) {
            this.qId = id;
        }

        public String getQId() {
            return this.qId;
        }

        /**
         * To get all query Ids as a list of strings
         *
         * @return
         */
        public static List<String> getAllQueries() {
            List<String> actIds =
                    Stream.of(All.values()).map(All::getQId).collect(Collectors.toList());

            return actIds;
        }
    }
}
