package apap.ti._5.accommodation_2306165585_be.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Check;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "accommodation_booking")
public class AccommodationBooking {

    @Id
    @Column(name = "booking_id", updatable = false, nullable = false)
    private String bookingID;

    @Column(name = "check_in_date", nullable = false)
    private LocalDateTime checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDateTime checkOutDate;

    @Column(name = "total_days", nullable = false)
    private int totalDays;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    /**
     * Status pemesanan
     * 0: Waiting for Payment
     * 1: Payment Confirmed
     * 2: Cancelled
     * 3: Request Refund
     * 4: Done
     */
    @Column(name = "status", nullable = false)
    @Check(constraints = "status IN (0, 1, 2, 3, 4)")
    private int status;

    @Column(name = "customer_id", nullable = false)
    private UUID customerID;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @Column(name = "is_breakfast", nullable = false)
    private boolean isBreakfast;

    @Column(name = "refund")
    private int refund;

    @Column(name = "extra_pay")
    private int extraPay;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room", referencedColumnName = "room_id", nullable = false)
    private Room room;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate; 

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}