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
package com.ge.research.rite.autoGsn.structures;

public class GsnViewsStore {

    private String gsnUri;
    private String gsnAlias;
    private String coreGsnPath;
    private String patternGsnPath;
    private String projectOverlayPath;
    private String outDirPath;
    private String tempDirPath;

    public synchronized String getCoreGsnPath() {
        return coreGsnPath;
    }

    public synchronized void setCoreGsnPath(String coreGsnPath) {
        this.coreGsnPath = coreGsnPath;
    }

    public synchronized String getPatternGsnPath() {
        return patternGsnPath;
    }

    public synchronized void setPatternGsnPath(String patternGsnPath) {
        this.patternGsnPath = patternGsnPath;
    }

    public synchronized String getProjectOverlayPath() {
        return projectOverlayPath;
    }

    public synchronized void setProjectOverlayPath(String projOverlayPath) {
        this.projectOverlayPath = projOverlayPath;
    }

    public String getOutDirPath() {
        return outDirPath;
    }

    public void setOutDirPath(String outDirPath) {
        this.outDirPath = outDirPath;
    }

    public String getTempDirPath() {
        return tempDirPath;
    }

    public void setTempDirPath(String tempDirPath) {
        this.tempDirPath = tempDirPath;
    }

    public String getGsnUri() {
        return gsnUri;
    }

    public void setGsnUri(String gsnUri) {
        this.gsnUri = gsnUri;
    }

    public String getGsnAlias() {
        return gsnAlias;
    }

    public void setGsnAlias(String gsnAlias) {
        this.gsnAlias = gsnAlias;
    }
}
