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
package com.ge.research.rack.arp4754.structures;

/**
 * @author Saswata Paul
 *     <p>This class stores the configuration data read from project-specific configurations that
 *     maps project classes to ARP4754 terminologies
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
    private String systemDesignDescription = "";
    
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
	/**
	 * @return the systemDesignDescription
	 */
	public String getSystemDesignDescription() {
		return systemDesignDescription;
	}
	/**
	 * @param systemDesignDescription the systemDesignDescription to set
	 */
	public void setSystemDesignDescription(String systemDesignDescription) {
		this.systemDesignDescription = systemDesignDescription;
	}
    
    
}
