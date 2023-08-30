/**
 * 
 */
package com.ge.research.rack.arp4754;

import com.ge.research.rack.arp4754.logic.ConfigReader;
import com.ge.research.rack.arp4754.logic.DataProcessor;
import com.ge.research.rack.arp4754.structures.Configuration;

/**
 * @author Saswata Paul
 *
 */
public class TestApp {

    public static void main(String[] args) throws Exception {
    	String configFile = "C://Users/212807042/Desktop/RACK_tests/temp_results/arp4754_dev/dummy.config";
    	String rackDir = "C://Users/212807042/Desktop/RACK_tests/temp_results/arp4754_dev";

    	
//    	Configuration config = ConfigReader.getConfigFromFile(configFile);

    	Configuration config = ConfigReader.getConfigFromRACK(rackDir);

    	
    	System.out.println("Got config");
    	
    	System.out.println(config.getSysReq());
    	System.out.println(config.getItemReq());
    	
//    	String rackDir = "C://Users/212807042/Desktop/RACK_tests/temp_results/arp4754_dev";
//    	String configFile = "dummy.config";
//    	
//    	DataProcessor processor = new DataProcessor();
//    	
//    	processor.getPlanData(rackDir, configFile);
    }

	

}
