/**
 * 
 */
package com.flyover.kube.tools.connector.spring;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.flyover.kube.tools.connector.Kubernetes;
import com.flyover.kube.tools.connector.KubernetesConfig;

/**
 * @author mramach
 *
 */
@Configuration
@RunWith(SpringJUnit4ClassRunner.class)
public class EnableKubernetesTest {
	
	@Autowired()
	private Kubernetes kube;
	
	@Test
	public void testBasicConfiguration() {
		
		assertNotNull(kube);
		assertEquals("http://10.10.10.10:8080", kube.getConfig().getEndpoint());		
		
	}
	
	@Configuration
	@EnableKubernetes
	public static class Config {
		
		@Bean
		public KubernetesConfig config() {
			
			KubernetesConfig c = new KubernetesConfig();
			c.setEndpoint("http://10.10.10.10:8080");
			
			return c;
			
		}
		
	}

}
