/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
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
package org.jboss.arquillian.persistence.dbunit.lifecycle;

import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.core.event.AfterPersistenceTest;
import org.jboss.arquillian.persistence.core.event.BeforePersistenceTest;
import org.jboss.arquillian.persistence.core.metadata.MetadataExtractor;
import org.jboss.arquillian.persistence.core.metadata.PersistenceExtensionFeatureResolver;
import org.jboss.arquillian.persistence.dbunit.api.CustomColumnFilter;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.dbunit.data.provider.DataSetProvider;
import org.jboss.arquillian.persistence.dbunit.data.provider.ExpectedDataSetProvider;
import org.jboss.arquillian.persistence.dbunit.event.CompareDBUnitData;
import org.jboss.arquillian.persistence.dbunit.event.PrepareDBUnitData;

import java.lang.reflect.Method;

public class DataSetHandler {

    @Inject
    private Instance<MetadataExtractor> metadataExtractorInstance;

    @Inject
    private Instance<DBUnitConfiguration> configurationInstance;

    @Inject
    private Instance<PersistenceExtensionFeatureResolver> persistenceExtensionFeatureResolverInstance;

    @Inject
    private Event<PrepareDBUnitData> prepareDataEvent;

    @Inject
    private Event<CompareDBUnitData> compareDataEvent;

    public void prepareDatabase(@Observes(precedence = 20) BeforePersistenceTest beforePersistenceTest) {
        PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = persistenceExtensionFeatureResolverInstance.get();

        if (persistenceExtensionFeatureResolver.shouldSeedData()) {
            DataSetProvider dataSetProvider = new DataSetProvider(metadataExtractorInstance.get(), configurationInstance.get());
            prepareDataEvent.fire(new PrepareDBUnitData(dataSetProvider.getDescriptorsDefinedFor(beforePersistenceTest.getTestMethod())));
        }

    }

    public void verifyDatabase(@Observes(precedence = 30) AfterPersistenceTest afterPersistenceTest) {

        PersistenceExtensionFeatureResolver persistenceExtensionFeatureResolver = persistenceExtensionFeatureResolverInstance.get();

        if (persistenceExtensionFeatureResolver.shouldVerifyDataAfterTest()) {
            final MetadataExtractor metadataExtractor = metadataExtractorInstance.get();
            final ExpectedDataSetProvider dataSetProvider = new ExpectedDataSetProvider(metadataExtractor, configurationInstance.get());
            final Method testMethod = afterPersistenceTest.getTestMethod();
            final ShouldMatchDataSet dataSetsToVerify = metadataExtractor.shouldMatchDataSet()
                    .fetchFrom(testMethod);
            final CustomColumnFilter customColumnFilter = metadataExtractor.using(CustomColumnFilter.class).fetchFrom(testMethod);
            final CompareDBUnitData compareDBUnitDataEvent = new CompareDBUnitData(dataSetProvider.getDescriptorsDefinedFor(testMethod), dataSetsToVerify.orderBy(), dataSetsToVerify.excludeColumns());
            if (customColumnFilter != null) {
                compareDBUnitDataEvent.add(customColumnFilter.value());
            }
            compareDataEvent.fire(compareDBUnitDataEvent);
        }

    }

}
