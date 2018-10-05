package dk.agenia.permissionmanagement.models;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Map;
import java.util.Set;

/**
 * <p>Created: 05-10-2018</p>
 * <p>author: Runi</p>
 */

public class ApplicationSecurityContext implements SecurityContext {

    private SecurityUser user;
    private Set<String> roles;
    private String scheme;
    public ApplicationSecurityContext(SecurityUser user, String scheme){
        this.user = user;
        this.scheme = scheme;
    }

    @Override
    public Principal getUserPrincipal() {
        return user;
    }

    @Override
    public boolean isUserInRole(String role) {
        return user.getRoles().contains(role);
    }

    @Override
    public boolean isSecure() {
        return "https".equalsIgnoreCase(scheme);
    }

    @Override
    public String getAuthenticationScheme() {
        return scheme;
    }
}
