/**
 * 
 */
package com.flyover.kube.connector.examples;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.flyover.kube.tools.connector.Deployment;
import com.flyover.kube.tools.connector.Kubernetes;
import com.flyover.kube.tools.connector.KubernetesConfig;
import com.flyover.kube.tools.connector.Namespace;
import com.flyover.kube.tools.connector.spring.EnableKubernetes;

/**
 * @author mramach
 *
 */
@Configuration
@EnableKubernetes
public class DeploymentExample {

	@Autowired
	private Kubernetes kube;
	
	public void execute() {
		
		Namespace ns = kube.namespace("demo").findOrCreate();
		
		try {
			
			// create basic nginx deployment
			Deployment nginx = ns.deployment("nginx", "nginx:latest", 80).merge();
			// wait for deployment to become ready
			nginx.ready(60, TimeUnit.SECONDS);
			// expose deployment via service
			nginx.expose(80);
			
		} finally {

			// delete namespace
			ns.delete();
			
		}
		
	}
	
	@Bean
	public KubernetesConfig config() {
		
		KubernetesConfig c = new KubernetesConfig();
		c.setEndpoint("http://10.50.216.13:8080");
		
		return c;
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		AnnotationConfigApplicationContext ctx = 
				new AnnotationConfigApplicationContext(DeploymentExample.class);
		
		DeploymentExample example = ctx.getBean(DeploymentExample.class);
		
		example.execute();
		
		ctx.close();
		
	}

}
