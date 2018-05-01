/**
 * 
 */
package com.flyover.kube.tools.connector;

import com.flyover.kube.tools.connector.model.ContainerModel;
import com.flyover.kube.tools.connector.model.EnvModel;
import com.flyover.kube.tools.connector.model.EnvModel.SecretKeyRefModel;
import com.flyover.kube.tools.connector.model.EnvModel.ValueFromModel;
import com.flyover.kube.tools.connector.model.PortModel;

/**
 * @author mramach
 *
 */
public class Container {
	
	private ContainerModel model;

	public Container(ContainerModel model) {
		this.model = model;
	}
	
	public String name() {
		return this.model.getName();
	}
	
	public Container name(String name) {
		this.model.setName(name);
		return this;
	}
	
	public Container image(String image) {
		this.model.setImage(image);
		return this;
	}
	
	public Container imagePullPolicy(String impagePullPolicy) {
		this.model.setImagePullPolicy(impagePullPolicy);
		return this;
	}
	
	public Container tcpPort(int port) {
		
		PortModel p = new PortModel();
		p.setProtocol("TCP");
		p.setContainerPort(port);
		
		model.getPorts().add(p);
		
		return this;
	}
	
	public Container env(String name, String value) {
		
		EnvModel e = new EnvModel();
		e.setName(name);
		e.setValue(value);
		
		model.getEnv().add(e);
		
		return this;
		
	}
	
	public Container env(String name, String key, Secret secret) {
		
		SecretKeyRefModel secretKeyRef = new SecretKeyRefModel();
		secretKeyRef.setKey(key);
		secretKeyRef.setName(secret.metadata().getName());
		
		ValueFromModel valueFrom = new ValueFromModel();
		valueFrom.setSecretKeyRef(secretKeyRef);

		EnvModel e = new EnvModel();
		e.setName(name);
		e.setValueFrom(valueFrom);
		
		model.getEnv().add(e);
		
		return this;
		
	}

	protected ContainerModel model() {
		return this.model;
	}

}
