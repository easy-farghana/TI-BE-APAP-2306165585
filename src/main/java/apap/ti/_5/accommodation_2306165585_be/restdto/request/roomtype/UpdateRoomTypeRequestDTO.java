package apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateRoomTypeRequestDTO {
    @NotNull(message = "ID tipe kamar wajib diisi")
    private String roomTypeID;

    @NotBlank(message = "Fasilitas wajib diisi")
    private String facility;

    @NotNull(message = "Harga wajib diisi")
    @Positive(message = "Harga harus lebih dari 0")
    private Integer price;

    @NotBlank(message = "Deskripsi wajib diisi")
    private String description;

    @NotNull(message = "Kapasitas wajib diisi")
    @Min(value = 1, message = "Kapasitas minimal 1")
    private Integer capacity;
}