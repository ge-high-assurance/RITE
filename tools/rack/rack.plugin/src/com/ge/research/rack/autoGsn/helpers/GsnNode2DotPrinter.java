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
package com.ge.research.rack.autoGsn.helpers;

import com.ge.research.rack.autoGsn.constants.GsnCoreElements;
import com.ge.research.rack.autoGsn.structures.GsnNode;
import com.ge.research.rack.autoGsn.utils.CustomStringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 *     <p>This class is used to convert a GSNNode to Dot for Graphviz
 */
public class GsnNode2DotPrinter {

    // To store all the nodes in the GSN Fragment
    private List<GsnNode> allNodes = new ArrayList<>();

    // to keep track of all edge declarations and remove duplicates
    private List<String> allEdgeStrings = new ArrayList<String>();

    /**
     * Takes a GsnNode and a file address and creates a dot file for the GSN fragment
     *
     * @param GSN
     * @param fout
     * @throws IOException
     */
    public void createDot(GsnNode GSN, File fout) throws IOException {

        // get all nodes in the GSN fragment
        getGsnNodes(GSN);

        // Create fileoutputstreams for writing to the file
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        // Start the graph
        bw.write("digraph ASSURANCE_CASE{");
        bw.newLine();
        bw.write("ratio = 0.5;");
        bw.newLine();
        bw.newLine();
        // declare all nodes
        writeNodes(bw);
        bw.newLine();
        // declare all edges
        writeEdges(bw);
        // end the graph
        bw.write("}");
        // close file
        bw.close();
        fos.close();
    }

    /**
     * Gets all nodes in the Gsn fragment
     *
     * @param node
     */
    private void getGsnNodes(GsnNode node) {
        allNodes.add(node);
        //    	System.out.println(node.getNodeId());
        if (node.getSupportedBy() != null) {
            for (int i = 0; i < node.getSupportedBy().size(); i++) {
                getGsnNodes(node.getSupportedBy().get(i));
            }
        }
        if (node.getInContextOf() != null) {
            for (int i = 0; i < node.getInContextOf().size(); i++) {
                getGsnNodes(node.getInContextOf().get(i));
            }
        }
    }

    /**
     * Writes the node declarations to the dot file
     *
     * @param allNodes
     * @param bw
     * @throws IOException
     */
    private void writeNodes(BufferedWriter bw) throws IOException {
        bw.write("//Node declarations:-");
        bw.newLine();
        for (GsnNode node : allNodes) {
            String nodeShape = "";
            String nodeColor = "";
            String nodeText = "";
            String hoverDisplay = "No additional information is available";
            String url = "";
            String margin = "0.05";
            String style = "bold";
            String penwidth = "3.0";

            boolean nodeStatus = true;

            // deciding node shape and hovertext
            if (node.getNodeType() == GsnCoreElements.Class.Context) {
                nodeShape = "rectangle, style=\"rounded\" ";
                nodeText = node.getContext().getContext().replace('-', '_').replace('.', '_');
            } else if (node.getNodeType() == GsnCoreElements.Class.Solution) {
                nodeShape = "circle";
                nodeText = node.getSolution().getSolTest().replace('-', '_').replace('.', '_');
                nodeStatus = node.getIsGreen();
            } else if (node.getNodeType() == GsnCoreElements.Class.Goal) {

                nodeShape = "box";
                nodeText = CustomStringUtils.stringWrapper(node.getGoal().getDescription());
                nodeStatus = node.getIsGreen();
            } else if (node.getNodeType() == GsnCoreElements.Class.Strategy) {
                nodeShape = "parallelogram";
                nodeText = node.getStrategy().getDescription();
                nodeStatus = node.getIsGreen();
            }

            // deciding node color
            if (nodeStatus) {
                nodeColor = "green";
            } else {
                nodeColor = "red";
            }
            if (node.getNodeType() == GsnCoreElements.Class.Context) {
                nodeColor = "black";
            }

            String nodeDeclareString =
                    node.getNodeId().replace('-', '_').replace('.', '_')
                            + " ["
                            + "href=\""
                            + url
                            + "\", tooltip=\""
                            + hoverDisplay
                            + "\", margin=\""
                            + margin
                            + "\", style=\""
                            + style
                            + "\", color="
                            + nodeColor
                            + ", shape="
                            + nodeShape
                            + ", penwidth ="
                            + penwidth
                            + ", label=\""
                            + node.getNodeId().replace('-', '_').replace('.', '_')
                            + "\\n\\n"
                            + nodeText
                            + "\"];";
            bw.write(nodeDeclareString);
            bw.newLine();
        }
    }

    /**
     * Declares all edges in the dot file
     *
     * @param allNodes
     * @param bw
     * @throws IOException
     */
    private void writeEdges(BufferedWriter bw) throws IOException {
        bw.write("//Edge declarations:-");
        bw.newLine();
        for (GsnNode node : allNodes) {
            // supportedBy edges
            if (node.getSupportedBy() != null) {
                for (GsnNode support : node.getSupportedBy()) {
                    String str =
                            node.getNodeId().replace('-', '_').replace('.', '_')
                                    + " -> "
                                    + support.getNodeId().replace('-', '_').replace('.', '_')
                                    + " [splines=curved, penwidth = 2.0, weight=3, arrowsize=2.0]";
                    // check if written, otherwise write
                    if (!CustomStringUtils.listContains(allEdgeStrings, str)) {
                        writeSringAndNewline(bw, str);
                        allEdgeStrings.add(str);
                    }
                }
            }
            // inContextOf edges
            if (node.getInContextOf() != null) {
                for (GsnNode context : node.getInContextOf()) {
                    String str =
                            node.getNodeId().replace('-', '_').replace('.', '_')
                                    + " -> "
                                    + context.getNodeId().replace('-', '_').replace('.', '_')
                                    + " [splines=curved, penwidth = 2.0, arrowhead=empty, arrowsize=1.5]";
                    if (!CustomStringUtils.listContains(allEdgeStrings, str)) {
                        writeSringAndNewline(bw, str);
                        allEdgeStrings.add(str);
                    }
                }
            }
        }
    }

    /**
     * Writes a give string to file and adds a newline
     *
     * @param bw
     * @param str
     * @throws IOException
     */
    private void writeSringAndNewline(BufferedWriter bw, String str) throws IOException {
        bw.write(str);
        bw.newLine();
    }
}
