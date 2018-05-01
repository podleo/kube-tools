/**
 * 
 */
package com.flyover.kube.tools.connector.model;

/**
 * @author mramach
 *
 */
public class DeploymentTemplateModel extends Model {
	
	private DeploymentMetadataModel metadata = new DeploymentMetadataModel();
	private PodSpecModel spec = new PodSpecModel();

	public DeploymentMetadataModel getMetadata() {
		return metadata;
	}

	public void setMetadata(DeploymentMetadataModel metadata) {
		this.metadata = metadata;
	}

	public PodSpecModel getSpec() {
		return spec;
	}

	public void setSpec(PodSpecModel spec) {
		this.spec = spec;
	}

}
