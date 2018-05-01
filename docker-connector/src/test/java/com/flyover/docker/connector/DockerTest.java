/**
 * 
 */
package com.flyover.docker.connector;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.util.StringUtils;

/**
 * @author mramach
 *
 */
public class DockerTest {
	
	private String username = System.getProperty("docker.username", "unittest");
	private String password = System.getProperty("docker.password");
	private String registryUrl = System.getProperty("docker.registryUrl", "https://portr.ctnr.ctl.io/v2");
	
	@Test
	public void testBuildImage() {
		
		Image image = null;
		
		try {
			
			image = new Docker()
				.image(Main.class)
					.build();
			
			assertNotNull(image);
			assertNotNull(image.imageId());
			
		} finally {
			
			if(image != null && StringUtils.hasText(image.imageId())) {
				image.remove();
			}
			
		}
		
	}
	
	@Test
	public void testPushImage() {
		
		Image image = null;
		
		DockerContextConfiguration config = new DockerContextConfiguration();
		config.setUsername(username);
		config.setPassword(password);
		config.setRegistryUrl(registryUrl);
		
		try {
			
			image = new Docker(config)
				.image(Main.class)
					.build();
			
			Tag tag = image.tag("portr.ctnr.ctl.io/demo/unittest", "latest");
			
			tag.push();
						
		} finally {
			
			if(image != null && StringUtils.hasText(image.imageId())) {
				image.remove();
			}
			
		}
		
	}
	
	public static class Main {
		
	}

}
