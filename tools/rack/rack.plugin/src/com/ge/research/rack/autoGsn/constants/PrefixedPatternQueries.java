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
package com.ge.research.rack.autoGsn.constants;

/**
 * @author Saswata Paul
 *     <p>Prefixed generic queries required to fetch pattern data from RACK
 */
public class PrefixedPatternQueries {

    private static final String GSN_GET_GOAL_PATTERNS =
            "{\r\n"
                    + "	\"version\": 3,\r\n"
                    + "	\"sparqlConn\": {\r\n"
                    + "		\"name\": \"gsn_pattern_queries\",\r\n"
                    + "		\"domain\": \"\",\r\n"
                    + "		\"enableOwlImports\": true,\r\n"
                    + "		\"model\": [\r\n"
                    + "			{\r\n"
                    + "				\"type\": \"fuseki\",\r\n"
                    + "				\"url\": \"MODEL_URL_PH\",\r\n"
                    + "				\"graph\": \"MODEL_GRAPH_PH\"\r\n"
                    + "			}\r\n"
                    + "		],\r\n"
                    + "		\"data\": [\r\n"
                    + "			{\r\n"
                    + "				\"type\": \"fuseki\",\r\n"
                    + "				\"url\": \"DATA_URL_PH\",\r\n"
                    + "				\"graph\": \"DATA_GRAPH_PH\"\r\n"
                    + "			}\r\n"
                    + "		]\r\n"
                    + "	},\r\n"
                    + "	\"sNodeGroup\": {\r\n"
                    + "		\"version\": 19,\r\n"
                    + "		\"limit\": 0,\r\n"
                    + "		\"offset\": 0,\r\n"
                    + "		\"sNodeList\": [\r\n"
                    + "			{\r\n"
                    + "				\"propList\": [\r\n"
                    + "					{\r\n"
                    + "						\"valueTypes\": [\r\n"
                    + "							\"string\"\r\n"
                    + "						],\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#string\",\r\n"
                    + "						\"UriRelationship\": \"http://arcos.rack/PROV-S#description\",\r\n"
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?description\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					},\r\n"
                    + "					{\r\n"
                    + "						\"valueTypes\": [\r\n"
                    + "							\"string\"\r\n"
                    + "						],\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#string\",\r\n"
                    + "						\"UriRelationship\": \"http://arcos.rack/PROV-S#identifier\",\r\n"
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?identifier\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					},\r\n"
                    + "					{\r\n"
                    + "						\"valueTypes\": [\r\n"
                    + "							\"boolean\"\r\n"
                    + "						],\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#boolean\",\r\n"
                    + "						\"UriRelationship\": \"http://sadl.org/GSN-Pattern-core.sadl#isPat\",\r\n"
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?Pat\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					},\r\n"
                    + "					{\r\n"
                    + "						\"valueTypes\": [\r\n"
                    + "							\"string\"\r\n"
                    + "						],\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#string\",\r\n"
                    + "						\"UriRelationship\": \"http://sadl.org/GSN-Pattern-core.sadl#pGoal\",\r\n"
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?pGoal\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					}\r\n"
                    + "				],\r\n"
                    + "				\"nodeList\": [],\r\n"
                    + "				\"fullURIName\": \"http://sadl.org/GSN-core.sadl#Goal\",\r\n"
                    + "				\"SparqlID\": \"?Goal\",\r\n"
                    + "				\"isReturned\": false,\r\n"
                    + "				\"isRuntimeConstrained\": false,\r\n"
                    + "				\"valueConstraint\": \"\",\r\n"
                    + "				\"instanceValue\": null,\r\n"
                    + "				\"deletionMode\": \"NO_DELETE\"\r\n"
                    + "			}\r\n"
                    + "		],\r\n"
                    + "		\"orderBy\": [],\r\n"
                    + "		\"groupBy\": [],\r\n"
                    + "		\"unionHash\": {},\r\n"
                    + "		\"columnOrder\": []\r\n"
                    + "	},\r\n"
                    + "	\"importSpec\": {\r\n"
                    + "		\"version\": \"1\",\r\n"
                    + "		\"baseURI\": \"\",\r\n"
                    + "		\"columns\": [],\r\n"
                    + "		\"dataValidator\": [],\r\n"
                    + "		\"texts\": [],\r\n"
                    + "		\"transforms\": [],\r\n"
                    + "		\"nodes\": [\r\n"
                    + "			{\r\n"
                    + "				\"sparqlID\": \"?Goal\",\r\n"
                    + "				\"type\": \"http://sadl.org/GSN-core.sadl#Goal\",\r\n"
                    + "				\"mapping\": [],\r\n"
                    + "				\"props\": []\r\n"
                    + "			}\r\n"
                    + "		]\r\n"
                    + "	},\r\n"
                    + "	\"plotSpecs\": null\r\n"
                    + "}";

    private static final String GSN_GET_STRATEGY_PATTERNS =
            "{\r\n"
                    + "	\"version\": 3,\r\n"
                    + "	\"sparqlConn\": {\r\n"
                    + "		\"name\": \"gsn_pattern_queries\",\r\n"
                    + "		\"domain\": \"\",\r\n"
                    + "		\"enableOwlImports\": true,\r\n"
                    + "		\"model\": [\r\n"
                    + "			{\r\n"
                    + "				\"type\": \"fuseki\",\r\n"
                    + "				\"url\": \"MODEL_URL_PH\",\r\n"
                    + "				\"graph\": \"MODEL_GRAPH_PH\"\r\n"
                    + "			}\r\n"
                    + "		],\r\n"
                    + "		\"data\": [\r\n"
                    + "			{\r\n"
                    + "				\"type\": \"fuseki\",\r\n"
                    + "				\"url\": \"DATA_URL_PH\",\r\n"
                    + "				\"graph\": \"DATA_GRAPH_PH\"\r\n"
                    + "			}\r\n"
                    + "		]\r\n"
                    + "	},\r\n"
                    + "	\"sNodeGroup\": {\r\n"
                    + "		\"version\": 19,\r\n"
                    + "		\"limit\": 0,\r\n"
                    + "		\"offset\": 0,\r\n"
                    + "		\"sNodeList\": [\r\n"
                    + "			{\r\n"
                    + "				\"propList\": [\r\n"
                    + "					{\r\n"
                    + "						\"valueTypes\": [\r\n"
                    + "							\"string\"\r\n"
                    + "						],\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#string\",\r\n"
                    + "						\"UriRelationship\": \"http://arcos.rack/PROV-S#description\",\r\n"
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?description\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					},\r\n"
                    + "					{\r\n"
                    + "						\"valueTypes\": [\r\n"
                    + "							\"string\"\r\n"
                    + "						],\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#string\",\r\n"
                    + "						\"UriRelationship\": \"http://arcos.rack/PROV-S#identifier\",\r\n"
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?identifier\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					},\r\n"
                    + "					{\r\n"
                    + "						\"valueTypes\": [\r\n"
                    + "							\"boolean\"\r\n"
                    + "						],\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#boolean\",\r\n"
                    + "						\"UriRelationship\": \"http://sadl.org/GSN-Pattern-core.sadl#isPat\",\r\n"
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?Pat\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					},\r\n"
                    + "					{\r\n"
                    + "						\"valueTypes\": [\r\n"
                    + "							\"string\"\r\n"
                    + "						],\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#string\",\r\n"
                    + "						\"UriRelationship\": \"http://sadl.org/GSN-Pattern-core.sadl#pGoal\",\r\n"
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?pGoal\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					},\r\n"
                    + "					{\r\n"
                    + "						\"valueTypes\": [\r\n"
                    + "							\"string\"\r\n"
                    + "						],\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#string\",\r\n"
                    + "						\"UriRelationship\": \"http://sadl.org/GSN-Pattern-core.sadl#pGoalSubGoalConnector\",\r\n"
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?pGoalSubGoalConnector\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					},\r\n"
                    + "					{\r\n"
                    + "						\"valueTypes\": [\r\n"
                    + "							\"string\"\r\n"
                    + "						],\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#string\",\r\n"
                    + "						\"UriRelationship\": \"http://sadl.org/GSN-Pattern-core.sadl#pSubGoal\",\r\n"
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?pSubGoal\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					}\r\n"
                    + "				],\r\n"
                    + "				\"nodeList\": [],\r\n"
                    + "				\"fullURIName\": \"http://sadl.org/GSN-core.sadl#Strategy\",\r\n"
                    + "				\"SparqlID\": \"?Strategy\",\r\n"
                    + "				\"isReturned\": false,\r\n"
                    + "				\"isRuntimeConstrained\": false,\r\n"
                    + "				\"valueConstraint\": \"\",\r\n"
                    + "				\"instanceValue\": null,\r\n"
                    + "				\"deletionMode\": \"NO_DELETE\"\r\n"
                    + "			}\r\n"
                    + "		],\r\n"
                    + "		\"orderBy\": [],\r\n"
                    + "		\"groupBy\": [],\r\n"
                    + "		\"unionHash\": {},\r\n"
                    + "		\"columnOrder\": []\r\n"
                    + "	},\r\n"
                    + "	\"importSpec\": {\r\n"
                    + "		\"version\": \"1\",\r\n"
                    + "		\"baseURI\": \"\",\r\n"
                    + "		\"columns\": [],\r\n"
                    + "		\"dataValidator\": [],\r\n"
                    + "		\"texts\": [],\r\n"
                    + "		\"transforms\": [],\r\n"
                    + "		\"nodes\": [\r\n"
                    + "			{\r\n"
                    + "				\"sparqlID\": \"?Strategy\",\r\n"
                    + "				\"type\": \"http://sadl.org/GSN-core.sadl#Strategy\",\r\n"
                    + "				\"mapping\": [],\r\n"
                    + "				\"props\": []\r\n"
                    + "			}\r\n"
                    + "		]\r\n"
                    + "	},\r\n"
                    + "	\"plotSpecs\": null\r\n"
                    + "}";

    private static final String GSN_GET_EVIDENCE_PATTERNS =
            "{\r\n"
                    + "	\"version\": 3,\r\n"
                    + "	\"sparqlConn\": {\r\n"
                    + "		\"name\": \"gsn_pattern_queries\",\r\n"
                    + "		\"domain\": \"\",\r\n"
                    + "		\"enableOwlImports\": true,\r\n"
                    + "		\"model\": [\r\n"
                    + "			{\r\n"
                    + "				\"type\": \"fuseki\",\r\n"
                    + "				\"url\": \"MODEL_URL_PH\",\r\n"
                    + "				\"graph\": \"MODEL_GRAPH_PH\"\r\n"
                    + "			}\r\n"
                    + "		],\r\n"
                    + "		\"data\": [\r\n"
                    + "			{\r\n"
                    + "				\"type\": \"fuseki\",\r\n"
                    + "				\"url\": \"DATA_URL_PH\",\r\n"
                    + "				\"graph\": \"DATA_GRAPH_PH\"\r\n"
                    + "			}\r\n"
                    + "		]\r\n"
                    + "	},\r\n"
                    + "	\"sNodeGroup\": {\r\n"
                    + "		\"version\": 19,\r\n"
                    + "		\"limit\": 0,\r\n"
                    + "		\"offset\": 0,\r\n"
                    + "		\"sNodeList\": [\r\n"
                    + "			{\r\n"
                    + "				\"propList\": [\r\n"
                    + "					{\r\n"
                    + "						\"valueTypes\": [\r\n"
                    + "							\"string\"\r\n"
                    + "						],\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#string\",\r\n"
                    + "						\"UriRelationship\": \"http://sadl.org/GSN-Pattern-core.sadl#classId\",\r\n"
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?classId\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					},\r\n"
                    + "					{\r\n"
                    + "						\"valueTypes\": [\r\n"
                    + "							\"string\"\r\n"
                    + "						],\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#string\",\r\n"
                    + "						\"UriRelationship\": \"http://sadl.org/GSN-Pattern-core.sadl#passValue\",\r\n"
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?passValue\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					},\r\n"
                    + "					{\r\n"
                    + "						\"valueTypes\": [\r\n"
                    + "							\"string\"\r\n"
                    + "						],\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#string\",\r\n"
                    + "						\"UriRelationship\": \"http://sadl.org/GSN-Pattern-core.sadl#statusProperty\",\r\n"
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?statusProperty\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					}\r\n"
                    + "				],\r\n"
                    + "				\"nodeList\": [],\r\n"
                    + "				\"fullURIName\": \"http://sadl.org/GSN-Pattern-core.sadl#GsnEvidence\",\r\n"
                    + "				\"SparqlID\": \"?GsnEvidence\",\r\n"
                    + "				\"isReturned\": false,\r\n"
                    + "				\"isRuntimeConstrained\": false,\r\n"
                    + "				\"valueConstraint\": \"\",\r\n"
                    + "				\"instanceValue\": null,\r\n"
                    + "				\"deletionMode\": \"NO_DELETE\"\r\n"
                    + "			}\r\n"
                    + "		],\r\n"
                    + "		\"orderBy\": [],\r\n"
                    + "		\"groupBy\": [],\r\n"
                    + "		\"unionHash\": {},\r\n"
                    + "		\"columnOrder\": []\r\n"
                    + "	},\r\n"
                    + "	\"importSpec\": {\r\n"
                    + "		\"version\": \"1\",\r\n"
                    + "		\"baseURI\": \"\",\r\n"
                    + "		\"columns\": [],\r\n"
                    + "		\"dataValidator\": [],\r\n"
                    + "		\"texts\": [],\r\n"
                    + "		\"transforms\": [],\r\n"
                    + "		\"nodes\": [\r\n"
                    + "			{\r\n"
                    + "				\"sparqlID\": \"?GsnEvidence\",\r\n"
                    + "				\"type\": \"http://sadl.org/GSN-Pattern-core.sadl#GsnEvidence\",\r\n"
                    + "				\"mapping\": [],\r\n"
                    + "				\"props\": []\r\n"
                    + "			}\r\n"
                    + "		]\r\n"
                    + "	},\r\n"
                    + "	\"plotSpecs\": []\r\n"
                    + "}";

    /**
     * @return the gsnGetGoalPatterns
     */
    public static String getGsnGetGoalPatterns() {
        return GSN_GET_GOAL_PATTERNS;
    }

    /**
     * @return the gsnGetStrategyPatterns
     */
    public static String getGsnGetStrategyPatterns() {
        return GSN_GET_STRATEGY_PATTERNS;
    }

    /**
     * @return the gsnGetEvidencePatterns
     */
    public static String getGsnGetEvidencePatterns() {
        return GSN_GET_EVIDENCE_PATTERNS;
    }
}
