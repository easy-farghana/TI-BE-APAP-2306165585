package apap.ti._5.accommodation_2306165585_be.service.external;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.http.MediaType;

import apap.ti._5.accommodation_2306165585_be.restdto.external.response.VerifyTokenResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.external.response.UserInfoResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.booking.AccommodationBookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ExternalApiService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AccommodationBookingService bookingService;

    @Value("${external.flight-service-url}")
    private String flightServiceUrl;

    @Value("${external.insurance-service-url}")
    private String insuranceServiceUrl;

    @Value("${external.tour-service-url}")
    private String tourServiceUrl;

    @Value("${external.rental-service-url}")
    private String rentalServiceUrl;

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();

        // Get token from current HTTP request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                headers.set("Authorization", authHeader);
            }
        }

        return headers;
    }

    /**
     * Check if a service reference is valid.
     * 
     * @param serviceName       name of the service (e.g. "accommodation", "flight", "rental", "tour", "insurance")
     * @param serviceReferenceId  ID of the service reference
     * @return true if the service reference is valid, false otherwise
     * @throws HttpClientErrorException if the service reference is not found in the external service
     * @throws Exception if any other exception occurs
     */
    public boolean checkIfValidServiceReference(String serviceName, String serviceReferenceId) {
        try {

            if (serviceName.equals("Accommodation")) {
                // internal service: will throw if not found
                bookingService.getAccommodationBookingById(UUID.fromString(serviceReferenceId));
                return true;
            }

            // For external services
            String url = switch (serviceName) {
                case "Flight" -> flightServiceUrl + "/api/bookings/" + serviceReferenceId + "/detail";
                case "VehicleRental" -> rentalServiceUrl + "/api/bookings/" + serviceReferenceId;
                case "TourPackage" -> tourServiceUrl + "/api/bookings/" + serviceReferenceId;
                case "Insurance" -> insuranceServiceUrl + "/api/policy/" + serviceReferenceId;
                default -> null;
            };

            // Unknown service
            if (url == null) {
                return false;
            }

            HttpEntity<?> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );

            return response.getStatusCode() == HttpStatus.OK;

        } catch (HttpClientErrorException.NotFound e) {
            // 404 from external service
            return false;
        } catch (Exception e) {
            // Other exceptions: log if needed
            log.error("Error checking service reference: " + e.getMessage());
            return false;
        }
    }


    public UserInfoResponseDTO getUserDetail(UUID userId) {
        try {
            String url = flightServiceUrl + "/api/users/" + userId;
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<BaseResponseDTO<UserInfoResponseDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<BaseResponseDTO<UserInfoResponseDTO>>() {}
            );

            if (response != null && response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData();
            }

            log.error("User profile response body is null for Id: " + userId);
            return null;
        } catch (Exception e) {
            log.error("Error fetching user profile from profile service for Id " + userId + ": " + e.getMessage());
            return null;
        }
    }

    
}
