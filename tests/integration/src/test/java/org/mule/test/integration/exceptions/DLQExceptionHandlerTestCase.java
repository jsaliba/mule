/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.exceptions;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.message.ExceptionMessage;
import org.mule.tck.junit4.FunctionalTestCase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DLQExceptionHandlerTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/integration/exceptions/exception-dlq.xml";
    }

    @Test
    public void testDLQ() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        client.dispatch("jms://request.queue", "testing 1 2 3", null);

        MuleMessage message = client.request("jms://out.queue", 3000);
        assertNull(message);

        try
        {
            message = client.request("jms://DLQ", 20000);
        }
        catch (MuleException e)
        {
            e.printStackTrace(System.err);
        }
        assertNotNull(message);

        ExceptionMessage em = (ExceptionMessage)message.getPayload();
        assertEquals("testing 1 2 3", em.getPayload());
    }
}
