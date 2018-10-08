package dk.agenia.permissionmanagement.services;

import dk.agenia.permissionmanagement.models.LoginData;
import dk.agenia.permissionmanagement.models.SecurityUser;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * <p>Created: 05-10-2018</p>
 * <p>author: Runi</p>
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("authentication")
public interface AuthenticationService<U extends SecurityUser> {

    @POST
    @Path("login")
    U login(@Context HttpServletResponse response, LoginData data) throws Exception;
}
