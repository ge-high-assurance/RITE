/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2025, GE Aerospace and Galois, Inc.
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

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import java.io.File;

public class RiteHyperlinkDetector extends AbstractHyperlinkDetector implements IHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer arg0, IRegion arg1, boolean arg2) {
		// TODO Auto-generated method stub
		IDocument document = arg0.getDocument();
		int offset = arg1.getOffset();

		// extract relevant characters
		IRegion lineRegion;
		String candidate;

		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IEditorPart editor = page.getActiveEditor();
		IEditorInput input = editor.getEditorInput();
		IPath path = ((FileEditorInput) input).getPath();
		File file = new File(path.makeAbsolute().toString());
		if (!file.getName().endsWith(".yaml")) {
			return null;
		}
		String directory = file.getParentFile().toString();
		try {
			lineRegion = document.getLineInformationOfOffset(offset);
			candidate = document.get(lineRegion.getOffset(), lineRegion.getLength());
		} catch (BadLocationException ex) {
			return null;
		}

		int index;
		index = candidate.indexOf("model:");
		if (index == -1) {
			index = candidate.indexOf("data:");
		}
		if (index == -1) {
			index = candidate.indexOf("nodegroup:");
		}
		if (index == -1) {
			index = candidate.indexOf("manifest:");
		}

		if (index == -1) {
			index = candidate.indexOf(".owl");
		}

		if (index == -1) {
			index = candidate.indexOf(".csv");
		}

		if (index == -1) {
			return null;
		}
		// get the rightmost text
		String relativePathVanilla = getPath(candidate);
		String relativePath = relativePathVanilla;
		if (OS.PLATFORM.contains("win32")) {
			relativePath = relativePath.replace("/", "\\");
		}
		File newFile = new File(directory + File.separator + relativePath);
		if (newFile.exists()) {
			IRegion targetRegion = new Region(lineRegion.getOffset() + candidate.indexOf(relativePathVanilla),
					relativePathVanilla.length());
			// logic to detect file from relative path
			return new IHyperlink[] { new RiteHyperlink(targetRegion, newFile.getAbsolutePath()) };
		}

		return null;
	}

	public static String getPath(String line) {
        String path = "";
        if (line.contains("model:")) {
            int index = line.indexOf("model:");
            int j = index + "model:".length();
            while (j < line.length() && line.charAt(j) != ' ') {
                j++;
            }
            if (j >= line.length()) {
                return "";
            }
            path = line.substring(j);
        }

        if (line.contains("data:")) {
            int index = line.indexOf("data:");
            int j = index + "data:".length();
            while (j < line.length() && line.charAt(j) != ' ') {
                j++;
            }
            if (j >= line.length()) {
                return "";
            }
            path = line.substring(j);
        }

        if (line.contains("nodegroup:")) {
            int index = line.indexOf("nodegroup:");
            int j = index + "nodegroup:".length();
            while (j < line.length() && line.charAt(j) != ' ') {
                j++;
            }
            if (j >= line.length()) {
                return "";
            }
            path = line.substring(j);
        }

        if (line.contains(".owl")) {
            path = line.trim();
            int j = path.length() - 1;
            while (j > 0 && path.charAt(j) != ' ') {
                j--;
            }
            if (j == 0) {
                return "";
            }
            path = path.substring(j);
        }

        if (line.contains(".csv")) {
        	
            int j = line.indexOf(".csv");
            int i = j;
            if(j + ".csv".length() < line.length() && line.charAt(j + ".csv".length()) == '\"') {
              
                while (i > 0 && line.charAt(i) != '\"') {
                i--;
            }
           
            }
            else {
            	while(i > 0 && line.charAt(i) != ' ') {
            		i--;
            	}
            }
            if (i == 0) {
                return "";
            }
             path = line.substring(i + 1, j + ".csv".length());
           
        	}
        

	return path.trim();
}}
