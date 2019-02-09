package com.vormadal.permissionmanagement.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created: 14-04-2018
 * Author: Runi
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestError {
    public String message;
    public int status;
}
