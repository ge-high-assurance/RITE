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
package com.ge.research.rite.autoGsn.viewManagers;

import com.ge.research.rite.autoGsn.logic.GsnPathInferenceEngine;
import com.ge.research.rite.autoGsn.structures.GsnViewsStore;
import com.ge.research.rite.autoGsn.structures.InstanceData;
import com.ge.research.rite.autoGsn.structures.MultiClassPackets.GoalIdAndClass;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul Note: This class manages all the views for the AutoGsn feature. It also
 *     stores data to be shared across all autoGSN views There are separate handler classes for each
 *     view fxml
 *     <p>When it is initially called by the JavafxAppLancgManager, This class creates a stage using
 *     AutoGsnMainView.fxml.
 */
public class AutoGsnViewsManager extends Application {

    // Stores the stage where all main scenes for the application will be staged
    public static Stage stage;

    // These variables will store data for all views in all instances of AutoGsnView
    // to store the user inputs
    public static GsnViewsStore gsnVObjs;

    // The instance data dependencies for allGsn
    public static List<InstanceData> allGsnInstanceDependencies;

    // Stores the list of all possible Goal Ids and corresponding classes
    public static List<GoalIdAndClass> allRootGoals;

    // stores rack output directory
    public static String rackDir;

    // stores the paterninfo and queries once created
    public static GsnPathInferenceEngine pathInferEngObj;

    // stores all goal class id info
    public static List<String> allGoalClassIds;

    // This will be used for opening external browser where required
    public static HostServices hostServices;

    /**
     * This function can initialize all the class variables that store Gsn data
     *
     * <p>Can be called once in the start method so that every invocation is a fresh one
     */
    public static void initializeViewVariables() {
        gsnVObjs = new GsnViewsStore();
        allGsnInstanceDependencies = new ArrayList<InstanceData>();
        allRootGoals = new ArrayList<GoalIdAndClass>();
        rackDir = new String();
        pathInferEngObj = new GsnPathInferenceEngine();
        allGoalClassIds = new ArrayList<String>();
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
            Bundle bundle = Platform.getBundle("rite.plugin");
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

        // sets the hostServices (only works here, no idea why)
        hostServices = getHostServices();

        try {

            // Set stage as the new stage sent by caller
            stage = primaryStage;

            // initialize the variables for this application launch
            initializeViewVariables(); // Comment out if you want the values from previous launch to
            // show up when the GSN feature is launched

            // set the initial AutoGsnMainView fxml to stage
            setNewFxmlToStage("resources/fxml/autoGsn/AutoGsnUnifiedMainView.fxml");

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }
}
