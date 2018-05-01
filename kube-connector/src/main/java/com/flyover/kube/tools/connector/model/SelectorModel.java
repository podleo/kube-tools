/**
 * 
 */
package com.flyover.kube.tools.connector.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author mramach
 *
 */
public class SelectorModel extends Model {
	
	private Map<String, String> matchLabels = new LinkedHashMap<>();

	public Map<String, String> getMatchLabels() {
		return matchLabels;
	}

	public void setMatchLabels(Map<String, String> matchLabels) {
		this.matchLabels = matchLabels;
	}

}
