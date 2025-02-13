package com.ge.research.rite;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.ge.research.rite.utils.RackConsole;
import com.ge.research.rite.views.ViewUtils;

public class RiteHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		ViewUtils.pinConsole(RackConsole.getConsole());
		return null;
	}

}