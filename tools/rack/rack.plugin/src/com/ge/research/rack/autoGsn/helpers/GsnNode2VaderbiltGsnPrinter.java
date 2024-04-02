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
package com.ge.research.rack.autoGsn.helpers;

import com.ge.research.rack.autoGsn.structures.GsnNode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class GsnNode2VaderbiltGsnPrinter {

    public void createVanderbiltGsn(GsnNode GSN, File fout) throws IOException {

        // Create fileoutputstreams for writing to the file
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        // Start the graph
        bw.write("GOALS gauss");
        bw.write("{");
        bw.newLine();
        bw.newLine();
        bw.newLine();

        // write GSN
        writeGsn(bw, GSN);

        // end the graph
        bw.write("}");
        // close file
        bw.close();
        fos.close();

        System.out.println("Created file " + fout.getAbsolutePath());
    }

    public void writeGsn(BufferedWriter bw, GsnNode root) throws IOException {

        // write node statements
        bw.write(
                root.getNodeType().toString().toLowerCase()
                        + " "
                        + root.getNodeId().toLowerCase().replace("-", "")
                        + "\n");
        bw.write("{ //Start of " + root.getNodeId().toLowerCase().replace("-", ""));
        bw.newLine();
        bw.write("uuid:\"" + UUID.randomUUID().toString() + "\";\n");
        bw.write("summary:'''" + root.getDescription() + "''';\n");
        // send to children
        if (root.getSupportedBy() != null) {
            for (GsnNode support : root.getSupportedBy()) {
                writeGsn(bw, support);
            }
            // complete node with final brace
            bw.write("} //End of " + root.getNodeId().toLowerCase().replace("-", ""));
        }

        bw.newLine();
    }
}
