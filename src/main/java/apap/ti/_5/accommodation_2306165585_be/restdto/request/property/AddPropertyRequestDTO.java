package apap.ti._5.accommodation_2306165585_be.restdto.request.property;


import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddPropertyRequestDTO {
   
    @NotBlank(message = "Nama properti wajib diisi")
    private String propertyName;

    /**
     * Tipe Properti
     * 1 = Hotel, 2 = Villa, 3 = Apartment
     */
    @NotNull(message = "Tipe properti wajib diisi")
    @Min(value = 1, message = "Tipe properti tidak valid")
    @Max(value = 3, message = "Tipe properti tidak valid")
    private Integer type;

    @NotNull(message = "Provinsi wajib diisi")
    @Min(value = 1, message = "Provinsi tidak valid")
    private Integer province;

    @NotBlank(message = "Alamat wajib diisi")
    private String address;

    @NotBlank(message = "Deskripsi wajib diisi")
    private String description;
    
    private UUID ownerId;

    @NotBlank(message = "Nama pemilik wajib diisi")
    private String ownerName; 
}
