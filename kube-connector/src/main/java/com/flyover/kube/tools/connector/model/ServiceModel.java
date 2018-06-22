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
	
	@Override
	public String checksum() {
		
		try {
		
			ObjectMapper mapper = new ObjectMapper();
			
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(mapper.writeValueAsBytes(getSpec().getType()));
			md.update(mapper.writeValueAsBytes(getSpec().getPorts()));
			md.update(mapper.writeValueAsBytes(getSpec().getSelector()));
			
			return new String(Base64.getEncoder().encodeToString(md.digest()));
			
		} catch (Exception e) {
			throw new RuntimeException("failed to create checksum", e);
		}
		
	}

	public ServiceSpecModel getSpec() {
		return spec;
	}

	public void setSpec(ServiceSpecModel spec) {
		this.spec = spec;
	}
	
}
