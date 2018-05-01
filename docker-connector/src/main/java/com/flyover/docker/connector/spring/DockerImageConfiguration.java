/**
 * 
 */
package com.flyover.docker.connector.spring;


/**
 * @author mramach
 *
 */
public interface DockerImageConfiguration {

	public boolean pushImageTag();
	
	public String getImageName(Class<?> type);
	
	public String getImageTag(Class<?> type);
	
	public Class<?> getEntrypoint();

}