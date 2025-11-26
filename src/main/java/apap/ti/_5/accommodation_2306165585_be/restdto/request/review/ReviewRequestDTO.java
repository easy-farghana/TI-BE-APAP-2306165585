package apap.ti._5.accommodation_2306165585_be.restdto.request.review;


import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDTO {

    private UUID bookingID;

    private String comment;

    @Min(value = 1, message = "Cleanliness rating must be at least 1")
    @Max(value = 5, message = "Cleanliness rating must be at most 5")
    private int cleanlinessRating;

    @Min(value = 1, message = "Facility rating must be at least 1")
    @Max(value = 5, message = "Facility rating must be at most 5")
    private int facilityRating;

    @Min(value = 1, message = "Service rating must be at least 1")
    @Max(value = 5, message = "Service rating must be at most 5")
    private int serviceRating;

    @Min(value = 1, message = "Value rating must be at least 1")
    @Max(value = 5, message = "Value rating must be at most 5")
    private int valueRating;

    private LocalDateTime createdAt;
}