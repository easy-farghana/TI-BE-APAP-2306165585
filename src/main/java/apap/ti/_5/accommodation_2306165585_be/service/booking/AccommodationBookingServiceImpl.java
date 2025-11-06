package apap.ti._5.accommodation_2306165585_be.service.booking;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306165585_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306165585_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AccommodationBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.room.RoomResponseDTO;

@Service
public class AccommodationBookingServiceImpl implements AccommodationBookingService {
    
    private final AccommodationBookingRepository accommodationBookingRepository;
    
    public AccommodationBookingServiceImpl(AccommodationBookingRepository accommodationBookingRepository) {
        this.accommodationBookingRepository = accommodationBookingRepository;
    }

    @Override
    public List<AccommodationBookingResponseDTO> getAllAccommodationBookings() {
        return accommodationBookingRepository.findAll()
            .stream()
            .map(this::mapToAccommodationBookingDTO)
            .collect(Collectors.toList());
    }

    private AccommodationBookingResponseDTO mapToAccommodationBookingDTO(AccommodationBooking accommodationBooking) {
        return AccommodationBookingResponseDTO.builder()
            .bookingID(accommodationBooking.getBookingID())
            .checkInDate(accommodationBooking.getCheckInDate())
            .checkOutDate(accommodationBooking.getCheckOutDate())
            .totalDays(accommodationBooking.getTotalDays())
            .totalPrice(accommodationBooking.getTotalPrice())
            .status(accommodationBooking.getStatus())
            .customerName(accommodationBooking.getCustomerName())
            .customerEmail(accommodationBooking.getCustomerEmail())
            .customerPhone(accommodationBooking.getCustomerPhone())
            .isBreakfast(accommodationBooking.isBreakfast())
            .refund(accommodationBooking.getRefund())
            .extraPay(accommodationBooking.getExtraPay())
            .capacity(accommodationBooking.getCapacity())
            .roomName(accommodationBooking.getRoom().getName())
            .build();
    }
}
