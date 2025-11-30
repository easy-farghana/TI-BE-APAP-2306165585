package apap.ti._5.accommodation_2306165585_be.restcontroller;

import org.springframework.web.bind.annotation.RestController;

import apap.ti._5.accommodation_2306165585_be.restdto.request.review.ReviewRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.review.ReviewResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.review.ReviewService;
import apap.ti._5.accommodation_2306165585_be.utils.ResponseUtil;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Slf4j
public class ReviewController {

    @Autowired
    private ResponseUtil responseUtil;

    @Autowired
    private ReviewService reviewService;


    public static final String BASE_URL = "/review";

    public static final String CREATE_REVIEW = BASE_URL + "/create";
    public static final String VIEW_REVIEW_DETAILS = BASE_URL + "/{reviewID}";
    public static final String VIEW_REVIEW_BY_PROPERTY = BASE_URL + "/property/{propertyID}";
    public static final String VIEW_REVIEW_BY_CUSTOMER = BASE_URL + "/customer";
    public static final String VIEW_REVIEW_BY_CUSTOMER_ID = BASE_URL + "/customer/{customerID}";


  
    @PostMapping(CREATE_REVIEW)
    public ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> createReview(
        @Valid @RequestBody ReviewRequestDTO request
    ) {

        ReviewResponseDTO created = reviewService.createReview(request);

        return responseUtil.success(
            created,
            "Review created successfully",
            HttpStatus.CREATED
        );
    }

    @GetMapping(VIEW_REVIEW_DETAILS)
    public ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> getReviewByID(@PathVariable UUID reviewID) {

        ReviewResponseDTO review = reviewService.getReviewByReviewID(reviewID);

        return responseUtil.success(
            review,
            "Review fetched successfully",
            HttpStatus.OK
        );
    }

    @GetMapping(VIEW_REVIEW_BY_PROPERTY)
    public ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> getReviewsByProperty(
        @PathVariable UUID propertyID
    ) {

        List<ReviewResponseDTO> reviews = reviewService.getAllReviewsByPropertyID(propertyID);

        return responseUtil.success(
            reviews,
            "Reviews for property fetched successfully",
            HttpStatus.OK
        );
    }


    @GetMapping(VIEW_REVIEW_BY_CUSTOMER_ID)
    public ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> getReviewsByCustomerID(
        @PathVariable UUID customerID
    ) {

        List<ReviewResponseDTO> reviews = reviewService.getAllReviewsByCustomerID(customerID);

        return responseUtil.success(
            reviews,
            "Reviews for customer fetched successfully",
            HttpStatus.OK
        );
    }

    @GetMapping(VIEW_REVIEW_BY_CUSTOMER)
    public ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> getReviewsByCustome() {

        List<ReviewResponseDTO> reviews = reviewService.getAllReviewsByCustomer();

        return responseUtil.success(
            reviews,
            "Reviews for customer fetched successfully",
            HttpStatus.OK
        );
    }
}
