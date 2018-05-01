/**
 * 
 */
package com.flyover.docker.connector.spring;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.flyover.docker.connector.Docker;
import com.flyover.docker.connector.DockerContextConfiguration;
import com.flyover.docker.connector.Image;

/**
 * @author mramach
 *
 */
@EnableDocker
@Configuration
@RunWith(SpringJUnit4ClassRunner.class)
public class EnableDockerTest {
	
	private static final String TAG = UUID.randomUUID().toString();
	
	@Autowired
	private Docker docker;
	
	@Test
	public void testEnableDocker() {
		
		assertNotNull(docker);
		
	}
	
	@Test
	public void testDockerImageBuild() {
		
		Image image = docker.image(Main.class)
				.find(String.format("%s:%s", Main.class.getSimpleName().toLowerCase(), TAG));

		assertNotNull(image);
		
		image.remove();
		
	}

	@Configuration
	public static class CustomDockerImageConfiguration extends AbstractDockerImageConfiguration {
		
		@Value("${docker.username:unittest}")
		private String username;
		@Value("${docker.password}")
		private String password;
		@Value("${docker.registryUrl:https://portr.ctnr.ctl.io/v2}")
		private String registryUrl;
		
		@Override
		public boolean pushImageTag() {
			return false;
		}

		@Override
		public String getImageTag(Class<?> type) {
			return TAG;
		}

		@Override
		public Class<?> getEntrypoint() {
			return Main.class;
		}
		
		@Bean
		public DockerContextConfiguration config() {
			
			DockerContextConfiguration config = new DockerContextConfiguration();
			config.setUsername(username);
			config.setPassword(password);
			config.setRegistryUrl(registryUrl);
			
			return config;
			
		}
		
	}
	
	public static class Main {
		
		public static void main(String[] args) {
			System.out.println("Hello World!");
		}
		
	}
	
}
