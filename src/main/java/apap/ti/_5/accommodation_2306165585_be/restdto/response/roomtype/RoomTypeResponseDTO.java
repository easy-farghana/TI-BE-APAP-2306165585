package apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype;

import apap.ti._5.accommodation_2306165585_be.restdto.response.room.RoomResponseDTO;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeResponseDTO {
    private UUID roomTypeID;
    private String name;
    private int price;
    private String description;
    private int capacity;
    private String facility;
    private int floor;
    private List<RoomResponseDTO> listRoom;
}
