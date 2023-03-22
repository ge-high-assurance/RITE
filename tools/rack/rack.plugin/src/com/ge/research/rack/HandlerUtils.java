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

import com.ge.research.rack.utils.NodegroupUtil;
import com.ge.research.rack.utils.OntologyUtil;
import com.ge.research.rack.utils.RackConsole;
import com.ge.research.rack.views.NodegroupsView;
import com.ge.research.rack.views.OntologyTreeView;
import com.ge.research.rack.views.ViewUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class HandlerUtils {

    public static void showNodegroupTable(IWorkbenchWindow window) {
        try {
            final IViewPart view4 = window.getActivePage().findView(NodegroupsView.ID);
            window.getActivePage().hideView(view4);
            window.getActivePage().showView(NodegroupsView.ID);
        } catch (Exception e) {
            String message = "Cannot populate nodegroups views";
            RackConsole.getConsole().error(message);
        }
    }

    public static void showOntologyTree() {
        Display.getDefault()
                .asyncExec(
                        new Thread(
                                () -> {
                                    Pair<IWorkbenchPage, IViewPart> pair =
                                            ViewUtils.getPageAndViewByViewId(OntologyTreeView.ID);
                                    if (pair != null
                                            && pair.getLeft() != null
                                            && pair.getRight() != null) {
                                        pair.getLeft().hideView(pair.getRight());
                                        try {
                                            pair.getLeft().showView(OntologyTreeView.ID);
                                        } catch (Exception e) {
                                            RackConsole.getConsole()
                                                    .error("Unable to show ontology tree view");
                                        }
                                    }
                                }));
    }

    public static void showNodegroupTable() {

        Display.getDefault()
                .asyncExec(
                        new Thread(
                                () -> {
                                    final IWorkbenchWindow[] windows =
                                            PlatformUI.getWorkbench().getWorkbenchWindows();

                                    Arrays.stream(windows)
                                            .flatMap(w -> Arrays.stream(w.getPages()))
                                            .filter(
                                                    page ->
                                                            null
                                                                    != page.findView(
                                                                            NodegroupsView.ID))
                                            .forEach(
                                                    page -> {
                                                        try {
                                                            page.hideView(
                                                                    page.findView(
                                                                            NodegroupsView.ID));
                                                            page.showView(NodegroupsView.ID);
                                                        } catch (final PartInitException e) {
                                                            RackConsole.getConsole()
                                                                    .error(
                                                                            "Cannot populate nodegroups");
                                                        }
                                                    });
                                }));
    }

    public static void loadNodegroups() {
        try {
            OntologyUtil.getoInfo();
            RackConsole.getConsole().print("Fetching all nodegroups from RACK... ");
            NodegroupUtil.init();
            NodegroupUtil.getAllNodegroups();
            RackConsole.getConsole().printOK();
        } catch (Exception e) {
        	RackConsole.getConsole().printFAIL();
            RackConsole.getConsole().error("Cannot fetch nodegroups");
        }
    }

    public static List<String> getCurrentSelection(ExecutionEvent event) {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        List<String> paths = new ArrayList<>();

        if (selection instanceof IStructuredSelection
                && ((IStructuredSelection) selection).size() > 0) {
            Object[] selObjs = ((IStructuredSelection) selection).toArray();

            for (Object selObj : selObjs) {
                if (selObj instanceof IFile) {
                    IFile selIFile = (IFile) selObj;
                    paths.add(selIFile.getProject().getLocation().toFile().getAbsolutePath());
                } else if (selObj instanceof IProject) {
                    IProject selIProject = (IProject) selObj;
                    paths.add(selIProject.getLocation().toFile().getAbsolutePath());
                } else if (selObj instanceof IFolder) {
                    IFolder selIFolder = (IFolder) selObj;
                    paths.add(selIFolder.getProject().getLocation().toFile().getAbsolutePath());
                } else {
                }
            }
        } else {
            // VerdictLogger.warning("Selection is not recognized!");
        }

        return paths;
    }
}
