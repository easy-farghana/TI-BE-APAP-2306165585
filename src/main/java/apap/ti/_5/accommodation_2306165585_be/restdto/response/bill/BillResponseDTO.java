package apap.ti._5.accommodation_2306165585_be.restdto.response.bill;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponseDTO {

    private UUID billID;

    private UUID customerID;

    private String serviceName;

    private String serviceReferenceID;

    private String description;

    private Integer status;

    private Long amount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime paymentTimestamp;
}
