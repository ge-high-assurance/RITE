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

import com.ge.research.rack.arp4754.logic.QueryItem.QueryStringType;
import com.ge.research.rack.arp4754.structures.Configuration;

import java.util.LinkedList;
import java.util.List;

public class QueryList {

    private List<QueryItem> queries = new LinkedList<QueryItem>();

    public void addConfigLookup(String str) {
        QueryItem item = new QueryItem(str);
        queries.add(item);
    }

    public void addConfigLookupWithIO(String str) {
        QueryItem item = new QueryItem(str, QueryStringType.CONFIG_LOOKUP_WITH_IO);
        queries.add(item);
    }

    public void addConfigLookup(String str1, String str2) {
        QueryItem item = new QueryItem(str1, str2);
        queries.add(item);
    }

    public void addDirect(String str1) {
        QueryItem item = new QueryItem(str1, QueryStringType.DIRECT);
        queries.add(item);
    }

    public List<String> getAllQueries(Configuration config) {
        List<String> strs = new LinkedList<String>();
        for (QueryItem item : queries) {
            strs.add(item.toString(config));
        }

        return strs;
    }

    public List<String> getBaseConfiguration() {
        List<String> strs = new LinkedList<String>();
        for (QueryItem item : queries) {
            if (!item.isDirect()) {
                String str = item.toStringDirect();
                if (!strs.contains(str)) {
                    strs.add(str);
                }

                if (item.hasPairLookup()) {
                    str = item.toStringDirectPair();
                    if (!strs.contains(str)) {
                        strs.add(str);
                    }
                }
            }
        }

        return strs;
    }
}
