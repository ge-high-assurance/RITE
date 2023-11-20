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
package com.ge.research.rack.report.viewManagers;

import com.ge.research.rack.report.structures.PsacNode;
import java.net.URL;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * @author Saswata Paul
 */
public class ReportViewsManager extends Application {

    private static final String FXML_FILE_PATH = "resources/fxml/report/ReportMainView_new.fxml";
    private static final String REPORT_TITLE = "DO-178C Compliance Report";
    private static double DEFAULT_FONT_SIZE_PX = 20;
    private static final double MIN_HEIGHT_PX = 600;
    private static final double MIN_WIDTH_PX = 800;

    // Stores the stage where all main scenes for the application will be staged
    public static Stage stage;

    // These variables will store data for all views in all instances of Report Application
    public static PsacNode reportDataObj;

    // stores the current font size at any time
    public static double currentFontSize;

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
     * This function can initialize all the class variables that store report data
     *
     * <p>Can be called once in the start method so that every invocation is a fresh one
     */
    public static void initializeViewVariables() {
        // define all data variables as new instances of respective classes
        reportDataObj = new PsacNode();
    }

    /**
     * This function sets a provided fxml to the class stage and returns the FXMLLoader object for
     * future use if needed
     *
     * @param fxmlFilePath
     * @return
     */
    public static FXMLLoader setNewFxmlToStage(final String fxmlFilePath) {

        try {

            final Bundle bundle = Platform.getBundle("rack.plugin");
            final URL fxmlUrl =
                    FileLocator.toFileURL(FileLocator.find(bundle, new Path(fxmlFilePath), null));

            // Creating an FXMLLoader object that can be returned for use where needed
            final FXMLLoader loader = new FXMLLoader(fxmlUrl);
            final Parent root = loader.load();

            double prevWidth = stage.getWidth();
            double prevHeight = stage.getHeight();
            if (null != stage.getScene()) {
                stage.getScene().setRoot(root);
            } else {
                stage.setScene(new Scene(root));
            }

            // -- Turning off below since we have dedicated size buttons now
            // dynamically scale the font size with window size
            //            final Scene scene = stage.getScene();
            //            final DoubleProperty fontSize = new
            // SimpleDoubleProperty(DEFAULT_FONT_SIZE_PX);
            //            final double scalingFactor =
            //                    ((MIN_HEIGHT_PX + MIN_WIDTH_PX) / DEFAULT_FONT_SIZE_PX) * 1.3;
            //
            // fontSize.bind(scene.widthProperty().add(scene.heightProperty()).divide(scalingFactor));
            //            scene.getRoot()
            //                    .styleProperty()
            //                    .bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));

            // set the value of current font size
            final Scene scene = stage.getScene();
            final DoubleProperty fontSize = new SimpleDoubleProperty(DEFAULT_FONT_SIZE_PX);
            scene.getRoot()
                    .styleProperty()
                    .bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));

            stage.setTitle(REPORT_TITLE);
            stage.setMinHeight(MIN_HEIGHT_PX);
            stage.setMinWidth(MIN_WIDTH_PX);
            stage.setResizable(true);
            stage.setHeight(prevHeight);
            stage.setWidth(prevWidth);

            stage.show();

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

            /* Comment out to cache values from previous launch.
             * Initializes the variables for this application launch. */
            initializeViewVariables();

            setNewFxmlToStage(FXML_FILE_PATH);

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }
}
