/**
 * 
 */
package com.ge.research.rack.arp4754.constants;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Saswata Paul
 * 
 *
 */
public class ARP4754Queries {
	
	/**
	 * 
	 * @author Saswata Paul
	 *
	 * Only ARP4754 generic Query IDs will be stored here. 
	 * The project-specific Query IDS will be generated dynamically using the configuration.
	 */
	public enum All {
        GET_DAP("getDAP"),
        GET_CONFIG("getConfiguration");

        private String qId;

        All(String id) {
            this.qId = id;
        }

        public String getQId() {
            return this.qId;
        }

        /**
         * To get all query Ids as a list of strings
         *
         * @return
         */
        public static List<String> getAllQueries() {
            List<String> actIds =
                    Stream.of(All.values()).map(All::getQId).collect(Collectors.toList());

            return actIds;
        }
	}
}
