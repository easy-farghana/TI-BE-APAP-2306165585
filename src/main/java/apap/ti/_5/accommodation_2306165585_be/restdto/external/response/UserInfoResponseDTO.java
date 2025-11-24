package apap.ti._5.accommodation_2306165585_be.restdto.external.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserInfoResponseDTO {
    private UUID id;
    private String username;
    private String email;
    private String name;
    private Boolean gender;
    private String userType; // "STAFF" or "CUSTOMER"
    private String role; // For staff: role name, for customer: "CUSTOMER"
    private Long saldo; // Only for customers, null for staff
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
