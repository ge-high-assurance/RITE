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

import com.ge.research.semtk.edc.client.OntologyInfoClient;
import com.ge.research.semtk.edc.client.OntologyInfoClientConfig;
import com.ge.research.semtk.ontologyTools.OntologyClass;
import com.ge.research.semtk.ontologyTools.OntologyInfo;
import com.ge.research.semtk.sparqlX.SparqlConnection;

import java.util.*;

public class OntologyUtil {

    public static OntologyInfo oInfo = null;
    public static ArrayList<String> classNames = new ArrayList<String>();
    public static MyTree root = null;

    public static String getLocalClassName(String className) {
        if (oInfo.getClass(className) == null) {
            return "";
        }
        return oInfo.getClass(className).getNameString(true);
    }

    public static OntologyInfo getoInfo() {
        try {
            SparqlConnection conn = ConnectionUtil.getSparqlConnection();
            OntologyInfoClientConfig config = ConnectionUtil.getOntologyClientConfig();
            OntologyInfoClient ontClient = new OntologyInfoClient(config);
            oInfo = ontClient.getOntologyInfo(conn);
        } catch (Exception e) {
            RackConsole.getConsole()
                    .error(
                            " Unable to connect to ontology service, please check connection parameters and retry");
            return null;
        }
        return oInfo;
    }

    public static void computeTree() throws Exception {
        root = new MyTree("", "root", "");
        ArrayList<String> topLevelNames = getTopLevelNodes();
        for (String name : topLevelNames) {
            MyTree child = new MyTree(name, "toplevel", "");
            root.children.add(child);
            computeChildren(child);
        }
    }

    public static boolean hasChildWithin(String uri, String localClass) {
        for (String classuri : classNames) {
            if (classuri.contains(localClass + "#")) {
                ArrayList<OntologyClass> parents = oInfo.getClassParents(oInfo.getClass(uri));
                for (OntologyClass parent : parents) {
                    if (parent.getName().equals(classuri)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void computeChildren(MyTree tree) throws Exception {
        if (tree.type.equals("toplevel")) {
            // get immediate children
            ArrayList<String> childMatch = new ArrayList<>();
            for (String uri : classNames) {
                if (uri.contains(tree.name + "#") /* && !hasChildWithin(uri, tree.name) */) {
                    childMatch.add(uri);
                }
            }
            for (String uri : childMatch) {
                String annotation = oInfo.getClass(uri).getAnnotationCommentsString();
                String comment = (annotation.equals("")) ? uri : annotation;
                MyTree child =
                        new MyTree(oInfo.getClass(uri).getNameString(true), "inner", comment);
                child.uri = uri;
                child.setProperties(oInfo, oInfo.getClass(uri));
                computeChildren(child);
                tree.children.add(child);
            }
        }

        if (tree.type.equals("inner")) {
            // get children : anyone who has oClass as parent, is a child of oClass
            tree.setProperties(oInfo, oInfo.getClass(tree.uri));
            for (String uri : classNames) {
                OntologyClass oClass = oInfo.getClass(uri);
                ArrayList<OntologyClass> oParents = oInfo.getClassParents(oClass);
                for (OntologyClass oParent : oParents) {
                    if (oParent.getName().equals(tree.uri)) {
                        String annotation = oInfo.getClass(uri).getAnnotationCommentsString();
                        String comment = (annotation.equals("")) ? uri : annotation;
                        MyTree child = new MyTree(oClass.getNameString(true), "inner", comment);
                        child.uri = uri;
                        child.setProperties(oInfo, oParent);
                        tree.children.add(child);
                        computeChildren(child);
                    }
                }
            }
        }
    }

    public static ArrayList<String> getClassNames() {
        ArrayList<String> classList = null;
        try {
            oInfo = OntologyUtil.getoInfo();
            if (oInfo == null) {
                return null;
            }
            classList = oInfo.getClassNames();
            classNames = oInfo.getClassNames();
            classList.sort(
                    new Comparator<String>() {
                        @Override
                        public int compare(String class1, String class2) {
                            String local1 = oInfo.getClass(class1).getNameString(true);
                            String local2 = oInfo.getClass(class2).getNameString(true);
                            return local1.compareTo(local2);
                        }
                    });
            classNames.sort(
                    new Comparator<String>() {
                        @Override
                        public int compare(String class1, String class2) {
                            String local1 = oInfo.getClass(class1).getNameString(true);
                            String local2 = oInfo.getClass(class2).getNameString(true);
                            return local1.compareTo(local2);
                        }
                    });
        } catch (Exception e) {
            RackConsole.getConsole().error("Cannot fetch ontology classes from RACK");
            return null;
        }
        return classList;
    }

    public static ArrayList<String> getTopLevelNodes() throws Exception {
        oInfo = OntologyUtil.getoInfo();
        if (oInfo == null) {
            return new ArrayList<>();
        }
        HashSet<String> set = new HashSet<>();
        ArrayList<String> classes = getClassNames();
        for (String name : classes) {
            set.add(getTopLevelName(name));
        }
        ArrayList<String> topLevel = new ArrayList<String>();

        for (String name : set) {
            topLevel.add(name);
        }
        topLevel.sort(
                new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareTo(s2);
                    }
                });
        return topLevel;
    }

    private static String getTopLevelName(String className) {
        String name = "";
        char[] chars = className.toCharArray();
        for (int j = chars.length - 1; j >= 0; j--) {
            if (chars[j] == '#') {
                j--;
                while (chars[j] != '/' && j >= 0) {
                    name += Character.toString(chars[j]);
                    j--;
                }
                break;
            }
        }
        return reverse(name);
    }

    private static String reverse(String s) {
        char[] chars = s.toCharArray();
        String reverse = "";
        for (int j = chars.length - 1; j >= 0; j--) {
            reverse += chars[j];
        }
        return reverse;
    }
}
