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
package com.ge.research.rite;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.services.IEvaluationService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class ManifestSelectedPropertyTester extends PropertyTester {
    public static final String MY_PROPERTY = "isManifestSelected";

    public static IEvaluationService getEvalService(BundleContext ctxt) {
        final ServiceReference<?> reference =
                ctxt.getServiceReference(IEvaluationService.class.getName());
        return (IEvaluationService) ctxt.getService(reference);
    }

    public void reevaluate(BundleContext ctxt) {
        // the package of the property in plugin.xml
        getEvalService(ctxt).requestEvaluation("com.ge.research.rite.isManifestSelected");
    }

    public boolean test(
            final Object receiver,
            final String property,
            final Object[] args,
            final Object expectedValue) {
        if (receiver.toString().endsWith(".yaml]")) {
            return true;
        }
        return false;
    }
}
