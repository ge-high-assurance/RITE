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
package com.ge.research.rite.autoGsn.constants;

/**
 * Contains elements/constants from RACK core ontology
 *
 * @author Saswata Paul
 */
public class RackCoreElements {

    /**
     * Classes fro the RACK core ontology
     *
     * @author Saswata Paul
     */
    public enum RackClass {
        SYSTEM("SYSTEM"),
        HAZARD("HAZARD"),
        REQUIREMENT("REQUIREMENT"),
        TEST("TEST"),
        ANALYSIS("ANALYSIS"),
        TEST_RESULT("TEST_RESULT"),
        ENTITY("ENTITY"),
        THING("THING"),
        PROPERTY("PROPERTY"),
        THREAT("THREAT"),
        CONTROL("CONTROL");

        // To set and get the strings of the enums
        private String str;

        RackClass(String uri) {
            this.str = uri;
        }

        public String toString() {
            return this.str;
        }
    }
}
