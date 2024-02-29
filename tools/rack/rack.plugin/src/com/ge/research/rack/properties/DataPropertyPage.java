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

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.resources.WorkspaceRoot;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.PropertyPage;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

// TODO:

// Page needs an overall scrollbar
// Figure out defaultWidgetSelected
// Make custom SelectionListener
// Lists of steps (2 places) needs scrollbars
// remove multiple selected items from lists
// Write out edited data
// Read/display other properties: name, steps
// Adjust height, width of list widgets when needed
// Adjust widgets to property window being enlarged, shrunk
// Add file or other browsers for locations of things

// Data style: datagraphs and model graphs 
// Data style: count/nodegroup/constraints
// Data style: fill in ingestion steps from yaml -- for all styles

// Manifest: needs steps

public class DataPropertyPage extends PropertyPage {

    private static final String NAME_TITLE = "Name:";
    private static final String PATH_TITLE = "Path:";
    private static final String KIND_TITLE = "Kind of yaml:";

    private static final int TEXT_FIELD_WIDTH = 100;
    private static final int LABEL_WIDTH = 12;

    private Shell shell;
    
    private Map<String, Object> yamlMap = new HashMap<>();
    
    private Combo kindCombo;
    private Text pathText;
    private Map<String, Widget> yamlWidgets = new HashMap<>();
    
    private Composite currentSubcomposite = null;

    /** Constructor for DataPropertyPage. */
    public DataPropertyPage() {
        super();
    }

    /**
     * @see PreferencePage#createContents(Composite)
     */
    protected Control createContents(Composite parent) {
    	shell = org.eclipse.swt.widgets.Display.getCurrent().getActiveShell();
        IFile file = null;
        if (this.getElement() instanceof IFile f) {
        	file = f;
        }
        
        var composite = addComposite(parent, 1);
        try {

        	if (file != null && file.getLocation() != null) {
        		yamlMap = readYaml(file.getLocation().toString());
        	}

        	if (yamlMap == null) yamlMap = new HashMap<>(); // Already gave an error message
        	var kind = autoDetectYamlKind(yamlMap);

        	addPathSection(composite);
        	addYamlSelectorSection(composite, kind);
        	addSeparator(composite);

        	//var scrolled = new ScrolledComposite(composite, SWT.V_SCROLL|SWT.H_SCROLL);
        	currentSubcomposite = addKindSubcomposite(composite, kind);
        	//scrolled.setContent(currentSubcomposite);

        	// This is some unit testing that retrieved yaml from the bunch of widgets is the same as what went in
        	var newYamlMap = collectYaml();
        	boolean b = Objects.equals(yamlMap, newYamlMap);
        	String diffs = compareYaml(yamlMap, newYamlMap);
        	if (!b || !diffs.isEmpty()) MessageDialog.openInformation(shell, "Compare", "Maps are " + (b?"":"NOT ") + "the same\n" + diffs);

        } catch (Exception e) {
        	
        	MessageDialog.openError(shell,  "Exception", 
        			"Exception during createContents\n"+ e.getMessage() + "\n" + getStack(e));
        }
    	return composite;
    }
    
    private void addPathSection(Composite parent) {
        Composite composite = addCompositeUnequal(parent, 2);
        // Label for path field
        addLabel(composite, PATH_TITLE, LABEL_WIDTH);
        // Path text field
        pathText = addText(composite, ((IResource) getElement()).getFullPath().toString(), TEXT_FIELD_WIDTH);
    }
    
    private void addYamlSelectorSection(Composite parent, String kind) {
        final Composite kindComposite = addCompositeUnequal(parent, 2);

        addLabel(kindComposite, KIND_TITLE, LABEL_WIDTH);

        kindCombo = new Combo(kindComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        kindCombo.add("");
        kindCombo.add("Manifest");
        kindCombo.add("Data");
        kindCombo.add("Model");
        kindCombo.select(0);
        if (kind != null) kindCombo.setText(kind);
        kindCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int k = kindCombo.getSelectionIndex();
				String kind = kindCombo.getItems()[k];

		        if (currentSubcomposite != null) currentSubcomposite.dispose();

		        //var scrolled = new ScrolledComposite(parent, SWT.V_SCROLL|SWT.H_SCROLL);
		        currentSubcomposite = addKindSubcomposite(parent, kind);
		        //scrolled.setContent(currentSubcomposite);

				parent.layout(true, true);
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
        	subcomp = addComposite(parent, 1);
        	addLabel(subcomp, "Choose a kind of yaml file", 30);
        	break;
        case "Manifest":
            subcomp = addManifestComposite(parent);
            break;
        case "Data":
            subcomp = addDataComposite(parent);
            break;
        case "Model":
            subcomp = addModelComposite(parent);
            break;
        }
        return subcomp;
    }
    
    public Composite addManifestComposite(Composite parent) {
    	var subcomp = addComposite(parent,1);
        addManifestNameSection(subcomp);
        addSeparator(subcomp);
        
        final var subComposite = addComposite(subcomp, 3);
        addContentSection(subComposite, "model-graphs", "Manifest");
        addContentSection(subComposite, "data-graphs", "Manifest");
        addContentSection(subComposite, "nodegroups", "Manifest");
        
        addManifestStepsSection(subcomp);
        return subcomp;	
    }
    
    public Composite addDataComposite(Composite parent) {
    	var subcomp = addComposite(parent,1);
    	var dg = addCompositeUnequal(subcomp,2);
    	addLabel(dg, "data-graph", LABEL_WIDTH);
    	var value = (String)yamlMap.get("data-graph");
    	addText(dg, value == null ? "" : value, TEXT_FIELD_WIDTH);
    	makeIngestionStepsComposite(subcomp);
    	return subcomp;

    }
    
    public Composite addModelComposite(Composite parent) {
    	var subcomp = addComposite(parent,1);
    	var dg = addCompositeUnequal(subcomp,2);
        addContentSection(dg, "files", "Model");
        addContentSection(dg, "model-graphs", "Model");
        return subcomp;	

    }
    
    private Label addLabel(Composite parent, String text, int width) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(text);

        final GridData gridData = new GridData();
        gridData.widthHint = convertWidthInCharsToPixels(width);
        label.setLayoutData(gridData);
        return label;
    }
    
    private Text addText(Composite parent, String initialText, int fieldWidth) {
        Text text = new Text(parent, SWT.WRAP);
        text.setText(initialText);

        final GridData gridData = new GridData();
        gridData.widthHint = convertWidthInCharsToPixels(fieldWidth);
        text.setLayoutData(gridData);
        return text;
    }

    private void addManifestNameSection(Composite parent) {
        Composite composite = addCompositeUnequal(parent, 2);
        int WIDTH3 = 20;
        addLabel(composite, NAME_TITLE, WIDTH3);
        String value = (yamlMap.get("name") instanceof String n) ? n : "";
        Text t = addText(composite, value, TEXT_FIELD_WIDTH);
        yamlWidgets.put("name", t);

        addLabel(composite, "Description:", WIDTH3);
        value = (yamlMap.get("description") instanceof String n) ? n : "";
        addText(composite, value, TEXT_FIELD_WIDTH);
        yamlWidgets.put("description", t);

        addLabel(composite, "Copy-to-graph:", WIDTH3);
        value = (yamlMap.get("copy-to-graph") instanceof String n) ? n : "";
        addText(composite, value, TEXT_FIELD_WIDTH);
        yamlWidgets.put("copy-to-graph", t);

        addLabel(composite, "Perform resolution:", WIDTH3);
        value = (yamlMap.get("perform-entity-resolution") instanceof String n) ? n : "";
        addText(composite, value, TEXT_FIELD_WIDTH);
        yamlWidgets.put("perform-entity-resolution", t);
    }
					
    private void addManifestStepsSection(Composite parent) {
    	var comp = addCompositeUnequal(parent, 2);
    	((GridData)comp.getLayoutData()).grabExcessVerticalSpace = true;
    	((GridData)comp.getLayoutData()).minimumHeight = convertHeightInCharsToPixels(50);
    	
    	addLabel(comp, "Steps:", 12);
    	var buttonComposite = addComposite(comp, 3);
    	var addButton = new Button(buttonComposite, SWT.PUSH);
    	addButton.setText("Add");
    	addButton.addSelectionListener( new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int id = MessageDialog.open(MessageDialog.QUESTION, shell, "", 
						"What kind of step should be added?",
						0,
						"Cancel", "Data", "Model", "Nodegroups", "Manifest", "Copy");
				switch (id) {
				default:
				case 0: // Cancel
					break;
				case 1:
					addLabel(comp, "data:", 12);
					addText(comp, "", TEXT_FIELD_WIDTH);
					break;
				case 2:
					addLabel(comp, "model:", 12);
					addText(comp, "", TEXT_FIELD_WIDTH);
					break;
				case 3:
					addLabel(comp, "nodegroups:", 12);
					addText(comp, "", TEXT_FIELD_WIDTH);
					break;
				case 4:
					addLabel(comp, "manifest:", 12);
					addText(comp, "", TEXT_FIELD_WIDTH);
					break;
				case 5:
					// FIXME - this needs improvement - from and to labels
					addLabel(comp, "copy:", 12);
					var sc = addComposite(comp, 2);
					addText(sc, "", TEXT_FIELD_WIDTH/2);
					addText(sc, "", TEXT_FIELD_WIDTH/2);
					break;
				}
				currentSubcomposite.layout(true, true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
    		
    	});
    	var editButton = new Button(buttonComposite, SWT.PUSH);
    	editButton.setText("Edit");
    	var removeButton = new Button(buttonComposite, SWT.PUSH);
    	removeButton.setText("Remove");
    	
    	if (yamlMap.get("steps") instanceof List<?> list) { 
    		for (var step: list) {
        		if (step instanceof Map<?,?> item) {
        			if (item.get("data") instanceof String value) {
        				addLabel(comp, "data:", 10);
        				addText(comp, value, TEXT_FIELD_WIDTH);
        			} else if (item.get("model") instanceof String value) {
        				addLabel(comp, "model:", 10);
        				addText(comp, value, TEXT_FIELD_WIDTH);
        			} else if (item.get("nodegroups") instanceof String value) {
        				addLabel(comp, "nodegroups:", 10);
        				addText(comp, value, TEXT_FIELD_WIDTH);
        			} else if (item.get("manifest") instanceof String value) {
        				addLabel(comp, "manifest:", 10);
        				addText(comp, value, TEXT_FIELD_WIDTH);
        			} else if (item.get("copygraph") instanceof Map<?,?> cgmap) {
        				addLabel(comp, "copy:", 10);
        				String to = (String) cgmap.get("from-graph");
        				String from = (String) cgmap.get("to-graph");
        				addText(comp, from + " ==> " + to, TEXT_FIELD_WIDTH);
        				// FIXME - perhaps improve this display
        			}
        		}
    		}
    	}
    }

    private void addSeparator(Composite parent) {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        separator.setLayoutData(gridData);
    }


    public Composite addCompositeUnequal(Composite parent, int numColumns) {
        final var subComposite = new Composite(parent, SWT.NONE);
        var layout = new GridLayout(numColumns, true);
        layout.makeColumnsEqualWidth = false;
        subComposite.setLayout(layout);
        var data = new GridData(GridData.FILL);
        data.grabExcessHorizontalSpace = true;
        subComposite.setLayoutData(data);
        return subComposite;
    }
    
    public Composite addComposite(Composite parent, int numColumns) {
        final var subComposite = new Composite(parent, SWT.NONE);
        var layout = new GridLayout(numColumns, true);
        subComposite.setLayout(layout);
        var data = new GridData(GridData.FILL);
        data.grabExcessHorizontalSpace = true;
        subComposite.setLayoutData(data);
        return subComposite;
    }
    
    /** Adds a list of items as found in an array element in the yaml file. 
     *  The organization is slightly different for different keys, so 
     *  there is various customization within this overall general routine.
     */
    public void addContentSection(Composite parent, String sectionName, String kind) {
        final Composite contentComposite = addComposite(parent, 1);

        final Label titleLabel = new Label(contentComposite, SWT.NONE);
        titleLabel.setText("Content sources for the " + sectionName);

        final Composite buttonComposite = addComposite(contentComposite, 2);

        final Button addButton = new Button(buttonComposite,SWT.PUSH);
        addButton.setText("Add");
        final Button removeButton = new Button(buttonComposite,SWT.PUSH);
        removeButton.setText("Remove");
        
        final var list = new org.eclipse.swt.widgets.List(contentComposite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
        GridData gridData = new GridData();
        gridData.widthHint = 200;
        gridData.heightHint = 200;
        list.setLayoutData(gridData);
        yamlWidgets.put(sectionName,  list);
        
        switch (kind) {
        case "Manifest":
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
        		yamlWidgets.put("footprint:" + sectionName, list);
        	}
        	break;
        case "Model":
			// The model-graphs section is optional
        	if (sectionName.equals("files"))  {
        		if (yamlMap.containsKey(sectionName)) {
        			@SuppressWarnings("unchecked")
        			var items = (List<String>) yamlMap.get(sectionName);
        			for (var item: items) list.add(item);
        		}
        		yamlWidgets.put("files", list);
        	} else { // model-graphs
        		Object o = yamlMap.get(sectionName);
        		if (o instanceof String item) {
        			list.add(item);
        		} else if (o instanceof List<?> array) {
        			@SuppressWarnings("unchecked")
        			var items = (List<String>) array;
        			for (var item: items) list.add(item);
        		}
        		yamlWidgets.put(sectionName, list);
        	}
        	break;
        default:
        	break;
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
		        }
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
        	
        });
        
    }


//    private Composite createDefaultComposite(Composite parent) {
//        Composite composite = new Composite(parent, SWT.NULL);
//        GridLayout layout = new GridLayout(2, false);
//        composite.setLayout(layout);
//
////        GridData data = new GridData();
////        data.grabExcessHorizontalSpace = false;
////        data.verticalAlignment = GridData.FILL;
////        data.horizontalAlignment = GridData.FILL;
////        composite.setLayoutData(data);
//
//        return composite;
//    }

    
    Composite makeIngestionStepsComposite(Composite parent) {
    	var subcomp = addComposite(parent, 1);
    	
    	addLabel(subcomp, "Ingestion steps", LABEL_WIDTH);
        final Composite buttonComposite = addComposite(subcomp, 3);
        final Composite stepsComposite = addComposite(subcomp, 1);
        ((GridData)stepsComposite.getLayoutData()).grabExcessVerticalSpace = true;
        ((GridData)stepsComposite.getLayoutData()).minimumHeight = convertHeightInCharsToPixels(15);
        

        final Button addButton = new Button(buttonComposite,SWT.PUSH);
        addButton.setText("Add");
        addButton.addSelectionListener(
        	new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					int id = MessageDialog.open(MessageDialog.QUESTION, shell, "", 
						"What kind of ingestion-step should be added?",
						0,
						"Cancel", "Nodegroup-Constraints", "Json", "Class-Csv", "Owl", "Nodegroup-Csv");
					switch (id) {
					    default:
					    case 0: // Cancel
					    	break;
					    case 1:
					    	makeNodegroupConstraintComposite(stepsComposite, 0, "", ""); // FIXME
					    	break;
					    case 2:
					    	makeJsonComposite(stepsComposite, "", "", "", "");
					    	break;
					    case 3:
					    	makeClassCsvComposite(stepsComposite, "", "");
					    	break;
					    case 4:
					    	makeOwlComposite(stepsComposite, "");
					    	break;
					    case 5:
					    	makeNodegroupCsvComposite(stepsComposite, "", "");
					    	break;
					}
					currentSubcomposite.layout(true, true);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
        	}
        );
        final Button editButton = new Button(buttonComposite,SWT.PUSH);
        editButton.setText("Edit");
        final Button removeButton = new Button(buttonComposite,SWT.PUSH);
        removeButton.setText("Remove");
        
        var array = (List)yamlMap.get("ingestion-steps");
        if (array != null) for (var obj: array) {
        	try {
        		if (obj instanceof Map<?,?> item) {
        			if (item.get("class") instanceof String className &&
            			item.get("csv") instanceof String csv) {
        				makeClassCsvComposite(stepsComposite, className, csv);
        				continue;
        			}
        			if (item.get("nodegroup") instanceof String nodegroup &&
        				item.get("csv") instanceof String csv) {
        				makeNodegroupCsvComposite(stepsComposite, nodegroup, csv);
        				continue;
        			}
        			if (item.get("owl") instanceof String owl) {
        				//makeNodegroupCsvComposite(stepsComposite, nodegroup, csvName);
        				continue;
        			}
        			if (item.get("name") instanceof String name &&
            			item.get("creator") instanceof String creator &&
            			item.get("nodegroup_json") instanceof String nodegroup_json) {
            			continue;
            		}
        			if (item.get("count") instanceof Integer count &&
            				item.get("nodegroup") instanceof String nodegroup &&
            				item.get("constraints") instanceof List<?> constraint) {
            			continue;
            		}
        			MessageDialog.openInformation(shell, "", "Unknown item in ingestion-steps list: " + obj);
        		}
        	} catch (Exception e) {
            	System.out.println(obj.getClass() + " " + obj);
        	}
        }
    	
    	return subcomp;
    }
        
    public static final int WIDTH2 = 10;
    Composite makeClassCsvComposite(Composite parent, String className, String csv) {
        final Composite subComposite = addCompositeUnequal(parent, 4);
        addLabel(subComposite, "  class:", WIDTH2);
        addText(subComposite, className, TEXT_FIELD_WIDTH/2);
        addLabel(subComposite, "    csv:", WIDTH2);
        addText(subComposite, csv, TEXT_FIELD_WIDTH/2);
        return subComposite;
    	
    }
    Composite makeNodegroupCsvComposite(Composite parent, String nodegroup, String csv) {
        final Composite subComposite = addCompositeUnequal(parent, 4);

        addLabel(subComposite, "nodegroup:", WIDTH2);
        addText(subComposite, nodegroup, TEXT_FIELD_WIDTH/2);
        addLabel(subComposite, "      csv:", WIDTH2);
        addText(subComposite, csv, TEXT_FIELD_WIDTH/2);
        return subComposite;
    }
    
    Composite makeOwlComposite(Composite parent, String owl) {
        final Composite subComposite = addCompositeUnequal(parent, 4);

        addLabel(subComposite, "owl:", WIDTH2);
        addText(subComposite, owl, TEXT_FIELD_WIDTH/2);
        return subComposite;
    }
    
    Composite makeJsonComposite(Composite parent, String name, String creator, String json, String comment) {
        final Composite subComposite = addCompositeUnequal(parent, 8);

        addLabel(subComposite, "name:", WIDTH2);
        addText(subComposite, name, TEXT_FIELD_WIDTH/4);
        addLabel(subComposite, "creator:", WIDTH2);
        addText(subComposite, creator, TEXT_FIELD_WIDTH/4);
        addLabel(subComposite, "json:", WIDTH2);
        addText(subComposite, json, TEXT_FIELD_WIDTH/4);
        addLabel(subComposite, "comment:", WIDTH2);
        addText(subComposite, comment, TEXT_FIELD_WIDTH/4);
        return subComposite;
    }
    
    Composite makeNodegroupConstraintComposite(Composite parent, int count, String nodegroup, String comment) {
        final Composite subComposite = addCompositeUnequal(parent, 4);

        addLabel(subComposite, "count:", WIDTH2);
        addText(subComposite, Integer.toString(count), 5);
        addLabel(subComposite, "nodegroup:", WIDTH2);
        addText(subComposite, nodegroup, TEXT_FIELD_WIDTH/3);
        addLabel(subComposite, "comment:", WIDTH2);
        addText(subComposite, comment, TEXT_FIELD_WIDTH/3);
        var constraintsButton = new Button(subComposite, SWT.PUSH);
        constraintsButton.setText("Constraints");
        constraintsButton.addSelectionListener( new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				new ConstraintDialog(shell).open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
        	
        });
        return subComposite;
    }
    
    public class ConstraintDialog extends org.eclipse.jface.dialogs.Dialog {

        public ConstraintDialog(Shell parentShell) {
            super(parentShell);
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite container = (Composite) super.createDialogArea(parent);
            var buttonComposite = addComposite(parent, 3);
            var addButton = new Button(buttonComposite, SWT.PUSH);
            addButton.setText("Add");
            addButton.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					addLine(container);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
            	
            });
            var editButton = new Button(buttonComposite, SWT.PUSH);
            editButton.setText("Edit");
            var removeButton = new Button(buttonComposite, SWT.PUSH);
            removeButton.setText("Remove");
            return container;
        }
        
        public void addLine(Composite container) {
			var comp = addCompositeUnequal(container, 3);
			addText(comp, "", 50);
			addText(comp, "", 6);
			addText(comp, "", 50);
			container.layout(true,true);
        }

        // overriding this methods allows you to set the
        // title of the custom dialog
        @Override
        protected void configureShell(Shell newShell) {
            super.configureShell(newShell);
            newShell.setText("Constraints");
        }

        @Override
        protected Point getInitialSize() {
            return new Point(600, 500);
        }

    	protected boolean isResizable() {
    		return true;
    	}

    }
 
    // Return null, "Manifest", "Data", "Model", or "" if unknown
    static String autoDetectYamlKind(Map<String,Object> yamlMap) {
    	if (yamlMap.get("ingestion-steps") != null) return "Data";
    	if (yamlMap.get("footprint") != null) return "Manifest";
    	if (yamlMap.get("files") != null) return "Model";
    	return "";
    }

    public static HashMap<String, Object> readYaml(String file) {
        File ingestionYaml = new File(file);
        if (!ingestionYaml.exists()) {
            ErrorMessageUtil.warning("No file found, nothing to read");
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
    
    /** Gives a message about the difference rather than just a boolean compare */
    public static String compareYaml(Map<String, Object> a, Map<String, Object> b) {
    	return compareYaml(a, b, "");
    }
    
    @SuppressWarnings("unchecked")
	private static String compareYaml(Map<String, Object> a, Map<String, Object> b, String level) {
    	String diffs = "";
    	var akeys = a.keySet();
    	var bkeys = b.keySet();
    	if (!akeys.equals(bkeys)) {
    		diffs += "The keys are different (level " + level + "): "
    				+ akeys.stream().collect(Collectors.joining(" "))
    				+ " vs. "
    				+ bkeys.stream().collect(Collectors.joining(" "))
    				+ "\n";
    	}
    	for (var key: akeys) {
    		var sa = a.get(key);
    		var sb = b.get(key);
    		var diff = compareYamlItems(sa, sb, level.isEmpty() ? key : (level + ":" + key));
    		if (diff != null) diffs += diff;
    	}
    	return diffs;
    }
    		
    private static String compareYamlItems(Object sa, Object sb, String level) {
    	String diffs = "";
		if (sa instanceof Map<?,?> mapa && sb instanceof Map<?,?> mapb) {
			@SuppressWarnings("unchecked")
			var diff = compareYaml((Map<String,Object>)mapa, (Map<String,Object>)mapb, level);
			if (diff != null) diffs += diff;
		} else if (sa instanceof String ssa && sb instanceof String ssb) {
			if (!Objects.equals(ssa,  ssb)) {
				diffs += "Values are different at " + level + " : " + ssa + " vs. " + ssb + ");\n";
			}
		} else if (sa instanceof List<?> lista && sb instanceof List<?> listb) {
			if (lista.size() != listb.size()) {
				diffs += "Lists at " + level + " have different lengths: " + lista.size() + " vs. " + listb.size() + "\n";
			}
			var itera = lista.iterator();
			var iterb = listb.iterator();
			while (itera.hasNext() && iterb.hasNext()) {
				var diff = compareYamlItems(itera.next(), iterb.next(), level);
				if (diff != null) diffs += diff;
			}
		} else if (sa instanceof List<?> lista && lista.size() == 1 && sb instanceof String s) {
			if (lista.get(0) instanceof String ss && ss.equals(s)) {
				// OK
			} else {
				diffs += "Single item array value differs from String: " + lista.get(0) + " vs. " + s;
			}
		} else if (sb instanceof List<?> listb && listb.size() == 1 && sa instanceof String s) {
			if (listb.get(0) instanceof String ss && ss.equals(s)) {
				// OK
			} else {
				diffs += "String differs from single item array value: " + s + " vs. " + listb.get(0);
			}

		} else {
			diffs += "Unknown or unequal item types (" + level + "): " + 
					(sa == null ? "null" : sa.getClass().toString()) + " vs." + 
					(sb == null ? "null" : sb.getClass().toString()) + "\n";
		}
		return diffs;
    }
    
    static void writeYaml(IFile file, Object content) throws ExecutionException {
        // Write YAML to disk
        try {
            // Generate prettier, human readable YAML
            final DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(FlowStyle.BLOCK);

            final Yaml yaml = new Yaml(options);
            final CharArrayWriter writer = new CharArrayWriter();
            yaml.dump(content, writer);

            final InputStream source = new ByteArrayInputStream(writer.toString().getBytes());
            final boolean force = true;
            final boolean keep_history = true;
            file.setContents(source, force, keep_history, null);
        } catch (CoreException e) {
            throw new ExecutionException("Failed writing yaml output: " + e.getMessage(), e);
        }
    }
    
    protected void performDefaults() {
        super.performDefaults();
    }

    public boolean performOk() {
    	String newPath = null;
        try {
            final IFile thisFile = (IFile) getElement();
            final String thisName = ((IResource) getElement()).getFullPath().toString();
            
            newPath = pathText.getText();
            
            boolean isNewFile = !newPath.trim().equals(thisName.trim());
            IFile outFile = thisFile;
            if (isNewFile) {
            	IPath p = new Path(newPath);
            	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            	outFile = root.getFile(p);
            }
            
            var outYaml = collectYaml();

            if (outFile.exists() && isNewFile) {
            	boolean ok = MessageDialog.openConfirm(shell, "", "OK to overwrite file " + newPath + " ?");
            	if (!ok) return false;
            }
            if (!outFile.exists()) outFile.create(null,  true,  null);
            writeYaml(outFile, outYaml);

        } catch (Exception e) {
        	MessageDialog.openError(shell,  "Error",  "Failed to write to file " + newPath);
            RackConsole.getConsole().error(e.toString());        	
        }
        // FIXME - should we call super.performOK()?
        return true;
    }
    
//    @SuppressWarnings("unchecked")
//    private static List<Map<String, String>> getIngestionSteps(Object obj) {
//        if (!(obj instanceof Map<?, ?> )) {
//            return null;
//        }
//        final Map<?, ?> dataMap = (Map<?, ?>) obj;
//
//        final Object ingestionStepsObj = dataMap.get("ingestion-steps");
//        if (!(ingestionStepsObj instanceof List<?>)) {
//            return null;
//        }
//        final List<?> ingestionStepsList = (List<?>) ingestionStepsObj;
//
//        for (final Object itemObj : ingestionStepsList) {
//            if (!(itemObj instanceof Map<?, ?>)) {
//                return null;
//            }
//            final Map<?, ?> itemMap = (Map<?, ?>) itemObj;
//
//            for (final Entry<?, ?> entry : itemMap.entrySet()) {
//                if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof String)) {
//                    return null;
//                }
//            }
//        }
//
//        return (List<Map<String, String>>) ingestionStepsList;
//    }

    public Map<String,Object> collectYaml() {
    	Map<String,Object> yaml = new HashMap<>();
    	int k = kindCombo.getSelectionIndex();
    	String kind = kindCombo.getItems()[k];
    	switch (kind) {
    	case "Manifest":
    		collectManifestYaml(yaml);
    		break;
    	case "Data":
    		collectDataYaml(yaml);
    		break;
    	case "Model":
    		collectModelYaml(yaml);
    		break;
    	default:
    		MessageDialog.openError(shell, "Error", "Unknown kind of Yaml to output");
    		return null;
    	}
    	return yaml;
    }
    
    @SuppressWarnings("unchecked")
	public void collectYaml(Map<String,Object> yaml, String[] keys) {
		for (var key: keys) {
			try {
				String[] names = key.split(":");
				Widget w = yamlWidgets.get(key);
				if (w == null) {
					MessageDialog.openError(shell,  "Error",  "No widget for " + key);
					continue;
				}
				var y = yaml;
				for (int i=0; i < names.length-1; i++) {
					var yy = (Map<String,Object>)y.get(names[i]);
					if (yy == null) y.put(keys[i], yy = new HashMap<String,Object>());
					y = yy;
				}
				if (w instanceof Text text) {
					String value = text.getText();
					if (!value.isEmpty()) {
						y.put(names[names.length-1], value);
					}
				} else if (w instanceof org.eclipse.swt.widgets.List list) {
					List<String> newlist = new java.util.ArrayList<>(list.getItemCount());
					for (String s: list.getItems()) newlist.add(s);
					y.put(names[names.length-1], newlist);
				} else {
					MessageDialog.openError(shell,  "Error",  "Unknown value for " + keys[0] + ": " + w.getClass());
				}
			} catch (Exception e) {
				MessageDialog.openError(shell, "Exception",
						"Exception while collecting Yaml from UI for key " + key + "\n" + e.getMessage() + "\n" + e.getCause()
						+ getStack(e));
			}
		}
    }
    
    public static String getStack(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
    }
    
    public static final String[] manifestKeys = {
    		"name",
    		"description",
    		"copy-to-graph",
    		"perform-entity-resolution",
    		"footprint:model-graphs",
    		"footprint:data-graphs",
    		"footprint:nodegroups",
    		"steps"
    };
    
    public void collectManifestYaml(Map<String,Object> yaml) {
    	collectYaml(yaml, manifestKeys);
    }
    
    public static final String[] dataKeys = {
    		"ingestion-steps",  // FIXME - list of complex items
    		"model-graphs",
    		"data-graph",
    		"extra-data-graphs",
    };
    
    public void collectDataYaml(Map<String,Object> yaml) {
    	collectYaml(yaml, dataKeys);
    }
    
    public static final String[] modelKeys = {
    		"files",
    		"model-graphs"
    };
    
    public void collectModelYaml(Map<String,Object> yaml) {
    	collectYaml(yaml, modelKeys);
    }
}
