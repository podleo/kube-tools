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
public class SecretModel extends KubeModel {
	
	private Map<String, String> data = new LinkedHashMap<>();
	private String type;
	
	public SecretModel() {
		setApiVersion("v1");
		setKind("Secret");
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}	
	
}
