package dk.agenia.permissionmanagement.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created: 14-04-2018
 * Owner: Runi
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestError {
    public String message;
    public int status;
}
