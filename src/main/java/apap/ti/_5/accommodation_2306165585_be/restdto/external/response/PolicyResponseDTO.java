package apap.ti._5.accommodation_2306165585_be.restdto.external.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class PolicyResponseDTO {
    
    private String id;

    private String bookingId;

    private String userId;

    private LocalDate startDate;

    private String status;

    private LocalDateTime updatedAt;
}