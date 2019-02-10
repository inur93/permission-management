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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 *
 * <p>Created: 14-04-2018</p>
 * <p>Author: Runi</p>
 *
 * <p>By implementing and registering this filter all requests except those with Method: OPTIONS will be processed.</p>
 * <p>The filter looks for the 'authorization' header and calls the 'resolveUser' method with the header as argument.</p>
 * <p>It is up to the user how the user is resolved.</p>
 * <p>However if the user is not resolved or the provided user does include its actual roles, the user might be denied access.</p>
 *
 * <p>This filter determines if a user has access by looking for the following annotations:</p>
 *
 * <li>
 * <ul>{@link DenyAll}</ul>
 * <ul>{@link PermitAll}</ul>
 * <ul>{@link RolesAllowed}</ul>
 * </li>
 * <p>If the endpoint is not annotated either on the class or method it will correspond using {@link PermitAll}</p>
 *
 */
@Slf4j
public abstract class BaseAuthenticationFilter<U extends SecurityUser> implements ContainerRequestFilter {

    @Context
    ResourceInfo resourceInfo;
    @Context
    HttpHeaders headers;


    /**
     * <p>Processes the following annotation on the requested method and/or class:</p>
     * <li>
     * <ul>{@link DenyAll}</ul>
     * <ul>{@link PermitAll}</ul>
     * <ul>{@link RolesAllowed}</ul>
     * </li>
     *
     * @param requestContext
     * @throws IOException
     */
    public void filter(ContainerRequestContext requestContext) throws IOException {
        //options requests are not filtered
        if ("options".equals(requestContext.getMethod().toLowerCase())) return;
        //Get the authentification passed in HTTP headers parameters
        //String auth = requestContext.getHeaderString("authorization");
        U user;
        try {
            user = resolveUser(requestContext);
            requestContext.setSecurityContext(new ApplicationSecurityContext(user, requestContext.getUriInfo().getRequestUri().getScheme()));
        } catch (WebApplicationException e) {
            error(requestContext, e);
            return;
        }

        try {
            processDenyAll(resourceInfo);
            if (processPermitAll(resourceInfo)) return;
            if (processRolesAllowed(requestContext, resourceInfo, user)) return;
        } catch (MissingRolesException e) {
            error(requestContext, e);
        } catch (WebApplicationException e) {
            error(requestContext, e);
        }

        error(requestContext, new MissingRolesException("no one is allowed here.", null, Response.Status.FORBIDDEN));
    }

    private boolean processRolesAllowed(ContainerRequestContext requestContext, ResourceInfo resourceInfo, U user) throws MissingRolesException {
        List<String> roles = new ArrayList<>();
        RolesAllowed methodAnnotation = resourceInfo.getResourceMethod().getAnnotation(RolesAllowed.class);
        RolesAllowed classAnnotation = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
        if (methodAnnotation != null) roles.addAll(asList(methodAnnotation.value()));
        if (classAnnotation != null) roles.addAll(asList(classAnnotation.value()));

        if(roles.size() == 0) return true; //no role required - allow all
        MultivaluedMap<String, String> parameters = requestContext.getUriInfo().getPathParameters();
        Set<String> keys = parameters.keySet();
        for (String key : keys) {
            String value = parameters.getFirst(key);
            for (int i = 0; i < roles.size(); i++) {
                roles.set(i, roles.get(i).replace("{" + key + "}", value));
            }
        }

        if (user == null) throw new WebApplicationException("Not authorized", Response.Status.UNAUTHORIZED);

        for (String role : roles) {
            if (user.getRoles().contains(role)) {
                return true;
            }
        }
        throw new MissingRolesException("Missing one of roles", roles.toArray(new String[0]), Response.Status.FORBIDDEN);
    }

    private boolean processPermitAll(ResourceInfo resourceInfo) {
        PermitAll permitAllMethod = resourceInfo.getResourceMethod().getAnnotation(PermitAll.class);
        PermitAll permitAllClass = resourceInfo.getResourceClass().getAnnotation(PermitAll.class);
        return permitAllMethod != null || permitAllClass != null;
    }

    private void processDenyAll(ResourceInfo resourceInfo) {
        DenyAll denyAllMethod = resourceInfo.getResourceMethod().getAnnotation(DenyAll.class);
        DenyAll denyAllClass = resourceInfo.getResourceClass().getAnnotation(DenyAll.class);
        if (denyAllClass != null || denyAllMethod != null) {
            throw new WebApplicationException("", Response.Status.UNAUTHORIZED);
        }

    }

    /**
     * <p>This method should resolve to the current user.</p>
     * <p>The user could be retrieved from the authorization header (recommended):</p>
     * <code>requestContext.getHeader("authorization")</code>
     *
     * @param requestContext use this to get the necessary information and optionally also set additional contextual elements.
     * @return the current user
     */
    public abstract U resolveUser(ContainerRequestContext requestContext);

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
