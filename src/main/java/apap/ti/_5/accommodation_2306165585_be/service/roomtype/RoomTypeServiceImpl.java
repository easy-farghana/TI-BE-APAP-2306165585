package apap.ti._5.accommodation_2306165585_be.service.roomtype;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.AddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.AddSingularRoomTypeDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306165585_be.security.RoleGroup;
import apap.ti._5.accommodation_2306165585_be.security.UserContext;
import apap.ti._5.accommodation_2306165585_be.service.property.PropertyService;
import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;


@Service
public class RoomTypeServiceImpl implements RoomTypeService {
    @Autowired
    RoomTypeRepository roomTypeRepository;

    @Autowired
    PropertyService propertyService;

    @Autowired
    UserContext userContext;
    
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
    public RoomTypeResponseDTO getRoomTypeById(UUID roomTypeId) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId).orElseThrow(
            () -> new NotFoundException("Room type with id " + roomTypeId + " not found.")
        );
        String role = userContext.getRole();
        if (role.equals(RoleGroup.ACCOMMODATION_OWNER) && !roomType.getProperty().getOwnerID().equals(userContext.getUserID())) {
            throw new SecurityException("You are not authorized to access this room type");
        }
        return mapToRoomTypeDTO(roomType);
    }

    @Override
    public List<RoomTypeResponseDTO> getRoomTypesByProperty(Property property) {
        String role = userContext.getRole();
        if (role.equals(RoleGroup.ACCOMMODATION_OWNER) && !property.getOwnerID().equals(userContext.getUserID())) {
            throw new SecurityException("You are not authorized to access this property");
        }

        return property.getListRoomType().stream()
            .map(this::mapToRoomTypeDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<RoomTypeResponseDTO> getRoomTypesByProperty(Property property, LocalDateTime checkIn, LocalDateTime checkOut) {
        String role = userContext.getRole();
        if (role.equals(RoleGroup.ACCOMMODATION_OWNER) && !property.getOwnerID().equals(userContext.getUserID())) {
            throw new SecurityException("You are not authorized to access this property");
        }

        return property.getListRoomType().stream()
            .map(roomType -> mapToRoomTypeDTO(roomType, checkIn, checkOut))
            .collect(Collectors.toList());
    }

    @Override
    public RoomType createRoomType(AddRoomTypeRequestDTO request, Property property) {
        String role = userContext.getRole();
        if (role.equals(RoleGroup.ACCOMMODATION_OWNER) && !property.getOwnerID().equals(userContext.getUserID())) {
            throw new SecurityException("You are not authorized to access this property");
        }
        
        RoomType roomType = RoomType.builder()
            .name(request.getName())
            .price(request.getPrice())
            .description(request.getDescription())
            .capacity(request.getCapacity())
            .facility(request.getFacility())
            .floor(request.getFloor())
            .listRoom(new ArrayList<>())
            .property(property)
            .build();

        return roomTypeRepository.save(roomType);
    }

    @Override
    public RoomTypeResponseDTO createRoomType(AddSingularRoomTypeDTO request) {
        Property property = propertyService.getRawPropertyById(request.getPropertyID());
        
        String role = userContext.getRole();
        if (role.equals(RoleGroup.ACCOMMODATION_OWNER) && !property.getOwnerID().equals(userContext.getUserID())) {
            throw new SecurityException("You are not authorized to access this property");
        }
        
        RoomType roomType = RoomType.builder()
            .name(request.getName())
            .price(request.getPrice())
            .description(request.getDescription())
            .capacity(request.getCapacity())
            .facility(request.getFacility())
            .floor(request.getFloor())
            .listRoom(new ArrayList<>())
            .property(property)
            .build();
        roomTypeRepository.save(roomType);
        
        return mapToRoomTypeDTO(roomType);
    }

    @Override
    public RoomType updateRoomType(RoomType roomType) {
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

    private RoomTypeResponseDTO mapToRoomTypeDTO(RoomType roomType, LocalDateTime checkIn, LocalDateTime checkOut) {
        List<RoomResponseDTO> roomDTOs = roomService.getRoomsByRoomType(roomType, checkIn, checkOut);
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
