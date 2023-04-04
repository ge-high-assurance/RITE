/** */
package com.ge.research.rack.report.constants;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Saswata Paul
 */
public class PsacQueriesBoeing {

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
        A5_5_SWCOMP_REQ_TRACE("Objective-A5-5-query-Boeing-swcomponent-subDD-trace"),
        A7_3_4_SBVT_TEST("Objective-A7-3-4-query-Boeing-SBVT-Test"),
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
