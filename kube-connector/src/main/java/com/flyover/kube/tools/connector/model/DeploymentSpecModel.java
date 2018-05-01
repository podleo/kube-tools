/**
 * 
 */
package com.flyover.kube.tools.connector.model;

/**
 * @author mramach
 *
 */
public class DeploymentSpecModel extends Model {
	
	private int replicas;
	private SelectorModel selector = new SelectorModel();
	private DeploymentTemplateModel template = new DeploymentTemplateModel();

	public DeploymentTemplateModel getTemplate() {
		return template;
	}

	public void setTemplate(DeploymentTemplateModel template) {
		this.template = template;
	}

	public SelectorModel getSelector() {
		return selector;
	}

	public void setSelector(SelectorModel selector) {
		this.selector = selector;
	}

	public int getReplicas() {
		return replicas;
	}

	public void setReplicas(int replicas) {
		this.replicas = replicas;
	}

}
