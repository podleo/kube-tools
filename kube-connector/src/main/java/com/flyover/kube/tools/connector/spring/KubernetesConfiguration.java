/**
 * 
 */
package com.flyover.kube.tools.connector.spring;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.flyover.kube.tools.connector.Kubernetes;
import com.flyover.kube.tools.connector.KubernetesConfig;

/**
 * @author mramach
 *
 */
@Configuration
public class KubernetesConfiguration {
	
	@Bean
	public Kubernetes kubernetes(@Autowired(required = false) KubernetesConfig config) {
		
		return new Kubernetes(config != null ? config : new KubernetesConfig());
		
	}
	
	@Bean
	public KubernetesLifecycle kubernetesLifecycle() {
		return new KubernetesLifecycle();
	}
	
	public static class KubernetesLifecycle {
		
		@Autowired
		private Kubernetes kube;
		
		@PreDestroy
		public void close() {
			kube.close();			
		}
		
	}
	
}
