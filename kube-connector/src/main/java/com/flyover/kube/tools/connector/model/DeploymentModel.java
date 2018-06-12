/**
 * 
 */
package com.flyover.kube.tools.connector.model;

import java.security.MessageDigest;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;


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

	@Override
	public String checksum() {
		
		try {
		
			String data = new ObjectMapper().writeValueAsString(spec);
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			return new String(Base64.getEncoder().encodeToString(md.digest(data.getBytes())));
			
		} catch (Exception e) {
			throw new RuntimeException("failed to create checksum", e);
		}
		
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


