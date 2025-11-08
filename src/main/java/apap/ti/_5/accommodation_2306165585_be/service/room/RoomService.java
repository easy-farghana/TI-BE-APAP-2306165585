package apap.ti._5.accommodation_2306165585_be.service.room;

import java.time.LocalDateTime;
import java.util.List;

import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.Room;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.restdto.response.room.RoomResponseDTO;

public interface RoomService {
    List<RoomResponseDTO> getAllRooms();
    List<RoomResponseDTO> getAllRooms(LocalDateTime checkIn, LocalDateTime checkOut);
    List<RoomResponseDTO> getRoomsByRoomType(RoomType roomType);
    List<RoomResponseDTO> getRoomsByRoomType(RoomType roomType, LocalDateTime checkIn, LocalDateTime checkOut);
    Room createRoom(Property property, RoomType roomType);
    void deleteRoom(String roomId);
    boolean isRoomAvailable(Room room, LocalDateTime checkIn, LocalDateTime checkOut);
}
