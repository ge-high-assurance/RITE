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
package com.ge.research.rack.analysis.managers;

import com.ge.research.rack.analysis.structures.Plan;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewsManager extends Application {

    protected static double DEFAULT_FONT_SIZE_PX = 20;
    protected static final double MIN_HEIGHT_PX = 600;
    protected static final double MIN_WIDTH_PX = 800;

    // Stores the stage where all main scenes for the application will be staged
    public static Stage stage;

    // These variables will store data for all views in all instances of Report Application
    public static Plan reportDataObj;

    // stores the current font size at any time
    public static double currentFontSize;

    // This will be used for opening external browser where required
    public static HostServices hostServices;

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

    protected void startInitialize() {
        // Subclass to override
    }

    @Override
    public void start(Stage primaryStage) {
        // sets the hostServices (only works here, no idea why)
        hostServices = getHostServices();

        try {
            // Set stage as the new stage sent by caller
            stage = primaryStage;

            /* Comment out to cache values from previous launch.
             * Initializes the variables for this application launch. */
            startInitialize();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
