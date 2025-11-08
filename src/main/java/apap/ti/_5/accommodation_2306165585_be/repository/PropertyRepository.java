package apap.ti._5.accommodation_2306165585_be.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import apap.ti._5.accommodation_2306165585_be.model.Property;

import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, String> {
    @Query("SELECT p FROM Property p WHERE p.activeStatus = 1 and p.propertyID = ?1")
    Optional<Property> findByIdActive(String propertyID);
    
    @Query("SELECT p FROM Property p WHERE p.activeStatus = 1")
    List<Property> findAllActive();
}
