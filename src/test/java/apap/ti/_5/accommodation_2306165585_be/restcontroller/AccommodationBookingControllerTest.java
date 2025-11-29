package apap.ti._5.accommodation_2306165585_be.restcontroller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;

import apap.ti._5.accommodation_2306165585_be.restdto.request.booking.BookingRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AccommodationBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AllBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.statistics.IncomeStatisticsDTO;
import apap.ti._5.accommodation_2306165585_be.service.booking.AccommodationBookingService;
import apap.ti._5.accommodation_2306165585_be.service.property.PropertyService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) 
public class AccommodationBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccommodationBookingService accommodationBookingService;

    @MockBean
    private PropertyService propertyService;
    

    @Autowired
    private ObjectMapper objectMapper;

    private AccommodationBookingResponseDTO bookingResponse;
    private AllBookingResponseDTO allBookingResponse;
    private BookingRequestDTO bookingRequest;
    private UUID bookingID;

   @BeforeEach
    void setUp() {
        bookingID = UUID.randomUUID();
        bookingResponse = new AccommodationBookingResponseDTO();
        bookingResponse.setBookingID(bookingID);
        bookingResponse.setCustomerName("William Cruise");
        bookingResponse.setTotalDays(2);
        bookingResponse.setTotalPrice(1250000);
        bookingResponse.setCheckInDate(LocalDateTime.now().plusDays(3));
        bookingResponse.setCheckOutDate(LocalDateTime.now().plusDays(5));

        allBookingResponse = new AllBookingResponseDTO();
        allBookingResponse.setPropertyName("Hotel Mewah Jakarta");

        bookingRequest = new BookingRequestDTO();
        bookingRequest.setRoomID(UUID.randomUUID());
        bookingRequest.setCheckInDate(LocalDateTime.now().plusDays(3));
        bookingRequest.setCheckOutDate(LocalDateTime.now().plusDays(5));
        bookingRequest.setCapacity(2);
        bookingRequest.setCustomerID(UUID.randomUUID());       
        bookingRequest.setCustomerName("William Cruise");      
        bookingRequest.setCustomerEmail("william@mail.com");   
        bookingRequest.setCustomerPhone("08123456789");        
        bookingRequest.setIsBreakfast(true);
    }

    @Test
    void testGetAllBookings_Success() throws Exception {
        List<AllBookingResponseDTO> mockList = Arrays.asList(allBookingResponse);
        when(accommodationBookingService.getAllAccommodationBookings()).thenReturn(mockList);

        mockMvc.perform(get("/api/booking")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("List of all accommodation bookings fetched successfully"))
                .andExpect(jsonPath("$.data[0].propertyName").value("Hotel Mewah Jakarta"));

        verify(accommodationBookingService, times(1)).getAllAccommodationBookings();
    }

    @Test
    void testGetBookingById_Success() throws Exception {
        UUID bookingID = UUID.randomUUID();
        when(accommodationBookingService.getAccommodationBookingById(bookingID))
                .thenReturn(bookingResponse);

        mockMvc.perform(get("/api/booking/{bookingID}", bookingID)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.customerName").value("William Cruise"));

        verify(accommodationBookingService, times(1))
                .getAccommodationBookingById(any(UUID.class));
    }


    @Test
    void testCreateBooking_Success() throws Exception {
        when(accommodationBookingService.createBooking(any())).thenReturn(bookingResponse);

        mockMvc.perform(post("/api/booking/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Accommodation booking created successfully"))
                .andExpect(jsonPath("$.data.customerName").value("William Cruise"));

        verify(accommodationBookingService, times(1)).createBooking(any());
    }

    @Test
    void testUpdateBooking_Success() throws Exception {
        when(accommodationBookingService.updateBooking(eq(bookingID), any()))
                .thenReturn(bookingResponse);

        mockMvc.perform(put("/api/booking/update/{bookingID}", bookingID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Accommodation booking updated successfully"));

        verify(accommodationBookingService, times(1)).updateBooking(eq(bookingID), any());
    }

    @Test
    void testUpdateBookingStatus_Success() throws Exception {
        when(accommodationBookingService.updateBookingStatus(bookingID))
                .thenReturn(bookingResponse);

        mockMvc.perform(put("/api/booking/update/status/{bookingID}", bookingID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Accommodation booking status updated successfully"));

        verify(accommodationBookingService, times(1)).updateBookingStatus(bookingID);
    }

    @Test
    void testCancelBooking_Success() throws Exception {
        when(accommodationBookingService.cancelBooking(bookingID)).thenReturn(bookingResponse);

        mockMvc.perform(post("/api/booking/cancel/{bookingID}", bookingID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Accommodation booking cancelled successfully"));

        verify(accommodationBookingService, times(1)).cancelBooking(bookingID);
    }

    @Test
    void testGetIncomeStatistics_Success() throws Exception {
        IncomeStatisticsDTO stats = new IncomeStatisticsDTO();
        stats.setPropertyNames(List.of("Hotel Mewah Jakarta"));
        stats.setPropertyIncomes(List.of(1250000));

        when(propertyService.getIncomeStatistics(anyInt(), anyInt())).thenReturn(stats);

        mockMvc.perform(get("/api/booking/chart")
                .param("month", "11")
                .param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Income statistics for 11/2025 fetched successfully"))
                .andExpect(jsonPath("$.data.propertyNames[0]").value("Hotel Mewah Jakarta"));

        verify(propertyService, times(1)).getIncomeStatistics(11, 2025);
    }
}
