package com.vuta.helpers;

import com.vuta.Constants;

import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

/**
 * Created by vuta on 03/11/2017.
 */
@Provider
public class Logger implements ContainerRequestFilter, ContainerResponseFilter {

    private FileWriter write;
    private PrintWriter printWriter;
    private Throwable t;
    private StringBuilder errorSb, debugSb, eventSb;
    private long startTime;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        //execution time
        startTime = System.currentTimeMillis();

        if (eventSb == null) {
            eventSb = new StringBuilder();
            eventSb.append("\n#------------------------------------------------------ \n ");
        }

        eventSb.append(" ** Path:").append(containerRequestContext.getUriInfo().getPath())
                    .append("\n** Date:").append(getDateTime())
                    .append("\n").append(logQueryParameters(containerRequestContext));

        if (errorSb == null) {
            errorSb = new StringBuilder();
            errorSb.append("\n#------------------------------------------------------ \n ");}

        errorSb.append("** Path:").append(containerRequestContext.getUriInfo().getPath())
                    .append("\n** Date:").append(getDateTime())
                    .append("\n").append(logQueryParameters(containerRequestContext));


        if (debugSb == null) {
            debugSb = new StringBuilder();
            debugSb.append("\n#------------------------------------------------------ \n ");
        }
            debugSb.append(" ** Path:").append(containerRequestContext.getUriInfo().getPath())
                    .append("\n** Date:").append(getDateTime())
                    .append("\n").append(logQueryParameters(containerRequestContext));

        logQueryParameters(containerRequestContext);
        logRequestHeader(containerRequestContext);

    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        if (startTime == 0) {
            return;
        }

        debugSb.append("\n ** REQ time:").append((System.currentTimeMillis()-startTime));
        errorSb.append("\n ** REQ time:").append((System.currentTimeMillis()-startTime));
        eventSb.append("\n ** REQ time:").append((System.currentTimeMillis()-startTime));

        saveLogs();

    }

    private String logQueryParameters(ContainerRequestContext requestContext) {
        Iterator iterator = requestContext.getUriInfo().getPathParameters().keySet().iterator();
        while (iterator.hasNext()) {
            String name = (String) iterator.next();
            List obj = requestContext.getUriInfo().getPathParameters().get(name);
            String value = null;
            if (null != obj && obj.size() > 0) {
                value = (String) obj.get(0);
            }
            return " ** Parameter: "  +name + "=" + value;
        }
        return " ** No Parameters";
    }

    private void logRequestHeader(ContainerRequestContext requestContext) {
        StringBuilder s = new StringBuilder();
        Iterator iterator;

        s.append("\n** Method Type:").append(requestContext.getMethod());
        iterator = requestContext.getHeaders().keySet().iterator();
        while (iterator.hasNext()) {
            String headerName = (String) iterator.next();
            String headerValue = requestContext.getHeaderString(headerName);

            s.append("\n** Header:").append(headerName).append("=").append(headerValue);
        }
    }


    public void logDebug(String message) {
        if (debugSb == null) {
            debugSb = new StringBuilder();
            debugSb.append("\n#------------------------------------------------------ \n ");
        }
       debugSb.append(" ** ").append(message);
    }

    public void logError(String message) {
        if (errorSb == null) {
            errorSb = new StringBuilder();
            errorSb.append("\n#------------------------------------------------------ \n ");}
       errorSb.append(" ** ").append(message);
    }

    public void logEvent(String event) {
        if (eventSb == null) {
            eventSb = new StringBuilder();
            eventSb.append("\n#------------------------------------------------------ \n ");
        }
        eventSb.append(" ** ").append(event);

    }


    private String getDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public PrintStream printStream() {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(Constants.LOGS_PATH + "log-exception.txt"), true);
            return new PrintStream(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveLogs() {

        try {
            // Save events
            eventSb.append("\n#------------------------------------------------------ \n ");
            write = new FileWriter(Constants.LOGS_PATH + "log-event.txt", true);
            printWriter = new PrintWriter(write);
            printWriter.printf("%s" + "%n", eventSb.toString());
            printWriter.close();

            // Save errors
            errorSb.append("\n#------------------------------------------------------ \n ");
            write = new FileWriter(Constants.LOGS_PATH + "log-error.txt", true);
            printWriter = new PrintWriter(write);
            printWriter.printf("%s" + "%n", errorSb.toString());
            printWriter.close();

            // Save debug
            debugSb.append("\n#------------------------------------------------------ \n ");
            write = new FileWriter(Constants.LOGS_PATH + "log-debug.txt", true);
            printWriter = new PrintWriter(write);
            printWriter.printf("%s" + "%n", debugSb.toString());
            printWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
