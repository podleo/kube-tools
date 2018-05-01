/**
 * 
 */
package com.flyover.docker.connector;

/**
 * @author mramach
 *
 */
public class DockerContextConfiguration {
	
	private String username;
	private String password;
	private String registryUrl;
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRegistryUrl() {
		return registryUrl;
	}

	public void setRegistryUrl(String registryUrl) {
		this.registryUrl = registryUrl;
	}
}
