/**
 * 
 */
package com.flyover.kube.tools.connector;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.flyover.kube.tools.connector.model.DeploymentMetadataModel;
import com.flyover.kube.tools.connector.model.DeploymentModel;
import com.flyover.kube.tools.connector.model.DeploymentSpecModel;
import com.flyover.kube.tools.connector.model.DeploymentTemplateModel;
import com.flyover.kube.tools.connector.model.KubeMetadataModel;
import com.flyover.kube.tools.connector.model.SelectorModel;

/**
 * @author mramach
 *
 */
public class Deployment {
	
	private Kubernetes kube;
	private DeploymentModel model = new DeploymentModel();

	public Deployment(Kubernetes kube) {
		this.kube = kube;
	}
	
	public KubeMetadataModel metadata() {
		return this.model.getMetadata();
	}
	
	public DeploymentSpec spec() {
		return new DeploymentSpec(this.model.getSpec());
	}
	
	public Deployment replicas(int replicas) {
		this.spec().replicas(replicas);
		return this;
	}
	
	public Deployment containers(Container c) {
		this.spec().template().podSpec().containers(c);
		return this;
	}
	
	public Deployment imagePullSecret(Secret s) {
		this.spec().template().podSpec().imagePullSecret(s);
		return this;
	}
	
	public DeploymentModel model() {
		return this.model;
	}
	
	public Deployment merge() {
		
		DeploymentModel found = kube.find(this.model);
		
		if(found == null) {
			this.model = kube.create(this.model);
		} else {
			this.model = kube.update(found, this.model);
		}
		
		return this;
		
	}
	
	public Deployment find() {
	
		this.model = kube.find(this.model);
		
		return this;
		
	}
	
	public void delete() {
		
		kube.delete(this.model);
		
	}
	
	public void ready(int timeout, TimeUnit unit) {

		try {
			
			kube.execute(Executors.callable(() -> {

				while(true) {
					
					DeploymentModel m = kube.find(model);

					// observed generations match
					if(m.getMetadata().getGeneration() == m.getStatus().getObservedGeneration()) {

						// if unavailable replicas is 0 then ready
						if(0 == m.getStatus().getUnavailableReplicas()) {
							break;
						}
						
					}
					
					try { Thread.sleep(5000L); } catch (Exception e) {}
					
				}
				
			})).get(timeout, unit);
			
		} catch (InterruptedException e) {
			throw new RuntimeException("operation interrupted", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("processing error", e);
		} catch (TimeoutException e) {
			throw new RuntimeException("operation timeout");
		}
		
	}
	
	public Service expose(int port) {
		
		Service service = new Service(kube);
		service.metadata().setNamespace(metadata().getNamespace());
		service.metadata().setName(String.format("%s-%s", metadata().getName(), port));
		service.spec().selectors().putAll(spec().selector().getMatchLabels());
		service.spec().tcpPort(port);
		
		return service.merge();
		
	}
	
	public static class DeploymentSpec {
		
		private DeploymentSpecModel model;

		public DeploymentSpec(DeploymentSpecModel model) {
			this.model = model;
		}
		
		public SelectorModel selector() {
			return this.model.getSelector();
		}

		public DeploymentTemplate template() {
			return new DeploymentTemplate(model.getTemplate());
		}

		public void replicas(int replicas) {
			this.model.setReplicas(replicas);
		}
		
	}
	
	public static class DeploymentTemplate {
		
		private DeploymentTemplateModel model;

		public DeploymentTemplate(DeploymentTemplateModel model) {
			this.model = model;
		}

		public PodSpec podSpec() {
			return new PodSpec(model.getSpec());
		}

		public DeploymentMetadataModel metadata() {
			return this.model.getMetadata();
		}
		
	}

	public Deployment selector(String key, String value) {
		
		spec().selector().getMatchLabels().put(key, value);
		spec().template().metadata().getLabels().put(key, value);
		
		return this;
		
	}

}
