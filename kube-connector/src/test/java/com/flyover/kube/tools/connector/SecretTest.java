/**
 * 
 */
package com.flyover.kube.tools.connector;

import static com.flyover.kube.tools.connector.MockUtils.*;
import static com.flyover.kube.tools.connector.MockUtils.RequestMatchers.*;
import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.client.ExpectedCount.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.util.Arrays;
import java.util.Base64;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.web.client.MockRestServiceServer;

import com.flyover.kube.tools.connector.model.NamespaceModel;
import com.flyover.kube.tools.connector.model.PathsModel;

/**
 * @author mramach
 *
 */
public class SecretTest {
	
	private static final String NAMESPACE = "test";

	@Test
	public void testCreate() {
		
		Kubernetes kube = new Kubernetes();
		
		MockRestServiceServer mockServer = 
			MockRestServiceServer.createServer(kube.getRestTemplate());
		
		setupNamespace(mockServer);
		
		ThreadLocal<byte[]> capture = new ThreadLocal<>();
		
		mockServer
			.expect(requestTo(
					localhost().path("/api/v1/namespaces/{namespace}/secrets")
						.buildAndExpand(NAMESPACE).toUri(), capture))
			.andExpect(method(POST))
			.andRespond((res) -> {
				
				MockClientHttpResponse response = new MockClientHttpResponse(capture.get(), HttpStatus.OK);
				response.getHeaders().put("Content-Type", Arrays.asList(APPLICATION_JSON_VALUE));
				
				return response;
				
			});
		
		Secret secret = kube.namespace(NAMESPACE).findOrCreate()
			.secret("name")
			.data("key", "value")
				.create();
		
		mockServer.verify();
		
		assertNotNull(secret);
		assertEquals(NAMESPACE, secret.metadata().getNamespace());
		assertEquals("name", secret.metadata().getName());
		
		assertTrue(secret.data().containsKey("key"));
		assertEquals(Base64.getEncoder().encodeToString("value".getBytes()), secret.data().get("key"));
		
	}
	
	@Test
	public void testCreateDockerConfig() {
		
		Kubernetes kube = new Kubernetes();
		
		MockRestServiceServer mockServer = 
			MockRestServiceServer.createServer(kube.getRestTemplate());
		
		setupNamespace(mockServer);
		
		ThreadLocal<byte[]> capture = new ThreadLocal<>();
		
		mockServer
			.expect(requestTo(
					localhost().path("/api/v1/namespaces/{namespace}/secrets")
						.buildAndExpand(NAMESPACE).toUri(), capture))
			.andExpect(method(POST))
			.andRespond((res) -> {
				
				MockClientHttpResponse response = new MockClientHttpResponse(capture.get(), HttpStatus.OK);
				response.getHeaders().put("Content-Type", Arrays.asList(APPLICATION_JSON_VALUE));
				
				return response;
				
			});
		
		Secret secret = kube.namespace(NAMESPACE).findOrCreate()
			.secret("name")
			.dockerconfigjson("registry", "username", "password")
				.create();
		
		mockServer.verify();
		
		assertNotNull(secret);
		assertEquals(NAMESPACE, secret.metadata().getNamespace());
		assertEquals("name", secret.metadata().getName());
		
		assertEquals("kubernetes.io/dockerconfigjson", secret.type());
		assertTrue(secret.data().containsKey(".dockerconfigjson"));
		
	}
	
	@Test
	public void testReplace() {
		
		Kubernetes kube = new Kubernetes();
		
		MockRestServiceServer mockServer = 
			MockRestServiceServer.createServer(kube.getRestTemplate());
		
		setupNamespace(mockServer);
		
		ThreadLocal<byte[]> capture = new ThreadLocal<>();

		mockServer
		.expect(requestTo(
				localhost().path("/api/v1/namespaces/{namespace}/secrets/{name}")
					.buildAndExpand(NAMESPACE, "name").toUri(), capture))
		.andExpect(method(DELETE))
		.andRespond((res) -> {
			
			MockClientHttpResponse response = new MockClientHttpResponse(capture.get(), HttpStatus.OK);
			response.getHeaders().put("Content-Type", Arrays.asList(APPLICATION_JSON_VALUE));
			
			return response;
			
		});
		
		mockServer
			.expect(requestTo(
					localhost().path("/api/v1/namespaces/{namespace}/secrets")
						.buildAndExpand(NAMESPACE).toUri(), capture))
			.andExpect(method(POST))
			.andRespond((res) -> {
				
				MockClientHttpResponse response = new MockClientHttpResponse(capture.get(), HttpStatus.OK);
				response.getHeaders().put("Content-Type", Arrays.asList(APPLICATION_JSON_VALUE));
				
				return response;
				
			});
		
		Secret secret = kube.namespace(NAMESPACE).findOrCreate()
			.secret("name")
			.data("key", "value")
				.replace();
		
		mockServer.verify();
		
		assertNotNull(secret);
		assertEquals(NAMESPACE, secret.metadata().getNamespace());
		assertEquals("name", secret.metadata().getName());
		
		assertTrue(secret.data().containsKey("key"));
		assertEquals(Base64.getEncoder().encodeToString("value".getBytes()), secret.data().get("key"));
		
	}
	
	private void setupNamespace(MockRestServiceServer mockServer) {
		
		PathsModel paths = new PathsModel();
		paths.setPaths(Arrays.asList(
			"/api/v1", 
			"/apis/extensions/v1beta1"));
		
		mockServer
			.expect(manyTimes(), requestTo(localhost().build().toUri()))
			.andExpect(method(GET))
			.andRespond(withSuccess(write(paths), APPLICATION_JSON));
		
		mockServer
			.expect(manyTimes(), requestTo(localhost().path("/api/v1").build().toUri()))
			.andExpect(method(GET))
			.andRespond(withSuccess(write(resourceList(
					resource("namespaces", "Namespace", false),
					resource("services", "Service", true),
					resource("secrets", "Secret", true))), APPLICATION_JSON));

		NamespaceModel model = new NamespaceModel();
		model.getMetadata().setName(NAMESPACE);
		
		mockServer
			.expect(manyTimes(), requestTo(
					localhost().path("/api/v1/namespaces/{namespace}")
						.buildAndExpand(NAMESPACE).toUri()))
			.andExpect(method(GET))
			.andRespond(withSuccess(write(model), APPLICATION_JSON));

	}

}
