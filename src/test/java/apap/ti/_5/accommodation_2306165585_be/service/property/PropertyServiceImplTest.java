package apap.ti._5.accommodation_2306165585_be.service.property;

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
import apap.ti._5.accommodation_2306165585_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.Room;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.AddPropertyRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.PropertyTransactionRequest;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.UpdatePropertyTransactionRequest;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.AddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.ListAddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.AllPropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.statistics.IncomeStatisticsDTO;
import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;
import apap.ti._5.accommodation_2306165585_be.service.roomtype.RoomTypeService;

@ExtendWith(MockitoExtension.class)
public class PropertyServiceImplTest {  // Changed class name

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private RoomTypeService roomTypeService;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private PropertyServiceImpl propertyService;  // Changed to PropertyServiceImpl

    private Property property1;
    private Property property2;
    private RoomType roomType1;
    private Room room1;
    private UUID ownerId;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        now = LocalDateTime.now();

        property1 = Property.builder()
                .propertyID("HOT-1234-001")
                .propertyName("Hotel Paradise")
                .type(1)
                .address("123 Main St")
                .province(1)
                .description("A beautiful hotel")
                .totalRoom(10)
                .income(0)
                .activeStatus(1)
                .ownerID(ownerId)
                .ownerName("John Doe")
                .createdDate(now)
                .updatedDate(now)
                .listRoomType(new ArrayList<>())
                .build();

        property2 = Property.builder()
                .propertyID("VIL-5678-002")
                .propertyName("Beach Villa")
                .type(2)
                .address("456 Beach Rd")
                .province(2)
                .description("A luxury villa")
                .totalRoom(5)
                .income(0)
                .activeStatus(1)
                .ownerID(ownerId)
                .ownerName("Jane Smith")
                .createdDate(now)
                .updatedDate(now)
                .listRoomType(new ArrayList<>())
                .build();

        roomType1 = new RoomType();
        roomType1.setRoomTypeID("RT-001");
        roomType1.setName("Deluxe");
        roomType1.setFloor(1);
        roomType1.setPrice(1000000);
        roomType1.setCapacity(2);
        roomType1.setListRoom(new ArrayList<>());

        room1 = new Room();
        room1.setRoomID("ROOM-001");
        room1.setActiveRoom(1);
        room1.setListAccommodationBooking(new ArrayList<>());
    }

    @Test
    void testGetAllProperties_Success() {
        // Arrange
        List<Property> properties = Arrays.asList(property1, property2);
        when(propertyRepository.findAll()).thenReturn(properties);

        // Act
        List<AllPropertyResponseDTO> result = propertyService.getAllProperties();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("HOT-1234-001", result.get(0).getPropertyID());
        assertEquals("Hotel Paradise", result.get(0).getPropertyName());
        assertEquals(1, result.get(0).getType());
        assertEquals(10, result.get(0).getTotalRooms());
        verify(propertyRepository, times(1)).findAll();
    }

    @Test
    void testGetAllProperties_EmptyList() {
        // Arrange
        when(propertyRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<AllPropertyResponseDTO> result = propertyService.getAllProperties();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(propertyRepository, times(1)).findAll();
    }

    @Test
    void testGetAllActiveProperties_Success() {
        // Arrange
        List<Property> activeProperties = Arrays.asList(property1);
        when(propertyRepository.findAllActive()).thenReturn(activeProperties);

        // Act
        List<AllPropertyResponseDTO> result = propertyService.getAllActiveProperties();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("HOT-1234-001", result.get(0).getPropertyID());
        assertEquals(1, result.get(0).getActiveStatus());
        verify(propertyRepository, times(1)).findAllActive();
    }

    @Test
    void testGetPropertyById_Success() {
        // Arrange
        String propertyId = "HOT-1234-001";
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property1));
        when(roomTypeService.getRoomTypesByProperty(property1)).thenReturn(new ArrayList<>());

        // Act
        PropertyResponseDTO result = propertyService.getPropertyById(propertyId);

        // Assert
        assertNotNull(result);
        assertEquals(propertyId, result.getPropertyID());
        assertEquals("Hotel Paradise", result.getPropertyName());
        assertEquals(1, result.getType());
        verify(propertyRepository, times(1)).findById(propertyId);
    }

    @Test
    void testGetPropertyById_NotFound() {
        // Arrange
        String propertyId = "NON-EXISTENT-001";
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            propertyService.getPropertyById(propertyId);
        });
        verify(propertyRepository, times(1)).findById(propertyId);
    }

    @Test
    void testGetPropertyById_WithDateRange_Success() {
        // Arrange
        String propertyId = "HOT-1234-001";
        LocalDateTime checkIn = LocalDateTime.now().plusDays(1);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(3);
        
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property1));
        when(roomTypeService.getRoomTypesByProperty(property1, checkIn, checkOut))
                .thenReturn(new ArrayList<>());

        // Act
        PropertyResponseDTO result = propertyService.getPropertyById(propertyId, checkIn, checkOut);

        // Assert
        assertNotNull(result);
        assertEquals(propertyId, result.getPropertyID());
        verify(propertyRepository, times(1)).findById(propertyId);
        verify(roomTypeService, times(1)).getRoomTypesByProperty(property1, checkIn, checkOut);
    }

    @Test
    void testGetPropertyById_WithDateRange_InvalidDates() {
        // Arrange
        String propertyId = "HOT-1234-001";
        LocalDateTime checkIn = LocalDateTime.now().plusDays(3);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(1);
        
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property1));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            propertyService.getPropertyById(propertyId, checkIn, checkOut);
        });
        verify(propertyRepository, times(1)).findById(propertyId);
    }

    @Test
    void testCreateProperty_Success() {
        // Arrange
        AddPropertyRequestDTO request = new AddPropertyRequestDTO();
        request.setPropertyName("New Hotel");
        request.setType(1);
        request.setAddress("789 New St");
        request.setProvince(1);
        request.setDescription("A new hotel");
        request.setOwnerId(ownerId);
        request.setOwnerName("New Owner");

        when(propertyRepository.count()).thenReturn(0L);
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> {
            Property saved = invocation.getArgument(0);
            saved.setCreatedDate(now);
            saved.setUpdatedDate(now);
            return saved;
        });

        // Act
        Property result = propertyService.createProperty(request);

        // Assert
        assertNotNull(result);
        assertEquals("New Hotel", result.getPropertyName());
        assertTrue(result.getPropertyID().startsWith("HOT-"));
        assertEquals(0, result.getIncome());
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void testCreatePropertyTransaction_Success() {
        // Arrange
        AddPropertyRequestDTO propertyRequest = new AddPropertyRequestDTO();
        propertyRequest.setPropertyName("New Hotel");
        propertyRequest.setType(1);
        propertyRequest.setAddress("789 New St");
        propertyRequest.setProvince(1);
        propertyRequest.setDescription("A new hotel");
        propertyRequest.setOwnerId(ownerId);
        propertyRequest.setOwnerName("New Owner");

        AddRoomTypeRequestDTO roomTypeRequest = new AddRoomTypeRequestDTO();
        roomTypeRequest.setName("Deluxe");
        roomTypeRequest.setFloor(1);
        roomTypeRequest.setUnit(5);
        roomTypeRequest.setPrice(1000000);
        roomTypeRequest.setCapacity(2);

        PropertyTransactionRequest request = new PropertyTransactionRequest();
        request.setProperty(propertyRequest);
        request.setRoomTypes(Arrays.asList(roomTypeRequest));

        Property newProperty = Property.builder()
                .propertyID("HOT-1234-001")
                .propertyName("New Hotel")
                .type(1)
                .listRoomType(new ArrayList<>())
                .build();

        when(propertyRepository.count()).thenReturn(0L);
        when(propertyRepository.save(any(Property.class))).thenReturn(newProperty);
        when(roomTypeService.createRoomType(any(AddRoomTypeRequestDTO.class), anyString()))
                .thenReturn(roomType1);
        when(roomService.createRoom(any(Property.class), any(RoomType.class)))
                .thenReturn(room1);
        when(roomTypeService.getRoomTypesByProperty(any(Property.class)))
                .thenReturn(new ArrayList<>());

        // Act
        PropertyResponseDTO result = propertyService.createPropertyTransaction(request);

        // Assert
        assertNotNull(result);
        assertEquals("HOT-1234-001", result.getPropertyID());
        verify(roomTypeService, times(1)).createRoomType(any(AddRoomTypeRequestDTO.class), anyString());
        verify(roomService, times(5)).createRoom(any(Property.class), any(RoomType.class));
    }

    @Test
    void testUpdatePropertyTransaction_Success() {
        // Arrange
        UpdatePropertyRequestDTO propertyRequest = new UpdatePropertyRequestDTO();
        propertyRequest.setPropertyId("HOT-1234-001");
        propertyRequest.setPropertyName("Updated Hotel");
        propertyRequest.setAddress("Updated Address");
        propertyRequest.setDescription("Updated Description");

        UpdateRoomTypeRequestDTO roomTypeRequest = new UpdateRoomTypeRequestDTO();
        roomTypeRequest.setRoomTypeID("RT-001");
        roomTypeRequest.setFacility("WiFi, TV");
        roomTypeRequest.setPrice(1200000);
        roomTypeRequest.setDescription("Updated room");
        roomTypeRequest.setCapacity(2);

        UpdatePropertyTransactionRequest request = new UpdatePropertyTransactionRequest();
        request.setProperty(propertyRequest);
        request.setRoomTypes(Arrays.asList(roomTypeRequest));

        property1.getListRoomType().add(roomType1);

        when(propertyRepository.findByIdActive("HOT-1234-001")).thenReturn(Optional.of(property1));
        when(propertyRepository.save(any(Property.class))).thenReturn(property1);
        when(roomTypeService.getRoomTypesByProperty(any(Property.class)))
                .thenReturn(new ArrayList<>());

        // Act
        PropertyResponseDTO result = propertyService.updatePropertyTransaction(request);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Hotel", property1.getPropertyName());
        assertEquals("Updated Address", property1.getAddress());
        verify(propertyRepository, times(1)).save(any(Property.class));
        verify(roomTypeService, times(1)).updateRoomType(any(RoomType.class));
    }

    @Test
    void testUpdatePropertyTransaction_PropertyNotFound() {
        // Arrange
        UpdatePropertyRequestDTO propertyRequest = new UpdatePropertyRequestDTO();
        propertyRequest.setPropertyId("NON-EXISTENT");

        UpdatePropertyTransactionRequest request = new UpdatePropertyTransactionRequest();
        request.setProperty(propertyRequest);
        request.setRoomTypes(new ArrayList<>());

        when(propertyRepository.findByIdActive("NON-EXISTENT")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            propertyService.updatePropertyTransaction(request);
        });
    }

    @Test
    void testUpdatePropertyTransaction_RoomTypeMismatch() {
        // Arrange
        UpdatePropertyRequestDTO propertyRequest = new UpdatePropertyRequestDTO();
        propertyRequest.setPropertyId("HOT-1234-001");

        UpdateRoomTypeRequestDTO roomTypeRequest = new UpdateRoomTypeRequestDTO();
        roomTypeRequest.setRoomTypeID("RT-999"); // Non-existent room type

        UpdatePropertyTransactionRequest request = new UpdatePropertyTransactionRequest();
        request.setProperty(propertyRequest);
        request.setRoomTypes(Arrays.asList(roomTypeRequest));

        property1.getListRoomType().add(roomType1);

        when(propertyRepository.findByIdActive("HOT-1234-001")).thenReturn(Optional.of(property1));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            propertyService.updatePropertyTransaction(request);
        });
    }

    @Test
    void testAddRoomTypeToProperty_Success() {
        // Arrange
        String propertyId = "HOT-1234-001";
        AddRoomTypeRequestDTO roomTypeRequest = new AddRoomTypeRequestDTO();
        roomTypeRequest.setName("Suite");
        roomTypeRequest.setFloor(2);
        roomTypeRequest.setUnit(3);
        roomTypeRequest.setPrice(2000000);
        roomTypeRequest.setCapacity(4);

        ListAddRoomTypeRequestDTO request = new ListAddRoomTypeRequestDTO();
        request.setRoomTypes(Arrays.asList(roomTypeRequest));

        RoomType newRoomType = new RoomType();
        newRoomType.setRoomTypeID("RT-002");
        newRoomType.setName("Suite");
        newRoomType.setFloor(2);
        newRoomType.setListRoom(new ArrayList<>());

        when(propertyRepository.findByIdActive(propertyId)).thenReturn(Optional.of(property1));
        when(roomTypeService.createRoomType(any(AddRoomTypeRequestDTO.class), anyString()))
                .thenReturn(newRoomType);
        when(roomService.createRoom(any(Property.class), any(RoomType.class)))
                .thenReturn(room1);
        when(propertyRepository.save(any(Property.class))).thenReturn(property1);
        when(roomTypeService.getRoomTypesByProperty(any(Property.class)))
                .thenReturn(new ArrayList<>());

        // Act
        PropertyResponseDTO result = propertyService.addRoomTypeToProperty(propertyId, request);

        // Assert
        assertNotNull(result);
        assertEquals(13, property1.getTotalRoom()); // 10 + 3
        verify(roomService, times(3)).createRoom(any(Property.class), any(RoomType.class));
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void testAddRoomTypeToProperty_DuplicateRoomType() {
        // Arrange
        String propertyId = "HOT-1234-001";
        property1.getListRoomType().add(roomType1);

        AddRoomTypeRequestDTO roomTypeRequest = new AddRoomTypeRequestDTO();
        roomTypeRequest.setName("Deluxe"); // Same name as existing
        roomTypeRequest.setFloor(1); // Same floor as existing
        roomTypeRequest.setUnit(2);

        ListAddRoomTypeRequestDTO request = new ListAddRoomTypeRequestDTO();
        request.setRoomTypes(Arrays.asList(roomTypeRequest));

        when(propertyRepository.findByIdActive(propertyId)).thenReturn(Optional.of(property1));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            propertyService.addRoomTypeToProperty(propertyId, request);
        });
    }

    @Test
    void testDeleteProperty_Success() {
        // Arrange
        String propertyId = "HOT-1234-001";
        property1.getListRoomType().add(roomType1);
        roomType1.getListRoom().add(room1);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property1));
        when(propertyRepository.save(any(Property.class))).thenReturn(property1);

        // Act
        propertyService.deleteProperty(propertyId);

        // Assert
        assertEquals(0, property1.getActiveStatus());
        verify(roomService, times(1)).deleteRoom(anyString());
        verify(propertyRepository, times(1)).save(property1);
    }

    @Test
    void testDeleteProperty_WithFutureBookings() {
        // Arrange
        String propertyId = "HOT-1234-001";
        property1.getListRoomType().add(roomType1);
        roomType1.getListRoom().add(room1);

        AccommodationBooking futureBooking = new AccommodationBooking();
        futureBooking.setCheckOutDate(LocalDateTime.now().plusDays(5));
        room1.getListAccommodationBooking().add(futureBooking);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property1));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            propertyService.deleteProperty(propertyId);
        });
    }

    @Test
    void testDeleteProperty_NotFound() {
        // Arrange
        String propertyId = "NON-EXISTENT";
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            propertyService.deleteProperty(propertyId);
        });
    }

    @Test
    void testGetIncomeStatistics_Success() {
        // Arrange
        int month = 11;
        int year = 2024;

        property1.getListRoomType().add(roomType1);
        roomType1.getListRoom().add(room1);

        AccommodationBooking booking = new AccommodationBooking();
        booking.setCheckInDate(LocalDateTime.of(2024, 11, 15, 14, 0));
        booking.setTotalPrice(2000000);
        room1.getListAccommodationBooking().add(booking);

        when(propertyRepository.findAll()).thenReturn(Arrays.asList(property1, property2));

        // Act
        IncomeStatisticsDTO result = propertyService.getIncomeStatistics(month, year);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getPropertyNames().size());
        assertEquals("Hotel Paradise", result.getPropertyNames().get(0));
        assertEquals(2000000, result.getPropertyIncomes().get(0));
        assertEquals(2000000, result.getTotalIncome());
    }

    @Test
    void testGetIncomeStatistics_NoBookings() {
        // Arrange
        int month = 12;
        int year = 2024;

        when(propertyRepository.findAll()).thenReturn(Arrays.asList(property1, property2));

        // Act
        IncomeStatisticsDTO result = propertyService.getIncomeStatistics(month, year);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getPropertyNames().size());
        assertEquals(0, result.getPropertyIncomes().get(0));
        assertEquals(0, result.getTotalIncome());
    }
}