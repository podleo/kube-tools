/**
 * 
 */
package com.flyover.kube.tools.connector.model;

/**
 * @author mramach
 *
 */
public class DeploymentStatusModel extends Model {

	private int availableReplicas;
	private int observedGeneration;
	private int unavailableReplicas;
	
	public int getAvailableReplicas() {
		return availableReplicas;
	}
	
	public void setAvailableReplicas(int availableReplicas) {
		this.availableReplicas = availableReplicas;
	}
	
	public int getObservedGeneration() {
		return observedGeneration;
	}
	
	public void setObservedGeneration(int observedGeneration) {
		this.observedGeneration = observedGeneration;
	}
	
	public int getUnavailableReplicas() {
		return unavailableReplicas;
	}
	
	public void setUnavailableReplicas(int unavailableReplicas) {
		this.unavailableReplicas = unavailableReplicas;
	}
	
}
