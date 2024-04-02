/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2024, General Electric Company and Galois, Inc.
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
package com.ge.research.rack.opgsn.structures;

import java.util.ArrayList;
import java.util.List;

public class OPTree {

    public class Evidence {
        private String id = "";
        private String confirmedBy = "";
        private Boolean isSupportive = false;
        private List<String> detail = new ArrayList<String>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getConfirmedBy() {
            return confirmedBy;
        }

        public void setConfirmedBy(String confirmedBy) {
            this.confirmedBy = confirmedBy;
        }

        public Boolean getIsSupportive() {
            return isSupportive;
        }

        public void setIsSupportive(Boolean isSupportive) {
            this.isSupportive = isSupportive;
        }

        public List<String> getDetail() {
            return detail;
        }

        public void setDetail(List<String> detail) {
            this.detail = detail;
        }
    }

    public class Premise {
        private String id = "";
        private String statement = "";
        private Boolean holds = false;
        private List<Premise> subPremises = new ArrayList<Premise>();
        private List<Evidence> evidence = new ArrayList<Evidence>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStatement() {
            return statement;
        }

        public void setStatement(String statement) {
            this.statement = statement;
        }

        public Boolean getHolds() {
            return holds;
        }

        public void setHolds(Boolean holds) {
            this.holds = holds;
        }

        public List<Premise> getSubPremises() {
            return subPremises;
        }

        public void setSubPremises(List<Premise> subPremises) {
            this.subPremises = subPremises;
        }

        public List<Evidence> getEvidence() {
            return evidence;
        }

        public void setEvidence(List<Evidence> evidence) {
            this.evidence = evidence;
        }
    }

    public class Argument {
        private String id = "";
        private String op = "";
        private String concerns = "";
        private String conclusion = "";
        private String justification = "";
        private List<Premise> subPremises = new ArrayList<Premise>();
        private Boolean holds = false;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOp() {
            return op;
        }

        public void setOp(String op) {
            this.op = op;
        }

        public String getConcerns() {
            return concerns;
        }

        public void setConcerns(String concerns) {
            this.concerns = concerns;
        }

        public String getConclusion() {
            return conclusion;
        }

        public void setConclusion(String conclusion) {
            this.conclusion = conclusion;
        }

        public String getJustification() {
            return justification;
        }

        public void setJustification(String justification) {
            this.justification = justification;
        }

        public List<Premise> getSubPremises() {
            return subPremises;
        }

        public void setSubPremises(List<Premise> subPremises) {
            this.subPremises = subPremises;
        }

        public Boolean getHolds() {
            return holds;
        }

        public void setHolds(Boolean holds) {
            this.holds = holds;
        }
    }

    private String id = "";
    private String statement = "";
    private List<Argument> arguments = new ArrayList<Argument>();
    private Boolean opsSatisfied = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(List<Argument> arguments) {
        this.arguments = arguments;
    }

    public Boolean getOpsSatisfied() {
        return opsSatisfied;
    }

    public void setOpsSatisfied(Boolean opsSatisfied) {
        this.opsSatisfied = opsSatisfied;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }
}
