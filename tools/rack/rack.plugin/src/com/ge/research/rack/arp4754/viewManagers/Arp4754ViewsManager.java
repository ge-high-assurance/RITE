/**
 * 
 */
package com.ge.research.rack.arp4754.viewManagers;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.ge.research.rack.arp4754.structures.DAPlan;
import com.ge.research.rack.do178c.structures.PsacNode;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Saswata Paul
 *
 */
public class Arp4754ViewsManager extends Application {


    private static final String FXML_FILE_PATH = "resources/fxml/arp4754/MainView.fxml";
    private static final String REPORT_TITLE = "ARP-4754 Compliance Report";
    private static double DEFAULT_FONT_SIZE_PX = 20;
    private static final double MIN_HEIGHT_PX = 600;
    private static final double MIN_WIDTH_PX = 800;

    // Stores the stage where all main scenes for the application will be staged
    public static Stage stage;

    // These variables will store data for all views in all instances of Report Application
    public static DAPlan reportDataObj;

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
        reportDataObj = new DAPlan();
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
