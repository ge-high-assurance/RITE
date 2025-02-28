/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2025, GE Aerospace and Galois, Inc.
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
package com.ge.research.rite;

import com.ge.research.rite.arp4754.viewManagers.Arp4754ViewsManager;
import com.ge.research.rite.autoGsn.viewManagers.AutoGsnViewsManager;
import com.ge.research.rite.autoGsn.viewManagers.GsnTreeViewManager;
import com.ge.research.rite.do178c.viewManagers.ReportViewsManager;
import com.ge.research.rite.utils.ErrorMessageUtil;
import com.ge.research.rite.views.RibView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * @author Saswata Paul
 *     <p>This class is needed for managing multiple javafx application window launches It was
 *     required because if Application.launch() is called more than once, then an exception is
 *     thrown : "java.lang.IllegalStateException: Application launch must not be called more than
 *     once"
 *     <p>Functionality: This class will have a class variable to store if launch() has been called
 *     already For the first call, a flag will be set For all future calls, the flag will be checked
 *     and a different approach will be taken
 *     <p>There will be a helper function for each javaFx application to make the application
 *     launches as and when required
 */
public class JavaFXAppLaunchManager {

    // TODO: Abstract all viewlaunch() functions into a single function with parameters?

    // To keep track if launch() has been already called
    private static volatile boolean launchFlag = false;

    /**
     * Used to launch different Javafx RibView applications by consulting the flag
     *
     * <p>Sets the flag to true on the first launch.
     *
     * <p>Note: This application is incomplete and the class names are not yet finalized
     */
    public static void ribViewLaunch() {
        if (!launchFlag) {
            Platform.setImplicitExit(false);
            new Thread(() -> Application.launch(RibView.class)).start();
            launchFlag = true;
        } else {
            Platform.runLater(
                    () -> {
                        try {
                            Application application = new RibView();
                            Stage primaryStage = new Stage();
                            application.start(primaryStage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    /**
     * Used to launch different Javafx AutoGsnMainView applications by consulting the flag
     *
     * <p>Sets the flag to true on the first launch.
     */
    public static void autoGsnMainViewLaunch() {
        if (!launchFlag) {
            Platform.setImplicitExit(false);
            new Thread(
                            () -> {
                                try {
                                    ErrorMessageUtil.reportInit();
                                    Application.launch(AutoGsnViewsManager.class);
                                } finally {
                                    ErrorMessageUtil.reportCleanup();
                                }
                            })
                    .start();
            launchFlag = true;
        } else {
            Platform.runLater(
                    () -> {
                        try {
                            ErrorMessageUtil.reportInit();
                            Application application = new AutoGsnViewsManager();
                            Stage primaryStage = new Stage();
                            application.start(primaryStage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            ErrorMessageUtil.reportCleanup();
                        }
                    });
        }
    }

    /**
     * Used to launch different Javafx GsnTreeView applications by consulting the flag
     *
     * <p>Sets the flag to true on the first launch.
     */
    public static void gsnTreeViewLaunch() {
        if (!launchFlag) {
            Platform.setImplicitExit(false);
            new Thread(() -> Application.launch(GsnTreeViewManager.class)).start();
            launchFlag = true;
        } else {
            Platform.runLater(
                    () -> {
                        try {
                            Application application = new GsnTreeViewManager();
                            Stage primaryStage = new Stage();
                            application.start(primaryStage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    /**
     * Used to launch different Javafx do178CReportMainView applications by consulting the flag
     *
     * <p>Sets the flag to true on the first launch.
     */
    public static void do178CReportMainViewLaunch() {
        if (!launchFlag) {
            Platform.setImplicitExit(false);
            new Thread(
                            () -> {
                                try {
                                    ErrorMessageUtil.reportInit();
                                    Application.launch(ReportViewsManager.class);
                                } finally {
                                    ErrorMessageUtil.reportCleanup();
                                }
                            })
                    .start();
            launchFlag = true;
        } else {
            Platform.runLater(
                    () -> {
                        try {
                            ErrorMessageUtil.reportInit();
                            Application application = new ReportViewsManager();
                            Stage primaryStage = new Stage();
                            application.start(primaryStage);

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            ErrorMessageUtil.reportCleanup();
                        }
                    });
        }
    }

    /**
     * Used to launch different Javafx arp4754ReportMainView applications by consulting the flag
     *
     * <p>Sets the flag to true on the first launch.
     */
    public static void arp4754ReportMainViewLaunch() {
        if (!launchFlag) {
            Platform.setImplicitExit(false);
            new Thread(
                            () -> {
                                try {
                                    ErrorMessageUtil.reportInit();
                                    Application.launch(Arp4754ViewsManager.class);
                                } finally {
                                    ErrorMessageUtil.reportCleanup();
                                }
                            })
                    .start();
            launchFlag = true;
        } else {
            Platform.runLater(
                    () -> {
                        try {
                            ErrorMessageUtil.reportInit();
                            Application application = new Arp4754ViewsManager();
                            Stage primaryStage = new Stage();
                            application.start(primaryStage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            ErrorMessageUtil.reportCleanup();
                        }
                    });
        }
    }

    /**
     * Used to launch different Javafx QueryNodeGroupSelectView applications by consulting the flag
     *
     * <p>Sets the flag to true on the first launch.
     *
     * <p>Note: This function calls the handler class because there is no manager class for this
     * single-screen app
     */
    public static void queryNodegroupSelectViewLaunch() {
        if (!launchFlag) {
            Platform.setImplicitExit(false);
            new Thread(() -> Application.launch(QueryNodegroupsSelectViewHandler.class)).start();
            launchFlag = true;
        } else {
            Platform.runLater(
                    () -> {
                        try {
                            Application application = new QueryNodegroupsSelectViewHandler();
                            Stage primaryStage = new Stage();
                            application.start(primaryStage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
