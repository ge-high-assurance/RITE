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
package com.ge.research.rack.arp4754.utils;

import com.ge.research.rack.analysis.structures.PlanObjective;
import com.ge.research.rack.analysis.structures.PlanTable;
import com.ge.research.rack.arp4754.structures.Evidence;
import com.ge.research.rack.arp4754.structures.Output;
import com.ge.research.rack.arp4754.viewManagers.Arp4754ViewsManager;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Saswata Paul
 */
public class ViewUtils {

    private static final Font VERANDA_FONT = Font.font("Verdana", FontWeight.BOLD, 12);

    private static final String TOOLTIP_FONT_SIZE_STYLE = "-fx-font-size: 20";

    private static final String RACK_PLUGIN_BUNDLE_NAME = "rack.plugin";

    private static final String GE_LOGO_IMG_PATH = "resources/images/GE_AEROSPACE_LOGO_L.png";

    /** Assign a given text tooltip to a given javafx node */
    public static void assignTooltip(Node node, String text) {
        final Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.seconds(0.1));
        tooltip.setStyle(TOOLTIP_FONT_SIZE_STYLE);
        Tooltip.install(node, tooltip);
    }

    /** Creates a data bar and adds a value label to the top (only for int values) */
    public static XYChart.Data<String, Integer> createIntDataBar(String country, int value) {

        final Label label = new Label(Integer.toString(value));
        label.setTextFill(Color.WHITE);
        label.setFont(VERANDA_FONT);

        final Group group = new Group(label);
        StackPane.setAlignment(group, Pos.TOP_CENTER);

        final StackPane node = new StackPane();
        node.getChildren().add(group);

        final XYChart.Data<String, Integer> data =
                new XYChart.Data<String, Integer>(country, value);

        data.setNode(node);

        return data;
    }

    /** Returns javafx color for a process object */
    public static Color getProcessColor(final PlanTable procObj) {

        if (procObj.isNoData()) {
            return Color.GRAY; // if no data, then GRAY
        }
        if (procObj.isPartialData()) {
            return Color.ORANGE;
        }

        return procObj.isPassed() ? Color.GREEN : Color.RED;
    }

    /** Returns javafx color for an objective object */
    public static Color getObjectiveColor(final PlanObjective objObj) {

        if (objObj.isNoData()) {
            return Color.GRAY; // if no data, then GRAY
        }
        if (objObj.isPartialData()) {
            return Color.ORANGE;
        }

        return objObj.isPassed() ? Color.GREEN : Color.RED;
    }

    public static ImageView loadGeIcon() throws IOException, URISyntaxException {

        final Bundle bundle = Platform.getBundle(RACK_PLUGIN_BUNDLE_NAME);

        final String imgUrl =
                FileLocator.find(bundle, new Path(GE_LOGO_IMG_PATH), null).toURI().getPath();

        return new ImageView(imgUrl);
    }

    /**
     * Computes the numbers of different types of Evidence artifacts present in a process and
     * returns list
     *
     * @param procObj
     * @return
     */
    public static List<Integer> getProcessArtifactStats(PlanTable procObj) {

        // some generic types of elemnts in ARP4754
        final Set<String> docIds = new HashSet<String>();
        final Set<String> reqIds = new HashSet<String>();
        final Set<String> itemIds = new HashSet<String>();
        final Set<String> systemIds = new HashSet<String>();
        final Set<String> interfaceIds = new HashSet<String>();
        // some generic types of verification/test outputs in ARP4754
        final Set<String> verificationIds = new HashSet<String>();
        final Set<String> testIds = new HashSet<String>();
        final Set<String> reviewIds = new HashSet<String>();
        final Set<String> analysisIds = new HashSet<String>();

        procObj.getTabObjectives().stream()
                .map(PlanObjective::getOutputs)
                .forEach(
                        objective -> {
                            for (final Evidence doc : ((Output) objective).getDocumentObjs()) {
                                docIds.add(doc.getId());
                            }
                            for (final Evidence derItemReq :
                                    ((Output) objective).getDerItemReqObjs()) {
                                reqIds.add(derItemReq.getId());
                            }
                            for (final Evidence derSysReq :
                                    ((Output) objective).getDerSysReqObjs()) {
                                reqIds.add(derSysReq.getId());
                            }
                            for (final Evidence itemReq : ((Output) objective).getItemReqObjs()) {
                                reqIds.add(itemReq.getId());
                            }
                            for (final Evidence sysReq : ((Output) objective).getSysReqObjs()) {
                                reqIds.add(sysReq.getId());
                            }
                            for (final Evidence item : ((Output) objective).getItemObjs()) {
                                itemIds.add(item.getId());
                            }
                            for (final Evidence interfce :
                                    ((Output) objective).getInterfaceObjs()) {
                                interfaceIds.add(interfce.getId());
                            }
                            for (final Evidence system : ((Output) objective).getSystemObjs()) {
                                systemIds.add(system.getId());
                            }
                            for (final Evidence verification :
                                    ((Output) objective).getVerificationObjs()) {
                                verificationIds.add(verification.getId());
                            }
                            for (final Evidence test : ((Output) objective).getTestObjs()) {
                                testIds.add(test.getId());
                            }
                            for (final Evidence review : ((Output) objective).getReviewObjs()) {
                                reviewIds.add(review.getId());
                            }
                            for (final Evidence analysis : ((Output) objective).getAnalysisObjs()) {
                                analysisIds.add(analysis.getId());
                            }
                        });

        return List.of(
                docIds.size(),
                reqIds.size(),
                itemIds.size(),
                systemIds.size(),
                interfaceIds.size(),
                verificationIds.size(),
                testIds.size(),
                reviewIds.size(),
                analysisIds.size());
    }

    /**
     * Given a file address, opens it in the default user specified app on the platform
     *
     * @param url
     */
    public static void openUrlInDefaultApp(String url) {
        System.out.println("Trying to open URL in default app!");
        try {
            Arp4754ViewsManager.hostServices.showDocument(url);
        } catch (Exception e) {
            System.out.println("ERROR: Failed to open URL in default app!");
            e.printStackTrace();
        }
    }
}
