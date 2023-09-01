/**
 * 
 */
package com.ge.research.rack.arp4754.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 * 
 * A class representing Development Assurance Plans for ARP4754
 *
 */
public class DAPlan {

	/**
	 * A package for different types pf outputs that can be associated with an ARP4754 Objective
	 * @author Saswata Paul
	 *
	 */
	public class Output{
		
	    // ARP4754 Output Objects
	    private List<Evidence> derItemReqObjs = new ArrayList<Evidence>();

	    private List<Evidence> derSysReqObjs = new ArrayList<Evidence>();

	    private List<Evidence> interfaceObjs = new ArrayList<Evidence>();

	    private List<Evidence> interfaceInputObjs = new ArrayList<Evidence>();

	    private List<Evidence> interfaceOutputObjs = new ArrayList<Evidence>();

	    private List<Evidence> itemObjs = new ArrayList<Evidence>();

	    private List<Evidence> itemReqObjs = new ArrayList<Evidence>();

	    private List<Evidence> sysReqObjs = new ArrayList<Evidence>();

	    private List<Evidence> systemObjs = new ArrayList<Evidence>();

		/**
		 * @return the derItemReqObjs
		 */
		public List<Evidence> getDerItemReqObjs() {
			return derItemReqObjs;
		}

		/**
		 * @param derItemReqObjs the derItemReqObjs to set
		 */
		public void setDerItemReqObjs(List<Evidence> derItemReqObjs) {
			this.derItemReqObjs = derItemReqObjs;
		}

		/**
		 * @return the derSysReqObjs
		 */
		public List<Evidence> getDerSysReqObjs() {
			return derSysReqObjs;
		}

		/**
		 * @param derSysReqObjs the derSysReqObjs to set
		 */
		public void setDerSysReqObjs(List<Evidence> derSysReqObjs) {
			this.derSysReqObjs = derSysReqObjs;
		}

		/**
		 * @return the interfaceObjs
		 */
		public List<Evidence> getInterfaceObjs() {
			return interfaceObjs;
		}

		/**
		 * @param interfaceObjs the interfaceObjs to set
		 */
		public void setInterfaceObjs(List<Evidence> interfaceObjs) {
			this.interfaceObjs = interfaceObjs;
		}

		/**
		 * @return the interfaceInputObjs
		 */
		public List<Evidence> getInterfaceInputObjs() {
			return interfaceInputObjs;
		}

		/**
		 * @param interfaceInputObjs the interfaceInputObjs to set
		 */
		public void setInterfaceInputObjs(List<Evidence> interfaceInputObjs) {
			this.interfaceInputObjs = interfaceInputObjs;
		}

		/**
		 * @return the interfaceOutputObjs
		 */
		public List<Evidence> getInterfaceOutputObjs() {
			return interfaceOutputObjs;
		}

		/**
		 * @param interfaceOutputObjs the interfaceOutputObjs to set
		 */
		public void setInterfaceOutputObjs(List<Evidence> interfaceOutputObjs) {
			this.interfaceOutputObjs = interfaceOutputObjs;
		}

		/**
		 * @return the itemObjs
		 */
		public List<Evidence> getItemObjs() {
			return itemObjs;
		}

		/**
		 * @param itemObjs the itemObjs to set
		 */
		public void setItemObjs(List<Evidence> itemObjs) {
			this.itemObjs = itemObjs;
		}

		/**
		 * @return the itemReqObjs
		 */
		public List<Evidence> getItemReqObjs() {
			return itemReqObjs;
		}

		/**
		 * @param itemReqObjs the itemReqObjs to set
		 */
		public void setItemReqObjs(List<Evidence> itemReqObjs) {
			this.itemReqObjs = itemReqObjs;
		}

		/**
		 * @return the sysReqObjs
		 */
		public List<Evidence> getSysReqObjs() {
			return sysReqObjs;
		}

		/**
		 * @param sysReqObjs the sysReqObjs to set
		 */
		public void setSysReqObjs(List<Evidence> sysReqObjs) {
			this.sysReqObjs = sysReqObjs;
		}

		/**
		 * @return the systemObjs
		 */
		public List<Evidence> getSystemObjs() {
			return systemObjs;
		}

		/**
		 * @param systemObjs the systemObjs to set
		 */
		public void setSystemObjs(List<Evidence> systemObjs) {
			this.systemObjs = systemObjs;
		}
	    
	    
		
	}

	/**
	 * Processes in a DAP
	 * @author Saswata Paul
	 *
	 */
	public class Process{
		
		
		private String id="";
		private String desc="";
		private List<Objective> objectives = new ArrayList<Objective>();

		
		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * @return the desc
		 */
		public String getDesc() {
			return desc;
		}

		/**
		 * @param desc the desc to set
		 */
		public void setDesc(String desc) {
			this.desc = desc;
		}

		/**
		 * @return the objectives
		 */
		public List<Objective> getObjectives() {
			return objectives;
		}

		/**
		 * @param objectives the objectives to set
		 */
		public void setObjectives(List<Objective> objectives) {
			this.objectives = objectives;
		}
		
		
	}
	
	
	/**
	 * Objectives in a DAP
	 * @author Saswata Paul
	 *
	 */
	public class Objective{
		
		private String id="";
		private String desc="";
		private String applicability="";
		private List<String> queries = new ArrayList<String>();
		private Output outputs = new Output();
		
		
		/**
		 * @return the desc
		 */
		public String getDesc() {
			return desc;
		}
		/**
		 * @param desc the desc to set
		 */
		public void setDesc(String desc) {
			this.desc = desc;
		}
		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}
		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}
		/**
		 * @return the applicability
		 */
		public String getApplicability() {
			return applicability;
		}
		/**
		 * @param applicability the applicability to set
		 */
		public void setApplicability(String applicability) {
			this.applicability = applicability;
		}
		/**
		 * @return the queries
		 */
		public List<String> getQueries() {
			return queries;
		}
		/**
		 * @param queries the queries to set
		 */
		public void setQueries(List<String> queries) {
			this.queries = queries;
		}
		/**
		 * @return the outputs
		 */
		public Output getOutputs() {
			return outputs;
		}
		/**
		 * @param outputs the outputs to set
		 */
		public void setOutputs(Output outputs) {
			this.outputs = outputs;
		}
	}
	
	private String id="";
	private String desc="";
	private String system="";
	private String sysDAL= "";
	private List<Process> processes = new ArrayList<Process>();
	
	
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the sysDAL
	 */
	public String getSysDAL() {
		return sysDAL;
	}
	/**
	 * @param sysDAL the sysDAL to set
	 */
	public void setSysDAL(String sysDAL) {
		this.sysDAL = sysDAL;
	}
	/**
	 * @return the process
	 */
	public List<Process> getProcesses() {
		return processes;
	}
	/**
	 * @param process the process to set
	 */
	public void setProcesses(List<Process> process) {
		processes = process;
	}

	
}
