package com.vuta.helpers;

import com.vuta.controllers.JwtController;
import com.vuta.model.ResponseMessage;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vuta on 26/09/2017.
 */
@Provider
public class RequestFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private  final String AUTHORIZATION_PROPERTY = "Authorization";
    private  final ServerResponse ACCESS_DENIED = new ServerResponse(new ResponseMessage("Access denied to this path"),
            401, new Headers<Object>());
    private  final ServerResponse ACCESS_FORBIDDEN = new ServerResponse(new ResponseMessage("Nobody can access this resource"),
            403, new Headers<Object>());
    private  final ServerResponse SERVER_ERROR = new ServerResponse(new ResponseMessage("INTERNAL SERVER ERROR"),
            500, new Headers<Object>());
    private JwtController jwtController = new JwtController();


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
        Method method = methodInvoker.getMethod();

        // Get request headers
        final MultivaluedMap<String, String> headers = requestContext.getHeaders();
        // Fetch authorization header
        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

        // If annotation @PermitALL is not present pass trough filter
        if (!method.isAnnotationPresent(PermitAll.class)) {
            // Access denied for all
            if (method.isAnnotationPresent(DenyAll.class)) {
                requestContext.abortWith(ACCESS_FORBIDDEN);
                return;
            }

            // If no authorization information present; block access
            if (authorization == null || authorization.isEmpty()) {
                requestContext.abortWith(ACCESS_DENIED);
                return;
            }

            // Get encoded username and password
            final String token = authorization.get(0);

            // Verify provided token signature
            if (!jwtController.verifyToken(token)) {
                requestContext.abortWith(ACCESS_DENIED);
                return;
            }

            // Verify user access ROLE
            if (method.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

                // Is user valid?
                if (!rolesSet.contains(jwtController.getClaims().getAudience())) {
                    requestContext.abortWith(ACCESS_DENIED);
                }
            }
        } else {
            // if the access of the resource is public, we will check if the authorization header
            // and update the last activity of user
            if (authorization != null && !authorization.isEmpty())
                jwtController.verifyToken(authorization.get(0));
        }
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {

    }
}
