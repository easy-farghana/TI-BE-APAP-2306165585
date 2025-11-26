package apap.ti._5.accommodation_2306165585_be.restcontroller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AllBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.AllPropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.booking.AccommodationBookingService;
import apap.ti._5.accommodation_2306165585_be.service.property.PropertyService;

@SpringBootTest
@AutoConfigureMockMvc
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropertyService propertyService;

    @MockBean
    private AccommodationBookingService accommodationBookingService;

    private AllPropertyResponseDTO property1;
    private AllPropertyResponseDTO property2;
    private AllPropertyResponseDTO property3;
    private AllBookingResponseDTO booking1;
    private AllBookingResponseDTO booking2;
    private UUID id1;
    private UUID id2;
    private UUID id3;

    @BeforeEach
    void setUp() {

        id1 = UUID.randomUUID();
        id2 = UUID.randomUUID();
        id3 = UUID.randomUUID();
        // Setup test data for properties
        property1 = AllPropertyResponseDTO.builder()
                .propertyID(id1)
                .propertyName("Hotel Paradise")
                .type(1)
                .totalRooms(50)
                .activeStatus(1)
                .build();

        property2 = AllPropertyResponseDTO.builder()
                .propertyID(id2)
                .propertyName("Beach Resort")
                .type(2)
                .totalRooms(30)
                .activeStatus(1)
                .build();

        property3 = AllPropertyResponseDTO.builder()
                .propertyID(id3)
                .propertyName("Mountain Lodge")
                .type(1)
                .totalRooms(20)
                .activeStatus(0)
                .build();

        // Setup test data for bookings
        booking1 = new AllBookingResponseDTO();
        // Set necessary fields for booking1
        
        booking2 = new AllBookingResponseDTO();
        // Set necessary fields for booking2
    }

    @Test
    void testGetStatistics_WithMultiplePropertiesAndBookings() throws Exception {
        // Arrange
        List<AllPropertyResponseDTO> properties = Arrays.asList(property1, property2, property3);
        List<AllBookingResponseDTO> bookings = Arrays.asList(booking1, booking2);
        
        when(propertyService.getAllProperties()).thenReturn(properties);
        when(accommodationBookingService.getAllAccommodationBookings()).thenReturn(bookings);

        // Act & Assert
        mockMvc.perform(get("/api/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Statistics fetched successfully"))
                .andExpect(jsonPath("$.data.totalProperties").value(3))
                .andExpect(jsonPath("$.data.totalBookings").value(2));

        verify(propertyService, times(1)).getAllProperties();
        verify(accommodationBookingService, times(1)).getAllAccommodationBookings();
    }

    @Test
    void testGetStatistics_WithNoPropertiesAndNoBookings() throws Exception {
        // Arrange
        when(propertyService.getAllProperties()).thenReturn(Arrays.asList());
        when(accommodationBookingService.getAllAccommodationBookings()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Statistics fetched successfully"))
                .andExpect(jsonPath("$.data.totalProperties").value(0))
                .andExpect(jsonPath("$.data.totalBookings").value(0));

        verify(propertyService, times(1)).getAllProperties();
        verify(accommodationBookingService, times(1)).getAllAccommodationBookings();
    }

    @Test
    void testGetStatistics_WithOnlyProperties() throws Exception {
        // Arrange
        List<AllPropertyResponseDTO> properties = Arrays.asList(property1, property2);
        
        when(propertyService.getAllProperties()).thenReturn(properties);
        when(accommodationBookingService.getAllAccommodationBookings()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Statistics fetched successfully"))
                .andExpect(jsonPath("$.data.totalProperties").value(2))
                .andExpect(jsonPath("$.data.totalBookings").value(0));

        verify(propertyService, times(1)).getAllProperties();
        verify(accommodationBookingService, times(1)).getAllAccommodationBookings();
    }

    @Test
    void testGetStatistics_WithOnlyBookings() throws Exception {
        // Arrange
        List<AllBookingResponseDTO> bookings = Arrays.asList(booking1, booking2);
        
        when(propertyService.getAllProperties()).thenReturn(Arrays.asList());
        when(accommodationBookingService.getAllAccommodationBookings()).thenReturn(bookings);

        // Act & Assert
        mockMvc.perform(get("/api/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Statistics fetched successfully"))
                .andExpect(jsonPath("$.data.totalProperties").value(0))
                .andExpect(jsonPath("$.data.totalBookings").value(2));

        verify(propertyService, times(1)).getAllProperties();
        verify(accommodationBookingService, times(1)).getAllAccommodationBookings();
    }

    @Test
    void testGetStatistics_WithSinglePropertyAndBooking() throws Exception {
        // Arrange
        List<AllPropertyResponseDTO> properties = Arrays.asList(property1);
        List<AllBookingResponseDTO> bookings = Arrays.asList(booking1);
        
        when(propertyService.getAllProperties()).thenReturn(properties);
        when(accommodationBookingService.getAllAccommodationBookings()).thenReturn(bookings);

        // Act & Assert
        mockMvc.perform(get("/api/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Statistics fetched successfully"))
                .andExpect(jsonPath("$.data.totalProperties").value(1))
                .andExpect(jsonPath("$.data.totalBookings").value(1));

        verify(propertyService, times(1)).getAllProperties();
        verify(accommodationBookingService, times(1)).getAllAccommodationBookings();
    }
}