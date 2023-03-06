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

import com.ge.research.rack.utils.IngestionTemplateUtil;
import com.ge.research.rack.utils.ProjectUtils;
import com.ge.research.rack.utils.RackConsole;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.*;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.handlers.HandlerUtil;

public class LoadNodeGroupsHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        /*  if (OwlUtil.coreClasses == null
                || OwlUtil.coreClasses.size() == 0
                || OwlUtil.overlayClasses == null
                || OwlUtil.overlayClasses.size() == 0) {
            RackConsole.getConsole()
                    .error(
                            "Core/Overlay ontologies are not correctly identified by the plugin, please select the correct Core/Overlay projects in RACK preference page");
            return null;
        } */

        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        try {
            String consoleViewId = IConsoleConstants.ID_CONSOLE_VIEW;
            IConsoleView consoleView =
                    (IConsoleView)
                            PlatformUI.getWorkbench()
                                    .getActiveWorkbenchWindow()
                                    .getActivePage()
                                    .showView(consoleViewId);
            consoleView.display(RackConsole.getConsole());
            BuildIngestionNodegroupsHandler.bookkeepNodegroups();
            HandlerUtils.loadNodegroups();
            HandlerUtils.showNodegroupTable(window);
            IngestionTemplateUtil.buildCsvTemplates();
            try {
                ProjectUtils.setupNodegroupsFolder();
            } catch (Exception e) {
                RackConsole.getConsole().error("Unable to setup nodegroups folders");
            }
        } catch (Exception ex) {
            RackConsole.getConsole().error("Unable to fetch nodegroups");
        }

        return null;
    }
}
