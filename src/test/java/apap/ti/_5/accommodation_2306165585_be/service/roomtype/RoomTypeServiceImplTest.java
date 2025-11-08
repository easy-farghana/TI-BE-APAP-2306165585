package apap.ti._5.accommodation_2306165585_be.service.roomtype;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.Room;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.AddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;

@ExtendWith(MockitoExtension.class)
public class RoomTypeServiceImplTest {

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomTypeServiceImpl roomTypeService;

    private RoomType roomType1;
    private RoomType roomType2;
    private Property property;
    private Room room1;
    private RoomResponseDTO roomResponseDTO;
    private AddRoomTypeRequestDTO addRoomTypeRequest;

    @BeforeEach
    void setUp() {
        // Setup Property
        property = Property.builder()
                .propertyID("HOT-1234-001")
                .propertyName("Hotel Paradise")
                .type(1)
                .listRoomType(new ArrayList<>())
                .build();

        // Setup Room
        room1 = new Room();
        room1.setRoomID("ROOM-001");
        room1.setActiveRoom(1);

        // Setup RoomType 1
        roomType1 = RoomType.builder()
                .roomTypeID("001-Deluxe-1")
                .name("Deluxe")
                .price(1000000)
                .description("Deluxe room with city view")
                .capacity(2)
                .facility("WiFi, TV, AC")
                .floor(1)
                .listRoom(new ArrayList<>(Arrays.asList(room1)))
                .build();

        // Setup RoomType 2
        roomType2 = RoomType.builder()
                .roomTypeID("001-Suite-2")
                .name("Suite")
                .price(2000000)
                .description("Luxury suite")
                .capacity(4)
                .facility("WiFi, TV, AC, Jacuzzi")
                .floor(2)
                .listRoom(new ArrayList<>())
                .build();

        // Setup RoomResponseDTO
        roomResponseDTO = RoomResponseDTO.builder()
                .roomID("ROOM-001")
                .availabilityStatus(1)
                .build();

        // Setup AddRoomTypeRequestDTO
        addRoomTypeRequest = new AddRoomTypeRequestDTO();
        addRoomTypeRequest.setName("Deluxe");
        addRoomTypeRequest.setPrice(1000000);
        addRoomTypeRequest.setDescription("Deluxe room");
        addRoomTypeRequest.setCapacity(2);
        addRoomTypeRequest.setFacility("WiFi, TV");
        addRoomTypeRequest.setFloor(1);
        addRoomTypeRequest.setUnit(5);
    }

    @Test
    void testGetAllRoomTypes_Success() {
        // Arrange
        List<RoomType> roomTypes = Arrays.asList(roomType1, roomType2);
        when(roomTypeRepository.findAll()).thenReturn(roomTypes);
        when(roomService.getRoomsByRoomType(any(RoomType.class)))
                .thenReturn(Arrays.asList(roomResponseDTO));

        // Act
        List<RoomTypeResponseDTO> result = roomTypeService.getAllRoomTypes();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("001-Deluxe-1", result.get(0).getRoomTypeID());
        assertEquals("Deluxe", result.get(0).getName());
        assertEquals(1000000, result.get(0).getPrice());
        assertEquals("001-Suite-2", result.get(1).getRoomTypeID());
        verify(roomTypeRepository, times(1)).findAll();
        verify(roomService, times(2)).getRoomsByRoomType(any(RoomType.class));
    }

    @Test
    void testGetAllRoomTypes_EmptyList() {
        // Arrange
        when(roomTypeRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<RoomTypeResponseDTO> result = roomTypeService.getAllRoomTypes();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roomTypeRepository, times(1)).findAll();
        verify(roomService, never()).getRoomsByRoomType(any(RoomType.class));
    }

    @Test
    void testGetRoomTypesByProperty_Success() {
        // Arrange
        property.getListRoomType().add(roomType1);
        property.getListRoomType().add(roomType2);
        when(roomService.getRoomsByRoomType(any(RoomType.class)))
                .thenReturn(Arrays.asList(roomResponseDTO));

        // Act
        List<RoomTypeResponseDTO> result = roomTypeService.getRoomTypesByProperty(property);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("001-Deluxe-1", result.get(0).getRoomTypeID());
        assertEquals("Deluxe", result.get(0).getName());
        assertEquals(1, result.get(0).getFloor());
        verify(roomService, times(2)).getRoomsByRoomType(any(RoomType.class));
    }

    @Test
    void testGetRoomTypesByProperty_EmptyList() {
        List<RoomTypeResponseDTO> result = roomTypeService.getRoomTypesByProperty(property);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roomService, never()).getRoomsByRoomType(any(RoomType.class));
    }

    @Test
    void testGetRoomTypesByProperty_WithDateRange_Success() {
        // Arrange
        LocalDateTime checkIn = LocalDateTime.of(2024, 12, 1, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2024, 12, 5, 11, 0);
        
        property.getListRoomType().add(roomType1);
        property.getListRoomType().add(roomType2);
        
        when(roomService.getRoomsByRoomType(any(RoomType.class), eq(checkIn), eq(checkOut)))
                .thenReturn(Arrays.asList(roomResponseDTO));

        // Act
        List<RoomTypeResponseDTO> result = roomTypeService.getRoomTypesByProperty(property, checkIn, checkOut);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("001-Deluxe-1", result.get(0).getRoomTypeID());
        assertEquals("001-Suite-2", result.get(1).getRoomTypeID());
        verify(roomService, times(2)).getRoomsByRoomType(any(RoomType.class), eq(checkIn), eq(checkOut));
    }

    @Test
    void testGetRoomTypesByProperty_WithDateRange_EmptyList() {
        // Arrange
        LocalDateTime checkIn = LocalDateTime.of(2024, 12, 1, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2024, 12, 5, 11, 0);
        
        // Act
        List<RoomTypeResponseDTO> result = roomTypeService.getRoomTypesByProperty(property, checkIn, checkOut);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roomService, never()).getRoomsByRoomType(any(RoomType.class), any(), any());
    }

    @Test
    void testCreateRoomType_Success() {
        // Arrange
        String propertyId = "HOT-1234-001";
        RoomType savedRoomType = RoomType.builder()
                .roomTypeID("001-Deluxe-1")
                .name("Deluxe")
                .price(1000000)
                .description("Deluxe room")
                .capacity(2)
                .facility("WiFi, TV")
                .floor(1)
                .listRoom(new ArrayList<>())
                .build();

        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(savedRoomType);

        // Act
        RoomType result = roomTypeService.createRoomType(addRoomTypeRequest, propertyId);

        // Assert
        assertNotNull(result);
        assertEquals("001-Deluxe-1", result.getRoomTypeID());
        assertEquals("Deluxe", result.getName());
        assertEquals(1000000, result.getPrice());
        assertEquals(2, result.getCapacity());
        assertEquals(1, result.getFloor());
        assertEquals("WiFi, TV", result.getFacility());
        assertNotNull(result.getListRoom());
        assertTrue(result.getListRoom().isEmpty());
        verify(roomTypeRepository, times(1)).save(any(RoomType.class));
    }

    @Test
    void testCreateRoomType_WithDifferentProperty() {
        // Arrange
        String propertyId = "VIL-5678-002";
        AddRoomTypeRequestDTO request = new AddRoomTypeRequestDTO();
        request.setName("Suite");
        request.setPrice(2000000);
        request.setDescription("Luxury suite");
        request.setCapacity(4);
        request.setFacility("WiFi, TV, Jacuzzi");
        request.setFloor(3);

        RoomType savedRoomType = RoomType.builder()
                .roomTypeID("002-Suite-3")
                .name("Suite")
                .price(2000000)
                .description("Luxury suite")
                .capacity(4)
                .facility("WiFi, TV, Jacuzzi")
                .floor(3)
                .listRoom(new ArrayList<>())
                .build();

        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(savedRoomType);

        // Act
        RoomType result = roomTypeService.createRoomType(request, propertyId);

        // Assert
        assertNotNull(result);
        assertEquals("002-Suite-3", result.getRoomTypeID());
        assertEquals("Suite", result.getName());
        assertEquals(3, result.getFloor());
        verify(roomTypeRepository, times(1)).save(any(RoomType.class));
    }

    @Test
    void testCreateRoomType_IDGenerationFormat() {
        // Arrange
        String propertyId = "HOT-9876-123";
        when(roomTypeRepository.save(any(RoomType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RoomType result = roomTypeService.createRoomType(addRoomTypeRequest, propertyId);

        // Assert
        assertNotNull(result);
        // PropertyID format: HOT-9876-123
        // Extract: position 9-12 (0-indexed: 9,10,11) = "123"
        assertTrue(result.getRoomTypeID().startsWith("123-"));
        assertTrue(result.getRoomTypeID().contains("-Deluxe-"));
        assertTrue(result.getRoomTypeID().endsWith("-1"));
        verify(roomTypeRepository, times(1)).save(any(RoomType.class));
    }

    @Test
    void testUpdateRoomType_Success() {
        // Arrange
        roomType1.setPrice(1200000);
        roomType1.setDescription("Updated deluxe room");
        roomType1.setFacility("WiFi, TV, AC, Minibar");

        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(roomType1);

        // Act
        RoomType result = roomTypeService.updateRoomType(roomType1);

        // Assert
        assertNotNull(result);
        assertEquals("001-Deluxe-1", result.getRoomTypeID());
        assertEquals(1200000, result.getPrice());
        assertEquals("Updated deluxe room", result.getDescription());
        assertEquals("WiFi, TV, AC, Minibar", result.getFacility());
        verify(roomTypeRepository, times(1)).save(roomType1);
    }

    @Test
    void testUpdateRoomType_MultipleFields() {
        // Arrange
        roomType2.setPrice(2500000);
        roomType2.setCapacity(6);
        roomType2.setDescription("Premium luxury suite");
        roomType2.setFacility("WiFi, TV, AC, Jacuzzi, Balcony");

        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(roomType2);

        // Act
        RoomType result = roomTypeService.updateRoomType(roomType2);

        // Assert
        assertNotNull(result);
        assertEquals("001-Suite-2", result.getRoomTypeID());
        assertEquals(2500000, result.getPrice());
        assertEquals(6, result.getCapacity());
        assertEquals("Premium luxury suite", result.getDescription());
        assertEquals("WiFi, TV, AC, Jacuzzi, Balcony", result.getFacility());
        verify(roomTypeRepository, times(1)).save(roomType2);
    }

    @Test
    void testMapToRoomTypeDTO_WithRooms() {
        // Arrange
        property.getListRoomType().add(roomType1);
        when(roomService.getRoomsByRoomType(roomType1))
                .thenReturn(Arrays.asList(roomResponseDTO));

        // Act
        List<RoomTypeResponseDTO> result = roomTypeService.getRoomTypesByProperty(property);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        RoomTypeResponseDTO dto = result.get(0);
        assertEquals("001-Deluxe-1", dto.getRoomTypeID());
        assertEquals("Deluxe", dto.getName());
        assertEquals(1000000, dto.getPrice());
        assertEquals("Deluxe room with city view", dto.getDescription());
        assertEquals(2, dto.getCapacity());
        assertEquals("WiFi, TV, AC", dto.getFacility());
        assertEquals(1, dto.getFloor());
        assertNotNull(dto.getListRoom());
        assertEquals(1, dto.getListRoom().size());
    }


    @Test
    void testMapToRoomTypeDTO_WithDateRange_NoAvailableRooms() {
        // Arrange
        LocalDateTime checkIn = LocalDateTime.of(2024, 12, 1, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2024, 12, 5, 11, 0);
        
        property.getListRoomType().add(roomType1);
        
        when(roomService.getRoomsByRoomType(roomType1, checkIn, checkOut))
                .thenReturn(new ArrayList<>());

        // Act
        List<RoomTypeResponseDTO> result = roomTypeService.getRoomTypesByProperty(property, checkIn, checkOut);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        RoomTypeResponseDTO dto = result.get(0);
        assertEquals("001-Deluxe-1", dto.getRoomTypeID());
        assertNotNull(dto.getListRoom());
        assertTrue(dto.getListRoom().isEmpty());
        verify(roomService, times(1)).getRoomsByRoomType(roomType1, checkIn, checkOut);
    }
}