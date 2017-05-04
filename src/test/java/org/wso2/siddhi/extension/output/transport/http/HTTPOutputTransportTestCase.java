/*
 * Copyright (c)  2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.siddhi.extension.output.transport.http;

import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;

import java.util.ArrayList;
import java.util.List;

public class HTTPOutputTransportTestCase {
    private List<String> receivedEventNameList;static final Logger log = Logger.getLogger(HTTPOutputTransportTestCase.class);
    @Test
    public void testPublisherWithEmailTransport() throws InterruptedException {
        receivedEventNameList = new ArrayList<>(2);
        log.info("Creating test for publishing events for static topic with a partition");
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.setExtension("outputtransport:http", HttpOutputTransport.class);
        String inStreamDefinition = "Define stream FooStream (message String);" +
                "@sink(type='http',"
                + "message='{{message}}', "
                + "HOST='localhost',"
                + "PORT='8005', "
                + "TO='http://localhost:8005',"
                + "http.query='POST',"
                + "PROTOCOL='http'," +
                "@map(type='text', @payload('Text is : {{message}}'))) "+
                "Define stream BarStream (message String);";
        String query = ("@info(name = 'query1') " +
                "from FooStream select message insert into BarStream;");
        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inStreamDefinition + query);
        InputHandler fooStream = executionPlanRuntime.getInputHandler("FooStream");
        executionPlanRuntime.start();
        ServerListnerHandler lst=new ServerListnerHandler();
        Thread serverThread = new Thread(lst);
        serverThread.start();
        fooStream.send(new Object[]{"This is main body"});
        while (!lst.getServerListner().iaMessageArrive()){
            Thread.sleep(10);
        }
        String eventData=lst.getServerListner().getData();
        Assert.assertEquals("HTTP Input Transport expected input not received", "This is main body\n", eventData);
        lst.shutdown();
        executionPlanRuntime.shutdown();
    }

}

