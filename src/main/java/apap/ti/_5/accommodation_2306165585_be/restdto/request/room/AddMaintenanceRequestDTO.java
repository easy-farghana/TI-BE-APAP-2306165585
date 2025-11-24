package apap.ti._5.accommodation_2306165585_be.restdto.request.room;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddMaintenanceRequestDTO {
    @NotBlank
    private UUID roomID;

    @NotNull(message = "maintenanceStart wajib diisi")
    private LocalDateTime maintenanceStart;

    @NotNull(message = "maintenanceEnd wajib diisi")
    private LocalDateTime maintenanceEnd;

}
