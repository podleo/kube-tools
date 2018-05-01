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
public class DeploymentMetadataModel extends Model {
	
	private Map<String, String> labels = new LinkedHashMap<>();

	public Map<String, String> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}

}
