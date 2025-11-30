package apap.ti._5.accommodation_2306165585_be.service.review;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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
import apap.ti._5.accommodation_2306165585_be.model.Review;
import apap.ti._5.accommodation_2306165585_be.model.Room;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306165585_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306165585_be.repository.ReviewRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.request.review.ReviewRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.review.ReviewResponseDTO;
import apap.ti._5.accommodation_2306165585_be.security.RoleGroup;
import apap.ti._5.accommodation_2306165585_be.security.UserContext;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private UserContext userContext;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AccommodationBookingRepository bookingRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private UUID userId;
    private UUID otherUserId;
    private UUID bookingId;
    private UUID reviewId;
    private UUID propertyId;
    private UUID ownerId;
    private UUID roomId;
    private UUID roomTypeId;
    
    private ReviewRequestDTO reviewRequest;
    private Review review;
    private AccommodationBooking booking;
    private Property property;
    private Room room;
    private RoomType roomType;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
        bookingId = UUID.randomUUID();
        reviewId = UUID.randomUUID();
        propertyId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        roomId = UUID.randomUUID();
        roomTypeId = UUID.randomUUID();

        // Setup property
        property = new Property();
        property.setPropertyID(propertyId);
        property.setOwnerID(ownerId);

        // Setup room type
        roomType = RoomType.builder()
                .roomTypeID(roomTypeId)
                .property(property)
                .build();

        // Setup room
        room = Room.builder()
                .roomID(roomId)
                .roomType(roomType)
                .build();

        // Setup booking
        booking = AccommodationBooking.builder()
                .bookingID(bookingId)
                .customerID(userId)
                .room(room)
                .status(1) // Completed status
                .checkOutDate(LocalDateTime.now().minusDays(1)) // Checked out yesterday
                .build();

        // Setup review request
        reviewRequest = new ReviewRequestDTO();
        reviewRequest.setBookingID(bookingId);
        reviewRequest.setComment("Great experience!");
        reviewRequest.setCleanlinessRating(5);
        reviewRequest.setFacilityRating(4);
        reviewRequest.setServiceRating(5);
        reviewRequest.setValueRating(4);

        // Setup review
        review = Review.builder()
                .reviewID(reviewId)
                .customerID(userId)
                .bookingID(bookingId)
                .comment("Great experience!")
                .cleanlinessRating(5)
                .facilityRating(4)
                .serviceRating(5)
                .valueRating(4)
                .overallRating(4) // (5+4+5+4)/4 = 4.5 -> 4 (integer division)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateReview_Success() {
        // Arrange
        when(userContext.getUserID()).thenReturn(userId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> {
            Review savedReview = i.getArgument(0);
            savedReview.setReviewID(reviewId);
            savedReview.setCreatedAt(LocalDateTime.now());
            return savedReview;
        });

        // Act
        ReviewResponseDTO result = reviewService.createReview(reviewRequest);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getCustomerID());
        assertEquals(bookingId, result.getBookingID());
        assertEquals("Great experience!", result.getComment());
        assertEquals(5, result.getCleanlinessRating());
        assertEquals(4, result.getFacilityRating());
        assertEquals(5, result.getServiceRating());
        assertEquals(4, result.getValueRating());
        assertEquals(4, result.getOverallRating()); // (5+4+5+4)/4 = 4
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testCreateReview_BookingNotFound() {
        // Arrange
        when(userContext.getUserID()).thenReturn(userId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, 
                () -> reviewService.createReview(reviewRequest));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testCreateReview_UnauthorizedUser() {
        // Arrange
        when(userContext.getUserID()).thenReturn(otherUserId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(SecurityException.class, 
                () -> reviewService.createReview(reviewRequest));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testCreateReview_BookingNotCompleted_StatusNotOne() {
        // Arrange
        booking.setStatus(0); // Not completed
        when(userContext.getUserID()).thenReturn(userId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> reviewService.createReview(reviewRequest));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testCreateReview_BookingNotCompleted_CheckOutDateInFuture() {
        // Arrange
        booking.setStatus(1);
        booking.setCheckOutDate(LocalDateTime.now().plusDays(1)); // Future checkout
        when(userContext.getUserID()).thenReturn(userId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> reviewService.createReview(reviewRequest));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testCreateReview_OverallRatingCalculation() {
        // Arrange
        reviewRequest.setCleanlinessRating(5);
        reviewRequest.setFacilityRating(5);
        reviewRequest.setServiceRating(5);
        reviewRequest.setValueRating(5);
        
        when(userContext.getUserID()).thenReturn(userId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> {
            Review savedReview = i.getArgument(0);
            savedReview.setReviewID(reviewId);
            return savedReview;
        });

        // Act
        ReviewResponseDTO result = reviewService.createReview(reviewRequest);

        // Assert
        assertEquals(5, result.getOverallRating()); // (5+5+5+5)/4 = 5
    }

    @Test
    void testGetReviewByReviewID_Success_AsCustomer() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(userContext.getRole()).thenReturn(RoleGroup.CUSTOMER);

        // Act
        ReviewResponseDTO result = reviewService.getReviewByReviewID(reviewId);

        // Assert
        assertNotNull(result);
        assertEquals(reviewId, result.getReviewID());
        assertEquals(userId, result.getCustomerID());
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    void testGetReviewByReviewID_Success_AsOwner() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(ownerId);
        when(bookingRepository.findById(reviewId)).thenReturn(Optional.of(booking));

        // Act
        ReviewResponseDTO result = reviewService.getReviewByReviewID(reviewId);

        // Assert
        assertNotNull(result);
        assertEquals(reviewId, result.getReviewID());
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    void testGetReviewByReviewID_Unauthorized_DifferentOwner() {
        // Arrange
        UUID differentOwnerId = UUID.randomUUID();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(differentOwnerId);
        when(bookingRepository.findById(reviewId)).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(SecurityException.class, 
                () -> reviewService.getReviewByReviewID(reviewId));
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    void testGetReviewByReviewID_NotFound() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, 
                () -> reviewService.getReviewByReviewID(reviewId));
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    void testGetAllReviewsByPropertyID_Success_AsSUPERADMIN() {
        // Arrange
        List<Review> reviews = Arrays.asList(review);
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(reviewRepository.findAllByPropertyID(propertyId)).thenReturn(reviews);

        // Act
        List<ReviewResponseDTO> result = reviewService.getAllReviewsByPropertyID(propertyId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reviewId, result.get(0).getReviewID());
        verify(reviewRepository, times(1)).findAllByPropertyID(propertyId);
    }

    @Test
    void testGetAllReviewsByPropertyID_Success_AsOwner() {
        // Arrange
        List<Review> reviews = Arrays.asList(review);
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(ownerId);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(reviewRepository.findAllByPropertyID(propertyId)).thenReturn(reviews);

        // Act
        List<ReviewResponseDTO> result = reviewService.getAllReviewsByPropertyID(propertyId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findAllByPropertyID(propertyId);
    }

    @Test
    void testGetAllReviewsByPropertyID_Unauthorized_DifferentOwner() {
        // Arrange
        UUID differentOwnerId = UUID.randomUUID();
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(differentOwnerId);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        // Act & Assert
        assertThrows(SecurityException.class, 
                () -> reviewService.getAllReviewsByPropertyID(propertyId));
        verify(reviewRepository, never()).findAllByPropertyID(propertyId);
    }

    @Test
    void testGetAllReviewsByPropertyID_PropertyNotFound() {
        // Arrange
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, 
                () -> reviewService.getAllReviewsByPropertyID(propertyId));
        verify(reviewRepository, never()).findAllByPropertyID(propertyId);
    }

    @Test
    void testGetAllReviewsByPropertyID_EmptyList() {
        // Arrange
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(reviewRepository.findAllByPropertyID(propertyId)).thenReturn(Arrays.asList());

        // Act
        List<ReviewResponseDTO> result = reviewService.getAllReviewsByPropertyID(propertyId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reviewRepository, times(1)).findAllByPropertyID(propertyId);
    }

    @Test
    void testGetAllReviewsByCustomerID_Success() {
        // Arrange
        List<Review> reviews = Arrays.asList(review);
        when(userContext.getUserID()).thenReturn(userId);
        when(reviewRepository.findAllByCustomerID(userId)).thenReturn(reviews);

        // Act
        List<ReviewResponseDTO> result = reviewService.getAllReviewsByCustomerID(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reviewId, result.get(0).getReviewID());
        verify(reviewRepository, times(1)).findAllByCustomerID(userId);
    }

    @Test
    void testGetAllReviewsByCustomerID_Unauthorized() {
        // Arrange
        when(userContext.getUserID()).thenReturn(otherUserId);

        // Act & Assert
        assertThrows(SecurityException.class, 
                () -> reviewService.getAllReviewsByCustomerID(userId));
        verify(reviewRepository, never()).findAllByCustomerID(any(UUID.class));
    }

    @Test
    void testGetAllReviewsByCustomerID_EmptyList() {
        // Arrange
        when(userContext.getUserID()).thenReturn(userId);
        when(reviewRepository.findAllByCustomerID(userId)).thenReturn(Arrays.asList());

        // Act
        List<ReviewResponseDTO> result = reviewService.getAllReviewsByCustomerID(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reviewRepository, times(1)).findAllByCustomerID(userId);
    }

    @Test
    void testGetAllReviewsByPropertyID_MultipleReviews() {
        // Arrange
        Review review2 = Review.builder()
                .reviewID(UUID.randomUUID())
                .customerID(otherUserId)
                .bookingID(UUID.randomUUID())
                .comment("Good stay")
                .cleanlinessRating(4)
                .facilityRating(4)
                .serviceRating(4)
                .valueRating(4)
                .overallRating(4)
                .createdAt(LocalDateTime.now())
                .build();

        List<Review> reviews = Arrays.asList(review, review2);
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(reviewRepository.findAllByPropertyID(propertyId)).thenReturn(reviews);

        // Act
        List<ReviewResponseDTO> result = reviewService.getAllReviewsByPropertyID(propertyId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(reviewRepository, times(1)).findAllByPropertyID(propertyId);
    }

    @Test
    void testCreateReview_DifferentRatings() {
        // Arrange
        reviewRequest.setCleanlinessRating(3);
        reviewRequest.setFacilityRating(4);
        reviewRequest.setServiceRating(2);
        reviewRequest.setValueRating(5);
        
        when(userContext.getUserID()).thenReturn(userId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> {
            Review savedReview = i.getArgument(0);
            savedReview.setReviewID(reviewId);
            return savedReview;
        });

        // Act
        ReviewResponseDTO result = reviewService.createReview(reviewRequest);

        // Assert
        assertEquals(3, result.getOverallRating()); // (3+4+2+5)/4 = 3.5 -> 3 (integer division)
    }

    @Test
    void testGetReviewByReviewID_Success_AsSUPERADMIN() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);

        // Act
        ReviewResponseDTO result = reviewService.getReviewByReviewID(reviewId);

        // Assert
        assertNotNull(result);
        assertEquals(reviewId, result.getReviewID());
        assertEquals("Great experience!", result.getComment());
        verify(reviewRepository, times(1)).findById(reviewId);
    }
}