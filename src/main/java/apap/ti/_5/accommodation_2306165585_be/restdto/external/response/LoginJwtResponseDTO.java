package apap.ti._5.accommodation_2306165585_be.restdto.external.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginJwtResponseDTO {
    private String token;
    private UUID id;
    private String username;
    private String email;
    private String name;
    private String userType;
    private String role;
    private Long saldo; 
}