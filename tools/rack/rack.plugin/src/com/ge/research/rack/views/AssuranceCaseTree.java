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
package com.ge.research.rack.views;

import com.ge.research.rack.autoGsn.viewHandlers.AutoGsnUnifiedMainViewHandler;
import com.ge.research.rack.utils.*;
import com.ge.research.rack.utils.TreeNode;
import com.google.inject.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import java.awt.Frame;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JScrollPane;
import javax.swing.JViewport;



/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained
 * from the model. The sample creates a dummy model on the fly, but a real implementation would
 * connect to the model available either in this or another plug-in (e.g. the workspace). The view
 * is connected to the model using a content provider.
 *
 * <p>The view uses a label provider to define how model objects should be presented in the view.
 * Each view can present the same model objects using different labels and icons, if needed.
 * Alternatively, a single label provider can be shared between views in order to ensure that
 * objects of the same type are presented in the same way everywhere.
 *
 * <p>
 */
public class AssuranceCaseTree extends ViewPart {

    /** The ID of the view as specified by the extension. */
    public static final String ID = "rackplugin.views.AssuranceCaseTree";

    @Inject IWorkbench workbench;
    public static HashMap<TreeNode, ArrayList<TreeNode>> graph = null;
    public static HashMap<String, TreeNode> index = null;
    
//    private AutoGsnUnifiedMainViewHandler obj;



    @Override
    public void createPartControl(Composite parent) {

    	try {
    		
        Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
        final Frame frame = SWT_AWT.new_Frame(composite);
        
        final JScrollPane parentPanel = new JScrollPane();
        parentPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        parentPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        var vp = new JViewport();
        final JFXPanel fxPanel = new JFXPanel();

//        obj = new AutoGsnUnifiedMainViewHandler();


        Parent root = FXMLLoader.load(getClass().getResource("/resources/fxml/autoGsn/AutoGsnUnifiedMainView.fxml"));
        Platform.setImplicitExit(false);
        Platform.runLater(
            new Runnable() {

            	@Override
            	public void run() {
            		try {

//            			Bundle bundle =
//            					org.eclipse.core.runtime.Platform.getBundle("rack.plugin");
//            			URL fxmlUrl =
//            					FileLocator.find(
//            							bundle,
//            							new Path(
//            									"resources/fxml/autoGsn/AutoGsnUnifiedMainView.fxml"
//            									//"resources/demo.fxml"
//            									),
//            							null);
//            			fxmlUrl = FileLocator.toFileURL(fxmlUrl);
//
//            			// Creating an FXMLLoader object that can be returned for use where needed
//            			FXMLLoader loader = new FXMLLoader(fxmlUrl);
//
//            			System.out.println(
//            					"Time before loading fxml:" + System.currentTimeMillis());
//            			Parent root = loader.load();
//            			System.out.println(
//            					"Time after loading fxml:" + System.currentTimeMillis());
//            			// stage.setTitle("Automatic GSN Inference");
//            			System.out.println(
//            					"Time before creating new scene:" + System.currentTimeMillis());

            			Scene scene = new Scene(root);
            			fxPanel.setScene(scene);

            			vp.add(fxPanel);
            			parentPanel.setViewport(vp);

            			frame.add(parentPanel);
            			frame.setSize(1300, 600);
            			frame.setVisible(true);

            			//obj.initialize();
            	    	
            		} catch (Exception ex) {
            			ErrorMessageUtil.error("createPartControl-exception " + ex);

            			ex.printStackTrace();
            		}
            	}
            }
        );
    	} catch (Exception ex) {
			ErrorMessageUtil.error("createPartControl-exception-end " + ex);
    	}
    }

//    private void showMessage(String message) {
//        MessageDialog.openInformation(
//                viewer.getControl().getShell(), "Assurance Case GSN", message);
//    }

    @Override
    public void setFocus() {
        // viewer.getControl().setFocus();
    }
    
    @FXML
    private void comboFilterAction(ActionEvent event) throws Exception {
    	ErrorMessageUtil.print("comboFilterAction");
    }

    @FXML
    private void btnFetchGoalsAction(ActionEvent event) throws Exception {
    	ErrorMessageUtil.print("btnFetchGoalsAction");
    }

    @FXML
    private void btnGenerateAction(ActionEvent event) throws Exception {
    	ErrorMessageUtil.print("btnGenerateAction");
    }

    @FXML
    private void btnTraverseAction(ActionEvent event) throws Exception {
    	ErrorMessageUtil.print("btnTraverseAction");
    }

    @FXML
    private void listGoalsSelectAction(MouseEvent event) {
    	ErrorMessageUtil.print("listGoalsSelectAction");
    }
}
