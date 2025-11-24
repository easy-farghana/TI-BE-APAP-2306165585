package apap.ti._5.accommodation_2306165585_be.service.bill;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.model.Bill;
import apap.ti._5.accommodation_2306165585_be.repository.BillRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.request.bill.CreateBillRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.bill.BillResponseDTO;
import apap.ti._5.accommodation_2306165585_be.security.UserContext;
import apap.ti._5.accommodation_2306165585_be.service.external.ExternalApiService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillServiceImpl implements BillService {
    
    @Autowired
    BillRepository billRepository;

    @Autowired
    ExternalApiService externalApiService;

    @Autowired
    UserContext userContext;
    
    private static final Set<String> VALID_SERVICES = Set.of(
        "Flight", "Accommodation", "Insurance", "VehicleRental", "TourPackage"
    );

    @Override
    public BillResponseDTO createBooking(CreateBillRequestDTO billDTO) {
        Bill bill = new Bill();
        

        if (!VALID_SERVICES.contains(billDTO.getServiceName())) {
            throw new IllegalArgumentException(
                "Invalid service name: " + billDTO.getServiceName() +
                ". Must be one of: " + VALID_SERVICES
            );
        }

        bill.setCustomerID(billDTO.getCustomerID());
        bill.setServiceName(billDTO.getServiceName());
        bill.setServiceReferenceID(billDTO.getServiceReferenceID());
        bill.setAmount(billDTO.getAmount());
        bill.setDescription(billDTO.getDescription());
        bill.setAmount(billDTO.getAmount());
        bill.setStatus(0);

        log.info("Saving new bill {}", bill);

        billRepository.save(bill);
        return mapToBillResponseDTO(bill);
    }

    /**
     * Get all bills with optional filters on customer ID, service name, and status
     * 
     * @param params a map containing the filters
     * @return a list of bills that match the given filters
     * @throws NotFoundException if no bills are found
     */
    @Override
    public List<BillResponseDTO> getAllBills(Map<String, Object> params) {

        UUID customerID = (UUID) params.get("customerID");
        String serviceName = (String) params.get("serviceName");
        Integer status = (Integer) params.get("status");

        List<Bill> listOfBill = billRepository.findAllWithFilters(customerID, serviceName, status);

        if (listOfBill.isEmpty()) {
            throw new NotFoundException("No bills found");
        }

        return listOfBill.stream()
            .map(this::mapToBillResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get all bills of a customer with optional filters on status, sort by, and sort direction
     * 
     * @param status The status to filter on
     * @param sortBy The field to sort by
     * @param sortDir The direction to sort in
     * @return A list of bills that match the given filters
     * @throws NotFoundException if no bills are found
     */
    @Override
    public List<BillResponseDTO> getCustomerBills(Map<String, Object> params) {
        Integer status = (Integer) params.get("status");
        String sortBy = (String) params.get("sortBy");
        String sortDir = (String) params.get("sortDir");

        UUID customerID = userContext.getUserID();
        List<Bill> listOfBill = billRepository.findAllWithFiltersForCustomers(customerID, status, sortBy, sortDir);

        if (listOfBill.isEmpty()) {
            throw new NotFoundException("No bills found");
        }

        return listOfBill.stream()
            .map(this::mapToBillResponseDTO)
            .collect(Collectors.toList());
    }

    
    /**
     * Get all bills of a customer for a specific service with optional filters on status and customer ID
     * 
     * @param params a map containing the filters
     * @param serviceName the name of the service to filter on
     * @return a list of bills that match the given filters
     * @throws SecurityException if the user is not authorized to access bills for the service
     * @throws NotFoundException if no bills are found
     */
    @Override
    public List<BillResponseDTO> getServiceBills(Map<String, Object> params, String serviceName) {

        Integer status = (Integer) params.get("status");
        UUID customerID = (UUID) params.get("customerID");

        String role = userContext.getRole();
        if (!isRoleAllowedForService(role, serviceName)) {
            throw new SecurityException("You are not authorized to access bills for service: " + serviceName);
        }

        List<Bill> listOfBill = billRepository.findServiceBills(customerID, status, serviceName);

        return listOfBill.stream()
                .map(this::mapToBillResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Check if the given role is allowed to access bills for the given service.
     * 
     * @param role the role of the user
     * @param serviceName the name of the service to check
     * @return true if the role is allowed to access bills for the service, false otherwise
     */
    private boolean isRoleAllowedForService(String role, String serviceName) {
        return switch (role) {
            case "ACCOMMODATION_OWNER" -> serviceName.equalsIgnoreCase("Accommodation");
            case "FLIGHT_AIRLINE" -> serviceName.equalsIgnoreCase("Flight");
            case "RENTAL_VENDOR" -> serviceName.equalsIgnoreCase("VehicleRental");
            case "INSURANCE_PROVIDER" -> serviceName.equalsIgnoreCase("Insurance");
            case "TOUR_PACKAGE_VENDOR" -> serviceName.equalsIgnoreCase("TourPackage");
            default -> false;
        };
    }


    private BillResponseDTO mapToBillResponseDTO(Bill bill) {
        return BillResponseDTO.builder()
            .billID(bill.getBillID())
            .customerID(bill.getCustomerID())
            .serviceName(bill.getServiceName())
            .serviceReferenceID(bill.getServiceReferenceID())
            .description(bill.getDescription())
            .status(bill.getStatus())
            .amount(bill.getAmount())
            .createdAt(bill.getCreatedAt())
            .updatedAt(bill.getUpdatedAt())
            .paymentTimestamp(bill.getPaymentTimestamp())
            .build();
    }
}
