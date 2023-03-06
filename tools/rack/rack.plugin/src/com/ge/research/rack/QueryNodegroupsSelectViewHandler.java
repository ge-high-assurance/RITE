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
package com.ge.research.rack;

import com.ge.research.rack.autoGsn.utils.CustomFileUtils;
import com.ge.research.rack.autoGsn.utils.CustomStringUtils;
import com.ge.research.rack.report.utils.RackQueryUtils;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * @author Saswata Paul
 *     <p>Note: There is not manager for this handler class since this is a single screen
 *     trivial-application This class contains both the start method to set the fxml file and the
 *     necessary functions to control the fxml elements
 */
public class QueryNodegroupsSelectViewHandler extends Application {

    // Stores the stage where all main scenes for the application will be staged
    public static Stage stage;

    // Stores last location selected in a filechooser instance
    private String lastLocation;

    // Stores filepaths of selected json files
    List<String> paths = new ArrayList<String>();

    // -------- FXML GUI variables below --------------
    @FXML private ListView<String> listJson;

    @FXML private Button btnSelect;

    @FXML private Button btnUpload;

    @FXML private ProgressIndicator progInd;

    // --------------------------------

    // -------- Manager functions --------------
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

            //            Parent root = FXMLLoader.load(fxmlUrl);
            Parent root = loader.load();
            stage.setTitle("Upload Query Nodegroups from .json Files");
            stage.setScene(new Scene(root));
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

            // set the initial AutoGsnMainView fxml to stage
            setNewFxmlToStage("resources/fxml/QueryNodeGroupSelectView.fxml");

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    // --------------------------------------------

    // -------- Handler Functions --------------

    private void populateListJson() {
        listJson.getItems().clear();

        for (String path : paths) {
            listJson.getItems().add(RackQueryUtils.getQueryIdFromFilePath(path));
        }

        // enable upload button
        btnUpload.setDisable(false);
    }

    @FXML
    private void initialize() {
        // hide the progress bar
        progInd.setVisible(false);

        // disable upload button
        btnUpload.setDisable(true);
    }

    @FXML
    private void btnSelectAction() {
        // Creating a File chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Query .json Files");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("JSON Files", "*.json*"));

        // Checks if lasLocation points to a valid location and sets default location for the
        // filechooser
        if ((lastLocation != null) && CustomFileUtils.validLocation(lastLocation)) {
            fileChooser.setInitialDirectory(new File(lastLocation));
        }

        // Get one/multiple choices
        List<File> listFiles = fileChooser.showOpenMultipleDialog(stage);

        if ((listFiles != null) && (listFiles.size() > 0)) {
            for (File file : listFiles) {
                paths.add(file.getAbsolutePath());
            }

            // set last location
            lastLocation = CustomFileUtils.getFileDirectory(listFiles.get(0).getAbsolutePath());

            // remove duplicates from paths
            paths = CustomStringUtils.removeDuplicates(paths);

            // refresh/initialize listview with paths
            populateListJson();
        }
    }

    @FXML
    private void btnUploadAction() {
        // TODO

        // Disable buttons, enable progInd
        btnSelect.setDisable(true);
        btnUpload.setDisable(true);
        progInd.setVisible(true);

        /*
         *  sending data processor to a different thread
         *  This is required to be able to disable the
         *  fetch button while it is already working
         */
        Task<Void> task =
                new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {

                        // Upload each query
                        for (String path : paths) {
                            RackQueryUtils.uploadQueryNodegroupToRackStore(
                                    RackQueryUtils.getQueryIdFromFilePath(path), path);
                        }

                        return null;
                    }
                };
        task.setOnSucceeded(
                evt -> {
                    // clear list and paths
                    paths = new ArrayList<String>();
                    listJson.getItems().clear();

                    // enable select button, disable progInd, (upload button already diabled)
                    btnSelect.setDisable(false);
                    progInd.setVisible(false);
                });
        new Thread(task).start();
    }
}
