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
    private List<String> propertyNames;
    private List<Integer> propertyIncomes;
    private Integer totalIncome;
}
