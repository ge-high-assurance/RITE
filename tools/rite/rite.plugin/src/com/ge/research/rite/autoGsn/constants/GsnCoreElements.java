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
package com.ge.research.rite.autoGsn.constants;

/**
 * @author Saswata Paul
 *     <p>Contains necessary information about elements of the GSN core hardcoded as enums NOTE:
 *     These things are harcoded because the GSN core is expected to be constant So this class needs
 *     to be kept in Sync with changes made to the GSN Core
 */
public class GsnCoreElements {

    // TO DO: Convert all enums to capital case and create necessary functions to call their string
    // values when needed
    // Do we need to do this?

    /**
     * NOTE: Against convention, the enums are not capital because we want to easily extract the
     * string equivalents as needed
     */

    /** All Gsn Classes */
    public enum Class {
        Goal,
        Strategy,
        Context,
        Assumption,
        Justification,
        Solution
    }

    /**
     * All relations that take string type values Some elements may take values that are strings,
     * while other may not This is necessary to know while creating the SADL statements for a GSN
     * instance Because String values require quotes around them E.g.: identifier "G-1"
     */
    public enum StringRelation {
        // From RACK PROV-S THING
        identifier,
        title,
        description,
        // From GSN core

    }

    /** The contextOf and supportedBy relations */
    public enum InternalRelation {
        supportedBy,
        inContextOf
    }

    /** Other relations needed to create a GSN instance */
    public enum OtherRelation {
        rootGoal
    }

    /** Some constants required to create the GSN instance */
    public enum OtherConstants {
        DEPALIAS("gsnDep"), // The alias for the dpendencies sadl file
        DEPURI("http://sadl.org/instanceDependencies.sadl"), // The uri for the dpendencies sadl
        WARNING(
                "//*********************************************************************************************************************************** \n"
                        + "//-- NOTE: This auto-generated dependency file contains duplicate SADl instantiations of the instance data connected to the GSN \n"
                        + "//--       This file ensures that the GSN sadl instance compiles in a SADL environment without error \n"
                        + "//-- WARNING: DO NOT INGEST THIS FILE INTO RACK !!! \n"
                        + "//*********************************************************************************************************************************** \n\n\n"); // A warning comment stating the purpose of the file
        // file

        // To set and get the strings of the enums
        private String conString;

        OtherConstants(String str) {
            this.conString = str;
        }

        public String getValue() {
            return this.conString;
        }
    }
}
