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
package com.ge.research.rack.autoGsn.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Saswata Paul
 *     <p>Contains functions to help extract data from JSONObject with ontology info returned by
 *     com.ge.research.semtk.edc.client.OntologyInfoClient.execGetOntologyInfoJson()
 */
public class OntologyJsonObjUtils {

    /**
     * Returns the uri prefix from an ontologyInfo JSONObject's "prefixes" field given the numerical
     * key
     *
     * @param ontInfo
     * @param key
     * @return
     */
    public static String getUriPrefix(JSONObject ontInfo, String key) {
        JSONObject uriInfo = (JSONObject) ontInfo.get("prefixes");
        return uriInfo.get(key).toString();
    }

    /**
     * Returns the full URI of the ranges of a given property as a list Also returns the URI of the
     * property as first element of the list
     *
     * @param ontInfo
     * @param classId (Id is enough. URI not needed because classIds are unique in an ontology)
     * @param propId (Id is enough. URI not needed because propertyIds are unique for a class)
     * @return
     */
    public static List<String> getPropertyRangeURIs(
            JSONObject ontInfo, String classId, String propId) {
        // Create a list of strings to store values
        List rangeUris = new ArrayList<String>();

        System.out.println("Finding range of " + classId + " | " + propId);

        JSONArray classPropertyRangeList = (JSONArray) ontInfo.get("classPropertyRangeList");
        Iterator classPropertyRangeListIter = classPropertyRangeList.iterator();

        // Flag to check if propertyUri has been added as first element
        Boolean addedPropUriFlag = false;

        while (classPropertyRangeListIter.hasNext()) {
            JSONArray classPropertyRange = (JSONArray) classPropertyRangeListIter.next();

            String rowClassInfo = classPropertyRange.get(0).toString();
            String rowPropInfo = classPropertyRange.get(1).toString();
            String rowRangeInfo = classPropertyRange.get(2).toString();

            String rowClassId = rowClassInfo.split("\\:")[1];
            String rowClassUri =
                    getUriPrefix(ontInfo, rowClassInfo.split("\\:")[0]) + "#" + rowClassId;
            String rowPropId = rowPropInfo.split("\\:")[1];
            String rowPropUri =
                    getUriPrefix(ontInfo, rowPropInfo.split("\\:")[0]) + "#" + rowPropId;
            String rowRangeId = rowRangeInfo.split("\\:")[1];
            String rowRangeUri =
                    getUriPrefix(ontInfo, rowRangeInfo.split("\\:")[0]) + "#" + rowRangeId;

            /**
             * The property info may be present as [ClassId, prop, range1]
             *
             * <p>and if nothing of that type is found it means that the property is an inherited
             * property from a supertype
             *
             * <p>[Super_ClassA, prop, range1]
             */
            if ((classId.equalsIgnoreCase(rowClassId) && (propId.equalsIgnoreCase(rowPropId)))) {
                if (!addedPropUriFlag) {
                    // Add the property URI as first element of the list
                    rangeUris.add(rowPropUri);
                    addedPropUriFlag = true;
                }
                // add the new range
                rangeUris.add(rowRangeUri);
            }
        }

        if (rangeUris.size()
                < 1) { // nothing was found, then it must be an inherited property from superclass
            // call itself with superclass
            rangeUris = getPropertyRangeURIs(ontInfo, getSuperClassId(ontInfo, classId), propId);
        }

        if (rangeUris.size() > 0) {

            //             print for testing
            for (int i = 0; i < rangeUris.size(); i++) {
                System.out.println(rangeUris.get(i));
            }

            return rangeUris;
        } else {
            return null;
        }
    }

    /**
     * Given a class, returns its uri
     *
     * @param ontInfo
     * @param classId
     * @return
     */
    public static String getClassURI(JSONObject ontInfo, String classId) {

        System.out.println("Finding URI of class " + classId);

        JSONArray subClassSuperClassList = (JSONArray) ontInfo.get("subClassSuperClassList");
        Iterator subClassSuperClassListIter = subClassSuperClassList.iterator();

        while (subClassSuperClassListIter.hasNext()) {
            JSONArray subSuperPair = (JSONArray) subClassSuperClassListIter.next();

            String rowSubInfo = subSuperPair.get(0).toString();
            String rowSuperInfo = subSuperPair.get(1).toString();

            // System.out.println(rowSubInfo + " " + rowSuperInfo);

            String rowSubClassId = rowSubInfo.split("\\:")[1];
            String rowSuperClassId = rowSuperInfo.split("\\:")[1];

            if (rowSubClassId.equals(classId)) {
                String rowSubClassUri =
                        getUriPrefix(ontInfo, rowSubInfo.split("\\:")[0]) + "#" + rowSubClassId;
                System.out.println(rowSubClassUri);
                return rowSubClassUri;
            }

            if (rowSuperClassId.equals(classId)) {
                String rowSuperClassUri =
                        getUriPrefix(ontInfo, rowSuperInfo.split("\\:")[0]) + "#" + rowSuperClassId;
                System.out.println(rowSuperClassUri);
                return rowSuperClassUri;
            }
        }

        System.out.println("ERROR: URI for " + classId + " was not found!!");
        return null;
    }

    public static String getSuperClassId(JSONObject ontInfo, String classId) {

        System.out.println("Finding Superclass of class " + classId);

        JSONArray subClassSuperClassList = (JSONArray) ontInfo.get("subClassSuperClassList");
        Iterator subClassSuperClassListIter = subClassSuperClassList.iterator();

        while (subClassSuperClassListIter.hasNext()) {
            JSONArray subSuperPair = (JSONArray) subClassSuperClassListIter.next();

            String rowSubInfo = subSuperPair.get(0).toString();
            String rowSuperInfo = subSuperPair.get(1).toString();

            String rowSubClassId = rowSubInfo.split("\\:")[1];
            String rowSuperClassId = rowSuperInfo.split("\\:")[1];

            if (rowSubClassId.equals(classId)) {
                System.out.println(rowSuperClassId);
                return rowSuperClassId;
            }
        }

        System.out.println("ERROR: Superclass for " + classId + " was not found!!");
        return null;
    }

    /***
     * Given a tuple <ClassA, prop, ClassB>
     * returns true if  prop(ClassA) = ClassB
     * and false if prop(ClassB) = ClassA
     *
     * @param ontInfo
     * @param classAId
     * @param propId
     * @param classBId
     * @return
     */
    public static Boolean isForwardProperty(
            JSONObject ontInfo, String classAId, String propId, String classBId) {

        JSONArray classPropertyRangeList = (JSONArray) ontInfo.get("classPropertyRangeList");
        Iterator classPropertyRangeListIter = classPropertyRangeList.iterator();

        /**
         * Note: Since class can be from overlays, the JSON ontology information can have the Class
         * A Class B via property information in the following ways:
         *
         * <p>1. [A, prop, B ] or [B, prop, A] may be there in classPropertyRangeList, so check this
         * first
         *
         * <p>2. [Super_A, prop, B ] or [B, prop, Super_A]
         *
         * <p>3. [Super_A, prop, Super_B] or [Super_B, prop, Super_A]
         *
         * <p>4. [A, prop, Super_B] or [Super_B, prop, A]
         *
         * <p>5. [THING/ENTITY, prop, B] or [B, prop, THING/ENTITY]
         *
         * <p>6. [THING/ENTITY, prop, Super_B] or [Super_B, prop, THING/ENTITY]
         *
         * <p>7. [THING/ENTITY, prop, A] or [A, prop, THING/ENTITY]
         *
         * <p>8. [THING/ENTITY, prop, Super_A] or [Super_A, prop, THING/ENTITY]
         *
         * <p>Limitations: Only works for one layer of class nesting (three if the first layer is
         * THING/ENTITY)
         */
        while (classPropertyRangeListIter.hasNext()) {
            JSONArray classPropertyRange = (JSONArray) classPropertyRangeListIter.next();

            String rowClassId = classPropertyRange.get(0).toString().split("\\:")[1];
            String rowPropId = classPropertyRange.get(1).toString().split("\\:")[1];
            String rowRangeId = classPropertyRange.get(2).toString().split("\\:")[1];

            // [A, prop, B ]
            if ((rowClassId.equals(classAId))
                    && (rowPropId.equals(propId))
                    && (rowRangeId.equals(classBId))) {
                return true;
            }

            // [B, prop, A]
            if ((rowClassId.equals(classBId))
                    && (rowPropId.equals(propId))
                    && (rowRangeId.equals(classAId))) {
                return false;
            }

            // [Super_A, prop, B ]
            if ((rowClassId.equals(getSuperClassId(ontInfo, classAId)))
                    && (rowPropId.equals(propId))
                    && (rowRangeId.equals(classBId))) {
                return true;
            }

            // [B, prop, Super_A]
            if ((rowClassId.equals(classBId))
                    && (rowPropId.equals(propId))
                    && (rowRangeId.equals(getSuperClassId(ontInfo, classAId)))) {
                return false;
            }

            // [Super_A, prop, Super_B ]
            if ((rowClassId.equals(getSuperClassId(ontInfo, classAId)))
                    && (rowPropId.equals(propId))
                    && (rowRangeId.equals(getSuperClassId(ontInfo, classBId)))) {
                return true;
            }

            // [Super_B, prop, Super_A]
            if ((rowClassId.equals(getSuperClassId(ontInfo, classBId)))
                    && (rowPropId.equals(propId))
                    && (rowRangeId.equals(getSuperClassId(ontInfo, classAId)))) {
                return false;
            }

            // [A, prop, Super_B ]
            if ((rowClassId.equals(classAId))
                    && (rowPropId.equals(propId))
                    && (rowRangeId.equals(getSuperClassId(ontInfo, classBId)))) {
                return true;
            }

            // [Super_B, prop, A]
            if ((rowClassId.equals(getSuperClassId(ontInfo, classBId)))
                    && (rowPropId.equals(propId))
                    && (rowRangeId.equals(classAId))) {
                return false;
            }

            // [THING/ENTITY, prop, B ]
            if (((rowClassId.equals("THING") || rowClassId.equals("ENTITY")))
                    && (rowPropId.equals(propId))
                    && (rowRangeId.equals(classBId))) {
                return true;
            }

            // [B, prop, THING/ENTITY]
            if ((rowClassId.equals(classBId))
                    && (rowPropId.equals(propId))
                    && ((rowRangeId.equals("THING") || rowRangeId.equals("ENTITY")))) {
                return false;
            }

            // [THING/ENTITY, prop, Super_B ]
            if (((rowClassId.equals("THING") || rowClassId.equals("ENTITY")))
                    && (rowPropId.equals(propId))
                    && (rowRangeId.equals(getSuperClassId(ontInfo, classBId)))) {
                return true;
            }

            // [Super_B, prop, THING/ENTITY]
            if ((rowClassId.equals(getSuperClassId(ontInfo, classBId)))
                    && (rowPropId.equals(propId))
                    && ((rowRangeId.equals("THING") || rowRangeId.equals("ENTITY")))) {
                return false;
            }

            // [THING/ENTITY, prop, A ]
            if (((rowClassId.equals("THING") || rowClassId.equals("ENTITY")))
                    && (rowPropId.equals(propId))
                    && (rowRangeId.equals(classAId))) {
                return true;
            }

            // [A, prop, THING/ENTITY]
            if ((rowClassId.equals(classAId))
                    && (rowPropId.equals(propId))
                    && ((rowRangeId.equals("THING") || rowRangeId.equals("ENTITY")))) {
                return false;
            }

            // [THING/ENTITY, prop, Super_A ]
            if (((rowClassId.equals("THING") || rowClassId.equals("ENTITY")))
                    && (rowPropId.equals(propId))
                    && (rowRangeId.equals(getSuperClassId(ontInfo, classAId)))) {
                return true;
            }

            // [Super_A, prop, THING/ENTITY]
            if ((rowClassId.equals(getSuperClassId(ontInfo, classAId)))
                    && (rowPropId.equals(propId))
                    && ((rowRangeId.equals("THING") || rowRangeId.equals("ENTITY")))) {
                return false;
            }
        }

        // if no match is found (which should not happen since the classes will be in the ontology)
        System.out.println(
                "ERROR: Property direction for "
                        + classAId
                        + " <"
                        + propId
                        + "> "
                        + classBId
                        + " was not found!!");
        return null;
    }
}
