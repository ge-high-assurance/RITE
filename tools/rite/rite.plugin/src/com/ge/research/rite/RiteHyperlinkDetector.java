package com.ge.research.rite;

import java.io.File;

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

public class RiteHyperlinkDetector extends AbstractHyperlinkDetector implements IHyperlinkDetector{

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
		  IPath path = ((FileEditorInput)input).getPath();
		  File file = new File(path.makeAbsolute().toString());
		  if(!file.getName().endsWith(".yaml")) {
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
		  if(index == -1) {
			  index = candidate.indexOf("data:");
		  }
		  if(index == -1) {
			  index = candidate.indexOf("nodegroup:");
		  }
		  if(index == -1) {
			  index = candidate.indexOf("manifest:");
		  }
		  
		  if(index == -1) {
			  index = candidate.indexOf(".owl");
		  }
		  
		  if(index == -1) {
			  index = candidate.indexOf(".csv");
		  }
		  
		  if(index == -1) {
			  return null;
		  }
		  // get the rightmost text 
		  String relativePathVanilla = getPath(candidate);
		  String relativePath = relativePathVanilla;
		  if(OS.PLATFORM.contains("win32")){
			  relativePath = relativePath.replace("/", "\\");
		 }
		  File newFile = new File(directory + File.separator + relativePath);
		       if(newFile.exists()){
		        IRegion targetRegion = new Region(lineRegion.getOffset() + candidate.indexOf(relativePathVanilla), relativePathVanilla.length());
		    	 //logic to detect file from relative path
		       return new IHyperlink[] { new RiteHyperlink(targetRegion, newFile.getAbsolutePath()) };
	           }  	  
	
		return null;
	}
	
	public static String getPath(String line) {
		String path = "";
		 if(line.contains("model:") ) {
			  int index = line.indexOf("model:");
			  int j = index + "model:".length();
			  while(j < line.length() && line.charAt(j) != ' ') {
				  j++;
			  }
			  if(j >= line.length()) {
				  return "";
			  }
		     path = line.substring(j);	 
		     
		 }
		 
		 if(line.contains("data:") ) {
			  int index = line.indexOf("data:");
			  int j = index + "data:".length();
			  while(j < line.length() && line.charAt(j) != ' ') {
				  j++;
			  }
			  if(j >= line.length()) {
				  return "";
			  }
		     path = line.substring(j);	 
		     
		 }
		 
		 if(line.contains("nodegroup:") ) {
			  int index = line.indexOf("nodegroup:");
			  int j = index + "nodegroup:".length();
			  while(j < line.length() && line.charAt(j) != ' ') {
				  j++;
			  }
			  if(j >= line.length()) {
				  return "";
			  }
		     path = line.substring(j);	 
		     
		 }
		 
		 if(line.contains(".owl") ) {
			  path = line.trim();
			  int j = path.length() - 1;
			  while(j > 0 && path.charAt(j) != ' ') {
				  j--;
			  }
			  if(j == 0) {
				  return "";
			  }
			  path = path.substring(j);
			 
		    
		 }
		 
		 if(line.contains(".csv")) {
			  int j = line.indexOf(".csv");
			  int i = j;
			  while(i > 0 && line.charAt(i) != '\"') {
				  i--;
			  }
			  if(i == 0) {
				  return "";
			  }
			  path = line.substring(i+1,j+".csv".length());
		 }
		
		 return path.trim();
	}

}
