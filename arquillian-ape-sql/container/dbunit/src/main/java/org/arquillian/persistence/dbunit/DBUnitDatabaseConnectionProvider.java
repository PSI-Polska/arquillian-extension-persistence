/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.arquillian.persistence.dbunit;

import org.dbunit.database.DatabaseConnection;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;

/**
 * Injects DBUnit database connection through {@link ArquillianResource} annotation to the test class instance.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class DBUnitDatabaseConnectionProvider implements ResourceProvider {

    @Inject
    private Instance<DataSource> dataSourceInstance;

    @Inject
    private Instance<DBUnitConfiguration> dbUnitConfigurationInstance;

    @Inject
    private Instance<DatabaseConnection> databaseConnectionInstance;

    /* (non-Javadoc)
     * @see org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider#canProvide(java.lang.Class)
     */
    @Override
    public boolean canProvide(Class<?> type) {
        return DatabaseConnection.class.isAssignableFrom(type);
    }

    /* (non-Javadoc)
     * @see org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider#lookup(org.jboss.arquillian.test.api.ArquillianResource, java.lang.annotation.Annotation[])
     */
    @Override
    public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
        return databaseConnectionInstance.get();
    }

}
