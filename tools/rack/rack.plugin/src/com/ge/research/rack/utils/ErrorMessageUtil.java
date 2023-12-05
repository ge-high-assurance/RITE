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

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ErrorMessageUtil {
    public static String getErrorMessage(Exception e) {
        return "";
    }

    public static Shell getShell() {
        Shell shell = null;
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
            if (windows.length > 0) {
                shell = windows[0].getShell();
            }
        } else {
            shell = window.getShell();
        }
        if (shell == null) System.out.println("NO SHELL"); // Just for debugging
        return shell;
    }
    
    public static void raiseConsole() {
    	var console = RackConsole.getConsole();
    	console.activate();
    }

    /**
     * Shows an error message in the RackConsole, which must be done in the UI thread; this method
     * is intended to be called from a computational thread.
     *
     * @param msg
     */
    public static void warning(String msg) {
        getShell()
                .getDisplay()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                try {
                                    RackConsole.getConsole().warning(msg);
                                } catch (Exception ee) {
                                    // Don't think this should happen, but catching the exception so
                                    // as not to propagate it further
                                    ee.printStackTrace(System.out);
                                }
                            }
                        });
        raiseConsole();
    }

    /**
     * Shows an error message in the RackConsole, which must be done in the UI thread; this method
     * is intended to be called from a computational thread.
     *
     * @param msg
     */
    public static void error(String msg) {
        getShell()
                .getDisplay()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                try {
                                    RackConsole.getConsole().error(msg);
                                } catch (Exception ee) {
                                    // Don't think this should happen, but catching the exception so
                                    // as not to propagate it further
                                    ee.printStackTrace(System.out);
                                }
                            }
                        });
        raiseConsole();
    }

    public static void error(String msg, Exception e) {
        getShell()
                .getDisplay()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                try {
                                    RackConsole.getConsole().error(msg, e);
                                } catch (Exception ee) {
                                    // Don't think this should happen, but catching the exception so
                                    // as not to propagate it further
                                    ee.printStackTrace(System.out);
                                }
                            }
                        });
        raiseConsole();
    }

    /**
     * Calls errorEcho in the RackConsole, which must be done in the UI thread; this method is
     * intended to be called from a computational thread.
     *
     * @param msg
     */
    public static void errorEcho(String msg) {
        getShell()
                .getDisplay()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                try {
                                    RackConsole.getConsole().errorEcho(msg);
                                } catch (Exception ee) {
                                    // Don't think this should happen, but catching the exception so
                                    // as not to propagate it further
                                    ee.printStackTrace(System.out);
                                }
                            }
                        });
        raiseConsole();
    }

    /**
     * Calls printEcho in the RackConsole, which must be done in the UI thread; this method is
     * intended to be called from a computational thread.
     *
     * @param msg
     */
    public static void printEcho(String msg) {
        getShell()
                .getDisplay()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                try {
                                    RackConsole.getConsole().printEcho(msg);
                                } catch (Exception ee) {
                                    // Don't think this should happen, but catching the exception so
                                    // as not to propagate it further
                                    ee.printStackTrace(System.out);
                                }
                            }
                        });
    }

    public static void print(String msg) {
        getShell()
                .getDisplay()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                try {
                                    RackConsole.getConsole().print(msg);
                                } catch (Exception ee) {
                                    // Don't think this should happen, but catching the exception so
                                    // as not to propagate it further
                                    ee.printStackTrace(System.out);
                                }
                            }
                        });
    }

    public static void println(String msg) {
        getShell()
                .getDisplay()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                try {
                                    RackConsole.getConsole().println(msg);
                                } catch (Exception ee) {
                                    // Don't think this should happen, but catching the exception so
                                    // as not to propagate it further
                                    ee.printStackTrace(System.out);
                                }
                            }
                        });
    }

    public static void printOK() {
        getShell()
                .getDisplay()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                try {
                                    RackConsole.getConsole().printOK();
                                } catch (Exception ee) {
                                    // Don't think this should happen, but catching the exception so
                                    // as not to propagate it further
                                    ee.printStackTrace(System.out);
                                }
                            }
                        });
    }

    public static void printFAIL() {
        getShell()
                .getDisplay()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                try {
                                    RackConsole.getConsole().printFAIL();
                                } catch (Exception ee) {
                                    // Don't think this should happen, but catching the exception so
                                    // as not to propagate it further
                                    ee.printStackTrace(System.out);
                                }
                            }
                        });
    }
}
