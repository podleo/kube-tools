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
public class PodSpecModel extends Model {

	private List<ContainerModel> containers = new LinkedList<>();
	private List<ImagePullSecretModel> imagePullSecrets = new LinkedList<>();

	public List<ContainerModel> getContainers() {
		return containers;
	}

	public void setContainers(List<ContainerModel> containers) {
		this.containers = containers;
	}
	
	public List<ImagePullSecretModel> getImagePullSecrets() {
		return imagePullSecrets;
	}

	public void setImagePullSecrets(List<ImagePullSecretModel> imagePullSecrets) {
		this.imagePullSecrets = imagePullSecrets;
	}

	public static class ImagePullSecretModel extends Model {
		
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
	
}
