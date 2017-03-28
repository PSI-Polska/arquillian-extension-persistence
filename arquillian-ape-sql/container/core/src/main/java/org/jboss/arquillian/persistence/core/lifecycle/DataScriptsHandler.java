/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.persistence.core.lifecycle;

import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.core.event.AfterPersistenceTest;
import org.jboss.arquillian.persistence.core.event.BeforePersistenceTest;
import org.jboss.arquillian.persistence.core.event.ExecuteScripts;
import org.jboss.arquillian.persistence.core.metadata.PersistenceExtensionFeatureResolver;
import org.jboss.arquillian.persistence.script.configuration.ScriptingConfiguration;
import org.jboss.arquillian.persistence.script.data.provider.SqlScriptProvider;

public class DataScriptsHandler {

    @Inject
    private Instance<ScriptingConfiguration> configuration;

    @Inject
    private Instance<PersistenceExtensionFeatureResolver> persistenceExtensionFeatureResolver;

    @Inject
    private Event<ExecuteScripts> executeScriptsEvent;

    public void executeBeforeTest(@Observes(precedence = 30) BeforePersistenceTest beforePersistenceTest) {
        executeCustomScriptsBefore(beforePersistenceTest);
    }

    public void executeAfterTest(@Observes(precedence = 40) AfterPersistenceTest afterPersistenceTest) {
        executeCustomScriptsAfter(afterPersistenceTest);
    }

    // Private methods

    private void executeCustomScriptsBefore(BeforePersistenceTest beforePersistenceTest) {
        if (!persistenceExtensionFeatureResolver.get().shouldCustomScriptBeAppliedBeforeTestRequested()) {
            return;
        }

        SqlScriptProvider<ApplyScriptBefore> scriptsProvider = SqlScriptProvider.createProviderForScriptsToBeAppliedBeforeTest(beforePersistenceTest.getTestClass(), configuration.get());

        executeScriptsEvent.fire(new ExecuteScripts(beforePersistenceTest, scriptsProvider.getDescriptorsDefinedFor(beforePersistenceTest.getTestMethod())));
    }

    private void executeCustomScriptsAfter(AfterPersistenceTest afterPersistenceTest) {
        if (!persistenceExtensionFeatureResolver.get().shouldCustomScriptBeAppliedAfterTestRequested()) {
            return;
        }

        SqlScriptProvider<ApplyScriptAfter> scriptsProvider = SqlScriptProvider.createProviderForScriptsToBeAppliedAfterTest(afterPersistenceTest.getTestClass(), configuration.get());

        executeScriptsEvent.fire(new ExecuteScripts(afterPersistenceTest, scriptsProvider.getDescriptorsDefinedFor(afterPersistenceTest.getTestMethod())));
    }
}
