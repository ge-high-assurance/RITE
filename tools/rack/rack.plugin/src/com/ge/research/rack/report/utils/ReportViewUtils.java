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

import com.ge.research.rack.autoGsn.utils.CustomStringUtils;
import com.ge.research.rack.report.structures.Analysis;
import com.ge.research.rack.report.structures.DataItem;
import com.ge.research.rack.report.structures.Hazard;
import com.ge.research.rack.report.structures.PsacNode;
import com.ge.research.rack.report.structures.Requirement;
import com.ge.research.rack.report.structures.ReviewLog;
import com.ge.research.rack.report.structures.Test;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Saswata Paul
 */
public class ReportViewUtils {

    /**
     * Assign a given text tooltip to a given javafx node
     *
     * @param node
     * @param text
     */
    public static void assignTooltip(Node node, String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.seconds(0.1));
        tooltip.setStyle("-fx-font-size: 20");
        Tooltip.install(node, tooltip);
    }

    /**
     * Returns javafx color for a given objective object
     *
     * @param node
     * @return
     */
    public static Color getObjectiveColor(PsacNode.Objective objective) {
        if (!objective.getNoData()) { // not no data
            if (objective.getPartialData()) {
                return Color.ORANGE;
            } else if (objective.getPassed()) {
                return Color.GREEN;
            } else if (!objective.getPassed()) {
                return Color.RED;
            }
        } else { // if no data
            if (objective.getMetrics().equals("TBD")) { // if TBD
                return Color.GRAY;
            } else { // if not relevant
                return Color.LIGHTGREY;
            }
        }
        return Color.GRAY; // unhandled case, should not occur
    }

    /**
     * Returns javafx color for a table object
     *
     * @param table
     * @return
     */
    public static Color getTableColor(PsacNode.Table table) {
        Color col = Color.GRAY;
        if (!table.getNoData()) {
            if (table.getPartialData()) {
                return Color.ORANGE;
            } else if (table.getPassed()) {
                return Color.GREEN;
            } else if (!table.getPassed()) {
                return Color.RED;
            }
        }
        return col; // if no data, then GRAY
    }

    /**
     * Given a table object, returns a list of doubles containing the objective metrics in order
     *
     * @param table
     * @return
     */
    public static List<Double> getObjectiveOrder(PsacNode.Table table) {
        List<Double> objStats = new ArrayList<Double>();

        for (PsacNode.Objective obj : table.getTabObjectives()) {
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
     *
     * @param table
     * @return
     */
    public static List<Integer> getTableArtifactStats(PsacNode.Table table) {
        List<Integer> artStats = new ArrayList<Integer>();
        //    	int docCount = 0;
        //    	int reqCount = 0;
        //    	int hzrdCount = 0;
        //    	int tstCount = 0;
        //    	int logCount = 0;
        //    	int anlsCount = 0;

        List<String> docIds = new ArrayList<String>();
        List<String> reqIds = new ArrayList<String>();
        List<String> hzrdIds = new ArrayList<String>();
        List<String> tstIds = new ArrayList<String>();
        List<String> logIds = new ArrayList<String>();
        List<String> anlsIds = new ArrayList<String>();

        for (PsacNode.Objective objective : table.getTabObjectives()) {
            //    		docCount = docCount + objective.getObjOutputs().getDataItems().size();
            //    		reqCount = reqCount + objective.getObjOutputs().getRequirements().size();
            //    		hzrdCount = hzrdCount + objective.getObjOutputs().getHazards().size();
            //    		tstCount = tstCount + objective.getObjOutputs().getTests().size();
            //    		logCount = logCount + objective.getObjOutputs().getLogs().size();
            //    		anlsCount = anlsCount + objective.getObjOutputs().getAnalyses().size();

            if (objective.getObjOutputs().getDocuments() != null) {
                for (DataItem doc : objective.getObjOutputs().getDocuments()) {
                    docIds.add(doc.getId());
                }
            }

            if (objective.getObjOutputs().getRequirements() != null) {
                for (Requirement req : objective.getObjOutputs().getRequirements()) {
                    reqIds.add(req.getId());
                    //        			reqIds.add(req.getDescription()); //Note: using description instead
                    // of ID here because the boeing dataset has multiple descriptions for same req
                    // and we want all charts to show the same number
                }
            }

            if (objective.getObjOutputs().getHazards() != null) {
                for (Hazard hzrd : objective.getObjOutputs().getHazards()) {
                    hzrdIds.add(hzrd.getId());
                }
            }

            if (objective.getObjOutputs().getTests() != null) {
                for (Test tst : objective.getObjOutputs().getTests()) {
                    tstIds.add(tst.getId());
                }
            }

            if (objective.getObjOutputs().getLogs() != null) {
                for (ReviewLog log : objective.getObjOutputs().getLogs()) {
                    logIds.add(log.getId());
                }
            }

            if (objective.getObjOutputs().getAnalyses() != null) {
                for (Analysis anls : objective.getObjOutputs().getAnalyses()) {
                    anlsIds.add(anls.getId());
                }
            }
        }

        // remove duplicates
        docIds = CustomStringUtils.removeDuplicates(docIds);
        reqIds = CustomStringUtils.removeDuplicates(reqIds);
        hzrdIds = CustomStringUtils.removeDuplicates(hzrdIds);
        tstIds = CustomStringUtils.removeDuplicates(tstIds);
        logIds = CustomStringUtils.removeDuplicates(logIds);
        anlsIds = CustomStringUtils.removeDuplicates(anlsIds);

        //    	artStats.add(docCount);
        //    	artStats.add(reqCount);
        //    	artStats.add(hzrdCount);
        //    	artStats.add(tstCount);
        //    	artStats.add(logCount);
        //    	artStats.add(anlsCount);

        artStats.add(docIds.size());
        artStats.add(reqIds.size());
        artStats.add(hzrdIds.size());
        artStats.add(tstIds.size());
        artStats.add(logIds.size());
        artStats.add(anlsIds.size());

        return artStats;
    }

    /**
     * Creates a data bar and adds a value label to the top (only for double values)
     *
     * @param country
     * @param value
     * @return
     */
    public static XYChart.Data createDoubleDataBar(String country, double value) {
        XYChart.Data data = new XYChart.Data(country, value);

        String text = Double.toString(value);

        StackPane node = new StackPane();
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 12));

        //        label.setRotate(-90);
        Group group = new Group(label);
        StackPane.setAlignment(group, Pos.TOP_CENTER);
        //        StackPane.setMargin(group, new Insets(0, 0, 5, 0));
        node.getChildren().add(group);
        data.setNode(node);

        return data;
    }

    /**
     * Creates a data bar and adds a value label to the top (only for int values)
     *
     * @param country
     * @param value
     * @return
     */
    public static XYChart.Data createIntDataBar(String country, int value) {
        XYChart.Data data = new XYChart.Data(country, value);

        String text = Integer.toString(value);

        StackPane node = new StackPane();
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 12));

        //        label.setRotate(-90);
        Group group = new Group(label);
        StackPane.setAlignment(group, Pos.TOP_CENTER);
        //        StackPane.setMargin(group, new Insets(0, 0, 5, 0));
        node.getChildren().add(group);
        data.setNode(node);

        return data;
    }
}
