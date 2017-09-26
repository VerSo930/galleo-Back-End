package com.vuta.helpers;

import com.vuta.controllers.JwtController;
import org.glassfish.jersey.internal.util.Base64;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
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
public class RequestFilter implements ContainerRequestFilter {

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final ServerResponse ACCESS_DENIED = new ServerResponse("Access denied for this resource", 401, new Headers<Object>());
    private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Nobody can access this resource", 403, new Headers<Object>());
    private static final ServerResponse SERVER_ERROR = new ServerResponse("INTERNAL SERVER ERROR", 500, new Headers<Object>());
    private JwtController jwtController = new JwtController();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        System.out.println("Filter called");
        ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
        Method method = methodInvoker.getMethod();

        // get roles allowed for method
        RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
        Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

        if( ! method.isAnnotationPresent(PermitAll.class))
        {
            //Access denied for all
            if(method.isAnnotationPresent(DenyAll.class))
            {
                requestContext.abortWith(ACCESS_FORBIDDEN);
                return;
            }

            //Get request headers
            final MultivaluedMap<String, String> headers = requestContext.getHeaders();

            //Fetch authorization header
            final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

            //If no authorization information present; block access
            if(authorization == null || authorization.isEmpty())
            {
                requestContext.abortWith(ACCESS_DENIED);
                return;
            }

            //Get encoded username and password
            final String token = authorization.get(0);

            System.out.println("Authorization:" + token);


                long start = System.currentTimeMillis();
                if(!jwtController.verifyToken(token)) {
                    long stop = System.currentTimeMillis();
                    System.out.println("Time for token verify:" + (stop - start));
                    return;
                }








            //Decode username and password
            String usernameAndPassword = null;
//            try {
//                usernameAndPassword = new String(Base64.decode(encodedUserPassword));
//            } catch (IOException e) {
//                requestContext.abortWith(SERVER_ERROR);
//                return;
//            }

//            //Split username and password tokens
//            final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
//            final String username = tokenizer.nextToken();
//            final String password = tokenizer.nextToken();

            //Verifying Username and password
//            System.out.println(username);
//            System.out.println(password);

            //Verify user access
//            if(method.isAnnotationPresent(RolesAllowed.class))
//            {
//                //Is user valid?
//                if( ! isUserAllowed(username, password, rolesSet))
//                {
//                    requestContext.abortWith(ACCESS_DENIED);
//                    return;
//                }
//            }
        }
    }

    private boolean isUserAllowed(final String username, final String password, final Set<String> rolesSet)
    {
        boolean isAllowed = false;

        //Step 1. Fetch password from database and match with password in argument
        //If both match then get the defined role for user from database and continue; else return isAllowed [false]
        //Access the database and do this part yourself
        //String userRole = userMgr.getUserRole(username);

        String userRole = "ADMIN";

        //Step 2. Verify user role
        if(rolesSet.contains(userRole))
        {
            isAllowed = true;
        }
        return isAllowed;
    }
}
