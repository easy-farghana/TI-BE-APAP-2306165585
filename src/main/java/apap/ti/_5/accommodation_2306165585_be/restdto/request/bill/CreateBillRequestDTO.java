package apap.ti._5.accommodation_2306165585_be.restdto.request.bill;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateBillRequestDTO {
    @NotNull(message = "Customer ID wajib diisi")
    private UUID customerID;

    @NotBlank(message = "Nama servis wajib diisi")
    private String serviceName;

    @NotBlank(message = "Reference ID wajib diisi")
    private String serviceReferenceID;

    @NotBlank(message = "Deskripsi wajib diisi")
    private String description;
    
    @NotNull(message = "Amount wajib diisi")
    @Min(value = 1, message = "amount minimal 1")
    private Long amount;
   
}
