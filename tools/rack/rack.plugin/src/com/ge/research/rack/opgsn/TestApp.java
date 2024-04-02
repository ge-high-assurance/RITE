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
package com.ge.research.rack.opgsn;

import com.ge.research.rack.autoGsn.structures.GsnNode;
import com.ge.research.rack.autoGsn.utils.GsnNodeUtils;
import com.ge.research.rack.autoGsn.utils.InterfacingUtils;
import com.ge.research.rack.opgsn.logic.DataProcessor;
import com.ge.research.rack.opgsn.logic.OPTreeToGSN;

public class TestApp {

    public static void main(String[] args) throws Exception {
        String tempDir = "C://Users/212807042/Desktop/test-tmp";

        DataProcessor opTreeDataProcessorObj = new DataProcessor();

        opTreeDataProcessorObj.createOPTreeObject(tempDir);

        OPTreeToGSN opTree2GsnObj = new OPTreeToGSN();

        GsnNode opGSN = opTree2GsnObj.createOPTreeGoalNode(opTreeDataProcessorObj.opTreeObj, 0);

        GsnNodeUtils.printGsnNode(opGSN);

        // -- Create SVG for entire tree
        InterfacingUtils.createGsnSvg(opGSN, tempDir, tempDir, "OPTree");

        // -- create SVGs for different OPs
        GsnNode intentNode = GsnNodeUtils.getSubGsnNode(opGSN, "G-soh-nn-INTENT-ARG");
        InterfacingUtils.createGsnSvg(intentNode, tempDir, tempDir, "intent");

        GsnNode correctnessNode = GsnNodeUtils.getSubGsnNode(opGSN, "G-soh-nn-CORRECTNESS-ARG");
        InterfacingUtils.createGsnSvg(correctnessNode, tempDir, tempDir, "correctness");

        GsnNode innocuityNode = GsnNodeUtils.getSubGsnNode(opGSN, "G-soh-nn-INNOCUITY-ARG");
        InterfacingUtils.createGsnSvg(innocuityNode, tempDir, tempDir, "innocuity");

        // -- Create Vanderbilt interface for entire tree
        //        File gsnVanderbiltFile = new File(tempDir, ("OPTree" + ".gsn"));
        //        GsnNode2VaderbiltGsnPrinter vanderBiltPrinterObj = new
        // GsnNode2VaderbiltGsnPrinter();
        //        vanderBiltPrinterObj.createVanderbiltGsn(opGSN, gsnVanderbiltFile);

        // -- launch traverser for entire tree

    }
}
