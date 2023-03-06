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

import com.ge.research.rack.autoGsn.constants.GsnCoreElements;
import com.ge.research.rack.autoGsn.structures.GsnNode;
import com.ge.research.rack.autoGsn.structures.GsnViewsStore;
import com.ge.research.rack.autoGsn.structures.InstanceData;
import com.ge.research.rack.autoGsn.structures.MultiClassPackets.GoalIdAndClass;
import com.ge.research.rack.autoGsn.viewManagers.AutoGsnViewsManager;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * backend for Gsn gui
 *
 * @author Saswata Paul
 */
public class AutoGsnGuiUtils {

    /**
     * Creates and returns a randmo hexadecimal color code
     *
     * @return
     */
    public static String randomColor() {

        // get a random number
        int random = new Random().nextInt(0xffffff + 1);

        // create hex and return
        String colHex = String.format("#%06x", random);
        System.out.println(colHex);
        return colHex;
    }

    /**
     * fetches the GSN icon for a node
     *
     * <p>NOTE: May not work properly on all platforms because of how the resource is fetched
     *
     * @param node
     * @return
     */
    public static ImageView getNodeImage(GsnNode node) {

        String imgName = "";

        // decide what image name to use
        if (node.getIsGreen()) {
            if (node.getNodeType() == GsnCoreElements.Class.Goal) {
                imgName = "goal_p";
            }
            if (node.getNodeType() == GsnCoreElements.Class.Strategy) {
                imgName = "strategy_p";
            }
            if (node.getNodeType() == GsnCoreElements.Class.Solution) {
                imgName = "solution_p";
            }
        } else {
            if (node.getNodeType() == GsnCoreElements.Class.Goal) {
                imgName = "goal_f";
            }
            if (node.getNodeType() == GsnCoreElements.Class.Strategy) {
                imgName = "strategy_f";
            }
            if (node.getNodeType() == GsnCoreElements.Class.Solution) {
                imgName = "solution_f";
            }
        }

        try {
            String imagePath = "icons/" + imgName + ".PNG";

            Bundle bundle = Platform.getBundle("rack.plugin");
            URL imgUrl = FileLocator.find(bundle, new Path(imagePath), null);
            imgUrl = FileLocator.toFileURL(imgUrl);

            // the imgURL starts with "file:/", so stripping it here
            String imgUrlStr = imgUrl.toString().substring("file:/".length());

            // System.out.println(imgUrl.toString());

            ImageView icon = new ImageView(new Image(new FileInputStream(new File(imgUrlStr))));

            return icon;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns javafx color for a given GsnNode
     *
     * @param node
     * @return
     */
    public static Color getNodeColor(GsnNode node) {

        if (node.getIsGreen()) {
            return Color.GREEN;
        }
        return Color.RED;
    }

    //    /**
    //     * Takes inputs, checks the GSN preferences to decide what dataprocessor to use for the
    // selected
    //     * overlay, creates the gsns and dependencies, and returns them
    //     *
    //     * @param inputs
    //     * @return
    //     * @throws Exception
    //     */
    //    public static MultiClassPackets.GsnListAndDependenyList checkProjectSelectionAndGetAllGsn(
    //            GsnViewsStore inputs, String tempDir) throws Exception {
    //
    //        MultiClassPackets.GsnListAndDependenyList pair =
    //                new MultiClassPackets().new GsnListAndDependenyList();
    //
    //        RackConstants.ProjectIds selectedProj = selectedProjOverlay();
    //
    //        switch (selectedProj) {
    //            case GE:
    //                System.out.println("Calling processor: " + RackConstants.ProjectIds.GE);
    //                // Create a GsnDataProcessor object and fetch the data
    //                GsnDataProcessorGE gDPObj = new GsnDataProcessorGE();
    //
    //                // Process data to create all possible GsnNodes
    //                pair.setGsnList(gDPObj.getGsnAllGoals(tempDir));
    //
    //                // get the dependencies
    //                pair.setDependencyList(gDPObj.getInstanceDataDependencies());
    //
    //                return pair;
    //
    //            case SRI:
    //                // TODO
    //                return pair;
    //
    //            case SRISS:
    //                System.out.println("Calling processor: " + RackConstants.ProjectIds.SRISS);
    //                // Create a GsnDataProcessor object and fetch the data
    //                GsnDataProcessorSRISS gDPObj1 = new GsnDataProcessorSRISS();
    //
    //                // Process data to create all possible GsnNodes
    //                pair.setGsnList(gDPObj1.getGsnAllGoals(tempDir));
    //
    //                // get the dependencies
    //                pair.setDependencyList(gDPObj1.getInstanceDataDependencies());
    //                return pair;
    //        }
    //
    //        System.out.println("Calling processor: NOT FOUND");
    //        return null;
    //    }

    /**
     * Takes user inputs, a set of GsnNodes and their dependencies and goalIds for which GSNs must
     * be created Creates the artifacts for the rootGoals corresponding to the goalIds
     *
     * @param inputs
     * @param allGsn
     * @param gsnDependencies
     * @param goalIdsToCreate
     * @throws Exception
     */
    public static void createArtifactsFromSelectedGsnNodes(
            GsnViewsStore inputs,
            List<GsnNode> allGsn,
            List<InstanceData> gsnDependencies,
            List<String> goalIdsToCreate)
            throws Exception {

        // fetch required imports for core, pattern, and instance
        String patternUri = CustomFileUtils.getUriFromSADLFile(inputs.getPatternGsnPath());
        String projOverlayUri = CustomFileUtils.getUriFromSADLFile(inputs.getProjectOverlayPath());

        // gets the required directory paths
        String tempDir = CustomFileUtils.getRackDir();
        String outDir =
                CustomFileUtils.getFileDirectory(inputs.getPatternGsnPath())
                        + System.getProperty("file.separator")
                        + "gsnArtifacts"
                        + System.getProperty("file.separator");

        // Create the output directory
        CustomFileUtils.createNewDirectory(outDir);

        // create the Gsn imports
        List<String> gsnImports = new ArrayList<String>();
        gsnImports.add(patternUri);

        // for each goalId, create the gsn
        for (String id : goalIdsToCreate) {
            GsnNode gsn = GsnNodeUtils.getGsnNode(allGsn, id);
            String svgPath =
                    InterfacingUtils.createGsnArtifactForGsnNode(
                            tempDir, outDir, gsn, gsnDependencies, projOverlayUri, gsnImports);

            // open the generated svg in default app
            openUrlInDefaultApp(svgPath);
        }
    }

    /**
     * Given a file address, opens it in the default user specified app on the platform
     *
     * @param url
     */
    public static void openUrlInDefaultApp(String url) {
        System.out.println("Trying to open generated GSN svg file in default app!");
        try {
            AutoGsnViewsManager.hostServices.showDocument(url);
        } catch (Exception e) {
            System.out.println("ERROR: Failed to open generated GSN svg file in default app!");
            e.printStackTrace();
        }
    }

    /**
     * Takes a List<GoalIdAndClass> and returns all unique class ids
     *
     * @param allRootGoals
     */
    public static List<String> getAllClasses(List<GoalIdAndClass> allRootGoals) {
        List<String> allClasses = new ArrayList<String>();

        for (GoalIdAndClass goalInfo : allRootGoals) {
            allClasses.add(goalInfo.getGoalClass());
        }

        // remove duplicates and return
        return CustomStringUtils.removeDuplicates(allClasses);
    }

    /**
     * Takes and instance id and a List<GoalIdAndClass> and returns the corresponding class id
     *
     * @param allRootGoals
     */
    public static String getGoalClassInfo(String instanceId, List<GoalIdAndClass> allRootGoals) {

        for (GoalIdAndClass goalInfo : allRootGoals) {
            if (goalInfo.getGoalId().equals(instanceId)) {
                return goalInfo.getGoalClass();
            }
        }

        return null;
    }

    /**
     * Takes a List<GoalIdAndClass> and a class Id and returns a count
     *
     * @param allRootGoals
     */
    public static int getClassCount(List<GoalIdAndClass> allRootGoals, String classId) {
        int count = 0;

        for (GoalIdAndClass goalInfo : allRootGoals) {
            if (goalInfo.getGoalClass().equals(classId)) {
                count = count + 1;
            }
        }

        return count;
    }
}
