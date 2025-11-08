package apap.ti._5.accommodation_2306165585_be.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AccommodationBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.booking.AccommodationBookingService;
import apap.ti._5.accommodation_2306165585_be.utils.ResponseUtil;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import apap.ti._5.accommodation_2306165585_be.restdto.request.booking.AddBookingRequestDTO;



@RestController
@RequestMapping("/api")
public class AccommodationBookingController {

    @Autowired
    ResponseUtil responseUtil;

    @Autowired
    AccommodationBookingService accommodationBookingService;

    public static final String BASE_URL = "/booking";
    public static final String VIEW_BOOKING = BASE_URL + "/{bookingID}";
    public static final String CREATE_BOOKING = BASE_URL + "/create";
    public static final String PAY_BOOKING = BASE_URL + "/pay/{bookingID}";
    public static final String REFUND_BOOKING = BASE_URL + "/refund/{bookingID}"; 
    public static final String CANCEL_BOOKING = BASE_URL + "/cancel/{bookingID}";
    public static final String UPDATE_BOOKING = BASE_URL + "/update/{bookingID}";



    @GetMapping(BASE_URL)
    public ResponseEntity<BaseResponseDTO<List<AccommodationBookingResponseDTO>>> getAllRoomType() {
        List<AccommodationBookingResponseDTO> listBooking = accommodationBookingService.getAllAccommodationBookings();
        return responseUtil.success(
            listBooking,
            "List of all accommodation bookings fetched successfully",
            HttpStatus.OK
        );
    }

    @GetMapping(VIEW_BOOKING)
    public ResponseEntity<BaseResponseDTO<AccommodationBookingResponseDTO>> getAccommodationBookingById(@RequestParam String bookingID) {
        AccommodationBookingResponseDTO accommodationBooking = accommodationBookingService.getAccommodationBookingById(bookingID);
        return responseUtil.success(
            accommodationBooking,
            "Accommodation booking fetched successfully",
            HttpStatus.OK
        );
    }

    @PostMapping(CREATE_BOOKING)
    public ResponseEntity<BaseResponseDTO<AccommodationBookingResponseDTO>> createAccommodationBooking(@RequestBody AddBookingRequestDTO accommodationBooking) {
        AccommodationBookingResponseDTO newAccommodationBooking = accommodationBookingService.createBooking(accommodationBooking);
        return responseUtil.success(
            newAccommodationBooking,
            "Accommodation booking created successfully",
            HttpStatus.CREATED
        );
    }

    @PutMapping(UPDATE_BOOKING)
    public ResponseEntity<BaseResponseDTO<AccommodationBookingResponseDTO>> updateAccommodationBooking(@PathVariable String bookingID, @RequestBody AddBookingRequestDTO accommodationBooking) {
        AccommodationBookingResponseDTO updatedAccommodationBooking = accommodationBookingService.updateBooking(bookingID, accommodationBooking);
        return responseUtil.success(
            updatedAccommodationBooking,
            "Accommodation booking updated successfully",
            HttpStatus.OK
        );
    }

    @PostMapping(PAY_BOOKING)
    public ResponseEntity<BaseResponseDTO<AccommodationBookingResponseDTO>> payAccommodationBooking(@PathVariable String bookingID) {
        AccommodationBookingResponseDTO newAccommodationBooking = accommodationBookingService.payBooking(bookingID);
        return responseUtil.success(
            newAccommodationBooking,
            "Accommodation booking paid successfully",
            HttpStatus.CREATED
        );
    }

    @PostMapping(REFUND_BOOKING)
    public ResponseEntity<BaseResponseDTO<AccommodationBookingResponseDTO>> refundAccommodationBooking(@PathVariable String bookingID) {
        AccommodationBookingResponseDTO newAccommodationBooking = accommodationBookingService.giveRefund(bookingID);
        return responseUtil.success(
            newAccommodationBooking,
            "Accommodation booking refunded successfully",
            HttpStatus.CREATED
        );
    }

    @PostMapping(CANCEL_BOOKING)
    public ResponseEntity<BaseResponseDTO<AccommodationBookingResponseDTO>> cancelAccommodationBooking(@PathVariable String bookingID) {
        AccommodationBookingResponseDTO canceledBooking = accommodationBookingService.cancelBooking(bookingID);
        return responseUtil.success(canceledBooking,
            "Accommodation booking cancelled successfully",
            HttpStatus.CREATED
        );
    }
}
