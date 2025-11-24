package apap.ti._5.accommodation_2306165585_be.service.booking;

import java.util.List;
import java.util.UUID;

import apap.ti._5.accommodation_2306165585_be.restdto.request.booking.AddBookingRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AccommodationBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AllBookingResponseDTO;


public interface AccommodationBookingService {
   List<AllBookingResponseDTO> getAllAccommodationBookings();
   AccommodationBookingResponseDTO createBooking(AddBookingRequestDTO request); 
   void updateBookingStatusesForCheckIn();
   AccommodationBookingResponseDTO cancelBooking(UUID bookingID);
   AccommodationBookingResponseDTO payBooking(UUID bookingID);
   AccommodationBookingResponseDTO giveRefund(UUID bookingID);
   AccommodationBookingResponseDTO getAccommodationBookingById(UUID id);
   AccommodationBookingResponseDTO updateBooking(UUID bookingID, AddBookingRequestDTO request);
}
