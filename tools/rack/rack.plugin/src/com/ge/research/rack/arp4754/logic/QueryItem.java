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
package com.ge.research.rack.arp4754.logic;

import com.ge.research.rack.arp4754.structures.Configuration;

public class QueryItem {

    public enum QueryStringType {
        DIRECT,
        CONFIG_LOOKUP,
        CONFIG_LOOKUP_PAIR,
        CONFIG_LOOKUP_WITH_IO
    };

    private String _label;
    private String _label2;
    private QueryStringType _type;

    public QueryItem(String str) {
        _label = str;
        _label2 = "";
        _type = QueryStringType.CONFIG_LOOKUP;
    }

    public QueryItem(String str, QueryStringType t) {
        _label = str;
        _label2 = "";
        _type = t;
    }

    public QueryItem(String str1, String str2) {
        _label = str1;
        _label2 = str2;
        _type = QueryStringType.CONFIG_LOOKUP_PAIR;
    }

    public boolean isDirect() {
        return _type == QueryStringType.DIRECT;
    }

    public String toString(Configuration config) {
        switch (_type) {
            case CONFIG_LOOKUP:
                return config.get(_label);
            case CONFIG_LOOKUP_PAIR:
                return config.get(_label, _label2);
            case CONFIG_LOOKUP_WITH_IO:
                return config.getWithIO(_label);
            default:
                break;
        }
        return _label;
    }

    public String toStringDirect() {
        return _label;
    }

    public boolean hasPairLookup() {
        return !_label2.isEmpty();
    }

    public String toStringDirectPair() {
        return _label2;
    }
}
