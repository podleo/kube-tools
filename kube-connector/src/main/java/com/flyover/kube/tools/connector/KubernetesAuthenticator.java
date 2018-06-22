/**
 * 
 */
package com.flyover.kube.tools.connector;

import org.springframework.web.client.RestTemplate;

/**
 * @author mramach
 *
 */
public interface KubernetesAuthenticator {
	
	void configure(RestTemplate restTemplate);

}
