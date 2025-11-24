package apap.ti._5.accommodation_2306165585_be.service.property;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.AddPropertyRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.PropertyTransactionRequest;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.UpdatePropertyTransactionRequest;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.ListAddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.AllPropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.statistics.IncomeStatisticsDTO;

public interface PropertyService {
    PropertyResponseDTO createPropertyTransaction(PropertyTransactionRequest request);
    PropertyResponseDTO updatePropertyTransaction(UpdatePropertyTransactionRequest request);
    Property createProperty(AddPropertyRequestDTO request);
    List<AllPropertyResponseDTO> getAllProperties();
    List<AllPropertyResponseDTO> getAllProperties(Map<String, Object> params);
    PropertyResponseDTO getPropertyById(UUID propertyId);
    void deleteProperty(UUID propertyId);
    PropertyResponseDTO addRoomTypeToProperty(UUID propertyId, ListAddRoomTypeRequestDTO request);
    PropertyResponseDTO getPropertyById(UUID propertyId, LocalDateTime checkIn, LocalDateTime checkOut);
    List<AllPropertyResponseDTO> getAllActiveProperties();
    IncomeStatisticsDTO getIncomeStatistics(int month, int year);
    List<AllPropertyResponseDTO> getAllActiveProperties(Map<String, Object> params);
}
