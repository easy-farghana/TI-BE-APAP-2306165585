package apap.ti._5.accommodation_2306165585_be.restdto.response.booking;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AllBookingResponseDTO {
    private UUID bookingID;
    private String propertyName;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private int totalPrice;
    int status;
    private String roomName;
}
