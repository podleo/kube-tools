/**
 * 
 */
package com.flyover.docker.connector;

import com.github.dockerjava.api.command.PushImageCmd;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.core.command.PushImageResultCallback;

/**
 * @author mramach
 *
 */
public class Tag {

	private DockerContext ctx;
	private String name;
	private String tag;
	
	public Tag(DockerContext ctx, String name, String tag) {
		this.ctx = ctx;
		this.name = name;
		this.tag = tag;
	}

	public void push() {
		
		PushImageCmd cmd = ctx.getClient().pushImageCmd(String.format("%s:%s", name, tag));
		
		if(ctx.isSecurityEnabled()) {
			cmd = cmd.withAuthConfig(ctx.getClient().authConfig());
		}
		
		try {

			cmd.exec(new PushImageResultCallback()).awaitSuccess();
			
		} catch (DockerClientException e) {
			throw new RuntimeException("failed while attempting to push image", e);
		}
		
	}

}
