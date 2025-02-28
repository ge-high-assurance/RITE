/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2025, GE Aerospace and Galois, Inc.
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

import com.ge.research.rite.autoGsn.constants.GsnCoreElements;
import com.ge.research.rite.autoGsn.structures.GsnSadl;

/**
 * @author Saswata Paul
 *     <p>Utilities for printing GsnSadlSentence objects
 */
public class GsnSadlPrintUtils {

    /**
     * Returns true if the value of the property must be written as string by checking against a
     * predefined set of properties in the Core GSN
     *
     * @param property
     * @return
     */
    public static Boolean propertyIsString(GsnSadl.RelationPair property) {

        Boolean flag = true;

        // try to get the enum
        try {
            GsnCoreElements.StringRelation.valueOf(property.getRelation());
        } catch (Exception e) {
            flag = false; // the valueOf threw an exception because the property is not in the list
            // of string relations
        }

        return flag;
    }

    /**
     * Function that returns a property string statement by deciding whether quotes are required for
     * the value or not
     *
     * @param property
     * @return
     */
    public static String createPropertyString(GsnSadl.RelationPair property) {

        String propString = "";

        if (propertyIsString(property)) {
            propString = "\t with " + property.getRelation() + " \"" + property.getValue() + "\"";
        } else {
            propString = "\t with " + property.getRelation() + " " + property.getValue() + "";
        }

        return propString;
    }

    /**
     * Creates a pretty-print string declare block from a GsnSadlSentence.Declareblock and returns
     * it
     *
     * @param sentenceObj
     * @return
     */
    public static String createElementDeclareBlockString(GsnSadl.ElementDeclareBlock sentenceObj) {

        // Element declaration
        String sentenceStr =
                sentenceObj.getSubjectId() + " is a " + sentenceObj.getSubjectClass() + "\n";

        // Add element properties
        for (int i = 0; i < sentenceObj.getSubjectProperties().size(); i++) {
            GsnSadl.RelationPair property = sentenceObj.getSubjectProperties().get(i);
            sentenceStr = sentenceStr + createPropertyString(property);

            // selectively add newline if not last property
            if (i != (sentenceObj.getSubjectProperties().size() - 1)) {
                sentenceStr = sentenceStr + "\n";
            }
        }
        sentenceStr =
                sentenceStr
                        + ".\n"; // need to fix this to appear without linebreak before the period

        return sentenceStr;
    }

    /**
     * Creates a pretty-print string context block from a GsnSadlSentence.Contextblock and returns
     * it
     *
     * @param sentenceObj
     * @return
     */
    public static String createInContextOfString(GsnSadl.InContextOfSentence sentenceObj) {

        // Context statement
        String sentenceStr =
                sentenceObj.getSubjectId()
                        + " "
                        + GsnCoreElements.InternalRelation.inContextOf
                        + " "
                        + sentenceObj.getSubjectContext()
                        + ".\n";

        return sentenceStr;
    }

    /**
     * Creates a pretty-print string supproted block from a GsnSadlSentence.Contextblock and returns
     * it
     *
     * @param sentenceObj
     * @return
     */
    public static String createSupportedByString(GsnSadl.SupportedBySentence sentenceObj) {

        // Support statement
        String sentenceStr =
                sentenceObj.getSubjectId()
                        + " "
                        + GsnCoreElements.InternalRelation.supportedBy
                        + " "
                        + sentenceObj.getSubjectSupport()
                        + ".\n";

        return sentenceStr;
    }

    /**
     * Creates a pretty-print string gsn rooGoal block from a GsnSadlSentence.Contextblock and
     * returns it
     *
     * @param sentenceObj
     * @return
     */
    public static String createRootSentenceString(GsnSadl.RootSentence sentenceObj) {

        // RootGoal statement
        String sentenceStr =
                sentenceObj.getGsnId()
                        + " is a GSN with "
                        + GsnCoreElements.OtherRelation.rootGoal
                        + " "
                        + sentenceObj.getGsnRootGoal()
                        + ".\n";

        return sentenceStr;
    }

    /**
     * Creates a string for the uri and import blocks and returns it
     *
     * @param sentenceObj
     * @return
     */
    public static String createUriAndImportsBlockString(GsnSadl.UriAndImportBlock sentenceObj) {

        // Uri and alias statement
        String sentenceStr =
                "uri \"" + sentenceObj.getUri() + "\" alias " + sentenceObj.getAlias() + ".\n";

        // Add the imports
        for (int i = 0; i < sentenceObj.getImports().size(); i++) {
            sentenceStr = sentenceStr + "import \"" + sentenceObj.getImports().get(i) + "\".\n";
        }

        return sentenceStr;
    }
}
