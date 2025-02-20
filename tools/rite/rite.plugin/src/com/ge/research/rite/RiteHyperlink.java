package com.ge.research.rite;

import java.nio.file.Paths;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class RiteHyperlink implements IHyperlink{

	 private String fPath = "";
	 private IRegion fUrlRegion;
	 
	public RiteHyperlink(IRegion region, String path) {
		fPath = path;
		fUrlRegion = region;
	}
	@Override
	public IRegion getHyperlinkRegion() {
		return fUrlRegion;
	}

	@Override
	public String getHyperlinkText() {
		return fPath;
	}

	@Override
	public String getTypeLabel() {
		return null;
	}

	@Override
	public void open() {
	
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	    if (window != null) {
	        IWorkbenchPage page = window.getActivePage();
	        if (page != null) {
	                try {
	                	IDE.openEditor(page, Paths.get(getHyperlinkText()).toUri(),"org.eclipse.ui.DefaultTextEditor", true);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            
	        }
	    }
	}

}
