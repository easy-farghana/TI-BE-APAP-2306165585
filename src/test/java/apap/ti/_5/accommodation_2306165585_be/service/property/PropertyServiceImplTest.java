package apap.ti._5.accommodation_2306165585_be.service.property;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.exception.SecurityException;
import apap.ti._5.accommodation_2306165585_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.Room;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.*;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.*;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.AllPropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.statistics.IncomeStatisticsDTO;
import apap.ti._5.accommodation_2306165585_be.security.RoleGroup;
import apap.ti._5.accommodation_2306165585_be.security.UserContext;
import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;
import apap.ti._5.accommodation_2306165585_be.service.roomtype.RoomTypeService;
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
class PropertyServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private RoomTypeService roomTypeService;

    @Mock
    private RoomService roomService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    private UUID propertyId;
    private UUID ownerId;
    private Property testProperty;
    private RoomType testRoomType;
    private Room testRoom;

    @BeforeEach
    void setUp() {
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
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        testRoomType = RoomType.builder()
                .roomTypeID(UUID.randomUUID())
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
                .roomID(UUID.randomUUID())
                .roomType(testRoomType)
                .listAccommodationBooking(new ArrayList<>())
                .availabilityStatus(1)
                .build();

        testRoomType.getListRoom().add(testRoom);
        testProperty.getListRoomType().add(testRoomType);
    }

    // Test getAllProperties with filters
    @Test
    void testGetAllPropertiesWithFilters_AsAccommodationOwner() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Test");
        params.put("type", 1);
        params.put("province", 1);

        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(ownerId);
        when(propertyRepository.findByFilters("Test", 1, 1, ownerId))
                .thenReturn(Collections.singletonList(testProperty));

        List<AllPropertyResponseDTO> result = propertyService.getAllProperties(params);

        assertEquals(1, result.size());
        assertEquals(propertyId, result.get(0).getPropertyID());
        verify(propertyRepository).findByFilters("Test", 1, 1, ownerId);
    }

    @Test
    void testGetAllPropertiesWithFilters_AsSUPERADMIN() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Test");

        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(propertyRepository.findByFilters("Test", null, null, null))
                .thenReturn(Collections.singletonList(testProperty));

        List<AllPropertyResponseDTO> result = propertyService.getAllProperties(params);

        assertEquals(1, result.size());
        verify(propertyRepository).findByFilters("Test", null, null, null);
    }

    // Test getAllProperties without filters
    @Test
    void testGetAllPropertiesWithoutFilters() {
        when(propertyRepository.findAll()).thenReturn(Collections.singletonList(testProperty));

        List<AllPropertyResponseDTO> result = propertyService.getAllProperties();

        assertEquals(1, result.size());
        assertEquals(propertyId, result.get(0).getPropertyID());
        verify(propertyRepository).findAll();
    }

    // Test getAllActiveProperties
    @Test
    void testGetAllActiveProperties() {
        when(propertyRepository.findAllActive()).thenReturn(Collections.singletonList(testProperty));

        List<AllPropertyResponseDTO> result = propertyService.getAllActiveProperties();

        assertEquals(1, result.size());
        verify(propertyRepository).findAllActive();
    }

    @Test
    void testGetAllActivePropertiesWithFilters() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Test");

        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(propertyRepository.findByFiltersAndActive("Test", null, null, null))
                .thenReturn(Collections.singletonList(testProperty));

        List<AllPropertyResponseDTO> result = propertyService.getAllActiveProperties(params);

        assertEquals(1, result.size());
        verify(propertyRepository).findByFiltersAndActive("Test", null, null, null);
    }

    // Test getPropertyById
    @Test
    void testGetPropertyById_Success() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(roomTypeService.getRoomTypesByProperty(testProperty))
                .thenReturn(Collections.singletonList(new RoomTypeResponseDTO()));

        PropertyResponseDTO result = propertyService.getPropertyById(propertyId);

        assertNotNull(result);
        assertEquals(propertyId, result.getPropertyID());
        verify(propertyRepository).findById(propertyId);
    }

    @Test
    void testGetPropertyById_NotFound() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> propertyService.getPropertyById(propertyId));
    }

    @Test
    void testGetPropertyById_AccommodationOwnerUnauthorized() {
        UUID otherOwnerId = UUID.randomUUID();
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(otherOwnerId);

        assertThrows(NotFoundException.class, () -> propertyService.getPropertyById(propertyId));
    }

    @Test
    void testGetPropertyById_AccommodationOwnerAuthorized() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(ownerId);
        when(roomTypeService.getRoomTypesByProperty(testProperty))
                .thenReturn(Collections.singletonList(new RoomTypeResponseDTO()));

        PropertyResponseDTO result = propertyService.getPropertyById(propertyId);

        assertNotNull(result);
        assertEquals(propertyId, result.getPropertyID());
    }

    // Test getRawPropertyById
    @Test
    void testGetRawPropertyById_Success() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);

        Property result = propertyService.getRawPropertyById(propertyId);

        assertNotNull(result);
        assertEquals(propertyId, result.getPropertyID());
    }

    @Test
    void testGetRawPropertyById_NotFound() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> propertyService.getRawPropertyById(propertyId));
    }

    // Test getPropertyById with dates
    @Test
    void testGetPropertyByIdWithDates_Success() {
        LocalDateTime checkIn = LocalDateTime.now();
        LocalDateTime checkOut = checkIn.plusDays(2);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(roomTypeService.getRoomTypesByProperty(testProperty, checkIn, checkOut))
                .thenReturn(Collections.singletonList(new RoomTypeResponseDTO()));

        PropertyResponseDTO result = propertyService.getPropertyById(propertyId, checkIn, checkOut);

        assertNotNull(result);
        assertEquals(propertyId, result.getPropertyID());
    }

    @Test
    void testGetPropertyByIdWithDates_InvalidDates() {
        LocalDateTime checkIn = LocalDateTime.now().plusDays(2);
        LocalDateTime checkOut = LocalDateTime.now();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));

        assertThrows(IllegalArgumentException.class, 
                () -> propertyService.getPropertyById(propertyId, checkIn, checkOut));
    }

    // Test getIncomeStatistics
    @Test
    void testGetIncomeStatistics() {
        AccommodationBooking booking = AccommodationBooking.builder()
                .bookingID(UUID.randomUUID())
                .checkInDate(LocalDateTime.of(2024, 1, 15, 14, 0))
                .checkOutDate(LocalDateTime.of(2024, 1, 17, 12, 0))
                .totalPrice(500000)
                .status(1) // Active booking
                .room(testRoom)
                .build();

        testRoom.getListAccommodationBooking().add(booking);

        when(propertyRepository.findAllActive()).thenReturn(Collections.singletonList(testProperty));

        IncomeStatisticsDTO result = propertyService.getIncomeStatistics(1, 2024);

        assertNotNull(result);
        assertNotNull(result.getPropertyStatistics());
        assertEquals(1, result.getPropertyStatistics().size());
        assertEquals("Test Hotel", result.getPropertyStatistics().get(0).getPropertyName());
        assertEquals(500000, result.getPropertyStatistics().get(0).getPropertyIncomes());
        assertEquals(500000, result.getTotalIncome());
    }

    @Test
    void testGetIncomeStatistics_NoBookingsInMonth() {
        when(propertyRepository.findAllActive()).thenReturn(Collections.singletonList(testProperty));

        IncomeStatisticsDTO result = propertyService.getIncomeStatistics(12, 2024);

        assertNotNull(result);
        assertNotNull(result.getPropertyStatistics());
        assertEquals(1, result.getPropertyStatistics().size());
        assertEquals("Test Hotel", result.getPropertyStatistics().get(0).getPropertyName());
        assertEquals(0, result.getPropertyStatistics().get(0).getPropertyIncomes());
        assertEquals(0, result.getTotalIncome());
    }

    @Test
    void testGetIncomeStatistics_MultipleProperties() {
        // Setup second property with booking
        RoomType roomType2 = RoomType.builder()
                .roomTypeID(UUID.randomUUID())
                .name("Standard")
                .listRoom(new ArrayList<>())
                .build();

        Room room2 = Room.builder()
                .roomID(UUID.randomUUID())
                .roomType(roomType2)
                .listAccommodationBooking(new ArrayList<>())
                .availabilityStatus(1)
                .build();

        AccommodationBooking booking2 = AccommodationBooking.builder()
                .bookingID(UUID.randomUUID())
                .checkInDate(LocalDateTime.of(2024, 1, 20, 14, 0))
                .checkOutDate(LocalDateTime.of(2024, 1, 22, 12, 0))
                .totalPrice(300000)
                .status(1)
                .room(room2)
                .build();

        room2.getListAccommodationBooking().add(booking2);
        roomType2.getListRoom().add(room2);

        Property property2 = Property.builder()
                .propertyID(UUID.randomUUID())
                .propertyName("Hotel 2")
                .activeStatus(1)
                .listRoomType(Arrays.asList(roomType2))
                .build();

        roomType2.setProperty(property2);

        // Add booking to first property
        AccommodationBooking booking1 = AccommodationBooking.builder()
                .bookingID(UUID.randomUUID())
                .checkInDate(LocalDateTime.of(2024, 1, 10, 14, 0))
                .checkOutDate(LocalDateTime.of(2024, 1, 12, 12, 0))
                .totalPrice(400000)
                .status(1)
                .room(testRoom)
                .build();

        testRoom.getListAccommodationBooking().add(booking1);

        when(propertyRepository.findAllActive()).thenReturn(Arrays.asList(testProperty, property2));

        IncomeStatisticsDTO result = propertyService.getIncomeStatistics(1, 2024);

        assertNotNull(result);
        assertNotNull(result.getPropertyStatistics());
        assertEquals(2, result.getPropertyStatistics().size());
        
        // Verify first property
        assertEquals("Test Hotel", result.getPropertyStatistics().get(0).getPropertyName());
        assertEquals(400000, result.getPropertyStatistics().get(0).getPropertyIncomes());
        
        // Verify second property
        assertEquals("Hotel 2", result.getPropertyStatistics().get(1).getPropertyName());
        assertEquals(300000, result.getPropertyStatistics().get(1).getPropertyIncomes());
        
        // Verify total
        assertEquals(700000, result.getTotalIncome());
    }

    // Test createPropertyTransaction
    @Test
    void testCreatePropertyTransaction_Success() {
        AddPropertyRequestDTO propertyRequest = new AddPropertyRequestDTO();
        propertyRequest.setOwnerId(UUID.randomUUID());
        propertyRequest.setOwnerName("Wahono");
        propertyRequest.setPropertyName("New Hotel");
        propertyRequest.setType(1);
        propertyRequest.setProvince(1);
        propertyRequest.setAddress("New Address");
        propertyRequest.setDescription("New Description");

        AddRoomTypeRequestDTO roomTypeRequest = new AddRoomTypeRequestDTO();
        roomTypeRequest.setName("Standard");
        roomTypeRequest.setFloor(1);
        roomTypeRequest.setUnit(5);
        roomTypeRequest.setCapacity(2);
        roomTypeRequest.setPrice(100000);

        PropertyTransactionRequest request = new PropertyTransactionRequest();
        request.setProperty(propertyRequest);
        request.setRoomTypes(Collections.singletonList(roomTypeRequest));

        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);
        when(roomTypeService.createRoomType(any(), any())).thenReturn(testRoomType);
        when(roomService.createRoom(any(), any())).thenReturn(testRoom);
        when(roomTypeService.getRoomTypesByProperty(any()))
                .thenReturn(Collections.singletonList(new RoomTypeResponseDTO()));

        PropertyResponseDTO result = propertyService.createPropertyTransaction(request);

        assertNotNull(result);
        verify(propertyRepository, atLeastOnce()).save(any(Property.class));
        verify(roomTypeService).createRoomType(any(), any());
        verify(roomService, times(5)).createRoom(any(), any());
    }

    @Test
    void testCreatePropertyTransaction_DuplicateRoomType() {
        AddPropertyRequestDTO propertyRequest = new AddPropertyRequestDTO();
        propertyRequest.setPropertyName("New Hotel");
        propertyRequest.setType(1);
        propertyRequest.setProvince(1);
        propertyRequest.setAddress("Address");
        propertyRequest.setDescription("Description");

        AddRoomTypeRequestDTO roomType1 = new AddRoomTypeRequestDTO();
        roomType1.setName("Standard");
        roomType1.setFloor(1);
        roomType1.setUnit(5);

        AddRoomTypeRequestDTO roomType2 = new AddRoomTypeRequestDTO();
        roomType2.setName("Standard");
        roomType2.setFloor(1);
        roomType2.setUnit(3);

        PropertyTransactionRequest request = new PropertyTransactionRequest();
        request.setProperty(propertyRequest);
        request.setRoomTypes(Arrays.asList(roomType1, roomType2));

        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        assertThrows(IllegalArgumentException.class, 
                () -> propertyService.createPropertyTransaction(request));
    }

    // Test updatePropertyTransaction
    @Test
    void testUpdatePropertyTransaction_Success() {
        UpdatePropertyRequestDTO propertyRequest = new UpdatePropertyRequestDTO();
        propertyRequest.setPropertyId(propertyId);
        propertyRequest.setPropertyName("Updated Hotel");
        propertyRequest.setType(1);
        propertyRequest.setProvince(1);
        propertyRequest.setAddress("Updated Address");
        propertyRequest.setDescription("Updated Description");

        UpdateRoomTypeRequestDTO roomTypeRequest = new UpdateRoomTypeRequestDTO();
        roomTypeRequest.setRoomTypeID(testRoomType.getRoomTypeID());
        roomTypeRequest.setFacility("WiFi");
        roomTypeRequest.setPrice(150000);
        roomTypeRequest.setDescription("Updated Room");
        roomTypeRequest.setCapacity(2);

        UpdatePropertyTransactionRequest request = new UpdatePropertyTransactionRequest();
        request.setProperty(propertyRequest);
        request.setRoomTypes(Collections.singletonList(roomTypeRequest));

        when(propertyRepository.findByIdActive(propertyId)).thenReturn(Optional.of(testProperty));
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);
        when(roomTypeService.getRoomTypesByProperty(any()))
                .thenReturn(Collections.singletonList(new RoomTypeResponseDTO()));

        PropertyResponseDTO result = propertyService.updatePropertyTransaction(request);

        assertNotNull(result);
        verify(propertyRepository).save(any(Property.class));
        verify(roomTypeService).updateRoomType(any(RoomType.class));
    }

    @Test
    void testUpdatePropertyTransaction_NotFound() {
        UpdatePropertyRequestDTO propertyRequest = new UpdatePropertyRequestDTO();
        propertyRequest.setPropertyId(propertyId);

        UpdatePropertyTransactionRequest request = new UpdatePropertyTransactionRequest();
        request.setProperty(propertyRequest);
        request.setRoomTypes(Collections.emptyList());

        when(propertyRepository.findByIdActive(propertyId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, 
                () -> propertyService.updatePropertyTransaction(request));
    }

    @Test
    void testUpdatePropertyTransaction_Unauthorized() {
        UUID otherOwnerId = UUID.randomUUID();

        UpdatePropertyRequestDTO propertyRequest = new UpdatePropertyRequestDTO();
        propertyRequest.setPropertyId(propertyId);

        UpdatePropertyTransactionRequest request = new UpdatePropertyTransactionRequest();
        request.setProperty(propertyRequest);
        request.setRoomTypes(Collections.emptyList());

        when(propertyRepository.findByIdActive(propertyId)).thenReturn(Optional.of(testProperty));
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(otherOwnerId);

        assertThrows(SecurityException.class, 
                () -> propertyService.updatePropertyTransaction(request));
    }

    @Test
    void testUpdatePropertyTransaction_RoomTypeMismatch() {
        UpdatePropertyRequestDTO propertyRequest = new UpdatePropertyRequestDTO();
        propertyRequest.setPropertyId(propertyId);
        propertyRequest.setType(1);
        propertyRequest.setProvince(1);
        propertyRequest.setAddress("Address");
        propertyRequest.setDescription("Description");

        UpdateRoomTypeRequestDTO roomTypeRequest = new UpdateRoomTypeRequestDTO();
        roomTypeRequest.setRoomTypeID(UUID.randomUUID()); // Different ID

        UpdatePropertyTransactionRequest request = new UpdatePropertyTransactionRequest();
        request.setProperty(propertyRequest);
        request.setRoomTypes(Collections.singletonList(roomTypeRequest));

        when(propertyRepository.findByIdActive(propertyId)).thenReturn(Optional.of(testProperty));
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);

        assertThrows(IllegalArgumentException.class, 
                () -> propertyService.updatePropertyTransaction(request));
    }

    // Test createProperty
    @Test
    void testCreateProperty_AsAccommodationOwner() {
        AddPropertyRequestDTO request = new AddPropertyRequestDTO();
        request.setPropertyName("New Hotel");
        request.setType(1);
        request.setProvince(1);
        request.setAddress("Address");
        request.setDescription("Description");

        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(ownerId);
        when(userContext.getName()).thenReturn("Owner Name");
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        Property result = propertyService.createProperty(request);

        assertNotNull(result);
        verify(propertyRepository).save(any(Property.class));
    }

    @Test
    void testCreateProperty_AsSUPERADMIN() {
        AddPropertyRequestDTO request = new AddPropertyRequestDTO();
        request.setPropertyName("New Hotel");
        request.setType(1);
        request.setProvince(1);
        request.setAddress("Address");
        request.setDescription("Description");
        request.setOwnerId(ownerId);
        request.setOwnerName("Owner Name");

        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        Property result = propertyService.createProperty(request);

        assertNotNull(result);
        verify(propertyRepository).save(any(Property.class));
    }

    @Test
    void testCreateProperty_MissingOwnerId() {
        AddPropertyRequestDTO request = new AddPropertyRequestDTO();
        request.setPropertyName("New Hotel");

        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);

        assertThrows(IllegalArgumentException.class, 
                () -> propertyService.createProperty(request));
    }

    // Test addRoomTypeToProperty
    @Test
    void testAddRoomTypeToProperty_Success() {
        AddRoomTypeRequestDTO roomTypeRequest = new AddRoomTypeRequestDTO();
        roomTypeRequest.setName("Suite");
        roomTypeRequest.setFloor(2);
        roomTypeRequest.setUnit(3);
        roomTypeRequest.setCapacity(4);
        roomTypeRequest.setPrice(200000);

        ListAddRoomTypeRequestDTO request = new ListAddRoomTypeRequestDTO();
        request.setRoomTypes(Collections.singletonList(roomTypeRequest));

        when(propertyRepository.findByIdActive(propertyId)).thenReturn(Optional.of(testProperty));
        when(roomTypeService.createRoomType(any(), any())).thenReturn(testRoomType);
        when(roomService.createRoom(any(), any())).thenReturn(testRoom);
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);
        when(roomTypeService.getRoomTypesByProperty(any()))
                .thenReturn(Collections.singletonList(new RoomTypeResponseDTO()));

        PropertyResponseDTO result = propertyService.addRoomTypeToProperty(propertyId, request);

        assertNotNull(result);
        verify(roomService, times(3)).createRoom(any(), any());
    }

    @Test
    void testAddRoomTypeToProperty_PropertyNotFound() {
        ListAddRoomTypeRequestDTO request = new ListAddRoomTypeRequestDTO();
        request.setRoomTypes(Collections.emptyList());

        when(propertyRepository.findByIdActive(propertyId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, 
                () -> propertyService.addRoomTypeToProperty(propertyId, request));
    }

    @Test
    void testAddRoomTypeToProperty_DuplicateRoomType() {
        AddRoomTypeRequestDTO roomTypeRequest = new AddRoomTypeRequestDTO();
        roomTypeRequest.setName("Deluxe"); // Same as existing
        roomTypeRequest.setFloor(1); // Same as existing
        roomTypeRequest.setUnit(3);

        ListAddRoomTypeRequestDTO request = new ListAddRoomTypeRequestDTO();
        request.setRoomTypes(Collections.singletonList(roomTypeRequest));

        when(propertyRepository.findByIdActive(propertyId)).thenReturn(Optional.of(testProperty));

        assertThrows(IllegalArgumentException.class, 
                () -> propertyService.addRoomTypeToProperty(propertyId, request));
    }

    // Test deleteProperty
    @Test
    void testDeleteProperty_Success() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        propertyService.deleteProperty(propertyId);

        verify(propertyRepository).save(testProperty);
        verify(roomService).deleteRoom(testRoom.getRoomID());
        assertEquals(0, testProperty.getActiveStatus());
    }

    @Test
    void testDeleteProperty_NotFound() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> propertyService.deleteProperty(propertyId));
    }

    @Test
    void testDeleteProperty_WithFutureBookings() {
        AccommodationBooking futureBooking = AccommodationBooking.builder()
                .bookingID(UUID.randomUUID())
                .checkInDate(LocalDateTime.now().plusDays(1))
                .checkOutDate(LocalDateTime.now().plusDays(3))
                .room(testRoom)
                .build();

        testRoom.getListAccommodationBooking().add(futureBooking);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));

        assertThrows(IllegalArgumentException.class, 
                () -> propertyService.deleteProperty(propertyId));
    }

    @Test
    void testDeleteProperty_WithPastBookings() {
        AccommodationBooking pastBooking = AccommodationBooking.builder()
                .bookingID(UUID.randomUUID())
                .checkInDate(LocalDateTime.now().minusDays(5))
                .checkOutDate(LocalDateTime.now().minusDays(3))
                .room(testRoom)
                .build();

        testRoom.getListAccommodationBooking().add(pastBooking);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        propertyService.deleteProperty(propertyId);

        verify(propertyRepository).save(testProperty);
        assertEquals(0, testProperty.getActiveStatus());
    }

    // Edge cases and additional coverage
    @Test
    void testGetIncomeStatistics_InactiveProperty() {
        testProperty.setActiveStatus(0);
        
        when(propertyRepository.findAllActive()).thenReturn(Collections.emptyList());

        IncomeStatisticsDTO result = propertyService.getIncomeStatistics(1, 2024);

        assertNotNull(result);
        assertNotNull(result.getPropertyStatistics());
        assertEquals(0, result.getPropertyStatistics().size());
        assertEquals(0, result.getTotalIncome());
    }

    @Test
    void testCreatePropertyTransaction_MultipleRoomTypes() {
        AddPropertyRequestDTO propertyRequest = new AddPropertyRequestDTO();
        propertyRequest.setOwnerId(UUID.randomUUID());
        propertyRequest.setOwnerName("John Doe");
        propertyRequest.setPropertyName("New Hotel");
        propertyRequest.setType(1);
        propertyRequest.setProvince(1);
        propertyRequest.setAddress("Address");
        propertyRequest.setDescription("Description");

        AddRoomTypeRequestDTO roomType1 = new AddRoomTypeRequestDTO();
        roomType1.setName("Standard");
        roomType1.setFloor(1);
        roomType1.setUnit(5);

        AddRoomTypeRequestDTO roomType2 = new AddRoomTypeRequestDTO();
        roomType2.setName("Deluxe");
        roomType2.setFloor(2);
        roomType2.setUnit(3);

        PropertyTransactionRequest request = new PropertyTransactionRequest();
        request.setProperty(propertyRequest);
        request.setRoomTypes(Arrays.asList(roomType1, roomType2));

        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);
        when(roomTypeService.createRoomType(any(), any())).thenReturn(testRoomType);
        when(roomService.createRoom(any(), any())).thenReturn(testRoom);
        when(roomTypeService.getRoomTypesByProperty(any()))
                .thenReturn(Collections.singletonList(new RoomTypeResponseDTO()));

        PropertyResponseDTO result = propertyService.createPropertyTransaction(request);

        assertNotNull(result);
        verify(roomService, times(8)).createRoom(any(), any()); // 5 + 3
    }

    @Test
    void testDeleteProperty_EmptyRoomList() {
        testProperty.getListRoomType().clear();
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        propertyService.deleteProperty(propertyId);

        verify(propertyRepository).save(testProperty);
        verify(roomService, never()).deleteRoom(any());
    }
}