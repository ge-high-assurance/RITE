package com.ge.research.rack.arp4754.wireframe.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Arp4754WireframeObjectivesViewManager extends Application {

    // Stores the stage where all main scenes for the application will be staged
    public static Stage stage;

    /**
     * This function sets a provided fxml to the class stage and returns the FXMLLoader object for
     * future use if needed
     *
     * @param fxmlFilePath
     * @return
     */
    public static FXMLLoader setNewFxmlToStage(String fxmlFilePath) {
        try {
            stage = new Stage();
            Bundle bundle = Platform.getBundle("rack.plugin");
            URL fxmlUrl = FileLocator.find(bundle, new Path(fxmlFilePath), null);
            fxmlUrl = FileLocator.toFileURL(fxmlUrl);

            // Creating an FXMLLoader object that can be returned for use where needed
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            stage.setTitle("Add Objective Query");
            stage.setScene(new Scene(root));
            stage.setResizable(false); // User cannot change window size

            // return the FXMLLoader
            return loader;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("Here");
        return null;
    }
    
    public static void show() {
    	stage.showAndWait();
    }
    
    public static void close() {
    	stage.close();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Nothing needs to be done here because in this case,
            // We want a new window to be opened along with the exsting window
            // So a new stage also needs to be created. This will be done when the caller
            // view's controller calls the createNewStageAndSetFxml() method
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
