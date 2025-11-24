package apap.ti._5.accommodation_2306165585_be.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import java.util.UUID;
import lombok.Data;

@Component
@RequestScope
@Data
public class UserContext {
    private UUID userID;
    private String role;
    private String email;
    private String name;
}


