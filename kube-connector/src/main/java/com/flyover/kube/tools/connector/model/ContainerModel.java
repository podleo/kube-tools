/**
 * 
 */
package com.flyover.kube.tools.connector.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mramach
 *
 */
public class ContainerModel extends Model {
	
	private String name;
	private String image;
	private String imagePullPolicy;
	private List<PortModel> ports = new LinkedList<>();
	private List<EnvModel> env = new LinkedList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<PortModel> getPorts() {
		return ports;
	}

	public void setPorts(List<PortModel> ports) {
		this.ports = ports;
	}

	public List<EnvModel> getEnv() {
		return env;
	}

	public void setEnv(List<EnvModel> env) {
		this.env = env;
	}

	public String getImagePullPolicy() {
		return imagePullPolicy;
	}

	public void setImagePullPolicy(String imagePullPolicy) {
		this.imagePullPolicy = imagePullPolicy;
	}

}
