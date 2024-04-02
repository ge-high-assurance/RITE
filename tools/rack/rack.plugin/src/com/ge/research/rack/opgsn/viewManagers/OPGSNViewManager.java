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
package com.ge.research.rack.opgsn.viewManagers;

import com.ge.research.rack.autoGsn.structures.GsnNode;
import java.net.URL;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class OPGSNViewManager extends Application {

    // Stores the stage where all main scenes for the application will be staged
    public static Stage stage;

    // To store the OP GSN
    public static GsnNode opGSN;

    private static double DEFAULT_FONT_SIZE_PX = 20;

    public static DirectoryChooser directoryChooser = new DirectoryChooser();
    public static Boolean dirSelected;

    /**
     * function to increase the global fontsize if tre is passed, else decrease it
     *
     * @param increase
     */
    public static void increaseGlobalFontSize(boolean increase) {
        // increase default font if true, else decrease
        if (increase) {
            if (DEFAULT_FONT_SIZE_PX < 30) {
                DEFAULT_FONT_SIZE_PX = DEFAULT_FONT_SIZE_PX + 2.0;
            }
        } else {
            if (DEFAULT_FONT_SIZE_PX > 8.0) {
                DEFAULT_FONT_SIZE_PX = DEFAULT_FONT_SIZE_PX - 2.0;
            }
        }
        System.out.println("New global font size = " + DEFAULT_FONT_SIZE_PX);
        final Scene scene = stage.getScene();
        final DoubleProperty fontSize = new SimpleDoubleProperty(DEFAULT_FONT_SIZE_PX);
        scene.getRoot()
                .styleProperty()
                .bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
    }

    /**
     * This function can initialize all the class variables that store Gsn data
     *
     * <p>Can be called once in the start method so that every invocation is a fresh one
     */
    public static void initializeViewVariables() {
        opGSN = new GsnNode();
    }

    /**
     * This function sets a provided fxml to the class stage and returns the FXMLLoader object for
     * future use if needed
     *
     * @param fxmlFilePath
     * @return
     */
    public static FXMLLoader setNewFxmlToStage(String fxmlFilePath) {

        try {
            Bundle bundle = Platform.getBundle("rack.plugin");
            URL fxmlUrl = FileLocator.find(bundle, new Path(fxmlFilePath), null);
            fxmlUrl = FileLocator.toFileURL(fxmlUrl);

            // Creating an FXMLLoader object that can be returned for use where needed
            FXMLLoader loader = new FXMLLoader(fxmlUrl);

            System.out.println("Time before loading fxml:" + System.currentTimeMillis());
            Parent root = loader.load();
            System.out.println("Time after loading fxml:" + System.currentTimeMillis());
            stage.setTitle("Automatic GSN Inference");
            System.out.println("Time before creating new scene:" + System.currentTimeMillis());
            stage.setScene(new Scene(root));
            System.out.println("Time after creating new scene:" + System.currentTimeMillis());
            stage.setResizable(false); // User cannot change window size
            stage.show();

            // return the FXMLLoader
            return loader;

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        System.out.println("Here");
        return null;
    }

    @Override
    public void start(Stage primaryStage) {

        try {

            // Set stage as the new stage sent by caller
            stage = primaryStage;

            dirSelected = false;

            // initialize the variables for this application launch
            initializeViewVariables(); // Comment out if you want the values from previous launch to
            // show up when the GSN feature is launched

            // set the initial AutoGsnMainView fxml to stage
            setNewFxmlToStage("resources/fxml/autoGsn/AutoGsnUnifiedDrillGoalView.fxml");

            //            // generate the GSN objects as necessary (Hardcoded for now)
            //            String tempDir = "C://Users/212807042/Desktop/test-tmp";
            //
            //            DataProcessor opTreeDataProcessorObj = new DataProcessor();
            //
            //            opTreeDataProcessorObj.createOPTreeObject(tempDir);
            //
            //            OPTreeToGSN opTree2GsnObj = new OPTreeToGSN();
            //
            //            opGSN =
            // opTree2GsnObj.createOPTreeGoalNode(opTreeDataProcessorObj.opTreeObj, 0);
            //
            //            GsnNodeUtils.printGsnNode(opGSN);
            //
            //            // -- Create SVG for entire tree
            //            InterfacingUtils.createGsnSvg(opGSN, tempDir, tempDir, "OPTree");
            //
            //            // -- Create Vanderbilt interface for entire tree
            //            File gsnVanderbiltFile = new File(tempDir, ("OPTree" + ".gsn"));
            //            GsnNode2VaderbiltGsnPrinter vanderBiltPrinterObj = new
            // GsnNode2VaderbiltGsnPrinter();
            //            vanderBiltPrinterObj.createVanderbiltGsn(opGSN, gsnVanderbiltFile);
            //
            //            // open the GSn object in navigator fxml
            //            // Set the stage with the other fxml
            //            FXMLLoader drillViewLoader =
            //                    OPGSNViewManager.setNewFxmlToStage(
            //
            // "resources/fxml/autoGsn/AutoGsnUnifiedDrillGoalView.fxml");
            //
            //            // initialize variables in the AutoGsnDrillGoalView page
            //            OPGSNNavigatorHandler drillViewLoaderClassObj =
            // drillViewLoader.getController();
            //            drillViewLoaderClassObj.prepareView(0, opGSN.getNodeId(), opGSN);

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }
}
