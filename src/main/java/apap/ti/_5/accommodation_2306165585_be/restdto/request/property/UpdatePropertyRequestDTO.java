package apap.ti._5.accommodation_2306165585_be.restdto.request.property;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdatePropertyRequestDTO {
    @NotNull(message = "ID properti wajib diisi")
    private String propertyId;

    @NotBlank(message = "Nama properti wajib diisi")
    private String propertyName;

    @NotBlank(message = "Alamat wajib diisi")
    private String address;

    @NotBlank(message = "Deskripsi wajib diisi")
    private String description;
}