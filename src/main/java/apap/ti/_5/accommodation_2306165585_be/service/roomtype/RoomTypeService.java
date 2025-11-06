package apap.ti._5.accommodation_2306165585_be.service.roomtype;

import java.util.List;

import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.AddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype.RoomTypeResponseDTO;

public interface RoomTypeService {
    List<RoomTypeResponseDTO> getAllRoomTypes();
    List<RoomTypeResponseDTO> getRoomTypesByProperty(Property property);
    RoomType createRoomType(AddRoomTypeRequestDTO request, Property property);
}
