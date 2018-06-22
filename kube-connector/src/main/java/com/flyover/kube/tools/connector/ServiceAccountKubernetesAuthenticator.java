/**
 * 
 */
package com.flyover.kube.tools.connector;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

/**
 * @author mramach
 *
 */
public class ServiceAccountKubernetesAuthenticator implements KubernetesAuthenticator {

	private String path;
	
	public ServiceAccountKubernetesAuthenticator() {
		this("/var/run/secrets/kubernetes.io/serviceaccount/token");
	}
	
	public ServiceAccountKubernetesAuthenticator(String path) {
		this.path = path;
	}

	@Override
	public void configure(RestTemplate restTemplate) {
		
		try {
			
			Path p = Paths.get(path);
			
			String t = IOUtils.toString(new FileInputStream(p.toFile()), "UTF-8");
			
			ClientHttpRequestInterceptor interceptor = (req, body, ex) -> {
				
				req.getHeaders().set("Authorization", String.format("Bearer %s", t.trim()));
				
				return ex.execute(req, body);
				
			};

			restTemplate.getInterceptors().add(interceptor);
			
		} catch (IOException e) {
			throw new RuntimeException("failed while attempting to read token", e);
		}
		
	}

}
