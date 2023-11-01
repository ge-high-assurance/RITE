package com.ge.research.rack.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class RackImportWizard extends Wizard implements INewWizard {
	
	private IWorkbench workbench;
    private IStructuredSelection selection;
    
    protected RackImportWizardPageOne one;
    protected RackImportWizardPageTwo two;
    

    public RackImportWizard() {
        setWindowTitle("Rack Import Wizard");
        // You can set a default image for the wizard here.
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;
    }

    @Override
    public void addPages() {
        // Add wizard pages for user input.
    	one = new RackImportWizardPageOne();
        two = new RackImportWizardPageTwo();
        addPage(one);
        addPage(two);
        // addPage(new MyNewWizardPage("MyNewWizardPage"));
    }

    @Override
    public boolean performFinish() {
        // Implement the logic to create new resources here.
        System.out.println(one.getText1());
        System.out.println(two.getText1());

        return true;
    }
    
}