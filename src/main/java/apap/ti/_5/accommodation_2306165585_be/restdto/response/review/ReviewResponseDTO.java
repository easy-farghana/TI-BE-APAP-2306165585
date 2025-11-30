package apap.ti._5.accommodation_2306165585_be.restdto.response.review;

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
public class ReviewResponseDTO {
    private UUID reviewID;
    private UUID customerID;
    private String customerName;
    private String propertyName;
    private UUID bookingID;
    private String comment;
    private int cleanlinessRating;
    private int facilityRating;
    private int serviceRating;
    private int valueRating;
    private int overallRating;
    private LocalDateTime createdAt;
}
