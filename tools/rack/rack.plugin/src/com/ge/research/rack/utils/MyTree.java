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

import com.ge.research.semtk.ontologyTools.OntologyClass;
import com.ge.research.semtk.ontologyTools.OntologyInfo;
import com.ge.research.semtk.ontologyTools.OntologyProperty;
import java.util.ArrayList;
import java.util.HashMap;

public class MyTree {
    public String name = "";
    public String type = "";
    public String uri = "";
    public String comment = "";
    public HashMap<String, String> properties = new HashMap<>();
    public static HashMap<String, String> propertyAnnotations = new HashMap<>();
    public ArrayList<MyTree> children = new ArrayList<MyTree>();

    public MyTree(String name, String type, String comment) {
        this.name = name;
        this.type = type;
        this.comment = comment;
        if (type.equals("toplevel")) {
            this.uri = "http://arcos/" + name;
        }
    }

    public void setProperties(OntologyInfo oInfo, OntologyClass oClass) throws Exception {
        ArrayList<OntologyProperty> properties = oInfo.getInheritedProperties(oClass);
        for (OntologyProperty property : properties) {

            String domain = property.getName().getLocalName();
            String range = property.getRange(oClass, oInfo).getDisplayString(true);
            this.properties.put(domain, range);
            String comment = property.getAnnotationCommentsString();
            if (!comment.equals("")) {
                propertyAnnotations.put(domain, property.getAnnotationCommentsString());
            }
        }
    }
}
