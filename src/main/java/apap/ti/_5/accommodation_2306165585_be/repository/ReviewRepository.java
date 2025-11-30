package apap.ti._5.accommodation_2306165585_be.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import apap.ti._5.accommodation_2306165585_be.model.Review;

public interface ReviewRepository  extends JpaRepository<Review, UUID> {
    List<Review> findAllByPropertyID(UUID propertyID);

    List<Review> findAllByCustomerID(UUID customerID);
}
