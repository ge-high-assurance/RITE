/**
 * 
 */
package com.ge.research.rack.arp4754.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import com.ge.research.rack.arp4754.constants.ARP4754Queries;
import com.ge.research.rack.arp4754.structures.Configuration;
import com.ge.research.rack.arp4754.utils.DataProcessorUtils;
import com.ge.research.rack.autoGsn.utils.CustomStringUtils;
import com.ge.research.rack.do178c.constants.DO178CQueries;
import com.ge.research.rack.do178c.utils.RackQueryUtils;
import com.ge.research.rack.utils.CSVUtil;

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
		
		System.out.println(configFilePath);
		
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
                if(config[0].equalsIgnoreCase("Interface")) {
                	projectConfig.setIntrface(config[1]);                	
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
	public static Configuration getConfigFromRACK(String rackDir) {

		Configuration projectConfig = new Configuration();
		
		List<String[]> configData  =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                        		ARP4754Queries.All.GET_CONFIG.getQId(), rackDir));
		
		System.out.println(configData.get(0)[0]);
		
        String[] configCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                        		ARP4754Queries.All.GET_CONFIG.getQId(), rackDir));
        int configIdCol = CustomStringUtils.getCSVColumnIndex(configCols, "Configuration");
        int derItemReqIdCol = CustomStringUtils.getCSVColumnIndex(configCols, "derivedItemRequirementAlias");
        int derSysReqIdCol = CustomStringUtils.getCSVColumnIndex(configCols, "derivedSystemRequirementAlias");
        int interfaceIdCol = CustomStringUtils.getCSVColumnIndex(configCols, "interfaceAlias");
        int interfaceInputIdCol = CustomStringUtils.getCSVColumnIndex(configCols, "interfaceInputAlias");
        int interfaceOutputIdCol = CustomStringUtils.getCSVColumnIndex(configCols, "interfaceOutputAlias");
        int itemIdCol = CustomStringUtils.getCSVColumnIndex(configCols, "itemAlias");
        int itemReqIdCol = CustomStringUtils.getCSVColumnIndex(configCols, "itemRequirementAlias");
        int sysReqIdCol = CustomStringUtils.getCSVColumnIndex(configCols, "systemRequirementAlias");
        int systemIdCol = CustomStringUtils.getCSVColumnIndex(configCols, "systemAlias");
		
        projectConfig.setDerivedItemReq(configData.get(0)[derItemReqIdCol]);
        projectConfig.setDerivedSysReq(configData.get(0)[derSysReqIdCol]);
        projectConfig.setIntrface(configData.get(0)[interfaceIdCol]);
        projectConfig.setIntrfaceInput(configData.get(0)[interfaceInputIdCol]);
        projectConfig.setIntrfaceOutput(configData.get(0)[interfaceOutputIdCol]);
        projectConfig.setItem(configData.get(0)[itemIdCol]);
        projectConfig.setItemReq(configData.get(0)[itemReqIdCol]);
        projectConfig.setSysReq(configData.get(0)[sysReqIdCol]);
        projectConfig.setSystem(configData.get(0)[systemIdCol]);
		
		return projectConfig;
	}

	
}
