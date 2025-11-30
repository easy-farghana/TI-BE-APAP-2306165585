package apap.ti._5.accommodation_2306165585_be.service.booking;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.exception.SecurityException;
import apap.ti._5.accommodation_2306165585_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.Room;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306165585_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306165585_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306165585_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.request.booking.BookingRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AccommodationBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AllBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.security.RoleGroup;
import apap.ti._5.accommodation_2306165585_be.security.UserContext;
import apap.ti._5.accommodation_2306165585_be.service.external.ExternalApiService;
import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;
import jakarta.validation.ConstraintViolationException;
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
class AccommodationBookingServiceImplTest {

    @Mock
    private AccommodationBookingRepository bookingRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private ExternalApiService externalApiService;

    @InjectMocks
    private AccommodationBookingServiceImpl bookingService;

    private UUID bookingId;
    private UUID roomId;
    private UUID roomTypeId;
    private UUID propertyId;
    private UUID customerId;
    private UUID ownerId;

    private Property testProperty;
    private RoomType testRoomType;
    private Room testRoom;
    private AccommodationBooking testBooking;
    private BookingRequestDTO validBookingRequest;

    @BeforeEach
    void setUp() {
        bookingId = UUID.randomUUID();
        roomId = UUID.randomUUID();
        roomTypeId = UUID.randomUUID();
        propertyId = UUID.randomUUID();
        customerId = UUID.randomUUID();
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
                .roomType(testRoomType)
                .listAccommodationBooking(new ArrayList<>())
                .availabilityStatus(1)
                .build();

        testRoomType.getListRoom().add(testRoom);
        testProperty.getListRoomType().add(testRoomType);

        LocalDateTime checkIn = LocalDateTime.now().plusDays(1);
        LocalDateTime checkOut = checkIn.plusDays(2);

        testBooking = AccommodationBooking.builder()
                .bookingID(bookingId)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .totalDays(2)
                .isBreakfast(true)
                .extraPay(0)
                .refund(0)
                .totalPrice(300000)
                .capacity(2)
                .customerID(customerId)
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .customerPhone("08123456789")
                .room(testRoom)
                .status(0)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        validBookingRequest = new BookingRequestDTO();
        validBookingRequest.setRoomID(roomId);
        validBookingRequest.setCheckInDate(checkIn);
        validBookingRequest.setCheckOutDate(checkOut);
        validBookingRequest.setCapacity(2);
        validBookingRequest.setCustomerID(customerId);
        validBookingRequest.setCustomerName("John Doe");
        validBookingRequest.setCustomerEmail("john@example.com");
        validBookingRequest.setCustomerPhone("08123456789");
        validBookingRequest.setIsBreakfast(true);
    }

    // Test getAllAccommodationBookings
    @Test
    void testGetAllAccommodationBookings_AsSuperAdmin() {
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(bookingRepository.findAll()).thenReturn(Collections.singletonList(testBooking));

        List<AllBookingResponseDTO> result = bookingService.getAllAccommodationBookings();

        assertEquals(1, result.size());
        assertEquals(bookingId, result.get(0).getBookingID());
        verify(bookingRepository).findAll();
    }

    @Test
    void testGetAllAccommodationBookings_AsAccommodationOwner() {
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(ownerId);
        when(bookingRepository.findAllByOwnerID(ownerId))
                .thenReturn(Collections.singletonList(testBooking));

        List<AllBookingResponseDTO> result = bookingService.getAllAccommodationBookings();

        assertEquals(1, result.size());
        verify(bookingRepository).findAllByOwnerID(ownerId);
    }

    @Test
    void testGetAllAccommodationBookings_AsCustomer() {
        when(userContext.getRole()).thenReturn(RoleGroup.CUSTOMER);
        when(userContext.getUserID()).thenReturn(customerId);
        when(bookingRepository.findAllByCustomerID(customerId))
                .thenReturn(Collections.singletonList(testBooking));

        List<AllBookingResponseDTO> result = bookingService.getAllAccommodationBookings();

        assertEquals(1, result.size());
        verify(bookingRepository).findAllByCustomerID(customerId);
    }

    @Test
    void testGetAllAccommodationBookings_UnauthorizedRole() {
        when(userContext.getRole()).thenReturn("UNKNOWN_ROLE");

        assertThrows(SecurityException.class, 
                () -> bookingService.getAllAccommodationBookings());
    }

    // Test getAccommodationBookingById
    @Test
    void testGetAccommodationBookingById_AsSuperAdmin() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);

        AccommodationBookingResponseDTO result = bookingService.getAccommodationBookingById(bookingId);

        assertNotNull(result);
        assertEquals(bookingId, result.getBookingID());
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void testGetAccommodationBookingById_AsAccommodationOwner_Authorized() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(ownerId);

        AccommodationBookingResponseDTO result = bookingService.getAccommodationBookingById(bookingId);

        assertNotNull(result);
        assertEquals(bookingId, result.getBookingID());
    }

    @Test
    void testGetAccommodationBookingById_AsAccommodationOwner_Unauthorized() {
        UUID otherOwnerId = UUID.randomUUID();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(otherOwnerId);

        assertThrows(SecurityException.class, 
                () -> bookingService.getAccommodationBookingById(bookingId));
    }

    @Test
    void testGetAccommodationBookingById_AsCustomer_Authorized() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(userContext.getRole()).thenReturn(RoleGroup.CUSTOMER);
        when(userContext.getUserID()).thenReturn(customerId);

        AccommodationBookingResponseDTO result = bookingService.getAccommodationBookingById(bookingId);

        assertNotNull(result);
        assertEquals(bookingId, result.getBookingID());
    }

    @Test
    void testGetAccommodationBookingById_AsCustomer_Unauthorized() {
        UUID otherCustomerId = UUID.randomUUID();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(userContext.getRole()).thenReturn(RoleGroup.CUSTOMER);
        when(userContext.getUserID()).thenReturn(otherCustomerId);

        assertThrows(SecurityException.class, 
                () -> bookingService.getAccommodationBookingById(bookingId));
    }

    @Test
    void testGetAccommodationBookingById_NotFound() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, 
                () -> bookingService.getAccommodationBookingById(bookingId));
    }

    // Test createBooking
    @Test
    void testCreateBooking_Success_WithBreakfast() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(userContext.getUserID()).thenReturn(customerId);
        when(roomService.isRoomAvailable(testRoom, validBookingRequest.getCheckInDate(), 
                validBookingRequest.getCheckOutDate())).thenReturn(true);
        when(bookingRepository.save(any(AccommodationBooking.class))).thenReturn(testBooking);
        doNothing().when(externalApiService).createBill(any());

        AccommodationBookingResponseDTO result = bookingService.createBooking(validBookingRequest);

        assertNotNull(result);
        verify(bookingRepository).save(any(AccommodationBooking.class));
        verify(externalApiService).createBill(any());
    }

    @Test
    void testCreateBooking_Success_WithoutBreakfast() {
        validBookingRequest.setIsBreakfast(false);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(userContext.getUserID()).thenReturn(customerId);
        when(roomService.isRoomAvailable(testRoom, validBookingRequest.getCheckInDate(), 
                validBookingRequest.getCheckOutDate())).thenReturn(true);
        when(bookingRepository.save(any(AccommodationBooking.class))).thenReturn(testBooking);
        doNothing().when(externalApiService).createBill(any());

        AccommodationBookingResponseDTO result = bookingService.createBooking(validBookingRequest);

        assertNotNull(result);
        verify(bookingRepository).save(any(AccommodationBooking.class));
    }

    @Test
    void testCreateBooking_RoomNotFound() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, 
                () -> bookingService.createBooking(validBookingRequest));
    }

    @Test
    void testCreateBooking_RoomNotInRoomType() {
        testRoomType.getListRoom().clear();
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        assertThrows(ConstraintViolationException.class, 
                () -> bookingService.createBooking(validBookingRequest));
    }

    @Test
    void testCreateBooking_CheckInDateInPast() {
        validBookingRequest.setCheckInDate(LocalDateTime.now().minusDays(1));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        assertThrows(ConstraintViolationException.class, 
                () -> bookingService.createBooking(validBookingRequest));
    }

    @Test
    void testCreateBooking_CheckInAfterCheckOut() {
        validBookingRequest.setCheckInDate(LocalDateTime.now().plusDays(5));
        validBookingRequest.setCheckOutDate(LocalDateTime.now().plusDays(2));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        assertThrows(ConstraintViolationException.class, 
                () -> bookingService.createBooking(validBookingRequest));
    }

    @Test
    void testCreateBooking_RoomNotAvailable() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(roomService.isRoomAvailable(testRoom, validBookingRequest.getCheckInDate(), 
                validBookingRequest.getCheckOutDate())).thenReturn(false);

        assertThrows(ConstraintViolationException.class, 
                () -> bookingService.createBooking(validBookingRequest));
    }

    @Test
    void testCreateBooking_CapacityExceeded() {
        validBookingRequest.setCapacity(5);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        assertThrows(ConstraintViolationException.class, 
                () -> bookingService.createBooking(validBookingRequest));
    }

    // Test cancelBooking
    @Test
    void testCancelBooking_Success() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(userContext.getUserID()).thenReturn(customerId);
        when(bookingRepository.save(any(AccommodationBooking.class))).thenReturn(testBooking);

        AccommodationBookingResponseDTO result = bookingService.cancelBooking(bookingId);

        assertNotNull(result);
        verify(bookingRepository).save(any(AccommodationBooking.class));
    }

    @Test
    void testCancelBooking_NotFound() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, 
                () -> bookingService.cancelBooking(bookingId));
    }

    @Test
    void testCancelBooking_NotOwnBooking() {
        UUID otherCustomerId = UUID.randomUUID();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(userContext.getUserID()).thenReturn(otherCustomerId);

        assertThrows(IllegalArgumentException.class, 
                () -> bookingService.cancelBooking(bookingId));
    }

    @Test
    void testCancelBooking_AlreadyPaid() {
        testBooking.setStatus(1);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(userContext.getUserID()).thenReturn(customerId);

        assertThrows(IllegalArgumentException.class, 
                () -> bookingService.cancelBooking(bookingId));
    }

    // Test updateBooking
    @Test
    void testUpdateBooking_Success() {
        LocalDateTime newCheckIn = LocalDateTime.now().plusDays(2);
        LocalDateTime newCheckOut = newCheckIn.plusDays(3);
        validBookingRequest.setCheckInDate(newCheckIn);
        validBookingRequest.setCheckOutDate(newCheckOut);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(userContext.getUserID()).thenReturn(customerId);
        when(bookingRepository.save(any(AccommodationBooking.class))).thenReturn(testBooking);

        AccommodationBookingResponseDTO result = bookingService.updateBooking(bookingId, validBookingRequest);

        assertNotNull(result);
        verify(bookingRepository).save(any(AccommodationBooking.class));
    }

    @Test
    void testUpdateBooking_BookingNotFound() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, 
                () -> bookingService.updateBooking(bookingId, validBookingRequest));
    }

    @Test
    void testUpdateBooking_RoomNotFound() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, 
                () -> bookingService.updateBooking(bookingId, validBookingRequest));
    }

    @Test
    void testUpdateBooking_NotOwnBooking() {
        UUID otherCustomerId = UUID.randomUUID();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(userContext.getUserID()).thenReturn(otherCustomerId);

        assertThrows(IllegalArgumentException.class, 
                () -> bookingService.updateBooking(bookingId, validBookingRequest));
    }

    @Test
    void testUpdateBooking_InvalidUpdateRequest_DifferentRoom() {
        UUID differentRoomId = UUID.randomUUID();
        Room differentRoom = Room.builder()
                .roomID(differentRoomId)
                .roomType(testRoomType)
                .build();

        validBookingRequest.setRoomID(differentRoomId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById(differentRoomId)).thenReturn(Optional.of(differentRoom));

        assertThrows(IllegalArgumentException.class, 
                () -> bookingService.updateBooking(bookingId, validBookingRequest));
    }

    // Test updateBookingStatusesForCheckIn
    @Test
    void testUpdateBookingStatusesForCheckIn_PendingBooking() {
        testBooking.setStatus(0);
        testBooking.setCheckInDate(LocalDateTime.now().toLocalDate().atTime(15, 0));

        when(bookingRepository.findByCheckInDateBetween(any(), any()))
                .thenReturn(Collections.singletonList(testBooking));
        when(bookingRepository.saveAll(anyList())).thenReturn(Collections.singletonList(testBooking));

        bookingService.updateBookingStatusesForCheckIn();

        verify(bookingRepository).saveAll(anyList());
        verify(propertyRepository).save(testProperty);
        assertEquals(0, testBooking.getStatus());
    }

    @Test
    void testUpdateBookingStatusesForCheckIn_PaidBooking() {
        testBooking.setStatus(1);
        testBooking.setCheckInDate(LocalDateTime.now().toLocalDate().atTime(15, 0));

        when(bookingRepository.findByCheckInDateBetween(any(), any()))
                .thenReturn(Collections.singletonList(testBooking));
        when(bookingRepository.saveAll(anyList())).thenReturn(Collections.singletonList(testBooking));

        bookingService.updateBookingStatusesForCheckIn();

        verify(bookingRepository).saveAll(anyList());
        assertEquals(4, testBooking.getStatus());
    }

    @Test
    void testUpdateBookingStatusesForCheckIn_CancelledBooking() {
        testBooking.setStatus(2);
        testBooking.setCheckInDate(LocalDateTime.now().toLocalDate().atTime(15, 0));

        when(bookingRepository.findByCheckInDateBetween(any(), any()))
                .thenReturn(Collections.singletonList(testBooking));
        when(bookingRepository.saveAll(anyList())).thenReturn(Collections.singletonList(testBooking));

        bookingService.updateBookingStatusesForCheckIn();

        verify(bookingRepository).saveAll(anyList());
        assertEquals(2, testBooking.getStatus()); // Status unchanged
    }

    @Test
    void testUpdateBookingStatusesForCheckIn_NoBookings() {
        when(bookingRepository.findByCheckInDateBetween(any(), any()))
                .thenReturn(Collections.emptyList());

        bookingService.updateBookingStatusesForCheckIn();

        verify(bookingRepository).saveAll(Collections.emptyList());
    }

    @Test
    void testUpdateBookingStatusesForCheckIn_MultipleBookings() {
        AccommodationBooking booking2 = AccommodationBooking.builder()
                .bookingID(UUID.randomUUID())
                .status(1)
                .checkInDate(LocalDateTime.now().toLocalDate().atTime(15, 0))
                .room(testRoom)
                .build();

        when(bookingRepository.findByCheckInDateBetween(any(), any()))
                .thenReturn(Arrays.asList(testBooking, booking2));
        when(bookingRepository.saveAll(anyList()))
                .thenReturn(Arrays.asList(testBooking, booking2));

        bookingService.updateBookingStatusesForCheckIn();

        verify(bookingRepository).saveAll(anyList());
        verify(propertyRepository, times(2)).save(testProperty);
    }

    // Test updateBookingStatus
    @Test
    void testUpdateBookingStatus_Success() {
        testBooking.setStatus(0);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(AccommodationBooking.class))).thenReturn(testBooking);
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        AccommodationBookingResponseDTO result = bookingService.updateBookingStatus(bookingId);

        assertNotNull(result);
        verify(bookingRepository).save(any(AccommodationBooking.class));
        verify(propertyRepository).save(testProperty);
        assertEquals(1, testBooking.getStatus());
        assertEquals(300000, testProperty.getIncome());
    }

    @Test
    void testUpdateBookingStatus_NotFound() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, 
                () -> bookingService.updateBookingStatus(bookingId));
    }

    @Test
    void testUpdateBookingStatus_AlreadyPaid() {
        testBooking.setStatus(1);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        assertThrows(ConstraintViolationException.class, 
                () -> bookingService.updateBookingStatus(bookingId));
    }

    @Test
    void testUpdateBookingStatus_AlreadyCancelled() {
        testBooking.setStatus(2);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        assertThrows(ConstraintViolationException.class, 
                () -> bookingService.updateBookingStatus(bookingId));
    }

    @Test
    void testUpdateBookingStatus_UpdatesPropertyIncome() {
        testBooking.setStatus(0);
        testProperty.setIncome(100000);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(AccommodationBooking.class))).thenReturn(testBooking);
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        bookingService.updateBookingStatus(bookingId);

        verify(propertyRepository).save(testProperty);
        assertEquals(400000, testProperty.getIncome()); // 100000 + 300000
    }


    // Additional edge cases
    @Test
    void testCreateBooking_SingleDayStay() {
        LocalDateTime checkIn = LocalDateTime.now().plusDays(1);
        LocalDateTime checkOut = checkIn.plusDays(1);
        validBookingRequest.setCheckInDate(checkIn);
        validBookingRequest.setCheckOutDate(checkOut);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(userContext.getUserID()).thenReturn(customerId);
        when(roomService.isRoomAvailable(testRoom, checkIn, checkOut)).thenReturn(true);
        when(bookingRepository.save(any(AccommodationBooking.class))).thenReturn(testBooking);
        doNothing().when(externalApiService).createBill(any());

        AccommodationBookingResponseDTO result = bookingService.createBooking(validBookingRequest);

        assertNotNull(result);
        verify(bookingRepository).save(any(AccommodationBooking.class));
    }

    @Test
    void testCreateBooking_LongStay() {
        LocalDateTime checkIn = LocalDateTime.now().plusDays(1);
        LocalDateTime checkOut = checkIn.plusDays(30);
        validBookingRequest.setCheckInDate(checkIn);
        validBookingRequest.setCheckOutDate(checkOut);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(userContext.getUserID()).thenReturn(customerId);
        when(roomService.isRoomAvailable(testRoom, checkIn, checkOut)).thenReturn(true);
        when(bookingRepository.save(any(AccommodationBooking.class))).thenReturn(testBooking);
        doNothing().when(externalApiService).createBill(any());

        AccommodationBookingResponseDTO result = bookingService.createBooking(validBookingRequest);

        assertNotNull(result);
        verify(bookingRepository).save(any(AccommodationBooking.class));
    }

    @Test
    void testUpdateBooking_SameRoomDifferentDates() {
        LocalDateTime newCheckIn = LocalDateTime.now().plusDays(5);
        LocalDateTime newCheckOut = newCheckIn.plusDays(3);
        validBookingRequest.setCheckInDate(newCheckIn);
        validBookingRequest.setCheckOutDate(newCheckOut);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(userContext.getUserID()).thenReturn(customerId);

        when(bookingRepository.save(any(AccommodationBooking.class))).thenReturn(testBooking);

        AccommodationBookingResponseDTO result = bookingService.updateBooking(bookingId, validBookingRequest);

        assertNotNull(result);
        verify(bookingRepository).save(any(AccommodationBooking.class));
    }

}