/**
 * 
 */
package com.flyover.docker.connector;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mramach
 *
 */
public class Dockerfile {
	
	private List<String> commands = new LinkedList<>();

	public Dockerfile(String baseImage) {
		commands.add(String.format("FROM %s", baseImage));
	}
	
	public Dockerfile run(String cmd) {
		commands.add(String.format("RUN %s", cmd));
		return this;
	}
	
	public Dockerfile add(String source, String dest) {
		commands.add(String.format("ADD %s %s", source, dest));
		return this;
	}
	
	public Dockerfile env(String name, String value) {
		commands.add(String.format("ENV %s %s", name, value));
		return this;
	}
	
	public Dockerfile entrypoint(String cmd) {
		commands.add(String.format("ENTRYPOINT %s", cmd));
		return this;
	}

	public byte[] getBytes() {

		return commands.stream()
			.collect(Collectors.joining("\n"))
				.getBytes();
		
	}

}
