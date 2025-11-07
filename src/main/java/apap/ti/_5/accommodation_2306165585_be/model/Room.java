package apap.ti._5.accommodation_2306165585_be.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "room")
@SQLDelete(sql = "UPDATE room SET active_room = 0 WHERE room_id = ?")
public class Room {

    @Id
    @Column(name = "room_id", updatable = false, nullable = false)
    private String roomID; 

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "availability_status")
    @Check(constraints = "availability_status IN (0, 1)")
    private int availabilityStatus;

    @Column(name = "active_room")
    @Check(constraints = "active_room IN (0, 1)")
    private int activeRoom;

    @Column(name = "maintenance_start")
    private LocalDateTime maintenanceStart;

    @Column(name = "maintenance_end")
    private LocalDateTime maintenanceEnd;

    @OneToMany(
        mappedBy = "room", 
        fetch = FetchType.LAZY
    )
    private List<AccommodationBooking> listAccommodationBooking;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
        this.activeRoom = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}