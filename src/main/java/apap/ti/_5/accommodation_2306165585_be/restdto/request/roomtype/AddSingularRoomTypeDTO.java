package apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddSingularRoomTypeDTO {

    @NotBlank(message = "propertyID wajib diisi")
    private UUID propertyID;

    @NotBlank(message = "Nama tipe kamar wajib diisi")
    private String name;

    @NotBlank(message = "Fasilitas wajib diisi")
    private String facility;

    @NotNull(message = "Harga wajib diisi")
    @Positive(message = "Harga harus lebih dari 0")
    private Integer price;

    @NotBlank(message = "Deskripsi wajib diisi")
    private String description;
    
    @NotNull(message = "Lantai wajib diisi")
    @Min(value = 1, message = "Lantai minimal 1")
    private Integer floor;

    @NotNull(message = "Kapasitas wajib diisi")
    @Min(value = 1, message = "Kapasitas minimal 1")
    private Integer capacity;

    @NotNull(message = "Jumlah unit wajib diisi")
    @Min(value = 1, message = "Minimal 1 unit kamar dibuat")
    private Integer unit;  
}
