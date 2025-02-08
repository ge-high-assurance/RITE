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
 * Constants in RACK stored as enums
 *
 * @author Saswata Paul
 */
public class RackConstants {

    /**
     * Different Proiject Ids used in RACK
     *
     * @author Saswata Paul
     */
    public enum ProjectIds {
        GE("http://arcos.turnstile/GE"),
        SRI("http://arcos.descert/SRI"),
        SRISS("http://arcos.descert/SRI-SS");

        // To set and get the strings of the enums
        private String projectUri;

        ProjectIds(String uri) {
            this.projectUri = uri;
        }

        /**
         * @return the projectUri
         */
        public String getProjectUri() {
            return projectUri;
        }
    }

    /**
     * Uris used in Arcos
     *
     * @author Saswata Paul
     */
    public enum ArcosUris {
<<<<<<< HEAD
        AGENTS("http://arcos.rack/AGENTS"),
        ANALYSIS("http://arcos.rack/ANALYSIS"),
        BASELINE("http://arcos.rack/BASELINE"),
        CONFIDENCE("http://arcos.rack/CONFIDENCE"),
        DOCUMENT("http://arcos.rack/DOCUMENT"),
        FILE("http://arcos.rack/FILE"),
        HARDWARE("http://arcos.rack/HARDWARE"),
        HAZARD("http://arcos.rack/HAZARD"),
        MODEL("http://arcos.rack/MODEL"),
        PROCESS("http://arcos.rack/PROCESS"),
        PROVS("http://arcos.rack/PROV-S"),
        REQUIREMENTS("http://arcos.rack/REQUIREMENTS"),
        REVIEW("http://arcos.rack/REVIEW"),
        SECURITY("http://arcos.rack/SECURITY"),
        SOFTWARE("http://arcos.rack/SOFTWARE"),
        SYSTEM("http://arcos.rack/SYSTEM"),
        TESTING("http://arcos.rack/TESTING");
=======
        AGENTS("http://arcos.rite/AGENTS"),
        ANALYSIS("http://arcos.rite/ANALYSIS"),
        BASELINE("http://arcos.rite/BASELINE"),
        CONFIDENCE("http://arcos.rite/CONFIDENCE"),
        DOCUMENT("http://arcos.rite/DOCUMENT"),
        FILE("http://arcos.rite/FILE"),
        HARDWARE("http://arcos.rite/HARDWARE"),
        HAZARD("http://arcos.rite/HAZARD"),
        MODEL("http://arcos.rite/MODEL"),
        PROCESS("http://arcos.rite/PROCESS"),
        PROVS("http://arcos.rite/PROV-S"),
        REQUIREMENTS("http://arcos.rite/REQUIREMENTS"),
        REVIEW("http://arcos.rite/REVIEW"),
        SECURITY("http://arcos.rite/SECURITY"),
        SOFTWARE("http://arcos.rite/SOFTWARE"),
        SYSTEM("http://arcos.rite/SYSTEM"),
        TESTING("http://arcos.rite/TESTING");
>>>>>>> 58d31630c3eca4cd02adf1b185a3c9fe3b893eb7

        // To set and get the strings of the enums
        private String arcosUri;

        ArcosUris(String uri) {
            this.arcosUri = uri;
        }

        public String getUri() {
            return this.arcosUri;
        }
    }

    /**
     * @author Saswata Paul
     */
    public enum TestStatus {
<<<<<<< HEAD
        PASSED("http://arcos.rack/TESTING#Passed"),
        FAILED("http://arcos.rack/TESTING#Failed");
=======
        PASSED("http://arcos.rite/TESTING#Passed"),
        FAILED("http://arcos.rite/TESTING#Failed");
>>>>>>> 58d31630c3eca4cd02adf1b185a3c9fe3b893eb7

        // To set and get the strings of the enums
        private String rackUri;

        TestStatus(String uri) {
            this.rackUri = uri;
        }

        public String getRackUri() {
            return this.rackUri;
        }
    }
}
