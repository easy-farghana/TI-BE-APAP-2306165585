package apap.ti._5.accommodation_2306165585_be.restdto.response.booking;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AllBookingResponseDTO {
    private String bookingID;
    private String propertyName;

    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private int totalPrice;
    int status;
    private String roomName;
}
