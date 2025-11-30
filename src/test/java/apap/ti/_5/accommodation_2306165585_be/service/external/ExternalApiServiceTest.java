package apap.ti._5.accommodation_2306165585_be.service.external;

import apap.ti._5.accommodation_2306165585_be.restdto.external.request.CouponRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.external.response.LoginJwtResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.external.response.PolicyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.external.response.UserInfoResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.bill.BillRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.bill.BillResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AccommodationBookingResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    @InjectMocks
    private ExternalApiService externalApiService;

    private UUID userId;
    private String validToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        validToken = "Bearer valid.token.here";
        
        // Create a valid JWT token for testing (with exp claim)
        String header = Base64.getEncoder().encodeToString("{\"alg\":\"HS256\"}".getBytes());
        long futureExp = (System.currentTimeMillis() / 1000) + 3600; // 1 hour in future
        String payload = Base64.getEncoder().encodeToString(
            String.format("{\"exp\":%d,\"sub\":\"admin\"}", futureExp).getBytes()
        );
        String signature = Base64.getEncoder().encodeToString("signature".getBytes());
        adminToken = header + "." + payload + "." + signature;

        // Set field values using reflection
        ReflectionTestUtils.setField(externalApiService, "accommodationServiceUrl", "http://accommodation");
        ReflectionTestUtils.setField(externalApiService, "flightServiceUrl", "http://flight");
        ReflectionTestUtils.setField(externalApiService, "insuranceServiceUrl", "http://insurance");
        ReflectionTestUtils.setField(externalApiService, "tourServiceUrl", "http://tour");
        ReflectionTestUtils.setField(externalApiService, "rentalServiceUrl", "http://rental");
        ReflectionTestUtils.setField(externalApiService, "adminEmail", "admin@test.com");
        ReflectionTestUtils.setField(externalApiService, "adminPassword", "password");
        ReflectionTestUtils.setField(externalApiService, "apiKey", "test-api-key");
    }

    // ==================== getAdminToken Tests ====================

    @Test
    void testGetAdminToken_Success() {
        LoginJwtResponseDTO loginResponse = new LoginJwtResponseDTO();
        loginResponse.setToken(adminToken);

        BaseResponseDTO<LoginJwtResponseDTO> responseDTO = new BaseResponseDTO<>();
        responseDTO.setData(loginResponse);

        ResponseEntity<BaseResponseDTO<LoginJwtResponseDTO>> responseEntity = 
            new ResponseEntity<>(responseDTO, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Use reflection to call private method
        String token = (String) ReflectionTestUtils.invokeMethod(externalApiService, "getAdminToken");

        assertNotNull(token);
        assertEquals(adminToken, token);
    }

    @Test
    void testGetAdminToken_UsesCachedToken() {
        // Set cached token
        long futureExpiry = System.currentTimeMillis() + 3600000; // 1 hour in future
        ReflectionTestUtils.setField(externalApiService, "cachedAdminToken", adminToken);
        ReflectionTestUtils.setField(externalApiService, "cachedAdminTokenExpiry", futureExpiry);

        String token = (String) ReflectionTestUtils.invokeMethod(externalApiService, "getAdminToken");

        assertEquals(adminToken, token);
        verify(restTemplate, never()).exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
    }

    @Test
    void testGetAdminToken_RefreshesExpiredToken() {
        // Set expired cached token
        long pastExpiry = System.currentTimeMillis() - 1000;
        ReflectionTestUtils.setField(externalApiService, "cachedAdminToken", "old-token");
        ReflectionTestUtils.setField(externalApiService, "cachedAdminTokenExpiry", pastExpiry);

        LoginJwtResponseDTO loginResponse = new LoginJwtResponseDTO();
        loginResponse.setToken(adminToken);

        BaseResponseDTO<LoginJwtResponseDTO> responseDTO = new BaseResponseDTO<>();
        responseDTO.setData(loginResponse);

        ResponseEntity<BaseResponseDTO<LoginJwtResponseDTO>> responseEntity = 
            new ResponseEntity<>(responseDTO, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        String token = (String) ReflectionTestUtils.invokeMethod(externalApiService, "getAdminToken");

        assertEquals(adminToken, token);
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
    }

    // ==================== deductBalance Tests ====================

    @Test
    void testDeductBalance_Success() {
        Long userBalance = 1000000L;
        Long paymentAmount = 500000L;

        // Mock admin token retrieval
        LoginJwtResponseDTO loginResponse = new LoginJwtResponseDTO();
        loginResponse.setToken(adminToken);
        BaseResponseDTO<LoginJwtResponseDTO> loginResponseDTO = new BaseResponseDTO<>();
        loginResponseDTO.setData(loginResponse);
        ResponseEntity<BaseResponseDTO<LoginJwtResponseDTO>> loginEntity = 
            new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);

        // Mock user update
        UserInfoResponseDTO userInfo = new UserInfoResponseDTO();
        userInfo.setSaldo(500000L);
        BaseResponseDTO<UserInfoResponseDTO> userResponseDTO = new BaseResponseDTO<>();
        userResponseDTO.setData(userInfo);
        ResponseEntity<BaseResponseDTO<UserInfoResponseDTO>> userEntity = 
            new ResponseEntity<>(userResponseDTO, HttpStatus.OK);

        when(restTemplate.exchange(
            contains("/api/auth/login"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(loginEntity);

        when(restTemplate.exchange(
            contains("/api/users/"),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(userEntity);

        assertDoesNotThrow(() -> externalApiService.deductBalance(userId, userBalance, paymentAmount));

        verify(restTemplate, times(1)).exchange(
            contains("/api/users/"),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void testDeductBalance_AdminTokenNull() {
        ReflectionTestUtils.setField(externalApiService, "cachedAdminToken", null);

        LoginJwtResponseDTO loginResponse = new LoginJwtResponseDTO();
        loginResponse.setToken(null);
        BaseResponseDTO<LoginJwtResponseDTO> loginResponseDTO = new BaseResponseDTO<>();
        loginResponseDTO.setData(loginResponse);
        ResponseEntity<BaseResponseDTO<LoginJwtResponseDTO>> loginEntity = 
            new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);

        when(restTemplate.exchange(
            contains("/api/auth/login"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(loginEntity);


        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> externalApiService.deductBalance(userId, 1000000L, 500000L));
        
        assertEquals(IllegalStateException.class, exception.getCause().getClass());
        assertTrue(exception.getMessage().contains("Failed to deduct balance"));
    }

    @Test
    void testDeductBalance_ServiceReturnsError() {
        LoginJwtResponseDTO loginResponse = new LoginJwtResponseDTO();
        loginResponse.setToken(adminToken);
        BaseResponseDTO<LoginJwtResponseDTO> loginResponseDTO = new BaseResponseDTO<>();
        loginResponseDTO.setData(loginResponse);
        ResponseEntity<BaseResponseDTO<LoginJwtResponseDTO>> loginEntity = 
            new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);

        when(restTemplate.exchange(
            contains("/api/auth/login"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(loginEntity);

        ResponseEntity<BaseResponseDTO<UserInfoResponseDTO>> errorEntity = 
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.exchange(
            contains("/api/users/"),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(errorEntity);

        assertThrows(RuntimeException.class, 
            () -> externalApiService.deductBalance(userId, 1000000L, 500000L));
    }

    @Test
    void testDeductBalance_NullResponseBody() {
        LoginJwtResponseDTO loginResponse = new LoginJwtResponseDTO();
        loginResponse.setToken(adminToken);
        BaseResponseDTO<LoginJwtResponseDTO> loginResponseDTO = new BaseResponseDTO<>();
        loginResponseDTO.setData(loginResponse);
        ResponseEntity<BaseResponseDTO<LoginJwtResponseDTO>> loginEntity = 
            new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);

        when(restTemplate.exchange(
            contains("/api/auth/login"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(loginEntity);

        ResponseEntity<BaseResponseDTO<UserInfoResponseDTO>> nullBodyEntity = 
            new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
            contains("/api/users/"),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(nullBodyEntity);

        assertThrows(RuntimeException.class, 
            () -> externalApiService.deductBalance(userId, 1000000L, 500000L));
    }

    @Test
    void testDeductBalance_NullDataField() {
        LoginJwtResponseDTO loginResponse = new LoginJwtResponseDTO();
        loginResponse.setToken(adminToken);
        BaseResponseDTO<LoginJwtResponseDTO> loginResponseDTO = new BaseResponseDTO<>();
        loginResponseDTO.setData(loginResponse);
        ResponseEntity<BaseResponseDTO<LoginJwtResponseDTO>> loginEntity = 
            new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);

        BaseResponseDTO<UserInfoResponseDTO> userResponseDTO = new BaseResponseDTO<>();
        userResponseDTO.setData(null);
        ResponseEntity<BaseResponseDTO<UserInfoResponseDTO>> userEntity = 
            new ResponseEntity<>(userResponseDTO, HttpStatus.OK);

        when(restTemplate.exchange(
            contains("/api/auth/login"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(loginEntity);

        when(restTemplate.exchange(
            contains("/api/users/"),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(userEntity);

        assertThrows(RuntimeException.class, 
            () -> externalApiService.deductBalance(userId, 1000000L, 500000L));
    }

    @Test
    void testDeductBalance_HttpClientErrorException() {
        LoginJwtResponseDTO loginResponse = new LoginJwtResponseDTO();
        loginResponse.setToken(adminToken);
        BaseResponseDTO<LoginJwtResponseDTO> loginResponseDTO = new BaseResponseDTO<>();
        loginResponseDTO.setData(loginResponse);
        ResponseEntity<BaseResponseDTO<LoginJwtResponseDTO>> loginEntity = 
            new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);

        when(restTemplate.exchange(
            contains("/api/auth/login"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(loginEntity);

        when(restTemplate.exchange(
            contains("/api/users/"),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(HttpClientErrorException.class, 
            () -> externalApiService.deductBalance(userId, 1000000L, 500000L));
    }

    @Test
    void testDeductBalance_UnexpectedException() {
        LoginJwtResponseDTO loginResponse = new LoginJwtResponseDTO();
        loginResponse.setToken(adminToken);
        BaseResponseDTO<LoginJwtResponseDTO> loginResponseDTO = new BaseResponseDTO<>();
        loginResponseDTO.setData(loginResponse);
        ResponseEntity<BaseResponseDTO<LoginJwtResponseDTO>> loginEntity = 
            new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);

        when(restTemplate.exchange(
            contains("/api/auth/login"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(loginEntity);

        when(restTemplate.exchange(
            contains("/api/users/"),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(RuntimeException.class, 
            () -> externalApiService.deductBalance(userId, 1000000L, 500000L));
    }

    // ==================== updateServicesBookingStatus Tests ====================

    @Test
    void testUpdateServicesBookingStatus_Accommodation() {
        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);
            when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("Authorization")).thenReturn(validToken);

            AccommodationBookingResponseDTO bookingResponse = new AccommodationBookingResponseDTO();
            BaseResponseDTO<AccommodationBookingResponseDTO> responseDTO = new BaseResponseDTO<>();
            responseDTO.setData(bookingResponse);
            ResponseEntity<BaseResponseDTO<AccommodationBookingResponseDTO>> responseEntity = 
                new ResponseEntity<>(responseDTO, HttpStatus.OK);

            when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
            )).thenReturn(responseEntity);

            assertDoesNotThrow(() -> 
                externalApiService.updateServicesBookingStatus("Accommodation", "ref123"));
        }
    }

    @Test
    void testUpdateServicesBookingStatus_Flight() {
        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);
            when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("Authorization")).thenReturn(validToken);

            BaseResponseDTO<Map<String, Object>> response = new BaseResponseDTO<>();
            ResponseEntity<BaseResponseDTO<Map<String, Object>>> responseEntity = 
                new ResponseEntity<>(response, HttpStatus.OK);

            when(restTemplate.exchange(
                contains("/api/bookings/"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
            )).thenReturn(responseEntity);

            assertDoesNotThrow(() -> 
                externalApiService.updateServicesBookingStatus("Flight", "ref123"));
            
            verify(restTemplate).exchange(
                contains("/api/bookings/ref123/status"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
            );
        }
    }

    @Test
    void testUpdateFlightBookingStatus_Success() {
        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);
            when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("Authorization")).thenReturn(validToken);

            BaseResponseDTO<Map<String, Object>> response = new BaseResponseDTO<>();
            ResponseEntity<BaseResponseDTO<Map<String, Object>>> responseEntity = 
                new ResponseEntity<>(response, HttpStatus.OK);

            when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
            )).thenReturn(responseEntity);

            assertDoesNotThrow(() -> 
                externalApiService.updateFlightBookingStatus("ref123"));
        }
    }

    @Test
    void testUpdateFlightBookingStatus_HttpClientError() {
        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);
            when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("Authorization")).thenReturn(validToken);

            when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
            )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

            IllegalStateException exception = assertThrows(IllegalStateException.class, 
                () -> externalApiService.updateFlightBookingStatus("ref123"));
            
            assertTrue(exception.getMessage().contains("Failed to update Flight booking status"));
        }
    }

    @Test
    void testUpdateServicesBookingStatus_Insurance() {
        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);
            when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("Authorization")).thenReturn(validToken);

            BaseResponseDTO<PolicyResponseDTO> responseDTO = new BaseResponseDTO<>();
            ResponseEntity<BaseResponseDTO<PolicyResponseDTO>> responseEntity = 
                new ResponseEntity<>(responseDTO, HttpStatus.OK);

            when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
            )).thenReturn(responseEntity);

            assertDoesNotThrow(() -> 
                externalApiService.updateServicesBookingStatus("Insurance", "ref123"));
        }
    }

    @Test
    void testUpdateServicesBookingStatus_TourPackage() {
        assertDoesNotThrow(() -> 
            externalApiService.updateServicesBookingStatus("TourPackage", "ref123"));
    }

    @Test
    void testUpdateServicesBookingStatus_UnknownService() {
        assertThrows(IllegalArgumentException.class, () -> 
            externalApiService.updateServicesBookingStatus("UnknownService", "ref123"));
    }

    // ==================== updateAccommodationBookingStatus Tests ====================

    @Test
    void testUpdateAccommodationBookingStatus_Success() {
        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);
            when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("Authorization")).thenReturn(validToken);

            AccommodationBookingResponseDTO bookingResponse = new AccommodationBookingResponseDTO();
            BaseResponseDTO<AccommodationBookingResponseDTO> responseDTO = new BaseResponseDTO<>();
            responseDTO.setData(bookingResponse);
            ResponseEntity<BaseResponseDTO<AccommodationBookingResponseDTO>> responseEntity = 
                new ResponseEntity<>(responseDTO, HttpStatus.OK);

            when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
            )).thenReturn(responseEntity);

            AccommodationBookingResponseDTO result = 
                externalApiService.updateAccommodationBookingStatus("ref123");

            assertNotNull(result);
            assertEquals(bookingResponse, result);
        }
    }

    @Test
    void testUpdateAccommodationBookingStatus_ErrorStatus() {
        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);
            when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("Authorization")).thenReturn(validToken);

            ResponseEntity<BaseResponseDTO<AccommodationBookingResponseDTO>> errorEntity = 
                new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

            when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
            )).thenReturn(errorEntity);

            assertThrows(IllegalStateException.class, 
                () -> externalApiService.updateAccommodationBookingStatus("ref123"));
        }
    }



    // ==================== updateInsurancePolicyStatus Tests ====================

    @Test
    void testUpdateInsurancePolicyStatus_Success() {
        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);
            when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("Authorization")).thenReturn(validToken);

            BaseResponseDTO<PolicyResponseDTO> responseDTO = new BaseResponseDTO<>();
            ResponseEntity<BaseResponseDTO<PolicyResponseDTO>> responseEntity = 
                new ResponseEntity<>(responseDTO, HttpStatus.OK);

            when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
            )).thenReturn(responseEntity);

            assertDoesNotThrow(() -> externalApiService.updateInsurancePolicyStatus("ref123"));
        }
    }

    @Test
    void testUpdateInsurancePolicyStatus_HttpClientError() {
        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);
            when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("Authorization")).thenReturn(validToken);

            when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
            )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

            assertThrows(IllegalStateException.class, 
                () -> externalApiService.updateInsurancePolicyStatus("ref123"));
        }
    }

    // ==================== updateFlightBookingStatus Tests ====================


    // ==================== checkRentalStatus Tests ====================

    @Test
    void testCheckRentalStatus_Done() {
        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);
            when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("Authorization")).thenReturn(validToken);

            Map<String, Object> data = new HashMap<>();
            data.put("status", "Done");

            BaseResponseDTO<Map<String, Object>> responseDTO = new BaseResponseDTO<>();
            responseDTO.setData(data);
            ResponseEntity<BaseResponseDTO<Map<String, Object>>> responseEntity = 
                new ResponseEntity<>(responseDTO, HttpStatus.OK);

            when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
            )).thenReturn(responseEntity);

            boolean result = externalApiService.checkRentalStatus("ref123");

            assertTrue(result);
        }
    }

    @Test
    void testCheckRentalStatus_NotDone() {
        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);
            when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("Authorization")).thenReturn(validToken);

            Map<String, Object> data = new HashMap<>();
            data.put("status", "Pending");

            BaseResponseDTO<Map<String, Object>> responseDTO = new BaseResponseDTO<>();
            responseDTO.setData(data);
            ResponseEntity<BaseResponseDTO<Map<String, Object>>> responseEntity = 
                new ResponseEntity<>(responseDTO, HttpStatus.OK);

            when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
            )).thenReturn(responseEntity);

            boolean result = externalApiService.checkRentalStatus("ref123");

            assertFalse(result);
        }
    }

    @Test
    void testCheckRentalStatus_HttpClientError() {
        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);
            when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("Authorization")).thenReturn(validToken);

            when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
            )).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

            assertThrows(IllegalStateException.class, 
                () -> externalApiService.checkRentalStatus("ref123"));
        }
    }

    // ==================== calculateDiscount Tests ====================

    @Test
    void testCalculateDiscount_Success() {
        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);
            when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("Authorization")).thenReturn(validToken);

            Map<String, Object> data = new HashMap<>();
            data.put("percentOff", 20);

            BaseResponseDTO<Map<String, Object>> responseDTO = new BaseResponseDTO<>();
            responseDTO.setData(data);
            ResponseEntity<BaseResponseDTO<Map<String, Object>>> responseEntity = 
                new ResponseEntity<>(responseDTO, HttpStatus.OK);

            when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
            )).thenReturn(responseEntity);

            int discount = externalApiService.calculateDiscount("COUPON20", userId);

            assertEquals(20, discount);
        }
    }

    @Test
    void testCalculateDiscount_HttpClientError() {
        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);
            when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("Authorization")).thenReturn(validToken);

            when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
            )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

            assertThrows(IllegalStateException.class, 
                () -> externalApiService.calculateDiscount("INVALID", userId));
        }
    }
}