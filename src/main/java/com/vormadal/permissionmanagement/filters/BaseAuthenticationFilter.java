package com.vormadal.permissionmanagement.filters;

import com.vormadal.permissionmanagement.models.ApplicationSecurityContext;
import com.vormadal.permissionmanagement.exceptions.MissingRoleError;
import com.vormadal.permissionmanagement.exceptions.MissingRolesException;
import com.vormadal.permissionmanagement.exceptions.RestError;
import com.vormadal.permissionmanagement.models.SecurityUser;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Set;

/**
 * Created: 14-04-2018
 * Owner: Runi
 */
@Slf4j
public abstract class BaseAuthenticationFilter<U extends SecurityUser> implements ContainerRequestFilter {

    @Context
    ResourceInfo resourceInfo;
    @Context
    HttpHeaders headers;


    public void filter(ContainerRequestContext requestContext) throws IOException {
        //options requests are not filtered
        if ("options".equals(requestContext.getMethod().toLowerCase())) return;


        //Get the authentification passed in HTTP headers parameters
        String auth = requestContext.getHeaderString("authorization");
        U user;

        try {
            user = resolveUser(auth);
            requestContext.setSecurityContext(new ApplicationSecurityContext(user, requestContext.getUriInfo().getRequestUri().getScheme()));
        } catch (WebApplicationException e) {
            error(requestContext, e);
            return;
        }

        try {
            processDenyAll(resourceInfo);
            if (processPermitAll(resourceInfo)) return;
            processRolesAllowed(requestContext, resourceInfo, user);
        }
        catch (MissingRolesException e){
            error(requestContext, e);
        }
        catch (WebApplicationException e){
            error(requestContext, e);
        }

    }

    private void processRolesAllowed(ContainerRequestContext requestContext, ResourceInfo resourceInfo, U user) throws MissingRolesException {
        RolesAllowed annotation = resourceInfo.getResourceMethod().getAnnotation(RolesAllowed.class);
        if (annotation == null) {
            annotation = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
        }
        if (annotation == null) return;

        String[] roles = annotation.value();
        MultivaluedMap<String, String> parameters = requestContext.getUriInfo().getPathParameters();
        Set<String> keys = parameters.keySet();
        for(String key : keys){
            String value = parameters.getFirst(key);
            for(int i = 0; i < roles.length; i++){
                roles[i] = roles[i].replace("{" + key + "}", value);
            }
        }

        if (user == null) throw new WebApplicationException("Not authorized", Response.Status.UNAUTHORIZED);
        boolean allow = false;
        for (String role : roles) {
            if (user.getRoles().contains(role)) {
                allow = true;
                break;
            }
        }

        if (!allow)
            throw new MissingRolesException("Missing one of roles", roles, Response.Status.FORBIDDEN);
    }

    private boolean processPermitAll(ResourceInfo resourceInfo) {
        PermitAll permitAllMethod = resourceInfo.getResourceMethod().getAnnotation(PermitAll.class);
        PermitAll permitAllClass = resourceInfo.getResourceClass().getAnnotation(PermitAll.class);
        if (permitAllMethod != null || permitAllClass != null) {
            return true;
        }
        return false;
    }

    private void processDenyAll(ResourceInfo resourceInfo) {
        DenyAll denyAllMethod = resourceInfo.getResourceMethod().getAnnotation(DenyAll.class);
        DenyAll denyAllClass = resourceInfo.getResourceClass().getAnnotation(DenyAll.class);
        if (denyAllClass != null || denyAllMethod != null) {
            throw new WebApplicationException("", Response.Status.UNAUTHORIZED);
        }

    }

    public abstract U resolveUser(String token);

    private void error(ContainerRequestContext requestContext, WebApplicationException e) {
        requestContext
                .abortWith(Response
                        .status(e.getResponse().getStatus())
                        .entity(new RestError(e.getMessage(), e.getResponse().getStatus()))
                        .build());
    }

    private void error(ContainerRequestContext requestContext, MissingRolesException e) {
        requestContext
                .abortWith(Response
                        .status(e.getResponse().getStatus())
                        .entity(new MissingRoleError(e.getMessage(),
                                e.getResponse().getStatus(),
                                e.getMissingRoles()))
                        .build());
    }
}
