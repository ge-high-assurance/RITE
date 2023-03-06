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
import com.ge.research.rack.utils.Core;
import com.ge.research.rack.utils.RackConsole;
import com.ge.research.rack.views.RackPreferencePage;
import com.ge.research.semtk.load.utility.SparqlGraphJson;
import com.ge.research.semtk.nodeGroupStore.client.NodeGroupStoreRestClient;
import com.ge.research.semtk.sparqlX.SparqlConnection;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.resources.File;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class UploadNodegroupHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        TreePath[] paths = ((TreeSelection) selection).getPaths();
        if (paths.length == 1) {
            File file = (File) paths[0].getLastSegment();
            if (file.getFileExtension().equals("json")) {
                String nodegroupId = file.getName().replace(".json", "");
                String filepath = file.getLocation().toFile().getAbsolutePath();
                try {
                    uploadNodegroup(nodegroupId, filepath);
                } catch (Exception e) {
                    RackConsole.getConsole()
                            .error("Upload of nodegroup: " + nodegroupId + ".json " + "failed");
                }

            } else {
                RackConsole.getConsole().error("The selected nodegroup file must be a json file");
            }
        }
        return null;
    }

    public static void uploadNodegroup(String ingestId, String filepath) throws Exception {
        String dir = RackPreferencePage.getInstanceDataFolder();
        String ngPath = dir + "/nodegroups/" + ingestId + ".json";
        String jsonstr =
                FileUtils.readFileToString(new java.io.File(ngPath), Charset.defaultCharset());
        SparqlGraphJson json = new SparqlGraphJson(jsonstr);
        NodeGroupStoreRestClient ngClient = ConnectionUtil.getNGSClient();
        SparqlConnection conn = ConnectionUtil.getSparqlConnection();
        json.setSparqlConn(conn);
        try {
            RackConsole.getConsole().println("Uploading nodegroup: " + ingestId + ".json");
            ngClient.executeStoreNodeGroup(
                    ingestId,
                    Core.NODEGROUP_INGEST_COMMENT,
                    System.getProperty("user.name"),
                    json.getJson());
            RackConsole.getConsole().println("Success!");

        } catch (Exception e) {
            RackConsole.getConsole()
                    .error("Upload of nodegroup: " + ingestId + ".json " + "failed");
        }
    }
}
