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
package com.ge.research.rite.utils;

import java.util.ArrayList;
import java.util.Arrays;

public class Core {
    public static final String nodegroupMetadataFile = "metadata.yaml";
    public static String selectedProject = "";
    public static ArrayList<String> rackOntology =
            new ArrayList<>(
                    Arrays.asList(
                            "http://arcos.rite/SOFTWARE#CODE_GEN",
                            "http://arcos.rite/SYSTEM#SYSTEM_DEVELOPMENT",
                            "http://arcos.rite/FILE#HASH_TYPE",
                            "http://arcos.rite/MODEL#MODEL",
                            "http://arcos.rite/SECURITY#THREAT",
                            "http://arcos.rite/TESTING#TEST_DEVELOPMENT",
                            "http://arcos.rite/DOCUMENT#DESCRIPTION",
                            "http://arcos.rite/SYSTEM#SYSTEM",
                            "http://arcos.rite/TESTING#TEST_STATUS",
                            "http://arcos.rite/PROV-S#AGENT",
                            "http://arcos.rite/SOFTWARE#COMPILE",
                            "http://arcos.rite/DOCUMENT#SECTION",
                            "http://arcos.rite/SOFTWARE#CODE_DEVELOPMENT",
                            "http://arcos.rite/PROCESS#OBJECTIVE",
                            "http://arcos.rite/TESTING#TEST_PROCEDURE",
                            "http://arcos.rite/PROV-S#THING",
                            "http://arcos.rite/SECURITY#THREAT_IDENTIFICATION",
                            "http://arcos.rite/DOCUMENT#DOCUMENT",
                            "http://arcos.rite/SYSTEM#INTERFACE",
                            "http://arcos.rite/SYSTEM#OP_ENV",
                            "http://arcos.rite/FILE#FILE_HASH",
                            "http://arcos.rite/ANALYSIS#ANALYSIS",
                            "http://arcos.rite/TESTING#TEST_EXECUTION",
                            "http://arcos.rite/PROV-S#ENTITY",
                            "http://arcos.rite/REVIEW#REVIEW_STATE",
                            "http://arcos.rite/DOCUMENT#DOC_STATUS",
                            "http://arcos.rite/REQUIREMENTS#DATA_DICTIONARY_TERM",
                            "http://arcos.rite/SYSTEM#OP_PROCEDURE",
                            "http://arcos.rite/HAZARD#HAZARD",
                            "http://arcos.rite/AGENTS#ORGANIZATION",
                            "http://arcos.rite/PROV-S#COLLECTION",
                            "http://arcos.rite/SOFTWARE#COMPONENT_TYPE",
                            "http://arcos.rite/TESTING#TEST_RECORD",
                            "http://arcos.rite/FILE#FILE_CREATION",
                            "http://arcos.rite/FILE#FORMAT",
                            "http://arcos.rite/CONFIDENCE#BDU_CONFIDENCE_ASSESSMENT",
                            "http://arcos.rite/DOCUMENT#SPECIFICATION",
                            "http://arcos.rite/SOFTWARE#SWCOMPONENT",
                            "http://arcos.rite/CONFIDENCE#ASSESSING_CONFIDENCE",
                            "http://arcos.rite/TESTING#TEST_STEP",
                            "http://arcos.rite/AGENTS#PERSON",
                            "http://arcos.rite/DOCUMENT#REPORT",
                            "http://arcos.rite/REVIEW#REVIEW",
                            "http://arcos.rite/SOFTWARE#PACKAGE",
                            "http://arcos.rite/DOCUMENT#REQUEST",
                            "http://arcos.rite/SOFTWARE#BUILD",
                            "http://arcos.rite/HARDWARE#HWCOMPONENT",
                            "http://arcos.rite/SECURITY#SECURITY_LABEL",
                            "http://arcos.rite/CONFIDENCE#CONFIDENCE_ASSESSMENT",
                            "http://arcos.rite/ANALYSIS#ANALYSIS_OUTPUT",
                            "http://arcos.rite/TESTING#TEST_LOG",
                            "http://arcos.rite/HAZARD#HAZARD_IDENTIFICATION",
                            "http://arcos.rite/PROCESS#PROPERTY",
                            "http://arcos.rite/TESTING#TEST",
                            "http://arcos.rite/TESTING#TEST_RESULT",
                            "http://arcos.rite/SECURITY#CONTROL",
                            "http://arcos.rite/DOCUMENT#PLAN",
                            "http://arcos.rite/REVIEW#REVIEW_LOG",
                            "http://arcos.rite/REQUIREMENTS#REQUIREMENT_DEVELOPMENT",
                            "http://arcos.rite/AGENTS#TOOL",
                            "http://arcos.rite/HARDWARE#PARTITION",
                            "http://arcos.rite/FILE#FILE",
                            "http://arcos.rite/SECURITY#CONTROLSET",
                            "http://arcos.rite/HARDWARE#HWCOMPONENT_TYPE",
                            "http://arcos.rite/DOCUMENT#PROCEDURE",
                            "http://arcos.rite/BASELINE#BASELINE",
                            "http://arcos.rite/SYSTEM#FUNCTION",
                            "http://arcos.rite/REQUIREMENTS#REQUIREMENT",
                            "http://arcos.rite/PROV-S#ACTIVITY"));
    public static ArrayList<String> defaultNodegroups =
            new ArrayList<>(
                    Arrays.asList(
                            "ingest_CODE_GEN",
                            "ingest_SYSTEM_DEVELOPMENT",
                            "ingest_ImplControl",
                            "ingest_THREAT",
                            "ingest_AGENT",
                            "ingest_SECTION",
                            "ingest_INTERFACE",
                            "ingest_OP_ENV",
                            "ingest_ASSESSING_CONFIDENCE",
                            "ingest_REVIEW",
                            "ingest_PACKAGE",
                            "ingest_REQUEST",
                            "ingest_SECURITY_LABEL",
                            "ingest_PROPERTY",
                            "ingest_FILE",
                            "ingest_PROCEDURE",
                            "ingest_FUNCTION",
                            "ingest_ConnectionType",
                            "ingest_HASH_TYPE",
                            "ingest_Cps",
                            "ingest_DESCRIPTION",
                            "ingest_SYSTEM",
                            "ingest_TEST_STATUS",
                            "ingest_DOCUMENT",
                            "ingest_TEST_EXECUTION",
                            "ingest_REVIEW_STATE",
                            "ingest_ORGANIZATION",
                            "ingest_Connection",
                            "ingest_COLLECTION",
                            "ingest_TEST_RECORD",
                            "ingest_FORMAT",
                            "ingest_CONTROL",
                            "ingest_PLAN",
                            "ingest_TOOL",
                            "ingest_REQUIREMENT",
                            "ingest_ACTIVITY",
                            "ingest_MODEL",
                            "ingest_TEST_DEVELOPMENT",
                            "ingest_THING",
                            "ingest_THREAT_IDENTIFICATION",
                            "ingest_ANALYSIS",
                            "ingest_DATA_DICTIONARY_TERM",
                            "ingest_HAZARD",
                            "ingest_FILE_CREATION",
                            "ingest_BDU_CONFIDENCE_ASSESSMENT",
                            "ingest_SPECIFICATION",
                            "ingest_TEST_STEP",
                            "ingest_CONFIDENCE_ASSESSMENT",
                            "ingest_TEST_LOG",
                            "ingest_TEST",
                            "ingest_TEST_RESULT",
                            "ingest_REQUIREMENT_DEVELOPMENT",
                            "ingest_BASELINE",
                            "ingest_COMPILE",
                            "ingest_CODE_DEVELOPMENT",
                            "ingest_OBJECTIVE",
                            "ingest_TEST_PROCEDURE",
                            "ingest_FILE_HASH",
                            "ingest_ENTITY",
                            "ingest_DOC_STATUS",
                            "ingest_OP_PROCEDURE",
                            "ingest_COMPONENT_TYPE",
                            "ingest_SWCOMPONENT",
                            "ingest_PERSON",
                            "ingest_REPORT",
                            "ingest_BUILD",
                            "ingest_HWCOMPONENT",
                            "ingest_ANALYSIS_OUTPUT",
                            "ingest_HAZARD_IDENTIFICATION",
                            "ingest_REVIEW_LOG",
                            "ingest_PARTITION",
                            "ingest_CONTROLSET",
                            "ingest_HWCOMPONENT_TYPE",
                            "ingest_CpsType",
                            "ingest_Pedigree",
                            "query Files of a Given Format",
                            "query Requirements without Tests",
                            "query Testcase without requirement",
                            "query Requirements with failed test result",
                            "query Requirements decomposition",
                            "query Interface structure",
                            "query Requirements with Tests",
                            "query Trace Hazards to Tests",
                            "query Trace Requirements to Tests",
                            "query Hazard structure",
                            "query Terms consumedBy Requirement",
                            "query Compilation Inputs",
                            "query System Structure",
                            "query Requirements without passed test",
                            "query Models for Thing",
                            "query Get DataInsertedBy From Guid",
                            "query dataVer INTERFACE without destination SYSTEM",
                            "query dataVer INTERFACE without source SYSTEM",
                            "query dataVer only REQUIREMENT subclasses",
                            "query dataVer SBVT_Result without confirms_SBVT_Test",
                            "query dataVer SBVT_Test without REQUIREMENT",
                            "query dataVer SRS_Req dataInsertedBy other than SRS Data Ingestion",
                            "query dataVer SRS_Req without CSID or PIDS",
                            "query dataVer SRS_Req without description",
                            "query dataVer SRS_Req without verifies SBVT_Test",
                            "query dataVer SubDD_Req without satisfies SRS_Req",
                            "query dataVer SYSTEM without partOf SYSTEM",
                            "query dataVer unlinked SWCOMPONENT",
                            "demoNodegroup"));

    public static final String CREATE_INSTANCE_DATA_LABEL = "Create Instance Data: ";
    public static final String INSTANCE_DATA_FOLDER = "/InstanceData/";
    public static final String NODEGROUP_DATA_FOLDER = "/nodegroups/";
    public static final String QUERY_RESULTS_FOLDER = "QueryResults";
    public static final String SADL_GOAL = "http://sadl.org/gsn.sadl#Goal";
    public static final String SADL_UNDEVGOAL = "http://sadl.org/gsn.sadl#UndevelopedGoal";
    public static final String SADL_STRATEGY = "http://sadl.org/gsn.sadl#Strategy";
    public static final String SADL_UNDEVSTRATEGY = "http://sadl.org/gsn.sadl#UndevelopedStrategy";
    public static final String SADL_ASSUMPTION = "http://sadl.org/gsn.sadl#Assumption";
    public static final String SADL_CONTEXT = "http://sadl.org/gsn.sadl#Context";
    public static final String SADL_SOLUTION = "http://sadl.org/gsn.sadl#Solution";
    public static final String SADL_JUSTIFICATION = "http://sadl.org/gsn.sadl#Justification";
    public static final String NODEGROUP_INGEST_COMMENT = "ingestion from rack plugin";
}
