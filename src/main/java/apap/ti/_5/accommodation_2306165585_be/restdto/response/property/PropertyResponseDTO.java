package apap.ti._5.accommodation_2306165585_be.restdto.response.property;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype.RoomTypeResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponseDTO {
    private UUID propertyID;
    private String propertyName;
    private int type;
    private String address;
    private int province;
    private String description;
    private int totalRoom;
    private int income;
    private int activeStatus;
    private List<RoomTypeResponseDTO> listRoomType;
    private String ownerName;
    private UUID ownerID;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
