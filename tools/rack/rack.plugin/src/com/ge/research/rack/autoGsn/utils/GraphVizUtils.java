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
package com.ge.research.rack.autoGsn.utils;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Saswata Paul
 */
public class GraphVizUtils {

    /**
     * Takes the address strings of a source dot file and a destination svg file and creates an svg
     * file from the dot using the GraphViz API
     *
     * <p>TODO: There is a stackoverflow issue that happens when using through the plugin. However,
     * this issue does not occur when calling from a main() running as a java application This is
     * not related to javafx because this occurs even on swt interface So most likely a
     * plugin-related issue
     *
     * @param sourceAddress
     * @param outputAddress
     * @throws IOException
     * @throws IOException
     */
    public static void generateGraphUsingGraphviz(String sourceAddress, String outputAddress)
            throws IOException {

        System.out.println("Dot file source address ---> " + sourceAddress);

        // Parse dot file and create the svg
        try (InputStream dot = new FileInputStream(sourceAddress); ) {
            MutableGraph g = new Parser().read(dot);
            Graphviz.useEngine(new GraphvizV8Engine());
            Graphviz.fromGraph(g).width(1400).render(Format.SVG).toFile(new File(outputAddress));
        }
    }

    /**
     * The plugin throws a weird stackoverflow error with the graphviz api. So this function is a
     * replacement that works by running a dot command natively on the OS
     *
     * @param sourceAddress
     * @param outputAddress
     * @throws IOException
     */
    public static void generateGraphUsingDot(String sourceAddress, String outputAddress)
            throws IOException {

        System.out.println("Dot file source address ---> " + sourceAddress);

        // Command format: dot -Tsvg GSN-TEST.dot -o test.svg
        String command = "dot -Tsvg " + sourceAddress + " -o " + outputAddress;

        // run the command on the OS terminal to create the svg
        System.out.println("Running " + command + " to ceate the svg");
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
