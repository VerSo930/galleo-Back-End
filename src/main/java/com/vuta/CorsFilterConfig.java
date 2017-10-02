package com.vuta;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

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

        if(cres.getHeaders().get("Access-Control-Allow-Origin") == null) {
            cres.getHeaders().add("Access-Control-Allow-Origin", "*");
        }
        if(cres.getHeaders().get("access-control-expose-headers") == null) {
            cres.getHeaders().add("access-control-expose-headers", "Origin, Content-Type, Accept, Authorization");
        }
        if(cres.getHeaders().get("Access-Control-Allow-Headers") == null) {
            cres.getHeaders().add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        }
        if(cres.getHeaders().get("Access-Control-Allow-Credentials") == null) {
            cres.getHeaders().add("Access-Control-Allow-Credentials", "true");
        }
        if(cres.getHeaders().get("Access-Control-Allow-Methods") == null) {
            cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        }
        if(cres.getHeaders().get("Access-Control-Max-Age") == null) {
            cres.getHeaders().add("Access-Control-Max-Age", "1209600");
        }
        if(cres.getHeaders().get("Access-Control-Max-Age") == null) {
            cres.getHeaders().add("Access-Control-Max-Age", "1209600");
        }

    }

}