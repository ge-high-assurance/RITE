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
package com.ge.research.rack.properties;


import com.ge.research.rack.utils.ErrorMessageUtil;
import com.ge.research.rack.utils.ProjectUtils;
import com.ge.research.rack.utils.RackConsole;
import com.ge.research.rack.views.RackPreferencePage;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

// TODO:
// Determine file location of opened file
// Write out edited data
// Read/display other properties: name, steps
// Adjust height, width of list widgets when needed
// Adjust widgets to property window being enlarged, shrunk

public class DataPropertyPage extends PropertyPage {

    private static final String NAME_TITLE = "Name:";
    private static final String PATH_TITLE = "Path:";
    private static final String MODE_TITLE = "&Mode:";
    private static final String KIND_TITLE = "Kind of yaml:";

    private static final int TEXT_FIELD_WIDTH = 100;
    private static final int LABEL_WIDTH = 12;

    private Combo modeCombo;
    private Text detailText;
    
    // Always non-null
    private Map<String, Object> yamlMap = new HashMap<>();
    
    private Composite currentSubcomposite = null;

    /** Constructor for DataPropertyPage. */
    public DataPropertyPage() {
        super();
    }

    /**
     * @see PreferencePage#createContents(Composite)
     */
    protected Control createContents(Composite parent) {
    	var shell = org.eclipse.swt.widgets.Display.getCurrent().getActiveShell();
        IFile file = null;
        if (this.getElement() instanceof IFile f) {
        	file = f;
        }

        var composite = makeComposite(parent, 1);
        
        if (file != null && file.getLocation() != null) {
            yamlMap = readDataYaml(file.getLocation().toString());
        }

        if (yamlMap == null) yamlMap = new HashMap<>();
        var kind = autoDetectYamlKind(yamlMap);
        if (kind == null) {
        	MessageDialog.openInformation(shell, "", "Unknown kind of yaml file. Select a kind manually");
        }
        
        addPathSection(composite);
        addYamlKindSection(composite, kind);
        addSeparator(composite);
        currentSubcomposite = addKindSubcomposite(composite, kind);
        return composite;
    }
    
    private void addYamlKindSection(Composite parent, String kind) {
        final Composite kindComposite = createDefaultComposite(parent);

        final Label kindLabel = new Label(kindComposite, SWT.SINGLE);
        kindLabel.setText(KIND_TITLE);
        final GridData kindLabelGridData = new GridData();
        kindLabelGridData.widthHint = convertWidthInCharsToPixels(LABEL_WIDTH);
        kindLabel.setLayoutData(kindLabelGridData);

        var kindCombo = new Combo(kindComposite, SWT.DROP_DOWN);
        kindCombo.add("Manifest");
        kindCombo.add("Data");
        kindCombo.add("Model");
        if (kind != null) kindCombo.setText(kind);
        kindCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int k = kindCombo.getSelectionIndex();
				String kind = kindCombo.getItems()[k];
				currentSubcomposite.dispose();
				addKindSubcomposite(parent, kind);
				parent.layout();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
        	
        });
    }

    
    public Composite addKindSubcomposite(Composite parent, String kind) {
        Composite subcomp = null;
        switch (kind) {
        default:
        case "Manifest":
            subcomp = createManifestComposite(parent);
            break;
        case "Data":
            subcomp = createDataComposite(parent);
            break;
        case "Model":
            subcomp = createModelComposite(parent);
            break;
        }
        return subcomp;
    }
    
    public Composite createManifestComposite(Composite parent) {
    	var subcomp = makeComposite(parent,1);
        addFirstSection(subcomp);
        addSeparator(subcomp);
        addModeSection(subcomp);
        
        final var subComposite = makeComposite(subcomp, 2);
        addContentSection(subComposite, "model-graphs");
        addContentSection(subComposite, "data-graphs");
        return subcomp;	
    }
    
    public Composite createDataComposite(Composite parent) {
    	var subcomp = makeComposite(parent,1);
    	return subcomp;

    }
    
    public Composite createModelComposite(Composite parent) {
    	var subcomp = makeComposite(parent,1);
    	return subcomp;

    }
    
    private void addPathSection(Composite parent) {
        Composite composite = createDefaultComposite(parent);
        
        // Label for path field
        Label pathLabel = new Label(composite, SWT.NONE);
        pathLabel.setText(PATH_TITLE);

        final GridData pathLabelGridData = new GridData();
        pathLabelGridData.widthHint = convertWidthInCharsToPixels(LABEL_WIDTH);
        pathLabel.setLayoutData(pathLabelGridData);

        // Path text field
        Text pathValueText = new Text(composite, SWT.WRAP);
        pathValueText.setText(((IResource) getElement()).getFullPath().toString());

        final GridData pathValueTextGridData = new GridData();
        pathValueTextGridData.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
        pathValueText.setLayoutData(pathValueTextGridData);
    }

    private void addFirstSection(Composite parent) {
        Composite composite = createDefaultComposite(parent);
        
        // Label for name field
        Label nameLabel = new Label(composite, SWT.NONE);
        nameLabel.setText(NAME_TITLE);

        final GridData labelGridData = new GridData();
        labelGridData.widthHint = convertWidthInCharsToPixels(LABEL_WIDTH);
        nameLabel.setLayoutData(labelGridData);

        // Name text field
        Text nameValueText = new Text(composite, SWT.WRAP);
        if (yamlMap.get("name") instanceof String n) {
        	nameValueText.setText(n);
        }

        final GridData nameValueTextGridData = new GridData();
        nameValueTextGridData.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
        nameValueText.setLayoutData(nameValueTextGridData);

    }

    private void addSeparator(Composite parent) {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        separator.setLayoutData(gridData);
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, String>> getIngestionSteps(Object obj) {
        if (!(obj instanceof Map<?, ?> )) {
            return null;
        }
        final Map<?, ?> dataMap = (Map<?, ?>) obj;

        final Object ingestionStepsObj = dataMap.get("ingestion-steps");
        if (!(ingestionStepsObj instanceof List<?>)) {
            return null;
        }
        final List<?> ingestionStepsList = (List<?>) ingestionStepsObj;

        for (final Object itemObj : ingestionStepsList) {
            if (!(itemObj instanceof Map<?, ?>)) {
                return null;
            }
            final Map<?, ?> itemMap = (Map<?, ?>) itemObj;

            for (final Entry<?, ?> entry : itemMap.entrySet()) {
                if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof String)) {
                    return null;
                }
            }
        }

        return (List<Map<String, String>>) ingestionStepsList;
    }


    public Composite makeComposite(Composite parent, int numColumns) {
        final var subComposite = new Composite(parent, SWT.NONE);
        var layout = new GridLayout(numColumns, true);
        subComposite.setLayout(layout);
        var data = new GridData(GridData.FILL);
        data.grabExcessHorizontalSpace = true;
        subComposite.setLayoutData(data);
        return subComposite;
    }
    
    public void addContentSection(Composite parent, String sectionName) {
        final Composite contentComposite = makeComposite(parent, 1);
//        final Composite contentComposite = new Composite(parent, SWT.NONE);
//        var layout = new GridLayout();
//        contentComposite.setLayout(layout);
//        var data = new GridData(GridData.FILL);
//        data.grabExcessHorizontalSpace = true;
//        contentComposite.setLayoutData(data);

        final Label titleLabel = new Label(contentComposite, SWT.NONE);
        titleLabel.setText("Content sources for the " + sectionName);

        final Composite buttonComposite = makeComposite(contentComposite, 2);
//        final Composite buttonComposite = new Composite(contentComposite, SWT.NONE);
//        var rowlayout = new RowLayout();
//        buttonComposite.setLayout(rowlayout);

        final Button addButton = new Button(buttonComposite,SWT.PUSH);
        addButton.setText("Add");
        final Button removeButton = new Button(buttonComposite,SWT.PUSH);
        removeButton.setText("Remove");
        
        final var list = new org.eclipse.swt.widgets.List(contentComposite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
        GridData gridData = new GridData();
        gridData.widthHint = 200;
        gridData.heightHint = 200;
        list.setLayoutData(gridData);
        
        if (yamlMap.containsKey("footprint")) {

            Object oFootprint = yamlMap.get("footprint");
            if (oFootprint instanceof Map) {
                Map oFootprintMap = (Map) oFootprint;
                if (!oFootprintMap.containsKey(sectionName)) {
                    //dGraphs.add(RackPreferencePage.getDefaultDataGraph());
                } else {
                    @SuppressWarnings("unchecked")
					var items = (List<String>) oFootprintMap.get(sectionName);
                    for (var item: items) list.add(item);
                }
            }
        }

        removeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int k = list.getSelectionIndex();
				if (k != -1) list.remove(k);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e); // FIXME - is this the right thing to do?
			}
        }
        );
        
        addButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
		        InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
		                "", "Enter new item", "", null);
		        if (dlg.open() == Window.OK) {
		        	// User clicked OK; update the label with the input
		        	list.add(dlg.getValue());
//		            var p = list.getSize();
//		            var n = list.getItemCount();
//		            int height = n == 0 ? 200 : (p.y/n)*10;
//		            list.setSize(p.x, 200);
		        }
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
        	
        });
        
    }

    private void addModeSection(Composite parent) {
        final Composite modeComposite = createDefaultComposite(parent);

        final Label modeLabel = new Label(modeComposite, SWT.SINGLE);
        modeLabel.setText(MODE_TITLE);
        final GridData modeLabelGridData = new GridData();
        modeLabelGridData.widthHint = convertWidthInCharsToPixels(LABEL_WIDTH);
        modeLabel.setLayoutData(modeLabelGridData);

        modeCombo = new Combo(modeComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        modeCombo.add("Not Data");
        modeCombo.add("Class URI");
        modeCombo.add("Nodegroup");
        modeCombo.add("Raw OWL");

        final Composite detailComposite = createDefaultComposite(parent);
        final Label detailLabel = new Label(detailComposite, SWT.NONE);
        detailLabel.setText("Unused");

        final GridData detailLabelGridData = new GridData();
        detailLabelGridData.widthHint = convertWidthInCharsToPixels(LABEL_WIDTH);
        detailLabel.setLayoutData(detailLabelGridData);

        modeCombo.addModifyListener(
                (ModifyEvent e) -> {
                    switch (modeCombo.getSelectionIndex()) {
                        case 0:
                            detailLabel.setText("Unused:");
                            break;
                        case 1:
                            detailLabel.setText("Class URI:");
                            break;
                        case 2:
                            detailLabel.setText("Nodegroup:");
                            break;
                        case 3:
                            detailLabel.setText("Unused:");
                            break;
                    }
                });
        modeCombo.select(0);

        detailText = new Text(detailComposite, SWT.SINGLE | SWT.BORDER);
        GridData gd = new GridData();
        gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
        detailText.setLayoutData(gd);

        try {
            final IFile thisFile = (IFile) getElement();
            final IFile dataYamlFile = thisFile.getParent().getFile(new Path("data.yaml"));

            final Yaml yaml = new Yaml();
            final InputStream contents = dataYamlFile.getContents();
            final Object dataObj = yaml.load(contents);
            final List<Map<String, String>> steps = getIngestionSteps(dataObj);

            final String thisName = thisFile.getName();

            for (final Map<String, String> step : steps) {
                if (thisName.equals(step.get("csv"))) {
                    final String classUri = step.get("class");
                    if (classUri != null) {
                        modeCombo.select(1);
                        detailLabel.setText("Class URI:");
                        detailText.setEnabled(true);
                        detailText.setText(classUri);
                        break;
                    }
                    final String nodegroup = step.get("nodegroup");
                    if (nodegroup != null) {
                        modeCombo.select(2);
                        detailLabel.setText("Nodegroup:");
                        detailText.setEnabled(true);
                        detailText.setText(nodegroup);
                        break;
                    }
                } else if (thisName.equals(step.get("owl"))) {
                    modeCombo.select(3);
                    detailLabel.setText("Unused");
                    detailText.setEnabled(false);
                    detailText.setText("");
                }
            }
        } catch (CoreException e) {
            RackConsole.getConsole().error(e.toString());
        }
    }

    private Composite createDefaultComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);

//        GridData data = new GridData();
//        data.grabExcessHorizontalSpace = false;
//        data.verticalAlignment = GridData.FILL;
//        data.horizontalAlignment = GridData.FILL;
//        composite.setLayoutData(data);

        return composite;
    }

    protected void performDefaults() {
        super.performDefaults();
    }

    public boolean performOk() {
        try {
            final IFile thisFile = (IFile) getElement();
            final String thisName = thisFile.getName();
            final IFile dataYamlFile = thisFile.getParent().getFile(new Path("data.yaml"));

            final Yaml yaml = new Yaml();
            final InputStream contents = dataYamlFile.getContents();
            final Object dataObj = yaml.load(contents);
            final List<Map<String, String>> steps = getIngestionSteps(dataObj);

            int existingIndex = -1;
            for (int i = 0; i < steps.size(); i++) {
                if (thisName.equals(steps.get(i).get("csv"))
                        || thisName.equals(steps.get(i).get("owl"))) {
                    existingIndex = i;
                    break;
                }
            }

            final int mode = modeCombo.getSelectionIndex();

            // delete previously existing entry
            if (existingIndex != -1 && mode == 0) {
                steps.remove(existingIndex);

            }
            // add or update a step
            else if (mode != 0) {
                // Either make a new step or reuse the previous one
                final Map<String, String> targetStep;
                if (existingIndex == -1) {
                    targetStep = new LinkedHashMap<>();
                    steps.add(targetStep);
                } else {
                    targetStep = steps.get(existingIndex);
                    targetStep.clear();
                }

                switch (mode) {
                    case 1:
                        targetStep.put("class", detailText.getText());
                        targetStep.put("csv", thisName);
                        break;
                    case 2:
                        targetStep.put("nodegroup", detailText.getText());
                        targetStep.put("csv", thisName);
                        break;
                    case 3:
                        targetStep.put("owl", thisName);
                        break;
                }
            }
            writeDataYaml(dataYamlFile, dataObj);
        } catch (CoreException e) {
            RackConsole.getConsole().error(e.toString());
        } catch (ExecutionException e) {
            RackConsole.getConsole().error(e.toString());
        }

        return true;
    }
    
    static HashMap<String, Object> readDataYaml(String file) {
        File ingestionYaml = new File(file);
        if (!ingestionYaml.exists()) {
            ErrorMessageUtil.warning("No manifest.yaml found, nothing to ingest");
            return null;
        }

        String dir = ingestionYaml.getParent();
        HashMap<String, Object> yamlMap = null;
        try {
            yamlMap = ProjectUtils.readYaml(ingestionYaml.getAbsolutePath());
        } catch (Exception e) {
            ErrorMessageUtil.error("Unable to read " + dir + "/" + ingestionYaml.getName());
            return null;
        }
        if (yamlMap == null) {
            ErrorMessageUtil.error(
                    "Ill formed manifest at "
                            + dir
                            + "/"
                            + ingestionYaml.getName()
                            + ", please check");
            ErrorMessageUtil.error("Check YAML: " + ingestionYaml.getAbsolutePath());
            return null;
        }
        return yamlMap;
    }
    
    // Return null, "Manifest", "Data", or Model"
    static String autoDetectYamlKind(Map<String,Object> yamlMap) {
    	return "Manifest";
    }

    // Replace the manifest.yaml for project using the Java object content.
    static void writeDataYaml(IFile file, Object content) throws ExecutionException {
        // Write YAML to disk
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
            file.setContents(source, force, keep_history, null);
        } catch (CoreException e) {
            throw new ExecutionException("Failed writing final data.yaml output", e);
        }
    }
}
