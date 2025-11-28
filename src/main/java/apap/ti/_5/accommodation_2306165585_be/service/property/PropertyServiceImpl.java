package apap.ti._5.accommodation_2306165585_be.service.property;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.Room;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.AddPropertyRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.PropertyTransactionRequest;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.UpdatePropertyTransactionRequest;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.AddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.ListAddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.AllPropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.statistics.IncomeStatisticsDTO;
import apap.ti._5.accommodation_2306165585_be.security.RoleGroup;
import apap.ti._5.accommodation_2306165585_be.security.UserContext;
import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;
import apap.ti._5.accommodation_2306165585_be.service.roomtype.RoomTypeService;
import apap.ti._5.accommodation_2306165585_be.exception.SecurityException;

import jakarta.transaction.Transactional;

@Service
public class PropertyServiceImpl implements PropertyService {
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private RoomTypeService roomTypeService;

    @Autowired
    private RoomService roomService;

    @Autowired
    UserContext userContext;

    @Override
    public List<AllPropertyResponseDTO> getAllProperties(Map<String, Object> params) {
        String name = (String) params.get("name");
        Integer type = (Integer) params.get("type");
        Integer province = (Integer) params.get("province");

        // Accommodation owner only gets their own properties
        UUID ownerID = getOwnerID();

        List<Property> properties = propertyRepository.findByFilters(name, type, province, ownerID);

        return properties.stream()
                .map(this::mapToAllPropertyDTO)
                .toList();
    }

    @Override
    public List<AllPropertyResponseDTO> getAllProperties() {
        List<Property> properties = propertyRepository.findAll();
        return properties.stream()
                .map(this::mapToAllPropertyDTO)
                .toList();
    }

    
    @Override
    public List<AllPropertyResponseDTO> getAllActiveProperties() {
        List<Property> properties = propertyRepository.findAllActive();
        return properties.stream()
            .map(this::mapToAllPropertyDTO)
            .toList();
    }

    @Override
    public List<AllPropertyResponseDTO> getAllActiveProperties(Map<String, Object> params) {
        String name = (String) params.get("name");
        Integer type = (Integer) params.get("type");
        Integer province = (Integer) params.get("province");

        // Accommodation owner only gets their own properties
        UUID ownerID = getOwnerID();

        List<Property> properties = propertyRepository.findByFiltersAndActive(name, type, province, ownerID);

        return properties.stream()
                .map(this::mapToAllPropertyDTO)
                .toList();
    }

    @Override
    public PropertyResponseDTO getPropertyById(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(
            () -> new NotFoundException("Property not found with ID: " + propertyId)
        );
        
        // Accommodation owner only can only see their own properties
        UUID ownerID = getOwnerID();
        String role = userContext.getRole();
        if (role.equals(RoleGroup.ACCOMMODATION_OWNER) && !property.getOwnerID().equals(ownerID)) {
            throw new NotFoundException("Property not found with ID: " + propertyId);
        }

        return mapToPropertyDTO(property);
    }

    @Override
    public Property getRawPropertyById(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(
            () -> new NotFoundException("Property not found with ID: " + propertyId)
        );
        
        // Accommodation owner only can only see their own properties
        UUID ownerID = getOwnerID();
        String role = userContext.getRole();
        if (role.equals(RoleGroup.ACCOMMODATION_OWNER) && !property.getOwnerID().equals(ownerID)) {
            throw new NotFoundException("Property not found with ID: " + propertyId);
        }

        return property;
    }


    /**
     * Retrieves a property by ID, and also checks if the property is available
     * between the given check-in and check-out dates.
     *
     * @param propertyId The ID of the property to be retrieved.
     * @param checkIn The check-in date.
     * @param checkOut The check-out date.
     * @return The PropertyResponseDTO of the retrieved property.
     * @throws NotFoundException If the property is not found with the given ID.
     * @throws IllegalArgumentException If the check-in date is after the check-out date.
     */
    @Override
    public PropertyResponseDTO getPropertyById(UUID propertyId, LocalDateTime checkIn, LocalDateTime checkOut) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(
            () -> new NotFoundException("Property not found with ID: " + propertyId)
        );

        if (checkIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }

        return mapToPropertyDTO(property, checkIn, checkOut);
    }

    @Override
    public IncomeStatisticsDTO getIncomeStatistics(int month, int year) {
        List<Property> properties = propertyRepository.findAll()
            .stream()
            .filter(p -> p.getActiveStatus() == 1)
            .toList();

        List<String> propertyNames = new ArrayList<>();
        List<Integer> propertyIncomes = new ArrayList<>();
        int totalIncome = 0;

        for (Property property : properties) {
            int propertyIncome = 0;

            for (RoomType roomType : property.getListRoomType()) {
                for (Room room : roomType.getListRoom()) {
                    for (AccommodationBooking booking : room.getListAccommodationBooking()) {
                        LocalDateTime checkIn = booking.getCheckInDate();
                        if (checkIn.getMonthValue() == month && checkIn.getYear() == year) {
                            propertyIncome += booking.getTotalPrice();
                        }
                    }
                }
            }

            propertyNames.add(property.getPropertyName());
            propertyIncomes.add(propertyIncome);
            totalIncome += propertyIncome;
        }

        return IncomeStatisticsDTO.builder()
            .propertyNames(propertyNames)
            .propertyIncomes(propertyIncomes)
            .totalIncome(totalIncome)
            .build();
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
    public PropertyResponseDTO updatePropertyTransaction(UpdatePropertyTransactionRequest request) {
        UUID propertyId = request.getProperty().getPropertyId();
        Property property = propertyRepository.findByIdActive(propertyId)
                .orElseThrow(() -> new NotFoundException("Property not found with ID: " + propertyId));

        String role = userContext.getRole();
        if (role.equals(RoleGroup.ACCOMMODATION_OWNER) && !property.getOwnerID().equals(userContext.getUserID())) {
            throw new SecurityException("Not authorized to modify property with ID: " + propertyId);
        }

        // Update property details
        property.setType(request.getProperty().getType());
        property.setProvince(request.getProperty().getProvince());
        property.setPropertyName(request.getProperty().getPropertyName());
        property.setAddress(request.getProperty().getAddress());
        property.setDescription(request.getProperty().getDescription());

        // Map existing room types by ID
        Map<UUID, RoomType> existingRoomTypeMap = property.getListRoomType().stream()
                .collect(Collectors.toMap(RoomType::getRoomTypeID, rt -> rt));

        // Check for missing or extra room types
        Set<UUID> requestRoomTypeIds = request.getRoomTypes().stream()
                .map(UpdateRoomTypeRequestDTO::getRoomTypeID)
                .collect(Collectors.toSet());

        if (!existingRoomTypeMap.keySet().equals(requestRoomTypeIds)) {
            throw new IllegalArgumentException("Room types mismatch. Cannot add or remove room types in this update.");
        }

        // Update room types
        for (UpdateRoomTypeRequestDTO roomTypeRequest : request.getRoomTypes()) {
            RoomType roomType = existingRoomTypeMap.get(roomTypeRequest.getRoomTypeID());

            // Update room type details
            roomType.setFacility(roomTypeRequest.getFacility());
            roomType.setPrice(roomTypeRequest.getPrice());
            roomType.setDescription(roomTypeRequest.getDescription());
            roomType.setCapacity(roomTypeRequest.getCapacity());

            roomTypeService.updateRoomType(roomType);
        }

        property = propertyRepository.save(property);
        return mapToPropertyDTO(property);
    }


    @Transactional
    @Override
    public Property createProperty(AddPropertyRequestDTO request) {
        // Check if its request from an accommodation_owner first
        UUID ownerID = getOwnerID();
        if (ownerID != null) {
            request.setOwnerId(ownerID);
            request.setOwnerName(userContext.getName());
        } else if (ownerID == null && request.getOwnerId() == null) {
            throw new IllegalArgumentException("Missing owner ID");
        }


        Property property = Property.builder()
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

    @Override
    public PropertyResponseDTO addRoomTypeToProperty(UUID propertyId, ListAddRoomTypeRequestDTO request) {
        Property property = propertyRepository.findByIdActive(propertyId)
            .orElseThrow(() -> new NotFoundException("Property not found or inactive with ID: " + propertyId));

        List<AddRoomTypeRequestDTO> roomTypeRequest = request.getRoomTypes();
        List<RoomType> roomTypes = property.getListRoomType();
        Set<Pair<String, Integer>> roomTypeCombination = new HashSet<>();

        // Existing Room Type Combination
        for (RoomType rt : roomTypes) {
            roomTypeCombination.add(Pair.of(rt.getName(), rt.getFloor()));
        }

        int roomCount = property.getTotalRoom();

        // Create Room Types
        for (AddRoomTypeRequestDTO req : roomTypeRequest) {
            String roomTypeName = req.getName();
            Integer roomTypeFloor = req.getFloor();
            if (roomTypeCombination.contains(Pair.of(roomTypeName, roomTypeFloor))) {
                throw new IllegalArgumentException("Duplicate room type name and floor combination: " 
                    + req.getName() + " on floor " + req.getFloor());
            }

            RoomType roomType = roomTypeService.createRoomType(req, property);

            // Create Rooms for each Room Type
            List<Room> rooms = new ArrayList<>();
            roomType.setListRoom(rooms);

            int units = req.getUnit();
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
        propertyRepository.save(property);

        return mapToPropertyDTO(property);
    }

    @Override
    public void deleteProperty(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new NotFoundException("Property not found with ID: " + propertyId));

        if (!haveNoFutureBookings(property)) {
            throw new IllegalArgumentException("Cannot delete property with future bookings");
        }

        // Soft delete all rooms first (keep relationships intact)
        List<RoomType> roomTypes = property.getListRoomType();
        for (RoomType roomType : roomTypes) {
            List<Room> rooms = roomType.getListRoom();
            for (Room room : rooms) {
                roomService.deleteRoom(room.getRoomID());
            }
        }

        // Soft delete the property
        property.setActiveStatus(0);
        propertyRepository.save(property);
    }

    /**
     * Checks if the current user is an owner of a property with the given ID.
     * 
     * @return The owner ID if the current user has the role ACCOMMODATION_OWNER, null otherwise.
     */
    private UUID getOwnerID() {
        String role = userContext.getRole();
        UUID ownerID = null;
        if (role.equals(RoleGroup.ACCOMMODATION_OWNER)) {
            ownerID = userContext.getUserID(); 
        } 
        return ownerID;
    }

    private boolean haveNoFutureBookings(Property property) {
        LocalDateTime now = LocalDateTime.now();
        for (RoomType roomType : property.getListRoomType()) {
            for (Room room : roomType.getListRoom()) {
                boolean hasFutureBooking = room.getListAccommodationBooking().stream()
                    .anyMatch(booking -> booking.getCheckOutDate().isAfter(now));
                if (hasFutureBooking) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Maps a property to a simpler DTO so that it can be used for all properties
     * and not be to big for the response
     * 
     * @param property The Property to be converted.
     * @return a simple DTO for the property.
     */ 
    private AllPropertyResponseDTO mapToAllPropertyDTO(Property property) {
        return AllPropertyResponseDTO.builder()
            .propertyID(property.getPropertyID())
            .propertyName(property.getPropertyName())
            .type(property.getType())
            .totalRooms(property.getTotalRoom())
            .activeStatus(property.getActiveStatus())
            .build();
    }
    
    /**
     * Maps a property to a detailed DTO 
     * 
     * @param property The Property to be converted.
     * @return a detailed DTO for the property.
     */ 
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

    private PropertyResponseDTO mapToPropertyDTO(Property property, LocalDateTime checkIn, LocalDateTime checkOut) {
        List<RoomTypeResponseDTO> roomTypeDTOs = roomTypeService.getRoomTypesByProperty(property, checkIn, checkOut);

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
