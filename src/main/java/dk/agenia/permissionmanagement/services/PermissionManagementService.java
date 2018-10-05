package dk.agenia.permissionmanagement.services;

import dk.agenia.permissionmanagement.models.ListWithTotal;
import dk.agenia.permissionmanagement.models.SecurityUser;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created: 26-04-2018
 * author: Runi
 */
/*
@Secured(requires = Permission.ADMIN)*/
@Path("management")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public abstract class PermissionManagementService<U extends SecurityUser> {

    @Context
    Application application;

    @GET
    @Path("users")
    public abstract ListWithTotal<U> getUsers(@QueryParam("query") String query,
                                              @QueryParam("page") @DefaultValue("0") int page,
                                              @QueryParam("size") @DefaultValue("-1") int size,
                                              @QueryParam("orderBy") @DefaultValue("username") String orderBy,
                                              @QueryParam("order") @DefaultValue("asc") String order) throws Exception;

    @POST
    @Path("users")
    public abstract U createUser(U user) throws Exception;

    @GET
    @Path("users/{id}")
    public abstract U getUser(@PathParam("id") String id) throws Exception;

    @PUT
    @Path("users/{id}")
    public abstract U updateUser(@PathParam("id") String id, U user) throws Exception;

    @DELETE
    @Path("users/{id}")
    public abstract void deleteUser(@PathParam("id") String id, U user) throws Exception;
}
