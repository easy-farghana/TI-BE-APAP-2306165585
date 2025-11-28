package apap.ti._5.accommodation_2306165585_be.restcontroller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import apap.ti._5.accommodation_2306165585_be.restdto.request.booking.BookingRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AccommodationBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AllBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.statistics.IncomeStatisticsDTO;
import apap.ti._5.accommodation_2306165585_be.service.booking.AccommodationBookingService;
import apap.ti._5.accommodation_2306165585_be.service.property.PropertyService;

@SpringBootTest
@AutoConfigureMockMvc
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

    @BeforeEach
    void setUp() {
        bookingResponse = new AccommodationBookingResponseDTO();
        bookingResponse.setBookingID(UUID.randomUUID());
        bookingResponse.setCustomerName("William Cruise");
        bookingResponse.setTotalDays(2);
        bookingResponse.setTotalPrice(1250000);
        bookingResponse.setCheckInDate(LocalDateTime.now().plusDays(3));
        bookingResponse.setCheckOutDate(LocalDateTime.now().plusDays(5));

        allBookingResponse = new AllBookingResponseDTO();
        // allBookingResponse.setBookingId("B002");
        allBookingResponse.setPropertyName("Hotel Mewah Jakarta");

        bookingRequest = new BookingRequestDTO();
        bookingRequest.setCustomerName("William Cruise");
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
        when(accommodationBookingService.getAccommodationBookingById(bookingID)).thenReturn(bookingResponse);

        mockMvc.perform(get("/api/booking/{bookingID}", bookingID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bookingID").value("B001"))
                .andExpect(jsonPath("$.data.customerName").value("William Cruise"));

        verify(accommodationBookingService, times(1)).getAccommodationBookingById(bookingID);
    }

    @Test
    void testCreateBooking_Success() throws Exception {
        when(accommodationBookingService.createBooking(any(BookingRequestDTO.class))).thenReturn(bookingResponse);

        mockMvc.perform(post("/api/booking/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Accommodation booking created successfully"))
                .andExpect(jsonPath("$.data.bookingID").value("B001"));

        verify(accommodationBookingService, times(1)).createBooking(any(BookingRequestDTO.class));
    }

    @Test
    void testUpdateBooking_Success() throws Exception {
        UUID bookingID = UUID.randomUUID();
        when(accommodationBookingService.updateBooking(eq(bookingID), any(BookingRequestDTO.class))).thenReturn(bookingResponse);

        mockMvc.perform(put("/api/booking/update/{bookingID}", bookingID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Accommodation booking updated successfully"))
                .andExpect(jsonPath("$.data.bookingID").value("B001"));

        verify(accommodationBookingService, times(1)).updateBooking(eq(bookingID), any(BookingRequestDTO.class));
    }

    @Test
    void testPayBooking_Success() throws Exception {
        UUID bookingID = UUID.randomUUID();
        when(accommodationBookingService.updateBookingStatus(bookingID)).thenReturn(bookingResponse);

        mockMvc.perform(post("/api/booking/pay/{bookingID}", bookingID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Accommodation booking paid successfully"));

        verify(accommodationBookingService, times(1)).payBooking(bookingID);
    }

    @Test
    void testRefundBooking_Success() throws Exception {
        UUID bookingID = UUID.randomUUID();
        when(accommodationBookingService.giveRefund(bookingID)).thenReturn(bookingResponse);

        mockMvc.perform(post("/api/booking/refund/{bookingID}", bookingID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Accommodation booking refunded successfully"));

        verify(accommodationBookingService, times(1)).giveRefund(bookingID);
    }

    @Test
    void testCancelBooking_Success() throws Exception {
        UUID bookingID = UUID.randomUUID();
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
                .param("year", "2025")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Income statistics for 11/2025 fetched successfully"))
                .andExpect(jsonPath("$.data.propertyNames[0]").value("Hotel Mewah Jakarta"));

        verify(propertyService, times(1)).getIncomeStatistics(11, 2025);
    }
}
