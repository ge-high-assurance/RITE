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
package com.ge.research.rack.arp4754.wireframe.ui;

import java.net.URL;
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

public class Arp4754WireframeMainViewManager extends Application {

    // Stores the stage where all main scenes for the application will be staged
    public static Stage stage;

    // This will be used for opening external browser where required
    public static HostServices hostServices;

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

            // System.out.println("Time before loading fxml:" + System.currentTimeMillis());
            Parent root = loader.load();
            // System.out.println("Time after loading fxml:" + System.currentTimeMillis());
            stage.setTitle("ARP-4754A Plan Creation");
            // System.out.println("Time before creating new scene:" + System.currentTimeMillis());
            stage.setScene(new Scene(root));
            // System.out.println("Time after creating new scene:" + System.currentTimeMillis());
            stage.setResizable(false); // User cannot change window size
            stage.show();

            // return the FXMLLoader
            return loader;

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Here");
        }

        return null;
    }

    @Override
    public void start(Stage primaryStage) {

        // sets the hostServices (only works here, no idea why)
        hostServices = getHostServices();
        try {
            // Set stage as the new stage sent by caller
            stage = primaryStage;

            // set the initial fxml to stage
            setNewFxmlToStage("resources/fxml/arp4754/WireframeMainView.fxml");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
