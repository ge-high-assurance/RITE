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

import com.ge.research.rack.utils.ConnectionUtil;
import com.ge.research.rack.utils.RackConsole;
import com.ge.research.rack.views.ClearRackDialog;
import com.ge.research.rack.views.SelectDataGraphsDialog;
import com.ge.research.rack.views.ViewUtils;
import com.ge.research.semtk.nodeGroupStore.client.NodeGroupStoreRestClient;
import com.ge.research.semtk.resultSet.Table;
import com.ge.research.semtk.resultSet.TableResultSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.handlers.HandlerUtil;

import java.util.ArrayList;

public class ClearAllHandler extends AbstractHandler {

    public static void clearRackModel() {

        RackConsole.getConsole().print("Clearing all Ontologies... ");
        try {
            ConnectionUtil.getOntologyUploadClient().clearAll();
            ConnectionUtil.getDataGraphClient().clearAll();

            // clear rest of the data graphs too
            TableResultSet graphInfo = ConnectionUtil.getGraphInfo();
            for (int i = 0; i < graphInfo.getTable().getNumRows(); i++) {
                ArrayList<String> entry = graphInfo.getResults().getRow(i);
                String dataGraph = entry.get(0);
                ConnectionUtil.getDataGraphClient(dataGraph).clearAll();
            }
            RackConsole.getConsole().printOK();

        } catch (Exception e) {
            RackConsole.getConsole().printFAIL();
            RackConsole.getConsole()
                    .error("Clearing ontologies failed, check connection parameters and retry");
        }
    }

    public static void deleteAllNodegroups(IProgressMonitor monitor) throws Exception {

        NodeGroupStoreRestClient ngClient = ConnectionUtil.getNGSClient();
        TableResultSet nodegroups = ngClient.executeGetNodeGroupMetadata();
        Table ngTable = nodegroups.getTable();
        ArrayList<ArrayList<String>> rows = ngTable.getRows();
        SubMonitor subMonitor = SubMonitor.convert(monitor);

        for (ArrayList<String> row : rows) {
            if (subMonitor.isCanceled()) {
                return;
            }
            String id = row.get(0);
            RackConsole.getConsole().print("Deleting nodegroup: " + id + "... ");
            try {
                ngClient.deleteStoredNodeGroup(id);
                monitor.worked((int) (100 / rows.size()) + 1);
                RackConsole.getConsole().printOK();
                // boolean isCore = (Core.defaultNodegroups.contains(id)) ? true : false;
                // NodegroupUtil.removeFromYaml(id, isCore);

            } catch (Exception e) {
                RackConsole.getConsole().printFAIL();
                String message = "Deletion of nodegroup" + id + " failed";
                RackConsole.getConsole().error(message);
            }
        }
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
    	
    	String queryNodegroup = "";
       
    	ClearRackDialog dialog =
                new ClearRackDialog(
                        Display.getDefault().getActiveShell(), queryNodegroup);
        dialog.run();
    	
    	
    	return null;
    	
    /*	try {

            String consoleViewId = IConsoleConstants.ID_CONSOLE_VIEW;
            IConsoleView consoleView =
                    (IConsoleView)
                            HandlerUtil.getActiveWorkbenchWindow(event)
                                    .getActivePage()
                                    .showView(consoleViewId);
            consoleView.display(RackConsole.getConsole());

            Job job =
                    new Job("Deleting all nodegroups and ontologies from RACK") {
                        @Override
                        protected IStatus run(IProgressMonitor monitor) {
                            // Set total number of work units
                            ViewUtils.showProgressView();
                            monitor.beginTask("start task", 100);
                            try {

                                deleteAllNodegroups(monitor);
                                clearRackModel();

                            } catch (Exception e) {
                                RackConsole.getConsole()
                                        .error(
                                                "Deletion of nodegroups failed, please check connection parameters for RACK and whether Rack-Box instance is running");
                                this.cancel();
                                return Status.CANCEL_STATUS;
                            }
                            return Status.OK_STATUS;
                        }
                    };
            job.schedule();
            job.addJobChangeListener(
                    new IJobChangeListener() {
                        @Override
                        public void done(IJobChangeEvent event) {

                            if (event.getResult() == Status.CANCEL_STATUS) {
                                return;
                            }

                            RackConsole.getConsole()
                                    .print("Ontologies and Nodegroups are cleared from RACK");
                            HandlerUtils.loadNodegroups();
                            HandlerUtils.showNodegroupTable();
                            HandlerUtils.showOntologyTree();
                        }

                        @Override
                        public void awake(IJobChangeEvent event) {}

                        @Override
                        public void aboutToRun(IJobChangeEvent event) {}

                        @Override
                        public void sleeping(IJobChangeEvent event) {}

                        @Override
                        public void running(IJobChangeEvent event) {}

                        @Override
                        public void scheduled(IJobChangeEvent event) {}
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; */
    }
}
