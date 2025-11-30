package apap.ti._5.accommodation_2306165585_be.service.booking;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

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
import apap.ti._5.accommodation_2306165585_be.restdto.request.bill.BillRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.booking.BookingRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AccommodationBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AllBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.security.RoleGroup;
import apap.ti._5.accommodation_2306165585_be.security.UserContext;
import apap.ti._5.accommodation_2306165585_be.service.external.ExternalApiService;
import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;

import apap.ti._5.accommodation_2306165585_be.exception.SecurityException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccommodationBookingServiceImpl implements AccommodationBookingService {
    
    private final AccommodationBookingRepository bookingRepository;
    private final RoomService roomService;
    private final RoomRepository roomRepository;
    private final PropertyRepository propertyRepository;
    private final UserContext userContext ;
    private final ExternalApiService externalApiService;

    public AccommodationBookingServiceImpl(
        AccommodationBookingRepository accommodationBookingRepository,
        RoomService roomService,
        RoomRepository roomRepository,
        RoomTypeRepository roomTypeRepository,
        PropertyRepository propertyRepository,
        UserContext userContext,
        ExternalApiService externalApiService
    ) {

        this.bookingRepository = accommodationBookingRepository;
        this.roomService = roomService;
        this.roomRepository = roomRepository;
        this.propertyRepository = propertyRepository;
        this.userContext = userContext;
        this.externalApiService = externalApiService;
    }


    /**
     * Returns a list of all accommodation bookings.
     * 
     * If the user is a SUPERADMIN, all bookings are returned.
     * If the user is an ACCOMMODATION_OWNER, all bookings associated with the user's property are returned.
     * If the user is a CUSTOMER, all bookings associated with the user are returned.
     * If the user is not authorized to access this endpoint, a SecurityException is thrown.
     * 
     * @return A list of all accommodation bookings.
     */
    @Override
    public List<AllBookingResponseDTO> getAllAccommodationBookings() {
        String role = userContext.getRole();
        List<AccommodationBooking> bookings;
        switch (role) {
            case RoleGroup.SUPERADMIN -> bookings = bookingRepository.findAll();
            case RoleGroup.ACCOMMODATION_OWNER -> bookings = bookingRepository.findAllByOwnerID(userContext.getUserID());
            case RoleGroup.CUSTOMER -> bookings = bookingRepository.findAllByCustomerID(userContext.getUserID());
            default -> throw new SecurityException("You are not authorized to access this endpoint");
        }

        return bookings.stream()
                .map(this::mapToAllBookingDTO)
                .toList();
    }


    /**
     * Returns the accommodation booking with the given ID.
     * 
     * If the user is a SUPERADMIN, the booking is returned.
     * If the user is an ACCOMMODATION_OWNER, the booking associated with the user's property is returned.
     * If the user is a CUSTOMER, the booking associated with the user is returned.
     * If the user is not authorized to access this booking, a SecurityException is thrown.
     * 
     * @param id The ID of the accommodation booking to fetch.
     * @return The accommodation booking with the given ID.
     */
    @Override 
    public AccommodationBookingResponseDTO getAccommodationBookingById(UUID id) {
        AccommodationBooking booking = bookingRepository.findById(id).orElseThrow(
            () -> new NotFoundException("Booking not found with ID: " + id)
        );

        String role = userContext.getRole();

        if (role.equals(RoleGroup.SUPERADMIN)) {
            return mapToAccommodationBookingDTO(booking);
        } 

        Room room = booking.getRoom();
        RoomType roomType = room.getRoomType();
        Property property = roomType.getProperty();
        UUID userID = userContext.getUserID();

        if (role.equals(RoleGroup.ACCOMMODATION_OWNER) && property.getOwnerID().equals(userID)) {
            return mapToAccommodationBookingDTO(booking);
        }

        if (role.equals(RoleGroup.CUSTOMER) && booking.getCustomerID().equals(userID)) {
            return mapToAccommodationBookingDTO(booking);
        }

        throw new SecurityException("You are not authorized to access this booking");
    }

    /**
     * Creates a new accommodation booking.
     * 
     * @param request the accommodation booking request
     * @return the created accommodation booking
     * @throws NotFoundException if the room with the given ID does not exist
     * @throws ConstraintViolationException if the request does not satisfy the constraints
     */
    @Transactional
    @Override
    public AccommodationBookingResponseDTO createBooking(BookingRequestDTO request) {
        Room room = roomRepository.findById(request.getRoomID()).orElseThrow(
            () -> new NotFoundException("Room not found with ID: " + request.getRoomID())
        );

        LocalDateTime checkInDate = request.getCheckInDate();
        LocalDateTime checkOutDate = request.getCheckOutDate();

        RoomType roomType = room.getRoomType();

        validateBookingRequest(request, room, roomType, false, null);
        
        int daysStaying = (int) ChronoUnit.DAYS.between(
            request.getCheckInDate().toLocalDate(),
            request.getCheckOutDate().toLocalDate()
        );

        UUID userID = userContext.getUserID();
        
        int totalPrice =  calculateTotalPrice(roomType, request.getIsBreakfast(), daysStaying);

        AccommodationBooking booking = AccommodationBooking.builder()
            .checkInDate(checkInDate)
            .checkOutDate(checkOutDate)
            .totalDays(daysStaying)
            .isBreakfast(request.getIsBreakfast())
            .totalDays(daysStaying)
            .extraPay(0)
            .refund(0)
            .totalPrice(totalPrice)
            .capacity(request.getCapacity())
            .customerID(userID)
            .customerName(request.getCustomerName())
            .customerEmail(request.getCustomerEmail())
            .customerPhone(request.getCustomerPhone())
            .room(room)
            .status(0)
            .build();
        
        booking = bookingRepository.save(booking);
        createBill(booking);
        return mapToAccommodationBookingDTO(booking);
    }
    
    /**
     * Create a bill for the given booking
     * @param booking the booking for which the bill should be created
     */
    private void createBill(AccommodationBooking booking) {
        BillRequestDTO request = new BillRequestDTO();
        request.setCustomerID(booking.getCustomerID());
        request.setServiceName("Accommodation");
        request.setServiceReferenceID(booking.getBookingID().toString());
        request.setAmount((long) booking.getTotalPrice());
        request.setDescription("Bill for booking on check in " + 
            booking.getCheckInDate() + 
            " and check out " + 
            booking.getCheckOutDate()
        );
        log.info("Request: ");
        log.info(request.toString());
        externalApiService.createBill(request);
    }

    /**
     * Cancel the booking with the given ID.
     * <p>
     * Only bookings that are not yet paid can be cancelled.
     * <p>
     * If the booking is not found, a NotFoundException is thrown.
     * If the booking is paid, an IllegalArgumentException is thrown.
     * If the booking is not your own, an IllegalArgumentException is thrown.
     * <p>
     * The booking is updated to the cancelled status, and the updated booking is returned.
     * @param bookingID the ID of the booking to cancel
     * @return the updated booking
     * @throws NotFoundException if the booking is not found
     * @throws IllegalArgumentException if the booking is paid or not your own
     */
    @Override
    public AccommodationBookingResponseDTO cancelBooking(UUID bookingID) {
        AccommodationBooking booking = bookingRepository.findById(bookingID).orElseThrow(
            () -> new NotFoundException("Booking not found with ID: " + bookingID)
        );
        
        UUID userID = userContext.getUserID();
        if (!booking.getCustomerID().equals(userID)) {
            throw new IllegalArgumentException("You cannot cancel booking that's not your own", null);
        }

        if (booking.getStatus() == 1 ) {
            throw new IllegalArgumentException("Booking is already paid", null);
        } 

        booking.setStatus(2);
        return mapToAccommodationBookingDTO(bookingRepository.save(booking));
    }

    /**
     * Updates the booking with the given ID.
     * <p>
     * Only bookings that are your own can be updated.
     * <p>
     * If the booking is not found, a NotFoundException is thrown.
     * If the booking is not your own, an IllegalArgumentException is thrown.
     * If the request is invalid, an IllegalArgumentException is thrown.
     * <p>
     * The booking is updated with the new details, and the updated booking is returned.
     * @param bookingID the ID of the booking to update
     * @param request the new booking details
     * @return the updated booking
     * @throws NotFoundException if the booking is not found
     * @throws IllegalArgumentException if the booking is not your own, or the request is invalid
     */
    @Override
    public AccommodationBookingResponseDTO updateBooking(UUID bookingID, BookingRequestDTO request) {
        AccommodationBooking booking = bookingRepository.findById(bookingID).orElseThrow(
            () -> new NotFoundException("Booking not found with ID: " + bookingID)
        );

        Room room = roomRepository.findById(request.getRoomID()).orElseThrow(
            () -> new NotFoundException("Room not found with ID: " + request.getRoomID())
        );
        RoomType roomType =  room.getRoomType();
        boolean isValidUpdate = isValidUpdateRequest(request, booking, room, roomType);
      
        if (!isValidUpdate) {
            throw new IllegalArgumentException("Invalid update request", null);
        }

        UUID userID = userContext.getUserID();

        if (booking.getCustomerID() != null && !booking.getCustomerID().equals(userID)) {
            throw new IllegalArgumentException("You cannot update booking that's not your own", null);
        }

        validateBookingRequest(request, room, roomType, true, booking.getRoom().getRoomID());

        int daysStaying = (int) ChronoUnit.DAYS.between(
            request.getCheckInDate().toLocalDate(),
            request.getCheckOutDate().toLocalDate()
        );

        int newTotalPrice = calculateTotalPrice(roomType, request.getIsBreakfast(), daysStaying);
    
        // Update booking details
        booking.setRoom(room);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setTotalDays(daysStaying);
        booking.setTotalPrice(newTotalPrice);
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



    /**
     * Automatically update the status of all paid bookings whose checkInDate is today.
     * <p>
     * If the booking status is 0 (pending), it is updated to 0 (pending).
     * If the booking status is 1 (paid), it is updated to 4 (checked in).
     * <p>
     * The booking status is updated to the new status, and the updated booking is returned.
     * Function is scheduled to run at 14:00 every day
     * @see apap.ti._5.accommodation_2306165585_be.scheduler.BookingStatusScheduler
     */
    @Override
    public void updateBookingStatusesForCheckIn() {
        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1).minusSeconds(1);

        // find all paid bookings whose checkInDate is today
        List<AccommodationBooking> todaysBookings = bookingRepository
                .findByCheckInDateBetween(startOfToday, endOfToday);

        for (AccommodationBooking booking : todaysBookings) {
            Room room = booking.getRoom();      
            RoomType roomType = room.getRoomType();
            Property property = roomType.getProperty();

            switch (booking.getStatus()) {
                case 0 -> booking.setStatus(0);
                case 1 -> booking.setStatus(4);
                default -> {}
            }
            propertyRepository.save(property);
        }

        bookingRepository.saveAll(todaysBookings);
        log.info("Updated " + todaysBookings.size() + " bookings.");
    }

    /**
     * Update the status of a booking.
     * <p>
     * If the booking status is 0 (pending), it is updated to 1 (paid).
     * If the booking status is 1 (paid), an exception is thrown.
     * If the booking status is 2 (cancelled), an exception is thrown.
     * <p>
     * The booking status is updated to the new status, and the updated booking is returned.
     * @param bookingID the ID of the booking to update
     * @return the updated booking
     * @throws NotFoundException if the booking is not found
     * @throws ConstraintViolationException if the booking is already paid or cancelled
     */
    @Override
    public AccommodationBookingResponseDTO updateBookingStatus(UUID bookingID) {
        AccommodationBooking booking = bookingRepository.findById(bookingID).orElseThrow(
            () -> new NotFoundException("Booking not found with ID: " + bookingID)
        );

        
        switch (booking.getStatus()) {
            case 1 -> throw new ConstraintViolationException("Booking is already Paid", null);
            case 2 -> throw new ConstraintViolationException("Booking is already Cancelled", null);
            default -> {}
        }

        Room room = booking.getRoom();
        RoomType roomType = room.getRoomType();
        Property property = roomType.getProperty();
        int income = property.getIncome();
        
        property.setIncome(income + booking.getTotalPrice());
        propertyRepository.save(property);
        booking.setStatus(1);

        AccommodationBooking savedBooking = bookingRepository.save(booking);
        return mapToAccommodationBookingDTO(savedBooking);
    }


    private boolean isValidUpdateRequest(
        BookingRequestDTO request, 
        AccommodationBooking booking, 
        Room room, 
        RoomType roomType
    ) {

        boolean isNotPaid = booking.getStatus() != 1;
        boolean isSameRoom = room.getRoomID().equals(booking.getRoom().getRoomID());
        boolean isSameRoomType = roomType.getRoomTypeID().equals(booking.getRoom().getRoomType().getRoomTypeID());
        boolean isSameProperty = roomType.getProperty().getPropertyID().equals(booking.getRoom().getRoomType().getProperty().getPropertyID());
        boolean isSameCustomer = true;
        
        if (request.getCustomerID() != null) {
            boolean isSameCustomerId = request.getCustomerID().equals(booking.getCustomerID());
            boolean isSameCustomerName = request.getCustomerName().equals(booking.getCustomerName());
            boolean isSameCustomerEmail = request.getCustomerEmail().equals(booking.getCustomerEmail());
            boolean isSameCustomerPhone = request.getCustomerPhone().equals(booking.getCustomerPhone());
            isSameCustomer = isSameCustomerId && isSameCustomerName && isSameCustomerEmail && isSameCustomerPhone;
        }

        boolean isSameOrder = isSameRoom && isSameRoomType && isSameProperty && isSameCustomer && isNotPaid;

        return isSameOrder;
    }
    private void validateBookingRequest(
            BookingRequestDTO request,
            Room room,
            RoomType roomType,
            boolean isUpdate,
            UUID oldRoomId
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

    private AllBookingResponseDTO mapToAllBookingDTO(AccommodationBooking booking) {
        return AllBookingResponseDTO.builder()
            .bookingID(booking.getBookingID())
            .roomName(booking.getRoom().getName())
            .checkInDate(booking.getCheckInDate())
            .checkOutDate(booking.getCheckOutDate())
            .totalPrice(booking.getTotalPrice())
            .status(booking.getStatus())
            .build();
    }

    private AccommodationBookingResponseDTO mapToAccommodationBookingDTO(AccommodationBooking accommodationBooking) {
        Room room = accommodationBooking.getRoom();
        RoomType roomType = room.getRoomType();
        Property property = roomType.getProperty();

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
            .capacity(accommodationBooking.getCapacity())
            .roomName(accommodationBooking.getRoom().getName())
            .roomID(room.getRoomID())
            .roomTypeID(roomType.getRoomTypeID())
            .createdDate(accommodationBooking.getCreatedDate())
            .updatedDate(accommodationBooking.getUpdatedDate())
            .propertyID(property.getPropertyID())
            .propertyName(property.getPropertyName())
            .build();
    }
}
