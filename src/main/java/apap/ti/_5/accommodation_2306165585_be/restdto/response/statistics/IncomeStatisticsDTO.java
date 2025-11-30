package apap.ti._5.accommodation_2306165585_be.restdto.response.statistics;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeStatisticsDTO {
    private List<PropertyStatisticDTO> propertyStatistics;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropertyStatisticDTO {
        private String propertyName;
        private Integer propertyIncomes;
    }
  
    private Integer totalIncome;
}
