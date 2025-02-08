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
package com.ge.research.rite.autoGsn.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 *     <p>Data structures to store components for all types of gsn SADL sentences
 */
public class GsnSadl {

    // TO DO: Use enums in the class variables wherever possible?
    //        Any advantage to this?

    /**
     * @author Saswata Paul A data structure to store a property value SADL pair E.g.: identifier
     *     "S-G-HLR-2"
     */
    public class RelationPair {

        // The relation/operator
        private String relation;
        // The value
        private String value;

        public RelationPair(String rel, String val) {
            this.relation = rel;
            this.value = val;
        }

        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * For the SADL file ID and imports uri "http://sadl.org/sample_gsn_instance.sadl" alias sgi.
     *
     * <p>import "http://sadl.org/GSN-core.sadl". import "http://sadl.org/instanceData.sadl". import
     * "http://sadl.org/GSN-Pattern-GE.sadl".
     */
    public class UriAndImportBlock {

        // The class uri and alias
        private String uri;
        private String alias;
        // The imports
        private List<String> imports = new ArrayList<String>();

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public List<String> getImports() {
            return imports;
        }

        public void setImports(List<String> imports) {
            this.imports = imports;
        }
    }

    /**
     * For declaration sentences * E.g: S-G-HLR-2 is a Strategy with identifier "S-G-HLR-2" with
     * description "Argument: Requirement decomposed to sub-requirements" with developed true.
     */
    public class ElementDeclareBlock {
        // The subject of the sentence
        private String subjectId;

        // The class of the sentence
        private String subjectClass;

        // The properties of the subject
        private List<RelationPair> subjectProperties = new ArrayList<RelationPair>();

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public String getSubjectClass() {
            return subjectClass;
        }

        public void setSubjectClass(String subjectClass) {
            this.subjectClass = subjectClass;
        }

        public List<RelationPair> getSubjectProperties() {
            return subjectProperties;
        }

        public void setSubjectProperties(List<RelationPair> subjectProperties) {
            this.subjectProperties = subjectProperties;
        }
    }

    /** For context sentences E.g.: G-H-1 inContextOf C-H-1. */
    public class InContextOfSentence {
        // The subject of the sentence
        private String subjectId;

        // The class of the sentence
        private String subjectContext;

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public String getSubjectContext() {
            return subjectContext;
        }

        public void setSubjectContext(String subjectContext) {
            this.subjectContext = subjectContext;
        }
    }

    /** For supported by sentences E.g.: G-H-1 supportedBy S-G-H-1. */
    public class SupportedBySentence {
        // The subject of the sentence
        private String subjectId;

        // The class of the sentence
        private String subjectSupport;

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public String getSubjectSupport() {
            return subjectSupport;
        }

        public void setSubjectSupport(String subjectSupport) {
            this.subjectSupport = subjectSupport;
        }
    }

    /** E.g: G is a GSN with rootGoal G-H-1. */
    public class RootSentence {
        // The GSN ID
        private String gsnId;

        // The root goal
        private String gsnRootGoal;

        public String getGsnId() {
            return gsnId;
        }

        public void setGsnId(String gsnId) {
            this.gsnId = gsnId;
        }

        public String getGsnRootGoal() {
            return gsnRootGoal;
        }

        public void setGsnRootGoal(String gsnRootGoal) {
            this.gsnRootGoal = gsnRootGoal;
        }
    }
}
