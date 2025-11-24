package apap.ti._5.accommodation_2306165585_be.service.bill;

import java.util.List;
import java.util.Map;

import apap.ti._5.accommodation_2306165585_be.restdto.request.bill.CreateBillRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.bill.BillResponseDTO;

public interface BillService {

    BillResponseDTO createBooking(CreateBillRequestDTO bill);
    List<BillResponseDTO> getAllBills(Map<String, Object> params);
    List<BillResponseDTO> getCustomerBills(Map<String, Object> params);
    List<BillResponseDTO> getServiceBills(Map<String,Object> params, String serviceName);
    
}
