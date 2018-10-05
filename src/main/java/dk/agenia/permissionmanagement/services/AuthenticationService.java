package dk.agenia.permissionmanagement.services;

import dk.agenia.permissionmanagement.models.LoginData;
import dk.agenia.permissionmanagement.models.SecurityUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * <p>Created: 05-10-2018</p>
 * <p>author: Runi</p>
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("authentication")
public abstract class AuthenticationService<U extends SecurityUser> {

    @POST
    @Path("login")
    public U login(@Context HttpServletResponse response, LoginData data) throws Exception {
        U user = authenticateUser(data);
        String jwt = Jwts.builder()
                .setSubject("user")
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * getTokenTimeout()))
                .claim("username", user.getUsername())
                .claim("name", user.getName())
                .claim("roles", user.getRoles())
                .signWith(getAlgorithm(), getSecret().getBytes("UTF-8"))
                .compact();
        response.setHeader("authorization", "Bearer " + jwt);
        return user;
    }

    public abstract U authenticateUser(LoginData data) throws Exception;

    public long getTokenTimeout() {
        return 60 * 60 * 24;
    }

    public SignatureAlgorithm getAlgorithm() {
        return SignatureAlgorithm.HS512;
    }

    public abstract String getSecret();
}
