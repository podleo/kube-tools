/**
 * 
 */
package com.flyover.kube.tools.connector;

/**
 * @author mramach
 *
 */
public class KubernetesConfig {
	
	private String endpoint = "http://localhost:8080";

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

}
