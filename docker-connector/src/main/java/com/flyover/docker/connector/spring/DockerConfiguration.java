/**
 * 
 */
package com.flyover.docker.connector.spring;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.flyover.docker.connector.Docker;
import com.flyover.docker.connector.DockerContextConfiguration;
import com.flyover.docker.connector.Image;
import com.flyover.docker.connector.Tag;

/**
 * @author mramach
 *
 */
@Configuration
public class DockerConfiguration {

	@Autowired
	private ApplicationContext ctx;
	@Autowired
	private Docker docker;
	
	@Bean
	public Docker docker(@Autowired(required = false) DockerContextConfiguration config) {
		
		if(config == null) {
			config = new DockerContextConfiguration();
		}
		
		return new Docker(config);
		
	}
	
	@PostConstruct
	public void afterPropertiesSet() {
		
		Map<String, DockerImageConfiguration> configurations = 
				ctx.getBeansOfType(DockerImageConfiguration.class);
		
		configurations.values().stream().forEach(this::processDockerImage);
		
	}
	
	private void processDockerImage(DockerImageConfiguration config) {
		
		Class<?> type = config.getEntrypoint();
		
		Image image = docker.image(type).build();
		Tag tag = image.tag(config.getImageName(type), config.getImageTag(type));
		
		if(config.pushImageTag()) {
			tag.push();
		}
		
	}
	
}
