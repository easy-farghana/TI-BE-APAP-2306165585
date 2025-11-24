package apap.ti._5.accommodation_2306165585_be.model;


import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Check;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bill")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bill_id", updatable = false, nullable = false)
    private UUID billID;

    @Column(name = "customer_id")
    private UUID customerID;

    @Column(name = "service_name")
    @Check(constraints = "service_name IN ('Flight', 'Accommodation', 'Insurance', 'VehicleRental', 'TourPackage')")
    private String serviceName;

    @Column(name = "service_reference_id")
    private String serviceReferenceID;

    @Column(name = "description")
    private String description;

    /**
     * Deskripsi Status
     * 0: Unpaid
     * 1: Paid
     */
    @Column(name = "status")
    @Check(constraints = "status IN (0, 1)")
    private int status;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "payment_timestamp")
    private LocalDateTime paymentTimestamp;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();    
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
