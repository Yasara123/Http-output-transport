package org.wso2.siddhi.extension.output.transport.http;

import org.wso2.carbon.messaging.MessageDataSource;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by yasara on 5/3/17.
 */
public class StringDataSource  implements MessageDataSource {
    private String value;
    private OutputStream outputStream;

    public StringDataSource(String value) {
        this.value = value;
        this.outputStream = null;
    }

    public StringDataSource(String value, OutputStream outputStream) {
        this.value = value;
        this.outputStream = outputStream;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public void setOutputStream(OutputStream outputStream)
    {
        this.outputStream = outputStream;
    }
    @Override
    public StringDataSource clone() {
        String clonedContent = this.getMessageAsString();
        return new StringDataSource(clonedContent);
    }
    @Override public String getValueAsString(String s) {
        return null;
    }

    @Override public String getValueAsString(String s, Map<String, String> map) {
        return null;
    }

    @Override public Object getValue(String s) {
        return null;
    }

    @Override public Object getDataObject() {
        return null;
    }

    @Override public String getContentType() {
        return null;
    }

    @Override public void setContentType(String s) {


    }

    @Override public void serializeData() {
        try {
            this.outputStream.write(this.value.getBytes(Charset.defaultCharset()));
        } catch (IOException e) {
            //throw new BallerinaException("Error occurred during writing the string message to the output stream", e);
        }

    }

    @Override public String getMessageAsString() {
        return this.value;
    }
}
