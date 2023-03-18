package com.ge.research.rack;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

public class RegenerateManifest extends AbstractHandler {

	static final String DEFAULT_MODEL_GRAPH = "http://rack001/model";
	static final String DEFAULT_DATA_GRAPH = "http://rack001/data";
	static final String MANIFEST_YAML_NAME = "manifest.yaml";
	static final String DATA_YAML_NAME = "data.yaml";
	static final String MODEL_YAML_NAME = "model.yaml";
	static final String NODEGROUPS_NAME = "store_data.csv";

	final Set<IProject> visitedProjects = new HashSet<>();
	
	// Open a YAML file and deserialize it as a Java object
	static Object openYamlResource(IResource resource) throws CoreException {
		final IFile file = resource.getProject().getFile(resource.getProjectRelativePath());
		final Yaml yaml = new Yaml();
		return yaml.load(file.getContents());
	}
	
	static void extendFootprintSet(Object object, Set<String> set) {
		if (object instanceof Collection) {
			for (Object entry : (Collection<?>) object) {
				if (entry instanceof String) {
					set.add((String)entry);
				}
			}
		}
	}

	// Generate the list of all relative paths to manifests used by this project
	List<String> getManifests(IProject project, Set<String> modelGraphs, Set<String> dataGraphs) throws ExecutionException {
		final List<String> manifests = new ArrayList<>();
		try {
			final IProject[] referencedProjects = project.getReferencedProjects();
			for (IProject referencedProject : referencedProjects) {
				
				// Update the manifest first if it hasn't been updated yet
				regenerateProjectManifest(referencedProject);
				
				IFile manifestFile = referencedProject.getFile("manifest.yaml");
				if (manifestFile.exists()) {
					manifests.add(manifestFile.getFullPath().makeRelativeTo(project.getFullPath()).toString());
					
					Object manifestObj = openYamlResource(manifestFile);
					if (manifestObj instanceof Map) {
						final Map<?,?> manifest = (Map<?,?>)manifestObj;
						final Object footprintObj = manifest.get("footprint");
						if (footprintObj instanceof Map) {
							final Map<?,?> footprint = (Map<?,?>)footprintObj;
							extendFootprintSet(footprint.get("model-graphs"), modelGraphs);
							extendFootprintSet(footprint.get("data-graphs"), dataGraphs);
						}
					}
				}
			}
		} catch (CoreException e) {
			throw new ExecutionException("Failed to check subproject manifest.yaml", e);
		}
		return manifests;
	}

	// Replace the manifest.yaml for project using the Java object content.
	static void writeManifest(IProject project, Object content) throws ExecutionException {
		// Write YAML to disk
		final IFile manifestFile = project.getFile(MANIFEST_YAML_NAME);
		try {
			// Generate prettier, human readable YAML
			final DumperOptions options = new DumperOptions();
			options.setDefaultFlowStyle(FlowStyle.BLOCK);

			final Yaml yaml = new Yaml(options);
			final CharArrayWriter writer = new CharArrayWriter();
			yaml.dump(content, writer);

			final InputStream source = new ByteArrayInputStream(writer.toString().getBytes());
			final boolean force = false;
			final boolean keep_history = true;
			manifestFile.setContents(source, force, keep_history, null);
		} catch (CoreException e) {
			throw new ExecutionException("Failed writing final manifest.yaml output", e);
		}
	}

	void regenerateProjectManifest(IProject project) throws ExecutionException {

		if (!project.isOpen() || !visitedProjects.add(project)) {
			return;
		}

		final Set<String> modelGraphs = new TreeSet<>();
		final Set<String> dataGraphs = new TreeSet<>();
		final List<String> models = new ArrayList<>();
		final List<String> datas = new ArrayList<>();		
		final List<String> manifests = getManifests(project, modelGraphs, dataGraphs);
		final List<String> nodegroups = new ArrayList<>();

		final IResourceProxyVisitor visitor = (IResourceProxy proxy) -> {
			final String name = proxy.getName();

			// Handle data sets
			if (name.equals("data.yaml")) {
				datas.add(proxy.requestFullPath().makeRelativeTo(project.getFullPath()).toString());

				Object dataYaml = openYamlResource(proxy.requestResource());
				if (dataYaml instanceof Map) {
					Map<?, ?> map = (Map<?, ?>) dataYaml;
					Object dataGraph = map.get("data-graph");

					if (dataGraph == null) {
						dataGraphs.add(DEFAULT_DATA_GRAPH);
					} else if (dataGraph instanceof String) {
						dataGraphs.add((String) dataGraph);
					}
				}

				// Handle model sets
			} else if (name.equals(MODEL_YAML_NAME)) {
				models.add(proxy.requestFullPath().makeRelativeTo(project.getFullPath()).toString());

				Object modelYaml = openYamlResource(proxy.requestResource());
				if (modelYaml instanceof Map) {
					Map<?, ?> map = (Map<?, ?>) modelYaml;
					Object modelGraph = map.get("model-graph");
					if (modelGraph == null) {
						modelGraphs.add(DEFAULT_MODEL_GRAPH);
					} else if (modelGraph instanceof String) {
						modelGraphs.add((String) modelGraph);
					}
				}

				// Handle Nodegroups
			} else if (name.equals(NODEGROUPS_NAME)) {
				nodegroups.add(proxy.requestFullPath().makeRelativeTo(project.getFullPath()).removeLastSegments(1).toString());
			}

			// True means to continue into any children of a directory
			return true;
		};

		try {
			// Recursively walk all the project resources using visitor as defined above
			project.accept(visitor, 0);
		} catch (CoreException e) {
			throw new ExecutionException("Failed traversing project resources", e);
		}

		// Top-level contents of the generated manifest.yaml
		// LinkedHashMap used to ensure output ordering matches generated ordering
		final Map<String, Object> content = new LinkedHashMap<>();

		// name:
		content.put("name", project.getName());

		// footprint:
		final Map<String, Object> footprint = new LinkedHashMap<>();
		content.put("footprint", footprint);
		footprint.put("model-graphs", modelGraphs.toArray());
		footprint.put("data-graphs", dataGraphs.toArray());

		// steps:
		final List<Map<String, String>> steps = new ArrayList<>();
		content.put("steps", steps);

		// all manifest entries first
		for (String path : manifests) {
			steps.add(Collections.singletonMap("manifest", path));
		}

		// all model entries second
		for (String path : models) {
			steps.add(Collections.singletonMap("model", path));
		}

		// all nodegroup entries third
		for (String path : nodegroups) {
			steps.add(Collections.singletonMap("nodegroups", path));
		}

		// all data entries fourth; most importantly after models
		for (String path : datas) {
			steps.add(Collections.singletonMap("data", path));
		}

		writeManifest(project, content);
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Ensure all the projects are visited at least once.
		// Duplicate visits due to references will be skipped.
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			regenerateProjectManifest(project);
		}
		return null;
	}

}