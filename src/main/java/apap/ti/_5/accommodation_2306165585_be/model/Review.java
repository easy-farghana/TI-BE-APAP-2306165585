package apap.ti._5.accommodation_2306165585_be.model;


import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

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
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "review_id", updatable = false, nullable = false)
    private UUID reviewID;

    @Column(name = "customer_id")
    private UUID customerID;

    @Column(name = "booking_id")
    private UUID bookingID;

    @Column(name = "comment")
    private String comment;

    @Column(name = "cleanliness_rating")
    @Min(value = 1, message = "Cleanliness rating must be at least 1")
    @Max(value = 5, message = "Cleanliness rating must be at most 5")
    private int cleanlinessRating;

    @Column(name = "facility_rating")
    @Min(value = 1, message = "Facility rating must be at least 1")
    @Max(value = 5, message = "Facility rating must be at most 5")
    private int facilityRating;

    @Column(name = "service_rating")
    @Min(value = 1, message = "Service rating must be at least 1")
    @Max(value = 5, message = "Service rating must be at most 5")
    private int serviceRating;

    @Column(name = "value_rating")
    @Min(value = 1, message = "Value rating must be at least 1")
    @Max(value = 5, message = "Value rating must be at most 5")
    private int valueRating;

    @Column(name = "overall_rating")
    @Min(value = 1, message = "Overall rating must be at least 1")
    @Max(value = 5, message = "Overall rating must be at most 5")
    private int overallRating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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