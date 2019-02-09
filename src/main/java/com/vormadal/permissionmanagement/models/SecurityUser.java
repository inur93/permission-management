package com.vormadal.permissionmanagement.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;

/**
 * Created: 14-04-2018
 * Author: Runi
 */

@Data
public abstract class SecurityUser implements Principal {
    private Set<String> roles;
}
