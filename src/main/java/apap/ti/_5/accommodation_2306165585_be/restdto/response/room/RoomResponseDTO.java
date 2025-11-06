package apap.ti._5.accommodation_2306165585_be.restdto.response.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDTO {
    private String roomID;
    private String name;
    private int availabilityStatus;
}
