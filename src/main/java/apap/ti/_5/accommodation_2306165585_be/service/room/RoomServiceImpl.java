package apap.ti._5.accommodation_2306165585_be.service.room;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306165585_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.Room;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.response.room.RoomResponseDTO;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepository roomRepository;
    
    /**
     * Gets all rooms in the system using the default search period.
     * @return a list of RoomResponseDTO representing all rooms
    */
    @Override
    public List<RoomResponseDTO> getAllRooms() {
        return roomRepository.findAll().stream()
            .map(this::mapToRoomDTO)
            .toList();
    }

    /**
     * Gets all rooms in the system for a specified search period.
     * @return a list of RoomResponseDTO representing all rooms
    */
    @Override
    public List<RoomResponseDTO> getAllRooms(LocalDateTime checkIn, LocalDateTime checkOut) {
        return roomRepository.findAll().stream()
            .map(room -> mapToRoomDTO(room, checkIn, checkOut))
            .toList();
    }

    @Override
    public List<RoomResponseDTO> getRoomsByRoomType(RoomType roomType) {
        return roomType.getListRoom().stream()
            .map(this::mapToRoomDTO)
            .toList();
    }

    @Override
    public List<RoomResponseDTO> getRoomsByRoomType(RoomType roomType, LocalDateTime checkIn, LocalDateTime checkOut) {
        return roomType.getListRoom().stream()
            .map(room -> mapToRoomDTO(room, checkIn, checkOut))
            .toList();
    }

    @Override
    public Room createRoom(Property property, RoomType roomType) {
        String roomName = String.format("%d%02d", roomType.getFloor(), roomType.getListRoom().size() + 1);
        String roomId = String.format("%s-%s", property.getPropertyID(), roomName);
        
        Room room = Room.builder()
            .roomID(roomId)
            .name(roomName)
            .listAccommodationBooking(new ArrayList<>())
            .build();
        return roomRepository.save(room);
    }

    /**
     * Maps a Room entity to a RoomResponseDTO using the default search period.
     * <p>
     * The default period is from the current date-time (as check-in)
     * to the next day (as check-out).
     * <br>
     * It automatically checks room availability based on active bookings and
     * maintenance schedules within that default period.
     *
     * @param room the Room entity to be converted
     * @return a RoomResponseDTO containing the room information and availability status
     */
    private RoomResponseDTO mapToRoomDTO(Room room) {
        LocalDateTime checkIn = LocalDateTime.now();
        LocalDateTime checkOut = checkIn.plusDays(1);
        int availabilityStatus = isRoomAvailable(room, checkIn, checkOut) ? 1 : 0;
        return RoomResponseDTO.builder()
            .roomID(room.getRoomID())
            .name(room.getName())
            .availabilityStatus(availabilityStatus)
            .build();
    }

    /**
     * Maps a Room entity to a RoomResponseDTO for a specified search period.
     * <p>
     * The availability status is determined based on whether the room
     * has any active booking or maintenance that overlaps with the given date range.
     *
     * @param room the Room entity to map
     * @param checkIn the start date-time of the search period
     * @param checkOut the end date-time of the search period
     * @return a RoomResponseDTO with room details and computed availability status
     */
    private RoomResponseDTO mapToRoomDTO(Room room, LocalDateTime checkIn, LocalDateTime checkOut) {
        int availabilityStatus = isRoomAvailable(room, checkIn, checkOut) ? 1 : 0;
        return RoomResponseDTO.builder()
            .roomID(room.getRoomID())
            .name(room.getName())
            .availabilityStatus(availabilityStatus)
            .build();
    }

    private boolean isRoomAvailable(Room room, LocalDateTime checkIn, LocalDateTime checkOut) {
        List<AccommodationBooking> bookings = room.getListAccommodationBooking();
        for (AccommodationBooking booking : bookings) {
            if (booking.getStatus() != 2 && 
                isDatesOverlap(booking.getCheckInDate(), booking.getCheckOutDate(), checkIn, checkOut)) {
                return false;
            }
        }

        if (room.getMaintenanceStart() != null && room.getMaintenanceEnd() != null) {
            if (isDatesOverlap(room.getMaintenanceStart(), room.getMaintenanceEnd(), checkIn, checkOut)) {
                return false;
            }
        }

        return true;
    }

    private boolean isDatesOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return !(end2.isBefore(start1) || start2.isAfter(end1));
    }
    
}
