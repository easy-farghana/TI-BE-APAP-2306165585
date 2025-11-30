package apap.ti._5.accommodation_2306165585_be.restcontroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import apap.ti._5.accommodation_2306165585_be.restdto.request.room.AddMaintenanceRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.ListAddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.AllPropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.property.PropertyService;
import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;
import apap.ti._5.accommodation_2306165585_be.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class PropertyController {
    
    @Autowired
    ResponseUtil responseUtil;

    @Autowired
    PropertyService propertyService;

    @Autowired
    RoomService roomService;

    public static final String BASE_URL = "/property";
    public static final String VIEW_ALL_ACTIVE = "/property-active";
    public static final String VIEW_PROPERTY = BASE_URL + "/{propertyId}";
    public static final String CREATE_PROPERTY = BASE_URL + "/create";
    public static final String UPDATE_PROPERTY = BASE_URL + "/update";
    public static final String DELETE_PROPERTY = BASE_URL + "/delete/{propertyId}";
    public static final String ADD_ROOM_TYPE = BASE_URL + "/add-room-type/{propertyId}";
    public static final String ADD_MAINTENANCE = BASE_URL + "/maintenance/add";


    /**
     * Fetch list of all properties based on given parameters.
     * 
     * @param name Name of the property.
     * @param type Type of the property.
     * @param province Province of the property.
     * @return ResponseEntity containing list of all properties.
     */
    @GetMapping(BASE_URL)
    public ResponseEntity<BaseResponseDTO<List<AllPropertyResponseDTO>>> getAllProperty(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "type", required = false) Integer type,
        @RequestParam(value = "province", required = false) Integer province
    ) {
        
        Map<String, Object> params = new HashMap<>();
        if (name != null) params.put("name", name);
        if (type != null) params.put("type", type);
        if (province != null) params.put("province", province);

        List<AllPropertyResponseDTO> listProperty = propertyService.getAllProperties(params);
        return responseUtil.success(
            listProperty,
            "List of all properties fetched successfully",
            HttpStatus.OK
        );
    }

    /**
     * Fetch list of all active properties based on given parameters.
     * 
     * @param name Name of the property.
     * @param type Type of the property.
     * @param province Province of the property.
     * @return ResponseEntity containing list of all active properties.
     */
    @GetMapping(VIEW_ALL_ACTIVE)
    public ResponseEntity<BaseResponseDTO<List<AllPropertyResponseDTO>>> getAllActiveProperty(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "type", required = false) Integer type,
        @RequestParam(value = "province", required = false) Integer province
    ) {
        Map<String, Object> params = new HashMap<>();
        if (name != null) params.put("name", name);
        if (type != null) params.put("type", type);
        if (province != null) params.put("province", province);
        List<AllPropertyResponseDTO> listProperty = propertyService.getAllActiveProperties(params);
        return responseUtil.success(
            listProperty,
            "List of all properties fetched successfully",
            HttpStatus.OK
        );
    }
    

    /**
     * Retrieves a property by its ID.
     * If check-in and check-out dates are provided, it also checks if the property is available between the given dates.
     * 
     * @param propertyId The ID of the property to be retrieved.
     * @param checkIn The check-in date.
     * @param checkOut The check-out date.
     * @return The PropertyResponseDTO of the retrieved property.
     * @throws NotFoundException If the property is not found with the given ID.
     * @throws IllegalArgumentException If the check-in date is after the check-out date.
     */
    @GetMapping(VIEW_PROPERTY)
    public ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> getPropertyById(
        @PathVariable("propertyId") UUID propertyId, 
        @RequestParam(required = false) String checkIn, 
        @RequestParam(required = false) String checkOut
    ) {

        PropertyResponseDTO propertyDTO;
        if (checkIn != null && checkOut != null) {
            log.info("Property with filters: " + propertyId + ", " + checkIn + ", " + checkOut);
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
    public ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> createProperty(@Valid @RequestBody PropertyTransactionRequest request) {
        PropertyResponseDTO propertyDTO = propertyService.createPropertyTransaction(request);
        return responseUtil.success(
            propertyDTO,
            "Property created successfully",
            HttpStatus.CREATED
        ); 
    }

    @PutMapping(UPDATE_PROPERTY)
    public ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> updateProperty(@Valid @RequestBody UpdatePropertyTransactionRequest request) {
        PropertyResponseDTO propertyDTO = propertyService.updatePropertyTransaction(request);
        return responseUtil.success(
            propertyDTO,
            "Property updated successfully",
            HttpStatus.OK
        ); 
    }

    @DeleteMapping(DELETE_PROPERTY)
    public ResponseEntity<BaseResponseDTO<String>> deleteProperty(@PathVariable("propertyId") UUID propertyId) {
        propertyService.deleteProperty(propertyId);
        return responseUtil.success(
            null,
            "Property with ID " + propertyId + " deleted successfully",
            HttpStatus.OK
        );
    }
    @PostMapping(ADD_MAINTENANCE)
    public ResponseEntity<BaseResponseDTO<String>> addMaintenanceToProperty(@RequestBody AddMaintenanceRequestDTO request) {
        roomService.addMaintenance(request);
        return responseUtil.success(
            null,
            "Maintenance added to property successfully",
            HttpStatus.OK
        );
    }

    @PostMapping(ADD_ROOM_TYPE)
    public ResponseEntity<BaseResponseDTO<PropertyResponseDTO>> addRoomTypeToProperty(
        @PathVariable("propertyId") UUID propertyId,
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
