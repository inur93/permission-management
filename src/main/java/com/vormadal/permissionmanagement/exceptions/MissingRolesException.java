package com.vormadal.permissionmanagement.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * <p>Created: 05-10-2018</p>
 * <p>author: Runi</p>
 */

public class MissingRolesException extends WebApplicationException {
    private String[] roles;
    public MissingRolesException(String msg, String[] roles, Response.Status status){
        super(msg, status);
        this.roles = roles;
    }

    public String[] getMissingRoles(){
        return this.roles;
    }


}
