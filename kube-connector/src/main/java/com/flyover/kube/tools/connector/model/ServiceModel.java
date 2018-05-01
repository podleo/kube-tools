/**
 * 
 */
package com.flyover.kube.tools.connector.model;

/**
 * @author mramach
 *
 */
public class ServiceModel extends KubeModel {

	private ServiceSpecModel spec = new ServiceSpecModel();
	
	public ServiceModel() {
		setApiVersion("v1");
		setKind("Service");
	}	

	@Override
	public <T extends KubeModel> void merge(T model) {
		
		ServiceModel s = (ServiceModel)model;
		
		super.merge(model);
		getSpec().setType(s.getSpec().getType());
		getSpec().setPorts(s.getSpec().getPorts());
		getSpec().setSelector(s.getSpec().getSelector());
		
	}

	public ServiceSpecModel getSpec() {
		return spec;
	}

	public void setSpec(ServiceSpecModel spec) {
		this.spec = spec;
	}
	
}
