package apap.ti._5.accommodation_2306165585_be.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.PropertyTransactionRequest;
import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.AllPropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.property.PropertyService;
import apap.ti._5.accommodation_2306165585_be.utils.ResponseUtil;

@RestController
@RequestMapping("/api")
public class PropertyController {
    
    @Autowired
    ResponseUtil responseUtil;

    @Autowired
    PropertyService propertyService;

    public static final String BASE_URL = "/property";
    public static final String VIEW_PROPERTY = BASE_URL + "/{propertyId}";
    public static final String CREATE_PROPERTY = BASE_URL + "/create";


    @GetMapping(BASE_URL)
    public ResponseEntity<BaseResponseDTO<List<AllPropertyResponseDTO>>> getAllProperty() {
        List<AllPropertyResponseDTO> listProperty = propertyService.getAllProperties();
        return responseUtil.success(
            listProperty,
            "List of all properties fetched successfully",
            HttpStatus.OK
        );
    }
    

    @GetMapping(VIEW_PROPERTY)
    public ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> getPropertyById(@PathVariable String propertyId) {
        PropertyResponseDTO propertyDTO = propertyService.getPropertyById(propertyId);
        if (propertyDTO != null) {
            return responseUtil.success(
                propertyDTO,
                "Property fetched successfully", 
                HttpStatus.OK 
            );
        }
        throw new NotFoundException("Property with ID " + propertyId + " not found");
    }

    @PostMapping(CREATE_PROPERTY)
    public ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> createProperty(@RequestBody PropertyTransactionRequest request) {
        PropertyResponseDTO propertyDTO = propertyService.createPropertyTransaction(request);
        return responseUtil.success(
            propertyDTO,
            "Property created successfully",
            HttpStatus.CREATED
        ); 
    }
}
