package apap.ti._5.accommodation_2306165585_be.service.property;

import java.util.List;

import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.AddPropertyRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.PropertyTransactionRequest;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.AllPropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.PropertyResponseDTO;

public interface PropertyService {
    PropertyResponseDTO createPropertyTransaction(PropertyTransactionRequest request);
    Property createProperty(AddPropertyRequestDTO request);
    List<AllPropertyResponseDTO> getAllProperties();
    PropertyResponseDTO getPropertyById(String propertyId);

}
