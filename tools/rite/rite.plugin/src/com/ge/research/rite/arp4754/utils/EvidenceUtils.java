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
package com.ge.research.rite.arp4754.utils;

import com.ge.research.rite.arp4754.structures.Evidence;

import java.util.List;

/**
 * @author Saswata Paul
 */
public class EvidenceUtils {

    /**
     * Given a list of evidence and an id, returns the appropriate evidence object
     *
     * @param list
     * @param id
     * @return
     */
    public static Evidence getEvidenceObjById(List<Evidence> list, String id) {
        for (Evidence evidence : list) {
            if (evidence.getId().equals(id)) {
                return evidence;
            }
        }
        return null;
    }

    /**
     * Given a list of evidence and an id, returns the index of the appropriate evidence object
     *
     * @param list
     * @param id
     * @return
     */
    public static Integer getEvidenceObjIndxById(List<Evidence> list, String id) {
        for (int i = 0; i < list.size(); i++) {
            Evidence evidence = list.get(i);
            if (evidence.getId().equals(id)) {
                return i;
            }
        }
        return null;
    }
}
