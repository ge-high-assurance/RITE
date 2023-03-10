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

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RackConsole extends MessageConsole {
    private static boolean setup = false;
    private static RackConsole console;
    private static MessageConsoleStream stream;
    private static final Logger logger = LoggerFactory.getLogger(RackConsole.class);

    public static RackConsole getConsole() {
        if (setup == false) {
            ConsolePlugin plugin = ConsolePlugin.getDefault();
            IConsoleManager conMan = plugin.getConsoleManager();
            console = new RackConsole();
            stream = console.newMessageStream();
            conMan.addConsoles(new IConsole[] {console});
            setup = true;
        }
        return console;
    }

    private RackConsole() {
        super("Rack Console", null, false);
    }

    public void print(String message) {
        // stream.print(message);
        // System.out.print(message);
        logger.info(message);
    }

    public void println(String message) {
        // stream.print("INFO: " + message + "\n");
        logger.info(message);
        // System.out.println("INFO: " + message);
    }

    public void error(String message) {
        // stream.print("ERROR: " + message + "\n");
        logger.error(message);
        // System.err.println("ERROR: " + message);
    }

    public void error(final String message, final Exception exception) {
        // stream.print("ERROR: " + message + "\n" + exception.getStackTrace() + "\n");
        // System.err.println("ERROR: " + message + "\n" + exception.getStackTrace());
        logger.error(message + "\n" + exception.getStackTrace());
    }

    public void warning(String message) {
        // stream.print("WARNING: " + message + "\n");
        logger.warn(message);
        // System.out.println("WARNING: " + message);
    }
}
