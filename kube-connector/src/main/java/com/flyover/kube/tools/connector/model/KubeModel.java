/**
 * 
 */
package com.flyover.kube.tools.connector.model;

import java.util.UUID;


/**
 * @author mramach
 *
 */
public class KubeModel extends Model {
	
	private String apiVersion;
	private String kind;
	private KubeMetadataModel metadata = new KubeMetadataModel();
	
	public <T extends KubeModel> void merge(T model) {
		
		getMetadata().getLabels().putAll(model.getMetadata().getLabels());
		getMetadata().getAnnotations().putAll(model.getMetadata().getAnnotations());
		
	}
	
	public String checksum() {
		return UUID.randomUUID().toString();
	}
	
	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public KubeMetadataModel getMetadata() {
		return metadata;
	}

	public void setMetadata(KubeMetadataModel metadata) {
		this.metadata = metadata;
	}

}
