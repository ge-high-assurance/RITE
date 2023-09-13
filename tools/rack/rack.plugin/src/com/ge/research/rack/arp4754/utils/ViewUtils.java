/**
 * 
 */
package com.ge.research.rack.arp4754.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ge.research.rack.arp4754.structures.DAPlan;
import com.ge.research.rack.arp4754.structures.Evidence;
import com.ge.research.rack.do178c.structures.Analysis;
import com.ge.research.rack.do178c.structures.DataItem;
import com.ge.research.rack.do178c.structures.Hazard;
import com.ge.research.rack.do178c.structures.PsacNode;
import com.ge.research.rack.do178c.structures.Requirement;
import com.ge.research.rack.do178c.structures.ReviewLog;
import com.ge.research.rack.do178c.structures.Test;

import javafx.scene.paint.Color;

/**
 * @author Saswata Paul
 *
 */
public class ViewUtils {

	
    /** Returns javafx color for a process object */
    public static Color getProcessColor(final DAPlan.Process procObj) {

        if (procObj.isNoData()) {
            return Color.GRAY; // if no data, then GRAY
        }
        if (procObj.isPartialData()) {
            return Color.ORANGE;
        }

        return procObj.isPassed() ? Color.GREEN : Color.RED;       
    }
    
    
    /** Returns javafx color for an objective object */
    public static Color getObjectiveColor(final DAPlan.Objective objObj) {

        if (objObj.isNoData()) {
            return Color.GRAY; // if no data, then GRAY
        }
        if (objObj.isPartialData()) {
            return Color.ORANGE;
        }

        return objObj.isPassed() ? Color.GREEN : Color.RED;       
    }
    
    
    
    /**
     * Computes the numbers of different types of Evidence artifacts present in a process and returns list 
     * @param procObj
     * @return
     */
    public static List<Integer> getProcessArtifactStats(DAPlan.Process procObj) {

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

        
        
        procObj.getObjectives().stream()
                .map(DAPlan.Objective::getOutputs)
                .forEach(
                        objective -> {
                            for (final Evidence doc : objective.getDocumentObjs()) {
                                docIds.add(doc.getId());
                            }
                            for (final Evidence derItemReq : objective.getDerItemReqObjs()) {
                                reqIds.add(derItemReq.getId());
                            }
                            for (final Evidence derSysReq : objective.getDerSysReqObjs()) {
                            	reqIds.add(derSysReq.getId());
                            }
                            for (final Evidence itemReq : objective.getItemReqObjs()) {
                            	reqIds.add(itemReq.getId());
                            }
                            for (final Evidence sysReq : objective.getSysReqObjs()) {
                            	reqIds.add(sysReq.getId());
                            }
                            for (final Evidence item : objective.getItemObjs()) {
                            	itemIds.add(item.getId());
                            }
                            for (final Evidence interfce : objective.getInterfaceObjs()) {
                            	interfaceIds.add(interfce.getId());
                            }
                            for (final Evidence system : objective.getSystemObjs()) {
                            	systemIds.add(system.getId());
                            }
                            for (final Evidence verification : objective.getVerificationObjs()) {
                            	verificationIds.add(verification.getId());
                            }
                            for (final Evidence test : objective.getTestObjs()) {
                            	testIds.add(test.getId());
                            }
                            for (final Evidence review : objective.getReviewObjs()) {
                            	reviewIds.add(review.getId());
                            }
                            for (final Evidence analysis : objective.getAnalysisObjs()) {
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
}