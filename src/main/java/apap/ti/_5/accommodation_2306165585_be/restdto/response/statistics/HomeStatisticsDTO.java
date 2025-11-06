package apap.ti._5.accommodation_2306165585_be.restdto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeStatisticsDTO {
    private int totalProperties;
    private int totalBookings;
}
