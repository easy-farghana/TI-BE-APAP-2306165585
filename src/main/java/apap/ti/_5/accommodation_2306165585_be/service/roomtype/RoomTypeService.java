package apap.ti._5.accommodation_2306165585_be.service.roomtype;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.AddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype.RoomTypeResponseDTO;

public interface RoomTypeService {
    List<RoomTypeResponseDTO> getAllRoomTypes();
    List<RoomTypeResponseDTO> getRoomTypesByProperty(Property property);
    // RoomType createRoomType(AddRoomTypeRequestDTO request);
    RoomType updateRoomType(RoomType roomType);
    List<RoomTypeResponseDTO> getRoomTypesByProperty(Property property, LocalDateTime checkIn, LocalDateTime checkOut);
    RoomTypeResponseDTO getRoomTypeById(UUID roomTypeId);
    RoomType createRoomType(AddRoomTypeRequestDTO request, Property property);
}
