/**
 * 
 */
package com.flyover.kube.tools.connector.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author mramach
 *
 */
public class EnvModel extends Model {
	
	private String name;
	@JsonInclude(Include.NON_NULL)
	private String value;
	@JsonInclude(Include.NON_NULL)
	private ValueFromModel valueFrom;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public ValueFromModel getValueFrom() {
		return valueFrom;
	}

	public void setValueFrom(ValueFromModel valueFrom) {
		this.valueFrom = valueFrom;
	}

	public static class ValueFromModel extends Model {
		
		private SecretKeyRefModel secretKeyRef;

		public SecretKeyRefModel getSecretKeyRef() {
			return secretKeyRef;
		}

		public void setSecretKeyRef(SecretKeyRefModel secretKeyRef) {
			this.secretKeyRef = secretKeyRef;
		}
		
	}
	
	public static class SecretKeyRefModel extends Model {
		
		private String name;
		private String key;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}
		
	}

}
