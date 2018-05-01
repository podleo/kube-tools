/**
 * 
 */
package com.flyover.docker.connector.spring;


/**
 * @author mramach
 *
 */
public abstract class AbstractDockerImageConfiguration implements DockerImageConfiguration {

	@Override
	public boolean pushImageTag() {
		return false;
	}

	@Override
	public String getImageName(Class<?> type) {
		return type.getSimpleName().toLowerCase();
	}

	@Override
	public String getImageTag(Class<?> type) {
		return String.valueOf(System.currentTimeMillis());
	}

}
