package apap.ti._5.accommodation_2306165585_be.service.room;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.Room;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.request.room.AddMaintenanceRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.room.RoomResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomServiceImpl roomService;

    private Room room;
    private Property property;
    private RoomType roomType;

    @BeforeEach
    void setUp() {
        room = Room.builder()
                .roomID("room-1")
                .name("101")
                .activeRoom(1)
                .availabilityStatus(1)
                .listAccommodationBooking(new ArrayList<>())
                .build();

        roomType = RoomType.builder()
                .floor(1)
                .listRoom(new ArrayList<>(List.of(room)))
                .build();

        property = Property.builder()
                .propertyID("property-1")
                .listRoomType(new ArrayList<>(List.of(roomType)))
                .build();
    }

    @Test
    void testGetAllRooms_ReturnsMappedList() {
        when(roomRepository.findAll()).thenReturn(List.of(room));

        List<RoomResponseDTO> result = roomService.getAllRooms();

        assertEquals(1, result.size());
        assertEquals(room.getRoomID(), result.get(0).getRoomID());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void testCreateRoom_Success() {
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));

        Room result = roomService.createRoom(property, roomType);

        assertNotNull(result);
        assertTrue(result.getRoomID().startsWith("property-1-"));
        assertEquals(1, result.getListAccommodationBooking().size() == 0 ? 0 : 1);
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testAddMaintenance_Success() {
        AddMaintenanceRequestDTO request = new AddMaintenanceRequestDTO();
        request.setRoomID("room-1");
        request.setMaintenanceStart(LocalDateTime.of(2025, 11, 1, 10, 0));
        request.setMaintenanceEnd(LocalDateTime.of(2025, 11, 2, 10, 0));

        when(roomRepository.findById("room-1")).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        roomService.addMaintenance(request);

        assertEquals(request.getMaintenanceStart(), room.getMaintenanceStart());
        assertEquals(request.getMaintenanceEnd(), room.getMaintenanceEnd());
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void testAddMaintenance_RoomNotFound() {
        AddMaintenanceRequestDTO request = new AddMaintenanceRequestDTO();
        request.setRoomID("invalid-room");
        when(roomRepository.findById("invalid-room")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> roomService.addMaintenance(request));
    }

    @Test
    void testAddMaintenance_InvalidDateRange() {
        AddMaintenanceRequestDTO request = new AddMaintenanceRequestDTO();
        request.setRoomID("room-1");
        request.setMaintenanceStart(LocalDateTime.of(2025, 11, 3, 10, 0));
        request.setMaintenanceEnd(LocalDateTime.of(2025, 11, 2, 10, 0));

        when(roomRepository.findById("room-1")).thenReturn(Optional.of(room));

        assertThrows(IllegalArgumentException.class, () -> roomService.addMaintenance(request));
    }

    @Test
    void testDeleteRoom_Success() {
        when(roomRepository.findById("room-1")).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        roomService.deleteRoom("room-1");

        assertEquals(0, room.getActiveRoom());
        assertEquals(0, room.getAvailabilityStatus());
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void testDeleteRoom_NotFound() {
        when(roomRepository.findById("invalid-room")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> roomService.deleteRoom("invalid-room"));
    }

    @Test
    void testIsRoomAvailable_NoBookings_ReturnsTrue() {
        boolean available = roomService.isRoomAvailable(room,
                LocalDateTime.of(2025, 11, 1, 10, 0),
                LocalDateTime.of(2025, 11, 2, 10, 0));

        assertTrue(available);
    }

    @Test
    void testIsRoomAvailable_WithOverlappingBooking_ReturnsFalse() {
        AccommodationBooking booking = AccommodationBooking.builder()
                .checkInDate(LocalDateTime.of(2025, 11, 1, 12, 0))
                .checkOutDate(LocalDateTime.of(2025, 11, 3, 10, 0))
                .status(1) // not cancelled
                .build();
        room.getListAccommodationBooking().add(booking);

        boolean available = roomService.isRoomAvailable(room,
                LocalDateTime.of(2025, 11, 2, 9, 0),
                LocalDateTime.of(2025, 11, 4, 10, 0));

        assertFalse(available);
    }

    @Test
    void testIsRoomAvailable_WithNonOverlappingBooking_ReturnsTrue() {
        AccommodationBooking booking = AccommodationBooking.builder()
                .checkInDate(LocalDateTime.of(2025, 11, 5, 12, 0))
                .checkOutDate(LocalDateTime.of(2025, 11, 7, 10, 0))
                .status(1)
                .build();
        room.getListAccommodationBooking().add(booking);

        boolean available = roomService.isRoomAvailable(room,
                LocalDateTime.of(2025, 11, 1, 10, 0),
                LocalDateTime.of(2025, 11, 2, 10, 0));

        assertTrue(available);
    }
}
