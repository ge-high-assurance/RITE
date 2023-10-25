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
package com.ge.research.rack.utils;

import com.ge.research.rack.views.ViewUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.ui.console.FileLink;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class RackConsole extends MessageConsole {
    private static boolean setup = false;
    private static RackConsole console;
    private static MessageConsoleStream stream;
    private static MessageConsoleStream streamErr;
    private static MessageConsoleStream streamWarn;
    private static final Logger logger = LoggerFactory.getLogger(RackConsole.class);
    private static final List<String> fileExtensions =
            (List<String>) Arrays.asList("OWL", "JSON", "YAML", "CSV");

    public static RackConsole getConsole() {

        if (setup == false) {
            ConsolePlugin plugin = ConsolePlugin.getDefault();
            IConsoleManager consoleManager = plugin.getConsoleManager();
            console = new RackConsole();
            stream = console.newMessageStream();
            streamErr = console.newMessageStream();
            streamWarn = console.newMessageStream();
            consoleManager.addConsoles(new IConsole[] {console});
            console.activate();
            setup = true;
            console.addPatternMatchListener(
                    new IPatternMatchListener() {

                        @Override
                        public void connect(TextConsole console) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void disconnect() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void matchFound(PatternMatchEvent event) {

                            try {
                                String errorLine =
                                        console.getDocument()
                                                .get(event.getOffset(), event.getLength());
                                for (String extension : fileExtensions) {
                                    if (errorLine.contains(extension + ":")) {

                                        String[] split = errorLine.split(extension + ":");
                                        String filePath = split[split.length - 1].trim();
                                        File file = new File(filePath);
                                        if (file.exists()) {
                                            IWorkspace workspace = ResourcesPlugin.getWorkspace();
                                            IPath location =
                                                    Path.fromOSString(file.getAbsolutePath());
                                            IFile ifile =
                                                    workspace
                                                            .getRoot()
                                                            .getFileForLocation(location);
                                            final FileLink link =
                                                    new FileLink(ifile, null, -1, -1, -1);
                                            Display.getDefault()
                                                    .asyncExec(
                                                            () -> {
                                                                try {
                                                                    console.addHyperlink(
                                                                            link,
                                                                            event.getOffset(),
                                                                            event.getLength());
                                                                } catch (BadLocationException e) {

                                                                }
                                                            });
                                        }
                                        return;
                                    }
                                }

                            } catch (BadLocationException e) {

                            }
                        }

                        @Override
                        public String getPattern() {
                            // TODO Auto-generated method stub
                            return Pattern.compile(
                                            "(ERROR.*yaml.*)|(ERROR.*csv.*)|(ERROR.*owl.*)|(ERROR.*json.*)")
                                    .pattern();
                        }

                        @Override
                        public int getCompilerFlags() {
                            // TODO Auto-generated method stub
                            return 0;
                        }

                        @Override
                        public String getLineQualifier() {
                            // TODO Auto-generated method stub
                            return null;
                        }
                    });
        }
        return console;
    }

    private RackConsole() {
        super("Rack Console", null, true);
    }

    public void print(String message) {
        Color black = new Color(0, 0, 0, 255);
        stream.setColor(black);
        stream.print("\nINFO:  " + message);
    }

    public void printEcho(String message) {
        Color black = new Color(0, 0, 0, 255);
        stream.setColor(black);
        stream.print("\n" + message);
    }

    public void println(String message) {
        Color black = new Color(0, 0, 0, 255);
        stream.setColor(black);
        stream.print("INFO:  " + message + "\n");
    }

    public void printOK() {
        stream.print("OK");
    }

    public void printFAIL() {
        stream.print("FAIL");
    }

    public void error(String message) {
        Color red = new Color(255, 0, 0, 255);
        streamErr.setColor(red);
        streamErr.print("\nERROR: " + message);
    }

    public void errorEcho(String message) {
        Color red = new Color(255, 0, 0, 255);
        streamErr.setColor(red);
        streamErr.setColor(red);
        streamErr.print("\n" + message);
    }

    public void error(final String message, final Exception exception) {
        Color red = new Color(255, 0, 0, 255);
        stream.setColor(red);
        stream.print("\nERROR: " + message + "\n" + exception.getStackTrace() + "\n");
    }

    public void warning(String message) {
        Color yellow = new Color(255, 220, 0, 155);
        streamWarn.setColor(yellow);
        streamWarn.print("\nWARNING: " + message);
    }

    public void clearConsole() {
        super.clearConsole();
    }
}
