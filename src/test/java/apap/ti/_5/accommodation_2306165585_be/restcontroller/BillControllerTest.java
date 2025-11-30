package apap.ti._5.accommodation_2306165585_be.restcontroller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
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

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.restdto.request.bill.BillCouponDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.bill.BillRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.bill.BillResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.bill.BillService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class BillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BillService billService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID billId;
    private UUID customerId;
    private BillResponseDTO billResponse;
    private BillRequestDTO billRequest;

    @BeforeEach
    void setUp() {
        billId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        // Setup BillResponseDTO
        billResponse = BillResponseDTO.builder()
                .billID(billId)
                .customerID(customerId)
                .serviceName("Accommodation")
                .serviceReferenceID("REF123")
                .description("Hotel booking payment")
                .status(0)
                .amount(1000000L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .paymentTimestamp(null)
                .build();

        // Setup BillRequestDTO
        billRequest = new BillRequestDTO();
        billRequest.setCustomerID(customerId);
        billRequest.setServiceName("Accommodation");
        billRequest.setServiceReferenceID("REF123");
        billRequest.setDescription("Hotel booking payment");
        billRequest.setAmount(1000000L);
    }

    // Test getAllBills - Success without filters
    @Test
    void testGetAllBills_SuccessWithoutFilters() throws Exception {
        List<BillResponseDTO> mockList = Arrays.asList(billResponse);
        when(billService.getAllBills(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/bill")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Get all bills successfully"))
                .andExpect(jsonPath("$.data[0].billID").value(billId.toString()))
                .andExpect(jsonPath("$.data[0].serviceName").value("Accommodation"))
                .andExpect(jsonPath("$.data[0].amount").value(1000000));

        verify(billService, times(1)).getAllBills(anyMap());
    }

    // Test getAllBills - With all filters
    @Test
    void testGetAllBills_WithAllFilters() throws Exception {
        List<BillResponseDTO> mockList = Arrays.asList(billResponse);
        when(billService.getAllBills(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/bill")
                .param("customerID", customerId.toString())
                .param("serviceName", "Accommodation")
                .param("status", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].customerID").value(customerId.toString()))
                .andExpect(jsonPath("$.data[0].serviceName").value("Accommodation"));

        verify(billService, times(1)).getAllBills(anyMap());
    }

    // Test getAllBills - With customerID filter only
    @Test
    void testGetAllBills_WithCustomerIDFilter() throws Exception {
        List<BillResponseDTO> mockList = Arrays.asList(billResponse);
        when(billService.getAllBills(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/bill")
                .param("customerID", customerId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].customerID").value(customerId.toString()));

        verify(billService, times(1)).getAllBills(anyMap());
    }

    // Test getAllBills - With serviceName filter only
    @Test
    void testGetAllBills_WithServiceNameFilter() throws Exception {
        List<BillResponseDTO> mockList = Arrays.asList(billResponse);
        when(billService.getAllBills(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/bill")
                .param("serviceName", "Accommodation")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].serviceName").value("Accommodation"));

        verify(billService, times(1)).getAllBills(anyMap());
    }

    // Test getAllBills - With status filter only
    @Test
    void testGetAllBills_WithStatusFilter() throws Exception {
        List<BillResponseDTO> mockList = Arrays.asList(billResponse);
        when(billService.getAllBills(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/bill")
                .param("status", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].status").value(0));

        verify(billService, times(1)).getAllBills(anyMap());
    }

    // Test getAllBills - Empty list
    @Test
    void testGetAllBills_EmptyList() throws Exception {
        when(billService.getAllBills(anyMap())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/bill")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(billService, times(1)).getAllBills(anyMap());
    }

    // Test getAllBills - Multiple bills
    @Test
    void testGetAllBills_MultipleBills() throws Exception {
        BillResponseDTO bill2 = BillResponseDTO.builder()
                .billID(UUID.randomUUID())
                .customerID(UUID.randomUUID())
                .serviceName("Flight")
                .status(1)
                .amount(2000000L)
                .build();

        List<BillResponseDTO> mockList = Arrays.asList(billResponse, bill2);
        when(billService.getAllBills(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/bill")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].serviceName").value("Accommodation"))
                .andExpect(jsonPath("$.data[1].serviceName").value("Flight"));

        verify(billService, times(1)).getAllBills(anyMap());
    }

    // Test getCustomerBills - Success with default params
    @Test
    void testGetCustomerBills_SuccessWithDefaults() throws Exception {
        List<BillResponseDTO> mockList = Arrays.asList(billResponse);
        when(billService.getCustomerBills(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/bill/customer")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Fetched all customer's bills successfully"))
                .andExpect(jsonPath("$.data[0].billID").value(billId.toString()));

        verify(billService, times(1)).getCustomerBills(anyMap());
    }

    // Test getCustomerBills - With status filter
    @Test
    void testGetCustomerBills_WithStatusFilter() throws Exception {
        List<BillResponseDTO> mockList = Arrays.asList(billResponse);
        when(billService.getCustomerBills(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/bill/customer")
                .param("status", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].status").value(0));

        verify(billService, times(1)).getCustomerBills(anyMap());
    }

    // Test getCustomerBills - With custom sort
    @Test
    void testGetCustomerBills_WithCustomSort() throws Exception {
        List<BillResponseDTO> mockList = Arrays.asList(billResponse);
        when(billService.getCustomerBills(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/bill/customer")
                .param("sortBy", "amount")
                .param("sortDir", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        verify(billService, times(1)).getCustomerBills(anyMap());
    }

    // Test getCustomerBills - With all parameters
    @Test
    void testGetCustomerBills_WithAllParameters() throws Exception {
        List<BillResponseDTO> mockList = Arrays.asList(billResponse);
        when(billService.getCustomerBills(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/bill/customer")
                .param("status", "1")
                .param("sortBy", "createdAt")
                .param("sortDir", "desc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(billService, times(1)).getCustomerBills(anyMap());
    }

    // Test getServiceBills - Success
    @Test
    void testGetServiceBills_Success() throws Exception {
        List<BillResponseDTO> mockList = Arrays.asList(billResponse);
        when(billService.getServiceBills(anyMap(), eq("Accommodation"))).thenReturn(mockList);

        mockMvc.perform(get("/api/bill/{serviceName}", "Accommodation")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Fetched Accommodationbills successfully"))
                .andExpect(jsonPath("$.data[0].serviceName").value("Accommodation"));

        verify(billService, times(1)).getServiceBills(anyMap(), eq("Accommodation"));
    }

    // Test getServiceBills - With status filter
    @Test
    void testGetServiceBills_WithStatusFilter() throws Exception {
        List<BillResponseDTO> mockList = Arrays.asList(billResponse);
        when(billService.getServiceBills(anyMap(), eq("Flight"))).thenReturn(mockList);

        mockMvc.perform(get("/api/bill/{serviceName}", "Flight")
                .param("status", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(billService, times(1)).getServiceBills(anyMap(), eq("Flight"));
    }

    // Test getServiceBills - With customerID filter
    @Test
    void testGetServiceBills_WithCustomerIDFilter() throws Exception {
        List<BillResponseDTO> mockList = Arrays.asList(billResponse);
        when(billService.getServiceBills(anyMap(), eq("Accommodation"))).thenReturn(mockList);

        mockMvc.perform(get("/api/bill/{serviceName}", "Accommodation")
                .param("customerID", customerId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].customerID").value(customerId.toString()));

        verify(billService, times(1)).getServiceBills(anyMap(), eq("Accommodation"));
    }

    // Test getBillDetails - Success
    @Test
    void testGetBillDetails_Success() throws Exception {
        when(billService.getBillDetails(billId)).thenReturn(billResponse);

        mockMvc.perform(get("/api/bill/detail/{billId}", billId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Fetched bill details successfully"))
                .andExpect(jsonPath("$.data.billID").value(billId.toString()))
                .andExpect(jsonPath("$.data.description").value("Hotel booking payment"))
                .andExpect(jsonPath("$.data.amount").value(1000000));

        verify(billService, times(1)).getBillDetails(billId);
    }

    // Test getBillDetails - Not Found
    @Test
    void testGetBillDetails_NotFound() throws Exception {
        when(billService.getBillDetails(billId))
                .thenThrow(new NotFoundException("Bill not found"));

        mockMvc.perform(get("/api/bill/detail/{billId}", billId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(billService, times(1)).getBillDetails(billId);
    }

    // Test createBill - Success
    @Test
    void testCreateBill_Success() throws Exception {
        when(billService.createBill(any(BillRequestDTO.class))).thenReturn(billResponse);

        mockMvc.perform(post("/api/bill/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(billRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Bill created successfully"))
                .andExpect(jsonPath("$.data.billID").value(billId.toString()))
                .andExpect(jsonPath("$.data.amount").value(1000000));

        verify(billService, times(1)).createBill(any(BillRequestDTO.class));
    }

    // Test updateBill - Success
    @Test
    void testUpdateBill_Success() throws Exception {
        billResponse.setAmount(1500000L);
        billRequest.setAmount(1500000L);

        when(billService.updateBill(any(BillRequestDTO.class), eq(billId)))
                .thenReturn(billResponse);

        mockMvc.perform(put("/api/bill/update/{billId}", billId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(billRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Bill updated successfully"))
                .andExpect(jsonPath("$.data.billID").value(billId.toString()))
                .andExpect(jsonPath("$.data.amount").value(1500000));

        verify(billService, times(1)).updateBill(any(BillRequestDTO.class), eq(billId));
    }

    // Test updateBill - Not Found
    @Test
    void testUpdateBill_NotFound() throws Exception {
        when(billService.updateBill(any(BillRequestDTO.class), eq(billId)))
                .thenThrow(new NotFoundException("Bill not found"));

        mockMvc.perform(put("/api/bill/update/{billId}", billId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(billRequest)))
                .andExpect(status().isNotFound());

        verify(billService, times(1)).updateBill(any(BillRequestDTO.class), eq(billId));
    }

    // Test payBill - Success without coupon
    @Test
    void testPayBill_SuccessWithoutCoupon() throws Exception {
        billResponse.setStatus(1);
        billResponse.setPaymentTimestamp(LocalDateTime.now());

        when(billService.payBill(eq(billId), isNull())).thenReturn(billResponse);

        mockMvc.perform(post("/api/bill/{billId}/pay", billId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Bill payed successfully"))
                .andExpect(jsonPath("$.data.billID").value(billId.toString()))
                .andExpect(jsonPath("$.data.status").value(1));

        verify(billService, times(1)).payBill(eq(billId), isNull());
    }

    // Test payBill - Success with coupon
    @Test
    void testPayBill_SuccessWithCoupon() throws Exception {
        billResponse.setStatus(1);
        billResponse.setPaymentTimestamp(LocalDateTime.now());
        billResponse.setAmount(900000L); // After discount

        BillCouponDTO couponRequest = new BillCouponDTO("DISCOUNT10");

        when(billService.payBill(eq(billId), eq("DISCOUNT10"))).thenReturn(billResponse);

        mockMvc.perform(post("/api/bill/{billId}/pay", billId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(couponRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Bill payed successfully"))
                .andExpect(jsonPath("$.data.status").value(1))
                .andExpect(jsonPath("$.data.amount").value(900000));

        verify(billService, times(1)).payBill(eq(billId), eq("DISCOUNT10"));
    }


    // Test payBill - IllegalArgumentException
    @Test
    void testPayBill_IllegalArgumentException() throws Exception {
        when(billService.payBill(eq(billId), isNull()))
                .thenThrow(new IllegalArgumentException("Invalid bill status"));

        mockMvc.perform(post("/api/bill/{billId}/pay", billId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(billService, times(1)).payBill(eq(billId), isNull());
    }

    // Test payBill - Generic Exception (returns error response)
    @Test
    void testPayBill_GenericException() throws Exception {
        when(billService.payBill(eq(billId), isNull()))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/bill/{billId}/pay", billId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Payment failed. An unexpexted error occured. Please try again later."));

        verify(billService, times(1)).payBill(eq(billId), isNull());
    }



    // Test payBill - Empty request body
    @Test
    void testPayBill_EmptyRequestBody() throws Exception {
        billResponse.setStatus(1);
        when(billService.payBill(eq(billId), isNull())).thenReturn(billResponse);

        mockMvc.perform(post("/api/bill/{billId}/pay", billId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(1));

        verify(billService, times(1)).payBill(eq(billId), isNull());
    }

    // Test getCustomerBills - Empty list
    @Test
    void testGetCustomerBills_EmptyList() throws Exception {
        when(billService.getCustomerBills(anyMap())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/bill/customer")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(billService, times(1)).getCustomerBills(anyMap());
    }

    // Test getServiceBills - Empty list
    @Test
    void testGetServiceBills_EmptyList() throws Exception {
        when(billService.getServiceBills(anyMap(), eq("Insurance")))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/bill/{serviceName}", "Insurance")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(billService, times(1)).getServiceBills(anyMap(), eq("Insurance"));
    }
}