package apap.ti._5.accommodation_2306165585_be.restcontroller;

import org.springframework.web.bind.annotation.RestController;

import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;
import apap.ti._5.accommodation_2306165585_be.service.roomtype.RoomTypeService;
import apap.ti._5.accommodation_2306165585_be.utils.ResponseUtil;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype.RoomTypeResponseDTO;



@RestController
@RequestMapping("/api")
public class RoomGlobalController {
    @Autowired
    ResponseUtil responseUtil;

    @Autowired
    RoomTypeService roomTypeService;

    @Autowired
    RoomService roomService;

    public static final String BASE_URL_TYPE = "/room-type";
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
