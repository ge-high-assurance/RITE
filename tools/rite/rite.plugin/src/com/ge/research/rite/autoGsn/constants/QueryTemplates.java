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
package com.ge.research.rite.autoGsn.constants;

/**
 * @author Saswata Paul
 */
public class QueryTemplates {

    /** Query template for a binary connection between two classes only returns identifiers */
    private static final String BINARY_CONN =
            "{\r\n"
                    + "	\"version\": 3,\r\n"
                    + "	\"sparqlConn\": {\r\n"
                    + "		\"name\": \"SPARQLCONN_NAME_PH\",\r\n"
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
<<<<<<< HEAD
                    + "						\"UriRelationship\": \"http://arcos.rack/PROV-S#identifier\",\r\n"
=======
                    + "						\"UriRelationship\": \"http://arcos.rite/PROV-S#identifier\",\r\n"
>>>>>>> 58d31630c3eca4cd02adf1b185a3c9fe3b893eb7
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?identifier_TARGET_CLASS_ID_PH\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					}\r\n"
                    + "				],\r\n"
                    + "				\"nodeList\": [],\r\n"
                    + "				\"fullURIName\": \"TARGET_CLASS_URI_PH\",\r\n"
                    + "				\"SparqlID\": \"?TARGET_CLASS_ID_PH\",\r\n"
                    + "				\"isReturned\": false,\r\n"
                    + "				\"isRuntimeConstrained\": false,\r\n"
                    + "				\"valueConstraint\": \"\",\r\n"
                    + "				\"instanceValue\": null,\r\n"
                    + "				\"deletionMode\": \"NO_DELETE\"\r\n"
                    + "			},\r\n"
                    + "			{\r\n"
                    + "				\"propList\": [\r\n"
                    + "					{\r\n"
                    + "						\"valueTypes\": [\r\n"
                    + "							\"string\"\r\n"
                    + "						],\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#string\",\r\n"
<<<<<<< HEAD
                    + "						\"UriRelationship\": \"http://arcos.rack/PROV-S#identifier\",\r\n"
=======
                    + "						\"UriRelationship\": \"http://arcos.rite/PROV-S#identifier\",\r\n"
>>>>>>> 58d31630c3eca4cd02adf1b185a3c9fe3b893eb7
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?identifier_SOURCE_CLASS_ID_PH\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					}\r\n"
                    + "				],\r\n"
                    + "				\"nodeList\": [\r\n"
                    + "					{\r\n"
                    + "						\"SnodeSparqlIDs\": [\r\n"
                    + "							\"?TARGET_CLASS_ID_PH\"\r\n"
                    + "						],\r\n"
                    + "						\"OptionalMinus\": [\r\n"
                    + "							1\r\n"
                    + "						],\r\n"
                    + "						\"Qualifiers\": [\r\n"
                    + "							\"\"\r\n"
                    + "						],\r\n"
                    + "						\"DeletionMarkers\": [\r\n"
                    + "							false\r\n"
                    + "						],\r\n"
                    + "						\"range\": [\r\n"
                    + "								SOURCE_CLASS_PROPERTY_RANGE_PH\r\n"
                    + "						],\r\n"
                    + "						\"ConnectBy\": \"triggers\",\r\n"
                    + "						\"Connected\": true,\r\n"
                    + "						\"UriConnectBy\": \"SOURCE_CLASS_PROPERTY_URI_PH\"\r\n"
                    + "					}\r\n"
                    + "				],\r\n"
                    + "				\"fullURIName\": \"SOURCE_CLASS_URI_PH\",\r\n"
                    + "				\"SparqlID\": \"?SOURCE_CLASS_ID_PH\",\r\n"
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
                    + "				\"sparqlID\": \"?SOURCE_CLASS_ID_PH\",\r\n"
                    + "				\"type\": \"SOURCE_CLASS_URI_PH\",\r\n"
                    + "				\"mapping\": [],\r\n"
                    + "				\"props\": []\r\n"
                    + "			},\r\n"
                    + "			{\r\n"
                    + "				\"sparqlID\": \"?TARGET_CLASS_ID_PH\",\r\n"
                    + "				\"type\": \"TARGET_CLASS_URI_PH\",\r\n"
                    + "				\"mapping\": [],\r\n"
                    + "				\"props\": []\r\n"
                    + "			}\r\n"
                    + "		]\r\n"
                    + "	},\r\n"
                    + "	\"plotSpecs\": []\r\n"
                    + "}";

    /**
     * Query template for a binary connection between two classes Returns identifiers and
     * descriptions
     */
    private static final String BINARY_CONN_WITH_DESCRIPTION =
            "{\r\n"
                    + "	\"version\": 3,\r\n"
                    + "	\"sparqlConn\": {\r\n"
                    + "		\"name\": \"SPARQLCONN_NAME_PH\",\r\n"
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
<<<<<<< HEAD
                    + "													\"UriRelationship\": \"http://arcos.rack/PROV-S#description\",	\r\n"
=======
                    + "													\"UriRelationship\": \"http://arcos.rite/PROV-S#description\",	\r\n"
>>>>>>> 58d31630c3eca4cd02adf1b185a3c9fe3b893eb7
                    + "						\"Constraints\": \"\",	\r\n"
                    + "						\"SparqlID\": \"?description_TARGET_CLASS_ID_PH\",	\r\n"
                    + "						\"isReturned\": true,	\r\n"
                    + "						\"optMinus\": 1,	\r\n"
                    + "						\"isRuntimeConstrained\": false,	\r\n"
                    + "						\"instanceValues\": [],	\r\n"
                    + "						\"isMarkedForDeletion\": false	\r\n"
                    + "					},	\r\n"
                    + "					{	\r\n"
                    + "						\"valueTypes\": [	\r\n"
                    + "							\"string\"	\r\n"
                    + "						],	\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#string\",\r\n"
<<<<<<< HEAD
                    + "						\"UriRelationship\": \"http://arcos.rack/PROV-S#identifier\",\r\n"
=======
                    + "						\"UriRelationship\": \"http://arcos.rite/PROV-S#identifier\",\r\n"
>>>>>>> 58d31630c3eca4cd02adf1b185a3c9fe3b893eb7
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?identifier_TARGET_CLASS_ID_PH\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					}\r\n"
                    + "				],\r\n"
                    + "				\"nodeList\": [],\r\n"
                    + "				\"fullURIName\": \"TARGET_CLASS_URI_PH\",\r\n"
                    + "				\"SparqlID\": \"?TARGET_CLASS_ID_PH\",\r\n"
                    + "				\"isReturned\": false,\r\n"
                    + "				\"isRuntimeConstrained\": false,\r\n"
                    + "				\"valueConstraint\": \"\",\r\n"
                    + "				\"instanceValue\": null,\r\n"
                    + "				\"deletionMode\": \"NO_DELETE\"\r\n"
                    + "			},\r\n"
                    + "			{\r\n"
                    + "				\"propList\": [\r\n"
                    + "					{\r\n"
                    + "						\"valueTypes\": [\r\n"
                    + "							\"string\"\r\n"
                    + "						],\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#string\",\r\n"
<<<<<<< HEAD
                    + "						\"UriRelationship\": \"http://arcos.rack/PROV-S#description\",	\r\n"
=======
                    + "						\"UriRelationship\": \"http://arcos.rite/PROV-S#description\",	\r\n"
>>>>>>> 58d31630c3eca4cd02adf1b185a3c9fe3b893eb7
                    + "						\"Constraints\": \"\",	\r\n"
                    + "						\"SparqlID\": \"?description_SOURCE_CLASS_ID_PH\",	\r\n"
                    + "						\"isReturned\": true,	\r\n"
                    + "						\"optMinus\": 1,	\r\n"
                    + "						\"isRuntimeConstrained\": false,	\r\n"
                    + "						\"instanceValues\": [],	\r\n"
                    + "						\"isMarkedForDeletion\": false	\r\n"
                    + "					},	\r\n"
                    + "					{	\r\n"
                    + "						\"valueTypes\": [	\r\n"
                    + "							\"string\"	\r\n"
                    + "						],	\r\n"
                    + "						\"rangeURI\": \"http://www.w3.org/2001/XMLSchema#string\",\r\n"
<<<<<<< HEAD
                    + "						\"UriRelationship\": \"http://arcos.rack/PROV-S#identifier\",\r\n"
=======
                    + "						\"UriRelationship\": \"http://arcos.rite/PROV-S#identifier\",\r\n"
>>>>>>> 58d31630c3eca4cd02adf1b185a3c9fe3b893eb7
                    + "						\"Constraints\": \"\",\r\n"
                    + "						\"SparqlID\": \"?identifier_SOURCE_CLASS_ID_PH\",\r\n"
                    + "						\"isReturned\": true,\r\n"
                    + "						\"optMinus\": 0,\r\n"
                    + "						\"isRuntimeConstrained\": false,\r\n"
                    + "						\"instanceValues\": [],\r\n"
                    + "						\"isMarkedForDeletion\": false\r\n"
                    + "					}\r\n"
                    + "				],\r\n"
                    + "				\"nodeList\": [\r\n"
                    + "					{\r\n"
                    + "						\"SnodeSparqlIDs\": [\r\n"
                    + "							\"?TARGET_CLASS_ID_PH\"\r\n"
                    + "						],\r\n"
                    + "						\"OptionalMinus\": [\r\n"
                    + "							1\r\n"
                    + "						],\r\n"
                    + "						\"Qualifiers\": [\r\n"
                    + "							\"\"\r\n"
                    + "						],\r\n"
                    + "						\"DeletionMarkers\": [\r\n"
                    + "							false\r\n"
                    + "						],\r\n"
                    + "						\"range\": [\r\n"
                    + "								SOURCE_CLASS_PROPERTY_RANGE_PH\r\n"
                    + "						],\r\n"
                    + "						\"ConnectBy\": \"triggers\",\r\n"
                    + "						\"Connected\": true,\r\n"
                    + "						\"UriConnectBy\": \"SOURCE_CLASS_PROPERTY_URI_PH\"\r\n"
                    + "					}\r\n"
                    + "				],\r\n"
                    + "				\"fullURIName\": \"SOURCE_CLASS_URI_PH\",\r\n"
                    + "				\"SparqlID\": \"?SOURCE_CLASS_ID_PH\",\r\n"
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
                    + "				\"sparqlID\": \"?SOURCE_CLASS_ID_PH\",\r\n"
                    + "				\"type\": \"SOURCE_CLASS_URI_PH\",\r\n"
                    + "				\"mapping\": [],\r\n"
                    + "				\"props\": []\r\n"
                    + "			},\r\n"
                    + "			{\r\n"
                    + "				\"sparqlID\": \"?TARGET_CLASS_ID_PH\",\r\n"
                    + "				\"type\": \"TARGET_CLASS_URI_PH\",\r\n"
                    + "				\"mapping\": [],\r\n"
                    + "				\"props\": []\r\n"
                    + "			}\r\n"
                    + "		]\r\n"
                    + "	},\r\n"
                    + "	\"plotSpecs\": []\r\n"
                    + "}";

    /**
     * @return the binary
     */
    public static String getBinary() {
        return BINARY_CONN;
    }

    /**
     * @return the binarywithdescription
     */
    public static String getBinarywithdescription() {
        return BINARY_CONN_WITH_DESCRIPTION;
    }
}
