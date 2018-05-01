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

import org.junit.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriComponentsBuilder;

import com.flyover.kube.tools.connector.model.VersionModel;

/**
 * @author mramach
 *
 */
public class KubernetesTest {
	
	@Test
	public void testConfig() {
	
		Kubernetes kube = new Kubernetes();
		
		VersionModel res = new VersionModel();
		res.setGitVersion("v1.7.11");

		MockRestServiceServer mockServer = 
				MockRestServiceServer.createServer(kube.getRestTemplate());
		
		mockServer
			.expect(once(), requestTo(localhost().path("/version").build().toUri()))
			.andExpect(method(GET))
			.andRespond(withSuccess(write(res), APPLICATION_JSON));
		
		VersionModel version = kube.version();
		
		mockServer.verify();
		
		assertEquals("v1.7.11", version.getGitVersion());
		
	}
	
	@Test
	public void testCustomConfig() {
		
		Kubernetes kube = new Kubernetes();
		kube.getConfig().setEndpoint("http://10.10.10.10:8080");
		
		MockRestServiceServer mockServer = 
				MockRestServiceServer.createServer(kube.getRestTemplate());
		
		mockServer
			.expect(once(), requestTo(UriComponentsBuilder
				.fromHttpUrl("http://10.10.10.10:8080").path("/version").build().toUri()))
			.andExpect(method(GET))
			.andRespond(withSuccess(write(new VersionModel()), APPLICATION_JSON));
		
		kube.version();
		
		mockServer.verify();
		
	}
	
}
