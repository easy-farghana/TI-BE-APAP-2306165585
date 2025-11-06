// package apap.ti._5.accommodation_2306165585_be.config;

// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Random;
// import java.util.UUID;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import apap.ti._5.accommodation_2306165585_be.model.AccommodationBooking;
// import apap.ti._5.accommodation_2306165585_be.model.Property;
// import apap.ti._5.accommodation_2306165585_be.model.Room;
// import apap.ti._5.accommodation_2306165585_be.model.RoomType;
// import apap.ti._5.accommodation_2306165585_be.repository.AccommodationBookingRepository;
// import apap.ti._5.accommodation_2306165585_be.repository.PropertyRepository;
// import apap.ti._5.accommodation_2306165585_be.repository.RoomRepository;
// import apap.ti._5.accommodation_2306165585_be.repository.RoomTypeRepository;
// import jakarta.annotation.PostConstruct;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Component
// public class DummyDataLoader {

//     @Autowired
//     private PropertyRepository propertyRepository;

//     @Autowired
//     private RoomTypeRepository roomTypeRepository;

//     @Autowired
//     private RoomRepository roomRepository;
    
//     @Autowired
//     private AccommodationBookingRepository accommodationBookingRepository;

//     private final Random random = new Random();

//     @PostConstruct
//     @Transactional
//     public void init() {
//         log.info("Loading dummy data...");
        
//         // Check if data already exists
//         if (propertyRepository.count() > 0) {
//             log.info("Dummy data already exists, skipping...");
//             return;
//         }
        
//         // Create dummy data
//         createDummyProperties();
        
//         log.info("Dummy data loaded successfully!");
//     }
    
//     private void createDummyProperties() {
//         // Sample owner UUIDs (last 4 characters will be used for property ID generation)
//         String[] ownerUuids = {
//             "550e8400-e29b-41d4-a716-446655440000", // 0000
//             "550e8400-e29b-41d4-a716-446655440001", // 0001
//             "550e8400-e29b-41d4-a716-446655440002", // 0002
//             "550e8400-e29b-41d4-a716-446655440003", // 0003
//             "550e8400-e29b-41d4-a716-446655440004"  // 0004
//         };
        
//         String[] ownerNames = {
//             "John Smith", "Sarah Johnson", "Michael Brown", "Emily Davis", "David Wilson"
//         };
        
//         String[] provinces = {
//             "Jakarta", "Bali", "Yogyakarta", "Bandung", "Surabaya"
//         };
        
//         // Create 15 properties (5 of each type)
//         for (int i = 0; i < 15; i++) {
//             int type = (i % 3) + 1; // 1=Hotel, 2=Villa, 3=Apartment
//             String typePrefix = getTypePrefix(type);
//             String ownerUuid = ownerUuids[i % ownerUuids.length];
//             String lastFourChars = ownerUuid.substring(ownerUuid.length() - 4);
//             String propertyId = String.format("%s-%s-%03d", typePrefix, lastFourChars, i + 1);
            
//             Property property = Property.builder()
//                 .propertyID(propertyId)
//                 .propertyName(generatePropertyName(type, i + 1))
//                 .type(type)
//                 .address(generateAddress(provinces[i % provinces.length]))
//                 .province(i % 5 + 1) // 1-5
//                 .description(generatePropertyDescription(type))
//                 .activeStatus(1)
//                 .totalRoom(0)
//                 .income(0)
//                 .ownerName(ownerNames[i % ownerNames.length])
//                 .ownerID(UUID.fromString(ownerUuid))
//                 .listRoomType(new ArrayList<>())
//                 .build();
            
//             // Create room types for this property
//             createRoomTypesForProperty(property, i + 1, type);
//             property = propertyRepository.save(property);
            
//         }
//     }
    
//     private void createRoomTypesForProperty(Property property, int propertyNumber, int type) {
//         String[] roomTypeHotel = {
//                 "Standard Room", "Double Room", "Deluxe Room", "Suite", "Family Room", "Superior Room"
//         };
        
//         String[] roomTypeVilla = {
//             "Luxury", "Beachfront", "Mountside", "Eco-friendly", "Romantic"
//         };
        
//         String[] roomTypeApartment = {
//             "Studio", "1BR", "2BR", "3BR", "Penthouse"
//         };
        
//         String[] facilities = {
//             "WiFi, AC, TV, Mini Bar", "WiFi, AC, TV, Balcony, Sea View", 
//             "WiFi, AC, TV, Kitchen, Living Room", "WiFi, AC, TV, Pool Access",
//             "WiFi, AC, TV, Jacuzzi, Private Pool"
//         };
        
//         int numRoomTypes = random.nextInt(3) + 2; // 2-4 room types per property
//         List<RoomType> roomTypes = new ArrayList<>();
        
//         for (int i = 0; i < numRoomTypes; i++) {
//             String roomTypeName;
//             if (type == 1) {
//                 roomTypeName = roomTypeHotel[random.nextInt(roomTypeHotel.length)];
//             } else if (type == 2) {
//                 roomTypeName = roomTypeVilla[random.nextInt(roomTypeVilla.length)];
//             } else if (type == 3) {
//                 roomTypeName = roomTypeApartment[random.nextInt(roomTypeApartment.length)];
//             } else {
//                 roomTypeName = "";
//             }
            

//             int floor = random.nextInt(5) + 1; // 1-5 floors
//             String roomTypeId = String.format("%03d-%s-%d", propertyNumber, roomTypeName, floor);
            
//             RoomType roomType = RoomType.builder()
//                 .roomTypeID(roomTypeId)
//                 .name(roomTypeName)
//                 .price(random.nextInt(500000) + 200000) // 200K-700K
//                 .description(generateRoomTypeDescription(roomTypeName))
//                 .capacity(random.nextInt(4) + 1) // 1-4 people
//                 .facility(facilities[random.nextInt(facilities.length)])
//                 .floor(floor)
//                 .listRoom(new ArrayList<>())
//                 .build();
            
//             // Save room type first without rooms
//             roomType = roomTypeRepository.save(roomType);
            
//             // Create rooms for this room type after saving
//             createRoomsForRoomType(roomType, property, floor);
            
//             roomTypes.add(roomType);
//         }
        
//         // Update property with room types
//         property.setListRoomType(roomTypes);
//         propertyRepository.save(property);

//     }
    
//     private void createRoomsForRoomType(RoomType roomType, Property property, int floor) {
//         int numRooms = random.nextInt(5) + 2;
//         List<Room> rooms = new ArrayList<>();

//         for (int i = 1; i <= numRooms; i++) {
//             String roomId = String.format("%s-%d%02d", property.getPropertyID(), floor, i);
//             Room room = Room.builder()
//                 .roomID(roomId)
//                 .name(String.format("Room %d", i))
//                 .activeRoom(1)
//                 .listAccommodationBooking(new ArrayList<>())
//                 .build();

//             room = roomRepository.save(room);
//             rooms.add(room);
//             createBookingsForRoom(room);

    
//             List<AccommodationBooking> bookings = accommodationBookingRepository.findAllByRoom(room);

//             for (AccommodationBooking booking : bookings) {
//                 if (booking.getStatus() == 4) {
//                     property.setIncome(property.getIncome() + booking.getTotalPrice());
//                     if (booking.isBreakfast()) {
//                         property.setIncome(property.getIncome() + 50_000 * booking.getTotalDays());
//                     }

//                     if (booking.getExtraPay() > 0) {
//                         property.setIncome(property.getIncome() + booking.getExtraPay());
//                     }

//                     if (booking.getRefund() > 0) {
//                         property.setIncome(property.getIncome() - booking.getRefund());
//                     }
//                 }
//             }

//             property.setTotalRoom(property.getTotalRoom() + 1);
//         }

//         roomType.setListRoom(rooms);
//         roomTypeRepository.save(roomType);
// }

    
//     private void createBookingsForRoom(Room room) {
//         String[] customerNames = {
//             "Alice Cooper", "Bob Dylan", "Charlie Brown", "Diana Prince", "Eve Adams",
//             "Frank Miller", "Grace Kelly", "Henry Ford", "Ivy League", "Jack Sparrow"
//         };
        
//         String[] customerEmails = {
//             "alice@email.com", "bob@email.com", "charlie@email.com", "diana@email.com", "eve@email.com",
//             "frank@email.com", "grace@email.com", "henry@email.com", "ivy@email.com", "jack@email.com"
//         };
        
//         String[] customerPhones = {
//             "+6281234567890", "+6281234567891", "+6281234567892", "+6281234567893", "+6281234567894",
//             "+6281234567895", "+6281234567896", "+6281234567897", "+6281234567898", "+6281234567899"
//         };
        
//         int numBookings = random.nextInt(3); // 0-2 bookings per room
        
//         for (int i = 0; i < numBookings; i++) {
//             // Get last 7 characters of room ID
//             String roomId = room.getRoomID();
//             String lastSevenChars = roomId.length() >= 7 
//                 ? roomId.substring(roomId.length() - 7) 
//                 : roomId;
            
//             // Get current timestamp when booking is created
//             LocalDateTime bookingCreatedTime = LocalDateTime.now();
//             DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");
//             String timestamp = bookingCreatedTime.format(formatter);
            
//             // Format: BOOK-{last7chars}-{yyyy-MM-dd-HH:mm:ss}
//             String bookingId = String.format("BOOK-%s-%s", lastSevenChars, timestamp);
            
//             LocalDateTime checkIn = LocalDateTime.now().plusDays(random.nextInt(30));
//             LocalDateTime checkOut = checkIn.plusDays(random.nextInt(7) + 1);
//             int totalDays = (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
            
//             AccommodationBooking booking = AccommodationBooking.builder()
//                 .bookingID(bookingId)
//                 .checkInDate(checkIn)
//                 .checkOutDate(checkOut)
//                 .totalDays(totalDays)
//                 .totalPrice(random.nextInt(2000000) + 500000) // 500K-2.5M
//                 .status(random.nextInt(5)) // 0-4
//                 .customerID(UUID.randomUUID())
//                 .customerName(customerNames[random.nextInt(customerNames.length)])
//                 .customerEmail(customerEmails[random.nextInt(customerEmails.length)])
//                 .customerPhone(customerPhones[random.nextInt(customerPhones.length)])
//                 .isBreakfast(random.nextBoolean())
//                 .refund(random.nextInt(500000))
//                 .extraPay(random.nextInt(200000))
//                 .capacity(random.nextInt(4) + 1) // 1-4 people
//                 .room(room)
//                 .build();
            
//             accommodationBookingRepository.save(booking);
//         }
//     }
    
//     private String getTypePrefix(int type) {
//         return switch (type) {
//             case 1 -> "HOT";
//             case 2 -> "VIL";
//             case 3 -> "APT";
//             default -> "HOT";
//         }; 
//     }
    
//     private String generatePropertyName(int type, int number) {
//         String[] hotelNames = {"Grand Hotel", "Plaza Hotel", "Royal Hotel", "Paradise Hotel", "Luxury Hotel"};
//         String[] villaNames = {"Villa Paradise", "Villa Sunset", "Villa Ocean", "Villa Garden", "Villa Mountain"};
//         String[] apartmentNames = {"Sky Apartment", "City Apartment", "Modern Apartment", "Luxury Apartment", "Garden Apartment"};
        
//         String[] names = type == 1 ? hotelNames : (type == 2 ? villaNames : apartmentNames);
//         return names[number % names.length] + " " + (number < 10 ? "0" + number : number);
//     }
    
//     private String generateAddress(String province) {
//         String[] streets = {"Jl. Sudirman", "Jl. Thamrin", "Jl. Gatot Subroto", "Jl. HR Rasuna Said", "Jl. Kuningan"};
//         return streets[random.nextInt(streets.length)] + " No. " + (random.nextInt(100) + 1) + ", " + province;
//     }
    
//     private String generatePropertyDescription(int type) {
//         String[] hotelDescs = {
//             "Luxurious hotel with modern amenities and excellent service",
//             "Boutique hotel offering personalized experiences",
//             "Business hotel with conference facilities and meeting rooms"
//         };
//         String[] villaDescs = {
//             "Private villa with stunning ocean views and private pool",
//             "Tropical villa surrounded by lush gardens",
//             "Modern villa with contemporary design and luxury amenities"
//         };
//         String[] apartmentDescs = {
//             "Modern apartment with city views and modern facilities",
//             "Luxury apartment with premium amenities and services",
//             "Contemporary apartment in prime location"
//         };
        
//         String[] descs = type == 1 ? hotelDescs : (type == 2 ? villaDescs : apartmentDescs);
//         return descs[random.nextInt(descs.length)];
//     }
    
//     private String generateRoomTypeDescription(String roomTypeName) {
//         return "Comfortable " + roomTypeName.toLowerCase() + " with modern amenities and excellent service";
//     }
    
// }
