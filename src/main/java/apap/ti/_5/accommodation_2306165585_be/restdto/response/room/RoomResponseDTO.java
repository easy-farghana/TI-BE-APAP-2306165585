package apap.ti._5.accommodation_2306165585_be.restdto.response.room;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDTO {
    private UUID roomID;
    private String name;
    private int availabilityStatus;
}
