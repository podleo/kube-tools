/**
 * 
 */
package com.flyover.kube.tools.connector.model;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author mramach
 *
 */
public class ServiceSpecModel extends Model {
	
	private String type = "ClusterIP";
	private List<PortTargetModel> ports = new LinkedList<>();
	private Map<String, String> selector = new LinkedHashMap<>();
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public List<PortTargetModel> getPorts() {
		return ports;
	}

	public void setPorts(List<PortTargetModel> ports) {
		this.ports = ports;
	}

	public Map<String, String> getSelector() {
		return selector;
	}

	public void setSelector(Map<String, String> selector) {
		this.selector = selector;
	}	

}
