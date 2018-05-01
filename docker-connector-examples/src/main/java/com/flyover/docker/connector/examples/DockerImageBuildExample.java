/**
 * 
 */
package com.flyover.docker.connector.examples;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.flyover.docker.connector.Docker;
import com.flyover.docker.connector.Image;
import com.flyover.docker.connector.spring.AbstractDockerImageConfiguration;
import com.flyover.docker.connector.spring.EnableDocker;

/**
 * @author mramach
 *
 */
@EnableDocker
@Configuration
public class DockerImageBuildExample {

	private static final String TAG = UUID.randomUUID().toString();
	
	@Bean
	public Example example() {
		return new Example();
	}
	
	public static class Example {
		
		@Autowired
		private Docker docker;
		
		public void execute() {
			
			Image image = null;
			
			try {
				
				// lookup image created by context
				image = docker.image(Main.class).find(
					String.format("%s:%s", Main.class.getSimpleName().toLowerCase(), TAG));
				
			} finally {

				if(image != null) {
					image.remove();
				}
				
			}
			
		}
		
	}

	@Configuration
	public static class CustomDockerImageConfiguration extends AbstractDockerImageConfiguration {

		@Override
		public String getImageTag(Class<?> type) {
			return TAG;
		}

		@Override
		public Class<?> getEntrypoint() {
			return Main.class;
		}
		
	}
	
	public static class Main {
		
		public static void main(String[] args) {
			System.out.println("Hello World!");
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		AnnotationConfigApplicationContext ctx = 
				new AnnotationConfigApplicationContext(DockerImageBuildExample.class);
		
		Example example = ctx.getBean(Example.class);
		
		example.execute();
		
		ctx.close();
		
	}
	
}
