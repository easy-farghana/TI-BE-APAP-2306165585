package apap.ti._5.accommodation_2306165585_be.restdto.external.request;


import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CouponRequestDTO {
    private String couponCode;
    private UUID customerID;
}
