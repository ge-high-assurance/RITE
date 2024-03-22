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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.forms.widgets.Twistie;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

/**
 * This class implements a property page for RIT yaml files, in particular for manifest, data and model
 * files. When opened on a file, the property page has widgets, typically editable text fields, that
 * hold the content of the various elements of the yaml file. That content can be edited and new elements 
 * added to various lists in the yaml file. Then an edited yaml map is constructed by scraping the content 
 * of the widgets and reassembling it into a new yaml map. The new yaml can then be written out to the
 * same or a new file. The UI also permits creating a new yaml from scratch (from an empty map).
 * 
 * Before writing an output file, a validator phase checks that the output yaml conforms to the rules 
 * for the kind of yaml file. (This alerts the user to missing required fields when starting from an 
 * empty map.)
 * 
 * The implementation here hard-codes the yaml structure as described in 
 * https://github.com/ge-high-assurance/RACK/blob/master/cli/rack/manifest.py#L15 and 
 * https://github.com/ge-high-assurance/RACK/blob/master/cli/rack/__init__.py#L147
 * (at the time of this writing -- 3/14/2024).
 * 
 * The fact that the structure is hard-coded is a bit brittle, but simpler in this case than writing
 * a generic yaml-description-file to UI translator.
 * 
 * If the yaml structure changes or new kinds of yaml files are added, then the following things need to 
 * be changed:
 * - the parsing of yaml into the tree-like structure of widgets
 * - the scraping of widgets to produce a reconstituted yaml
 * - the hard-coded validator that checks that yaml map conforms to the expected structure for these
 *   kinds of yaml files
 * Reading and writing yaml files uses a library.
 * 
 * Also, a couple of internal defensive checks are performed:
 * - when a yaml file is first read and represented in UI, the UI is immediately scraped and the result
 * checked that is is identical to what was read. This establishes that no element of the yaml was 
 * inadvertently omitted from either the representation or the scraping.
 * - when a yaml file is written, it is immediately reread and the resulting map compared to the output
 * map -- to check that nothing is amiss in the reading and writing actions.
 * 
 * @author davidcok
 *
 */

/* Implementation notes and possible improvements:
 * - the structure of the yaml maps is hard-coded here, making it tedious to change (and test any
 * change) to that structure, or to add a new kind of map. One might drive the whole process from
 * the yaml description (the links given above). This design would require a generic yaml-descriptor
 * to UI implementation (and the reverse), but would be more flexible against changes.
 * 
 * - The property page has an overall scrollbar which kicks in if the internal content is too large.
 * I originally wanted scrollbars just on the variable size aspects of the property page content, so that
 * some of the header content, such as the file location, or even the buttons to add elements to lists,
 * would not be scrolled.  Though I think the design question is still debatable, two points weighed in favor
 * of just using the Eclipse-provided scrollbars. First, if only part of a page scrolled, the remaining might 
 * still be too large for the display window, leaving some parts of the content inaccessible except by
 * enlarging the window. When there is variable content managed by a ScrolledComposite, one has to tell
 * ScrolledComposite to recalculate its sizes (and scrollbar positions) when its content changes. If the 
 * content is programmed to fill up as much space as available (grabExcessVerticalSpace) and is inside a
 * ScrolledComposite, there will always be enough space for it -- that is the content managed within a 
 * ScrolledComposite is allowed to expand as much as it needs -- and therefore would never need any internal
 * scrollbars of its own. It is certainly simpler to just use the top-level scrollbars provided by the 
 * Eclipse Property Page.
 * 
 * - It is a pain that a scrollbar has to be manually told to recalculate its size. It seems that should be 
 * part of the top-level layout(true, true) call. But as it is manual, the necessary action is abstracted 
 * into the relayout() call.
 * 
 * - More could be done in semantic validation -- e.g. are the named files of the right type and suffix,
 * are the nodegroups legitimate, do the URLs exist, etc.
 * 
 * - Possible appearance improvements:
 * -- the - and B buttons should just be simple round buttons
 * -- the vertical spacing between widgets could be reduced (e.g. in the lists of steps)
 * -- The button label "Apply & Close" would be better named "Save & Close", but there is no principled 
 * way I've found to get at this button
 */

// TODO:

// Finish validation
// Add unit testing

// Would like to change the label Apply & CLose
// It seems that label text cannot be right-aligned
// Would like the '-' buttons to be smaller
// Figure out defaultWidgetSelected
// Adjust height, width of list widgets when needed
// Adjust widgets to property window being enlarged, shrunk

// make properties window non-modal

// General Yaml editor:
//   + button with various kinds
//   general left alignment
//   general right alignment
//   scraping for reconstituting yaml
//   warn about duplicate keys in maps
//   warn about ill-formatted integers
//   add any other types?
//   what happens with long keys
//   support for multiple documents per file


public class DataPropertyPage extends PropertyPage {

    private static final String NAME_TITLE = "Name:";
    private static final String PATH_TITLE = "Path:";
    private static final String KIND_TITLE = "Kind of yaml:";

    private static final int TEXT_FIELD_WIDTH = 100;
    private static final int LABEL_WIDTH = 12;

    // Names of kinds of files
    public static final String MANIFEST = "Manifest";
    public static final String DATA = "Data";
    public static final String MODEL = "Model";
    public static final String GENERAL = "General";

    private Shell shell;
    private Composite topParent;
    private ScrolledComposite topScrolled;

    private Object currentYaml = null;
    private Stack<Object> yamlStack = new Stack<>();

    private Combo kindCombo;
    private Text pathText;
    private Map<String, Object> yamlWidgets = new HashMap<>();

    private Composite currentSubcomposite = null;

    /** Constructor for DataPropertyPage. */
    public DataPropertyPage() {
        super();
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        topScrolled = (ScrolledComposite) parent.getParent();
        getApplyButton().setText("Save to file");
        getDefaultsButton().setText("Discard changes");
        relayout();
        // I'd like to change the PropertyDialog button named 'Apply and Close' to 'Save and Close'
        // One can get the PropertyDialog using this.getContainer(), but the buttons themselves are
        // private and there appears no way to get a handle to them. Plus we'd want to change the
        // label just for this DataPropertyPage tab of the overall PropertyDialog.
    }
    
    @Override
    public void contributeButtons(Composite buttonBar) {
        ((GridLayout) buttonBar.getLayout()).numColumns++;
        var validateButton = new Button(buttonBar, SWT.PUSH);
        validateButton.setText("Validate");
        validateButton.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        int k = kindCombo.getSelectionIndex();
                        if (k == 0) {
                            MessageDialog.openInformation(
                                    shell, "Validate", "No kind of yaml file selected");
                            return;
                        }
                        String kind = kindCombo.getItems()[k];
                        var yaml = collectYaml(false, kind, false);
                        String diffs = validate(yaml, kind);
                        if (diffs.isEmpty()) {
                            MessageDialog.openInformation(shell, "Validate", "No errors found");
                        } else {
                            MessageDialog.openInformation(
                                    shell, "Validate", "Errors found:\n\n" + diffs);
                        }
                    }
                });
    }

    /**
     * @see PreferencePage#createContents(Composite)
     */
    protected Control createContents(Composite parent) {
        this.topParent = parent;
        shell = org.eclipse.swt.widgets.Display.getCurrent().getActiveShell();
        IFile file = null;
        if (this.getElement() instanceof IFile f) {
            file = f;
        }

        var composite = addComposite(parent, 1);
        extendVertically(composite);
        try {

            boolean isNewOrIllformedFile;
            if (file != null && file.getLocation() != null && file.exists()) {
                currentYaml = readYaml(file.getLocation().toString());
                isNewOrIllformedFile = currentYaml == null;
            } else {
            	currentYaml = null;
            	isNewOrIllformedFile = true;
            }

            String kind;
            if (currentYaml == null) {
            	// Either a new file or errors on reading
                currentYaml = new HashMap<>(); // Already gave an error message
                kind = "";
            } else {
                kind = autoDetectYamlKind(currentYaml);
            }
            
            yamlStack.push(copyChecked(currentYaml));

            
            String diffs = validate(currentYaml, kind);
            
            // This is some unit testing that retrieved yaml from the bunch of widgets is the same
            // as what went in
            if (!isNewOrIllformedFile && !diffs.isEmpty()) {
                var dialog = new MessageDialog(
                        shell,
                        "",
                        null,
                        "The YAML file is similar to a " + kind + " yaml file but with extra or missing required content as listed below.\n"
                        +"Do you want to continue editing the file as a " + kind + " file or switch to a general YAML editor?\n\n"
                        +"Input and reread maps are NOT the same\n" + diffs,
                		MessageDialog.QUESTION,
                		new String[]{"Continue as " + kind,"Switch to YAML editor"},
                		0);
                int k = dialog.open();
                if (k == 1) {
                	kind = GENERAL;
                }
            }
            
            addPathSection(composite);
            addYamlSelectorSection(composite, kind);
            addSeparator(composite);
            currentSubcomposite = addKindSubcomposite(composite, kind);
            
            if (!isNewOrIllformedFile) {
            	// This is a unit-test of the composite creation and scraping functionality 
                var newYamlMap = collectYaml(isNewOrIllformedFile, kind, false);
                boolean b = Objects.equals(currentYaml, newYamlMap);
                diffs = compareYaml(currentYaml, newYamlMap);
                if (!b || !diffs.isEmpty()) {
                	MessageDialog.openInformation(shell, "",
                		"Reading and then writing the yaml produced different results -- possible bug or ill-formed file\n\n" + diffs);
                }
            }

        } catch (Exception e) {

            MessageDialog.openError(
                    shell,
                    "Exception",
                    "Exception during createContents\n" + e.getMessage() + "\n" + getStack(e));
        }
        return composite;
    }

    public void addPathSection(Composite parent) {
        Composite composite = addCompositeUnequal(parent, 2);
        // Label for path field
        addLabel(composite, PATH_TITLE, LABEL_WIDTH);
        // Path text field
        pathText =
                addTextExpandable(
                        composite,
                        ((IResource) getElement()).getFullPath().toString(),
                        TEXT_FIELD_WIDTH);
    }

    public void addYamlSelectorSection(Composite parent, String kind) {
        final Composite kindComposite = addCompositeUnequal(parent, 2);

        addLabel(kindComposite, KIND_TITLE, LABEL_WIDTH);

        kindCombo = new Combo(kindComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        kindCombo.add("");
        kindCombo.add(MANIFEST);
        kindCombo.add(DATA);
        kindCombo.add(MODEL);
        kindCombo.add(GENERAL);
        kindCombo.select(0);
        if (kind != null) kindCombo.setText(kind);
        kindCombo.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        int k = kindCombo.getSelectionIndex();
                        String kind = kindCombo.getItem(k);

                        Composite p = parent;
                        if (currentSubcomposite != null) {
                            p = currentSubcomposite.getParent();
                            currentSubcomposite.dispose();
                        }

                        //if (!compareYaml(currentYaml, yamlStack.peek()).isEmpty()) {
                        //	if (!MessageDialog.openQuestion(shell, "", "OK to discard edits?")) return;
                        //}
                        //currentYaml = new HashMap<String,Object>();
                        
                        yamlWidgets.clear();
                        currentSubcomposite = addKindSubcomposite(p, kind);

                        relayout();
                    }
                });
    }

    /**
     * Creates a UI widget structure for the yamlMap, which must be empty or be of the correct kind.
     */
    public Composite addKindSubcomposite(Composite parent, String kind) {
        Composite subcomp = null;
        switch (kind) {
            default:
                subcomp = addComposite(parent, 1);
                addLabel(subcomp, "Choose a kind of yaml file", 30);
                addLabel(subcomp, "Current file is either new or not recognized as one of the supported types", 100);
                break;
            case MANIFEST:
                subcomp = addManifestComposite(parent);
                break;
            case DATA:
                subcomp = addDataComposite(parent);
                break;
            case MODEL:
                subcomp = addModelComposite(parent);
                break;
            case GENERAL:
                subcomp = addGeneralComposite(parent);
                break;
        }
        return subcomp;
    }

    public Composite addManifestComposite(Composite parent) {
        var subcomp = addComposite(parent, 1);
        addManifestNameSection(subcomp);
        addSeparator(subcomp);

        final var subComposite = addComposite(subcomp, 3);
        addListComposite(subComposite, "model-graphs", MANIFEST, false);
        addListComposite(subComposite, "data-graphs", MANIFEST, false);
        addListComposite(subComposite, "nodegroups", MANIFEST, false);

        addManifestStepsSection(subcomp);
        return subcomp;
    }

    public Composite addDataComposite(Composite parent) {
    	Map<String,Object> yamlMap = (Map<String,Object>)currentYaml; // data yaml always has a top-level map
        var subcomp = addComposite(parent, 1);
        extendVertically(subcomp);
        var dg = addCompositeUnequal(subcomp, 2);
        addLabel(dg, "data-graph:", LABEL_WIDTH);
        var value = (String) yamlMap.get("data-graph");
        Text t = addTextExpandable(dg, value == null ? "" : value, TEXT_FIELD_WIDTH);
        yamlWidgets.put("data-graph", t);
        var cc = addCompositeUnequal(subcomp, 2);
        addListComposite(cc, "extra-data-graphs", DATA, false);
        addListComposite(cc, "model-graphs", DATA, false);
        addIngestionStepsComposite(subcomp);
        return subcomp;
    }

    public Composite addModelComposite(Composite parent) {
        var subcomp = addComposite(parent, 1);
        var dg = addCompositeUnequal(subcomp, 2);
        addListComposite(dg, "files", MODEL, true);
        addListComposite(dg, "model-graphs", MODEL, false);
        return subcomp;
    }

    public static final int INDENT = 30;
    
    public class YamlItemKindDialog extends org.eclipse.jface.dialogs.Dialog {
    	Object host; // adding to a map or to a list
    	Class<?>[] output;
    	
        public YamlItemKindDialog(Shell parentShell, Object host, Class<?>[] output) {
            super(parentShell);
            this.host = host;
            this.output = output;
        }
               
        static String[] kinds = { "Map", "List", "String", "Integer" };
        static Class<?>[] classes = { java.util.HashMap.class, java.util.ArrayList.class, java.lang.String.class, java.lang.Integer.class };
        @Override
        protected Control createDialogArea(Composite parent) {
            Composite container = (Composite) super.createDialogArea(parent);
        	int index = 0;
            if (host instanceof Map<?,?> map) {
            	
            	int[] counts = new int[10];
            	for (var entry: map.entrySet()) {
            		Object v = entry.getValue();
            		if (v instanceof Map<?,?>) counts[0]++;
            		else if (v instanceof List<?>) counts[1]++;
            		else if (v instanceof String) counts[2]++;
            		else if (v instanceof Integer) counts[3]++;
            	}
            	int max = 0;
            	for (int i=0; i < counts.length; i++) if (counts[i] > max) { max = counts[i]; index = i; }
            	
            	addLabel(container, "You are adding an key-object element to a Yaml map", 50);
            	addLabel(container, "What kind of object should the value have?", 50);
            	if (map.size() > 0) {
            		addLabel(container, "The most common kind already in the map is a " + kinds[index], 50);
            	}

            } else if (host instanceof List<?> list) {

            	int[] counts = new int[10];
            	for (var v: list) {
            		if (v instanceof Map<?,?>) counts[0]++;
            		else if (v instanceof List<?>) counts[1]++;
            		else if (v instanceof String) counts[2]++;
            		else if (v instanceof Integer) counts[3]++;
            	}
            	int max = 0;
            	for (int i=0; i < counts.length; i++) if (counts[i] > max) { max = counts[i]; index = i; }
            	
            	addLabel(container, "You are adding an element to a Yaml list", 50);
            	addLabel(container, "What kind of object should this list element be?", 50);
            	if (list.size() > 0) addLabel(container, "The most common kind already in the list is a " + kinds[index], 50);
            	
            }
            
            Composite c = addComposite(parent, 1);
            setLeftMargin(c, 50);
            for (int i=0; i < kinds.length; i++) {
            	var b = new Button(c, SWT.RADIO);
            	b.setText(kinds[i]);
            	b.setSelection(i==index);
            	if (i == index) output[0] = classes[i];
            	b.setData(i);
            	b.addSelectionListener( new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						Button b = (Button)e.getSource();
						if ( b.getSelection()) {
							output[0] = classes[(int)b.getData()];
						}
					}
            		
            	});
            }
            return container;
        }
    }
    
    public Object getChosenObjectKind(Shell shell, Object o) {
        var output = new Class<?>[1];
		Object inst = null;
		int k = new YamlItemKindDialog(shell, o, output).open();
		if (k == 0) {
			try {
				inst = output[0].getConstructor().newInstance();
			} catch (Exception ex) {
				try {
				    inst = output[0].getConstructor(int.class).newInstance(0);
				} catch (Exception exx) {
				}
			}
		}
		return inst;
    }
    
    public void addGeneralPlusButton(Composite parent, Object o, Consumer<Object>  action) {
        addButton(parent, "+").addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
		        var inst = getChosenObjectKind(shell, o);
				if (inst != null) {
					action.accept(inst);
					relayout();
				}
			}
        	
        });
    }

    
    /** This is the top-level call for the general yaml editor */
    public Composite addGeneralComposite(Composite parent) {
    	var subcomp = addComposite(parent, 1);
    	var head = addComposite(subcomp, 1);
    	String s = currentYaml.getClass().toString();
    	s = s.replace(".class","");
    	s = s.substring(s.lastIndexOf('.'));
    	if (s.endsWith("List")) s = "List";
    	if (s.endsWith("Map")) s = "Map";
    	addLabel(head, "The top-level of this yaml is a " + s, 60);
    	addLabel(head, "Use + to add elements. Use - to remove elements. Use twistie to fold.", 60);
        addGeneralSubcomposite(head, currentYaml);
        return subcomp;
    }
    
    public Control addGeneralSubcomposite(Composite parent, Object value) {
    	if (value instanceof Map<?,?> map) {
    		return addGeneralMapSubcomposite(parent, (Map<String,Object>)map);
    	} else if (value instanceof List<?> list) {
    		return addGeneralListSubcomposite(parent, list);
    	} else {
    		var t = addTextExpandable(parent, value.toString(), 20);
    		t.setData(value.getClass());
    		return t;
    	}
    }
    
    public Composite addGeneralMapSubcomposite(Composite parent, Map<?,?> map) {
        var head = addComposite(parent, 2);
        var items = addComposite(parent.getParent(), 1);
        items.setData(Map.class);
        setVerticalSpacing(items, -5);
        setVerticalMargin(items,0);
        if (map != currentYaml) setLeftMargin(items,INDENT); // No indent at top level
        Set<Object> sorted = new java.util.TreeSet<>();
        sorted.addAll(map.keySet());
        for (var key: sorted) {
        	Object value = map.get(key);
        	addGeneralKeyAndItem(items, key.toString(), value);
        } 
        addGeneralPlusButton(head, map, (Object inst) -> { addGeneralKeyAndItem(items, "", inst); });
        addTwistie(head, items);
        return items;
    }
    
    public Composite addGeneralListSubcomposite(Composite parent, List<?> list) {
        var head = addComposite(parent, 2);
        var wlist = addComposite(parent.getParent(), 1);
        wlist.setData(List.class);
        setLeftMargin(wlist, 10);
        setVerticalMargin(wlist, 0);
        setVerticalSpacing(wlist, -5);
        Consumer<Object> addline = (Object inst) -> { 
			Composite c = addCompositeUnequal(wlist, 3);
			addLabel(c, "-", 1);
			addGeneralRemoveButton(c);
			addGeneralSubcomposite(c, inst);
			};
        for (var item: list) {
        	addline.accept(item);
        }
        addGeneralPlusButton(head, list, addline);
        addTwistie(head, wlist);

        return null;
    }
    
    public Composite addGeneralKeyAndItem(Composite parent, String key, Object value) {
        Composite subcomp;
        subcomp = addCompositeUnequal(parent, 4);
        addGeneralRemoveButton(subcomp, subcomp);
        addText(subcomp, key, 25);
        addLabel(subcomp, ":", 2);
    	if (value instanceof Map<?,?> map) {
    		subcomp = addGeneralMapSubcomposite(subcomp, map);

    	} else if (value instanceof List<?> list) {
    		subcomp = addGeneralListSubcomposite(subcomp, list);
    		
    	} else {
    		// This option is appropriate for any datatype that has a simple string representation
    		// The redundant 'subcomp' composite is to keep a parallel with the Map and List, so that 
    		// alignment is easier.
            var text = addTextExpandable(subcomp, value.toString(), 20);
            text.setData(value.getClass());
    		
    	}
    	return subcomp;
    }
    
    public Button addGeneralRemoveButton(Composite parent) {
    	return addGeneralRemoveButton(parent, parent);
    }
    
    public Button addGeneralRemoveButton(Composite parent, Composite widgetControlled) {
    	var b = addDeleteButton(parent);
    	b.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetControlled.dispose();
				relayout();
			}
    	});
    	return b;
    }
    
    public Twistie addTwistie(Composite parent, Composite content) {
    	var tw = new Twistie(parent, SWT.NONE);
    	tw.setExpanded(true); // Initialize as 'down'
    	tw.addMouseListener( new MouseAdapter() {
    		@Override
    		public void mouseUp(MouseEvent e) {
    			boolean b = tw.isExpanded();
    			content.setVisible(b);
    			((GridData)content.getLayoutData()).exclude = !b;
    			relayout();
    		}
    	});
    	return tw;
    }

    public Label addLabel(Composite parent, String text, int width) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(text);

        final GridData gridData = new GridData();
        gridData.widthHint = convertWidthInCharsToPixels(width);
        label.setLayoutData(gridData);
        return label;
    }

    public Text addText(Composite parent, String initialText, int fieldWidth) {
        Text text = new Text(parent, SWT.WRAP);
        text.setText(initialText);

        final GridData gridData = new GridData();
        gridData.widthHint = convertWidthInCharsToPixels(fieldWidth);
        text.setLayoutData(gridData);
        return text;
    }

    public Text addTextExpandable(Composite parent, String initialText, int fieldWidth) {
        Text text = new Text(parent, SWT.WRAP);
        text.setText(initialText);

        final GridData gridData = new GridData(GridData.FILL, SWT.CENTER, true, false);
        gridData.minimumWidth = convertWidthInCharsToPixels(fieldWidth);
        text.setLayoutData(gridData);
        return text;
    }

    public void addManifestNameSection(Composite parent) {
    	var yamlAsMap= (Map<String,Object>)currentYaml; // manifest must have top-level map
        Composite composite = addCompositeUnequal(parent, 2);
        setVerticalSpacing(composite, 3);
        int WIDTH3 = 20;
        addLabel(composite, NAME_TITLE, WIDTH3);
        String value = (yamlAsMap.get("name") instanceof String n) ? n : "";
        Text t = addTextExpandable(composite, value, TEXT_FIELD_WIDTH);
        yamlWidgets.put("name", t);

        addLabel(composite, "Description:", WIDTH3);
        value = (yamlAsMap.get("description") instanceof String n) ? n : "";
        t = addTextExpandable(composite, value, TEXT_FIELD_WIDTH);
        yamlWidgets.put("description", t);

        addLabel(composite, "Copy-to-graph:", WIDTH3);
        value = (yamlAsMap.get("copy-to-graph") instanceof String n) ? n : "";
        t = addTextExpandable(composite, value, TEXT_FIELD_WIDTH);
        yamlWidgets.put("copy-to-graph", t);

        addLabel(composite, "Perform resolution:", WIDTH3);
        value = (yamlAsMap.get("perform-entity-resolution") instanceof String n) ? n : "";
        t = addTextExpandable(composite, value, TEXT_FIELD_WIDTH);
        yamlWidgets.put("perform-entity-resolution", t);
    }

    public void addManifestStepsSection(Composite parent) {
        var comp = addComposite(parent, 1);
        extendVertically(comp);
        setVerticalSpacing(comp, -5);

        java.util.List<ManifestStepWidget> widgetList = new java.util.LinkedList<>();
        addLabel(comp, "Steps:", 12);
        var buttonComposite = addCompositeUnequal(comp, 2);
        ((GridLayout) buttonComposite.getLayout()).marginTop = 5;
        var addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setText("Add");
        addButton.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        int id =
                                MessageDialog.open(
                                        MessageDialog.QUESTION,
                                        shell,
                                        "",
                                        "What kind of step should be added?",
                                        0,
                                        "Cancel",
                                        "Data",
                                        "Model",
                                        "Nodegroups",
                                        "Manifest",
                                        "Copy");
                        switch (id) {
                                // Note similarity to the initial setup code below
                            default:
                            case 0: // Cancel
                                break;
                            case 1:
                                addSimpleStep(comp, "data", "", widgetList, true);
                                break;
                            case 2:
                                addSimpleStep(comp, "model", "", widgetList, true);
                                break;
                            case 3:
                                addSimpleStep(comp, "nodegroups", "", widgetList, false);
                                break;
                            case 4:
                                addSimpleStep(comp, "manifest", "", widgetList, true);
                                break;
                            case 5:
                                addCopygraphStep(comp, "copygraph", null, widgetList);
                                break;
                        }
                        yamlWidgets.put("steps", widgetList);
                        relayout();
                    }
                });
        addLabel(
                buttonComposite,
                "Press '-' button to remove line.  Press 'B' for file browser.  Text fields may be edited in place.",
                70);

        yamlWidgets.put("steps", widgetList);
        if (((Map<?,?>)currentYaml).get("steps") instanceof List<?> list) {
            for (var step : list) {
                if (step instanceof Map<?, ?> item) {
                    // Note similarity to the selection listener code above
                    if (item.get("data") instanceof String value) {
                        addSimpleStep(comp, "data", value, widgetList, true);
                    } else if (item.get("model") instanceof String value) {
                        addSimpleStep(comp, "model", value, widgetList, true);
                    } else if (item.get("nodegroups") instanceof String value) {
                        addSimpleStep(comp, "nodegroups", value, widgetList, false);
                    } else if (item.get("manifest") instanceof String value) {
                        addSimpleStep(comp, "manifest", value, widgetList, true);
                    } else if (item.get("copygraph") instanceof Map<?, ?> cgmap) {
                        addCopygraphStep(comp, "copygraph", cgmap, widgetList);
                    }
                }
            }
        }
    }
    
    public Button addDeleteButton(Composite parent) {
        var b = new Button(parent, SWT.PUSH);
        b.setText("-");
        GridData buttonLayoutData = new GridData();
        buttonLayoutData.widthHint = 30;
        b.setLayoutData(buttonLayoutData);
        return b;
    }

    public void addSimpleStep(
            Composite comp,
            String name,
            String value,
            java.util.List<ManifestStepWidget> widgetList,
            boolean fileBrowser) {
        Composite x = addCompositeUnequal(comp, fileBrowser ? 4 : 3);
        var b = addDeleteButton(x);
        b.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Composite p = ((Button) e.getSource()).getParent();
                        Composite pp = p.getParent();
                        widgetList.removeIf(msw -> msw.value().getParent() == p);
                        p.dispose();
                        pp.layout(true, true);
                    }
                });
        addLabel(x, name + ":", 10);
        Text t = addText(x, value, TEXT_FIELD_WIDTH - 5);
        if (fileBrowser) addFileBrowseButton(x, t);
        widgetList.add(new ManifestStepWidget(name, t));
    }

    public void addCopygraphStep(
            Composite comp,
            String name,
            Map<?, ?> cgmap,
            java.util.List<ManifestStepWidget> widgetList) {
        Composite x = addCompositeUnequal(comp, 3);
        var b = addDeleteButton(x);
        b.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Composite p = ((Button) e.getSource()).getParent();
                        Composite pp = p.getParent();
                        widgetList.removeIf(msw -> msw.value().getParent() == p);
                        p.dispose();
                        pp.layout(true, true);
                    }
                });
        addLabel(x, name + ":", 10);
        var sc = addCompositeUnequal(x, 4);
        addLabel(sc, "from:", 4);
        Text t =
                addText(
                        sc,
                        cgmap == null ? "" : (String) cgmap.get("from-graph"),
                        TEXT_FIELD_WIDTH / 2 - 5);
        addLabel(sc, "to:", 3);
        Text tt =
                addText(
                        sc,
                        cgmap == null ? "" : (String) cgmap.get("to-graph"),
                        TEXT_FIELD_WIDTH / 2 - 5);
        widgetList.add(new ManifestStepWidget(name, t, tt));
    }

    public record ManifestStepWidget(String key, Text value, Text value2) {

        public ManifestStepWidget(String key, Text value) {
            this(key, value, null);
        }
    }

    public record DataStepWidget(String key, Text[] values, java.util.List<String> constraints) {

        public DataStepWidget(String key, Text... values) {
            this(key, values, null);
        }

        public DataStepWidget(String key, Text t1, Text t2, java.util.List<String> constraints) {
            this(key, new Text[] {t1, t2}, constraints);
        }
    }

    public void addSeparator(Composite parent) {
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
        layout.marginHeight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.verticalSpacing = 0;
        subComposite.setLayout(layout);
        var data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        subComposite.setLayoutData(data);
        return subComposite;
    }

    public Composite addComposite(Composite parent, int numColumns) {
        final var subComposite = new Composite(parent, SWT.NONE);
        var layout = new GridLayout(numColumns, true);
        subComposite.setLayout(layout);
        var data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        subComposite.setLayoutData(data);
        return subComposite;
    }

    /**
     * Adds a list of items as found in an array element in the yaml file. The organization is
     * slightly different for different keys, so there is various customization within this overall
     * general routine.
     */
    public void addListComposite(
            Composite parent, String sectionName, String kind, boolean fileBrowser) {
        final Composite contentComposite = addComposite(parent, 1);

        final Label titleLabel = new Label(contentComposite, SWT.NONE);
        titleLabel.setText("Content sources for the " + sectionName);

        final Composite buttonComposite =
                addCompositeUnequal(contentComposite, fileBrowser ? 3 : 2);

        // list must be declared before browseButton
        final var list =
                new org.eclipse.swt.widgets.List(
                        contentComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);

        final Button addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setText("Add");
        if (fileBrowser) {
            addFileBrowseButton(buttonComposite, list::add, true);
        }
        final Button removeButton = new Button(buttonComposite, SWT.PUSH);
        removeButton.setText("Remove");

        GridData gridData = new GridData();
        gridData.widthHint = 250;
        gridData.heightHint = 200;
        list.setLayoutData(gridData);
        yamlWidgets.put(sectionName, list);
        var yamlAsMap = (Map<String,Object>)currentYaml;
        switch (kind) {
            case MANIFEST:
                if (yamlAsMap.containsKey("footprint")) {

                    Object oFootprint = yamlAsMap.get("footprint");
                    if (oFootprint instanceof Map) {
                        Map oFootprintMap = (Map) oFootprint;
                        if (!oFootprintMap.containsKey(sectionName)) {
                            // dGraphs.add(RackPreferencePage.getDefaultDataGraph());
                        } else {
                            @SuppressWarnings("unchecked")
                            var items = (List<String>) oFootprintMap.get(sectionName);
                            for (var item : items) list.add(item);
                        }
                    }
                }
                yamlWidgets.put("footprint:" + sectionName, list);
                break;
            case MODEL:
                // The model-graphs section is optional
                if (sectionName.equals("files")) {
                    if (yamlAsMap.containsKey(sectionName)) {
                        @SuppressWarnings("unchecked")
                        var items = (List<String>) yamlAsMap.get(sectionName);
                        for (var item : items) list.add(item);
                    }
                    yamlWidgets.put("files", list);
                } else { // model-graphs
                    Object o = yamlAsMap.get(sectionName);
                    if (o instanceof String item) {
                        list.add(item);
                    } else if (o instanceof List<?> array) {
                        @SuppressWarnings("unchecked")
                        var items = (List<String>) array;
                        for (var item : items) list.add(item);
                    }
                    yamlWidgets.put(sectionName, list);
                }
                break;
            case DATA:
                // These sections are optional
                if (sectionName.equals("extra-data-graphs") || sectionName.equals("model-graphs")) {
                    if (yamlAsMap.containsKey(sectionName)) {
                        @SuppressWarnings("unchecked")
                        var items = (List<String>) yamlAsMap.get(sectionName);
                        for (var item : items) list.add(item);
                    }
                    yamlWidgets.put(sectionName, list);
                }
                break;
            default:
                break;
        }

        removeButton.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        int[] sel = list.getSelectionIndices();
                        list.remove(sel);
                    }
                });

        addButton.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        InputDialog dlg =
                                new InputDialog(
                                        Display.getCurrent().getActiveShell(),
                                        "",
                                        "Enter new item",
                                        "",
                                        null);
                        if (dlg.open() == Window.OK) {
                            // User clicked OK; update the label with the input
                            list.add(dlg.getValue());
                        }
                    }
                });
    }

    public void extendVertically(Composite composite) {
        ((GridData) composite.getLayoutData()).verticalAlignment = SWT.FILL;
        ((GridData) composite.getLayoutData()).grabExcessVerticalSpace = true;
    }
    
    /** For a composite that is a GridLayout, this call sets the vertical spacing of the 
     * rows in the grid; the value may be negative to remove more space than the default.
     */
    public void setVerticalSpacing(Composite composite, int space) {
        ((GridLayout)composite.getLayout()).verticalSpacing = space;
    }

    public void setVerticalMargin(Composite composite, int space) {
        ((GridLayout)composite.getLayout()).marginHeight = space;
    }

    public void setLeftMargin(Composite composite, int space) {
        ((GridLayout)composite.getLayout()).marginLeft = space;
    }

    public ScrolledComposite addScrolledComposite(Composite parent) {
        ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        sc.setAlwaysShowScrollBars(false);
        return sc;
    }

    public void relayout() {
        topScrolled.setMinSize(topScrolled.getChildren()[0].computeSize(SWT.DEFAULT, SWT.DEFAULT));
        topParent.layout(true, true);
    }

    public Composite addIngestionStepsComposite(Composite parent) {
        var subcomp = addComposite(parent, 1);
        extendVertically(subcomp);
        setVerticalSpacing(subcomp, 0);

        var ingestionStepsWidgets = new java.util.ArrayList<DataStepWidget>(10);
        yamlWidgets.put("ingestion-steps", ingestionStepsWidgets);

        addLabel(subcomp, "Ingestion steps", LABEL_WIDTH);
        final Composite buttonComposite = addCompositeUnequal(subcomp, 2);

        var sc = subcomp;
        final Composite stepsComposite = addComposite(sc, 1);
        extendVertically(stepsComposite);
        setVerticalSpacing(stepsComposite, -5);

        final Button addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setText("Add");
        addButton.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        int id =
                                MessageDialog.open(
                                        MessageDialog.QUESTION,
                                        shell,
                                        "",
                                        "What kind of ingestion-step should be added?",
                                        0,
                                        "Cancel",
                                        "Nodegroup-Constraints",
                                        "Json",
                                        "Class-Csv",
                                        "Owl",
                                        "Nodegroup-Csv");
                        switch (id) {
                            default:
                            case 0: // Cancel
                                break;
                            case 1:
                                addNodegroupConstraintsComposite(
                                        ingestionStepsWidgets,
                                        stepsComposite,
                                        0,
                                        "",
                                        new java.util.ArrayList<String>());
                                break;
                            case 2:
                                addJsonComposite(
                                        ingestionStepsWidgets, stepsComposite, "", "", "", "");
                                break;
                            case 3:
                                addClassCsvComposite(ingestionStepsWidgets, stepsComposite, "", "");
                                break;
                            case 4:
                                addOwlComposite(ingestionStepsWidgets, stepsComposite, "");
                                break;
                            case 5:
                                addNodegroupCsvComposite(
                                        ingestionStepsWidgets, stepsComposite, "", "");
                                break;
                        }
                    }
                });
        addLabel(
                buttonComposite,
                "Click '-' to remove line.  Click 'B' for file browser.  Edit text in place.",
                70);

        var array = (List<?>) ((Map<String,Object>)currentYaml).get("ingestion-steps");
        if (array != null) {
            for (var obj : array) {
                try {
                    if (obj instanceof Map<?, ?> item) {
                        if (item.get("class") instanceof String className
                                && item.get("csv") instanceof String csv) {
                            addClassCsvComposite(
                                    ingestionStepsWidgets, stepsComposite, className, csv);
                            continue;
                        }
                        if (item.get("nodegroup") instanceof String nodegroup
                                && item.get("csv") instanceof String csv) {
                            addNodegroupCsvComposite(
                                    ingestionStepsWidgets, stepsComposite, nodegroup, csv);
                            continue;
                        }
                        if (item.get("owl") instanceof String owl) {
                            addOwlComposite(ingestionStepsWidgets, stepsComposite, owl);
                            continue;
                        }
                        if (item.get("name") instanceof String name
                                && item.get("creator") instanceof String creator
                                && item.get("nodegroup_json") instanceof String nodegroup_json) {
                            var comment = (String) item.get("comment"); // optional - may be null
                            addJsonComposite(
                                    ingestionStepsWidgets,
                                    stepsComposite,
                                    name,
                                    creator,
                                    nodegroup_json,
                                    comment);
                            continue;
                        }
                        if (item.get("count") instanceof Integer count
                                && item.get("nodegroup") instanceof String nodegroup) {
                            java.util.List<String> constraints =
                                    (List<String>)
                                            item.get("constraints"); // optional - may be null
                            addNodegroupConstraintsComposite(
                                    ingestionStepsWidgets,
                                    stepsComposite,
                                    Integer.valueOf(count),
                                    nodegroup,
                                    constraints);
                            continue;
                        }
                        MessageDialog.openInformation(
                                shell, "", "Unknown item in ingestion-steps list: " + obj);
                    }
                } catch (Exception e) {
                    System.out.println(obj.getClass() + " " + obj);
                }
            }
        }
        // sc.setMinSize(stepsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        return subcomp;
    }

    public static final int WIDTH2 = 10;
    public static final int WIDTH3 = 6;

    public void addRemoveButton(Composite parent, List<DataStepWidget> widgets) {
        var b = new Button(parent, SWT.PUSH);
        b.setText("-");
        b.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Composite parent = ((Button) e.getSource()).getParent();
                        Composite gparent = parent.getParent();
                        widgets.removeIf(w -> w.values()[0].getParent() == parent);
                        parent.dispose();
                        gparent.layout(true, true);
                    }
                });
    }

    public Composite addClassCsvComposite(
            java.util.List<DataStepWidget> widgets,
            Composite parent,
            String className,
            String csv) {
        final Composite subComposite = addCompositeUnequal(parent, 6);
        addRemoveButton(subComposite, widgets);
        addLabel(subComposite, "class:", WIDTH2);
        Text t1 = addText(subComposite, className, TEXT_FIELD_WIDTH / 2);
        addLabel(subComposite, "csv:", 4);
        Text t2 = addText(subComposite, csv, TEXT_FIELD_WIDTH / 2);
        addFileBrowseButton(subComposite, t2);
        widgets.add(new DataStepWidget("class#csv", t1, t2));
        return subComposite;
    }

    public Composite addNodegroupCsvComposite(
            java.util.List<DataStepWidget> widgets,
            Composite parent,
            String nodegroup,
            String csv) {
        final Composite subComposite = addCompositeUnequal(parent, 6);
        addRemoveButton(subComposite, widgets);
        addLabel(subComposite, "nodegroup:", WIDTH2);
        Text t1 = addText(subComposite, nodegroup, TEXT_FIELD_WIDTH / 2);
        addLabel(subComposite, "csv:", 4);
        Text t2 = addText(subComposite, csv, TEXT_FIELD_WIDTH / 2);
        addFileBrowseButton(subComposite, t2);
        widgets.add(new DataStepWidget("nodegroup#csv", t1, t2));
        return subComposite;
    }

    public Composite addOwlComposite(
            java.util.List<DataStepWidget> widgets, Composite parent, String owl) {
        final Composite subComposite = addCompositeUnequal(parent, 5);
        addRemoveButton(subComposite, widgets);
        addLabel(subComposite, "owl:", WIDTH2);
        Text t1 = addText(subComposite, owl, TEXT_FIELD_WIDTH / 2);
        addFileBrowseButton(subComposite, t1);
        widgets.add(new DataStepWidget("owl", t1, null));
        return subComposite;
    }

    public Composite addJsonComposite(
            java.util.List<DataStepWidget> widgets,
            Composite parent,
            String name,
            String creator,
            String json,
            String comment) {
        final Composite subComposite = addCompositeUnequal(parent, 9);
        addRemoveButton(subComposite, widgets);
        addLabel(subComposite, "name:", WIDTH2);
        Text t1 = addText(subComposite, name, TEXT_FIELD_WIDTH / 4);
        addLabel(subComposite, "creator:", 6);
        Text t2 = addText(subComposite, creator, TEXT_FIELD_WIDTH / 4);
        addLabel(subComposite, "json:", 4);
        Text t3 = addText(subComposite, json, TEXT_FIELD_WIDTH / 4);
        addLabel(subComposite, "comment:", 8);
        Text t4 = addText(subComposite, comment, TEXT_FIELD_WIDTH / 4);
        widgets.add(new DataStepWidget("name#creator#nodegroup_json#comment", t1, t2, t3, t4));
        return subComposite;
    }

    public Composite addNodegroupConstraintsComposite(
            java.util.List<DataStepWidget> widgets,
            Composite parent,
            int count,
            String nodegroup,
            java.util.List<String> constraints) {
        final Composite subComposite = addCompositeUnequal(parent, 6);
        addRemoveButton(subComposite, widgets);
        addLabel(subComposite, "count:", WIDTH2);
        Text t1 = addText(subComposite, Integer.toString(count), 5);
        addLabel(subComposite, "nodegroup:", WIDTH2);
        Text t2 = addText(subComposite, nodegroup, TEXT_FIELD_WIDTH / 2);
        var constraintsButton = new Button(subComposite, SWT.PUSH);
        constraintsButton.setText("Constraints");
        constraintsButton.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        new ConstraintDialog(shell, constraints).open();
                    }
                });

        widgets.add(new DataStepWidget("count#nodegroup", t1, t2, constraints));
        return subComposite;
    }

    public static interface Setter {
        void apply(String text);
    }

    public Button addButton(Composite parent, String name) {
        var b = new Button(parent, SWT.PUSH);
        b.setText(name);
        return b;
    }
    
    public void addFileBrowseButton(Composite parent, Text textfield) {
        addFileBrowseButton(parent, textfield::setText, false);
    }

    public void addFileBrowseButton(Composite parent, Setter setter, boolean longName) {
            var b = new Button(parent, SWT.PUSH);
            b.setText(longName ? "Browse" : "B");
        b.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        var fd = new FileDialog(shell, SWT.OPEN);
                        IPath currentDir;
                        if (DataPropertyPage.this.getElement() instanceof IFile f) {
                            currentDir = f.getParent().getLocation();
                        } else {
                            currentDir = new Path(".");
                        }
                        fd.setFilterPath(currentDir.toOSString());
                        String file = fd.open();
                        var relativePath = new Path(file).makeRelativeTo(currentDir);
                        setter.apply(relativePath.toOSString());
                    }
                });
    }

    public class ConstraintDialog extends org.eclipse.jface.dialogs.Dialog {
        public java.util.List<String> constraints;
        public java.util.List<Text> textFields = new java.util.LinkedList<Text>();

        public ConstraintDialog(Shell parentShell, java.util.List<String> constraints) {
            super(parentShell);
            this.constraints = constraints;
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite container = (Composite) super.createDialogArea(parent);
            var buttonComposite = addCompositeUnequal(container, 2);
            var addButton = addButton(buttonComposite, "Add");
            addLabel(buttonComposite, "Click '-' to remove line.  Edit text in place.", 40);
            ScrolledComposite sc = new ScrolledComposite(container, SWT.H_SCROLL | SWT.V_SCROLL);
            sc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
            sc.setExpandHorizontal(true);
            sc.setExpandVertical(true);
            sc.setAlwaysShowScrollBars(false);
            var comp = addComposite(sc, 1);
            setVerticalSpacing(comp, -3);
            ((GridData) comp.getLayoutData()).horizontalAlignment = SWT.FILL;
            ((GridData) comp.getLayoutData()).verticalAlignment = SWT.FILL;
            ((GridData) comp.getLayoutData()).grabExcessHorizontalSpace = true;
            ((GridData) comp.getLayoutData()).grabExcessVerticalSpace = true;
            sc.setContent(comp);

            if (constraints != null) {
                for (var constraint : constraints) {
                    textFields.add(addConstraintLine(comp, constraint));
                }
            }
            addButton.addSelectionListener(
                    new SelectionAdapter() {

                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            textFields.add(addConstraintLine(comp, ""));
                            sc.setMinSize(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                            parent.layout(true, true);
                        }
                    });
            sc.setMinSize(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            parent.layout(true, true);
            return container;
        }

        public Text addConstraintLine(Composite parent, String content) {
            var c = addCompositeUnequal(parent, 2);
            ((GridData) c.getLayoutData()).horizontalAlignment = GridData.FILL;
            ((GridData) c.getLayoutData()).grabExcessHorizontalSpace = true;
            var b = new Button(c, SWT.PUSH);
            b.setText("-");
            b.addSelectionListener(
                    new SelectionAdapter() {

                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            Composite p = ((Button) e.getSource()).getParent();
                            Composite pp = p.getParent();
                            Object o = p.getChildren()[1]; // the sibling text field
                            textFields.removeIf(tf -> tf == o);
                            p.dispose();
                            ((ScrolledComposite) pp.getParent())
                                    .setMinSize(pp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                            pp.getParent().layout(true, true);
                        }
                    });
            var t = addTextExpandable(c, content, TEXT_FIELD_WIDTH);
            return t;
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

        @Override
        protected boolean isResizable() {
            return true;
        }

        @Override
        protected void okPressed() {
            constraints.clear();
            for (var t : textFields) {
                String v = t.getText().trim();
                if (!v.isEmpty()) constraints.add(v);
            }
            super.okPressed();
        }
    }

    // Return null, "Manifest", "Data", "Model", "General" or "" if unknown
    public static String autoDetectYamlKind(Object yaml) {
    	if (yaml instanceof Map<?,?> yamlMap) {
    		if (yamlMap.get("ingestion-steps") != null) return DATA;
    		if (yamlMap.get("name") != null) return MANIFEST;
    		if (yamlMap.get("files") != null) return MODEL;
    	} else if (yaml instanceof List<?>) {
    		return GENERAL;
    	}
        return "";
    }

    public static Object readYaml(String file) {
        File ingestionYaml = new File(file);
        if (!ingestionYaml.exists()) {
            ErrorMessageUtil.warning("No file found, nothing to read");
            return null;
        }

        String dir = ingestionYaml.getParent();
        Object yamlAsRead = null;
        try {
            yamlAsRead = ProjectUtils.readAnyYaml(ingestionYaml.getAbsolutePath());
        } catch (Exception e) {
            ErrorMessageUtil.error("Unable to read " + dir + "/" + ingestionYaml.getName());
            return null;
        }
        if (yamlAsRead == null) {
            ErrorMessageUtil.error(
                    "Ill formed manifest at "
                            + dir
                            + "/"
                            + ingestionYaml.getName()
                            + ", please check");
            ErrorMessageUtil.error("Check YAML: " + ingestionYaml.getAbsolutePath());
            return null;
        }
        return yamlAsRead;
    }

    /** Returns a description of differences */
    public static String compareYaml(Object a, Object b) {
        return compareYamlItems(a, b, "");
    }

    @SuppressWarnings("unchecked")
    public static String compareYamlMaps(Map<String, Object> a, Map<String, Object> b, String level) {
        String diffs = "";
        var akeys = a.keySet();
        var bkeys = b.keySet();
        if (!akeys.equals(bkeys)) {
            diffs +=
                    "The keys are different (in '"
                            + level
                            + "'): "
                            + akeys.stream().collect(Collectors.joining(" "))
                            + " vs. "
                            + bkeys.stream().collect(Collectors.joining(" "))
                            + "\n";
        }
        for (var key : akeys) {
            var sa = a.get(key);
            var sb = b.get(key);
            var diff = compareYamlItems(sa, sb, level.isEmpty() ? key : (level + ":" + key));
            if (diff != null) diffs += diff;
        }
        return diffs;
    }

    public static String compareYamlItems(Object sa, Object sb, String level) {
        String diffs = "";
        if (sa instanceof Map<?, ?> mapa && sb instanceof Map<?, ?> mapb) {
            @SuppressWarnings("unchecked")
            var diff = compareYamlMaps((Map<String, Object>) mapa, (Map<String, Object>) mapb, level);
            if (diff != null) diffs += diff;
        } else if (sa instanceof String ssa && sb instanceof String ssb) {
            if (!Objects.equals(ssa, ssb)) {
                diffs += "Values are different at " + level + " : " + ssa + " vs. " + ssb + ");\n";
            }
        } else if (sa instanceof Integer ia && sb instanceof Integer ib) {
            if (!Objects.equals(ia, ib)) {
                diffs +=
                        "Integer values are different at "
                                + level
                                + " : "
                                + ia
                                + " vs. "
                                + ib
                                + ");\n";
            }
        } else if (sa instanceof List<?> lista && sb instanceof List<?> listb) {
            if (lista.size() != listb.size()) {
                diffs +=
                        "Lists at "
                                + level
                                + " have different lengths: "
                                + lista.size()
                                + " vs. "
                                + listb.size()
                                + "\n";
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
                diffs +=
                        "Single item array value differs from String: "
                                + lista.get(0)
                                + " vs. "
                                + s;
            }
        } else if (sb instanceof List<?> listb && listb.size() == 1 && sa instanceof String s) {
            if (listb.get(0) instanceof String ss && ss.equals(s)) {
                // OK
            } else {
                diffs +=
                        "String differs from single item array value: "
                                + s
                                + " vs. "
                                + listb.get(0);
            }

        } else {
            diffs +=
                    "Unknown or unequal item types ("
                            + level
                            + "): "
                            + (sa == null ? "null" : sa.getClass().toString())
                            + " vs."
                            + (sb == null ? "null" : sb.getClass().toString())
                            + "\n";
        }
        return diffs;
    }

    public static void writeYaml(IFile file, Object content) throws ExecutionException {
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

    public void performDefaults() {
    	currentYaml = yamlStack.peek();
        var kind = autoDetectYamlKind(currentYaml);
        var parent = currentSubcomposite.getParent();
        if (currentSubcomposite != null) currentSubcomposite.dispose();
        currentSubcomposite = addKindSubcomposite(parent, kind);
        pathText.setText(((IResource) getElement()).getFullPath().toString());
        parent.layout(true, true);
        super.performDefaults();
    }

    /** This is called for both the 'Save to file' button and the 'Apply & Close' button */
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

            int k = kindCombo.getSelectionIndex();
            String kind = kindCombo.getItems()[k];
            var outYaml = collectYaml(false, kind, true);

            var diffs = validate(outYaml, kind);
            if (diffs != null && !diffs.isEmpty()) {
                var ok =
                        MessageDialog.openConfirm(
                                shell,
                                "",
                                "The edited yaml content has some errors. Save the content anyway?\n\n"
                                        + diffs);
                if (!ok) return false;
            }

            if (outFile.exists() && isNewFile) {
                boolean ok =
                        MessageDialog.openConfirm(
                                shell, "", "OK to overwrite file " + newPath + " ?");
                if (!ok) return false;
            }
            if (!outFile.exists()) outFile.create(null, true, null);
            writeYaml(outFile, outYaml);
            
            // Check
            var inYaml = readYaml(outFile.getLocation().toOSString());
            String inoutDiff = compareYaml(outYaml, inYaml);
            if (!inoutDiff.isEmpty()) {
            	MessageDialog.openError(shell, "Error", "Rereading the just written file produces a different yaml map:\n\n" + inoutDiff);
            }

        } catch (Exception e) {
            MessageDialog.openError(
                    shell, "Error", "Failed to write to file " + newPath + "\n" + e.getMessage());
            RackConsole.getConsole().error(e.toString());
        }
        return true;
    }

    public Object collectYaml(boolean isNewFile, String kind, boolean showErrors) {
        String diffs;
        Object y = null;
        switch (kind) {
            case MANIFEST: {
                Map<String, Object> yaml = new HashMap<>();
                diffs = collectManifestYaml(yaml);
                y = yaml;
                break;
            }
            case DATA: {
                Map<String, Object> yaml = new HashMap<>();
                diffs = collectDataYaml(yaml);
                y = yaml;
                break;
            }
            case MODEL: {
                Map<String, Object> yaml = new HashMap<>();
                diffs = collectModelYaml(yaml);
                y = yaml;
                break;
            }
            default:
            	StringBuilder str = new StringBuilder();
            	y = collectGeneralYaml(str);
                diffs = str.toString();
                break;
        }
        if (showErrors && !diffs.isEmpty()) {
            MessageDialog.openError(shell, "Error", diffs);
        }
        return y;
    }

    @SuppressWarnings("unchecked")
    public String collectYaml(Map<String, Object> yaml, String[] keys) {
        String diffs = "";
        for (var key : keys) {
            try {
                String[] names = key.split(":");
                Object w = yamlWidgets.get(key);
                if (w == null) {
                    diffs += "No widget for " + key + "\n";
                    continue;
                }
                var y = yaml;
                for (int i = 0; i < names.length - 1; i++) {
                    var yy = (Map<String, Object>) y.get(names[i]);
                    if (yy == null) y.put(names[i], yy = new HashMap<String, Object>());
                    y = yy;
                }
                if (w instanceof Text text) {
                    String value = text.getText();
                    if (!value.isEmpty()) {
                        y.put(names[names.length - 1], value);
                    }
                } else if (w instanceof org.eclipse.swt.widgets.List list) {
                    if (list.getItemCount() > 0) {
                        List<String> newlist = new java.util.ArrayList<>(list.getItemCount());
                        for (String s : list.getItems()) newlist.add(s);
                        y.put(names[names.length - 1], newlist);
                    }
                } else if (w instanceof java.util.List<?> wlist
                        && (wlist.size() == 0 || wlist.get(0) instanceof ManifestStepWidget)) {
                    var wsteps = (java.util.List<ManifestStepWidget>) wlist;
                    java.util.List<Object> yamlList = new java.util.ArrayList<>(wsteps.size());
                    for (var step : wsteps) {
                        if (step.value2 == null) {
                            var mp = new HashMap<String, Object>();
                            mp.put(step.key(), step.value.getText());
                            yamlList.add(mp);
                        } else {
                            var mp1 = new HashMap<String, Object>();
                            mp1.put("from-graph", step.value.getText());
                            mp1.put("to-graph", step.value2.getText());
                            var mp = new HashMap<String, Object>();
                            mp.put(step.key(), mp1);
                            yamlList.add(mp);
                        }
                    }
                    yaml.put(names[names.length - 1], yamlList);
                } else if (w instanceof java.util.List<?> wlist
                        && (wlist.size() == 0 || wlist.get(0) instanceof DataStepWidget)) {
                    var wsteps = (java.util.List<DataStepWidget>) wlist;
                    java.util.List<Object> yamlList = new java.util.ArrayList<>(wsteps.size());
                    for (var step : wsteps) {
                        var snames = step.key().split("#");
                        var mp = new HashMap<String, Object>();
                        int k = 0;
                        for (var sname : snames) {
                            var text = step.values()[k];
                            if (text != null) {
                                String v = text.getText();
                                if (sname.equals("count")) {
                                    // count is a special case where the yaml expects an Integer
                                    try {
                                        mp.put(sname, Integer.valueOf(v));
                                    } catch (NumberFormatException e) {
                                        diffs +=
                                                "Value for 'count' is not a String representation of a non-negative integer: "
                                                        + v
                                                        + "\n";
                                    }
                                } else {
                                    mp.put(sname, v);
                                }
                            }
                            k++;
                        }
                        if (step.constraints != null && step.constraints.size() > 0) {
                            mp.put("constraints", step.constraints);
                        }
                        yamlList.add(mp);
                    }
                    yaml.put(names[names.length - 1], yamlList);
                } else {
                    diffs += "Unknown value for " + keys[0] + ": " + w.getClass();
                }
                // Patch up -- FIXME this is a hack
                var f = (Map<?,?>)yaml.get("footprint");
                if (f == null || f.size() == 0) yaml.remove("footprint");
            } catch (Exception e) {
                MessageDialog.openError(
                        shell,
                        "Exception",
                        "Exception while collecting Yaml from UI for key "
                                + key
                                + "\n"
                                + e.getMessage()
                                + "\n"
                                + e.getCause()
                                + getStack(e));
                diffs +=
                        "Exception while collecting Yaml from UI for key "
                                + key
                                + "\n"
                                + e.getMessage()
                                + "\n"
                                + e.getCause();
            }
        }
        return diffs;
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

    public String collectManifestYaml(Map<String, Object> yaml) {
        return collectYaml(yaml, manifestKeys);
    }

    public static final String[] dataKeys = {
        "ingestion-steps", "model-graphs", "data-graph", "extra-data-graphs",
    };

    public String collectDataYaml(Map<String, Object> yaml) {
        return collectYaml(yaml, dataKeys);
    }

    public static final String[] modelKeys = {"files", "model-graphs"};

    public String collectModelYaml(Map<String, Object> yaml) {
        return collectYaml(yaml, modelKeys);
    }

    public Object collectGeneralYaml(StringBuilder str) {
    	// The main content is the [1] child of the top composite -- presuming it is a list or map.
    	var c = (Composite)currentSubcomposite.getChildren()[1];
        return collectGeneralObject(c, str);
    }
        
    public List<Object> collectGeneralList(Composite listComp, StringBuilder str) {
    	var list = new java.util.ArrayList<Object>();
    	for (var c: listComp.getChildren()) {
    		if (c instanceof Composite cc) {
    			var children = cc.getChildren();
    			if (children.length == 3 && children[2] instanceof Text t) {
    				// scalar
    	    		var item = collectGeneralObject(t, str);
    	    		if (item != null) list.add(item);
    			} else if (children.length > 0 && children[0] instanceof Composite) {
    				// A map or list
    				if (cc.getData() == Map.class) {
    					list.add(collectGeneralMap(cc, str)); 
    				} else if (cc.getData() == List.class) {
    					list.add(collectGeneralList(cc, str)); 
    				}
    			} else {
    				// skip -- should be a header for a map or list
    			}
    		}
    	}
    	return list;
    }
    
    public Map<String,Object> collectGeneralMap(Composite mapComp, StringBuilder str) {
    	// Each child of a Map is a Composite with a key at [1] and a value at [3]
    	String key = null;
    	var map = new java.util.HashMap<String,Object>();
    	for (var c: mapComp.getChildren()) {
    		var children = ((Composite)c).getChildren();
    		if (key != null) {
    			if (Map.class == c.getData()) {
    				var m = collectGeneralMap((Composite)c, str);
    				map.put(key, m);
    				key = null;
    			} else if (List.class == c.getData()) {
    				var m = collectGeneralList((Composite)c, str);
    				map.put(key, m);
    				key = null;
    			}
    		} else if (children[1] instanceof Text t) {
    			key = t.getText();
        		Control valueComp = children[3];
        		if (valueComp.getData() != null) {
            		var item = collectGeneralObject(valueComp, str);
            		map.put(key, item);
            		key = null;
        		}
    		}
    	}
    	return map;
    }
    
    public Object collectGeneralObject(Object obj, StringBuilder str) {
    	if (obj instanceof Composite cc) {
    		var clazz = cc.getData();
    		if (clazz == Map.class) return collectGeneralMap(cc, str);
    		if (clazz == List.class) return collectGeneralList(cc, str); 
     	} else {
			return collectGeneralScalar(obj, str);
    	}
    	return null ;
    }
    
    public Object collectGeneralScalar(Object obj, StringBuilder str) {
    	if (obj instanceof Text t) {
			var clazz = t.getData();
			var text = t.getText();
			if (clazz == String.class) return text; 
			if (clazz == Integer.class) return Integer.valueOf(text);
    		str.append("Unknown data type: " + clazz + "\n");
    	} else {
    		str.append("Unknown kind of widget: " + obj.getClass() + "\n");
    	}
    	
    	return null;
    }

    public String validate(Object yamlToCheck, String kind) {
    	String newPath = ((IResource) getElement()).getFullPath().toString();
        IPath p = new Path(newPath);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IContainer currentDir = root.getFile(p).getParent();

        switch (kind) {
            case MANIFEST:
                return validateManifest((Map<String, Object>)yamlToCheck, currentDir);
            case MODEL:
                return validateModel((Map<String, Object>)yamlToCheck, currentDir);
            case DATA:
                return validateData((Map<String, Object>)yamlToCheck, currentDir);
            case GENERAL:
            	return "";
        }
        return "No such kind of yaml file: " + kind + "\n";
    }

    public String validateManifest(Map<String, Object> yamlToCheck, IContainer currentDir) {
        String diffs = "";
        diffs +=
                okKeys(
                        yamlToCheck,
                        "name",
                        null,
                        "description",
                        "copy-to-graph",
                        "perform-entity-resolution",
                        "footprint",
                        "steps");
        diffs += okString(yamlToCheck, "name", true);
        diffs += okString(yamlToCheck, "description", false);
        diffs += okURL(yamlToCheck, "copy-to-graph");
        diffs += okURL(yamlToCheck, "perform-entity-resuolution");

        var footprint = yamlToCheck.get("footprint");
        if (footprint instanceof Map<?, ?> ofootprint) {
            @SuppressWarnings("unchecked")
            var mfootprint = (Map<String, Object>) ofootprint;
            diffs += okKeys(mfootprint, null, "model-graphs", "data-graphs", "nodegroups");
            diffs += okListOfURL(mfootprint, "model-graphs", false);
            diffs += okListOfURL(mfootprint, "data-graphs", false);
            diffs += okListOfNodegroup(mfootprint, "nodegroups", currentDir);
        } else if (footprint != null) {
            diffs +=
                    "The yaml map has a 'footprint' key whose value is not a map: "
                            + footprint.getClass()
                            + "\n";
        }

        Object w = yamlToCheck.get("steps");
        if (w == null) {
            // Not required
        } else if (w instanceof java.util.List<?> wlist) {
            if (wlist.size() == 0) {
                diffs += "There are no steps in the manifest yaml\n";
            } else
                for (var step : wlist) {
                    if (step == null) {
                        diffs += "An item in the 'steps' list is null\n";
                    } else if (step instanceof Map<?, ?> stepm) {
                        @SuppressWarnings("unchecked")
                        var stepmap = (Map<String, Object>) stepm;
                        if (stepmap.get("data") != null) {
                            diffs += okKeys(stepmap, "data", null);
                            diffs += okFile(stepmap, "data", currentDir); // FIXME what is this?
                        } else if (stepmap.get("model") != null) {
                            diffs += okKeys(stepmap, "model", null);
                            diffs += okFile(stepmap, "model", currentDir);
                        } else if (stepmap.get("nodegroups") != null) {
                            diffs += okKeys(stepmap, "nodegroups", null);
                            diffs += okNodegroup(stepmap, "nodegroups", currentDir);
                        } else if (stepmap.get("manifest") != null) {
                            diffs += okKeys(stepmap, "manifest", null);
                            diffs += okFile(stepmap, "manifest", currentDir);
                        } else if (stepmap.get("copygraph") != null) {
                            diffs += okKeys(stepmap, "copygraph", null);
                            var o = stepmap.get("copygraph");
                            if (o == null) {
                                diffs += "The yaml map has a 'copygraph' key whose value is null\n";
                            } else if (o instanceof Map<?, ?> m) {
                                @SuppressWarnings("unchecked")
                                var map = (Map<String, Object>) m;
                                diffs += okKeys(map, "from-graph", "to-graph", null);
                                diffs += okURL(map, "from-graph");
                                diffs += okURL(map, "to-graph");
                            } else {
                                diffs +=
                                        "The yaml map has a 'copygraph' key whose value is not a map: "
                                                + footprint.getClass()
                                                + "\n";
                            }
                        }
                    } else {
                        diffs +=
                                "An item in the 'steps' list is not a Map: "
                                        + step.getClass()
                                        + "\n";
                    }
                }
        } else {
            diffs +=
                    "The yaml map has a 'steps' key whose value is not a list: "
                            + w.getClass()
                            + "\n";
        }
        return diffs;
    }

    public String validateModel(Map<String, Object> yamlToCheck, IContainer currentDir) {
        String diffs = "";
        diffs += okKeys(yamlToCheck, "files", null, "model-graphs");
        diffs += okListOfFile(yamlToCheck, "files", currentDir);
        diffs += okListOfURL(yamlToCheck, "model-graphs", true);
        return diffs;
    }

    public String validateData(Map<String, Object> yamlToCheck, IContainer currentDir) {
        String diffs = "";
        diffs +=
                okKeys(
                        yamlToCheck,
                        "ingestion-steps",
                        null,
                        "data-graph",
                        "extra-data-graphs",
                        "model-graphs");
        diffs += okURL(yamlToCheck, "data-graph");
        diffs += okListOfURL(yamlToCheck, "extra-data-graphs", false);
        diffs += okListOfURL(yamlToCheck, "model-graphs", true);

        // Check ingestion-steps
        String key = "ingestion-steps";
        var value = yamlToCheck.get(key);
        if (value == null) {
            // Not required
        } else if (value instanceof List<?> steps) {
            if (steps.size() == 0) {
                diffs += "There are no ingestions-steps in the data yaml\n";
            } else
                for (var item : steps) {
                    if (item instanceof Map<?, ?> step) {
                        @SuppressWarnings("unchecked")
                        var map = (Map<String, Object>) item;

                        if (map.get("nodegroup") != null && map.get("csv") != null) {
                            diffs += okKeys(map, "nodegroup", "csv", null);
                            diffs += okNodegroup(map, "nodegroup", currentDir);
                            diffs += okFile(map, "csv", currentDir);
                        } else if (map.get("class") != null && map.get("csv") != null) {
                            diffs += okKeys(map, "class", "csv", null);
                            diffs += okURL(map, "class");
                            diffs += okFile(map, "csv", currentDir);
                        } else if (map.get("owl") != null) {
                            diffs += okKeys(map, "owl", null);
                            diffs += okFile(map, "owl", currentDir);
                        } else if (map.get("name") != null
                                && map.get("creator") != null
                                && map.get("nodegroup_json") != null) {
                            diffs +=
                                    okKeys(
                                            map,
                                            "name",
                                            "creator",
                                            "nodegroup_json",
                                            null,
                                            "comment");
                            diffs += okString(map, "name", true);
                            diffs += okString(map, "creator", true);
                            diffs += okString(map, "comment", false);
                            diffs +=
                                    okString(
                                            map,
                                            "nodegroup_json",
                                            true); // FIXME - should be legitimate json?
                        } else if (map.get("count") != null && map.get("nodegroup") != null) {
                            diffs += okKeys(map, "count", "nodegroup", null, "constraints");
                            diffs += okNumber(map, "count");
                            diffs += okNodegroup(map, "nodegroup", currentDir);
                            diffs += okListOfString(map, "constraints");
                        } else {
                            diffs +=
                                    "A step does not have the required keys to be a correct step; has just";
                            for (var k : map.keySet()) diffs += " " + k;
                            diffs += "\n";
                        }
                    } else {
                        diffs +=
                                "Element of '"
                                        + key
                                        + "' list is not a Map: "
                                        + item.getClass()
                                        + "\n";
                    }
                }
        } else {
            diffs += "Element '" + key + "' has the wrong type: " + value.getClass() + "\n";
        }

        return diffs;
    }

    // The array of Strings is expected to contain the required keys, then a null, then optional
    // keys
    public String okKeys(Map<String, Object> yamlToCheck, String... keys) {
        String diffs = "";
        var keyset = new java.util.HashSet<String>();
        keyset.addAll(yamlToCheck.keySet());
        boolean required = true;
        for (String key : keys) {
            if (key == null) {
                required = false;
                continue;
            }
            if (!keyset.remove(key))
                if (required) diffs += "Required key '" + key + "' is not present\n";
        }
        if (keyset.size() > 0) {
            diffs += "The model yaml has excess keys:";
            for (var item : keyset) {
                diffs += " " + item;
            }
            diffs += "\n";
        }
        return diffs;
    }

    public String okString(Map<String, Object> yamlToCheck, String key, boolean nonEmpty) {
        String diffs = "";
        var value = yamlToCheck.get(key);
        if (value == null) {
            if (nonEmpty)
                diffs += "The value for required key '" + key + "' may not be null or empty\n";
        } else if (value instanceof String text) {
            if (nonEmpty && text.trim().isEmpty())
                diffs += "The value for required key '" + key + "' may not be an empty String\n";
            // Just a string
        } else {
            diffs +=
                    "Value for '"
                            + key
                            + "' key is expected to be a String: "
                            + value.getClass()
                            + "\n";
        }
        return diffs;
    }

    public String okNumber(Map<String, Object> yamlToCheck, String key) {
        String diffs = "";
        var value = yamlToCheck.get(key);
        if (value == null) {
            diffs += "The value for required key '" + key + "' may not be null\n";
        } else if (value instanceof Integer n) {
            if (n < 0) diffs += "The value for required key '" + key + "' may not be negative\n";
        } else {
            diffs +=
                    "Value for '"
                            + key
                            + "' key is expected to be an Integer: "
                            + value.getClass()
                            + "\n";
        }
        return diffs;
    }

    public String okURL(Map<String, Object> yamlToCheck, String key) {
        String diffs = "";
        var value = yamlToCheck.get(key);
        if (value == null) {
            // Not required
        } else if (value instanceof String text) {
            diffs += okURL(text);
        } else {
            diffs +=
                    "Value for '"
                            + key
                            + "' key is expected to be a String: "
                            + value.getClass()
                            + "\n";
        }
        return diffs;
    }

    public String okURL(String text) { // FIXME - are empty elements allowed in lists?
        try {
            // Check if the text is a valid URL
            text = text.trim();
            if (text.isEmpty()) return "";
            URL u = new URL(text);
            HttpURLConnection huc = (HttpURLConnection) u.openConnection();
            huc.setRequestMethod("HEAD");
            if (true || huc.getResponseCode() == HttpURLConnection.HTTP_OK)
                return ""; // FIXME - unable to test URLs for now
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
        return "URL " + text + " does not exist\n";
    }

    public String okListOfURL(Map<String, Object> yamlToCheck, String key, boolean allowSingleton) {
        String diffs = "";
        var value = yamlToCheck.get(key);
        if (value == null) {
            // Not required
        } else if (allowSingleton && value instanceof String s) {
            diffs += okURL(s);
        } else if (value instanceof List<?> list) {
            for (var text : list) {
                if (text instanceof String s) diffs += okURL(s);
                else
                    diffs +=
                            "Element of '"
                                    + key
                                    + "' list is not a String: "
                                    + text.toString()
                                    + " is a "
                                    + text.getClass()
                                    + "\n";
            }
        } else {
            diffs += "Element '" + key + "' has the wrong type: " + value.getClass() + "\n";
        }
        return diffs;
    }

    public String okListOfString(Map<String, Object> yamlToCheck, String key) {
        String diffs = "";
        var value = yamlToCheck.get(key);
        if (value == null) {
            // Not required
        } else if (value instanceof List<?> list) {
            for (var text : list) {
                if (text instanceof String s) {
                } // FIXME  - allowed to be empty? or null?
                else
                    diffs +=
                            "Element of '"
                                    + key
                                    + "' list is not a String: "
                                    + text.toString()
                                    + " is a "
                                    + text.getClass()
                                    + "\n";
            }
        } else {
            diffs += "Element '" + key + "' has the wrong type: " + value.getClass() + "\n";
        }
        return diffs;
    }

    public String okFile(Map<String, Object> yamlToCheck, String key, IContainer currentDir) {
        String diffs = "";
        var value = yamlToCheck.get(key);
        if (value == null) {
            // Not required
        } else if (value instanceof String text) {
            diffs += okFile(text, currentDir);
        } else {
            diffs += "Element '" + key + "' has the wrong type: " + value.getClass() + "\n";
        }
        return diffs;
    }

    public String okFile(String text, IContainer currentDir) {
        try {
            // Check if the text is a path in the workspace
            IPath p = new Path(text);
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            if (currentDir.getFile(p).exists()) return "";
            try {
                if (root.getFile(p).exists()) return "";
            } catch (Exception e) {
                // Just skip -- throws exception if p does not contain a project name
            }
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
        return "File " + text + " does not exist\n";
    }

    public String okListOfFile(Map<String, Object> yamlToCheck, String key, IContainer currentDir) {
        String diffs = "";
        var value = yamlToCheck.get(key);
        if (value == null) {
            // Not required
        } else if (value instanceof List<?> list) {
            for (var text : list) {
                if (text instanceof String s) diffs += okFile(s, currentDir);
                else
                    diffs +=
                            "Element of '"
                                    + key
                                    + "' list is not a String: "
                                    + text.toString()
                                    + " is a "
                                    + text.getClass()
                                    + "\n";
            }
        } else {
            diffs += "Element '" + key + "' has the wrong type: " + value.getClass() + "\n";
        }
        return diffs;
    }

    public String okListOfNodegroup(
            Map<String, Object> yamlToCheck, String key, IContainer currentDir) {
        String diffs = "";
        var value = yamlToCheck.get(key);
        if (value == null) {
            // Not required
        } else if (value instanceof List<?> list) {
            for (var text : list) {
                if (text instanceof String s) diffs += okNodegroup(s, currentDir);
                else
                    diffs +=
                            "Element of '"
                                    + key
                                    + "' list is not a String: "
                                    + text.toString()
                                    + " is a "
                                    + text.getClass()
                                    + "\n";
            }
        } else {
            diffs += "Element '" + key + "' has the wrong type: " + value.getClass() + "\n";
        }
        return diffs;
    }

    public String okNodegroup(Map<String, Object> yamlToCheck, String key, IContainer currentDir) {
        String diffs = "";
        var value = yamlToCheck.get(key);
        if (value == null) {
            // Not required
        } else if (value instanceof String s) {
            diffs += okNodegroup(s, currentDir);
        } else {
            diffs += "Element '" + key + "' has the wrong type: " + value.getClass() + "\n";
        }
        return diffs;
    }

    public String okNodegroup(String text, IContainer currentDir) {
        // check that this is a relative path to a folder holding a store.csv file
        try {
            // Check if the text is a path in the workspace
            IPath p = new Path(text.trim());
            if (currentDir.getFolder(p).getFile("store.csv").exists()) return "";
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
        return "File " + text + " is not a nodegroup folder\n";
    }

    /**
     * Adds a default implementation for widgetDefaultSelected, which just delegates to
     * widgetSelected
     */
    public static interface SelectionAdapter extends SelectionListener {

        @Override
        public abstract void widgetSelected(SelectionEvent e);

        @Override
        public default void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    }
    
    /** A copy sufficient for yaml purposes -- not a true clone. */
    public Object copy(Object in) {
    	if (in instanceof String) return in;
    	if (in instanceof Integer) return in;
    	if (in instanceof List<?> list) {
    		var nlist = new java.util.ArrayList<Object>();
    		for (var item: list) nlist.add(copy(item));
    		return nlist;
    	}
    	if (in instanceof Map<?,?> map) {
    		var nmap = new HashMap<>();
    		for (var entry: map.entrySet()) {
    			nmap.put(entry.getKey(), copy(entry.getValue()));
    		}
    		return nmap;
    	}
    	MessageDialog.openError(shell, "Error", "Unsupported type in copy: " + in.getClass());
    	return null;
    }
    
    /** A copy sufficient for yaml purposes -- not a true clone. */
    public Object copyChecked(Object in) {
    	@SuppressWarnings("unchecked")
		var c = copy(in);
    	var diffs = compareYaml(c, in);
    	if (!diffs.isEmpty()) {
        	MessageDialog.openError(shell, "Error", "Copy failed to produce a correct copy:\n" + diffs);
    	}
    	return c;
    }
}
