package apap.ti._5.accommodation_2306165585_be.service.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import apap.ti._5.accommodation_2306165585_be.model.Review;
import apap.ti._5.accommodation_2306165585_be.model.Room;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.restdto.request.review.ReviewRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.review.ReviewResponseDTO;
import apap.ti._5.accommodation_2306165585_be.security.UserContext;
import apap.ti._5.accommodation_2306165585_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306165585_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306165585_be.repository.ReviewRepository;
import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306165585_be.security.RoleGroup;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    UserContext userContext;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    AccommodationBookingRepository bookingRepository;

    @Autowired
    PropertyRepository propertyRepository;
    
    @Override
    public ReviewResponseDTO createReview(ReviewRequestDTO request) {
        UUID userID = userContext.getUserID(); 
        
        AccommodationBooking booking = bookingRepository.findById(request.getBookingID()).orElseThrow(
            () -> new NotFoundException("Booking not found with ID: " + request.getBookingID())
        );

        if (!booking.getCustomerID().equals(userID)) {
            throw new SecurityException("You are not authorized to create a review for this booking");
        }

        LocalDateTime currentTime = LocalDateTime.now();

        boolean isCompleted = booking.getStatus() == 1 && booking.getCheckOutDate().isBefore(currentTime);
        
        if (!isCompleted) {
            throw new IllegalArgumentException("You can only create a review for completed bookings");
        }

        int overallRating = (
            request.getCleanlinessRating() + 
            request.getFacilityRating() + 
            request.getServiceRating() + 
            request.getValueRating()
        ) / 4;

        String customerName = userContext.getName();
        Property property = getPropertyFromReviewId(request.getBookingID());

        Review review = Review.builder()
            .customerID(userID)
            .customerName(customerName)
            .propertyID(property.getPropertyID())
            .bookingID(request.getBookingID())
            .comment(request.getComment())
            .cleanlinessRating(request.getCleanlinessRating())
            .facilityRating(request.getFacilityRating())
            .serviceRating(request.getServiceRating())
            .valueRating(request.getValueRating())
            .overallRating(overallRating)
            .build();

        reviewRepository.save(review);
        return mapToReviewResponseDTO(review);
    }

    @Override
    public ReviewResponseDTO getReviewByReviewID(UUID reviewID) {
        Review review = reviewRepository.findById(reviewID).orElseThrow(
            () -> new NotFoundException("Review not found with ID: " + reviewID)
        );

        String role = userContext.getRole();
        if (role.equals(RoleGroup.ACCOMMODATION_OWNER) && !isOwnerOfProperty(review)) {
            throw new SecurityException("You are not authorized to access this review");
        }

        return mapToReviewResponseDTO(review);
    }

    @Override
    public List<ReviewResponseDTO> getAllReviewsByPropertyID(UUID propertyID) {
        String role = userContext.getRole();
        Property property = propertyRepository.findById(propertyID).orElseThrow(
            () -> new NotFoundException("Property not found with ID: " + propertyID)
        );
        boolean isOwner = property.getOwnerID().equals(userContext.getUserID());

        if (role.equals(RoleGroup.ACCOMMODATION_OWNER) && !isOwner) {
            throw new SecurityException("You are not authorized to access this review");
        }

        List<Review> listOfReviews = reviewRepository.findAllByPropertyID(propertyID);


        return listOfReviews.stream()
            .map(this::mapToReviewResponseDTO)
            .toList();
    }

    @Override
    public List<ReviewResponseDTO> getAllReviewsByCustomerID(UUID customerID) {
        UUID userID = userContext.getUserID();
        if (!userID.equals(customerID)) {
            throw new SecurityException("You are not authorized to access this user's review");
        }

        List<Review> listOfReviews = reviewRepository.findAllByCustomerID(customerID);
        return listOfReviews.stream()
            .map(this::mapToReviewResponseDTO)
            .toList();
    }

    @Override
    public List<ReviewResponseDTO> getAllReviewsByCustomer() {
        UUID userID = userContext.getUserID();
        List<Review> listOfReviews = reviewRepository.findAllByCustomerID(userID);
        return listOfReviews.stream()
            .map(this::mapToReviewResponseDTO)
            .toList();
    }

    private Property getPropertyFromReviewId(UUID reviewID) {
        AccommodationBooking booking = bookingRepository.findById(reviewID).orElseThrow(
            () -> new NotFoundException("Booking not found with ID: " + reviewID)
        );
        Room room = booking.getRoom();
        RoomType roomType = room.getRoomType();
        Property property = roomType.getProperty();

        return property;
    }

    private Property getPropertyFromReview(Review review) {
        Property property = propertyRepository.findById(review.getPropertyID()).orElseThrow(
            () -> new NotFoundException("Booking not found with ID: " + review.getPropertyID())
        );

        return property;
    }

    private boolean isOwnerOfProperty(Review review) {
        Property property = getPropertyFromReview(review);
        return property.getOwnerID().equals(userContext.getUserID());
    }
    
    private ReviewResponseDTO mapToReviewResponseDTO(Review review) {
        String propertyName = getPropertyFromReview(review).getPropertyName();

        return ReviewResponseDTO.builder()
            .reviewID(review.getReviewID())
            .customerID(review.getCustomerID())
            .customerName(review.getCustomerName())
            .propertyName(propertyName)   
            .bookingID(review.getBookingID())
            .comment(review.getComment())
            .cleanlinessRating(review.getCleanlinessRating())
            .facilityRating(review.getFacilityRating())
            .serviceRating(review.getServiceRating())
            .valueRating(review.getValueRating())
            .overallRating(review.getOverallRating())
            .createdAt(review.getCreatedAt())
            .build();
    }
}
