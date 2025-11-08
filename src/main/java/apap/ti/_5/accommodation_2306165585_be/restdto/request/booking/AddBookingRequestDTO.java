package apap.ti._5.accommodation_2306165585_be.restdto.request.booking;


import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddBookingRequestDTO {
    
    @NotBlank(message = "roomID tidak boleh kosong")
    private String roomID;

    @NotBlank(message = "roomTypeId tidak boleh kosong")
    private String roomTypeId;

    @NotNull(message = "checkInDate tidak boleh kosong")
    private LocalDateTime checkInDate;

    @NotNull(message = "checkOutDate tidak boleh kosong")
    private LocalDateTime checkOutDate;

    @NotNull(message = "capacity tidak boleh kosong")
    private Integer capacity;

    @NotNull(message = "customerId tidak boleh kosong")
    private UUID customerID;
    
    @NotBlank(message = "customerName tidak boleh kosong")
    private String customerName;

    @NotBlank(message = "email tidak boleh kosong")
    @Email(message = "format email harus valid")
    private String customerEmail;

    @NotBlank(message = "nomor telepon tidak boleh kosong")
    private String customerPhone;

    @NotNull(message = "isBreakfast tidak boleh kosong")
    private Boolean isBreakfast;
}
