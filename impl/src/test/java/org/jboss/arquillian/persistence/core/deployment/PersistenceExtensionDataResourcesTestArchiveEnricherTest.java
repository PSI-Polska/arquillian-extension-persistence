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
package org.jboss.arquillian.persistence.core.deployment;


import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.dbunit.configuration.DBUnitConfiguration;
import org.jboss.arquillian.persistence.script.configuration.ScriptingConfiguration;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.NodeImpl;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;

public class PersistenceExtensionDataResourcesTestArchiveEnricherTest
{

   private PersistenceExtensionDataResourcesTestArchiveEnricher enricher = new PersistenceExtensionDataResourcesTestArchiveEnricher();

   @Before
   public void initializeEnricher()
   {
      enricher.dbunitConfigurationInstance = new Instance<DBUnitConfiguration>() {
         @Override
         public DBUnitConfiguration get()
         {
            return new DBUnitConfiguration();
         }
      };
      enricher.scriptingConfigurationInstance = new Instance<ScriptingConfiguration>()
      {
         @Override
         public ScriptingConfiguration get()
         {
            return new ScriptingConfiguration();
         }
      };
   }

   @Test
   public void should_bundle_resources_directly_in_java_archive() throws Exception
   {
      // given
      final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar");
      final String scriptPath = "/scripts/two-inserts.sql";

      // when
      enricher.process(archive, new TestClass(ScriptOnMethodLevel.class));

      // then
      assertThatContainsOnly(archive, scriptPath);
   }

   @Test
   public void should_bundle_resources_directly_in_web_archive() throws Exception
   {
      // given
      final WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war");
      final String scriptPath = "/WEB-INF/classes/scripts/two-inserts.sql";

      // when
      enricher.process(archive, new TestClass(ScriptOnMethodLevel.class));

      // then
      assertThatContainsOnly(archive, scriptPath);
   }

   @Test
   public void should_bundle_resources_as_library_jar_in_enterprise_archive() throws Exception
   {
      // given
      final EnterpriseArchive archive = ShrinkWrap.create(EnterpriseArchive.class, "test.ear");
      final String scriptPath = "/lib/arquillian-persistence-datasets.jar";

      // when
      enricher.process(archive, new TestClass(DatasetOnMethodLevel.class));

      // then
      final Node datasetArchive = archive.getContent(Filters.include(scriptPath)).values().iterator().next();
      final Archive<?> library = ((ArchiveAsset) datasetArchive.getAsset()).getArchive();

      assertThatContainsOnly(archive, scriptPath);
      assertThatContainsOnly(library, "/datasets/users.json");
   }

   //

   private static void assertThatContainsOnly(Archive<?> archive, String path)
   {
      final Map<ArchivePath,Node> content = archive.getContent(Filters.include(path));
      assertThat(content).hasSize(1)
                         .includes(entry(new BasicPath(path), new NodeImpl(ArchivePaths.create(path))));
   }

   private static class ScriptOnMethodLevel
   {

      @ApplyScriptAfter("two-inserts.sql")
      public void should_work() throws Exception
      {
         // given
         // when
         // then
      }

   }

   private static class DatasetOnMethodLevel
   {

      @ShouldMatchDataSet("users.json")
      public void should_work() throws Exception
      {
         // given
         // when
         // then
      }

   }
}