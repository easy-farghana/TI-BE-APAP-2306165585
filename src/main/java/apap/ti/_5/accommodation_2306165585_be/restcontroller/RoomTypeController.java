package apap.ti._5.accommodation_2306165585_be.restcontroller;

import org.springframework.web.bind.annotation.RestController;

import apap.ti._5.accommodation_2306165585_be.service.roomtype.RoomTypeService;
import apap.ti._5.accommodation_2306165585_be.utils.ResponseUtil;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.AllPropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype.RoomTypeResponseDTO;



@RestController
@RequestMapping("/api")
public class RoomTypeController {
    @Autowired
    ResponseUtil responseUtil;

    @Autowired
    RoomTypeService roomTypeService;

    public static final String BASE_URL = "/room-type";

    @GetMapping(BASE_URL)
    public ResponseEntity<BaseResponseDTO<List<RoomTypeResponseDTO>>> getAllRoomType() {
        List<RoomTypeResponseDTO> listRoomType = roomTypeService.getAllRoomTypes();
        return responseUtil.success(
            listRoomType,
            "List of all room types fetched successfully",
            HttpStatus.OK
        );
    }
}
