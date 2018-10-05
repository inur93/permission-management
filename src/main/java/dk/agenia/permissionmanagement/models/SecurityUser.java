package dk.agenia.permissionmanagement.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

/**
 * Created: 14-04-2018
 * Owner: Runi
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SecurityUser extends BaseModel implements Principal {

    private Set<String> roles;
    private String username;
    @JsonIgnore
    private String hash;

    @Override
    public String getName() {
        return username;
    }
}
