package apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ListAddRoomTypeRequestDTO {
    
    @Valid
    @NotEmpty(message = "Minimal harus ada satu tipe kamar")
    private List<AddRoomTypeRequestDTO> roomTypes;
}
