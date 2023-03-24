package com.ge.research.rack;
import com.ge.research.rack.utils.RackConsole;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

public class RegenerateManifest extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
        for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
        	if (!project.isOpen()) continue;

        	List<String> manifests = new ArrayList<>();
        	List<String> models = new ArrayList<>();
        	List<String> datas = new ArrayList<>();
        	List<String> nodegroups = new ArrayList<>();

        	IProject[] referencedProjects;
        	try {
        		referencedProjects = project.getReferencedProjects();
        	} catch (CoreException e) {
                RackConsole.getConsole().error(e.toString());
        		return null;
        	}

        	for (IProject referencedProject : referencedProjects) {
        		IFile manifestFile = referencedProject.getFile("manifest.yaml");
        		if (manifestFile.exists()) {
        			manifests.add(manifestFile.getFullPath().makeRelativeTo(project.getFullPath()).toString());
        		}
        	}
        	
        	IResourceProxyVisitor visitor = (IResourceProxy proxy) -> {
        		String name = proxy.getName();
                if (name.equals("data.yaml")) {
                	datas.add(proxy.requestFullPath().makeRelativeTo(project.getFullPath()).toString());
                } else if (name.equals("model.yaml")) {
                	models.add(proxy.requestFullPath().makeRelativeTo(project.getFullPath()).toString());
                } else if (name.equals("store_data.csv")) {
                	nodegroups.add(proxy.requestFullPath().makeRelativeTo(project.getFullPath()).removeLastSegments(1).toString());
                }
        		return true;
        	};

        	try {
	        	project.accept(visitor, 0);
        	} catch (CoreException e) {
                RackConsole.getConsole().error(e.toString());
                return null;
        	}
        	
        	List<Map<String, String>> steps = new ArrayList<>();
        	
        	for (String path : manifests) {
        		steps.add(Collections.singletonMap("manifest", path));
        	}

        	for (String path : models) {
        		steps.add(Collections.singletonMap("model", path));
        	}

        	for (String path : nodegroups) {
        		steps.add(Collections.singletonMap("nodegroups", path));
        	}

        	for (String path : datas) {
        		steps.add(Collections.singletonMap("data", path));
        	}
        	
        	Map<String, Object> content = new LinkedHashMap<>();
        	content.put("name", project.getName());
        	content.put("steps", steps);

        	IFile manifestFile = project.getFile("manifest.yaml");

        	try {
	        	FileWriter writer = new FileWriter(manifestFile.getRawLocation().toString());
	        	DumperOptions options = new DumperOptions();
	        	options.setDefaultFlowStyle(FlowStyle.BLOCK);
	        	Yaml yaml = new Yaml(options);
	        	yaml.dump((Object)content, writer);
        	} catch (IOException e) {
                RackConsole.getConsole().error(e.toString());
        	}
        }
		return null;
	}

}
