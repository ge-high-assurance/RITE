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

import java.util.*;

public class TreeNode {
    private GSN gsnElement;
    private ArrayList<TreeNode> supportedBy = new ArrayList<>();
    private ArrayList<TreeNode> inContextOf = new ArrayList<>();
    private boolean isChild = false;
    private String id;
    private String comment = "";
    private String description = "";

    public void addSupportedBy(TreeNode supportedByNode) {
        supportedBy.add(supportedByNode);
    }

    public void addInContextOf(TreeNode inContextOfNode) {
        inContextOf.add(inContextOfNode);
    }

    public ArrayList<TreeNode> getSupports() {
        return supportedBy;
    }

    public ArrayList<TreeNode> getContexts() {
        return inContextOf;
    }

    public TreeNode(String id) {
        this.id = id;
        this.isChild = false;
        this.comment = "";
        this.description = "";
        this.gsnElement = null;
    }

    public boolean isTopLevelNode() {
        return !isChild;
    }

    public void setAsChild() {
        isChild = true;
    }

    public String getId() {
        return this.id;
    }

    public void setGsnType(GSN element) {
        this.gsnElement = element;
    }

    public GSN getGsnType() {
        return this.gsnElement;
    }

    public String getLabel() {
        if (comment != null && comment != "") {
            return comment;
        }
        if (description != null && description != "") {
            return description;
        }
        return "";
    }

    public void setComment(String text) {
        this.comment = text;
    }

    public void setDescription(String text) {
        this.description = text;
    }

    @Override
    public int hashCode() {
        int result = (int) (id.hashCode() ^ (id.hashCode() >>> 32));
        result = 31 * result + gsnElement.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        TreeNode node = (TreeNode) o;
        if (!this.id.equals(node.getId())) {
            return false;
        }
        if (!this.gsnElement.equals(node.getGsnType())) {
            return false;
        }
        return true;
    }
}
