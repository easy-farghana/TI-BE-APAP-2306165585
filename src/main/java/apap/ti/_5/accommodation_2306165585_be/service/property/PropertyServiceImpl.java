package apap.ti._5.accommodation_2306165585_be.service.property;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.Room;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.AddPropertyRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.PropertyTransactionRequest;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.AddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.AllPropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;
import apap.ti._5.accommodation_2306165585_be.service.roomtype.RoomTypeService;
import jakarta.transaction.Transactional;
@Service
public class PropertyServiceImpl implements PropertyService {
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private RoomTypeService roomTypeService;

    @Autowired
    private RoomService roomService;

    @Override
    public List<AllPropertyResponseDTO> getAllProperties() {
        List<Property> properties = propertyRepository.findAll();
        return properties.stream()
            .map(this::mapToAllPropertyDTO)
            .toList();
    }

    @Override
    public PropertyResponseDTO getPropertyById(String propertyId) {
        return propertyRepository.findById(propertyId)
            .map(this::mapToPropertyDTO)
            .orElse(null);
    }
    
    @Transactional
    @Override
    public PropertyResponseDTO createPropertyTransaction(PropertyTransactionRequest request) {
        List<RoomType> roomTypes = new ArrayList<>();
        Set<Pair<String, Integer>> roomTypeCombination = new HashSet<>();
        Property property = createProperty(request.getProperty());
        int roomCount = 0;
        // Create Room Types 
        for (AddRoomTypeRequestDTO roomTypeRequest: request.getRoomTypes()) {
            String roomTypeName = roomTypeRequest.getName();
            Integer roomTypeFloor = roomTypeRequest.getFloor();
            if (roomTypeCombination.contains(Pair.of(roomTypeName, roomTypeFloor))) {
                throw new IllegalArgumentException("Duplicate room type name and floor combination: " 
                    + roomTypeRequest.getName() + " on floor " + roomTypeRequest.getFloor());
            }
            RoomType roomType = roomTypeService.createRoomType(roomTypeRequest, property);

            // Create Rooms for each Room Type
            List<Room> rooms = new ArrayList<>();
            roomType.setListRoom(rooms);

            int units = roomTypeRequest.getUnit();
            roomCount += units;
            for (int i = 0; i < units; i++) {
                Room room = roomService.createRoom(property, roomType);
                rooms.add(room);
            }

            roomTypeCombination.add(Pair.of(roomTypeName, roomTypeFloor));
            roomTypes.add(roomType);
        }
        property.setTotalRoom(roomCount);
        property.setListRoomType(roomTypes);
        return mapToPropertyDTO(property);
    }


    @Transactional
    @Override
    public Property createProperty(AddPropertyRequestDTO request) {
        String typePrefix = getTypePrefix(request.getType());
        String ownerUuid = request.getOwnerId().toString();
        String lastFourChars = ownerUuid.substring(ownerUuid.length() - 4);
        String propertyId = String.format("%s-%s-%03d", typePrefix, lastFourChars, propertyRepository.count() + 1);

        Property property = Property.builder()
            .propertyID(propertyId)
            .propertyName(request.getPropertyName())
            .type(request.getType())
            .address(request.getAddress())
            .province(request.getProvince())
            .description(request.getDescription())
            .income(0)
            .ownerID(request.getOwnerId())
            .ownerName(request.getOwnerName())
            .build();

        return propertyRepository.save(property);
    }

    private String getTypePrefix(int type) {
        return switch (type) {
            case 1 -> "HOT";
            case 2 -> "VIL";
            case 3 -> "APT";
            default -> "HOT";
        }; 
    }

    private AllPropertyResponseDTO mapToAllPropertyDTO(Property property) {
        return AllPropertyResponseDTO.builder()
            .propertyID(property.getPropertyID())
            .propertyName(property.getPropertyName())
            .type(property.getType())
            .totalRooms(property.getTotalRoom())
            .activeStatus(property.getActiveStatus())
            .build();
    }

    private PropertyResponseDTO mapToPropertyDTO(Property property) {
        List<RoomTypeResponseDTO> roomTypeDTOs = roomTypeService.getRoomTypesByProperty(property);

        return PropertyResponseDTO.builder()
            .propertyID(property.getPropertyID())
            .propertyName(property.getPropertyName())
            .type(property.getType())
            .address(property.getAddress())
            .province(property.getProvince())
            .description(property.getDescription())
            .totalRoom(property.getTotalRoom())
            .income(property.getIncome())
            .activeStatus(property.getActiveStatus())
            .listRoomType(roomTypeDTOs)
            .ownerID(property.getOwnerID())
            .ownerName(property.getOwnerName())
            .createdDate(property.getCreatedDate())
            .updatedDate(property.getUpdatedDate())
            .build();
    }
}
