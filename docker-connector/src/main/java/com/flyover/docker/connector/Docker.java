/**
 * 
 */
package com.flyover.docker.connector;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

/**
 * @author mramach
 *
 */
public class Docker {

	private DockerClient client;
	private DockerContextConfiguration config;
	
	public Docker() {
		this(new DockerContextConfiguration());
	}
	
	public Docker(DockerContextConfiguration config) {
		
		DockerClientConfig dockerConfig = DefaultDockerClientConfig
				.createDefaultConfigBuilder()
				.withDockerConfig(null)
				.withRegistryUsername(config.getUsername())
				.withRegistryPassword(config.getPassword())
				.withRegistryUrl(config.getRegistryUrl())
					.build();
		
		this.config = config;
		this.client = DockerClientBuilder.getInstance(dockerConfig).build();
		
	}
	
	public Image image(Class<?> entrypoint) {
		
		return new Image(new DockerContext(client, config)).entrypoint(entrypoint);
		
	}
	
	public Image image() {
		return new Image(new DockerContext(client, config));
	}

}
