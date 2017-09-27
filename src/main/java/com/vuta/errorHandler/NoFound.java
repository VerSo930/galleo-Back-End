package com.vuta.errorHandler;

import com.vuta.model.ResponseMessage;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by verso_dxr17un on 9/27/2017.
 */
//@Provider
public class NoFound implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException e) {
        return Response.ok(new ResponseMessage("Path not found!")).type(MediaType.APPLICATION_JSON_TYPE).status(404).build();
    }
}
