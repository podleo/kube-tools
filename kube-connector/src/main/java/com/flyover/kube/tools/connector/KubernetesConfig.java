/**
 * 
 */
package com.flyover.kube.tools.connector;

import org.springframework.web.client.RestTemplate;

/**
 * @author mramach
 *
 */
public class KubernetesConfig {
	
	private String endpoint = "http://localhost:8080";
	private SslPolicy sslPolicy = new DefaultSslPolicy();
	private KubernetesAuthenticator authenticator = new ServiceAccountKubernetesAuthenticator();

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public SslPolicy getSslPolicy() {
		return sslPolicy;
	}

	public void setSslPolicy(SslPolicy sslPolicy) {
		this.sslPolicy = sslPolicy;
	}

	public KubernetesAuthenticator getAuthenticator() {
		return authenticator;
	}

	public void setAuthenticator(KubernetesAuthenticator authenticator) {
		this.authenticator = authenticator;
	}

	public static class DefaultSslPolicy implements SslPolicy {

		@Override
		public void configure(RestTemplate restTemplate) {
			// do nothing
		}
		
	}
	
}
