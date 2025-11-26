package apap.ti._5.accommodation_2306165585_be.service.review;

import apap.ti._5.accommodation_2306165585_be.restdto.request.review.ReviewRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.review.ReviewResponseDTO;
import java.util.List;
import java.util.UUID;

public interface ReviewService {
    List<ReviewResponseDTO> getAllReviewsByPropertyID(UUID propertyID);
    List<ReviewResponseDTO> getAllReviewsByCustomerID(UUID customerID);
    ReviewResponseDTO getReviewByReviewID(UUID reviewID);
    ReviewResponseDTO createReview(ReviewRequestDTO request);
}
