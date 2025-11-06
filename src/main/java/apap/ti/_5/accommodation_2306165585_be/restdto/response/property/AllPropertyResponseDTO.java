package apap.ti._5.accommodation_2306165585_be.restdto.response.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllPropertyResponseDTO {
    private String propertyID;
    private String propertyName;
    private int type;
    private int totalRooms;
    private int activeStatus;  
}
