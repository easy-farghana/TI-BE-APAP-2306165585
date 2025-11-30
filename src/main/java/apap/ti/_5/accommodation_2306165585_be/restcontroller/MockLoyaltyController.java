package apap.ti._5.accommodation_2306165585_be.restcontroller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import apap.ti._5.accommodation_2306165585_be.restdto.external.request.CouponRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306165585_be.utils.ResponseUtil;



@RestController
@RequestMapping("/api")
public class MockLoyaltyController {
    
    @Autowired
    private ResponseUtil responseUtil;
    
    public static final String BASE_URL = "/loyalty";


    @PostMapping(BASE_URL + "/coupon/use")
    public ResponseEntity<BaseResponseDTO<Map<String, Object>>> useCoupon(@RequestBody CouponRequestDTO request) {
                
        Map<String, Object> body = new HashMap<>();
        if (request.getCouponCode().equals("MOCK_COUPON_CODE")) {
            body.put("percentOff", 10);
            return responseUtil.success(body, "Coupon used successfully", HttpStatus.OK);
        } 
        body.put("percentOff", 0);
        return responseUtil.success(body, "Invalid coupon code", HttpStatus.OK);
    }
}