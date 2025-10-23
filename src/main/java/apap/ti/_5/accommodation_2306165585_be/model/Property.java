package apap.ti._5.accommodation_2306165585_be.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "properties")
@SQLDelete(sql = "UPDATE properties SET active_status = 0 WHERE property_id = ?")
@SQLRestriction("active_status = 1") 
public class Property {

    @Id
    @Column(name = "property_id", updatable = false, nullable = false)
    private String propertyID;

    @Column(name = "property_name", nullable = false)
    private String propertyName;

    /**
     * Tipe Properti
     * 1: Hotel
     * 2: Villa
     * 3: Apartment
     */
    @Column(name = "type")
    @Check(constraints = "type IN (1, 2, 3)")
    private int type;

    @Column(name = "address")
    private String address;

    @Column(name = "province")
    private int province;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_room")
    private int totalRoom;

    @Column(name = "active_status")
    @Check(constraints = "active_status IN (0, 1)")
    private int activeStatus;

    @Column(name = "income")
    private int income;

    @OneToMany(
        cascade = CascadeType.ALL, 
        fetch = FetchType.LAZY
    )
    @JoinColumn(name = "property_id")
    private List<RoomType> listRoomType;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerID;

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
