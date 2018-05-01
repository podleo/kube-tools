/**
 * 
 */
package com.flyover.docker.connector;

import com.github.dockerjava.api.DockerClient;

/**
 * @author mramach
 *
 */
public class DockerContext {
	
	private DockerClient client;
	private DockerContextConfiguration config;
	
	public DockerContext(DockerClient client, DockerContextConfiguration config) {
		this.client = client;
		this.config = config;
	}

	public DockerClient getClient() {
		return client;
	}

	public DockerContextConfiguration getConfig() {
		return config;
	}

	public boolean isSecurityEnabled() {
		return config.getUsername() != null;
	}

}
