package com.vuta.errorHandler;

import com.vuta.model.ResponseMessage;
import org.jboss.resteasy.core.NoMessageBodyWriterFoundFailure;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by verso_dxr17un on 9/27/2017.
 */
@Provider
public class MediaHandler implements ExceptionMapper<NotSupportedException> {

    @Override
    public Response toResponse(NotSupportedException e) {
        return Response.ok(new ResponseMessage("Unsupported Media Type")).type(MediaType.APPLICATION_JSON_TYPE).status(415).build();
    }
}
