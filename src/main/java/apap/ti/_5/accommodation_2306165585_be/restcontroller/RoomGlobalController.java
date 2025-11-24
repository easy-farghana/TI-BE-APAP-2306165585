package apap.ti._5.accommodation_2306165585_be.restcontroller;

import org.springframework.web.bind.annotation.RestController;

import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;
import apap.ti._5.accommodation_2306165585_be.service.roomtype.RoomTypeService;
import apap.ti._5.accommodation_2306165585_be.utils.ResponseUtil;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype.RoomTypeResponseDTO;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/api")
public class RoomGlobalController {
    @Autowired
    ResponseUtil responseUtil;

    @Autowired
    RoomTypeService roomTypeService;

    @Autowired
    RoomService roomService;

    @Autowired
    PropertyRepository propertyRepository;

    public static final String BASE_URL_TYPE = "/room-type";
    public static final String VIEW_TYPES_BY_PROPERTY = BASE_URL_TYPE + "property/{propertyId}";
    public static final String VIEW_TYPE = BASE_URL_TYPE + "/{roomTypeId}";
    public static final String CREATE_TYPE = BASE_URL_TYPE + "/create";

    public static final String BASE_URL_ROOM = "/room";


    @GetMapping(BASE_URL_TYPE)
    public ResponseEntity<BaseResponseDTO<List<RoomTypeResponseDTO>>> getAllRoomType() {
        List<RoomTypeResponseDTO> listRoomType = roomTypeService.getAllRoomTypes();
        return responseUtil.success(
            listRoomType,
            "List of all room types fetched successfully",
            HttpStatus.OK
        );
    }

    @GetMapping(VIEW_TYPES_BY_PROPERTY)
    public ResponseEntity<BaseResponseDTO<List<RoomTypeResponseDTO>>> getRoomTypesByPropertyId(@PathVariable UUID propertyId) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(
            () -> new IllegalArgumentException("Property not found with ID: " + propertyId)
        );

        List<RoomTypeResponseDTO> listRoomType = roomTypeService.getRoomTypesByProperty(property);
        return responseUtil.success(
            listRoomType,
            "List of all room types with property id " + propertyId + " fetched successfully",
            HttpStatus.OK
        );  
    }

    // @GetMapping(VIEW_TYPE)
    // public ResponseEntity<BaseResponseDTO<List<RoomTypeResponseDTO>>> getRoomType(@PathVariable UUID roomTypeId) {

    //     List<RoomTypeResponseDTO> listRoomType = roomTypeService.getRoomType(roomTypeId);
    //     return responseUtil.success(
    //         listRoomType,
    //         "Room type details id " + roomTypeId + " fetched successfully",
    //         HttpStatus.OK
    //     );  
    // }
    
    @GetMapping(BASE_URL_ROOM)
    public ResponseEntity<BaseResponseDTO<List<RoomResponseDTO>>> getAllRoom() {
        List<RoomResponseDTO> listRoom = roomService.getAllRooms();
        return responseUtil.success(
            listRoom,
            "List of all rooms fetched successfully",
            HttpStatus.OK
        );
    }
}
