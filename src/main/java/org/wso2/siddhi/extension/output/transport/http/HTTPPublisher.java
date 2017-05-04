package org.wso2.siddhi.extension.output.transport.http;

import org.apache.log4j.Logger;
import org.wso2.carbon.messaging.CarbonMessage;
import org.wso2.carbon.messaging.exceptions.ClientConnectorException;
import org.wso2.carbon.transport.http.netty.message.HTTPCarbonMessage;
import org.wso2.carbon.transport.http.netty.sender.HTTPClientConnector;
import org.wso2.carbon.transport.http.netty.sender.HTTPSender;

import java.util.Map;


/**
 * Created by yasara on 5/2/17.
 */
public class HTTPPublisher  implements Runnable {
    private Map<String, String> httpProperties;
    private HTTPClientConnector httpClientConnector;
    private CarbonMessage message ;
    private static final Logger log = Logger.getLogger(HTTPPublisher.class);

    public HTTPPublisher(CarbonMessage message,String textBody, Map<String, String> staticHTTPProperties,
            HTTPClientConnector httpClientConnector, Object payload) {
        this.message=message;
        HTTPCarbonMessage ht=new HTTPCarbonMessage();

        this.httpProperties = staticHTTPProperties;
        this.httpProperties.putAll(staticHTTPProperties);
       // this.httpProperties.put(HttpConstants.DESTINATION_PARAM_NAME, destination);
        this.httpClientConnector = httpClientConnector;
        //message = new HTTPCarbonMessage();
    }

    @Override public void run() {
        try {

            httpClientConnector.send(message, null, httpProperties);
        } catch (ClientConnectorException e) {
            log.error("Error sending the HTTP message: ", e);
        }
    }
}
