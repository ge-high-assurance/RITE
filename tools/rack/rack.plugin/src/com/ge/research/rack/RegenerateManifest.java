/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2023, General Electric Company and Galois, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ge.research.rack;

import com.ge.research.rack.utils.RackConsole;
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
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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
    static final String COPY_TO_GRAPH_KEY = "copy-to-graph";
    static final String PERFORM_ENTITY_RESOLUTION_KEY = "perform-entity-resolution";

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
                    set.add((String) entry);
                }
            }
        }
    }

    // Generate the list of all relative paths to manifests used by this project
    List<String> getManifests(IProject project, Set<String> modelGraphs, Set<String> dataGraphs)
            throws ExecutionException {

        // getLocation is used instead of getFullPath for manifests because manifests
        // link across project boundaries and are access by tooling that is unaware of
        // the Eclipse workspace structure.

        final List<String> manifests = new ArrayList<>();
        final IPath projectPath = project.getLocation();

        try {
            final IProject[] referencedProjects = project.getReferencedProjects();
            for (IProject referencedProject : referencedProjects) {

                // Update the manifest first if it hasn't been updated yet
                regenerateProjectManifest(referencedProject);

                final IFile manifestFile = referencedProject.getFile(MANIFEST_YAML_NAME);
                if (manifestFile.exists()) {
                    final IPath manifestPath = manifestFile.getLocation();
                    manifests.add(manifestPath.makeRelativeTo(projectPath).toString());

                    final Object manifestObj = openYamlResource(manifestFile);
                    if (manifestObj instanceof Map) {
                        final Map<?, ?> manifest = (Map<?, ?>) manifestObj;
                        final Object footprintObj = manifest.get("footprint");
                        if (footprintObj instanceof Map) {
                            final Map<?, ?> footprint = (Map<?, ?>) footprintObj;
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

        final IFile manifestResource = project.getFile(MANIFEST_YAML_NAME);
        if (!manifestResource.exists()) {
            return;
        }

        final Object manifestYaml;
        try {
            manifestYaml = openYamlResource(manifestResource);
        } catch (CoreException e) {
            throw new ExecutionException("Failed to open preexisting manifest.yaml", e);
        }

        String copyToGraph = null;
        String performEntityResolution = null;

        if (manifestYaml instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) manifestYaml;

            final Object copyToGraphObj = map.get(COPY_TO_GRAPH_KEY);
            if (copyToGraphObj instanceof String) {
                copyToGraph = (String) copyToGraphObj;
            }

            final Object performEntityResolutionObj = map.get(PERFORM_ENTITY_RESOLUTION_KEY);
            if (performEntityResolutionObj instanceof String) {
                performEntityResolution = (String) performEntityResolutionObj;
            }
        }

        final Set<String> modelGraphs = new TreeSet<>();
        final Set<String> dataGraphs = new TreeSet<>();
        final List<String> models = new ArrayList<>();
        final List<String> datas = new ArrayList<>();
        final List<String> manifests = getManifests(project, modelGraphs, dataGraphs);
        final List<String> nodegroups = new ArrayList<>();

        final IResourceProxyVisitor visitor =
                (IResourceProxy proxy) -> {
                    final String name = proxy.getName();

                    // Handle data sets
                    if (name.equals(DATA_YAML_NAME)) {
                        datas.add(
                                proxy.requestFullPath()
                                        .makeRelativeTo(project.getFullPath())
                                        .toString());

                        final Object dataYaml = openYamlResource(proxy.requestResource());
                        if (dataYaml instanceof Map) {
                            final Map<?, ?> map = (Map<?, ?>) dataYaml;
                            final Object dataGraph = map.get("data-graph");

                            if (dataGraph == null) {
                                dataGraphs.add(DEFAULT_DATA_GRAPH);
                            } else if (dataGraph instanceof String) {
                                dataGraphs.add((String) dataGraph);
                            }
                        }

                        // Handle model sets
                    } else if (name.equals(MODEL_YAML_NAME)) {
                        models.add(
                                proxy.requestFullPath()
                                        .makeRelativeTo(project.getFullPath())
                                        .toString());

                        final Object modelYaml = openYamlResource(proxy.requestResource());
                        if (modelYaml instanceof Map) {
                            final Map<?, ?> map = (Map<?, ?>) modelYaml;
                            final Object modelGraph = map.get("model-graph");
                            if (modelGraph == null) {
                                modelGraphs.add(DEFAULT_MODEL_GRAPH);
                            } else if (modelGraph instanceof String) {
                                modelGraphs.add((String) modelGraph);
                            }
                        }

                        // Handle Nodegroups
                    } else if (name.equals(NODEGROUPS_NAME)) {
                        nodegroups.add(
                                proxy.requestFullPath()
                                        .makeRelativeTo(project.getFullPath())
                                        .removeLastSegments(1)
                                        .toString());
                    }

                    // True means to continue into any children of a directory
                    return true;
                };

        try {
            // Recursively walk all the project resources using visitor as defined above
            project.accept(visitor, IContainer.NONE);
        } catch (CoreException e) {
            throw new ExecutionException("Failed traversing project resources", e);
        }

        // Top-level contents of the generated manifest.yaml
        // LinkedHashMap used to ensure output ordering matches generated ordering
        final Map<String, Object> content = new LinkedHashMap<>();

        // name:
        content.put("name", project.getName());

        try {
            final String description = project.getDescription().getComment();
            if (!description.isBlank()) {
                content.put("description", description);
            }
        } catch (CoreException e) {
        }

        // footprint:
        final Map<String, Object> footprint = new LinkedHashMap<>();
        content.put("footprint", footprint);
        if (!modelGraphs.isEmpty()) {
            footprint.put("model-graphs", modelGraphs.toArray());
        }
        if (!dataGraphs.isEmpty()) {
            footprint.put("data-graphs", dataGraphs.toArray());
        }

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

        if (performEntityResolution != null) {
            content.put(PERFORM_ENTITY_RESOLUTION_KEY, performEntityResolution);
        }

        if (copyToGraph != null) {
            content.put(COPY_TO_GRAPH_KEY, copyToGraph);
        }

        writeManifest(project, content);
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // Ensure all the projects are visited at least once.
        // Duplicate visits due to references will be skipped.
        visitedProjects.clear();
        IProject project = HandlerUtils.getCurrentIProject(event);
        regenerateProjectManifest(project);
        RackConsole.getConsole().print("Done");
        return null;
    }
}
