package apap.ti._5.accommodation_2306165585_be.restdto.response.booking;

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
public class AccommodationBookingResponseDTO {
    private String bookingID;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private int totalDays;
    private int totalPrice;
    private int status;
    private UUID customerID;    
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private boolean isBreakfast;
    private int refund;
    private int extraPay;
    private int capacity;
    private String roomName;
    private String roomID;
    private String roomTypeID;
    private String propertyName;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
