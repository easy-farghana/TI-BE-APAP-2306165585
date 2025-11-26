package apap.ti._5.accommodation_2306165585_be.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import apap.ti._5.accommodation_2306165585_be.model.Review;

public interface ReviewRepository  extends JpaRepository<Review, UUID> {
    @Query(value = """
        SELECT r.*
        FROM review r
        JOIN accommodation_booking ab 
            ON r.booking_id = ab.booking_id
        JOIN room room 
            ON ab.room = room.room_id
        JOIN roomtype_room rr 
            ON room.room_id = rr.room_id
        JOIN room_type rt 
            ON rr.roomtype_id = rt.roomtype_id
        JOIN property p 
            ON rt.property_id = p.property_id
        WHERE p.property_id = :propertyID
    """, nativeQuery = true)
    List<Review> findAllByPropertyID(UUID propertyID);

    List<Review> findAllByCustomerID(UUID customerID);
}
