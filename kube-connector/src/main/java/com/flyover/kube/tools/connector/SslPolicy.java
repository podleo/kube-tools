/**
 * 
 */
package com.flyover.kube.tools.connector;

import org.springframework.web.client.RestTemplate;

/**
 * @author mramach
 *
 */
public interface SslPolicy {
	
	void configure(RestTemplate restTemplate);

}
