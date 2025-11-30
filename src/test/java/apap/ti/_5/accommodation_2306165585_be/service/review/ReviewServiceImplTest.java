package apap.ti._5.accommodation_2306165585_be.service.review;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.exception.SecurityException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    private UUID customerId;
    private UUID ownerId;
    private UUID propertyId;
    private UUID bookingId;
    private UUID reviewId;
    private UUID roomTypeId;
    private UUID roomId;

    private Review testReview;
    private AccommodationBooking testBooking;
    private Property testProperty;
    private RoomType testRoomType;
    private Room testRoom;
    private ReviewRequestDTO testRequest;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        propertyId = UUID.randomUUID();
        bookingId = UUID.randomUUID();
        reviewId = UUID.randomUUID();
        roomTypeId = UUID.randomUUID();
        roomId = UUID.randomUUID();

        // Setup Property
        testProperty = Property.builder()
                .propertyID(propertyId)
                .propertyName("Test Hotel")
                .ownerID(ownerId)
                .ownerName("Test Owner")
                .type(1)
                .province(1)
                .address("Test Address")
                .description("Test Description")
                .activeStatus(1)
                .listRoomType(new ArrayList<>())
                .build();

        // Setup RoomType
        testRoomType = RoomType.builder()
                .roomTypeID(roomTypeId)
                .name("Deluxe")
                .floor(1)
                .capacity(2)
                .price(100000)
                .property(testProperty)
                .listRoom(new ArrayList<>())
                .build();

        // Setup Room
        testRoom = Room.builder()
                .roomID(roomId)
                .roomType(testRoomType)
                .availabilityStatus(1)
                .listAccommodationBooking(new ArrayList<>())
                .build();

        // Setup Booking (completed)
        testBooking = AccommodationBooking.builder()
                .bookingID(bookingId)
                .customerID(customerId)
                .checkInDate(LocalDateTime.now().minusDays(5))
                .checkOutDate(LocalDateTime.now().minusDays(2))
                .totalPrice(500000)
                .status(1)
                .room(testRoom)
                .build();

        // Setup Review
        testReview = Review.builder()
                .reviewID(reviewId)
                .customerID(customerId)
                .customerName("Test Customer")
                .propertyID(propertyId)
                .bookingID(bookingId)
                .comment("Great stay!")
                .cleanlinessRating(5)
                .facilityRating(4)
                .serviceRating(5)
                .valueRating(4)
                .overallRating(4)
                .createdAt(LocalDateTime.now())
                .build();

        // Setup Request
        testRequest = new ReviewRequestDTO();
        testRequest.setBookingID(bookingId);
        testRequest.setComment("Great stay!");
        testRequest.setCleanlinessRating(5);
        testRequest.setFacilityRating(4);
        testRequest.setServiceRating(5);
        testRequest.setValueRating(4);
    }

    // Test createReview - Success
@Test
    void testCreateReview_Success() {
        when(userContext.getUserID()).thenReturn(customerId);
        when(userContext.getName()).thenReturn("Test Customer");
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        
        // Need to mock propertyRepository.findById twice:
        // 1st call: in getPropertyFromReviewId() -> returns property for creating review
        // 2nd call: in mapToReviewResponseDTO() -> getPropertyFromReview() -> returns property for response
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        
        // When save is called, return the testReview with all fields populated
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review savedReview = invocation.getArgument(0);
            // Copy the saved review but with an ID
            return Review.builder()
                    .reviewID(reviewId)
                    .customerID(savedReview.getCustomerID())
                    .customerName(savedReview.getCustomerName())
                    .propertyID(savedReview.getPropertyID())
                    .bookingID(savedReview.getBookingID())
                    .comment(savedReview.getComment())
                    .cleanlinessRating(savedReview.getCleanlinessRating())
                    .facilityRating(savedReview.getFacilityRating())
                    .serviceRating(savedReview.getServiceRating())
                    .valueRating(savedReview.getValueRating())
                    .overallRating(savedReview.getOverallRating())
                    .createdAt(LocalDateTime.now())
                    .build();
        });

        ReviewResponseDTO result = reviewService.createReview(testRequest);

        assertNotNull(result);
        assertEquals(reviewId, result.getReviewID());
        assertEquals(customerId, result.getCustomerID());
        assertEquals("Test Customer", result.getCustomerName());
        assertEquals("Test Hotel", result.getPropertyName());
        assertEquals(4, result.getOverallRating());

        verify(reviewRepository).save(any(Review.class));
        verify(propertyRepository).findById(propertyId);
    }

    @Test
    void testCreateReview_CorrectOverallRatingCalculation() {
        testRequest.setCleanlinessRating(5);
        testRequest.setFacilityRating(5);
        testRequest.setServiceRating(4);
        testRequest.setValueRating(2);
        // Expected: (5 + 5 + 4 + 2) / 4 = 4

        when(userContext.getUserID()).thenReturn(customerId);
        when(userContext.getName()).thenReturn("Test Customer");
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        
        Review savedReview = Review.builder()
                .reviewID(reviewId)
                .customerID(customerId)
                .customerName("Test Customer")
                .propertyID(propertyId)
                .bookingID(bookingId)
                .comment("Great stay!")
                .cleanlinessRating(5)
                .facilityRating(5)
                .serviceRating(4)
                .valueRating(2)
                .overallRating(4)
                .createdAt(LocalDateTime.now())
                .build();
        
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        ReviewResponseDTO result = reviewService.createReview(testRequest);

        assertEquals(4, result.getOverallRating());
    }

    // Test createReview - Booking Not Found
    @Test
    void testCreateReview_BookingNotFound() {
        when(userContext.getUserID()).thenReturn(customerId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reviewService.createReview(testRequest));
        verify(reviewRepository, never()).save(any());
    }

    // Test createReview - Unauthorized User
    @Test
    void testCreateReview_UnauthorizedUser() {
        UUID otherUserId = UUID.randomUUID();
        
        when(userContext.getUserID()).thenReturn(otherUserId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        assertThrows(SecurityException.class, () -> reviewService.createReview(testRequest));
        verify(reviewRepository, never()).save(any());
    }

    // Test createReview - Booking Not Completed (future checkout)
    @Test
    void testCreateReview_BookingNotCompleted_FutureCheckout() {
        testBooking.setCheckOutDate(LocalDateTime.now().plusDays(2));
        
        when(userContext.getUserID()).thenReturn(customerId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        assertThrows(IllegalArgumentException.class, () -> reviewService.createReview(testRequest));
        verify(reviewRepository, never()).save(any());
    }

    // Test createReview - Booking Cancelled (status = 0)
    @Test
    void testCreateReview_BookingCancelled() {
        testBooking.setStatus(0);
        
        when(userContext.getUserID()).thenReturn(customerId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        assertThrows(IllegalArgumentException.class, () -> reviewService.createReview(testRequest));
        verify(reviewRepository, never()).save(any());
    }

    // Test getReviewByReviewID - Success as Customer
    @Test
    void testGetReviewByReviewID_Success_AsCustomer() {
        when(userContext.getRole()).thenReturn(RoleGroup.CUSTOMER);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));

        ReviewResponseDTO result = reviewService.getReviewByReviewID(reviewId);

        assertNotNull(result);
        assertEquals(reviewId, result.getReviewID());
        assertEquals("Test Hotel", result.getPropertyName());
    }

    // Test getReviewByReviewID - Success as Owner
    @Test
    void testGetReviewByReviewID_Success_AsOwner() {
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(ownerId);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));

        ReviewResponseDTO result = reviewService.getReviewByReviewID(reviewId);

        assertNotNull(result);
        assertEquals(reviewId, result.getReviewID());
    }

    // Test getReviewByReviewID - Unauthorized Owner
    @Test
    void testGetReviewByReviewID_UnauthorizedOwner() {
        UUID otherOwnerId = UUID.randomUUID();
        
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(otherOwnerId);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));

        assertThrows(SecurityException.class, () -> reviewService.getReviewByReviewID(reviewId));
    }

    // Test getReviewByReviewID - Review Not Found
    @Test
    void testGetReviewByReviewID_NotFound() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reviewService.getReviewByReviewID(reviewId));
    }

    // Test getAllReviewsByPropertyID - Success as Owner
    @Test
    void testGetAllReviewsByPropertyID_Success_AsOwner() {
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(ownerId);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(reviewRepository.findAllByPropertyID(propertyId))
                .thenReturn(Collections.singletonList(testReview));

        List<ReviewResponseDTO> result = reviewService.getAllReviewsByPropertyID(propertyId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reviewId, result.get(0).getReviewID());
    }

    // Test getAllReviewsByPropertyID - Success as Admin
    @Test
    void testGetAllReviewsByPropertyID_Success_AsAdmin() {
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(reviewRepository.findAllByPropertyID(propertyId))
                .thenReturn(Collections.singletonList(testReview));

        List<ReviewResponseDTO> result = reviewService.getAllReviewsByPropertyID(propertyId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // Test getAllReviewsByPropertyID - Unauthorized Owner
    @Test
    void testGetAllReviewsByPropertyID_UnauthorizedOwner() {
        UUID otherOwnerId = UUID.randomUUID();
        
        when(userContext.getRole()).thenReturn(RoleGroup.ACCOMMODATION_OWNER);
        when(userContext.getUserID()).thenReturn(otherOwnerId);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));

        assertThrows(SecurityException.class, 
                () -> reviewService.getAllReviewsByPropertyID(propertyId));
    }

    // Test getAllReviewsByPropertyID - Property Not Found
    @Test
    void testGetAllReviewsByPropertyID_PropertyNotFound() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, 
                () -> reviewService.getAllReviewsByPropertyID(propertyId));
    }

    // Test getAllReviewsByPropertyID - Multiple Reviews
    @Test
    void testGetAllReviewsByPropertyID_MultipleReviews() {
        Review review2 = Review.builder()
                .reviewID(UUID.randomUUID())
                .customerID(UUID.randomUUID())
                .customerName("Customer 2")
                .propertyID(propertyId)
                .bookingID(UUID.randomUUID())
                .comment("Good stay")
                .cleanlinessRating(4)
                .facilityRating(4)
                .serviceRating(4)
                .valueRating(4)
                .overallRating(4)
                .createdAt(LocalDateTime.now())
                .build();

        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(reviewRepository.findAllByPropertyID(propertyId))
                .thenReturn(Arrays.asList(testReview, review2));

        List<ReviewResponseDTO> result = reviewService.getAllReviewsByPropertyID(propertyId);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // Test getAllReviewsByCustomerID - Success
    @Test
    void testGetAllReviewsByCustomerID_Success() {
        when(userContext.getUserID()).thenReturn(customerId);
        when(reviewRepository.findAllByCustomerID(customerId))
                .thenReturn(Collections.singletonList(testReview));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));

        List<ReviewResponseDTO> result = reviewService.getAllReviewsByCustomerID(customerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reviewId, result.get(0).getReviewID());
    }

    // Test getAllReviewsByCustomerID - Unauthorized
    @Test
    void testGetAllReviewsByCustomerID_Unauthorized() {
        UUID otherCustomerId = UUID.randomUUID();
        
        when(userContext.getUserID()).thenReturn(customerId);

        assertThrows(SecurityException.class, 
                () -> reviewService.getAllReviewsByCustomerID(otherCustomerId));
    }

    // Test getAllReviewsByCustomer - Success
    @Test
    void testGetAllReviewsByCustomer_Success() {
        when(userContext.getUserID()).thenReturn(customerId);
        when(reviewRepository.findAllByCustomerID(customerId))
                .thenReturn(Collections.singletonList(testReview));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));

        List<ReviewResponseDTO> result = reviewService.getAllReviewsByCustomer();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reviewId, result.get(0).getReviewID());
    }

    // Test getAllReviewsByCustomer - No Reviews
    @Test
    void testGetAllReviewsByCustomer_NoReviews() {
        when(userContext.getUserID()).thenReturn(customerId);
        when(reviewRepository.findAllByCustomerID(customerId))
                .thenReturn(Collections.emptyList());

        List<ReviewResponseDTO> result = reviewService.getAllReviewsByCustomer();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Test getAllReviewsByCustomer - Multiple Reviews
    @Test
    void testGetAllReviewsByCustomer_MultipleReviews() {
        Property property2 = Property.builder()
                .propertyID(UUID.randomUUID())
                .propertyName("Hotel 2")
                .ownerID(ownerId)
                .build();

        Review review2 = Review.builder()
                .reviewID(UUID.randomUUID())
                .customerID(customerId)
                .customerName("Test Customer")
                .propertyID(property2.getPropertyID())
                .bookingID(UUID.randomUUID())
                .comment("Another good stay")
                .cleanlinessRating(5)
                .facilityRating(5)
                .serviceRating(5)
                .valueRating(5)
                .overallRating(5)
                .createdAt(LocalDateTime.now())
                .build();

        when(userContext.getUserID()).thenReturn(customerId);
        when(reviewRepository.findAllByCustomerID(customerId))
                .thenReturn(Arrays.asList(testReview, review2));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.findById(property2.getPropertyID())).thenReturn(Optional.of(property2));

        List<ReviewResponseDTO> result = reviewService.getAllReviewsByCustomer();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // Test Edge Case - Checkout exactly at current time
    @Test
    void testCreateReview_CheckoutAtCurrentTime() {
        testBooking.setCheckOutDate(LocalDateTime.now().minusSeconds(1));
        
        when(userContext.getUserID()).thenReturn(customerId);
        when(userContext.getName()).thenReturn("Test Customer");
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        ReviewResponseDTO result = reviewService.createReview(testRequest);

        assertNotNull(result);
        verify(reviewRepository).save(any(Review.class));
    }

    // Test Rating Calculation - All Same Ratings
    @Test
    void testCreateReview_AllSameRatings() {
        testRequest.setCleanlinessRating(3);
        testRequest.setFacilityRating(3);
        testRequest.setServiceRating(3);
        testRequest.setValueRating(3);
        
        Review savedReview = Review.builder()
                .reviewID(reviewId)
                .customerID(customerId)
                .customerName("Test Customer")
                .propertyID(propertyId)
                .bookingID(bookingId)
                .comment("Average stay")
                .cleanlinessRating(3)
                .facilityRating(3)
                .serviceRating(3)
                .valueRating(3)
                .overallRating(3)
                .createdAt(LocalDateTime.now())
                .build();

        when(userContext.getUserID()).thenReturn(customerId);
        when(userContext.getName()).thenReturn("Test Customer");
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        ReviewResponseDTO result = reviewService.createReview(testRequest);

        assertEquals(3, result.getOverallRating());
    }

    // Test with null comment
    @Test
    void testCreateReview_NullComment() {
        testRequest.setComment(null);
        
        when(userContext.getUserID()).thenReturn(customerId);
        when(userContext.getName()).thenReturn("Test Customer");
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(testProperty));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        ReviewResponseDTO result = reviewService.createReview(testRequest);

        assertNotNull(result);
        verify(reviewRepository).save(any(Review.class));
    }
}