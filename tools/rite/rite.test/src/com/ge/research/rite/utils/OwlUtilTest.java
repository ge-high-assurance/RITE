/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2024, GE Aerospace and Galois, Inc.
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
<<<<<<< HEAD
package com.ge.research.rack.utils;

import com.ge.research.rack.views.RackPreferencePage;
=======
package com.ge.research.rite.utils;

import com.ge.research.rite.views.RackPreferencePage;
>>>>>>> 58d31630c3eca4cd02adf1b185a3c9fe3b893eb7
import java.util.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test verifies that OwlUtil.getOwlClasses is able to read an OWL file from the file system.
 *
 * <p>The tools/rack/pom.xml configures this test's bundle in two places, the
 * target-platform-configuration to enable slf4j logging and the tycho-surefire-plugin to configure
 * slf4j logging and enable the UI harness. If you want to examine the test bundle's log, open the
 * file tools/rack/rite.test/target/rite.test.log. If you want to run this test from Maven and debug
 * it in the Eclipse debugger, set debugPort in the tycho-surefire-plugin's configuration (see
 * https://tycho.eclipseprojects.io/doc/latest/tycho-surefire-plugin/test-mojo.html#debugPort).
 *
 * <p>There does not seem to be much documentation how to write test bundles, but see "Test bundles"
 * in https://wiki.eclipse.org/Tycho/Reference_Card#Test_bundles, "Section 2" in
 * https://www.vogella.com/tutorials/EclipseTycho/article.html#executing-plug-in-unit-tests-with-tycho,
 * and "Default behaviour" in
 * https://wiki.eclipse.org/Tycho/Testing_with_Surefire#Default_behaviour:_Create_OSGi_runtime_from_target_platform.
 *
 * <p>My takeaway is that you should look at the classes in the rite.plugin sources, pick a class
 * with an important method which you want to test automatically, examine its API, and try to find a
 * way to call that method from your test code. Keep in mind that this test will have a temporary
 * workspace under tools/rack/rack.test/target/work but the temporary workspace will be empty unless
 * you find a way to set up test projects with Maven's test resources and/or Eclipse's API. You will
 * have to know how to copy a project into the temporary workspace or open an existing project in
 * your test code (as I did in the test code below) if your test code needs to use a test project.
 */
public class OwlUtilTest {

    private static String overlay = "../../../models/GE-Drone-Ontology";
    private static String pattern = "../../../models/GE-GSN-Pattern";
    private static String owlFile = overlay + "/OwlModels/GE-Drone-Overlay.owl";

    // Not needed for this particular test to work, but may be needed for other tests.
    // If you must run this before every test, change to @Before public void setup.
    @BeforeClass
    public static void setupOnce() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        new RackPreferencePage().init(workbench);
        RackPreferencePage.setGsnProjectOverlaySadl(overlay);
        RackPreferencePage.setGsnProjectPatternSadl(pattern);
        Assert.assertTrue(RackPreferencePage.areGSNPreferencesComplete());
    }

    @Test
    public void testGetOwlClasses() {
        List<String> classes = OwlUtil.getOwlClasses(owlFile);
        Assert.assertNotNull(classes);
        Assert.assertTrue(classes.size() > 0);
    }
}
