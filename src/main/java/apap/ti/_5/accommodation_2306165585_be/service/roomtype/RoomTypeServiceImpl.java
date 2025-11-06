package apap.ti._5.accommodation_2306165585_be.service.roomtype;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.AddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;

@Service
public class RoomTypeServiceImpl implements RoomTypeService {
    @Autowired
    RoomTypeRepository roomTypeRepository;

    @Autowired
    RoomService roomService;


    @Override
    public List<RoomTypeResponseDTO> getAllRoomTypes() {
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        return roomTypes.stream()
            .map(this::mapToRoomTypeDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<RoomTypeResponseDTO> getRoomTypesByProperty(Property property) {
        return property.getListRoomType().stream()
            .map(this::mapToRoomTypeDTO)
            .collect(Collectors.toList());
    }

    @Override
    public RoomType createRoomType(AddRoomTypeRequestDTO request, Property property) {
        String propertyNumber = property.getPropertyID().substring(9, 12);
        String roomTypeId = String.format("%s-%s-%d", propertyNumber, request.getName(), request.getFloor());

        RoomType roomType = RoomType.builder()
            .roomTypeID(roomTypeId)
            .name(request.getName())
            .price(request.getPrice())
            .description(request.getDescription())
            .capacity(request.getCapacity())
            .facility(request.getFacility())
            .floor(request.getFloor())
            .listRoom(new ArrayList<>())
            .build();
        return roomTypeRepository.save(roomType);
    }

    private RoomTypeResponseDTO mapToRoomTypeDTO(RoomType roomType) {
        List<RoomResponseDTO> roomDTOs = roomService.getRoomsByRoomType(roomType);
        return RoomTypeResponseDTO.builder()
            .roomTypeID(roomType.getRoomTypeID())
            .name(roomType.getName())
            .price(roomType.getPrice())
            .description(roomType.getDescription())
            .capacity(roomType.getCapacity())
            .facility(roomType.getFacility())
            .floor(roomType.getFloor())
            .listRoom(roomDTOs)
            .build();
    }
}
