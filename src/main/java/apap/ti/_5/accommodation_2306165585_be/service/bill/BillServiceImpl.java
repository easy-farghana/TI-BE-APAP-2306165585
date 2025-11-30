package apap.ti._5.accommodation_2306165585_be.service.bill;

import java.time.LocalDateTime;
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
import apap.ti._5.accommodation_2306165585_be.restdto.request.bill.BillRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.bill.BillResponseDTO;
import apap.ti._5.accommodation_2306165585_be.security.RoleGroup;
import apap.ti._5.accommodation_2306165585_be.security.UserContext;
import apap.ti._5.accommodation_2306165585_be.service.external.ExternalApiService;
import apap.ti._5.accommodation_2306165585_be.restdto.external.response.UserInfoResponseDTO;
import apap.ti._5.accommodation_2306165585_be.exception.SecurityException;

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
    public BillResponseDTO createBill(BillRequestDTO billDTO) {
        
        if (!VALID_SERVICES.contains(billDTO.getServiceName())) {
            throw new IllegalArgumentException(
                "Invalid service name: " + billDTO.getServiceName() +
                ". Must be one of: " + VALID_SERVICES
            );
        }

        Bill billOnReferenceID = billRepository.findByServiceReferenceID(billDTO.getServiceReferenceID()).orElse(null); 
        
        if (billOnReferenceID != null) {
            throw new IllegalArgumentException(
                "Bill with service reference ID " + billDTO.getServiceReferenceID() + " already exists"
            );
        }
        
        Bill bill = new Bill();

        bill.setCustomerID(billDTO.getCustomerID());
        bill.setServiceName(billDTO.getServiceName());
        bill.setServiceReferenceID(billDTO.getServiceReferenceID());
        bill.setAmount(billDTO.getAmount());
        bill.setDescription(billDTO.getDescription());
        bill.setAmount(billDTO.getAmount());
        bill.setStatus(0);
        Bill savedBill = billRepository.save(bill);
        log.info("Saving new bill {}", savedBill);
        return mapToBillResponseDTO(savedBill);
    }

    @Override
    public BillResponseDTO updateBill(BillRequestDTO billDTO, UUID billId) {
        Bill bill = billRepository.findById(billId).orElseThrow(
            () -> new NotFoundException("Bill with id: " + billId + " not found.")
        );
        if (!VALID_SERVICES.contains(billDTO.getServiceName())) {
            throw new IllegalArgumentException(
                "Invalid service name: " + billDTO.getServiceName() +
                ". Must be one of: " + VALID_SERVICES
            );
        }

       
        if (bill.getStatus() == 1) {
            throw new IllegalArgumentException("Cannot update paid bill");
        }
        boolean isSameCustomer = bill.getCustomerID().equals(billDTO.getCustomerID()); 
        boolean isSameService = bill.getServiceName().equals(billDTO.getServiceName());
        boolean isSameReferenceID = bill.getServiceReferenceID().equals(billDTO.getServiceReferenceID());

        if (!isSameCustomer) throw new IllegalArgumentException("Customer ID cannot be changed");
        if (!isSameService) throw new IllegalArgumentException("Service name cannot be changed");
        if (!isSameReferenceID) throw new IllegalArgumentException("Service Reference ID cannot be changed");

        bill.setDescription(billDTO.getDescription());
        bill.setAmount(billDTO.getAmount());

        log.info("Updating bill {}", bill);

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

    @Override
    public BillResponseDTO getBillDetails(UUID billID) {
        Bill bill = billRepository.findById(billID).orElseThrow(
            () -> new NotFoundException("Bill not found")
        );

        String role = userContext.getRole();
        
        if (role.equals(RoleGroup.SUPERADMIN)) {
            return mapToBillResponseDTO(bill);
        } else if (role.equals(RoleGroup.CUSTOMER) && bill.getCustomerID().equals(userContext.getUserID())) {
            return mapToBillResponseDTO(bill);
        } else if (isRoleAllowedForService(role, bill.getServiceName())) {
            return mapToBillResponseDTO(bill);
        } else {
            throw new SecurityException("You are not authorized to access this bill");
        }
    }

    @Override
    public BillResponseDTO payBill(UUID billID, String couponCode) {
        Bill bill = billRepository.findById(billID).orElseThrow(
            () -> new NotFoundException("Bill not found")
        );

        if (bill.getStatus() == 1) {
            throw new IllegalArgumentException("Bill is already paid");
        } 

        UUID userID = userContext.getUserID();
        if (!userID.equals(bill.getCustomerID())) {
            throw new SecurityException("You are not authorized to pay this bill");
        }

        UserInfoResponseDTO userInfo = externalApiService.getUserDetail(userID);

        // Adjust bill amount if coupon is provided
        long paymentAmount = bill.getAmount();
        long userSaldo = userInfo.getSaldo();
        // TODO: call loyalty service
        
        // if (couponCode != null && !couponCode.isEmpty()) {
        //     double discount = externalApiService.calculateDiscount(couponCode, bill);
        //     finalAmount -= discount;
        // }

        if (userSaldo < paymentAmount) {
            throw new IllegalArgumentException("Insufficient balance. Please top up balance.");
        }
        
        if (bill.getServiceName().equalsIgnoreCase("VehicleRental")) {
            boolean isDone = externalApiService.checkRentalStatus(bill.getServiceReferenceID());
            if (!isDone) {
                throw new IllegalArgumentException("Rental is not done");
            }
        }

        // Deduct balance via profile service
        externalApiService.deductBalance(userID, userSaldo, paymentAmount);

        // callback
        externalApiService.updateServicesBookingStatus(bill.getServiceName(), bill.getServiceReferenceID());

        // Update bill
        bill.setStatus(1);
        bill.setPaymentTimestamp(LocalDateTime.now());
        billRepository.save(bill);
        
        return mapToBillResponseDTO(bill);
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
