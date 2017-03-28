package org.jboss.arquillian.persistence.core.event;

import org.jboss.arquillian.persistence.script.data.descriptor.SqlScriptResourceDescriptor;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

import java.util.Collection;

public class ApplyScriptsAfterTest extends ExecuteScripts {

    public ApplyScriptsAfterTest(TestEvent testEvent, Collection<SqlScriptResourceDescriptor> dataSetDescriptors) {
        super(testEvent, dataSetDescriptors);
    }

}
