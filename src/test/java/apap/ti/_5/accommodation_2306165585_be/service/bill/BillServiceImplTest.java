package apap.ti._5.accommodation_2306165585_be.service.bill;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.exception.SecurityException;
import apap.ti._5.accommodation_2306165585_be.model.Bill;
import apap.ti._5.accommodation_2306165585_be.repository.BillRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.external.response.UserInfoResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.bill.BillRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.bill.BillResponseDTO;
import apap.ti._5.accommodation_2306165585_be.security.RoleGroup;
import apap.ti._5.accommodation_2306165585_be.security.UserContext;
import apap.ti._5.accommodation_2306165585_be.service.external.ExternalApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillServiceImplTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private ExternalApiService externalApiService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private BillServiceImpl billService;

    private UUID billId;
    private UUID customerId;
    private Bill testBill;
    private BillRequestDTO testBillRequest;

    @BeforeEach
    void setUp() {
        billId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        testBill = new Bill();
        testBill.setBillID(billId);
        testBill.setCustomerID(customerId);
        testBill.setServiceName("Accommodation");
        testBill.setServiceReferenceID("REF123");
        testBill.setDescription("Test bill");
        testBill.setAmount(100000L);
        testBill.setStatus(0);
        testBill.setCreatedAt(LocalDateTime.now());
        testBill.setUpdatedAt(LocalDateTime.now());

        testBillRequest = new BillRequestDTO();
        testBillRequest.setCustomerID(customerId);
        testBillRequest.setServiceName("Accommodation");
        testBillRequest.setServiceReferenceID("REF123");
        testBillRequest.setDescription("Test bill");
        testBillRequest.setAmount(100000L);
    }

    // Test createBill
    @Test
    void testCreateBill_Success() {
        when(billRepository.findByServiceReferenceID("REF123")).thenReturn(Optional.empty());
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);

        BillResponseDTO result = billService.createBill(testBillRequest);

        assertNotNull(result);
        assertEquals(billId, result.getBillID());
        assertEquals(customerId, result.getCustomerID());
        assertEquals("Accommodation", result.getServiceName());
        verify(billRepository).save(any(Bill.class));
    }

    @Test
    void testCreateBill_InvalidServiceName() {
        testBillRequest.setServiceName("InvalidService");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> billService.createBill(testBillRequest));

        assertTrue(exception.getMessage().contains("Invalid service name"));
        verify(billRepository, never()).save(any());
    }

    @Test
    void testCreateBill_DuplicateReferenceID() {
        when(billRepository.findByServiceReferenceID("REF123")).thenReturn(Optional.of(testBill));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> billService.createBill(testBillRequest));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(billRepository, never()).save(any());
    }

    @Test
    void testCreateBill_AllValidServiceNames() {
        List<String> validServices = Arrays.asList(
                "Flight", "Accommodation", "Insurance", "VehicleRental", "TourPackage"
        );

        for (String service : validServices) {
            testBillRequest.setServiceName(service);
            testBillRequest.setServiceReferenceID("REF_" + service);
            
            when(billRepository.findByServiceReferenceID(anyString())).thenReturn(Optional.empty());
            when(billRepository.save(any(Bill.class))).thenReturn(testBill);

            BillResponseDTO result = billService.createBill(testBillRequest);

            assertNotNull(result);
        }
    }

    // Test updateBill
    @Test
    void testUpdateBill_Success() {
        testBillRequest.setDescription("Updated description");
        testBillRequest.setAmount(150000L);

        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);

        BillResponseDTO result = billService.updateBill(testBillRequest, billId);

        assertNotNull(result);
        verify(billRepository).save(any(Bill.class));
    }

    @Test
    void testUpdateBill_NotFound() {
        when(billRepository.findById(billId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> billService.updateBill(testBillRequest, billId));
    }

    @Test
    void testUpdateBill_InvalidServiceName() {
        testBillRequest.setServiceName("InvalidService");
        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));

        assertThrows(IllegalArgumentException.class,
                () -> billService.updateBill(testBillRequest, billId));
    }

    @Test
    void testUpdateBill_AlreadyPaid() {
        testBill.setStatus(1);
        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> billService.updateBill(testBillRequest, billId));

        assertEquals("Cannot update paid bill", exception.getMessage());
    }

    @Test
    void testUpdateBill_ChangeCustomerID() {
        testBillRequest.setCustomerID(UUID.randomUUID());
        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> billService.updateBill(testBillRequest, billId));

        assertEquals("Customer ID cannot be changed", exception.getMessage());
    }

    @Test
    void testUpdateBill_ChangeServiceName() {
        testBillRequest.setServiceName("Flight");
        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> billService.updateBill(testBillRequest, billId));

        assertEquals("Service name cannot be changed", exception.getMessage());
    }

    @Test
    void testUpdateBill_ChangeReferenceID() {
        testBillRequest.setServiceReferenceID("NEW_REF");
        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> billService.updateBill(testBillRequest, billId));

        assertEquals("Service Reference ID cannot be changed", exception.getMessage());
    }

    // Test getAllBills
    @Test
    void testGetAllBills_Success() {
        Map<String, Object> params = new HashMap<>();
        params.put("customerID", customerId);
        params.put("serviceName", "Accommodation");
        params.put("status", 0);

        when(billRepository.findAllWithFilters(customerId, "Accommodation", 0))
                .thenReturn(Collections.singletonList(testBill));

        List<BillResponseDTO> result = billService.getAllBills(params);

        assertEquals(1, result.size());
        assertEquals(billId, result.get(0).getBillID());
    }

    @Test
    void testGetAllBills_NoFilters() {
        Map<String, Object> params = new HashMap<>();

        when(billRepository.findAllWithFilters(null, null, null))
                .thenReturn(Collections.singletonList(testBill));

        List<BillResponseDTO> result = billService.getAllBills(params);

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllBills_NotFound() {
        Map<String, Object> params = new HashMap<>();

        when(billRepository.findAllWithFilters(null, null, null))
                .thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class,
                () -> billService.getAllBills(params));
    }

    @Test
    void testGetAllBills_MultipleBills() {
        Map<String, Object> params = new HashMap<>();
        
        Bill bill2 = new Bill();
        bill2.setBillID(UUID.randomUUID());
        bill2.setCustomerID(customerId);
        bill2.setServiceName("Flight");

        when(billRepository.findAllWithFilters(null, null, null))
                .thenReturn(Arrays.asList(testBill, bill2));

        List<BillResponseDTO> result = billService.getAllBills(params);

        assertEquals(2, result.size());
    }

    // Test getCustomerBills
    @Test
    void testGetCustomerBills_Success() {
        Map<String, Object> params = new HashMap<>();
        params.put("status", 0);
        params.put("sortBy", "createdAt");
        params.put("sortDir", "DESC");

        when(userContext.getUserID()).thenReturn(customerId);
        when(billRepository.findAllWithFiltersForCustomers(customerId, 0, "createdAt", "DESC"))
                .thenReturn(Collections.singletonList(testBill));

        List<BillResponseDTO> result = billService.getCustomerBills(params);

        assertEquals(1, result.size());
        assertEquals(billId, result.get(0).getBillID());
    }

    @Test
    void testGetCustomerBills_NoFilters() {
        Map<String, Object> params = new HashMap<>();

        when(userContext.getUserID()).thenReturn(customerId);
        when(billRepository.findAllWithFiltersForCustomers(customerId, null, null, null))
                .thenReturn(Collections.singletonList(testBill));

        List<BillResponseDTO> result = billService.getCustomerBills(params);

        assertEquals(1, result.size());
    }

    @Test
    void testGetCustomerBills_NotFound() {
        Map<String, Object> params = new HashMap<>();

        when(userContext.getUserID()).thenReturn(customerId);
        when(billRepository.findAllWithFiltersForCustomers(customerId, null, null, null))
                .thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class,
                () -> billService.getCustomerBills(params));
    }

    // Test getServiceBills
    @Test
    void testGetServiceBills_AsAccommodationOwner_Success() {
        Map<String, Object> params = new HashMap<>();
        params.put("status", 0);
        params.put("customerID", customerId);

        when(userContext.getRole()).thenReturn("ACCOMMODATION_OWNER");
        when(billRepository.findServiceBills(customerId, 0, "Accommodation"))
                .thenReturn(Collections.singletonList(testBill));

        List<BillResponseDTO> result = billService.getServiceBills(params, "Accommodation");

        assertEquals(1, result.size());
        assertEquals(billId, result.get(0).getBillID());
    }

    @Test
    void testGetServiceBills_AsFlightAirline_Success() {
        testBill.setServiceName("Flight");
        Map<String, Object> params = new HashMap<>();

        when(userContext.getRole()).thenReturn("FLIGHT_AIRLINE");
        when(billRepository.findServiceBills(null, null, "Flight"))
                .thenReturn(Collections.singletonList(testBill));

        List<BillResponseDTO> result = billService.getServiceBills(params, "Flight");

        assertEquals(1, result.size());
    }

    @Test
    void testGetServiceBills_AsRentalVendor_Success() {
        testBill.setServiceName("VehicleRental");
        Map<String, Object> params = new HashMap<>();

        when(userContext.getRole()).thenReturn("RENTAL_VENDOR");
        when(billRepository.findServiceBills(null, null, "VehicleRental"))
                .thenReturn(Collections.singletonList(testBill));

        List<BillResponseDTO> result = billService.getServiceBills(params, "VehicleRental");

        assertEquals(1, result.size());
    }

    @Test
    void testGetServiceBills_AsInsuranceProvider_Success() {
        testBill.setServiceName("Insurance");
        Map<String, Object> params = new HashMap<>();

        when(userContext.getRole()).thenReturn("INSURANCE_PROVIDER");
        when(billRepository.findServiceBills(null, null, "Insurance"))
                .thenReturn(Collections.singletonList(testBill));

        List<BillResponseDTO> result = billService.getServiceBills(params, "Insurance");

        assertEquals(1, result.size());
    }

    @Test
    void testGetServiceBills_AsTourPackageVendor_Success() {
        testBill.setServiceName("TourPackage");
        Map<String, Object> params = new HashMap<>();

        when(userContext.getRole()).thenReturn("TOUR_PACKAGE_VENDOR");
        when(billRepository.findServiceBills(null, null, "TourPackage"))
                .thenReturn(Collections.singletonList(testBill));

        List<BillResponseDTO> result = billService.getServiceBills(params, "TourPackage");

        assertEquals(1, result.size());
    }

    @Test
    void testGetServiceBills_Unauthorized() {
        Map<String, Object> params = new HashMap<>();

        when(userContext.getRole()).thenReturn("CUSTOMER");

        assertThrows(SecurityException.class,
                () -> billService.getServiceBills(params, "Accommodation"));
    }

    @Test
    void testGetServiceBills_WrongServiceForRole() {
        Map<String, Object> params = new HashMap<>();

        when(userContext.getRole()).thenReturn("ACCOMMODATION_OWNER");

        assertThrows(SecurityException.class,
                () -> billService.getServiceBills(params, "Flight"));
    }

    @Test
    void testGetServiceBills_EmptyResult() {
        Map<String, Object> params = new HashMap<>();

        when(userContext.getRole()).thenReturn("ACCOMMODATION_OWNER");
        when(billRepository.findServiceBills(null, null, "Accommodation"))
                .thenReturn(Collections.emptyList());

        List<BillResponseDTO> result = billService.getServiceBills(params, "Accommodation");

        assertTrue(result.isEmpty());
    }

    // Test getBillDetails
    @Test
    void testGetBillDetails_AsSuperAdmin() {
        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);

        BillResponseDTO result = billService.getBillDetails(billId);

        assertNotNull(result);
        assertEquals(billId, result.getBillID());
    }

    @Test
    void testGetBillDetails_AsCustomer_OwnBill() {
        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));
        when(userContext.getRole()).thenReturn(RoleGroup.CUSTOMER);
        when(userContext.getUserID()).thenReturn(customerId);

        BillResponseDTO result = billService.getBillDetails(billId);

        assertNotNull(result);
        assertEquals(billId, result.getBillID());
    }

    @Test
    void testGetBillDetails_AsCustomer_NotOwnBill() {
        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));
        when(userContext.getRole()).thenReturn(RoleGroup.CUSTOMER);
        when(userContext.getUserID()).thenReturn(UUID.randomUUID());

        assertThrows(SecurityException.class,
                () -> billService.getBillDetails(billId));
    }

    @Test
    void testGetBillDetails_AsServiceProvider_Authorized() {
        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));
        when(userContext.getRole()).thenReturn("ACCOMMODATION_OWNER");

        BillResponseDTO result = billService.getBillDetails(billId);

        assertNotNull(result);
        assertEquals(billId, result.getBillID());
    }

    @Test
    void testGetBillDetails_AsServiceProvider_Unauthorized() {
        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));
        when(userContext.getRole()).thenReturn("FLIGHT_AIRLINE");

        assertThrows(SecurityException.class,
                () -> billService.getBillDetails(billId));
    }

    @Test
    void testGetBillDetails_NotFound() {
        when(billRepository.findById(billId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> billService.getBillDetails(billId));
    }

    // Test payBill
    @Test
    void testPayBill_Success() {
        UserInfoResponseDTO userInfo = new UserInfoResponseDTO();
        userInfo.setSaldo(200000L);

        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));
        when(userContext.getUserID()).thenReturn(customerId);
        when(externalApiService.getUserDetail(customerId)).thenReturn(userInfo);
        doNothing().when(externalApiService).deductBalance(customerId, 200000L, 100000L);
        doNothing().when(externalApiService).updateServicesBookingStatus("Accommodation", "REF123");
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);

        BillResponseDTO result = billService.payBill(billId, null);

        assertNotNull(result);
        verify(externalApiService).deductBalance(customerId, 200000L, 100000L);
        verify(externalApiService).updateServicesBookingStatus("Accommodation", "REF123");
        verify(billRepository).save(any(Bill.class));
    }

    @Test
    void testPayBill_NotFound() {
        when(billRepository.findById(billId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> billService.payBill(billId, null));
    }

    @Test
    void testPayBill_AlreadyPaid() {
        testBill.setStatus(1);
        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> billService.payBill(billId, null));

        assertEquals("Bill is already paid", exception.getMessage());
    }

    @Test
    void testPayBill_NotOwnBill() {
        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));
        when(userContext.getUserID()).thenReturn(UUID.randomUUID());

        assertThrows(SecurityException.class,
                () -> billService.payBill(billId, null));
    }

    @Test
    void testPayBill_InsufficientBalance() {
        UserInfoResponseDTO userInfo = new UserInfoResponseDTO();
        userInfo.setSaldo(50000L);

        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));
        when(userContext.getUserID()).thenReturn(customerId);
        when(externalApiService.getUserDetail(customerId)).thenReturn(userInfo);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> billService.payBill(billId, null));

        assertTrue(exception.getMessage().contains("Insufficient balance"));
    }

    @Test
    void testPayBill_ExactBalance() {
        UserInfoResponseDTO userInfo = new UserInfoResponseDTO();
        userInfo.setSaldo(100000L);

        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));
        when(userContext.getUserID()).thenReturn(customerId);
        when(externalApiService.getUserDetail(customerId)).thenReturn(userInfo);
        doNothing().when(externalApiService).deductBalance(customerId, 100000L, 100000L);
        doNothing().when(externalApiService).updateServicesBookingStatus(anyString(), anyString());
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);

        BillResponseDTO result = billService.payBill(billId, null);

        assertNotNull(result);
        verify(externalApiService).deductBalance(customerId, 100000L, 100000L);
    }

    // Test isRoleAllowedForService (indirectly through other tests)
    @Test
    void testIsRoleAllowedForService_AllCombinations() {
        Map<String, Object> params = new HashMap<>();

        // Test all valid role-service combinations
        String[][] validCombinations = {
                {"ACCOMMODATION_OWNER", "Accommodation"},
                {"FLIGHT_AIRLINE", "Flight"},
                {"RENTAL_VENDOR", "VehicleRental"},
                {"INSURANCE_PROVIDER", "Insurance"},
                {"TOUR_PACKAGE_VENDOR", "TourPackage"}
        };

        for (String[] combo : validCombinations) {
            when(userContext.getRole()).thenReturn(combo[0]);
            when(billRepository.findServiceBills(any(), any(), eq(combo[1])))
                    .thenReturn(Collections.emptyList());

            List<BillResponseDTO> result = billService.getServiceBills(params, combo[1]);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testIsRoleAllowedForService_CaseInsensitiveServiceName() {
        Map<String, Object> params = new HashMap<>();
        
        when(userContext.getRole()).thenReturn("ACCOMMODATION_OWNER");
        when(billRepository.findServiceBills(any(), any(), eq("accommodation")))
                .thenReturn(Collections.emptyList());

        List<BillResponseDTO> result = billService.getServiceBills(params, "accommodation");
        assertTrue(result.isEmpty());
    }

    // Test edge cases
    @Test
    void testCreateBill_NullReferenceID() {
        when(billRepository.findByServiceReferenceID(null)).thenReturn(Optional.empty());
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);

        testBillRequest.setServiceReferenceID(null);
        
        // Should not throw exception, null is valid
        assertDoesNotThrow(() -> billService.createBill(testBillRequest));
    }

    @Test
    void testGetAllBills_WithPartialFilters() {
        Map<String, Object> params = new HashMap<>();
        params.put("status", 0);
        // No customerID or serviceName

        when(billRepository.findAllWithFilters(null, null, 0))
                .thenReturn(Collections.singletonList(testBill));

        List<BillResponseDTO> result = billService.getAllBills(params);

        assertEquals(1, result.size());
    }

    @Test
    void testPayBill_EmptyCouponCode() {
        UserInfoResponseDTO userInfo = new UserInfoResponseDTO();
        userInfo.setSaldo(200000L);

        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));
        when(userContext.getUserID()).thenReturn(customerId);
        when(externalApiService.getUserDetail(customerId)).thenReturn(userInfo);
        doNothing().when(externalApiService).deductBalance(customerId, 200000L, 100000L);
        doNothing().when(externalApiService).updateServicesBookingStatus(anyString(), anyString());
        when(billRepository.save(any(Bill.class))).thenReturn(testBill);

        BillResponseDTO result = billService.payBill(billId, "");

        assertNotNull(result);
    }

    @Test
    void testMapToBillResponseDTO_AllFields() {
        testBill.setPaymentTimestamp(LocalDateTime.now());
        
        when(billRepository.findById(billId)).thenReturn(Optional.of(testBill));
        when(userContext.getRole()).thenReturn(RoleGroup.SUPERADMIN);

        BillResponseDTO result = billService.getBillDetails(billId);

        assertNotNull(result);
        assertEquals(billId, result.getBillID());
        assertEquals(customerId, result.getCustomerID());
        assertEquals("Accommodation", result.getServiceName());
        assertEquals("REF123", result.getServiceReferenceID());
        assertEquals("Test bill", result.getDescription());
        assertEquals(0, result.getStatus());
        assertEquals(100000L, result.getAmount());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertNotNull(result.getPaymentTimestamp());
    }
}