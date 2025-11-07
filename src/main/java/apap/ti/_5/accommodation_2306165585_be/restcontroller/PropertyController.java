package apap.ti._5.accommodation_2306165585_be.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.PropertyTransactionRequest;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.UpdatePropertyTransactionRequest;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.ListAddRoomTypeRequestDTO;
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
    public static final String UPDATE_PROPERTY = BASE_URL + "/update";
    public static final String DELETE_PROPERTY = BASE_URL + "/delete/{propertyId}";
    public static final String ADD_ROOM_TYPE = BASE_URL + "/add-room-type/{propertyId}";

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
    public ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> getPropertyById(
        @PathVariable String propertyId, 
        @RequestParam(required = false) String checkIn, 
        @RequestParam(required = false) String checkOut) {

        PropertyResponseDTO propertyDTO;
        if (checkIn != null && checkOut != null) {
            propertyDTO = propertyService.getPropertyById(
                propertyId, 
                java.time.LocalDateTime.parse(checkIn), 
                java.time.LocalDateTime.parse(checkOut)
            );
        } else {
            propertyDTO = propertyService.getPropertyById(propertyId);
        }

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

    @PutMapping(UPDATE_PROPERTY)
    public ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> updateProperty(@RequestBody UpdatePropertyTransactionRequest request) {
        PropertyResponseDTO propertyDTO = propertyService.updatePropertyTransaction(request);
        return responseUtil.success(
            propertyDTO,
            "Property updated successfully",
            HttpStatus.OK
        ); 
    }

    @DeleteMapping(DELETE_PROPERTY)
    public ResponseEntity<BaseResponseDTO<String>> deleteProperty(@PathVariable String propertyId) {
        propertyService.deleteProperty(propertyId);
        return responseUtil.success(
            null,
            "Property with ID " + propertyId + " deleted successfully",
            HttpStatus.OK
        );
    }

    @PostMapping(ADD_ROOM_TYPE)
    public ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> addRoomTypeToProperty(
        @PathVariable String propertyId,
        @RequestBody ListAddRoomTypeRequestDTO request
    ) {
        PropertyResponseDTO propertyDTO = propertyService.addRoomTypeToProperty(propertyId, request);
        return responseUtil.success(
            propertyDTO,
            "Room types added to property successfully",
            HttpStatus.OK
        );
    }

}
