package com.ge.research.rack;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * 
 */

/**
 * @author Saswata Paul
 *
 */
public class Arp4754ButtonHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        System.out.println("Arp4754ButtonHandler");
        // Launch the AutoGsnMainView
        JavaFXAppLaunchManager.arp4754ReportMainViewLaunch();

        return null;
    }
}