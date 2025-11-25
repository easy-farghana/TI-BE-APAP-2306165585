package apap.ti._5.accommodation_2306165585_be.security;

import apap.ti._5.accommodation_2306165585_be.restdto.external.response.VerifyTokenResponseDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import org.springframework.http.HttpHeaders;

import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${external.auth-service-url}")
    private String authServiceUrl;

    /**
     * Verify JWT token validity
     * @param token JWT token to verify
     * @return VerifyTokenResponseDTO containing token verification result
     */
    public VerifyTokenResponseDTO verifyToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("token", token);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<BaseResponseDTO<VerifyTokenResponseDTO>> response =
                restTemplate.exchange(
                    authServiceUrl + "/api/auth/verify",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {}
                );
        
            return response.getBody().getData();
        } catch (HttpClientErrorException.Unauthorized e) {
            log.warn("Token not valid or expired: " + e.getMessage());
            throw new SecurityException("Token not valid or expired: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error verifying token: " + e.getMessage(), e);
            throw new SecurityException("Token not valid or expired: " + e.getMessage());
        }
    }
}
