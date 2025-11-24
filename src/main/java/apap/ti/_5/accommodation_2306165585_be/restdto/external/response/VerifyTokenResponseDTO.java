package apap.ti._5.accommodation_2306165585_be.restdto.external.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VerifyTokenResponseDTO {
    private boolean valid;
    private UUID userId;
    private String username;
    private String email;
    private String name;
    private String userType; // "STAFF" or "CUSTOMER"
    private String role; // Role name or "CUSTOMER"
    private String message;
}

