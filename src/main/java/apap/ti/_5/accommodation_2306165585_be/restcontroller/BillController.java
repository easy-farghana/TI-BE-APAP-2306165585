package apap.ti._5.accommodation_2306165585_be.restcontroller;

import org.springframework.web.bind.annotation.RestController;

import apap.ti._5.accommodation_2306165585_be.service.bill.BillService;
import apap.ti._5.accommodation_2306165585_be.utils.ResponseUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import apap.ti._5.accommodation_2306165585_be.restdto.request.bill.BillCouponDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.bill.CreateBillRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.bill.BillResponseDTO;


@RestController
@RequestMapping("/api")
public class BillController {
    @Autowired
    ResponseUtil responseUtil;

    @Autowired
    BillService billService;

    public static final String BASE_URL = "/bill";
    public static final String VIEW_CUSTOMER_BILL = BASE_URL + "/customer";
    public static final String VIEW_SERVICE_BILL = BASE_URL + "/{serviceName}";
    public static final String VIEW_BILL_DETAILS = BASE_URL + "/detail/{billId}";
    public static final String CREATE_BILL = BASE_URL + "/create";
    public static final String PAY_BILL = BASE_URL + "/{billId}/pay";


    /**
     * Get all bills with optional filters on customer ID, service name, and status
     * 
     * @param customerID The customer ID to filter on
     * @param serviceName The service name to filter on
     * @param status The status to filter on
     * @return A list of bills that match the given filters
     */
    @GetMapping(BASE_URL)
    public ResponseEntity<BaseResponseDTO<List<BillResponseDTO>>> getAllBills(
        @RequestParam(value = "customerID", required = false) UUID customerID,
        @RequestParam(value = "serviceName", required = false) String serviceName,
        @RequestParam(value = "status", required = false) Integer status
    ) {

        Map<String, Object> params = new HashMap<>();
        if (customerID != null) params.put("customerID", customerID);
        if (serviceName != null) params.put("serviceName", serviceName);
        if (status != null) params.put("status", status);

        List<BillResponseDTO> allBill = billService.getAllBills(params);
        return responseUtil.success(
            allBill,
            "Get all bills successfully",
            HttpStatus.OK
        );
    }

    /**
     * Get all bills of a customer with optional filters on status, sort by, and sort direction
     * 
     * @param status The status to filter on
     * @param sortBy The field to sort by
     * @param sortDir The direction to sort in
     * @return A list of bills that match the given filters
     */
    @GetMapping(VIEW_CUSTOMER_BILL)
    public ResponseEntity<BaseResponseDTO<List<BillResponseDTO>>> getCustomerBills(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir
    ) {

        Map<String, Object> params = new HashMap<>();
        if (status != null) params.put("status", status);

        params.put("sortBy", sortBy);
        params.put("sortDir", sortDir);

        List<BillResponseDTO> allBill = billService.getAllBills(params);

        return responseUtil.success(
            allBill,
            "Fetched all customer's bills successfully",
            HttpStatus.OK
        );
    }

    @GetMapping(VIEW_SERVICE_BILL)
    public ResponseEntity<BaseResponseDTO<List<BillResponseDTO>>> getServiceBills(
        @PathVariable String serviceName,
        @RequestParam(value = "status", required = false) Integer status,
        @RequestParam(value = "customerID", required = false) UUID customerID
    ) {

        Map<String, Object> params = new HashMap<>();
        if (status != null) params.put("status", status);
        if (status != null) params.put("customerID", customerID);

        List<BillResponseDTO> allBill = billService.getServiceBills(params, serviceName);

        return responseUtil.success(
            allBill,
            "Fetched all customer's bills successfully",
            HttpStatus.OK
        );
    }

    @GetMapping(VIEW_BILL_DETAILS)
    public ResponseEntity<BaseResponseDTO<BillResponseDTO>> getBillDetails(@PathVariable UUID billId) {
        BillResponseDTO bill = billService.getBillDetails(billId);
        return responseUtil.success(
            bill,
            "Fetched bill details successfully",
            HttpStatus.OK
        );
    }

    @PostMapping(PAY_BILL)
    public ResponseEntity<BaseResponseDTO<BillResponseDTO>> payBill(
        @PathVariable UUID billId,
        @RequestBody(required = false) BillCouponDTO request
    ) {

        String couponCode = request != null ? request.getCouponCode() : null;
        BillResponseDTO bill= billService.payBill(billId, couponCode);
 
        return responseUtil.success(
            bill,
            "Bill payed successfully",
            HttpStatus.OK
        );
    }


    /**
     * Create a new bill with the given information
     * 
     * @param bill The information of the bill to be created
     * @return The created bill with a success message and HTTP status code of CREATED
     */
    @PostMapping(CREATE_BILL)
    public ResponseEntity<BaseResponseDTO<BillResponseDTO>> createBill(@RequestBody CreateBillRequestDTO bill) {
        BillResponseDTO newBill = billService.createBill(bill);
        return responseUtil.success(
            newBill,
            "Bill created successfully",
            HttpStatus.CREATED
        );
    }
}
