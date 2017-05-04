package org.wso2.siddhi.extension.output.transport.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by yasara on 5/3/17.
 */
class ServerListner implements HttpHandler{
    public ServerListner(boolean isEventArraved) {
    }

    private AtomicBoolean isEventArraved = new AtomicBoolean(false);
    StringBuilder strBld;
    @Override public void handle(HttpExchange t) throws IOException {
        // Get the paramString form the request
        String line = "";
        InputStream is = t.getRequestBody();
        BufferedReader in = new BufferedReader(new InputStreamReader(is)); // initiating
        strBld = new StringBuilder();
        while ((line = in.readLine()) != null) {
            strBld = strBld.append(line + "\n");
            System.out.print(line + "\n");
        }
        isEventArraved.set(true);
    }
   public  String getData(){
        return strBld.toString();
   }
    public boolean iaMessageArrive() {
        return isEventArraved.get();
    }

}
public class ServerListnerHandler implements Runnable {
    public ServerListner getServerListner() {
        return sl;
    }

    private ServerListner sl;
    private HttpServer server;

    public ServerListnerHandler() {
        sl = new ServerListner(false);
    }

    @Override public void run() {
        try {
            server = HttpServer.create(new InetSocketAddress(8005), 5);
            server.createContext("/", sl);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void shutdown() {
        if (server != null) {
            server.stop(1);
        }
    }


}
