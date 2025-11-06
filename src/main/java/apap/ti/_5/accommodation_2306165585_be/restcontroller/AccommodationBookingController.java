package apap.ti._5.accommodation_2306165585_be.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AccommodationBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.booking.AccommodationBookingService;
import apap.ti._5.accommodation_2306165585_be.utils.ResponseUtil;

@RestController
@RequestMapping("/api")
public class AccommodationBookingController {

    @Autowired
    ResponseUtil responseUtil;

    @Autowired
    AccommodationBookingService accommodationBookingService;

    public static final String BASE_URL = "/booking";

    @GetMapping(BASE_URL)
    public ResponseEntity<BaseResponseDTO<List<AccommodationBookingResponseDTO>>> getAllRoomType() {
        List<AccommodationBookingResponseDTO> listBooking = accommodationBookingService.getAllAccommodationBookings();
        return responseUtil.success(
            listBooking,
            "List of all accommodation bookings fetched successfully",
            HttpStatus.OK
        );
    }
}
