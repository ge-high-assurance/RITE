/**
 * 
 */
package com.ge.research.rack.arp4754.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 * 
 * This class stores the configuration data read from project-specific configurations that maps project classes to ARP4754 terminologies
 *
 */
public class Configuration {

    private String derivedItemReq = "";
    private String derivedSysReq = "";
    private String intrface = "";
    private String intrfaceInput = "";
    private String intrfaceOutput = "";    
    private String item = "";
    private String itemReq = "";
    private String sysReq = "";
    private String system = "";
	/**
	 * @return the derivedItemReq
	 */
	public String getDerivedItemReq() {
		return derivedItemReq;
	}
	/**
	 * @param derivedItemReq the derivedItemReq to set
	 */
	public void setDerivedItemReq(String derivedItemReq) {
		this.derivedItemReq = derivedItemReq;
	}
	/**
	 * @return the derivedSysReq
	 */
	public String getDerivedSysReq() {
		return derivedSysReq;
	}
	/**
	 * @param derivedSysReq the derivedSysReq to set
	 */
	public void setDerivedSysReq(String derivedSysReq) {
		this.derivedSysReq = derivedSysReq;
	}
	/**
	 * @return the intrface
	 */
	public String getIntrface() {
		return intrface;
	}
	/**
	 * @param intrface the intrface to set
	 */
	public void setIntrface(String intrface) {
		this.intrface = intrface;
	}
	/**
	 * @return the intrfaceInput
	 */
	public String getIntrfaceInput() {
		return intrfaceInput;
	}
	/**
	 * @param intrfaceInput the intrfaceInput to set
	 */
	public void setIntrfaceInput(String intrfaceInput) {
		this.intrfaceInput = intrfaceInput;
	}
	/**
	 * @return the intrfaceOutput
	 */
	public String getIntrfaceOutput() {
		return intrfaceOutput;
	}
	/**
	 * @param intrfaceOutput the intrfaceOutput to set
	 */
	public void setIntrfaceOutput(String intrfaceOutput) {
		this.intrfaceOutput = intrfaceOutput;
	}
	/**
	 * @return the item
	 */
	public String getItem() {
		return item;
	}
	/**
	 * @param item the item to set
	 */
	public void setItem(String item) {
		this.item = item;
	}
	/**
	 * @return the itemReq
	 */
	public String getItemReq() {
		return itemReq;
	}
	/**
	 * @param itemReq the itemReq to set
	 */
	public void setItemReq(String itemReq) {
		this.itemReq = itemReq;
	}
	/**
	 * @return the sysReq
	 */
	public String getSysReq() {
		return sysReq;
	}
	/**
	 * @param sysReq the sysReq to set
	 */
	public void setSysReq(String sysReq) {
		this.sysReq = sysReq;
	}
	/**
	 * @return the system
	 */
	public String getSystem() {
		return system;
	}
	/**
	 * @param system the system to set
	 */
	public void setSystem(String system) {
		this.system = system;
	}

	
    
}
