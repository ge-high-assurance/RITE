/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2024, GE Aerospace and Galois, Inc.
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
                            "http://arcos.rack/SOFTWARE#CODE_GEN",
                            "http://arcos.rack/SYSTEM#SYSTEM_DEVELOPMENT",
                            "http://arcos.rack/FILE#HASH_TYPE",
                            "http://arcos.rack/MODEL#MODEL",
                            "http://arcos.rack/SECURITY#THREAT",
                            "http://arcos.rack/TESTING#TEST_DEVELOPMENT",
                            "http://arcos.rack/DOCUMENT#DESCRIPTION",
                            "http://arcos.rack/SYSTEM#SYSTEM",
                            "http://arcos.rack/TESTING#TEST_STATUS",
                            "http://arcos.rack/PROV-S#AGENT",
                            "http://arcos.rack/SOFTWARE#COMPILE",
                            "http://arcos.rack/DOCUMENT#SECTION",
                            "http://arcos.rack/SOFTWARE#CODE_DEVELOPMENT",
                            "http://arcos.rack/PROCESS#OBJECTIVE",
                            "http://arcos.rack/TESTING#TEST_PROCEDURE",
                            "http://arcos.rack/PROV-S#THING",
                            "http://arcos.rack/SECURITY#THREAT_IDENTIFICATION",
                            "http://arcos.rack/DOCUMENT#DOCUMENT",
                            "http://arcos.rack/SYSTEM#INTERFACE",
                            "http://arcos.rack/SYSTEM#OP_ENV",
                            "http://arcos.rack/FILE#FILE_HASH",
                            "http://arcos.rack/ANALYSIS#ANALYSIS",
                            "http://arcos.rack/TESTING#TEST_EXECUTION",
                            "http://arcos.rack/PROV-S#ENTITY",
                            "http://arcos.rack/REVIEW#REVIEW_STATE",
                            "http://arcos.rack/DOCUMENT#DOC_STATUS",
                            "http://arcos.rack/REQUIREMENTS#DATA_DICTIONARY_TERM",
                            "http://arcos.rack/SYSTEM#OP_PROCEDURE",
                            "http://arcos.rack/HAZARD#HAZARD",
                            "http://arcos.rack/AGENTS#ORGANIZATION",
                            "http://arcos.rack/PROV-S#COLLECTION",
                            "http://arcos.rack/SOFTWARE#COMPONENT_TYPE",
                            "http://arcos.rack/TESTING#TEST_RECORD",
                            "http://arcos.rack/FILE#FILE_CREATION",
                            "http://arcos.rack/FILE#FORMAT",
                            "http://arcos.rack/CONFIDENCE#BDU_CONFIDENCE_ASSESSMENT",
                            "http://arcos.rack/DOCUMENT#SPECIFICATION",
                            "http://arcos.rack/SOFTWARE#SWCOMPONENT",
                            "http://arcos.rack/CONFIDENCE#ASSESSING_CONFIDENCE",
                            "http://arcos.rack/TESTING#TEST_STEP",
                            "http://arcos.rack/AGENTS#PERSON",
                            "http://arcos.rack/DOCUMENT#REPORT",
                            "http://arcos.rack/REVIEW#REVIEW",
                            "http://arcos.rack/SOFTWARE#PACKAGE",
                            "http://arcos.rack/DOCUMENT#REQUEST",
                            "http://arcos.rack/SOFTWARE#BUILD",
                            "http://arcos.rack/HARDWARE#HWCOMPONENT",
                            "http://arcos.rack/SECURITY#SECURITY_LABEL",
                            "http://arcos.rack/CONFIDENCE#CONFIDENCE_ASSESSMENT",
                            "http://arcos.rack/ANALYSIS#ANALYSIS_OUTPUT",
                            "http://arcos.rack/TESTING#TEST_LOG",
                            "http://arcos.rack/HAZARD#HAZARD_IDENTIFICATION",
                            "http://arcos.rack/PROCESS#PROPERTY",
                            "http://arcos.rack/TESTING#TEST",
                            "http://arcos.rack/TESTING#TEST_RESULT",
                            "http://arcos.rack/SECURITY#CONTROL",
                            "http://arcos.rack/DOCUMENT#PLAN",
                            "http://arcos.rack/REVIEW#REVIEW_LOG",
                            "http://arcos.rack/REQUIREMENTS#REQUIREMENT_DEVELOPMENT",
                            "http://arcos.rack/AGENTS#TOOL",
                            "http://arcos.rack/HARDWARE#PARTITION",
                            "http://arcos.rack/FILE#FILE",
                            "http://arcos.rack/SECURITY#CONTROLSET",
                            "http://arcos.rack/HARDWARE#HWCOMPONENT_TYPE",
                            "http://arcos.rack/DOCUMENT#PROCEDURE",
                            "http://arcos.rack/BASELINE#BASELINE",
                            "http://arcos.rack/SYSTEM#FUNCTION",
                            "http://arcos.rack/REQUIREMENTS#REQUIREMENT",
                            "http://arcos.rack/PROV-S#ACTIVITY"));
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
    public static final String NODEGROUP_INGEST_COMMENT = "Ingestion from RITE plugin";
}
