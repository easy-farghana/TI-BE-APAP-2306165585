package apap.ti._5.accommodation_2306165585_be.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import apap.ti._5.accommodation_2306165585_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306165585_be.model.Room;


public interface AccommodationBookingRepository  extends JpaRepository<AccommodationBooking, UUID> {
    List<AccommodationBooking> findAllByRoom(Room room);
    List<AccommodationBooking> findByCheckInDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
