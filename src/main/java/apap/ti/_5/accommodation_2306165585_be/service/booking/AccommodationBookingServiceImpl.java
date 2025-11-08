package apap.ti._5.accommodation_2306165585_be.service.booking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306165585_be.model.Property;
import apap.ti._5.accommodation_2306165585_be.model.Room;
import apap.ti._5.accommodation_2306165585_be.model.RoomType;
import apap.ti._5.accommodation_2306165585_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306165585_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306165585_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306165585_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.request.booking.AddBookingRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AccommodationBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccommodationBookingServiceImpl implements AccommodationBookingService {
    
    private final AccommodationBookingRepository bookingRepository;
    private final RoomService roomService;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final PropertyRepository propertyRepository;


    
    public AccommodationBookingServiceImpl(
        AccommodationBookingRepository accommodationBookingRepository,
        RoomService roomService,
        RoomRepository roomRepository,
        RoomTypeRepository roomTypeRepository,
        PropertyRepository propertyRepository) {

        this.bookingRepository = accommodationBookingRepository;
        this.roomService = roomService;
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public List<AccommodationBookingResponseDTO> getAllAccommodationBookings() {
        return bookingRepository.findAll()
            .stream()
            .map(this::mapToAccommodationBookingDTO)
            .collect(Collectors.toList());
    }

    @Override 
    public AccommodationBookingResponseDTO getAccommodationBookingById(String id) {
        AccommodationBooking booking = bookingRepository.findById(id).orElseThrow(
            () -> new NotFoundException("Booking not found with ID: " + id)
        );
        return mapToAccommodationBookingDTO(booking);
    }

    @Override
    public AccommodationBookingResponseDTO createBooking(AddBookingRequestDTO request) {
        String roomId = request.getRoomID();
        Room room = roomRepository.findById(roomId).orElseThrow(
            () -> new NotFoundException("Room not found with ID: " + roomId)
        );
        LocalDateTime checkInDate = request.getCheckInDate();
        LocalDateTime checkOutDate = request.getCheckOutDate();

        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId()).orElseThrow(
                    () -> new NotFoundException("Room type not found with ID: " + request.getRoomTypeId())
                );

        validateBookingRequest(request, room, roomType, false, null);
        
        String lastSevenChars = roomId.substring(roomId.length() - 7); 
        LocalDateTime bookingCreatedTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");
        String timestamp = bookingCreatedTime.format(formatter);
        String bookingID = String.format("BOOK-%s-%s", lastSevenChars, timestamp);
        
        int daysStaying = (int) ChronoUnit.DAYS.between(
            request.getCheckInDate().toLocalDate(),
            request.getCheckOutDate().toLocalDate()
        );
        int totalPrice = roomType.getPrice() * daysStaying;
        
        if (request.getIsBreakfast()) {
            totalPrice += 50_000 * daysStaying;
        }

        AccommodationBooking booking = AccommodationBooking.builder()
            .bookingID(bookingID)
            .checkInDate(checkInDate)
            .checkOutDate(checkOutDate)
            .totalDays(daysStaying)
            .isBreakfast(request.getIsBreakfast())
            .totalDays(daysStaying)
            .extraPay(0)
            .refund(0)
            .totalPrice(totalPrice)
            .capacity(request.getCapacity())
            .customerID(request.getCustomerID())
            .customerName(request.getCustomerName())
            .customerEmail(request.getCustomerEmail())
            .customerPhone(request.getCustomerPhone())
            .room(room)
            .status(0)
            .build();
        
        booking = bookingRepository.save(booking);
        return mapToAccommodationBookingDTO(booking);
    }

    @Override
    public AccommodationBookingResponseDTO cancelBooking(String bookingID) {
        AccommodationBooking booking = bookingRepository.findById(bookingID).orElseThrow(
            () -> new NotFoundException("Booking not found with ID: " + bookingID)
        );

        Room room = booking.getRoom();
        Property property = getBookingProperty(room);
        int income = property.getIncome();

        if (booking.getStatus() == 0 && booking.getExtraPay() != 0) {
            property.setIncome(income - booking.getTotalPrice());
        } else if (booking.getStatus() == 1) {
            property.setIncome(income - booking.getTotalPrice());
        } 
        propertyRepository.save(property);
        booking.setStatus(2);

        return mapToAccommodationBookingDTO(bookingRepository.save(booking));
    }

    @Override
    public AccommodationBookingResponseDTO updateBooking(String bookingID, AddBookingRequestDTO request) {
        AccommodationBooking booking = bookingRepository.findById(bookingID).orElseThrow(
            () -> new NotFoundException("Booking not found with ID: " + bookingID)
        );

        Room newRoom = roomRepository.findById(request.getRoomID()).orElseThrow(
            () -> new NotFoundException("Room not found with ID: " + request.getRoomID())
        );

        RoomType newRoomType = roomTypeRepository.findById(request.getRoomTypeId()).orElseThrow(
            () -> new NotFoundException("Room type not found with ID: " + request.getRoomTypeId())
        );

        validateBookingRequest(request, newRoom, newRoomType, true, booking.getRoom().getRoomID());

        int daysStaying = (int) ChronoUnit.DAYS.between(
            request.getCheckInDate().toLocalDate(),
            request.getCheckOutDate().toLocalDate()
        );

        int newTotalPrice = calculateTotalPrice(newRoomType, request.getIsBreakfast(), daysStaying);
        int oldTotalPrice = booking.getTotalPrice();

        // Handle extra pay or refund logic
        if (booking.getStatus() == 1) {
            if (newTotalPrice > oldTotalPrice) {
                booking.setExtraPay(newTotalPrice - oldTotalPrice);
                booking.setRefund(0);
                booking.setStatus(0);
            } else if (newTotalPrice < oldTotalPrice) {
                booking.setRefund(oldTotalPrice - newTotalPrice);
                booking.setExtraPay(0);
                booking.setStatus(3);
            }
        } else {
            booking.setExtraPay(0);
            booking.setRefund(0);
        }

        // Update booking ID if room changed
        if (!booking.getRoom().getRoomID().equals(newRoom.getRoomID())) {
            String[] parts = booking.getBookingID().split("-", 3);
            if (parts.length == 3) {
                String newLastSevenChars = newRoom.getRoomID()
                    .substring(newRoom.getRoomID().length() - 7);
                String newBookingID = String.format("BOOK-%s-%s", newLastSevenChars, parts[2]);
                booking.setBookingID(newBookingID);
            }
        }

        // Update booking details
        booking.setRoom(newRoom);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setTotalDays(daysStaying);
        booking.setTotalPrice(oldTotalPrice);
        booking.setCapacity(request.getCapacity());
        booking.setBreakfast(request.getIsBreakfast());
        booking.setCustomerID(request.getCustomerID());
        booking.setCustomerName(request.getCustomerName());
        booking.setCustomerEmail(request.getCustomerEmail());
        booking.setCustomerPhone(request.getCustomerPhone());
        booking.setUpdatedDate(LocalDateTime.now());

        bookingRepository.save(booking);
        return mapToAccommodationBookingDTO(booking);
    }



    @Override
    public void updateBookingStatusesForCheckIn() {
        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1).minusSeconds(1);

        // find all paid bookings whose checkInDate is today
        List<AccommodationBooking> todaysBookings = bookingRepository
                .findByCheckInDateBetween(startOfToday, endOfToday);

        for (AccommodationBooking booking : todaysBookings) {
            Room room = booking.getRoom();
            Property property = getBookingProperty(room);

            int income = property.getIncome();
            switch (booking.getStatus()) {
                case 0 -> {
                    if (booking.getExtraPay() > 0) {
                        property.setIncome(income - booking.getTotalPrice());
                    }
                }
                case 1 -> booking.setStatus(4);
                case 3 -> {
                    property.setIncome(income - booking.getRefund());
                    booking.setStatus(4);
                }
                default -> {
                }
            }
            propertyRepository.save(property);
        }

        bookingRepository.saveAll(todaysBookings);
        log.info("Updated " + todaysBookings.size() + " bookings.");
    }

    @Override
    public AccommodationBookingResponseDTO payBooking(String bookingID) {
        AccommodationBooking booking = bookingRepository.findById(bookingID).orElseThrow(
            () -> new NotFoundException("Booking not found with ID: " + bookingID)
        );

        Room room = booking.getRoom();
        Property property = getBookingProperty(room);
        int income = property.getIncome();

        switch (booking.getStatus()) {
            case 1 -> throw new ConstraintViolationException("Booking is already Paid", null);
            case 3 -> throw new ConstraintViolationException("Booking is already Cancelled", null);
            case 4 -> throw new ConstraintViolationException("Booking is already Done", null);
            default -> {
            }
        }
        
        // Asumsi: Ada Extra pay artinya totalPrice sudah terhitung 
        if (booking.getExtraPay() > 0) {
            property.setIncome(income + booking.getExtraPay());

            // Asumsi: Setelah extra pay terbayar, masukkan ke total price
            booking.setTotalPrice(booking.getTotalPrice() + booking.getExtraPay());
            booking.setExtraPay(0);
        } else {
            property.setIncome(income + booking.getTotalPrice());
        }

        
        property.setIncome(income + booking.getTotalPrice());
        propertyRepository.save(property);
        booking.setStatus(1);

        AccommodationBooking savedBooking = bookingRepository.save(booking);
        return mapToAccommodationBookingDTO(savedBooking);
    }

    @Override
    public AccommodationBookingResponseDTO giveRefund(String bookingID) {
        AccommodationBooking booking = bookingRepository.findById(bookingID).orElseThrow(
            () -> new NotFoundException("Booking not found with ID: " + bookingID)
        );
        
        Room room = booking.getRoom();
        Property property = getBookingProperty(room);
        int income = property.getIncome();
        property.setIncome(income - booking.getRefund());
        propertyRepository.save(property);

        // Asumsi: Memberikan refund akan mengembalikan status menjadi 1
        booking.setStatus(1);

        AccommodationBooking savedBooking = bookingRepository.save(booking);
        return mapToAccommodationBookingDTO(savedBooking);
    }

    private Property getBookingProperty(Room room) {
        String roomId = room.getRoomID();
        String propertyId = roomId.substring(0, 12);
        Property property = propertyRepository.findById(propertyId).orElseThrow(
            () -> new NotFoundException("Property not found with ID: " + propertyId)
        );
        return property;
    }
    private void validateBookingRequest(
            AddBookingRequestDTO request,
            Room room,
            RoomType roomType,
            boolean isUpdate,
            String oldRoomId
    ) {
        LocalDateTime checkIn = request.getCheckInDate();
        LocalDateTime checkOut = request.getCheckOutDate();
        
        if (!roomType.getListRoom().contains(room)) {
            throw new ConstraintViolationException("Room not found in room type", null);
        }
        if (checkIn.isBefore(LocalDateTime.now()) && !isUpdate) {
            throw new ConstraintViolationException("Check-in date must be at minimum today before 14:00", null);
        }

        if (checkIn.isAfter(checkOut)) {
            throw new ConstraintViolationException("Check-in date must be before check-out date", null);
        }

        // Only check availability if it's a new room or dates changed
        if (!isUpdate || !room.getRoomID().equals(oldRoomId)) {
            if (!roomService.isRoomAvailable(room, checkIn, checkOut)) {
                throw new ConstraintViolationException("Room is not available", null);
            }
        }

        if (request.getCapacity() > roomType.getCapacity()) {
            throw new ConstraintViolationException("Capacity must be ≤ room type capacity", null);
        }
    }

    private int calculateTotalPrice(RoomType roomType, boolean isBreakfast, int days) {
        int totalPrice = roomType.getPrice() * days;
        if (isBreakfast) totalPrice += 50_000 * days;
        return totalPrice;
    }

    private AccommodationBookingResponseDTO mapToAccommodationBookingDTO(AccommodationBooking accommodationBooking) {
        return AccommodationBookingResponseDTO.builder()
            .bookingID(accommodationBooking.getBookingID())
            .checkInDate(accommodationBooking.getCheckInDate())
            .checkOutDate(accommodationBooking.getCheckOutDate())
            .totalDays(accommodationBooking.getTotalDays())
            .totalPrice(accommodationBooking.getTotalPrice())
            .status(accommodationBooking.getStatus())
            .customerID(accommodationBooking.getCustomerID())
            .customerName(accommodationBooking.getCustomerName())
            .customerEmail(accommodationBooking.getCustomerEmail())
            .customerPhone(accommodationBooking.getCustomerPhone())
            .isBreakfast(accommodationBooking.isBreakfast())
            .refund(accommodationBooking.getRefund())
            .extraPay(accommodationBooking.getExtraPay())
            .capacity(accommodationBooking.getCapacity())
            .roomName(accommodationBooking.getRoom().getName())
            .createdDate(accommodationBooking.getCreatedDate())
            .updatedDate(accommodationBooking.getUpdatedDate())
            .build();
    }
}
