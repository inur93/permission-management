package dk.agenia.permissionmanagement.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Created: 05-10-2018</p>
 * <p>author: Runi</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissingRoleError {
    private String message;
    private int status;
    private String[] missingRoles;
}
