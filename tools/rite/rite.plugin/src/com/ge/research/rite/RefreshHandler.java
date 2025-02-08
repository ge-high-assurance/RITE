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
package com.ge.research.rite;

import com.ge.research.rite.utils.IngestionTemplateUtil;
import com.ge.research.rite.utils.RackConsole;
import com.ge.research.rite.views.OntologyTreeView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class RefreshHandler extends AbstractHandler {

    private static final String ONTOLOGY_ERROR = "Unable to refresh Ontology View";
    private static final String CSV_TEMPLATE_ERROR = "Unable to get CSV templates";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        // hide and show ontology view, also refresh nodegroups views
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

        // refresh ontology view
        final IViewPart view = window.getActivePage().findView(OntologyTreeView.ID);

        window.getActivePage().hideView(view);

        try {
            window.getActivePage().showView(OntologyTreeView.ID);
        } catch (final PartInitException e1) {
            RackConsole.getConsole().error(ONTOLOGY_ERROR);
        }

        // refresh nodegroups view
        refreshNodegroups();
        HandlerUtils.showNodegroupTable(window);
        RackConsole.getConsole().print("Refresh done");
        return null;
    }

    public static void refreshNodegroups() {
        BuildIngestionNodegroupsHandler.bookkeepNodegroups();
        HandlerUtils.loadNodegroups();
        try {
            IngestionTemplateUtil.buildCsvTemplates();
        } catch (Exception e) {
            RackConsole.getConsole().error(CSV_TEMPLATE_ERROR);
        }
    }
}
