/**
 * 
 */
package com.flyover.kube.tools.connector;

import static com.flyover.kube.tools.connector.PodSpec.Builders.*;

import com.flyover.kube.tools.connector.model.KubeMetadataModel;
import com.flyover.kube.tools.connector.model.NamespaceModel;

/**
 * @author mramach
 *
 */
public class Namespace {
	
	private Kubernetes kube;
	private NamespaceModel model;

	public Namespace(Kubernetes kube) {
		this.kube = kube;
		this.model = new NamespaceModel();
	}
	
	public KubeMetadataModel metadata() {
		return this.model.getMetadata();
	}

	public Namespace create() {
		
		this.model = kube.create(this.model);
		
		return this;
		
	}
	
	public Namespace findOrCreate() {
		
		NamespaceModel found = kube.find(this.model);
		
		this.model = found != null ? found : kube.create(this.model); 
		
		return this;
		
	}
	
	public void delete() {
		
		kube.delete(model);
		
	}

	public Deployment deployment(String name, String image, int port) {
		
		Deployment d = new Deployment(kube);
		d.metadata().setNamespace(this.model.getMetadata().getName());
		d.metadata().setName(name);
		d.spec().replicas(1);
		d.spec().selector().getMatchLabels().put("key", name);
		d.spec().template().metadata().getLabels().put("key", name);
		d.containers(container(name).image(image).tcpPort(port));
		
		return d;
		
	}

	public Deployment deployment(String name) {
		
		Deployment d = new Deployment(kube);
		d.metadata().setNamespace(this.model.getMetadata().getName());
		d.metadata().setName(name);
		d.spec().replicas(1);
		d.spec().selector().getMatchLabels().put("key", name);
		d.spec().template().metadata().getLabels().put("key", name);
		
		return d;
		
	}
	
	public Secret secret(String name) {
		
		Secret s = new Secret(kube);
		s.metadata().setNamespace(this.model.getMetadata().getName());
		s.metadata().setName(name);
		
		return s;
		
	}

}
