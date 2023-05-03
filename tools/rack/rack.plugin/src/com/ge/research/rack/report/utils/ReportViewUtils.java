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
package com.ge.research.rack.report.utils;

import com.ge.research.rack.report.structures.Analysis;
import com.ge.research.rack.report.structures.DataItem;
import com.ge.research.rack.report.structures.Hazard;
import com.ge.research.rack.report.structures.PsacNode;
import com.ge.research.rack.report.structures.Requirement;
import com.ge.research.rack.report.structures.ReviewLog;
import com.ge.research.rack.report.structures.Test;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

/**
 * @author Saswata Paul
 */
public class ReportViewUtils {

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

    /** Returns javafx color for a given objective object */
    public static Color getObjectiveColor(final PsacNode.Objective objective) {

        if (objective.getNoData()) {
            return objective.getMetrics().equals("TBD") ? Color.GRAY : Color.LIGHTGREY;
        }

        if (objective.getPartialData()) {
            return Color.ORANGE;
        }

        return objective.getPassed() ? Color.GREEN : Color.RED;
    }

    /** Returns javafx color for a table object */
    public static Color getTableColor(final PsacNode.Table table) {

        if (table.getNoData()) {
            return Color.GRAY; // if no data, then GRAY
        }
        if (table.getPartialData()) {
            return Color.ORANGE;
        }

        return table.getPassed() ? Color.GREEN : Color.RED;
    }

    /** Given a table object, returns a list of doubles containing the objective metrics in order */
    public static List<Double> getObjectiveOrder(PsacNode.Table table) {

        final List<Double> objStats = new ArrayList<Double>();

        for (final PsacNode.Objective obj : table.getTabObjectives()) {
            if (obj.getMetrics().contains("%")) { // if there is a numeric value
                objStats.add(Double.parseDouble(obj.getMetrics().split("%")[0]));
            } else { // if no numeric value, use -1.0 to differentiate later
                objStats.add(-1.0);
            }
        }

        Collections.sort(objStats); // sort ascending
        Collections.reverse(objStats); // reverse

        return objStats;
    }

    /**
     * Given a table, returns the combined total number of associated artifacts for all objectives
     * in the table
     */
    public static List<Integer> getTableArtifactStats(PsacNode.Table table) {

        final Set<String> docIds = new HashSet<String>();
        final Set<String> reqIds = new HashSet<String>();
        final Set<String> hzrdIds = new HashSet<String>();
        final Set<String> tstIds = new HashSet<String>();
        final Set<String> logIds = new HashSet<String>();
        final Set<String> anlsIds = new HashSet<String>();

        table.getTabObjectives().stream()
                .map(PsacNode.Objective::getObjOutputs)
                .forEach(
                        objective -> {
                            for (final DataItem doc : objective.getDocuments()) {
                                docIds.add(doc.getId());
                            }
                            for (final Requirement req : objective.getRequirements()) {
                                reqIds.add(req.getId());
                            }
                            for (final Hazard hzrd : objective.getHazards()) {
                                hzrdIds.add(hzrd.getId());
                            }
                            for (final Test tst : objective.getTests()) {
                                tstIds.add(tst.getId());
                            }
                            for (final ReviewLog log : objective.getLogs()) {
                                logIds.add(log.getId());
                            }
                            for (final Analysis anls : objective.getAnalyses()) {
                                anlsIds.add(anls.getId());
                            }
                        });

        return List.of(
                docIds.size(),
                reqIds.size(),
                hzrdIds.size(),
                tstIds.size(),
                logIds.size(),
                anlsIds.size());
    }

    /** Creates a data bar and adds a value label to the top (only for double values) */
    public static XYChart.Data<String, Double> createDoubleDataBar(String country, double value) {

        final Label label = new Label(Double.toString(value));
        label.setTextFill(Color.WHITE);
        label.setFont(VERANDA_FONT);

        final Group group = new Group(label);
        StackPane.setAlignment(group, Pos.TOP_CENTER);

        final StackPane node = new StackPane();
        node.getChildren().add(group);

        final XYChart.Data<String, Double> data = new XYChart.Data<String, Double>(country, value);

        data.setNode(node);

        return data;
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

    public static String getSrcLabelText(String src) {
        return String.format("Source: %s", null != src ? src : "Not Found");
    }

    public static ImageView loadGeIcon() throws IOException, URISyntaxException {

        final Bundle bundle = Platform.getBundle(RACK_PLUGIN_BUNDLE_NAME);

        final String imgUrl =
                FileLocator.find(bundle, new Path(GE_LOGO_IMG_PATH), null).toURI().getPath();

        return new ImageView(imgUrl);
    }
}
