/**
 * 
 */
package com.ge.research.rack.arp4754.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.ge.research.rack.arp4754.structures.Configuration;
import com.ge.research.rack.autoGsn.utils.CustomStringUtils;

/**
 * @author Saswata Paul
 *
 * This class has structures and functions needed to read a configuration file and store the data
 *
 */
public class ConfigReader {
		
	
	/**
	 * Gets the config from a .config file
	 * @return
	 */
	public static Configuration getConfigFromFile(String configFilePath) {

		Configuration projectConfig = new Configuration();
		
        try {
            File file = new File(configFilePath); // creates a new file instance
            FileReader fr = new FileReader(file); // reads the file
            BufferedReader br =
                    new BufferedReader(fr); // creates a buffering character input stream
            String line;

    		// Read a file line
            while ((line = br.readLine()) != null) {
//            	System.out.println(line);
                String content = line.trim();
    			// Extract and store info in appropriate field
                String[] config = content.split("\\::");
                
                if(config.length != 2) { // ill formed
                    System.out.println("ERROR: Ill-formed .config file at " + configFilePath);
                    return projectConfig;
                } 
//                System.out.println(config[0] + "--" +config[1]);
                
                if(config[0].equalsIgnoreCase("SystemRequirement")) {
                	projectConfig.setSysReq(config[1]);
                }
                if(config[0].equalsIgnoreCase("ItemRequirement")) {
                	projectConfig.setItemReq(config[1]);                	
                }
                if(config[0].equalsIgnoreCase("DerivedSystemRequirement")) {
                	projectConfig.setDerivedSysReq(config[1]);                	
                }
                if(config[0].equalsIgnoreCase("DerivedItemRequirement")) {
                	projectConfig.setDerivedItemReq(config[1]);                	
                }
                if(config[0].equalsIgnoreCase("InterfaceInput")) {
                	projectConfig.setIntrfaceInput(config[1]);                	
                }
                if(config[0].equalsIgnoreCase("InterfaceOutput")) {
                	projectConfig.setIntrfaceOutput(config[1]);                	
                }
                if(config[0].equalsIgnoreCase("Item")) {
                	projectConfig.setItem(config[1]);                	
                }
                if(config[0].equalsIgnoreCase("System")) {
                	projectConfig.setSystem(config[1]);                	
                }   
            }
            fr.close(); // closes the stream and release the resources

        } catch (Exception e) {
            e.printStackTrace();
        }
		
		
		return projectConfig;
	}
	
	
	
	/**
	 * TODO
	 * Gets the config stored in RACK
	 * @return
	 */
	public Configuration getConfigFromRACK() {

		Configuration projectConfig = new Configuration();
		
		
		
		return projectConfig;
	}

	
}
