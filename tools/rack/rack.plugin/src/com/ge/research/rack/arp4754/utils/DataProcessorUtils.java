/**
 * 
 */
package com.ge.research.rack.arp4754.utils;

import com.ge.research.rack.arp4754.structures.Configuration;

/**
 * @author Saswata Paul
 *
 */
public class DataProcessorUtils {

	/**
	 * Takes a key that represents a variable name and a config file and returns the CSV/query ID for that variable name 
	 * @param key
	 * @param config
	 * @return
	 * TODO: COmplete for all possible variables/verbiage in arp4754
	 */
	public static String getVarCSVID(String key, Configuration config) {
		String id = "";
		switch(key) {
		  case "allDerivedItemRequirement":
			 id = config.getDerivedItemReq(); 
		    break;
		  case "allDerivedSystemRequirement":
			 id = config.getDerivedSysReq(); 
		    break;
		  case "allInterface":
			 id = config.getIntrface(); 
		    break;  
		  case "allInterfaceInput":
			 id = config.getIntrfaceInput(); 
		    break;  
		  case "allInterfaceOutput":
			 id = config.getIntrfaceOutput(); 
		    break;     
		  case "allItem":
			 id = config.getItem(); 
		    break;  
		  case "allSystem":
			 id = config.getSystem(); 
		    break;    
		  case "allItemRequirement":
			 id = config.getItemReq(); 
		    break;    
		  case "allSystemRequirement":
			 id = config.getSysReq(); 
		    break;    
		  case "allInterfaceWithInputOutput":
			 id = config.getIntrface() + "_with_io"; 
		    break;    
		  case "allItemRequirementWIthItem":
			 id = config.getItemReq() +"_with_" + config.getItem(); 
		    break;    
		  case "allSystemRequirementWIthSystem":
			 id = config.getSysReq() +"_with_" + config.getSystem(); 
		    break;      
		  case "allSystemWIthInterface":
			 id = config.getSystem() +"_with_" + config.getIntrface(); 
		    break;      
		  default:
		    id = "";
		    break;
		}
		return id;
	}
	
}
