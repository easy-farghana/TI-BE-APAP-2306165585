package apap.ti._5.accommodation_2306165585_be.restcontroller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import apap.ti._5.accommodation_2306165585_be.restdto.request.booking.BookingRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AccommodationBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AllBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.statistics.IncomeStatisticsDTO;
import apap.ti._5.accommodation_2306165585_be.service.booking.AccommodationBookingService;
import apap.ti._5.accommodation_2306165585_be.service.property.PropertyService;
import apap.ti._5.accommodation_2306165585_be.utils.ResponseUtil;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/api")
public class AccommodationBookingController {

    private final ResponseUtil responseUtil;
    private final AccommodationBookingService accommodationBookingService;
    private final PropertyService propertyService;

    public static final String BASE_URL = "/booking";
    public static final String VIEW_BOOKING = BASE_URL + "/{bookingID}";
    public static final String CREATE_BOOKING = BASE_URL + "/create";
    public static final String UPDATE_BOOKING_STATUS = BASE_URL + "/update/status/{bookingID}";
    // public static final String REFUND_BOOKING = BASE_URL + "/refund/{bookingID}"; 
    public static final String CANCEL_BOOKING = BASE_URL + "/cancel/{bookingID}";
    public static final String UPDATE_BOOKING = BASE_URL + "/update/{bookingID}";
    
    public AccommodationBookingController(
        AccommodationBookingService accommodationBookingService,
        PropertyService propertyService,
        ResponseUtil responseUtil
    ) {

        this.accommodationBookingService = accommodationBookingService;
        this.propertyService = propertyService;
        this.responseUtil = responseUtil;
    } 


    @GetMapping(BASE_URL)
    public ResponseEntity<BaseResponseDTO<List<AllBookingResponseDTO>>> getAllBookingsings() {
        List<AllBookingResponseDTO> listBooking = accommodationBookingService.getAllAccommodationBookings();
        return responseUtil.success(
            listBooking,
            "List of all accommodation bookings fetched successfully",
            HttpStatus.OK
        );
    }

    @GetMapping(VIEW_BOOKING)
    public ResponseEntity<BaseResponseDTO<AccommodationBookingResponseDTO>> getAccommodationBookingById(@PathVariable UUID bookingID) {
        AccommodationBookingResponseDTO accommodationBooking = accommodationBookingService.getAccommodationBookingById(bookingID);
        return responseUtil.success(
            accommodationBooking,
            "Accommodation booking fetched successfully",
            HttpStatus.OK
        );
    }

    @PostMapping(CREATE_BOOKING)
    public ResponseEntity<BaseResponseDTO<AccommodationBookingResponseDTO>> createAccommodationBooking(@Valid @RequestBody BookingRequestDTO accommodationBooking) {
        AccommodationBookingResponseDTO newAccommodationBooking = accommodationBookingService.createBooking(accommodationBooking);
        return responseUtil.success(
            newAccommodationBooking,
            "Accommodation booking created successfully",
            HttpStatus.CREATED
        );
    }

    @PutMapping(UPDATE_BOOKING)
    public ResponseEntity<BaseResponseDTO<AccommodationBookingResponseDTO>> updateAccommodationBooking(
        @PathVariable UUID bookingID, 
        @RequestBody BookingRequestDTO accommodationBooking
    ) {
        AccommodationBookingResponseDTO updatedAccommodationBooking = accommodationBookingService.updateBooking(bookingID, accommodationBooking);
        return responseUtil.success(
            updatedAccommodationBooking,
            "Accommodation booking updated successfully",
            HttpStatus.OK
        );
    }

    @PutMapping(UPDATE_BOOKING_STATUS)
    public ResponseEntity<BaseResponseDTO<AccommodationBookingResponseDTO>> updateAccommodationBookingStatus(@PathVariable UUID bookingID) {
        AccommodationBookingResponseDTO newAccommodationBooking = accommodationBookingService.updateBookingStatus(bookingID);
        return responseUtil.success(
            newAccommodationBooking,
            "Accommodation booking status updated successfully",
            HttpStatus.CREATED
        );
    }


    @PostMapping(CANCEL_BOOKING)
    public ResponseEntity<BaseResponseDTO<AccommodationBookingResponseDTO>> cancelAccommodationBooking(@PathVariable UUID bookingID) {
        AccommodationBookingResponseDTO canceledBooking = accommodationBookingService.cancelBooking(bookingID);
        return responseUtil.success(canceledBooking,
            "Accommodation booking cancelled successfully",
            HttpStatus.CREATED
        );
    }

    @GetMapping(BASE_URL + "/chart")
    public ResponseEntity<BaseResponseDTO<IncomeStatisticsDTO>> getIncomeStatistics(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        LocalDate now = LocalDate.now();
        int targetMonth = (month != null) ? month : now.getMonthValue();
        int targetYear = (year != null) ? year : now.getYear();

        IncomeStatisticsDTO stats = propertyService.getIncomeStatistics(targetMonth, targetYear);

        return responseUtil.success(
            stats,
            String.format("Income statistics for %02d/%d fetched successfully", targetMonth, targetYear),
            HttpStatus.OK
        );
    }
}
