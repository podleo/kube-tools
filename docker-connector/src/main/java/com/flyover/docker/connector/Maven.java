/**
 * 
 */
package com.flyover.docker.connector;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.apache.maven.repository.internal.DefaultArtifactDescriptorReader;
import org.apache.maven.repository.internal.DefaultVersionRangeResolver;
import org.apache.maven.repository.internal.DefaultVersionResolver;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.SettingsUtils;
import org.apache.maven.settings.TrackableBase;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.DependencyGraphTransformer;
import org.sonatype.aether.collection.DependencyManager;
import org.sonatype.aether.collection.DependencySelector;
import org.sonatype.aether.collection.DependencyTraverser;
import org.sonatype.aether.connector.async.AsyncRepositoryConnectorFactory;
import org.sonatype.aether.connector.file.FileRepositoryConnectorFactory;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.impl.ArtifactDescriptorReader;
import org.sonatype.aether.impl.VersionRangeResolver;
import org.sonatype.aether.impl.VersionResolver;
import org.sonatype.aether.impl.internal.DefaultServiceLocator;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManager;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManagerFactory;
import org.sonatype.aether.repository.LocalArtifactRequest;
import org.sonatype.aether.repository.LocalArtifactResult;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.NoLocalRepositoryManagerException;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.graph.manager.ClassicDependencyManager;
import org.sonatype.aether.util.graph.selector.AndDependencySelector;
import org.sonatype.aether.util.graph.selector.ExclusionDependencySelector;
import org.sonatype.aether.util.graph.selector.OptionalDependencySelector;
import org.sonatype.aether.util.graph.selector.ScopeDependencySelector;
import org.sonatype.aether.util.graph.transformer.ChainedDependencyGraphTransformer;
import org.sonatype.aether.util.graph.transformer.ConflictMarker;
import org.sonatype.aether.util.graph.transformer.JavaDependencyContextRefiner;
import org.sonatype.aether.util.graph.transformer.JavaEffectiveScopeCalculator;
import org.sonatype.aether.util.graph.transformer.NearestVersionConflictResolver;
import org.sonatype.aether.util.graph.traverser.FatArtifactTraverser;

import com.jcabi.aether.Aether;



/**
 * @author mramach
 *
 */
public class Maven {
	
	// https://stackoverflow.com/questions/27818659/loading-mavens-settings-xml-for-jcabi-aether-to-use?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
	
	public static Settings SETTINGS = settings();

	public static Model getProjectModel() {

		try {
			
			File projectPomFile = Paths.get("", "pom.xml").toAbsolutePath().toFile();
			
		    RepositorySystemSession session = getRepositorySystemSession();
		    
			DefaultModelResolver resolver = new DefaultModelResolver(
				session, new LinkedList<>(Arrays.asList(central())));
			
			DefaultModelBuildingRequest modelBuildingRequest = new DefaultModelBuildingRequest()
				.setModelResolver(resolver)
				.setPomFile(projectPomFile);

			ModelBuilder modelBuilder = new DefaultModelBuilderFactory().newInstance();
			ModelBuildingResult modelBuildingResult = modelBuilder.build(modelBuildingRequest);

			return modelBuildingResult.getEffectiveModel();
			
		} catch (ModelBuildingException e) {
			throw new RuntimeException("failed to load pom.xml file", e);
		}

	}
	
	public static void resolveDependencies(Model model, Path target) {
		
		String local = settings().getLocalRepository();
		
	    List<RemoteRepository> repos = model.getRepositories().stream()
			.map(r -> new RemoteRepository(r.getId(), r.getLayout(), r.getUrl()))
				.collect(Collectors.toList());
	    
	    Aether aether = new Aether(repos, Paths.get(local).toFile());
	    
		Set<Artifact> artifacts = new LinkedHashSet<>();
	    
		model.getDependencies().stream()
			.filter(d -> !d.isOptional() && !Arrays.asList("test", "provided", "runtime").contains(d.getScope()))
			.forEach(d -> artifacts.addAll(processDependency(aether, d)));
		
		artifacts.stream()
			.forEach(a -> {
				
				Path t = target.resolve(a.getFile().getName());	
				
				try {
				
					Files.copy(a.getFile().toPath(), t, StandardCopyOption.REPLACE_EXISTING);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			});
		
	}
	
	private static List<Artifact> processDependency(Aether aether, org.apache.maven.model.Dependency dependency) {
		
		try {
			
			Artifact artifact = new DefaultArtifact(
					dependency.getGroupId(), 
					dependency.getArtifactId(), 
					dependency.getClassifier(), 
					dependency.getType(), 
					dependency.getVersion());
			
			return aether.resolve(artifact, "compile");
			
		} catch (Exception e) {
			e.printStackTrace();
			return new LinkedList<>();
		}
		
	}
	
	public static RepositorySystemSession getRepositorySystemSession() {
		
		    DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();
		    session.setIgnoreMissingArtifactDescriptor( true );
		    session.setIgnoreInvalidArtifactDescriptor( true );
		    DependencyTraverser depTraverser = new FatArtifactTraverser();
		    session.setDependencyTraverser( depTraverser );

		    DependencyManager depManager = new ClassicDependencyManager();
		    session.setDependencyManager( depManager );

		    DependencySelector depFilter =
		        new AndDependencySelector( new ScopeDependencySelector( "test", "provided" ),
		                                   new OptionalDependencySelector(), new ExclusionDependencySelector() );
		    session.setDependencySelector( depFilter );

		    DependencyGraphTransformer transformer =
		        new ChainedDependencyGraphTransformer( new ConflictMarker(), new JavaEffectiveScopeCalculator(),
		                                               new NearestVersionConflictResolver(),
		                                               new JavaDependencyContextRefiner() );
		    session.setDependencyGraphTransformer( transformer );

		    session.setLocalRepositoryManager( new SimpleLocalRepositoryManager( 
		    		new LocalRepository(SETTINGS.getLocalRepository()).getBasedir() ) );

		    return session;
		}

	private static RepositorySystem getRepositorySystem() {
		
		DefaultServiceLocator locator = new DefaultServiceLocator();
	    locator.addService( VersionResolver.class, DefaultVersionResolver.class );
	    locator.addService( VersionRangeResolver.class, DefaultVersionRangeResolver.class );
	    locator.addService( ArtifactDescriptorReader.class, DefaultArtifactDescriptorReader.class );
	    locator.addService( RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class );
	    locator.addService( RepositoryConnectorFactory.class, FileRepositoryConnectorFactory.class );
	    locator.addService( RepositoryConnectorFactory.class, AsyncRepositoryConnectorFactory.class );

	    return locator.getService( RepositorySystem.class );
		
	}
	
	  public static List<RemoteRepository> getRepositories(Model model) {
		  
		  return model.getRepositories().stream()
		  	.map((r) -> new RemoteRepository(r.getId(), r.getLayout(), r.getUrl()))
		  		.collect(Collectors.toList());
		  
	  }
	  
	  private static RemoteRepository central() {
		  return new RemoteRepository("central", "default", "http://central.maven.org/maven2/");
	  }
	  
	  private static class DefaultModelResolver implements ModelResolver {

		  private RepositorySystemSession session;
		  private List<RemoteRepository> repositories = new LinkedList<>();
		  private Set<String> repositoryIds = new HashSet<>();

		  public DefaultModelResolver(
				  RepositorySystemSession session, 
				  List<RemoteRepository> repositories) {
			  this.session = session;
			  this.repositories = repositories;
		  }
		
		public void addRepository(Repository r) throws InvalidRepositoryException {
			
		    if ( !repositoryIds.add( r.getId() ) ) {
		        return;
		    }
		
		    this.repositories.add(new RemoteRepository(r.getId(), "default", r.getUrl()));
		    
		}

	    public ModelResolver newCopy() {
	        return this;
	    }

	    public ModelSource resolveModel(String groupId, String artifactId, String version) throws UnresolvableModelException {
	    	
	        Artifact pomArtifact = new DefaultArtifact( groupId, artifactId, "", "pom", version );

	        try {
	        	
	            ArtifactRequest request = new ArtifactRequest(); 
	            request.setArtifact(pomArtifact);
				request.setRepositories(repositories);
	            
	            ArtifactResult result = getRepositorySystem().resolveArtifact(session, request);
	            
	            pomArtifact = result.getArtifact();
	            
	        } catch ( ArtifactResolutionException e ) {
	        	
	        	LocalArtifactRequest r = new LocalArtifactRequest();
	        	r.setArtifact(pomArtifact);
				r.setRepositories(repositories);
	        	
	        	try {
					
	        		LocalArtifactResult res = new SimpleLocalRepositoryManagerFactory()
						.newInstance(new LocalRepository(SETTINGS.getLocalRepository())).find(session, r);
					
					return new FileModelSource(res.getFile());
					
				} catch (NoLocalRepositoryManagerException e1) {
					throw new UnresolvableModelException(e.getMessage(), groupId, artifactId, version, e);
				}
	        	
	        }

	        return new FileModelSource(pomArtifact.getFile());
	        
	    }

	}
	  
	    /**
	     * Provide settings from maven.
	     * @return Maven settings.
	     */
	    private static Settings settings() {
	        final SettingsBuilder builder =
	            new DefaultSettingsBuilderFactory().newInstance();
	        final SettingsBuildingRequest request =
	            new DefaultSettingsBuildingRequest();
	        final String user =
	            System.getProperty("org.apache.maven.user-settings");
	        if (user == null) {
	            request.setUserSettingsFile(
	                new File(
	                    new File(
	                        System.getProperty("user.home")
	                    ).getAbsoluteFile(),
	                    "/.m2/settings.xml"
	                )
	            );
	        } else {
	            request.setUserSettingsFile(new File(user));
	        }
	        final String global =
	            System.getProperty("org.apache.maven.global-settings");
	        if (global != null) {
	            request.setGlobalSettingsFile(new File(global));
	        }
	        final SettingsBuildingResult result;
	        try {
	            result = builder.build(request);
	        } catch (final SettingsBuildingException ex) {
	            throw new IllegalStateException(ex);
	        }
	        return invokers(builder, result);
	    }

	    /**
	     * Apply maven invoker settings.
	     * @param builder Settings builder.
	     * @param result User and global settings.
	     * @return User, global and invoker settings.
	     */
	    private static Settings invokers(final SettingsBuilder builder,
	        final SettingsBuildingResult result) {
	        Settings main = result.getEffectiveSettings();
	        final Path path = Paths.get(
	            System.getProperty("user.dir"), "..", "interpolated-settings.xml"
	        );
	        if (path.toFile().exists()) {
	            final DefaultSettingsBuildingRequest irequest =
	                new DefaultSettingsBuildingRequest();
	            irequest.setUserSettingsFile(path.toAbsolutePath().toFile());
	            try {
	                final Settings isettings = builder.build(irequest)
	                    .getEffectiveSettings();
	                SettingsUtils.merge(isettings, main, TrackableBase.USER_LEVEL);
	                main = isettings;
	            } catch (final SettingsBuildingException ex) {
	                throw new IllegalStateException(ex);
	            }
	        }
	        return main;
	    }
	  
}
