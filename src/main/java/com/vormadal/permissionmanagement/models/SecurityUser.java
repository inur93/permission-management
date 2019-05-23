package com.vormadal.permissionmanagement.models;

import java.security.Principal;
import java.util.Set;

/**
 * Created: 14-04-2018
 * Author: Runi
 */

public interface SecurityUser extends Principal {
    Set<String> getRoles();
}
