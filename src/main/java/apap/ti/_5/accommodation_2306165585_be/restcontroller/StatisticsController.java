package apap.ti._5.accommodation_2306165585_be.restcontroller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AllBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.AllPropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.statistics.HomeStatisticsDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.statistics.IncomeStatisticsDTO;
import apap.ti._5.accommodation_2306165585_be.service.booking.AccommodationBookingService;
import apap.ti._5.accommodation_2306165585_be.service.property.PropertyService;
import apap.ti._5.accommodation_2306165585_be.utils.ResponseUtil;

@RestController
@RequestMapping("/api")
public class StatisticsController {
    @Autowired
    private ResponseUtil responseUtil;
    
    @Autowired
    private PropertyService propertyService;

    @Autowired
    private AccommodationBookingService accommodationBookingService;
    
    public static final String BASE_URL = "/statistics";

    @GetMapping(BASE_URL)
    public ResponseEntity<BaseResponseDTO<HomeStatisticsDTO>> getStatistics() {
        List<AllPropertyResponseDTO> properties = propertyService.getAllProperties();
        List<AllBookingResponseDTO> bookings = accommodationBookingService.getAllAccommodationBookings();

        int totalProperties = properties.size();
        int totalBookings = bookings.size();

        return responseUtil.success(
            HomeStatisticsDTO.builder()
                .totalProperties(totalProperties)
                .totalBookings(totalBookings)
                .build(),
            "Statistics fetched successfully",
            HttpStatus.OK
        );
    }

}
