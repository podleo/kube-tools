/**
 * 
 */
package com.flyover.kube.tools.connector.model;


/**
 * @author mramach
 *
 */
public class DeploymentModel extends KubeModel {

	private DeploymentSpecModel spec = new DeploymentSpecModel();
	private DeploymentStatusModel status = new DeploymentStatusModel();
	
	public DeploymentModel() {
		setApiVersion("extensions/v1beta1");
		setKind("Deployment");
	}

	@Override
	public <T extends KubeModel> void merge(T model) {
		
		DeploymentModel d = (DeploymentModel)model;
		
		super.merge(model);
		setSpec(d.getSpec());
		
	}

	public DeploymentSpecModel getSpec() {
		return spec;
	}

	public void setSpec(DeploymentSpecModel spec) {
		this.spec = spec;
	}

	public DeploymentStatusModel getStatus() {
		return status;
	}

	public void setStatus(DeploymentStatusModel status) {
		this.status = status;
	}
	
}


