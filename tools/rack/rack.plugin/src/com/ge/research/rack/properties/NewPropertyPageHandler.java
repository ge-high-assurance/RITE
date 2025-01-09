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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * This is the action for the command associated with the 'New yaml file' menu action under the RACK
 * top-level menu item.
 */
public class NewPropertyPageHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        var shell = org.eclipse.swt.widgets.Display.getCurrent().getActiveShell();
        IProject project = null;
        var window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            var sel = window.getSelectionService().getSelection();
            if (sel instanceof IStructuredSelection selection) {
                Object firstElement = selection.getFirstElement();
                if (firstElement instanceof IAdaptable adapt) {
                    project = (IProject) adapt.getAdapter(IProject.class);
                }
            } else {
                System.out.println(sel.getClass());
            }
        }
        if (project == null) {
            MessageDialog.openError(
                    shell, "", "Select a project in which to put the new yaml file");
            return null;
        }
        var path = project.getFile("temp.yaml").getFullPath();
        IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        String ID = "com.ge.research.rack.properties.dataPropertyPage";
        org.eclipse.ui.dialogs.PreferencesUtil.createPropertyDialogOn(shell, f, ID, null, null, 0)
                .open();
        return null;
    }

    /** This is the handler for the context menu action 'Properties' in a Yaml Editor window */
    public static class Editor extends AbstractHandler
            implements org.eclipse.ui.IEditorActionDelegate {

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            var shell = org.eclipse.swt.widgets.Display.getCurrent().getActiveShell();
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            IEditorInput input = window.getActivePage().getActiveEditor().getEditorInput();
            IFile file = ((org.eclipse.ui.part.FileEditorInput) input).getFile();
            String ID = "com.ge.research.rack.properties.dataPropertyPage";
            org.eclipse.ui.dialogs.PreferencesUtil.createPropertyDialogOn(
                            shell, file, ID, null, null, 0)
                    .open();
            return null;
        }

        // The following necessary implementations do not seem to be needed in practice

        @Override
        public void run(IAction action) {}

        @Override
        public void selectionChanged(IAction action, ISelection selection) {}

        @Override
        public void setActiveEditor(IAction action, IEditorPart targetEditor) {}
    }
}
