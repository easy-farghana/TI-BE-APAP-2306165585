package apap.ti._5.accommodation_2306165585_be.restcontroller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.restdto.request.review.ReviewRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.review.ReviewResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.review.ReviewService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID reviewId;
    private UUID propertyId;
    private UUID customerId;
    private UUID bookingId;
    private ReviewResponseDTO reviewResponse;
    private ReviewRequestDTO reviewRequest;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();
        propertyId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        bookingId = UUID.randomUUID();

        // Setup ReviewResponseDTO
        reviewResponse = ReviewResponseDTO.builder()
                .reviewID(reviewId)
                .customerID(customerId)
                .customerName("John Doe")
                .propertyName("Grand Hotel")
                .bookingID(bookingId)
                .comment("Great hotel experience!")
                .cleanlinessRating(5)
                .facilityRating(4)
                .serviceRating(5)
                .valueRating(4)
                .overallRating(5)
                .createdAt(LocalDateTime.now())
                .build();

        // Setup ReviewRequestDTO
        reviewRequest = new ReviewRequestDTO();
        reviewRequest.setBookingID(bookingId);
        reviewRequest.setComment("Great hotel experience!");
        reviewRequest.setCleanlinessRating(5);
        reviewRequest.setFacilityRating(4);
        reviewRequest.setServiceRating(5);
        reviewRequest.setValueRating(4);
        reviewRequest.setCreatedAt(LocalDateTime.now());
    }

    // Test createReview - Success
    @Test
    void testCreateReview_Success() throws Exception {
        when(reviewService.createReview(any(ReviewRequestDTO.class)))
                .thenReturn(reviewResponse);

        mockMvc.perform(post("/api/review/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Review created successfully"))
                .andExpect(jsonPath("$.data.reviewID").value(reviewId.toString()))
                .andExpect(jsonPath("$.data.cleanlinessRating").value(5))
                .andExpect(jsonPath("$.data.facilityRating").value(4))
                .andExpect(jsonPath("$.data.serviceRating").value(5))
                .andExpect(jsonPath("$.data.valueRating").value(4))
                .andExpect(jsonPath("$.data.comment").value("Great hotel experience!"));

        verify(reviewService, times(1)).createReview(any(ReviewRequestDTO.class));
    }

    // Test createReview - Invalid Cleanliness Rating (too low)
    @Test
    void testCreateReview_InvalidCleanlinessRatingTooLow() throws Exception {
        ReviewRequestDTO invalidRequest = new ReviewRequestDTO();
        invalidRequest.setBookingID(bookingId);
        invalidRequest.setComment("Review");
        invalidRequest.setCleanlinessRating(0); // Invalid: less than 1
        invalidRequest.setFacilityRating(5);
        invalidRequest.setServiceRating(5);
        invalidRequest.setValueRating(5);

        mockMvc.perform(post("/api/review/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // Test createReview - Invalid Cleanliness Rating (too high)
    @Test
    void testCreateReview_InvalidCleanlinessRatingTooHigh() throws Exception {
        ReviewRequestDTO invalidRequest = new ReviewRequestDTO();
        invalidRequest.setBookingID(bookingId);
        invalidRequest.setComment("Review");
        invalidRequest.setCleanlinessRating(6); // Invalid: greater than 5
        invalidRequest.setFacilityRating(5);
        invalidRequest.setServiceRating(5);
        invalidRequest.setValueRating(5);

        mockMvc.perform(post("/api/review/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // Test createReview - Invalid Facility Rating
    @Test
    void testCreateReview_InvalidFacilityRating() throws Exception {
        ReviewRequestDTO invalidRequest = new ReviewRequestDTO();
        invalidRequest.setBookingID(bookingId);
        invalidRequest.setComment("Review");
        invalidRequest.setCleanlinessRating(5);
        invalidRequest.setFacilityRating(6); // Invalid
        invalidRequest.setServiceRating(5);
        invalidRequest.setValueRating(5);

        mockMvc.perform(post("/api/review/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // Test createReview - Invalid Service Rating
    @Test
    void testCreateReview_InvalidServiceRating() throws Exception {
        ReviewRequestDTO invalidRequest = new ReviewRequestDTO();
        invalidRequest.setBookingID(bookingId);
        invalidRequest.setComment("Review");
        invalidRequest.setCleanlinessRating(5);
        invalidRequest.setFacilityRating(5);
        invalidRequest.setServiceRating(0); // Invalid
        invalidRequest.setValueRating(5);

        mockMvc.perform(post("/api/review/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // Test createReview - Invalid Value Rating
    @Test
    void testCreateReview_InvalidValueRating() throws Exception {
        ReviewRequestDTO invalidRequest = new ReviewRequestDTO();
        invalidRequest.setBookingID(bookingId);
        invalidRequest.setComment("Review");
        invalidRequest.setCleanlinessRating(5);
        invalidRequest.setFacilityRating(5);
        invalidRequest.setServiceRating(5);
        invalidRequest.setValueRating(6); // Invalid

        mockMvc.perform(post("/api/review/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // Test createReview - All Ratings at Minimum Valid Value
    @Test
    void testCreateReview_MinimumValidRatings() throws Exception {
        ReviewRequestDTO minRequest = new ReviewRequestDTO();
        minRequest.setBookingID(bookingId);
        minRequest.setComment("Could be better");
        minRequest.setCleanlinessRating(1);
        minRequest.setFacilityRating(1);
        minRequest.setServiceRating(1);
        minRequest.setValueRating(1);

        ReviewResponseDTO minResponse = ReviewResponseDTO.builder()
                .reviewID(reviewId)
                .customerID(customerId)
                .customerName("John Doe")
                .propertyName("Grand Hotel")
                .bookingID(bookingId)
                .comment("Could be better")
                .cleanlinessRating(1)
                .facilityRating(1)
                .serviceRating(1)
                .valueRating(1)
                .overallRating(1)
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewService.createReview(any(ReviewRequestDTO.class))).thenReturn(minResponse);

        mockMvc.perform(post("/api/review/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.cleanlinessRating").value(1))
                .andExpect(jsonPath("$.data.facilityRating").value(1))
                .andExpect(jsonPath("$.data.serviceRating").value(1))
                .andExpect(jsonPath("$.data.valueRating").value(1));

        verify(reviewService, times(1)).createReview(any(ReviewRequestDTO.class));
    }

    // Test getReviewByID - Success
    @Test
    void testGetReviewByID_Success() throws Exception {
        when(reviewService.getReviewByReviewID(reviewId)).thenReturn(reviewResponse);

        mockMvc.perform(get("/api/review/{reviewID}", reviewId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Review fetched successfully"))
                .andExpect(jsonPath("$.data.reviewID").value(reviewId.toString()))
                .andExpect(jsonPath("$.data.customerName").value("John Doe"))
                .andExpect(jsonPath("$.data.propertyName").value("Grand Hotel"))
                .andExpect(jsonPath("$.data.overallRating").value(5));

        verify(reviewService, times(1)).getReviewByReviewID(reviewId);
    }

    // Test getReviewByID - Not Found
    @Test
    void testGetReviewByID_NotFound() throws Exception {
        when(reviewService.getReviewByReviewID(reviewId))
                .thenThrow(new NotFoundException("Review not found"));

        mockMvc.perform(get("/api/review/{reviewID}", reviewId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(reviewService, times(1)).getReviewByReviewID(reviewId);
    }

    // Test getReviewsByProperty - Success
    @Test
    void testGetReviewsByProperty_Success() throws Exception {
        ReviewResponseDTO review2 = ReviewResponseDTO.builder()
                .reviewID(UUID.randomUUID())
                .customerID(UUID.randomUUID())
                .customerName("Jane Smith")
                .propertyName("Grand Hotel")
                .bookingID(UUID.randomUUID())
                .comment("Nice place")
                .cleanlinessRating(4)
                .facilityRating(4)
                .serviceRating(3)
                .valueRating(4)
                .overallRating(4)
                .createdAt(LocalDateTime.now())
                .build();

        List<ReviewResponseDTO> reviews = Arrays.asList(reviewResponse, review2);
        when(reviewService.getAllReviewsByPropertyID(propertyId)).thenReturn(reviews);

        mockMvc.perform(get("/api/review/property/{propertyID}", propertyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Reviews for property fetched successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].customerName").value("John Doe"))
                .andExpect(jsonPath("$.data[1].customerName").value("Jane Smith"));

        verify(reviewService, times(1)).getAllReviewsByPropertyID(propertyId);
    }

    // Test getReviewsByProperty - Empty List
    @Test
    void testGetReviewsByProperty_EmptyList() throws Exception {
        when(reviewService.getAllReviewsByPropertyID(propertyId))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/review/property/{propertyID}", propertyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(reviewService, times(1)).getAllReviewsByPropertyID(propertyId);
    }

    // Test getReviewsByProperty - Property Not Found
    @Test
    void testGetReviewsByProperty_PropertyNotFound() throws Exception {
        when(reviewService.getAllReviewsByPropertyID(propertyId))
                .thenThrow(new NotFoundException("Property not found"));

        mockMvc.perform(get("/api/review/property/{propertyID}", propertyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(reviewService, times(1)).getAllReviewsByPropertyID(propertyId);
    }

    // Test getReviewsByCustomerID - Success
    @Test
    void testGetReviewsByCustomerID_Success() throws Exception {
        List<ReviewResponseDTO> reviews = Arrays.asList(reviewResponse);
        when(reviewService.getAllReviewsByCustomerID(customerId)).thenReturn(reviews);

        mockMvc.perform(get("/api/review/customer/{customerID}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Reviews for customer fetched successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].customerID").value(customerId.toString()));

        verify(reviewService, times(1)).getAllReviewsByCustomerID(customerId);
    }

    // Test getReviewsByCustomerID - Empty List
    @Test
    void testGetReviewsByCustomerID_EmptyList() throws Exception {
        when(reviewService.getAllReviewsByCustomerID(customerId))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/review/customer/{customerID}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(reviewService, times(1)).getAllReviewsByCustomerID(customerId);
    }

    // Test getReviewsByCustomerID - Customer Not Found
    @Test
    void testGetReviewsByCustomerID_CustomerNotFound() throws Exception {
        when(reviewService.getAllReviewsByCustomerID(customerId))
                .thenThrow(new NotFoundException("Customer not found"));

        mockMvc.perform(get("/api/review/customer/{customerID}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(reviewService, times(1)).getAllReviewsByCustomerID(customerId);
    }

    // Test getReviewsByCustomer - Success (current authenticated customer)
    @Test
    void testGetReviewsByCustomer_Success() throws Exception {
        List<ReviewResponseDTO> reviews = Arrays.asList(reviewResponse);
        when(reviewService.getAllReviewsByCustomer()).thenReturn(reviews);

        mockMvc.perform(get("/api/review/customer")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Reviews for customer fetched successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));

        verify(reviewService, times(1)).getAllReviewsByCustomer();
    }

    // Test getReviewsByCustomer - Empty List
    @Test
    void testGetReviewsByCustomer_EmptyList() throws Exception {
        when(reviewService.getAllReviewsByCustomer()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/review/customer")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(reviewService, times(1)).getAllReviewsByCustomer();
    }

    // Test getReviewsByProperty - Multiple Reviews with Different Ratings
    @Test
    void testGetReviewsByProperty_MultipleRatings() throws Exception {
        ReviewResponseDTO review2 = ReviewResponseDTO.builder()
                .reviewID(UUID.randomUUID())
                .customerID(UUID.randomUUID())
                .customerName("Alice Brown")
                .propertyName("Grand Hotel")
                .bookingID(UUID.randomUUID())
                .comment("Average experience")
                .cleanlinessRating(3)
                .facilityRating(3)
                .serviceRating(3)
                .valueRating(3)
                .overallRating(3)
                .createdAt(LocalDateTime.now())
                .build();

        ReviewResponseDTO review3 = ReviewResponseDTO.builder()
                .reviewID(UUID.randomUUID())
                .customerID(UUID.randomUUID())
                .customerName("Bob Wilson")
                .propertyName("Grand Hotel")
                .bookingID(UUID.randomUUID())
                .comment("Excellent!")
                .cleanlinessRating(5)
                .facilityRating(5)
                .serviceRating(5)
                .valueRating(5)
                .overallRating(5)
                .createdAt(LocalDateTime.now())
                .build();

        List<ReviewResponseDTO> reviews = Arrays.asList(reviewResponse, review2, review3);
        when(reviewService.getAllReviewsByPropertyID(propertyId)).thenReturn(reviews);

        mockMvc.perform(get("/api/review/property/{propertyID}", propertyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].overallRating").value(5))
                .andExpect(jsonPath("$.data[1].overallRating").value(3))
                .andExpect(jsonPath("$.data[2].overallRating").value(5));

        verify(reviewService, times(1)).getAllReviewsByPropertyID(propertyId);
    }

    // Test getReviewsByCustomerID - Multiple Reviews
    @Test
    void testGetReviewsByCustomerID_MultipleReviews() throws Exception {
        ReviewResponseDTO review2 = ReviewResponseDTO.builder()
                .reviewID(UUID.randomUUID())
                .customerID(customerId)
                .customerName("John Doe")
                .propertyName("Beach Resort")
                .bookingID(UUID.randomUUID())
                .comment("Good service")
                .cleanlinessRating(4)
                .facilityRating(4)
                .serviceRating(5)
                .valueRating(4)
                .overallRating(4)
                .createdAt(LocalDateTime.now())
                .build();

        List<ReviewResponseDTO> reviews = Arrays.asList(reviewResponse, review2);
        when(reviewService.getAllReviewsByCustomerID(customerId)).thenReturn(reviews);

        mockMvc.perform(get("/api/review/customer/{customerID}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].customerID").value(customerId.toString()))
                .andExpect(jsonPath("$.data[1].customerID").value(customerId.toString()))
                .andExpect(jsonPath("$.data[0].propertyName").value("Grand Hotel"))
                .andExpect(jsonPath("$.data[1].propertyName").value("Beach Resort"));

        verify(reviewService, times(1)).getAllReviewsByCustomerID(customerId);
    }

    // Test createReview - All Ratings at Maximum Valid Value
    @Test
    void testCreateReview_MaximumValidRatings() throws Exception {
        ReviewRequestDTO maxRequest = new ReviewRequestDTO();
        maxRequest.setBookingID(bookingId);
        maxRequest.setComment("Perfect stay!");
        maxRequest.setCleanlinessRating(5);
        maxRequest.setFacilityRating(5);
        maxRequest.setServiceRating(5);
        maxRequest.setValueRating(5);

        ReviewResponseDTO maxResponse = ReviewResponseDTO.builder()
                .reviewID(reviewId)
                .customerID(customerId)
                .customerName("John Doe")
                .propertyName("Grand Hotel")
                .bookingID(bookingId)
                .comment("Perfect stay!")
                .cleanlinessRating(5)
                .facilityRating(5)
                .serviceRating(5)
                .valueRating(5)
                .overallRating(5)
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewService.createReview(any(ReviewRequestDTO.class))).thenReturn(maxResponse);

        mockMvc.perform(post("/api/review/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maxRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.cleanlinessRating").value(5))
                .andExpect(jsonPath("$.data.facilityRating").value(5))
                .andExpect(jsonPath("$.data.serviceRating").value(5))
                .andExpect(jsonPath("$.data.valueRating").value(5))
                .andExpect(jsonPath("$.data.overallRating").value(5));

        verify(reviewService, times(1)).createReview(any(ReviewRequestDTO.class));
    }
}