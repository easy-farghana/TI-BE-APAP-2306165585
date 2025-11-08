package apap.ti._5.accommodation_2306165585_be.service.booking;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.Room;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306165585_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306165585_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306165585_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.request.booking.AddBookingRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AccommodationBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AllBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @InjectMocks
    private AccommodationBookingServiceImpl bookingService;

    private Room testRoom;
    private RoomType testRoomType;
    private Property testProperty;
    private AccommodationBooking testBooking;
    private AddBookingRequestDTO testRequest;

    @BeforeEach
    void setUp() {
        UUID randomUUID = UUID.randomUUID();
        // Setup test property
        testProperty = new Property();
        testProperty.setPropertyID("PROP12345678");
        testProperty.setPropertyName("Test Hotel");
        testProperty.setIncome(1000000);

        // Setup test room type
        testRoomType = new RoomType();
        testRoomType.setRoomTypeID("RT001");
        testRoomType.setPrice(500000);
        testRoomType.setCapacity(2);
        testRoomType.setListRoom(new ArrayList<>());

        // Setup test room
        testRoom = new Room();
        testRoom.setRoomID("PROP12345678-R001");
        testRoom.setName("Room 101");
        testRoomType.getListRoom().add(testRoom);

        // Setup test booking
        testBooking = AccommodationBooking.builder()
                .bookingID("BOOK-78-R001-2025-01-15-10:00:00")
                .checkInDate(LocalDateTime.of(2025, 1, 20, 14, 0))
                .checkOutDate(LocalDateTime.of(2025, 1, 22, 12, 0))
                .totalDays(2)
                .totalPrice(1000000)
                .status(0)
                .isBreakfast(false)
                .extraPay(0)
                .refund(0)
                .capacity(2)
                .customerID(randomUUID)
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .customerPhone("08123456789")
                .room(testRoom)
                .roomTypeID("RT001")
                .build();

        // Setup test request
        testRequest = new AddBookingRequestDTO();
        testRequest.setRoomID("PROP12345678-R001");
        testRequest.setRoomTypeID("RT001");
        testRequest.setCheckInDate(LocalDateTime.of(2025, 12, 20, 14, 0));
        testRequest.setCheckOutDate(LocalDateTime.of(2025, 12, 22, 12, 0));
        testRequest.setCapacity(2);
        testRequest.setIsBreakfast(false);
        testRequest.setCustomerID(randomUUID);
        testRequest.setCustomerName("John Doe");
        testRequest.setCustomerEmail("john@example.com");
        testRequest.setCustomerPhone("08123456789");
    }

    @Test
    void testGetAccommodationBookingById_Success() {
        // Arrange
        String bookingId = "BOOK-78-R001-2025-01-15-10:00:00";
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(propertyRepository.findById("PROP12345678")).thenReturn(Optional.of(testProperty));

        // Act
        AccommodationBookingResponseDTO result = bookingService.getAccommodationBookingById(bookingId);

        // Assert
        assertNotNull(result);
        assertEquals(bookingId, result.getBookingID());
        assertEquals("John Doe", result.getCustomerName());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void testGetAccommodationBookingById_NotFound() {
        // Arrange
        String bookingId = "NONEXISTENT";
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            bookingService.getAccommodationBookingById(bookingId);
        });
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void testCreateBooking_Success() {
        // Arrange
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(testRoom));
        when(roomTypeRepository.findById(anyString())).thenReturn(Optional.of(testRoomType));
        when(roomService.isRoomAvailable(any(), any(), any())).thenReturn(true);
        when(bookingRepository.save(any(AccommodationBooking.class))).thenReturn(testBooking);
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(testProperty));

        // Act
        AccommodationBookingResponseDTO result = bookingService.createBooking(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getCustomerName());
        verify(bookingRepository, times(1)).save(any(AccommodationBooking.class));
    }

    @Test
    void testCreateBooking_WithBreakfast() {
        // Arrange
        testRequest.setIsBreakfast(true);
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(testRoom));
        when(roomTypeRepository.findById(anyString())).thenReturn(Optional.of(testRoomType));
        when(roomService.isRoomAvailable(any(), any(), any())).thenReturn(true);
        when(bookingRepository.save(any(AccommodationBooking.class))).thenReturn(testBooking);
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(testProperty));

        // Act
        AccommodationBookingResponseDTO result = bookingService.createBooking(testRequest);

        // Assert
        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any(AccommodationBooking.class));
    }

    @Test
    void testCreateBooking_RoomNotFound() {
        // Arrange
        when(roomRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(testRequest);
        });
        verify(roomRepository, times(1)).findById(anyString());
    }

    @Test
    void testCreateBooking_RoomTypeNotFound() {
        // Arrange
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(testRoom));
        when(roomTypeRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(testRequest);
        });
    }

    @Test
    void testCreateBooking_RoomNotAvailable() {
        // Arrange
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(testRoom));
        when(roomTypeRepository.findById(anyString())).thenReturn(Optional.of(testRoomType));
        when(roomService.isRoomAvailable(any(), any(), any())).thenReturn(false);

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            bookingService.createBooking(testRequest);
        });
    }

    @Test
    void testCreateBooking_CheckInAfterCheckOut() {
        // Arrange
        testRequest.setCheckInDate(LocalDateTime.of(2025, 12, 25, 14, 0));
        testRequest.setCheckOutDate(LocalDateTime.of(2025, 12, 20, 12, 0));
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(testRoom));
        when(roomTypeRepository.findById(anyString())).thenReturn(Optional.of(testRoomType));

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            bookingService.createBooking(testRequest);
        });
    }

    @Test
    void testCreateBooking_CapacityExceeded() {
        // Arrange
        testRequest.setCapacity(5);
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(testRoom));
        when(roomTypeRepository.findById(anyString())).thenReturn(Optional.of(testRoomType));

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            bookingService.createBooking(testRequest);
        });
    }

    @Test
    void testCancelBooking_UnpaidWithExtraPay() {
        // Arrange
        testBooking.setStatus(0);
        testBooking.setExtraPay(100000);
        when(bookingRepository.findById(anyString())).thenReturn(Optional.of(testBooking));
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(testProperty));
        when(bookingRepository.save(any())).thenReturn(testBooking);

        // Act
        AccommodationBookingResponseDTO result = bookingService.cancelBooking(testBooking.getBookingID());

        // Assert
        assertNotNull(result);
        verify(propertyRepository, times(1)).save(any(Property.class));
        verify(bookingRepository, times(1)).save(any(AccommodationBooking.class));
    }

    @Test
    void testCancelBooking_PaidBooking() {
        // Arrange
        testBooking.setStatus(1);
        when(bookingRepository.findById(anyString())).thenReturn(Optional.of(testBooking));
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(testProperty));
        when(bookingRepository.save(any())).thenReturn(testBooking);

        // Act
        AccommodationBookingResponseDTO result = bookingService.cancelBooking(testBooking.getBookingID());

        // Assert
        assertNotNull(result);
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void testUpdateBooking_Success() {
        // Arrange
        when(bookingRepository.findById(anyString())).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(testRoom));
        when(roomTypeRepository.findById(anyString())).thenReturn(Optional.of(testRoomType));
      
        when(bookingRepository.save(any())).thenReturn(testBooking);
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(testProperty));

        // Act
        AccommodationBookingResponseDTO result = bookingService.updateBooking(
                testBooking.getBookingID(), testRequest);

        // Assert
        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any(AccommodationBooking.class));
    }

    @Test
    void testUpdateBooking_PriceIncrease() {
        // Arrange
        testBooking.setStatus(1);
        testBooking.setTotalPrice(500000);
        testRoomType.setPrice(800000);
        
        when(bookingRepository.findById(anyString())).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(testRoom));
        when(roomTypeRepository.findById(anyString())).thenReturn(Optional.of(testRoomType));

        when(bookingRepository.save(any())).thenReturn(testBooking);
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(testProperty));

        // Act
        AccommodationBookingResponseDTO result = bookingService.updateBooking(
                testBooking.getBookingID(), testRequest);

        // Assert
        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any(AccommodationBooking.class));
    }

    @Test
    void testUpdateBooking_PriceDecrease() {
        // Arrange
        testBooking.setStatus(1);
        testBooking.setTotalPrice(2000000);
        testRoomType.setPrice(300000);
        
        when(bookingRepository.findById(anyString())).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(testRoom));
        when(roomTypeRepository.findById(anyString())).thenReturn(Optional.of(testRoomType));

        when(bookingRepository.save(any())).thenReturn(testBooking);
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(testProperty));

        // Act
        AccommodationBookingResponseDTO result = bookingService.updateBooking(
                testBooking.getBookingID(), testRequest);

        // Assert
        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any(AccommodationBooking.class));
    }

    @Test
    void testUpdateBooking_RoomChanged() {
        // Arrange
        Room newRoom = new Room();
        newRoom.setRoomID("PROP12345678-R002");
        newRoom.setName("Room 102");
        testRequest.setRoomID("PROP12345678-R002");
        testRoomType.getListRoom().add(newRoom);

        when(bookingRepository.findById(anyString())).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById("PROP12345678-R002")).thenReturn(Optional.of(newRoom));
        when(roomTypeRepository.findById(anyString())).thenReturn(Optional.of(testRoomType));
        when(roomService.isRoomAvailable(any(), any(), any())).thenReturn(true);
        when(bookingRepository.save(any())).thenReturn(testBooking);
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(testProperty));

        // Act
        AccommodationBookingResponseDTO result = bookingService.updateBooking(
                testBooking.getBookingID(), testRequest);

        // Assert
        assertNotNull(result);
        verify(bookingRepository, times(1)).delete(any(AccommodationBooking.class));
        verify(bookingRepository, times(1)).save(any(AccommodationBooking.class));
    }

    @Test
    void testPayBooking_Success() {
        // Arrange
        testBooking.setStatus(0);
        when(bookingRepository.findById(anyString())).thenReturn(Optional.of(testBooking));
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(testProperty));
        when(bookingRepository.save(any())).thenReturn(testBooking);

        // Act
        AccommodationBookingResponseDTO result = bookingService.payBooking(testBooking.getBookingID());

        // Assert
        assertNotNull(result);
        verify(propertyRepository, times(1)).save(any(Property.class));
        verify(bookingRepository, times(1)).save(any(AccommodationBooking.class));
    }

    @Test
    void testPayBooking_WithExtraPay() {
        // Arrange
        testBooking.setStatus(0);
        testBooking.setExtraPay(200000);
        when(bookingRepository.findById(anyString())).thenReturn(Optional.of(testBooking));
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(testProperty));
        when(bookingRepository.save(any())).thenReturn(testBooking);

        // Act
        AccommodationBookingResponseDTO result = bookingService.payBooking(testBooking.getBookingID());

        // Assert
        assertNotNull(result);
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void testPayBooking_AlreadyPaid() {
        // Arrange
        testBooking.setStatus(1);
        when(bookingRepository.findById(anyString())).thenReturn(Optional.of(testBooking));
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(testProperty));

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            bookingService.payBooking(testBooking.getBookingID());
        });
    }

    @Test
    void testGiveRefund_Success() {
        // Arrange
        testBooking.setStatus(3);
        testBooking.setRefund(100000);
        when(bookingRepository.findById(anyString())).thenReturn(Optional.of(testBooking));
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(testProperty));
        when(bookingRepository.save(any())).thenReturn(testBooking);

        // Act
        AccommodationBookingResponseDTO result = bookingService.giveRefund(testBooking.getBookingID());

        // Assert
        assertNotNull(result);
        verify(propertyRepository, times(1)).save(any(Property.class));
        verify(bookingRepository, times(1)).save(any(AccommodationBooking.class));
    }

    @Test
    void testUpdateBookingStatusesForCheckIn_StatusZeroWithExtraPay() {
        // Arrange
        testBooking.setStatus(0);
        testBooking.setExtraPay(100000);
        testBooking.setCheckInDate(LocalDateTime.now().toLocalDate().atTime(14, 0));
        
        when(bookingRepository.findByCheckInDateBetween(any(), any()))
                .thenReturn(Arrays.asList(testBooking));
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(testProperty));

        // Act
        bookingService.updateBookingStatusesForCheckIn();

        // Assert
        verify(propertyRepository, times(1)).save(any(Property.class));
        verify(bookingRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testUpdateBookingStatusesForCheckIn_StatusOne() {
        // Arrange
        testBooking.setStatus(1);
        testBooking.setCheckInDate(LocalDateTime.now().toLocalDate().atTime(14, 0));
        
        when(bookingRepository.findByCheckInDateBetween(any(), any()))
                .thenReturn(Arrays.asList(testBooking));
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(testProperty));

        // Act
        bookingService.updateBookingStatusesForCheckIn();

        // Assert
        verify(propertyRepository, times(1)).save(any(Property.class));
        verify(bookingRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testUpdateBookingStatusesForCheckIn_StatusThree() {
        // Arrange
        testBooking.setStatus(3);
        testBooking.setRefund(50000);
        testBooking.setCheckInDate(LocalDateTime.now().toLocalDate().atTime(14, 0));
        
        when(bookingRepository.findByCheckInDateBetween(any(), any()))
                .thenReturn(Arrays.asList(testBooking));
        when(propertyRepository.findById(anyString())).thenReturn(Optional.of(testProperty));

        // Act
        bookingService.updateBookingStatusesForCheckIn();

        // Assert
        verify(propertyRepository, times(1)).save(any(Property.class));
        verify(bookingRepository, times(1)).saveAll(anyList());
    }
}