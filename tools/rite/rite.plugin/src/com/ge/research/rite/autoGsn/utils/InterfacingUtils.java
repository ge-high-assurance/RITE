/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2024, GE Aerospace and Galois, Inc.
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
package com.ge.research.rite.autoGsn.utils;

import com.ge.research.rite.autoGsn.constants.GsnCoreElements;
import com.ge.research.rite.autoGsn.constants.RackConstants;
import com.ge.research.rite.autoGsn.helpers.GsnNode2DotPrinter;
import com.ge.research.rite.autoGsn.structures.GsnNode;
import com.ge.research.rite.autoGsn.structures.InstanceData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Contains functions that provide an interface to the different functionalities in the GSN Pipeline
 *
 * @author Saswata Paul
 */
public class InterfacingUtils {

    /**
     * Creates a sadl file with all the dependencies that may be required by the GSNs
     *
     * @param instanceDataDependencies
     * @param projOverlayUri
     * @param outDir
     * @throws IOException
     */
    public static String createGsnDependeciesSadl(
            List<InstanceData> instanceDataDependencies, String projOverlayUri, String outDir)
            throws IOException {

        // List of all sadl sentences for the dependency files
        List<String> sentences = new ArrayList<String>();

        // add the warning comment
        sentences.add(GsnCoreElements.OtherConstants.WARNING.getValue());

        // create the imports for the dependencies sadl using the projectOverlayUri and Rack
        // constant uris
        List<String> imports = new ArrayList<String>();
        imports.add(projOverlayUri);
        // Iterate over the arcos uris and add to the import
        EnumSet.allOf(RackConstants.ArcosUris.class).forEach(Uri -> imports.add(Uri.getUri()));

        // Create uriBlock for the dependency file and add to the list
        sentences.add(
                GsnNode2GsnSadlUtils.createUriBlock(
                        GsnCoreElements.OtherConstants.DEPURI.getValue(),
                        GsnCoreElements.OtherConstants.DEPALIAS.getValue(),
                        imports));

        // Create all required dependency Sadl sentences and add to the list
        for (InstanceData dependency : instanceDataDependencies) {
            sentences.add(GsnNode2GsnSadlUtils.getSadlDeclareSentence(dependency));
        }

        // Save the dependency sentences in a file
        String sadlDependenciesFilename = outDir + "instanceDependencies" + ".sadl";
        CustomStringUtils.writeStrListAsNewFile(sentences, sadlDependenciesFilename);

        return GsnCoreElements.OtherConstants.DEPURI.getValue();
    }

    /**
     * Creates the svg and returns its address
     *
     * @param gsn
     * @param outDir
     * @param gsnName
     * @throws IOException
     */
    public static String createGsnSvg(GsnNode gsn, String tempDir, String outDir, String gsnName)
            throws IOException {

        // Create the dotfile in the tempdir
        File gsnDotFile = new File(tempDir, (gsnName + ".dot"));
        GsnNode2DotPrinter gsn2dotObj = new GsnNode2DotPrinter();
        gsn2dotObj.createDot(gsn, gsnDotFile);

        // Create the svg filepath in output dir
        String graphDestination = outDir + System.getProperty("file.separator") + gsnName + ".SVG";
        String dotFilePath = gsnDotFile.getAbsolutePath();

        // generate the svg file using graphviz (or directly using dot)

        GraphVizUtils.generateGraphUsingGraphviz(dotFilePath, graphDestination);
        // GraphVizUtils.generateGraphUsingDot(dotFilePath, graphDestination);

        System.out.println("Info: Written GSN to svg");

        //        // Delete the dot files
        // GsnFileUtils.delFilesWithExt("dot", outDir);

        // return the graph path
        return graphDestination;
    }

    /**
     * creates the sadl
     *
     * @param gsn
     * @param outDir
     * @param gsnName
     * @param gsnAlias
     * @param gsnUri
     * @param imports
     * @throws IOException
     */
    public static void createGsnSadl(
            GsnNode gsn,
            String outDir,
            String gsnName,
            String gsnAlias,
            String gsnUri,
            List<String> imports)
            throws IOException {

        // Create the Gsn SADL sentences
        List<String> sentences =
                GsnNode2GsnSadlUtils.createGsnSadlInstance(gsnName, gsn, gsnUri, gsnAlias, imports);

        // Save the Gsn SADL sentences in a file
        String sadlGsnFileName = outDir + gsnName + ".sadl";
        CustomStringUtils.writeStrListAsNewFile(sentences, sadlGsnFileName);
    }

    /**
     * Takes directory information, a GSN as GsnNode, the dependencies, and import information
     * Creates sadl and svg artifacts for the GSN
     *
     * <p>Also returns the svgPath to the caller
     *
     * @param tempDir
     * @param outDir
     * @param gsn
     * @param projOverlayUri
     * @param imports
     * @throws IOException
     */
    public static String createGsnArtifactForGsnNode(
            String tempDir,
            String outDir,
            GsnNode gsn,
            List<InstanceData> gsnDependencies,
            String projOverlayUri,
            List<String> imports)
            throws IOException {

        // Create name, Alias, and Uri for the gsn GsnNode object
        List<String> gsnIdentifiers = GsnNodeUtils.synthesizeGsnNodeNameAliasUri(gsn);

        // Create the sadl file with all dependencies and adds the depndency sadl file uri to the
        // list of gsn imports
        imports.add(createGsnDependeciesSadl(gsnDependencies, projOverlayUri, outDir));

        // Create the Gsn Sadl file
        createGsnSadl(
                gsn,
                outDir,
                gsnIdentifiers.get(0),
                gsnIdentifiers.get(1),
                gsnIdentifiers.get(2),
                imports);

        // Create the Gsn svg
        String svgPath = createGsnSvg(gsn, tempDir, outDir, gsnIdentifiers.get(0));

        return svgPath;
    }
}
