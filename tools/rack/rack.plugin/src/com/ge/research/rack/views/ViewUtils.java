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

import com.ge.research.rack.utils.RackConsole;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jface.resource.ResourceLocator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ViewUtils {
	public static Image getIcon(String name) {
		return ResourceLocator.imageDescriptorFromBundle("rack.plugin", "icons/" + name).get().createImage();
	}

	public static Pair<IWorkbenchPage, IViewPart> getPageAndViewByViewId(String viewId) {
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			IWorkbenchPage[] pages = windows[i].getPages();
			for (int j = 0; j < pages.length; j++) {
				IViewPart view = pages[j].findView(viewId);
				if (view != null) {
					return Pair.of(pages[j], view);
				}
			}
		}
		return null;
	}

	public static void showView(String viewId) {
		try {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IViewPart view = window.getActivePage().findView(viewId);
			window.getActivePage().hideView(view);
			window.getActivePage().showView(viewId);
		} catch (Exception e) {
			RackConsole.getConsole().error("Unable to show view : " + viewId);
		}
	}

	public static void showProgressView() {
		Display.getDefault().asyncExec(new Thread(() -> {
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if (windows.length > 0) {
				IWorkbenchWindow window = windows[0];
				IWorkbenchPage[] pages = window.getPages();
				if (pages.length > 0) {
					IWorkbenchPage page = pages[0];
					try {
						page.showView(org.eclipse.ui.progress.IProgressConstants.PROGRESS_VIEW_ID);
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						RackConsole.getConsole().error("Unable to show progress view");
					}
				}
			}
		}));
	}
}
