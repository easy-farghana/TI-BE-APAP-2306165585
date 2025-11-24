package apap.ti._5.accommodation_2306165585_be.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import apap.ti._5.accommodation_2306165585_be.model.Property;

import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, UUID> {
    @Query("SELECT p FROM Property p WHERE p.activeStatus = 1 and p.propertyID = ?1")
    Optional<Property> findByIdActive(UUID propertyID);
    
    @Query("SELECT p FROM Property p WHERE p.activeStatus = 1")
    List<Property> findAllActive();

    @Query("""
        SELECT p FROM Property p
        WHERE (:name IS NULL OR p.propertyName LIKE %:name%)
          AND (:type IS NULL OR p.type = :type)
          AND (:province IS NULL OR p.province = :province)
          AND (:ownerID IS NULL OR p.ownerID = :ownerID)
    """)
    List<Property> findByFilters(
            @Param("name") String name,
            @Param("type") Integer type,
            @Param("province") Integer province,
            @Param("ownerID") UUID ownerID
    );

    @Query("""
        SELECT p FROM Property p
        WHERE (:name IS NULL OR p.propertyName LIKE %:name%)
          AND (:type IS NULL OR p.type = :type)
          AND (:province IS NULL OR p.province = :province)
          AND (:ownerID IS NULL OR p.ownerID = :ownerID)
          AND p.activeStatus = 1
    """)
    List<Property> findByFiltersAndActive(
            @Param("name") String name,
            @Param("type") Integer type,
            @Param("province") Integer province,
            @Param("ownerID") UUID ownerID
    );

}
