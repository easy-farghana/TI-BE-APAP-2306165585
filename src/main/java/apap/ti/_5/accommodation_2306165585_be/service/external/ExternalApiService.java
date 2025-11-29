package apap.ti._5.accommodation_2306165585_be.service.external;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.http.MediaType;

import apap.ti._5.accommodation_2306165585_be.restdto.external.response.LoginJwtResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.external.response.UserInfoResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.bill.BillRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.bill.BillResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AccommodationBookingResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ExternalApiService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${external.accommodation-service-url}")
    private String accommodationServiceUrl;

    @Value("${external.flight-service-url}")
    private String flightServiceUrl;

    @Value("${external.insurance-service-url}")
    private String insuranceServiceUrl;

    @Value("${external.tour-service-url}")
    private String tourServiceUrl;

    @Value("${external.rental-service-url}")
    private String rentalServiceUrl;

    @Value("${credentials.admin-email}")
    private String adminEmail;

    @Value("${credentials.admin-password}")
    private String adminPassword;

    @Value("${accommodation-be.app.apiKey}")
    private String apiKey;

    private String cachedAdminToken = null;
    private Long cachedAdminTokenExpiry = null;

    
    /**      
     *  Creates HTTP headers for the given HTTP request.
     *  If the request has a valid "Authorization" header, it is copied to the headers.
     *  @return The created HTTP headers.
     */ 
    private HttpHeaders createHeaders(boolean withApiKey) {
        HttpHeaders headers = new HttpHeaders();

        // Get token from current HTTP request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                headers.set("Authorization", authHeader);
            }

            if (withApiKey) {
                headers.set("API-KEY", apiKey);
            }
        }

        return headers;
    }

    /**
     * Gets an admin token from the flight service.
     * If a valid JWT token already exists, it is returned.
     * Otherwise, a new token is obtained and cached.
     * @return the admin token
     */
    private String getAdminToken() {

        Long now = System.currentTimeMillis();

        // check if valid JWT still exists
        if (cachedAdminToken != null && cachedAdminTokenExpiry != null && now < cachedAdminTokenExpiry) {
            return cachedAdminToken;
        }

        String url = flightServiceUrl + "/api/auth/login";

        Map<String, Object> body = new HashMap<>();
        body.put("email", adminEmail);
        body.put("password", adminPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = new HttpEntity<>(body, headers);

        ResponseEntity<BaseResponseDTO<LoginJwtResponseDTO>> response =
            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<BaseResponseDTO<LoginJwtResponseDTO>>() {}
            );

        String token = response.getBody().getData().getToken();
        Long exp = extractExpiration(token);

        cachedAdminToken = token;
        cachedAdminTokenExpiry = exp;

        return token;
    }

    /**
     * Deduct the payment amount from the user's balance.
     * @param userID the user's ID
     * @param userBalance the user's current balance
     * @param paymentAmount the payment amount to deduct
     * @return the user info response including the new balance
     * @throws IllegalStateException if the admin token is null or empty, or if the service returned an error status
     * @throws HttpClientErrorException if there is an HTTP error while deducting the balance
     * @throws RuntimeException if there is an unexpected error while deducting the balance
     */
    public UserInfoResponseDTO deductBalance(UUID userID, Long userBalance, Long paymentAmount) {
        try {
            String adminToken = getAdminToken();
            if (adminToken == null || adminToken.isEmpty()) {
                log.error("Failed to deduct balance: admin token is null or empty");
                throw new IllegalStateException("Failed to authorize");
            }

            Long newSaldo = userBalance - paymentAmount;
            Map<String, Object> body = new HashMap<>();
            body.put("saldo", newSaldo);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(adminToken);

            HttpEntity<?> entity = new HttpEntity<>(body, headers);

            String url = flightServiceUrl + "/api/users/" + userID;

            ResponseEntity<BaseResponseDTO<UserInfoResponseDTO>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.PUT,
                        entity,
                        new ParameterizedTypeReference<BaseResponseDTO<UserInfoResponseDTO>>() {}
                );

            if (response.getStatusCode().isError()) {
                log.error("PUT /api/users/{id} returned non-OK status: {}", response.getStatusCode());
                throw new IllegalStateException("Failed to deduct balance: service returned " + response.getStatusCode());
            }

            BaseResponseDTO<UserInfoResponseDTO> bodyResponse = response.getBody();
            if (bodyResponse == null) {
                log.error("Flight service response body is NULL");
                throw new IllegalStateException("Flight service response body is null");
            }

            if (bodyResponse.getData() == null) {
                log.error("Flight service returned NULL data field when deducting balance for user {}", userID);
                throw new IllegalStateException("No user data returned from flight service");
            }

            return bodyResponse.getData();
        } catch (HttpClientErrorException e) {
            log.error("HTTP error while deducting balance for user {}: {}", userID, e.getMessage());
            throw e; 
        } catch (Exception e) {
            log.error("Unexpected error in deductBalance for user {}: {}", userID, e.getMessage());
            throw new RuntimeException("Failed to deduct balance: " + e.getMessage(), e);
        }
    }

    /**
     * Update the booking status of a service.
     * @param serviceName the name of the service (Accommodation, Flight, Insurance, VehicleRental, TourPackage)
     * @param serviceReferenceID the reference ID of the booking
     */
    public void updateServicesBookingStatus(String serviceName, String serviceReferenceID) {
        switch (serviceName) {
            case "Accommodation":
                updateAccommodationBookingStatus(serviceReferenceID);
                break;
            case "Flight":
                // updateFlightBookingStatus(serviceReferenceID);
                break;
            case "Insurance":
                // updateInsuranceBookingStatus(serviceReferenceID);
                break;
            case "VehicleRental":
                // updateRentalBookingStatus(serviceReferenceID);
                break;
            case "TourPackage":
                // updateTourBookingStatus(serviceReferenceID);
                break;
            default:
                log.error("Unknown service name: {}", serviceName);
                throw new IllegalArgumentException("Unknown service name: " + serviceName);
        }
    }

    /**
     * Create a new bill with the given information.
     * @param request the information of the bill to be created
     * @return the created bill with a success message and HTTP status code of CREATED
     * @throws IllegalStateException if the service returned an error status
     */
    public BillResponseDTO createBill(BillRequestDTO request) {
        HttpEntity<BillRequestDTO> entity = new HttpEntity<>(request, createHeaders(true));
        log.info("Sending to bill-service: {}", request.toString());

        // Call service
        String url = accommodationServiceUrl + "/api/bill/create";
        try {
            ResponseEntity<BaseResponseDTO<BillResponseDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<BaseResponseDTO<BillResponseDTO>>() {}
            );
            return response.getBody().getData();
        } catch (HttpClientErrorException e) {
            log.error("POST /api/bill/create returned non-OK status: {}", e.getMessage());
            throw new IllegalStateException("Failed to create bill: service returned " + e.getMessage());
        } catch (SecurityException e) {
            log.error("POST /api/bill/create returned non-OK status: {}", e.getMessage());
            throw new IllegalStateException("Failed to create bill: service returned " + e.getMessage());
        } catch (Exception e) {
            log.error("POST /api/bill/create returned non-OK status: {}", e.getMessage());
            throw new IllegalStateException("Failed to create bill: service returned " + e.getMessage());
        }
    }

    public AccommodationBookingResponseDTO updateAccommodationBookingStatus(String serviceReferenceID) {
        // Call service
        String url = accommodationServiceUrl + "/api/booking/update/status/" + serviceReferenceID;

        try {
            ResponseEntity<BaseResponseDTO<AccommodationBookingResponseDTO>> response;
            response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(createHeaders(true)),
                    new ParameterizedTypeReference<BaseResponseDTO<AccommodationBookingResponseDTO>>() {}
            );
            if (response.getStatusCode().isError()) {
                log.error("PUT /api/booking/update/status returned non-OK status: {}", response.getStatusCode());
                throw new IllegalStateException("Failed to update Accommodation booking status: service returned " + response.getStatusCode());
            }

            return response.getBody().getData();
        } catch (HttpClientErrorException e) {
            log.error("HTTP error while updating Accommodation booking status: {}", e.getMessage());
            throw e;
        }
    }

    public UserInfoResponseDTO getUserDetail(UUID userId) {
        try {
            String url = flightServiceUrl + "/api/users/" + userId;
            HttpEntity<?> entity = new HttpEntity<>(createHeaders(false));

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

    private Long extractExpiration(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;

            String payloadJson = new String(java.util.Base64.getDecoder().decode(parts[1]));
            
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> payload = mapper.readValue(payloadJson, Map.class);

            if (payload.containsKey("exp")) {
                return ((Number) payload.get("exp")).longValue() * 1000;
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }



}
