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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.http.MediaType;

import apap.ti._5.accommodation_2306165585_be.restdto.external.response.VerifyTokenResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.external.response.LoginJwtResponseDTO;
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

    @Value("${credentials.admin-email}")
    private String adminEmail;

    @Value("${credentials.admin-password}")
    private String adminPassword;

    private String cachedAdminToken = null;
    private Long cachedAdminTokenExpiry = null;


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
    public String getAdminToken() {

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
