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
package com.ge.research.rack.arp4754.structures;

import com.ge.research.rack.analysis.structures.PlanGraph;

public class Graph extends PlanGraph {

    private GraphData derItemReqGraphData =
            new GraphData(""); // TODO: might need to create a separate graph element for each
    // objective
    private GraphData derSysReqData = new GraphData("");
    private GraphData interfaceGraphData = new GraphData("");
    private GraphData itemGraphData = new GraphData("");
    private GraphData itemReqGraphData = new GraphData("");
    private GraphData sysReqGraphData = new GraphData("");
    private GraphData systemGraphData = new GraphData("");

    /**
     * @return the derItemReqGraphData
     */
    public GraphData getDerItemReqGraphData() {
        return derItemReqGraphData;
    }

    /**
     * @param derItemReqGraphData the derItemReqGraphData to set
     */
    public void setDerItemReqGraphData(GraphData derItemReqGraphData) {
        this.derItemReqGraphData = derItemReqGraphData;
    }

    /**
     * @return the derSysReqData
     */
    public GraphData getDerSysReqData() {
        return derSysReqData;
    }

    /**
     * @param derSysReqData the derSysReqData to set
     */
    public void setDerSysReqData(GraphData derSysReqData) {
        this.derSysReqData = derSysReqData;
    }

    /**
     * @return the interfaceGraphData
     */
    public GraphData getInterfaceGraphData() {
        return interfaceGraphData;
    }

    /**
     * @param interfaceGraphData the interfaceGraphData to set
     */
    public void setInterfaceGraphData(GraphData interfaceGraphData) {
        this.interfaceGraphData = interfaceGraphData;
    }

    /**
     * @return the itemGraphData
     */
    public GraphData getItemGraphData() {
        return itemGraphData;
    }

    /**
     * @param itemGraphData the itemGraphData to set
     */
    public void setItemGraphData(GraphData itemGraphData) {
        this.itemGraphData = itemGraphData;
    }

    /**
     * @return the itemReqGraphData
     */
    public GraphData getItemReqGraphData() {
        return itemReqGraphData;
    }

    /**
     * @param itemReqGraphData the itemReqGraphData to set
     */
    public void setItemReqGraphData(GraphData itemReqGraphData) {
        this.itemReqGraphData = itemReqGraphData;
    }

    /**
     * @return the sysReqGraphData
     */
    public GraphData getSysReqGraphData() {
        return sysReqGraphData;
    }

    /**
     * @param sysReqGraphData the sysReqGraphData to set
     */
    public void setSysReqGraphData(GraphData sysReqGraphData) {
        this.sysReqGraphData = sysReqGraphData;
    }

    /**
     * @return the systemGraphData
     */
    public GraphData getSystemGraphData() {
        return systemGraphData;
    }

    /**
     * @param systemGraphData the systemGraphData to set
     */
    public void setSystemGraphData(GraphData systemGraphData) {
        this.systemGraphData = systemGraphData;
    }
}
