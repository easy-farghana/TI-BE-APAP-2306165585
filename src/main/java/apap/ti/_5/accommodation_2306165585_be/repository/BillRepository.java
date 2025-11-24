package apap.ti._5.accommodation_2306165585_be.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import apap.ti._5.accommodation_2306165585_be.model.Bill;

public interface BillRepository extends JpaRepository<Bill, UUID>{
    @Query("""
        SELECT b FROM Bill b
        WHERE (:customerID IS NULL OR b.customerID = :customerID)
            AND (:serviceName IS NULL OR b.serviceName LIKE %:serviceName%)
            AND (:status IS NULL OR b.status = :status)
    """)
    List<Bill> findAllWithFilters(
            @Param("customerID") UUID customerID,
            @Param("serviceName") String serviceName,
            @Param("status") Integer status
    );

     @Query("""
        SELECT b FROM Bill b
        WHERE b.customerID = :customerID
        AND (:status IS NULL OR b.status = :status)
        ORDER BY
            CASE WHEN :sortBy = 'createdAt' AND :sortDir = 'asc' THEN b.createdAt END ASC,
            CASE WHEN :sortBy = 'createdAt' AND :sortDir = 'desc' THEN b.createdAt END DESC,
            CASE WHEN :sortBy = 'serviceName' AND :sortDir = 'asc' THEN b.serviceName END ASC,
            CASE WHEN :sortBy = 'serviceName' AND :sortDir = 'desc' THEN b.serviceName END DESC
    """)
    List<Bill> findAllWithFiltersForCustomers(
            @Param("customerID") UUID customerID,
            @Param("status") Integer status,
            @Param("sortBy") String sortBy,
            @Param("sortDir") String sortDir
    );

    @Query("""
        SELECT b FROM Bill b
        WHERE (:customerID IS NULL OR b.customerID = :customerID)
        AND (:status IS NULL OR b.status = :status)
        AND LOWER(b.serviceName) = LOWER(:serviceName)
    """)
    List<Bill> findServiceBills(
            @Param("customerID") UUID customerID,
            @Param("status") Integer status,
            @Param("serviceName") String serviceName
    );
}
