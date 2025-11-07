package apap.ti._5.accommodation_2306165585_be.restdto.request.property;

import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import jakarta.validation.Valid;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdatePropertyTransactionRequest {
    @Valid
    @NotNull(message = "Data properti wajib diisi")
    private UpdatePropertyRequestDTO property;

    @Valid
    @NotEmpty(message = "Minimal harus ada satu tipe kamar")
    private List<UpdateRoomTypeRequestDTO> roomTypes;
    
}
