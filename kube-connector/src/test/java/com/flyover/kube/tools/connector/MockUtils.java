/**
 * 
 */
package com.flyover.kube.tools.connector;

import java.net.URI;
import java.util.Arrays;

import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flyover.kube.tools.connector.model.ResourceListModel;
import com.flyover.kube.tools.connector.model.ResourceModel;

/**
 * @author mramach
 *
 */
public class MockUtils {
	
	public static UriComponentsBuilder localhost() {
		return UriComponentsBuilder.fromHttpUrl("http://localhost:8080");
	}
	
	public static <T> byte[] write(T value) {
		
		try {

			return new ObjectMapper().writeValueAsBytes(value);
			
		} catch (JsonProcessingException e) {
			throw new RuntimeException("failed to write value to byte array", e);
		}
		
	}
	
	public static ResourceListModel resourceList(ResourceModel...resources) {
		
		ResourceListModel resourceList = new ResourceListModel();
		resourceList.setResources(Arrays.asList(resources));
		
		return resourceList;
		
	}
	
	public static ResourceModel resource(String name, String kind, boolean namespaced) {
		
		ResourceModel resource = new ResourceModel();
		resource.setName(name);
		resource.setKind(kind);
		resource.setNamespaced(namespaced);
		
		return resource;
		
	}
	
	public static class RequestMatchers {
		
		public static RequestMatcher requestTo(URI uri, ThreadLocal<byte[]> capture) {
			
			return (req) -> {
				
				capture.set(req.getBody().toString().getBytes());
				
				AssertionErrors.assertEquals("Unexpected request", uri, req.getURI());
				
			};
			
		}
		
	}

}
