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
package com.ge.research.rack.utils;

import com.ge.research.semtk.load.utility.IngestionNodegroupBuilder;
import com.ge.research.semtk.load.utility.SparqlGraphJson;
import com.ge.research.semtk.sparqlX.SparqlConnection;
import java.util.*;

public class IngestionTemplateUtil {
    public static HashMap<String, String> csvTemplates = new HashMap<>();
    private static final String ID_REGEX_DEFAULT = "identifier";

    private static String getPrefix(String className) {
        String[] split = className.split("/");
        if (split.length >= 3) {
            String dir = split[2];
            dir = dir.replaceAll("arcos\\.rack", "");
            dir = dir.replaceAll("arcos\\.", "");
            return dir;
        }
        return "";
    }

    public static void buildCsvTemplates() throws Exception {
        SparqlConnection conn = ConnectionUtil.getSparqlConnection();
        if (conn == null) {
            return;
        }
        ArrayList<String> classList = OntologyUtil.getClassNames();

        if (classList == null) {
            return;
        }

        for (String className : classList) {
            String ingestionId = IngestionTemplateUtil.getIngestionNodegroupId(className);
            String template = IngestionTemplateUtil.getNodegroupCSVTemplate(className);
            if (template == null) {
            	ErrorMessageUtil.error("Unable to get CSV template for: " + ingestionId);
            }
            csvTemplates.put(ingestionId, template);
        }
    }

    private static IngestionNodegroupBuilder getIngestionNodegroupBuilder(String className) {
        SparqlConnection conn = ConnectionUtil.getSparqlConnection();
        IngestionNodegroupBuilder builder =
                new IngestionNodegroupBuilder(className, conn, OntologyUtil.oInfo);
        builder.setIdRegex(ID_REGEX_DEFAULT);
        try {
            builder.build();
        } catch (Exception ex) {
            RackConsole.getConsole()
                    .error("Error processing ingestion nodegroup for :" + className);
            return null;
        }
        return builder;
    }

    public static String getNodegroupIngestionTemplate(String className) {
        IngestionNodegroupBuilder builder = getIngestionNodegroupBuilder(className);
        if (builder == null) {
            return null;
        }
        return builder.getNodegroupJsonStr();
    }

    public static String getNodegroupCSVTemplate(String className) {
        IngestionNodegroupBuilder builder = getIngestionNodegroupBuilder(className);
        if (builder == null) {
            return null;
        }
        return builder.getCsvTemplate();
    }

    public static String getIngestionNodegroupId(String className) {
        String localName = OntologyUtil.getLocalClassName(className);
        String prefix = getPrefix(className);
        if (!prefix.equals("")) {
            prefix += "_";
        }
        return "ingest_" + prefix + localName;
    }

    public static SparqlGraphJson getSparqlGraphJson(String className) {
        SparqlGraphJson json = null;
        try {
            json = new SparqlGraphJson(getNodegroupIngestionTemplate(className));
            json.setSparqlConn(ConnectionUtil.getSparqlConnection());
        } catch (Exception e) {
            RackConsole.getConsole()
                    .error(
                            "Error parsing nodegroup json for nodegroup: "
                                    + OntologyUtil.getLocalClassName(className));
        }

        return json;
    }
}
