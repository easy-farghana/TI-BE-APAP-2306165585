package apap.ti._5.accommodation_2306165585_be.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import apap.ti._5.accommodation_2306165585_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306165585_be.model.Room;


public interface AccommodationBookingRepository  extends JpaRepository<AccommodationBooking, String> {
    List<AccommodationBooking> findAllByRoom(Room room);
    List<AccommodationBooking> findByCheckInDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    
    @Query(value = """
        SELECT 
            b.booking_id AS bookingID,
            r.name AS roomName,
            p.property_name AS propertyName,
            b.status AS status,
            b.check_in_date AS checkInDate,
            b.check_out_date AS checkOutDate,
            b.total_price AS totalPrice
        FROM accommodation_booking b
        JOIN room r ON b.room = r.room_id
        JOIN roomtype_room rr ON rr.room_id = r.room_id
        JOIN room_type rt ON rt.room_type_id = rr.room_type_id
        JOIN properties p ON p.property_id = rt.property_id
        """, nativeQuery = true)
    List<Object[]> findAllBookingsWithPropertyInfo();
}
