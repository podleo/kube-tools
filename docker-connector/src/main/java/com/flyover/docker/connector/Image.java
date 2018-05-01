/**
 * 
 */
package com.flyover.docker.connector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;

import com.github.dockerjava.core.command.BuildImageResultCallback;

/**
 * @author mramach
 *
 */
public class Image {

	private DockerContext ctx;
	private Class<?> entrypoint;
	private String imageId;
	
	public Image(DockerContext ctx) {
		this.ctx = ctx;
	}
	
	public Image entrypoint(Class<?> entrypoint) {
		this.entrypoint = entrypoint;
		return this;
	}
	
	public String imageId() {
		return this.imageId;
	}
	
	public Image find(String tag) {
		
		com.github.dockerjava.api.model.Image found = ctx.getClient().listImagesCmd().exec().stream()
			.filter(i -> Arrays.asList(i.getRepoTags()).contains(tag))
				.findFirst().orElseThrow(() -> new RuntimeException(String.format("image %s was not found on the system", tag)));
		
		this.imageId = found.getId();
		
		return this;
		
	}
	
	public Image build() {

		Model model = Maven.getProjectModel();
		
		Path target = Paths.get(model.getBuild().getDirectory(), "docker");
		Path classes = Paths.get(model.getBuild().getDirectory(), "docker", "classes");
		Path lib = Paths.get(model.getBuild().getDirectory(), "docker", "lib");
		
		try {
			
			Files.createDirectories(target);
			Files.createDirectories(classes);
			Files.createDirectories(lib);
			FileUtils.copyDirectory(Paths.get(model.getBuild().getOutputDirectory()).toFile(), classes.toFile());
			Maven.resolveDependencies(model, lib);
			
		} catch (Exception e) {
			throw new RuntimeException("failed while attempting to setup target directory", e);
		}
		
		// construct docker file commands
		Dockerfile df = new Dockerfile("openjdk:8u151-jdk-alpine")
			.run(String.format("mkdir -p /app/lib"))
			.run(String.format("mkdir -p /app/classes"))
			.add("lib", "/app/lib")
			.add("classes", "/app/classes")
			.entrypoint(String.format("java -cp /app/classes:/app/lib/* %s", entrypoint.getName()));
		
		Path dockerfile = target.resolve("Dockerfile");
		
		try {
			
			Files.write(dockerfile, df.getBytes());
			
		} catch (IOException e) {
			throw new RuntimeException("failed while attempting to write docker file", e);
		}
		
		BuildImageResultCallback callback = new BuildImageResultCallback();
		
		try {
			
			ctx.getClient().buildImageCmd(target.toFile())
				.exec(callback).awaitCompletion(10, TimeUnit.MINUTES);
			
			this.imageId = callback.awaitImageId();
			
		} catch (Exception e) {
			throw new RuntimeException("failed while attempting to build docker image", e);
		}
		
		return this;
		
	}
	
	public Tag tag(String name, String tag) {
		
		ctx.getClient().tagImageCmd(imageId, name, tag).withForce(true).exec();
		
		return new Tag(ctx, name, tag);
		
	}
	
	public void remove() {
		
		try {
			
			ctx.getClient().removeImageCmd(imageId()).withForce(true).exec();
			
		} catch (Exception e) {
			throw new RuntimeException("failed while attempting to remove docker image", e);
		}
		
	}

}
