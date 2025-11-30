package apap.ti._5.accommodation_2306165585_be.service.room;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.Room;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.request.room.AddMaintenanceRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306165585_be.security.RoleGroup;
import apap.ti._5.accommodation_2306165585_be.security.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private RoomServiceImpl roomService;

    private UUID roomId;
    private UUID roomTypeId;
    private UUID propertyId;
    private UUID ownerId;
    
    private Property testProperty;
    private RoomType testRoomType;
    private Room testRoom;
    private AccommodationBooking testBooking;

    @BeforeEach
    void setUp() {
        roomId = UUID.randomUUID();
        roomTypeId = UUID.randomUUID();
        propertyId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        testProperty = Property.builder()
                .propertyID(propertyId)
                .propertyName("Test Hotel")
                .type(1)
                .address("Test Address")
                .province(1)
                .description("Test Description")
                .totalRoom(10)
                .income(0)
                .ownerID(ownerId)
                .ownerName("Test Owner")
                .activeStatus(1)
                .listRoomType(new ArrayList<>())
                .build();

        testRoomType = RoomType.builder()
                .roomTypeID(roomTypeId)
                .name("Deluxe")
                .floor(1)
                .capacity(2)
                .price(100000)
                .facility("WiFi, TV")
                .description("Deluxe Room")
                .property(testProperty)
                .listRoom(new ArrayList<>())
                .build();

        testRoom = Room.builder()
                .roomID(roomId)
                .name("101")
                .roomType(testRoomType)
                .listAccommodationBooking(new ArrayList<>())
                .activeRoom(1)
                .availabilityStatus(1)
                .build();

        testRoomType.getListRoom().add(testRoom);
        testProperty.getListRoomType().add(testRoomType);

        testBooking = AccommodationBooking.builder()
                .bookingID(UUID.randomUUID())
                .checkInDate(LocalDateTime.now().plusDays(1))
                .checkOutDate(LocalDateTime.now().plusDays(3))
                .totalDays(2)
                .totalPrice(200000)
                .status(0)
                .customerID(UUID.randomUUID())
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .customerPhone("08123456789")
                .isBreakfast(false)
                .capacity(2)
                .room(testRoom)
                .build();
    }

    // Test getAllRooms
    @Test
    void testGetAllRooms_WithoutDates() {
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));

        List<RoomResponseDTO> result = roomService.getAllRooms();

        assertEquals(1, result.size());
        assertEquals(roomId, result.get(0).getRoomID());
        assertEquals("101", result.get(0).getName());
        assertEquals(1, result.get(0).getAvailabilityStatus());
        verify(roomRepository).findAll();
    }

    @Test
    void testGetAllRooms_WithDates() {
        LocalDateTime checkIn = LocalDateTime.now().plusDays(5);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(7);

        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));

        List<RoomResponseDTO> result = roomService.getAllRooms(checkIn, checkOut);

        assertEquals(1, result.size());
        assertEquals(roomId, result.get(0).getRoomID());
        assertEquals(1, result.get(0).getAvailabilityStatus());
        verify(roomRepository).findAll();
    }

    @Test
    void testGetAllRooms_EmptyList() {
        when(roomRepository.findAll()).thenReturn(Collections.emptyList());

        List<RoomResponseDTO> result = roomService.getAllRooms();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllRooms_MultipleRooms() {
        Room room2 = Room.builder()
                .roomID(UUID.randomUUID())
                .name("102")
                .roomType(testRoomType)
                .listAccommodationBooking(new ArrayList<>())
                .activeRoom(1)
                .build();

        when(roomRepository.findAll()).thenReturn(Arrays.asList(testRoom, room2));

        List<RoomResponseDTO> result = roomService.getAllRooms();

        assertEquals(2, result.size());
    }

    @Test
    void testGetAllRooms_InactiveRoom() {
        testRoom.setActiveRoom(0);
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));

        List<RoomResponseDTO> result = roomService.getAllRooms();

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getAvailabilityStatus());
    }

    // Test getRoomsByRoomType
    @Test
    void testGetRoomsByRoomType_WithoutDates() {
        List<RoomResponseDTO> result = roomService.getRoomsByRoomType(testRoomType);

        assertEquals(1, result.size());
        assertEquals(roomId, result.get(0).getRoomID());
        assertEquals("101", result.get(0).getName());
    }

    @Test
    void testGetRoomsByRoomType_WithDates() {
        LocalDateTime checkIn = LocalDateTime.now().plusDays(5);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(7);

        List<RoomResponseDTO> result = roomService.getRoomsByRoomType(testRoomType, checkIn, checkOut);

        assertEquals(1, result.size());
        assertEquals(roomId, result.get(0).getRoomID());
        assertEquals(1, result.get(0).getAvailabilityStatus());
    }

    @Test
    void testGetRoomsByRoomType_EmptyList() {
        testRoomType.getListRoom().clear();

        List<RoomResponseDTO> result = roomService.getRoomsByRoomType(testRoomType);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetRoomsByRoomType_MultipleRooms() {
        Room room2 = Room.builder()
                .roomID(UUID.randomUUID())
                .name("102")
                .roomType(testRoomType)
                .listAccommodationBooking(new ArrayList<>())
                .activeRoom(1)
                .build();

        testRoomType.getListRoom().add(room2);

        List<RoomResponseDTO> result = roomService.getRoomsByRoomType(testRoomType);

        assertEquals(2, result.size());
    }

    @Test
    void testCreateRoom_FirstRoomOnFloor() {
        testRoomType.getListRoom().clear();
        testProperty.getListRoomType().clear();

        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room room = invocation.getArgument(0);
            room.setRoomID(roomId);
            return room;
        });

        Room result = roomService.createRoom(testProperty, testRoomType);

        assertNotNull(result);
        assertEquals("101", result.getName()); // Floor 1, first room
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void testCreateRoom_FloorTwo() {
        RoomType roomTypeFloor2 = RoomType.builder()
                .roomTypeID(UUID.randomUUID())
                .name("Suite")
                .floor(2)
                .capacity(4)
                .price(200000)
                .property(testProperty)
                .listRoom(new ArrayList<>())
                .build();

        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room room = invocation.getArgument(0);
            room.setRoomID(UUID.randomUUID());
            return room;
        });

        Room result = roomService.createRoom(testProperty, roomTypeFloor2);

        assertNotNull(result);
        assertEquals("201", result.getName()); // Floor 2, first room
        verify(roomRepository).save(any(Room.class));
    }


    @Test
    void testCreateRoom_NullRoomTypesList() {
        testProperty.setListRoomType(null);
        testRoomType.getListRoom().clear();

        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room room = invocation.getArgument(0);
            room.setRoomID(roomId);
            return room;
        });

        Room result = roomService.createRoom(testProperty, testRoomType);

        assertNotNull(result);
        assertEquals("101", result.getName());
        verify(roomRepository).save(any(Room.class));
    }

    // Test addMaintenance
    @Test
    void testAddMaintenance_Success_AsSUPERADMIN() {
        AddMaintenanceRequestDTO request = new AddMaintenanceRequestDTO();
        request.setRoomID(roomId);
        request.setMaintenanceStart(LocalDateTime.now().plusDays(10));
        request.setMaintenanceEnd(LocalDateTime.now().plusDays(12));

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        roomService.addMaintenance(request);

        verify(roomRepository).save(testRoom);
        assertEquals(request.getMaintenanceStart(), testRoom.getMaintenanceStart());
        assertEquals(request.getMaintenanceEnd(), testRoom.getMaintenanceEnd());
    }

    @Test
    void testAddMaintenance_Success_AsOwner() {
        AddMaintenanceRequestDTO request = new AddMaintenanceRequestDTO();
        request.setRoomID(roomId);
        request.setMaintenanceStart(LocalDateTime.now().plusDays(10));
        request.setMaintenanceEnd(LocalDateTime.now().plusDays(12));

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(ownerId);
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        roomService.addMaintenance(request);

        verify(roomRepository).save(testRoom);
    }

    @Test
    void testAddMaintenance_RoomNotFound() {
        AddMaintenanceRequestDTO request = new AddMaintenanceRequestDTO();
        request.setRoomID(roomId);

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> roomService.addMaintenance(request));
    }

    @Test
    void testAddMaintenance_Unauthorized_WrongOwner() {
        UUID differentOwnerId = UUID.randomUUID();
        AddMaintenanceRequestDTO request = new AddMaintenanceRequestDTO();
        request.setRoomID(roomId);
        request.setMaintenanceStart(LocalDateTime.now().plusDays(10));
        request.setMaintenanceEnd(LocalDateTime.now().plusDays(12));

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(differentOwnerId);

        assertThrows(SecurityException.class, () -> roomService.addMaintenance(request));
    }

    @Test
    void testAddMaintenance_InvalidDates_StartAfterEnd() {
        AddMaintenanceRequestDTO request = new AddMaintenanceRequestDTO();
        request.setRoomID(roomId);
        request.setMaintenanceStart(LocalDateTime.now().plusDays(12));
        request.setMaintenanceEnd(LocalDateTime.now().plusDays(10));

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> roomService.addMaintenance(request));

        assertEquals("maintenanceStart must be before maintenanceEnd", exception.getMessage());
    }

    @Test
    void testAddMaintenance_RoomNotAvailable_HasBooking() {
        testRoom.getListAccommodationBooking().add(testBooking);
        
        AddMaintenanceRequestDTO request = new AddMaintenanceRequestDTO();
        request.setRoomID(roomId);
        request.setMaintenanceStart(LocalDateTime.now().plusDays(1));
        request.setMaintenanceEnd(LocalDateTime.now().plusDays(2));

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> roomService.addMaintenance(request));

        assertEquals("Room is not available for maintenance", exception.getMessage());
    }

    @Test
    void testAddMaintenance_RoomAvailable_CancelledBooking() {
        testBooking.setStatus(2); // Cancelled
        testRoom.getListAccommodationBooking().add(testBooking);
        
        AddMaintenanceRequestDTO request = new AddMaintenanceRequestDTO();
        request.setRoomID(roomId);
        request.setMaintenanceStart(LocalDateTime.now().plusDays(1));
        request.setMaintenanceEnd(LocalDateTime.now().plusDays(2));

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        roomService.addMaintenance(request);

        verify(roomRepository).save(testRoom);
    }

    // Test deleteRoom
    @Test
    void testDeleteRoom_Success() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        roomService.deleteRoom(roomId);

        verify(roomRepository).save(testRoom);
        assertEquals(0, testRoom.getActiveRoom());
        assertEquals(0, testRoom.getAvailabilityStatus());
    }

    @Test
    void testDeleteRoom_NotFound() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> roomService.deleteRoom(roomId));
    }

    // Test isRoomAvailable
    @Test
    void testIsRoomAvailable_NoBookings_NoMaintenance() {
        LocalDateTime checkIn = LocalDateTime.now().plusDays(1);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(3);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertTrue(result);
    }

    @Test
    void testIsRoomAvailable_WithActiveBooking_Overlapping() {
        testRoom.getListAccommodationBooking().add(testBooking);
        
        LocalDateTime checkIn = LocalDateTime.now().plusDays(2);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(4);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertFalse(result);
    }

    @Test
    void testIsRoomAvailable_WithActiveBooking_NotOverlapping() {
        testRoom.getListAccommodationBooking().add(testBooking);
        
        LocalDateTime checkIn = LocalDateTime.now().plusDays(10);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(12);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertTrue(result);
    }

    @Test
    void testIsRoomAvailable_WithCancelledBooking() {
        testBooking.setStatus(2); // Cancelled
        testRoom.getListAccommodationBooking().add(testBooking);
        
        LocalDateTime checkIn = LocalDateTime.now().plusDays(2);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(4);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertTrue(result); // Cancelled bookings don't block availability
    }

    @Test
    void testIsRoomAvailable_WithMaintenance_Overlapping() {
        testRoom.setMaintenanceStart(LocalDateTime.now().plusDays(1));
        testRoom.setMaintenanceEnd(LocalDateTime.now().plusDays(3));
        
        LocalDateTime checkIn = LocalDateTime.now().plusDays(2);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(4);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertFalse(result);
    }

    @Test
    void testIsRoomAvailable_WithMaintenance_NotOverlapping() {
        testRoom.setMaintenanceStart(LocalDateTime.now().plusDays(1));
        testRoom.setMaintenanceEnd(LocalDateTime.now().plusDays(3));
        
        LocalDateTime checkIn = LocalDateTime.now().plusDays(10);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(12);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertTrue(result);
    }

    @Test
    void testIsRoomAvailable_NoMaintenance() {
        testRoom.setMaintenanceStart(null);
        testRoom.setMaintenanceEnd(null);
        
        LocalDateTime checkIn = LocalDateTime.now().plusDays(1);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(3);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertTrue(result);
    }

    @Test
    void testIsRoomAvailable_EdgeCase_CheckOutEqualsCheckIn() {
        testRoom.getListAccommodationBooking().add(testBooking);
        
        // Check-out of new booking equals check-in of existing booking
        LocalDateTime checkIn = LocalDateTime.now();
        LocalDateTime checkOut = testBooking.getCheckInDate();

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertFalse(result); // Should overlap
    }

    @Test
    void testIsRoomAvailable_EdgeCase_CheckInEqualsCheckOut() {
        testRoom.getListAccommodationBooking().add(testBooking);
        
        // Check-in of new booking equals check-out of existing booking
        LocalDateTime checkIn = testBooking.getCheckOutDate();
        LocalDateTime checkOut = testBooking.getCheckOutDate().plusDays(2);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertFalse(result); // Should overlap
    }

    @Test
    void testIsRoomAvailable_MultipleBookings_AllNonOverlapping() {
        AccommodationBooking booking2 = AccommodationBooking.builder()
                .bookingID(UUID.randomUUID())
                .checkInDate(LocalDateTime.now().plusDays(10))
                .checkOutDate(LocalDateTime.now().plusDays(12))
                .status(0)
                .room(testRoom)
                .build();

        testRoom.getListAccommodationBooking().add(testBooking);
        testRoom.getListAccommodationBooking().add(booking2);
        
        LocalDateTime checkIn = LocalDateTime.now().plusDays(5);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(7);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertTrue(result);
    }

    @Test
    void testIsRoomAvailable_MultipleBookings_OneOverlapping() {
        AccommodationBooking booking2 = AccommodationBooking.builder()
                .bookingID(UUID.randomUUID())
                .checkInDate(LocalDateTime.now().plusDays(5))
                .checkOutDate(LocalDateTime.now().plusDays(7))
                .status(0)
                .room(testRoom)
                .build();

        testRoom.getListAccommodationBooking().add(testBooking);
        testRoom.getListAccommodationBooking().add(booking2);
        
        LocalDateTime checkIn = LocalDateTime.now().plusDays(6);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(8);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertFalse(result);
    }

    // Test mapToRoomDTO with different statuses
    @Test
    void testMapToRoomDTO_ActiveRoomAvailable() {
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));

        List<RoomResponseDTO> result = roomService.getAllRooms();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getAvailabilityStatus());
    }

    @Test
    void testMapToRoomDTO_ActiveRoomWithBooking() {
        testRoom.getListAccommodationBooking().add(testBooking);
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));

        List<RoomResponseDTO> result = roomService.getAllRooms();

        assertEquals(1, result.size());
        // Availability depends on whether booking overlaps with default check-in (now to tomorrow)
    }

    @Test
    void testMapToRoomDTO_InactiveRoom() {
        testRoom.setActiveRoom(0);
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));

        List<RoomResponseDTO> result = roomService.getAllRooms();

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getAvailabilityStatus());
    }

    @Test
    void testMapToRoomDTO_WithDates_ActiveRoomAvailable() {
        LocalDateTime checkIn = LocalDateTime.now().plusDays(10);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(12);

        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));

        List<RoomResponseDTO> result = roomService.getAllRooms(checkIn, checkOut);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getAvailabilityStatus());
    }

    @Test
    void testMapToRoomDTO_WithDates_ActiveRoomUnavailable() {
        testRoom.getListAccommodationBooking().add(testBooking);
        
        LocalDateTime checkIn = LocalDateTime.now().plusDays(2);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(4);

        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));

        List<RoomResponseDTO> result = roomService.getAllRooms(checkIn, checkOut);

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getAvailabilityStatus());
    }

    @Test
    void testMapToRoomDTO_WithDates_InactiveRoom() {
        testRoom.setActiveRoom(0);
        LocalDateTime checkIn = LocalDateTime.now().plusDays(10);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(12);

        when(roomRepository.findAll()).thenReturn(Collections.singletonList(testRoom));

        List<RoomResponseDTO> result = roomService.getAllRooms(checkIn, checkOut);

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getAvailabilityStatus());
    }

    // Test date overlap logic thoroughly
    @Test
    void testIsRoomAvailable_CompletelyBefore() {
        testRoom.getListAccommodationBooking().add(testBooking);
        
        // New booking completely before existing
        LocalDateTime checkIn = LocalDateTime.now().minusDays(5);
        LocalDateTime checkOut = LocalDateTime.now().minusDays(3);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertTrue(result);
    }

    @Test
    void testIsRoomAvailable_CompletelyAfter() {
        testRoom.getListAccommodationBooking().add(testBooking);
        
        // New booking completely after existing
        LocalDateTime checkIn = LocalDateTime.now().plusDays(10);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(12);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertTrue(result);
    }

    @Test
    void testIsRoomAvailable_PartialOverlap_Start() {
        testRoom.getListAccommodationBooking().add(testBooking);
        
        // New booking overlaps start of existing
        LocalDateTime checkIn = LocalDateTime.now();
        LocalDateTime checkOut = LocalDateTime.now().plusDays(2);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertFalse(result);
    }

    @Test
    void testIsRoomAvailable_PartialOverlap_End() {
        testRoom.getListAccommodationBooking().add(testBooking);
        
        // New booking overlaps end of existing
        LocalDateTime checkIn = LocalDateTime.now().plusDays(2);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(5);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertFalse(result);
    }

    @Test
    void testIsRoomAvailable_CompletelyInside() {
        testRoom.getListAccommodationBooking().add(testBooking);
        
        // New booking completely inside existing
        LocalDateTime checkIn = LocalDateTime.now().plusDays(1).plusHours(12);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(2);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertFalse(result);
    }

    @Test
    void testIsRoomAvailable_CompletelyContains() {
        testRoom.getListAccommodationBooking().add(testBooking);
        
        // New booking completely contains existing
        LocalDateTime checkIn = LocalDateTime.now();
        LocalDateTime checkOut = LocalDateTime.now().plusDays(5);

        boolean result = roomService.isRoomAvailable(testRoom, checkIn, checkOut);

        assertFalse(result);
    }

}