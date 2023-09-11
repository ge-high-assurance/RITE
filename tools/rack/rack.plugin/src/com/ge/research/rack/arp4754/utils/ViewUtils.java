/**
 * 
 */
package com.ge.research.rack.arp4754.utils;

import com.ge.research.rack.arp4754.structures.DAPlan;
import com.ge.research.rack.do178c.structures.PsacNode;

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
}
