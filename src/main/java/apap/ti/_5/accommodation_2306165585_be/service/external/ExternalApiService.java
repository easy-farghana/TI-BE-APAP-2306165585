package apap.ti._5.accommodation_2306165585_be.service.external;

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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.http.MediaType;

import apap.ti._5.accommodation_2306165585_be.restdto.external.response.VerifyTokenResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.external.response.UserInfoResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ExternalApiService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${external.profile-service-url}")
    private String authServiceUrl;

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

    public VerifyTokenResponseDTO verifyToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("token", token);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<BaseResponseDTO<VerifyTokenResponseDTO>> response =
                restTemplate.exchange(
                    authServiceUrl + "/api/auth/verify",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {}
                );

        if (response != null && response.getBody() != null && response.getBody().getData() != null) {
                        return response.getBody().getData();
        }

        log.error("User profile response body is null for the given token");
        return null;
    }


    public UserInfoResponseDTO getUserDetail(UUID userId) {
        try {
            String url = authServiceUrl + "/api/profile/" + userId;
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
