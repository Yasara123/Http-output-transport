package org.wso2.siddhi.extension.output.transport.http;

/**
 * Created by yasara on 5/3/17.
 */
public class Constant {

    public static final String BASE_PATH = "BASE_PATH";
    public static final String SUB_PATH = "SUB_PATH";
    public static final String QUERY_STR = "QUERY_STR";
    public static final String RAW_QUERY_STR = "RAW_QUERY_STR";

    public static final String DEFAULT_INTERFACE = "default";
    public static final String DEFAULT_BASE_PATH = "/";
    public static final String DEFAULT_SUB_PATH = "/*";

    public static final String PROTOCOL_HTTP = "http";
    public static final String PROTOCOL_HTTPS = "https";
    public static final String HTTP_METHOD = "HTTP_METHOD";
    public static final String HTTP_STATUS_CODE = "HTTP_STATUS_CODE";
    public static final String HTTP_REASON_PHRASE = "HTTP_REASON_PHRASE";
    public static final String HTTP_CONTENT_LENGTH = "Content-Length";
    public static final String USER_AGENT_HEADER = "User-Agent";
    public static final String PROTOCOL = "PROTOCOL";
    public static final String HOST = "HOST";
    public static final String PORT = "PORT";
    public static final String TO = "TO";


    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_PUT = "PUT";
    public static final String HTTP_METHOD_PATCH = "PATCH";
    public static final String HTTP_METHOD_DELETE = "DELETE";
    public static final String HTTP_METHOD_OPTIONS = "OPTIONS";
    public static final String HTTP_METHOD_HEAD = "HEAD";

    /* Annotations */
    public static final String ANNOTATION_NAME_SOURCE = "Source";
    public static final String ANNOTATION_NAME_BASE_PATH = "BasePath";
    public static final String ANNOTATION_NAME_PATH = "Path";
    public static final String ANNOTATION_METHOD_GET = HTTP_METHOD_GET;
    public static final String ANNOTATION_METHOD_POST = HTTP_METHOD_POST;
    public static final String ANNOTATION_METHOD_PUT = HTTP_METHOD_PUT;
    public static final String ANNOTATION_METHOD_PATCH = HTTP_METHOD_PATCH;
    public static final String ANNOTATION_METHOD_DELETE = HTTP_METHOD_DELETE;
    public static final String ANNOTATION_METHOD_OPTIONS = HTTP_METHOD_OPTIONS;

    /* WebSocket Annotations */
    public static final String PROTOCOL_WEBSOCKET = "ws";
    public static final String ANNOTATION_NAME_WEBSOCKET_UPGRADE_PATH = "WebSocketUpgradePath";
    public static final String ANNOTATION_NAME_ON_OPEN = "OnOpen";
    public static final String ANNOTATION_NAME_ON_TEXT_MESSAGE = "OnTextMessage";
    public static final String ANNOTATION_NAME_ON_BINARY_MESSAGE = "OnBinaryMessage";
    public static final String ANNOTATION_NAME_ON_PONG_MESSAGE = "OnPongMessage";
    public static final String ANNOTATION_NAME_ON_CLOSE = "OnClose";
    public static final String ANNOTATION_NAME_ON_ERROR = "OnError";

    public static final String CONNECTION = "Connection";
    public static final String UPGRADE = "Upgrade";
    public static final String WEBSOCKET_UPGRADE = "websocket";
    public static final String CHANNEL_ID = "CHANNEL_ID";
    public static final String WEBSOCKET_SESSION = "WEBSOCKET_SESSION";

    public static final String ANNOTATION_SOURCE_KEY_INTERFACE = "interface";
}
