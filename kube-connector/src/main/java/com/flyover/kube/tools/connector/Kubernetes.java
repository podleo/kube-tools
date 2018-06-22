/**
 * 
 */
package com.flyover.kube.tools.connector;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.flyover.kube.tools.connector.model.KubeModel;
import com.flyover.kube.tools.connector.model.PathsModel;
import com.flyover.kube.tools.connector.model.ResourceListModel;
import com.flyover.kube.tools.connector.model.ResourceModel;
import com.flyover.kube.tools.connector.model.VersionModel;

/**
 * @author mramach
 *
 */
public class Kubernetes {
	
	private ExecutorService pool = Executors.newFixedThreadPool(4);
	private KubernetesConfig config;
	private RestTemplate restTemplate;

	public Kubernetes() {
		this(new KubernetesConfig());
	}
	
	public Kubernetes(KubernetesConfig config) {
		
		RestTemplate restTemplate = new RestTemplate();
		// apply ssl policy
		config.getSslPolicy().configure(restTemplate);
		// apply authenticator
		config.getAuthenticator().configure(restTemplate);
		
		this.setRestTemplate(restTemplate);
		this.setConfig(config);
		
	}

	public void close() {
		
		pool.shutdown();
		
		try {
		
			pool.awaitTermination(60, TimeUnit.SECONDS);
			
		} catch (InterruptedException e) {
			// log this
		}
		
	}
	
	public KubernetesConfig getConfig() {
		return config;
	}

	public void setConfig(KubernetesConfig config) {
		this.config = config;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public VersionModel version() {
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(config.getEndpoint())
				.path("/version");
		
		return restTemplate.getForObject(builder.build().toUri(), VersionModel.class);
		
	}

	public Namespace namespace(String name) {
		
		Namespace ns = new Namespace(this);
		ns.metadata().setName(name);
		
		return ns;
		
	}
	
	public <T extends KubeModel> T create(T model) {

		ResourceRef ref = resource(model);
		
		if(ref.getResource().isNamespaced()) {
			
			URI uri = UriComponentsBuilder
    			.fromHttpUrl(config.getEndpoint())
    			.path(ref.getPath())
    			.path("/namespaces/")
    			.path(model.getMetadata().getNamespace())
    			.path("/")
    			.path(ref.getResource().getName())
    			.build()
    				.toUri();
			
			return create(uri, model);
			
		} else {
			
			URI uri = UriComponentsBuilder
    			.fromHttpUrl(config.getEndpoint())
    			.path(ref.getPath())
    			.path("/")
    			.path(ref.getResource().getName())
    			.build()
    				.toUri();
			
			return create(uri, model);
			
		}
		
	}
	
	public <T extends KubeModel> T update(T existing, T model) {

		return update(uri(model, resource(model)), existing, model);
		
	}
	
	public <T extends KubeModel> T find(T model) {
		
		return find(uri(model, resource(model)), model);
		
	}
	
	public <T extends KubeModel> void delete(T model) {
		
		delete(uri(model, resource(model)), model);
		
	}

	private <T extends KubeModel> URI uri(T model, ResourceRef ref) {
		
		URI uri = null;
		
		if(ref.getResource().isNamespaced()) {
			
			uri = UriComponentsBuilder
    			.fromHttpUrl(config.getEndpoint())
    			.path(ref.getPath())
    			.path("/namespaces/")
    			.path(model.getMetadata().getNamespace())
    			.path("/")
    			.path(ref.getResource().getName())
    			.path("/")
    			.path(model.getMetadata().getName())
    			.build()
    				.toUri();
			
		} else {
			
			uri = UriComponentsBuilder
    			.fromHttpUrl(config.getEndpoint())
    			.path(ref.getPath())
    			.path("/")
    			.path(ref.getResource().getName())
    			.path("/")
    			.path(model.getMetadata().getName())
    			.build()
    				.toUri();
			
		}
		
		return uri;
		
	}
	
	protected <T> Future<T> execute(Callable<T> task) {
		
		return this.pool.submit(task);
		
	}
	
	@SuppressWarnings("unchecked")
	private <T extends KubeModel> T create(URI uri, T model) {

		model.getMetadata().getAnnotations().put("com.flyover.checksum", model.checksum());
		
		return (T) restTemplate.postForObject(uri, model, model.getClass());
		
	}
	
	@SuppressWarnings("unchecked")
	private <T extends KubeModel> T update(URI uri, T existing, T model) {

		String checksum = existing.getMetadata().getAnnotations().getOrDefault("com.flyover.checksum", "");
		
		if(model.checksum().equals(checksum)) {
			return existing;
		}
		
		System.out.println(String.format("change detected, updating model %s", uri));
		
		model.getMetadata().getAnnotations().put("com.flyover.checksum", model.checksum());
		
		existing.merge(model);
		
		return (T) restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(existing), model.getClass()).getBody();
		
	}
	
	@SuppressWarnings("unchecked")
	private <T extends KubeModel> T find(URI uri, T model) {

		try {
			
			return (T) restTemplate.getForObject(uri, model.getClass());
			
		} catch (HttpClientErrorException e) {
			
			if(HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				return null;
			}
			
			throw e;
			
		}
		
	}
	
	private <T extends KubeModel> void delete(URI uri, T model) {

		try {
			
			restTemplate.delete(uri);
			
		} catch (HttpClientErrorException e) {
			
			if(HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				return; // no action
			}
			
			throw e;
			
		}
		
	}
	
	private String path(KubeModel model) {
		
		// get the api path for the component
		
		URI uri = UriComponentsBuilder
			.fromHttpUrl(config.getEndpoint()).build().toUri();
		
		PathsModel res = restTemplate.getForObject(uri, PathsModel.class); 
		
		return res.getPaths().stream()
			.filter(p -> p.contains(model.getApiVersion()))
			.findFirst()
				.orElseThrow(() -> new RuntimeException(
					String.format("could not determine api path for compoent with apiVersion %s and kind", 
							model.getApiVersion(), model.getKind())));
		
	}
	
	private ResourceRef resource(KubeModel model) {
		
		String path = path(model);
		
		URI uri = UriComponentsBuilder
			.fromHttpUrl(config.getEndpoint())
			.path(path)
			.build()
				.toUri();
		
		ResourceListModel resourceList = restTemplate.getForObject(uri, ResourceListModel.class);
		
		ResourceModel resource = resourceList.getResources().stream()
			.filter(r -> model.getKind().equals(r.getKind()))
			.findFirst()
			.orElseThrow(() -> new RuntimeException(
					String.format("could not find api resource for compoent with apiVersion %s and kind %s", 
							model.getApiVersion(), model.getKind())));
		
		return new ResourceRef(path, resource);
		
	}

	private static class ResourceRef {
		
		private String path;
		private ResourceModel resource;
		
		public ResourceRef(String path, ResourceModel resource) {
			this.path = path;
			this.resource = resource;
		}

		public String getPath() {
			return path;
		}

		public ResourceModel getResource() {
			return resource;
		}
		
	}
	
}
