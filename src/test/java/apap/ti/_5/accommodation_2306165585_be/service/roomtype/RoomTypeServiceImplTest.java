package apap.ti._5.accommodation_2306165585_be.service.roomtype;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.Room;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.AddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.AddSingularRoomTypeDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306165585_be.security.RoleGroup;
import apap.ti._5.accommodation_2306165585_be.security.UserContext;
import apap.ti._5.accommodation_2306165585_be.service.property.PropertyService;
import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;

@ExtendWith(MockitoExtension.class)
class RoomTypeServiceImplTest {

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private PropertyService propertyService;

    @Mock
    private UserContext userContext;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomTypeServiceImpl roomTypeService;

    private UUID roomTypeId;
    private UUID propertyId;
    private UUID ownerId;
    private UUID otherOwnerId;
    private RoomType roomType;
    private Property property;
    private AddRoomTypeRequestDTO addRoomTypeRequest;
    private AddSingularRoomTypeDTO addSingularRoomTypeRequest;

    @BeforeEach
    void setUp() {
        roomTypeId = UUID.randomUUID();
        propertyId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        otherOwnerId = UUID.randomUUID();

        property = new Property();
        property.setPropertyID(propertyId);
        property.setOwnerID(ownerId);
        property.setListRoomType(new ArrayList<>());

        roomType = RoomType.builder()
                .roomTypeID(roomTypeId)
                .name("Deluxe Room")
                .price(500000)
                .description("Luxury room with ocean view")
                .capacity(2)
                .facility("WiFi, TV, AC")
                .floor(3)
                .property(property)
                .listRoom(new ArrayList<>())
                .build();

        addRoomTypeRequest = new AddRoomTypeRequestDTO();
        addRoomTypeRequest.setName("Standard Room");
        addRoomTypeRequest.setPrice(300000);
        addRoomTypeRequest.setDescription("Comfortable standard room");
        addRoomTypeRequest.setCapacity(2);
        addRoomTypeRequest.setFacility("WiFi, AC");
        addRoomTypeRequest.setFloor(2);
        addRoomTypeRequest.setUnit(5);

        addSingularRoomTypeRequest = new AddSingularRoomTypeDTO();
        addSingularRoomTypeRequest.setPropertyID(propertyId);
        addSingularRoomTypeRequest.setName("Suite Room");
        addSingularRoomTypeRequest.setPrice(800000);
        addSingularRoomTypeRequest.setDescription("Luxurious suite");
        addSingularRoomTypeRequest.setCapacity(4);
        addSingularRoomTypeRequest.setFacility("WiFi, TV, AC, Bathtub");
        addSingularRoomTypeRequest.setFloor(5);
        addSingularRoomTypeRequest.setUnit(3);
    }

    @Test
    void testGetAllRoomTypes_Success() {
        // Arrange
        List<RoomType> roomTypes = Arrays.asList(roomType);
        when(roomTypeRepository.findAll()).thenReturn(roomTypes);
        when(roomService.getRoomsByRoomType(any(RoomType.class)))
                .thenReturn(new ArrayList<>());

        // Act
        List<RoomTypeResponseDTO> result = roomTypeService.getAllRoomTypes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(roomTypeId, result.get(0).getRoomTypeID());
        assertEquals("Deluxe Room", result.get(0).getName());
        verify(roomTypeRepository, times(1)).findAll();
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
    }

    @Test
    void testGetRoomTypeById_Success_AsSUPERADMIN() {
        // Arrange
        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.of(roomType));
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(roomService.getRoomsByRoomType(any(RoomType.class)))
                .thenReturn(new ArrayList<>());

        // Act
        RoomTypeResponseDTO result = roomTypeService.getRoomTypeById(roomTypeId);

        // Assert
        assertNotNull(result);
        assertEquals(roomTypeId, result.getRoomTypeID());
        assertEquals("Deluxe Room", result.getName());
        verify(roomTypeRepository, times(1)).findById(roomTypeId);
    }

    @Test
    void testGetRoomTypeById_Success_AsOwner() {
        // Arrange
        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.of(roomType));
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(ownerId);
        when(roomService.getRoomsByRoomType(any(RoomType.class)))
                .thenReturn(new ArrayList<>());

        // Act
        RoomTypeResponseDTO result = roomTypeService.getRoomTypeById(roomTypeId);

        // Assert
        assertNotNull(result);
        assertEquals(roomTypeId, result.getRoomTypeID());
        verify(roomTypeRepository, times(1)).findById(roomTypeId);
    }

    @Test
    void testGetRoomTypeById_Unauthorized_DifferentOwner() {
        // Arrange
        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.of(roomType));
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(otherOwnerId);

        // Act & Assert
        assertThrows(SecurityException.class, 
                () -> roomTypeService.getRoomTypeById(roomTypeId));
        verify(roomTypeRepository, times(1)).findById(roomTypeId);
    }

    @Test
    void testGetRoomTypeById_NotFound() {
        // Arrange
        when(roomTypeRepository.findById(roomTypeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, 
                () -> roomTypeService.getRoomTypeById(roomTypeId));
        verify(roomTypeRepository, times(1)).findById(roomTypeId);
    }

    @Test
    void testGetRoomTypesByProperty_Success_AsSUPERADMIN() {
        // Arrange
        property.getListRoomType().add(roomType);
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(roomService.getRoomsByRoomType(any(RoomType.class)))
                .thenReturn(new ArrayList<>());

        // Act
        List<RoomTypeResponseDTO> result = roomTypeService.getRoomTypesByProperty(property);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(roomTypeId, result.get(0).getRoomTypeID());
    }

    @Test
    void testGetRoomTypesByProperty_Success_AsOwner() {
        // Arrange
        property.getListRoomType().add(roomType);
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(ownerId);
        when(roomService.getRoomsByRoomType(any(RoomType.class)))
                .thenReturn(new ArrayList<>());

        // Act
        List<RoomTypeResponseDTO> result = roomTypeService.getRoomTypesByProperty(property);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetRoomTypesByProperty_Unauthorized() {
        // Arrange
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(otherOwnerId);

        // Act & Assert
        assertThrows(SecurityException.class, 
                () -> roomTypeService.getRoomTypesByProperty(property));
    }

    @Test
    void testGetRoomTypesByPropertyWithDates_Success() {
        // Arrange
        property.getListRoomType().add(roomType);
        LocalDateTime checkIn = LocalDateTime.now().plusDays(1);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(3);
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(roomService.getRoomsByRoomType(any(RoomType.class), 
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        // Act
        List<RoomTypeResponseDTO> result = roomTypeService
                .getRoomTypesByProperty(property, checkIn, checkOut);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roomService, times(1))
                .getRoomsByRoomType(any(RoomType.class), eq(checkIn), eq(checkOut));
    }

    @Test
    void testGetRoomTypesByPropertyWithDates_Unauthorized() {
        // Arrange
        LocalDateTime checkIn = LocalDateTime.now().plusDays(1);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(3);
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(otherOwnerId);

        // Act & Assert
        assertThrows(SecurityException.class, 
                () -> roomTypeService.getRoomTypesByProperty(property, checkIn, checkOut));
    }

    @Test
    void testCreateRoomType_Success_AsSUPERADMIN() {
        // Arrange
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(roomTypeRepository.save(any(RoomType.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        RoomType result = roomTypeService.createRoomType(addRoomTypeRequest, property);

        // Assert
        assertNotNull(result);
        assertEquals("Standard Room", result.getName());
        assertEquals(300000, result.getPrice());
        assertEquals(property, result.getProperty());
        verify(roomTypeRepository, times(1)).save(any(RoomType.class));
    }

    @Test
    void testCreateRoomType_Success_AsOwner() {
        // Arrange
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(ownerId);
        when(roomTypeRepository.save(any(RoomType.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        RoomType result = roomTypeService.createRoomType(addRoomTypeRequest, property);

        // Assert
        assertNotNull(result);
        assertEquals("Standard Room", result.getName());
        verify(roomTypeRepository, times(1)).save(any(RoomType.class));
    }

    @Test
    void testCreateRoomType_Unauthorized() {
        // Arrange
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(otherOwnerId);

        // Act & Assert
        assertThrows(SecurityException.class, 
                () -> roomTypeService.createRoomType(addRoomTypeRequest, property));
        verify(roomTypeRepository, never()).save(any(RoomType.class));
    }

    @Test
    void testCreateRoomTypeSingular_Success() {
        // Arrange
        when(propertyService.getRawPropertyById(propertyId)).thenReturn(property);
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(ownerId);
        when(roomTypeRepository.save(any(RoomType.class))).thenAnswer(i -> i.getArguments()[0]);
        when(roomService.getRoomsByRoomType(any(RoomType.class)))
                .thenReturn(new ArrayList<>());

        // Act
        RoomTypeResponseDTO result = roomTypeService
                .createRoomType(addSingularRoomTypeRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Suite Room", result.getName());
        assertEquals(800000, result.getPrice());
        verify(propertyService, times(1)).getRawPropertyById(propertyId);
        verify(roomTypeRepository, times(1)).save(any(RoomType.class));
    }

    @Test
    void testCreateRoomTypeSingular_Unauthorized() {
        // Arrange
        when(propertyService.getRawPropertyById(propertyId)).thenReturn(property);
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(otherOwnerId);

        // Act & Assert
        assertThrows(SecurityException.class, 
                () -> roomTypeService.createRoomType(addSingularRoomTypeRequest));
        verify(roomTypeRepository, never()).save(any(RoomType.class));
    }

    @Test
    void testUpdateRoomType_Success() {
        // Arrange
        when(roomTypeRepository.save(roomType)).thenReturn(roomType);

        // Act
        RoomType result = roomTypeService.updateRoomType(roomType);

        // Assert
        assertNotNull(result);
        assertEquals(roomType, result);
        verify(roomTypeRepository, times(1)).save(roomType);
    }

    @Test
    void testUpdateRoomType_ModifiedData() {
        // Arrange
        roomType.setPrice(600000);
        roomType.setDescription("Updated description");
        when(roomTypeRepository.save(roomType)).thenReturn(roomType);

        // Act
        RoomType result = roomTypeService.updateRoomType(roomType);

        // Assert
        assertNotNull(result);
        assertEquals(600000, result.getPrice());
        assertEquals("Updated description", result.getDescription());
        verify(roomTypeRepository, times(1)).save(roomType);
    }

    @Test
    void testGetAllRoomTypes_MultipleRoomTypes() {
        // Arrange
        RoomType roomType2 = RoomType.builder()
                .roomTypeID(UUID.randomUUID())
                .name("Economy Room")
                .price(200000)
                .description("Budget room")
                .capacity(1)
                .facility("WiFi")
                .floor(1)
                .property(property)
                .listRoom(new ArrayList<>())
                .build();

        List<RoomType> roomTypes = Arrays.asList(roomType, roomType2);
        when(roomTypeRepository.findAll()).thenReturn(roomTypes);
        when(roomService.getRoomsByRoomType(any(RoomType.class)))
                .thenReturn(new ArrayList<>());

        // Act
        List<RoomTypeResponseDTO> result = roomTypeService.getAllRoomTypes();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getName().equals("Deluxe Room")));
        assertTrue(result.stream().anyMatch(r -> r.getName().equals("Economy Room")));
    }
}