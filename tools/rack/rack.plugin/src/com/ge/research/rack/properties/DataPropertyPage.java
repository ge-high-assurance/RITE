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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
        return composite;
    }
    
    private void addPathSection(Composite parent) {
        Composite composite = addCompositeUnequal(parent, 2);
        // Label for path field
        addLabel(composite, PATH_TITLE, LABEL_WIDTH);
        // Path text field
        addText(composite, ((IResource) getElement()).getFullPath().toString(), TEXT_FIELD_WIDTH);
    }
    
    private void addYamlSelectorSection(Composite parent, String kind) {
        final Composite kindComposite = addCompositeUnequal(parent, 2);

        addLabel(kindComposite, KIND_TITLE, LABEL_WIDTH);

        var kindCombo = new Combo(kindComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
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
        addText(composite, value, TEXT_FIELD_WIDTH);

        addLabel(composite, "Description:", WIDTH3);
        value = (yamlMap.get("description") instanceof String n) ? n : "";
        addText(composite, value, TEXT_FIELD_WIDTH);

        addLabel(composite, "Copy-to-graph:", WIDTH3);
        value = (yamlMap.get("description") instanceof String n) ? n : "";
        addText(composite, value, TEXT_FIELD_WIDTH);

        addLabel(composite, "Perform resolution:", WIDTH3);
        value = (yamlMap.get("description") instanceof String n) ? n : "";
        addText(composite, value, TEXT_FIELD_WIDTH);
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
        	} else { // model-graphs
        		Object o = yamlMap.containsKey(sectionName);
        		if (o instanceof String item) {
        			list.add(item);
        		} else if (o instanceof List<?> array) {
        			@SuppressWarnings("unchecked")
        			var items = (List<String>) array;
        			for (var item: items) list.add(item);
        		}
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
        
    static HashMap<String, Object> readYaml(String file) {
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
    
    // Return null, "Manifest", "Data", "Model", or "" if unknown
    static String autoDetectYamlKind(Map<String,Object> yamlMap) {
    	if (yamlMap.get("ingestion-steps") != null) return "Data";
    	if (yamlMap.get("footprint") != null) return "Manifest";
    	if (yamlMap.get("files") != null) return "Model";
    	return "";
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

//            final int mode = modeCombo.getSelectionIndex();
//
//            // delete previously existing entry
//            if (existingIndex != -1 && mode == 0) {
//                steps.remove(existingIndex);
//
//            }
//            // add or update a step
//            else if (mode != 0) {
//                // Either make a new step or reuse the previous one
//                final Map<String, String> targetStep;
//                if (existingIndex == -1) {
//                    targetStep = new LinkedHashMap<>();
//                    steps.add(targetStep);
//                } else {
//                    targetStep = steps.get(existingIndex);
//                    targetStep.clear();
//                }
//
//                switch (mode) {
//                    case 1:
//                        targetStep.put("class", detailText.getText());
//                        targetStep.put("csv", thisName);
//                        break;
//                    case 2:
//                        targetStep.put("nodegroup", detailText.getText());
//                        targetStep.put("csv", thisName);
//                        break;
//                    case 3:
//                        targetStep.put("owl", thisName);
//                        break;
//                }
//            }
//            writeDataYaml(dataYamlFile, dataObj);
        } catch (CoreException e) {
            RackConsole.getConsole().error(e.toString());
//        } catch (ExecutionException e) {
//            RackConsole.getConsole().error(e.toString());
        }

        return true;
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

}
