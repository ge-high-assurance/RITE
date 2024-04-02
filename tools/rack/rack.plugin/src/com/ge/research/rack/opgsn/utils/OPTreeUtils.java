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
package com.ge.research.rack.opgsn.utils;

import com.ge.research.rack.opgsn.structures.OPTree;
import java.util.List;

public class OPTreeUtils {

    /**
     * Returns index of an argument if exists
     *
     * @param arguments
     * @param id
     * @return
     */
    public static Integer getArgumentPosition(List<OPTree.Argument> arguments, String id) {

        for (int i = 0; i < arguments.size(); i++) {
            OPTree.Argument pkg = arguments.get(i);
            if (pkg.getId().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Returns index of a premise if exists
     *
     * @param premises
     * @param id
     * @return
     */
    public static Integer getPremisePosition(List<OPTree.Premise> premises, String id) {

        for (int i = 0; i < premises.size(); i++) {
            OPTree.Premise pkg = premises.get(i);
            if (pkg.getId().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Returns index of an argument if exists
     *
     * @param evidences
     * @param id
     * @return
     */
    public static Integer getEvidencePosition(List<OPTree.Evidence> evidences, String id) {

        for (int i = 0; i < evidences.size(); i++) {
            OPTree.Evidence pkg = evidences.get(i);
            if (pkg.getId().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return null;
    }
}
