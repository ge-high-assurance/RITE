/** */
package com.ge.research.rack.report;

import com.ge.research.rack.report.boeingPsac.PsacDataProcessorBoeing;

/**
 * FOR TESTING THE BACKEND FUNCTIONS DURING DEVELOPMENT
 *
 * @author Saswata Paul
 */
public class Test_App {

    public static void main(String[] args) {

        String tempDir = "C:\\Users\\212807042\\Desktop\\RACK_tests\\temp_results\\";

        PsacDataProcessorBoeing obj = new PsacDataProcessorBoeing();

        obj.getPSACData(tempDir);
    }
}
