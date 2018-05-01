/**
 * 
 */
package com.flyover.kube.tools.connector;

import static com.flyover.kube.tools.connector.MockUtils.*;
import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.client.ExpectedCount.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;

import com.flyover.kube.tools.connector.model.NamespaceModel;
import com.flyover.kube.tools.connector.model.PathsModel;
import com.flyover.kube.tools.connector.model.ResourceListModel;
import com.flyover.kube.tools.connector.model.ResourceModel;

/**
 * @author mramach
 *
 */
public class NamespaceTest {

	private static final String NAMESPACE = "test";

	@Test
	public void testCreateNamespace() {
		
		Kubernetes kube = new Kubernetes();
		
		MockRestServiceServer mockServer = 
				MockRestServiceServer.createServer(kube.getRestTemplate());
		
		PathsModel paths = new PathsModel();
		paths.setPaths(Arrays.asList("/api/v1"));
		
		mockServer
			.expect(once(), requestTo(localhost().build().toUri()))
			.andExpect(method(GET))
			.andRespond(withSuccess(write(paths), APPLICATION_JSON));

		ResourceModel resource = new ResourceModel();
		resource.setName("namespaces");
		resource.setKind("Namespace");
		resource.setNamespaced(false);
		
		ResourceListModel resourceList = new ResourceListModel();
		resourceList.setResources(Arrays.asList(resource));
		
		mockServer
			.expect(once(), requestTo(localhost().path("/api/v1").build().toUri()))
			.andExpect(method(GET))
			.andRespond(withSuccess(write(resourceList), APPLICATION_JSON));
		
		NamespaceModel model = new NamespaceModel();
		model.getMetadata().setName(NAMESPACE);
		
		mockServer
			.expect(once(), requestTo(localhost().path("/api/v1/namespaces").build().toUri()))
			.andExpect(method(POST))
			.andRespond(withSuccess(write(model), APPLICATION_JSON));
		
		// actual code we are testing....
		Namespace ns = kube.namespace(NAMESPACE).create();
		
		mockServer.verify();
		
		assertNotNull(ns);
		assertEquals(NAMESPACE, ns.metadata().getName());
		
	}
	
	@Test
	public void testFindOrCreateNamespace() {
		
		Kubernetes kube = new Kubernetes();
		
		MockRestServiceServer mockServer = 
				MockRestServiceServer.createServer(kube.getRestTemplate());
		
		PathsModel paths = new PathsModel();
		paths.setPaths(Arrays.asList("/api/v1"));
		
		mockServer
			.expect(manyTimes(), requestTo(localhost().build().toUri()))
			.andExpect(method(GET))
			.andRespond(withSuccess(write(paths), APPLICATION_JSON));

		mockServer
			.expect(manyTimes(), requestTo(localhost().path("/api/v1").build().toUri()))
			.andExpect(method(GET))
			.andRespond(withSuccess(write(resourceList(resource("namespaces", "Namespace", false))), APPLICATION_JSON));
		
		mockServer
			.expect(once(), requestTo(
					localhost().path("/api/v1/namespaces/{namespace}")
						.buildAndExpand(NAMESPACE).toUri()))
			.andExpect(method(GET))
			.andRespond(withStatus(HttpStatus.NOT_FOUND));
		
		NamespaceModel model = new NamespaceModel();
		model.getMetadata().setName(NAMESPACE);
		
		mockServer
			.expect(once(), requestTo(localhost().path("/api/v1/namespaces").build().toUri()))
			.andExpect(method(POST))
			.andRespond(withSuccess(write(model), APPLICATION_JSON));
		
		// actual code we are testing....
		Namespace ns = kube.namespace(NAMESPACE).findOrCreate();
		
		mockServer.verify();
		
		assertNotNull(ns);
		assertEquals(NAMESPACE, ns.metadata().getName());
		
	}
	
	@Test
	public void testDeleteNamespace() {
		
		Kubernetes kube = new Kubernetes();
		
		MockRestServiceServer mockServer = 
				MockRestServiceServer.createServer(kube.getRestTemplate());
		
		PathsModel paths = new PathsModel();
		paths.setPaths(Arrays.asList("/api/v1"));
		
		mockServer
			.expect(manyTimes(), requestTo(localhost().build().toUri()))
			.andExpect(method(GET))
			.andRespond(withSuccess(write(paths), APPLICATION_JSON));

		mockServer
			.expect(manyTimes(), requestTo(localhost().path("/api/v1").build().toUri()))
			.andExpect(method(GET))
			.andRespond(withSuccess(write(resourceList(resource("namespaces", "Namespace", false))), APPLICATION_JSON));
		
		NamespaceModel model = new NamespaceModel();
		model.getMetadata().setName(NAMESPACE);
		
		mockServer
			.expect(once(), requestTo(
					localhost().path("/api/v1/namespaces/{namespace}")
						.buildAndExpand(NAMESPACE).toUri()))
			.andExpect(method(DELETE))
			.andRespond(withSuccess(write(model), APPLICATION_JSON));
		
		// actual code we are testing....
		kube.namespace(NAMESPACE).delete();
		
		mockServer.verify();
		
	}
	
}
