package com.vuta;


import javax.ws.rs.ForbiddenException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles CORS requests both preflight and simple CORS requests.
 * You must bind this as a singleton and set up allowedOrigins and other settings to use.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
public class CorsFilterConfig implements ContainerResponseFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext cres) throws IOException {
        //cres.getHeaders().get();
        cres.getHeaders().add("Access-Control-Allow-Origin", "*");
        cres.getHeaders().add("access-control-expose-headers", "Authorization");
        cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, Authorization");
        cres.getHeaders().add("Access-Control-Allow-Credentials", "true");

        cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        cres.getHeaders().add("Access-Control-Max-Age", "1209600");
    }

}