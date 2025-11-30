package apap.ti._5.accommodation_2306165585_be.restcontroller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
@AutoConfigureMockMvc(addFilters = false)
public class AccommodationBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccommodationBookingService accommodationBookingService;

    @MockBean
    private PropertyService propertyService;

    private static final LocalDateTime FIXED_CHECKIN = LocalDateTime.of(2025, 1, 10, 12, 0);
    private static final LocalDateTime FIXED_CHECKOUT = LocalDateTime.of(2025, 1, 12, 12, 0);

    @Autowired
    private ObjectMapper objectMapper;

    private UUID bookingId;
    private UUID customerId;
    private UUID roomId;
    private AccommodationBookingResponseDTO bookingResponse;
    private AllBookingResponseDTO allBookingResponse;
    private BookingRequestDTO bookingRequest;

    @BeforeEach
    void setUp() {
        bookingId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        roomId = UUID.randomUUID();

        bookingResponse = new AccommodationBookingResponseDTO();
        bookingResponse.setBookingID(bookingId);
        bookingResponse.setCustomerID(customerId);
        bookingResponse.setRoomID(roomId);
        bookingResponse.setCustomerName("William Cruise");
        bookingResponse.setCustomerEmail("william@mail.com");
        bookingResponse.setCustomerPhone("08123456789");
        bookingResponse.setCapacity(2);
        bookingResponse.setBreakfast(true);
        bookingResponse.setTotalDays(2);
        bookingResponse.setTotalPrice(1250000);
        bookingResponse.setStatus(0);
        bookingResponse.setCheckInDate(FIXED_CHECKIN);
        bookingResponse.setCheckOutDate(FIXED_CHECKOUT);
        bookingResponse.setRoomName("Deluxe Room 101");
        bookingResponse.setPropertyName("Hotel Mewah Jakarta");

        allBookingResponse = new AllBookingResponseDTO();
        allBookingResponse.setBookingID(bookingId);
        allBookingResponse.setPropertyName("Hotel Mewah Jakarta");
        allBookingResponse.setRoomName("Deluxe Room 101");
        allBookingResponse.setStatus(0);
        allBookingResponse.setTotalPrice(1250000);
        allBookingResponse.setCheckInDate(FIXED_CHECKIN);
        allBookingResponse.setCheckOutDate(FIXED_CHECKOUT);

        bookingRequest = new BookingRequestDTO();
        bookingRequest.setRoomID(roomId);
        bookingRequest.setCheckInDate(FIXED_CHECKIN);
        bookingRequest.setCheckOutDate(FIXED_CHECKOUT);
        bookingRequest.setCapacity(2);
        bookingRequest.setCustomerID(customerId);
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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("List of all accommodation bookings fetched successfully"))
                .andExpect(jsonPath("$.data[0].propertyName").value("Hotel Mewah Jakarta"))
                .andExpect(jsonPath("$.data[0].bookingID").value(bookingId.toString()));

        verify(accommodationBookingService, times(1)).getAllAccommodationBookings();
    }

    @Test
    void testGetAllBookings_EmptyList() throws Exception {
        when(accommodationBookingService.getAllAccommodationBookings()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/booking")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(accommodationBookingService, times(1)).getAllAccommodationBookings();
    }

    @Test
    void testGetBookingById_Success() throws Exception {
        when(accommodationBookingService.getAccommodationBookingById(bookingId))
                .thenReturn(bookingResponse);

        mockMvc.perform(get("/api/booking/{bookingID}", bookingId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Accommodation booking fetched successfully"))
                .andExpect(jsonPath("$.data.bookingID").value(bookingId.toString()))
                .andExpect(jsonPath("$.data.customerName").value("William Cruise"))
                .andExpect(jsonPath("$.data.capacity").value(2));

        verify(accommodationBookingService, times(1)).getAccommodationBookingById(bookingId);
    }

    @Test
    void testCreateBooking_Success() throws Exception {
        when(accommodationBookingService.createBooking(any(BookingRequestDTO.class)))
                .thenReturn(bookingResponse);

        mockMvc.perform(post("/api/booking/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Accommodation booking created successfully"))
                .andExpect(jsonPath("$.data.bookingID").value(bookingId.toString()))
                .andExpect(jsonPath("$.data.customerName").value("William Cruise"))
                .andExpect(jsonPath("$.data.totalPrice").value(1250000));

        verify(accommodationBookingService, times(1)).createBooking(any(BookingRequestDTO.class));
    }

    @Test
    void testUpdateBooking_Success() throws Exception {
        bookingResponse.setCapacity(3);
        bookingRequest.setCapacity(3);

        when(accommodationBookingService.updateBooking(eq(bookingId), any(BookingRequestDTO.class)))
                .thenReturn(bookingResponse);

        mockMvc.perform(put("/api/booking/update/{bookingID}", bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Accommodation booking updated successfully"))
                .andExpect(jsonPath("$.data.bookingID").value(bookingId.toString()))
                .andExpect(jsonPath("$.data.capacity").value(3));

        verify(accommodationBookingService, times(1)).updateBooking(eq(bookingId), any(BookingRequestDTO.class));
    }

    @Test
    void testUpdateBookingStatus_Success() throws Exception {
        bookingResponse.setStatus(1);
        when(accommodationBookingService.updateBookingStatus(bookingId))
                .thenReturn(bookingResponse);

        mockMvc.perform(put("/api/booking/update/status/{bookingID}", bookingId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Accommodation booking status updated successfully"))
                .andExpect(jsonPath("$.data.status").value(1));

        verify(accommodationBookingService, times(1)).updateBookingStatus(bookingId);
    }

    @Test
    void testCancelBooking_Success() throws Exception {
        bookingResponse.setStatus(2);
        when(accommodationBookingService.cancelBooking(bookingId))
                .thenReturn(bookingResponse);

        mockMvc.perform(post("/api/booking/cancel/{bookingID}", bookingId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Accommodation booking cancelled successfully"))
                .andExpect(jsonPath("$.data.bookingID").value(bookingId.toString()))
                .andExpect(jsonPath("$.data.status").value(2));

        verify(accommodationBookingService, times(1)).cancelBooking(bookingId);
    }

//     @Test
//     void testGetIncomeStatistics_WithParameters() throws Exception {
//         IncomeStatisticsDTO stats = new IncomeStatisticsDTO();
//         stats.setPropertyNames(List.of("Hotel Mewah Jakarta", "Hotel Santika Bandung"));
//         stats.setPropertyIncomes(List.of(1250000, 850000));

//         when(propertyService.getIncomeStatistics(11, 2025)).thenReturn(stats);

//         mockMvc.perform(get("/api/booking/chart")
//                 .param("month", "11")
//                 .param("year", "2025")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andDo(print())
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.message").value("Income statistics for 11/2025 fetched successfully"))
//                 .andExpect(jsonPath("$.data.propertyNames[0]").value("Hotel Mewah Jakarta"))
//                 .andExpect(jsonPath("$.data.propertyIncomes[0]").value(1250000));

//         verify(propertyService, times(1)).getIncomeStatistics(11, 2025);
//     }

//     @Test
//     void testGetIncomeStatistics_WithoutParameters() throws Exception {
//         LocalDate now = LocalDate.now();
//         int currentMonth = now.getMonthValue();
//         int currentYear = now.getYear();

//         IncomeStatisticsDTO stats = new IncomeStatisticsDTO();
//         stats.setPropertyNames(List.of("Hotel Mewah Jakarta"));
//         stats.setPropertyIncomes(List.of(500000));

//         when(propertyService.getIncomeStatistics(currentMonth, currentYear)).thenReturn(stats);

//         mockMvc.perform(get("/api/booking/chart")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andDo(print())
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.data.propertyNames[0]").value("Hotel Mewah Jakarta"));

//         verify(propertyService, times(1)).getIncomeStatistics(currentMonth, currentYear);
//     }

//     @Test
//     void testGetIncomeStatistics_WithOnlyMonth() throws Exception {
//         int currentYear = LocalDate.now().getYear();
//         IncomeStatisticsDTO stats = new IncomeStatisticsDTO();
//         stats.setPropertyNames(List.of("Hotel Test"));
//         stats.setPropertyIncomes(List.of(300000));

//         when(propertyService.getIncomeStatistics(6, currentYear)).thenReturn(stats);

//         mockMvc.perform(get("/api/booking/chart")
//                 .param("month", "6")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.data.propertyNames[0]").value("Hotel Test"));

//         verify(propertyService, times(1)).getIncomeStatistics(6, currentYear);
//     }

//     @Test
//     void testGetIncomeStatistics_WithOnlyYear() throws Exception {
//         int currentMonth = LocalDate.now().getMonthValue();
//         IncomeStatisticsDTO stats = new IncomeStatisticsDTO();
//         stats.setPropertyNames(List.of("Hotel 2024"));
//         stats.setPropertyIncomes(List.of(800000));

//         when(propertyService.getIncomeStatistics(currentMonth, 2024)).thenReturn(stats);

//         mockMvc.perform(get("/api/booking/chart")
//                 .param("year", "2024")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.data.propertyNames[0]").value("Hotel 2024"));

//         verify(propertyService, times(1)).getIncomeStatistics(currentMonth, 2024);
//     }

    @Test
    void testGetAllBookings_MultipleBookings() throws Exception {
        AllBookingResponseDTO booking2 = new AllBookingResponseDTO();
        booking2.setBookingID(UUID.randomUUID());
        booking2.setPropertyName("Hotel Santika Bandung");
        booking2.setStatus(1);

        List<AllBookingResponseDTO> mockList = Arrays.asList(allBookingResponse, booking2);
        when(accommodationBookingService.getAllAccommodationBookings()).thenReturn(mockList);

        mockMvc.perform(get("/api/booking")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].propertyName").value("Hotel Mewah Jakarta"))
                .andExpect(jsonPath("$.data[1].propertyName").value("Hotel Santika Bandung"));

        verify(accommodationBookingService, times(1)).getAllAccommodationBookings();
    }
}