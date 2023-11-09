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
package com.ge.research.rack.views;

import com.ge.research.rack.utils.ConnectionUtil;
import com.ge.research.rack.utils.RackConsole;
import com.google.inject.*;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained
 * from the model. The sample creates a dummy model on the fly, but a real implementation would
 * connect to the model available either in this or another plug-in (e.g. the workspace). The view
 * is connected to the model using a content provider.
 *
 * <p>The view uses a label provider to define how model objects should be presented in the view.
 * Each view can present the same model objects using different labels and icons, if needed.
 * Alternatively, a single label provider can be shared between views in order to ensure that
 * objects of the same type are presented in the same way everywhere.
 *
 * <p>
 */
public class QueryResultsView extends ViewPart {

    /** The ID of the view as specified by the extension. */
    public static final String ID = "rackplugin.views.QueryResults";

    @Inject IWorkbench workbench;

    private TableViewer viewer;
    private static Table table;
    public static com.ge.research.semtk.resultSet.Table results = null;

    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
        @Override
        public String getColumnText(Object obj, int index) {
            return getText(obj);
        }

        @Override
        public Image getColumnImage(Object obj, int index) {
            return getImage(obj);
        }

        @Override
        public Image getImage(Object obj) {
            return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        if (results == null) {
            return;
        }

        if (!ConnectionUtil.ping()) {
            return;
        }

        Display display = Display.getCurrent();

        table = new Table(parent, SWT.NONE);

        table.setSize(1130, 600);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setFocus();
        table.setHeaderBackground(display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
        table.setHeaderForeground(display.getSystemColor(SWT.COLOR_TITLE_FOREGROUND));

        // render columns
        String[] cols = {};
        try {
            cols = results.getHeaderAsCSVString().split(",");
        } catch (Exception e) {
            RackConsole.getConsole()
                    .error("Unable to process query result headers for current nodegroup");
            return;
        }
        for (String columnName : cols) {
            TableColumn column = new TableColumn(table, SWT.LEFT);
            column.setText(columnName);
            column.pack();
        }

        for (int i = 0; i < results.getNumRows(); i++) {
            ArrayList<String> data = results.getRow(i);
            TableItem item = new TableItem(table, SWT.NONE);
            int j = 0;
            for (String entry : data) {
                item.setText(j, entry);
                j++;
            }
        }

        table.pack();
    }

    public static void pack() {
        ArrayList<TableColumn> columns = new ArrayList<>(Arrays.asList(table.getColumns()));
        for (TableColumn column : columns) {
            column.pack();
        }
        table.pack();
    }

    @Override
    public void setFocus() {
        if (viewer != null) {
            viewer.getControl().setFocus();
        }
    }
}
