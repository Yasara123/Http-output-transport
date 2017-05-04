package org.wso2.siddhi.extension.output.transport.http;

import io.netty.buffer.Unpooled;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;

import org.apache.log4j.Logger;
import org.wso2.carbon.messaging.Constants;
import org.wso2.carbon.messaging.DefaultCarbonMessage;
import org.wso2.carbon.messaging.Header;
import org.wso2.carbon.messaging.Headers;
import org.wso2.carbon.messaging.MessageDataSource;
import org.wso2.carbon.messaging.exceptions.ClientConnectorException;
import org.wso2.carbon.transport.http.netty.config.ListenerConfiguration;
import org.wso2.carbon.transport.http.netty.config.SenderConfiguration;
import org.wso2.carbon.transport.http.netty.config.TransportProperty;
import org.wso2.carbon.transport.http.netty.config.TransportsConfiguration;
import org.wso2.carbon.transport.http.netty.config.YAMLTransportConfigurationBuilder;
import org.wso2.carbon.transport.http.netty.listener.HTTPServerConnector;
import org.wso2.carbon.transport.http.netty.listener.ServerConnectorController;
import org.wso2.carbon.transport.http.netty.listener.SourceHandler;
import org.wso2.carbon.transport.http.netty.message.HTTPCarbonMessage;
import org.wso2.carbon.transport.http.netty.sender.HTTPClientConnector;
import org.wso2.carbon.transport.http.netty.sender.channel.pool.ConnectionManager;

import org.wso2.carbon.transport.http.netty.sender.channel.pool.PoolConfiguration;
import org.wso2.siddhi.annotation.Extension;
import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.exception.ConnectionUnavailableException;
import org.wso2.siddhi.core.stream.output.sink.OutputTransport;
import org.wso2.siddhi.core.util.transport.DynamicOptions;
import org.wso2.siddhi.core.util.transport.Option;
import org.wso2.siddhi.core.util.transport.OptionHolder;
import org.wso2.siddhi.query.api.definition.StreamDefinition;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;
import org.wso2.carbon.messaging.CarbonMessageProcessor;
import org.wso2.carbon.messaging.ClientConnector;
import org.wso2.carbon.messaging.TransportSender;
import org.wso2.siddhi.core.stream.input.source.SourceEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import io.netty.buffer.ByteBuf;
import sun.awt.Symbol;



/**
 * Created by yasara on 5/1/17.
 */

@Extension(
        name = "http",
        namespace = "outputtransport",
        description = ""
)
public class HttpOutputTransport extends OutputTransport {
    public static final String ADAPTER_TYPE_HTTP = "http";
    public static final String ADAPTER_MESSAGE_URL = "http.url";
    public static final String ADAPTER_MESSAGE_URL_HINT = "http.url.hint";
    public static final int ADAPTER_MIN_THREAD_POOL_SIZE = 8;
    public static final int ADAPTER_MAX_THREAD_POOL_SIZE = 100;
    public static final int ADAPTER_EXECUTOR_JOB_QUEUE_SIZE = 2000;
    public static final long DEFAULT_KEEP_ALIVE_TIME_IN_MILLIS = 20000;
    public static final String ADAPTER_MIN_THREAD_POOL_SIZE_NAME = "minThread";
    public static final String ADAPTER_MAX_THREAD_POOL_SIZE_NAME = "maxThread";
    public static final String ADAPTER_KEEP_ALIVE_TIME_NAME = "keepAliveTimeInMillis";
    public static final String ADAPTER_EXECUTOR_JOB_QUEUE_SIZE_NAME = "jobQueueSize";
    public static final String ADAPTER_PROXY_HOST = "HOST";
    public static final String ADAPTER_PROXY_HOST_HINT = "http.proxy.host.hint";
    public static final String ADAPTER_PROXY_PORT = "PORT";
    public static final String ADAPTER_PROXY_PORT_HINT = "http.proxy.port.hint";
    public static final String ADAPTER_USERNAME = "http.username";
    public static final String ADAPTER_USERNAME_HINT = "http.username.hint";
    public static final String ADAPTER_PASSWORD = "http.password";
    public static final String ADAPTER_PASSWORD_HINT = "http.password.hint";
    public static final String ADAPTER_HEADERS = "http.headers";
    public static final String TO = "TO";
    public static final String QUERY = "http.query";
    public static final String PROTOCOL = "PROTOCOL";
    public static final String ADAPTER_HEADERS_HINT = "http.headers.hint";
    public static final String HEADER_SEPARATOR = ",";
    public static final String ENTRY_SEPARATOR = ":";
    public static final String ADAPTER_HTTP_CLIENT_METHOD = "http.client.method";
    public static final String CONSTANT_HTTP_POST = "HttpPost";
    public static final String CONSTANT_HTTP_PUT = "HttpPut";
    //configurations for the httpConnectionManager
    public static final String DEFAULT_MAX_CONNECTIONS_PER_HOST = "defaultMaxConnectionsPerHost";
    public static final int DEFAULT_DEFAULT_MAX_CONNECTIONS_PER_HOST = 2;
    public static final String MAX_TOTAL_CONNECTIONS = "maxTotalConnections";
    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;
    public static final String ADAPTER_MESSAGE= "message";
    private static final Logger log = Logger.getLogger(HttpOutputTransport.class);
    private Set<TransportProperty> confTrasport;
    Set<ListenerConfiguration> confListner;
    private Set<SenderConfiguration> confSender;
    private TransportsConfiguration configuration;
    private static HttpConnectionManager connectionManager;
    private String contentType;
    private HttpClient httpClient = null;
    private HostConfiguration hostConfiguration = null;
    private OptionHolder optionHolder;
    private ExecutorService executorService;
    private HTTPClientConnector clientConnector;
    String host="localhost";
    int port=9005;
    String toPath;
    String query ;
    String protocol;
    private Map<String, String> httpStaticProperties;
    Option messageTxt;
    private CarbonMessage message ;
    SourceHandler src;
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String TEXT_PLAIN = "text/plain";
    @Override public Map<String, Object> currentState() {
        return null;
    }

    @Override public void restoreState(Map<String, Object> state) {

    }

    @Override public void publish(Object payload, DynamicOptions transportOptions)
            throws ConnectionUnavailableException {
        String messageBody = messageTxt.getValue(transportOptions);

        //ByteBuffer buffer = ByteBuffer.wrap(text.getBytes());
        //MessageDataSource dataSource
       // message.setMessageDataSource();


        this.message=prepareRequest(this.message,messageBody);
        StringDataSource messageDataSource=new StringDataSource(messageBody,message.getOutputStream());
       // this.message=generateHTTPMessage("http://localhost:8005", "POST", null, messageDataSource);


        messageDataSource.setOutputStream(this.message.getOutputStream());
        this.message.setMessageDataSource(messageDataSource);
        this.message.setAlreadyRead(true);
        //this.message.setEndOfMsgAdded(true);
        message.setHeader(CONTENT_TYPE, TEXT_PLAIN);
        executorService.submit(new HTTPPublisher(this.message,messageBody, httpStaticProperties, clientConnector, payload));

    }

    @Override public String[] getSupportedDynamicOptions() {
        return new String[] { "message"};
    }

    @Override protected void init(StreamDefinition outputStreamDefinition, OptionHolder  optionHolder,
            ExecutionPlanContext executionPlanContext) {
        if (executorService == null) {
            messageTxt= optionHolder.validateAndGetOption(ADAPTER_MESSAGE);
            host=optionHolder.validateAndGetStaticValue(ADAPTER_PROXY_HOST);
            port=Integer.parseInt(optionHolder.validateAndGetStaticValue(ADAPTER_PROXY_PORT));
            toPath=optionHolder.validateAndGetStaticValue(TO);
            query=optionHolder.validateAndGetStaticValue(QUERY) ;
            protocol=optionHolder.validateAndGetStaticValue(PROTOCOL) ;
            this.optionHolder = optionHolder;
            messageTxt= optionHolder.validateAndGetOption(ADAPTER_MESSAGE);
            this. httpStaticProperties = initHTTPProperties();
            this.executorService = executionPlanContext.getExecutorService();
            message=new HTTPCarbonMessage();
            configuration = YAMLTransportConfigurationBuilder
                   .build("src/test/resources/simple-test-config/netty-transports.yml");
            port = (port==Integer.parseInt("0")) ? 9763 : port;
            host = (host.equals("")) ? "0.0.0.0" : host;

            ListenerConfiguration Config = new ListenerConfiguration("Http Listener", host, port);
            confTrasport=configuration.getTransportProperties();
            confListner=configuration.getListenerConfigurations();
            confSender=configuration.getSenderConfigurations();
            /*
            Map<String, Object> transportProperties= new HashMap<String, Object>();
            PoolConfiguration poolConfiguration = PoolConfiguration.getInstance();
            PoolConfiguration.createPoolConfiguration(transportProperties);
            poolConfiguration = PoolConfiguration.getInstance();
            ConnectionManager cm=ConnectionManager.getInstance(transportProperties);
            try {
                src=new SourceHandler(cm,Config);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }

    }
    public static CarbonMessage generateHTTPMessage(String path, String method, List<Header> headers,
           StringDataSource payload) {

        CarbonMessage carbonMessage = new DefaultCarbonMessage();

        // Set meta data
        carbonMessage.setProperty(org.wso2.carbon.messaging.Constants.PROTOCOL,
                Constant.PROTOCOL_HTTP);
        carbonMessage.setProperty(org.wso2.carbon.messaging.Constants.LISTENER_INTERFACE_ID,
                Constant.DEFAULT_INTERFACE);
        // Set url
        carbonMessage.setProperty(org.wso2.carbon.messaging.Constants.TO, path);

        // Set method
        carbonMessage.setProperty(Constant.HTTP_METHOD, method.trim().toUpperCase(Locale.getDefault()));

        // Set Headers
        if (headers != null) {
            carbonMessage.setHeaders(headers);
        }

        // Set message body
        if (payload != null) {
            payload.setOutputStream(carbonMessage.getOutputStream());
            carbonMessage.setMessageDataSource(payload);
            carbonMessage.setAlreadyRead(true);
        }

        return carbonMessage;
    }
    protected CarbonMessage  prepareRequest( CarbonMessage cMsg,String event) {
        cMsg.setHeader("MESSAGE",event);
            cMsg.setProperty("HTTP_METHOD","POST");
           // cMsg.setProperty("SRC_HANDLER",src);
        // Handle operations for empty content messages initiated from the Ballerina core itself
        if (cMsg instanceof DefaultCarbonMessage && cMsg.isEmpty() && cMsg.getMessageDataSource() == null) {
            cMsg.setEndOfMsgAdded(true);
        }
        try {
            cMsg.setProperty(HttpConstants.HOST, host);
            cMsg.setProperty(HttpConstants.PORT, port);
            if (query != null) {
                toPath = toPath + "?" + query;
            }
            cMsg.setProperty(Constants.TO, toPath);
            cMsg.setProperty(Constants.PROTOCOL, protocol);
            if (port != 80) {
                cMsg.getHeaders().set(Constants.HOST, host + ":" + port);
            } else {
                cMsg.getHeaders().set(Constants.HOST, host);
            }
            //Set User-Agent Header
            Object headerObj = cMsg.getProperty(HttpConstants.INTERMEDIATE_HEADERS);
            if (headerObj == null) {
                headerObj = new Headers();
                cMsg.setProperty(HttpConstants.INTERMEDIATE_HEADERS, headerObj);
            }
            Headers headers = (Headers) headerObj;
            if (!headers.contains(HttpConstants.USER_AGENT_HEADER)) { // If User-Agent is not already set from program
                cMsg.setHeader(HttpConstants.USER_AGENT_HEADER,"Siddhi HTTP Output Transport");
            }
        } catch (Throwable t) {
            // throw new BallerinaException("Failed to prepare request. " + t.getMessage());
        }
        return cMsg;
    }
    @Override public void connect() throws ConnectionUnavailableException {
        this.clientConnector = new HTTPClientConnector();
    }

    @Override public void disconnect() {

    }

    @Override public void destroy() {

    }
    /**
     * Initializing HTTP properties.
     * The properties in the required options list are mandatory.
     * Other HTTP options can be passed in as key value pairs, key being in the JMS spec or the broker spec.
     * @return all the options map.
     */
    private Map<String, String> initHTTPProperties() {
        List<String> requiredOptions = Arrays.asList(
                ADAPTER_PROXY_HOST, ADAPTER_PROXY_PORT,
                TO,QUERY,PROTOCOL);
        // getting the required values
        Map<String, String> transportProperties = new HashMap<>();
        requiredOptions.forEach(requiredOption ->
                transportProperties.put(requiredOption, optionHolder.validateAndGetStaticValue(requiredOption)));
        // getting optional values
        optionHolder.getStaticOptionsKeys().stream()
                .filter(option -> !requiredOptions.contains(option) && !option.equals("type")).forEach(option ->
                transportProperties.put(option, optionHolder.validateAndGetStaticValue(option)));
        return transportProperties;
    }
}
