package apap.ti._5.accommodation_2306165585_be.service.booking;

import java.util.List;

import apap.ti._5.accommodation_2306165585_be.restdto.request.booking.AddBookingRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AccommodationBookingResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.booking.AllBookingResponseDTO;


public interface AccommodationBookingService {
   List<AllBookingResponseDTO> getAllAccommodationBookings();
   AccommodationBookingResponseDTO createBooking(AddBookingRequestDTO request); 
   void updateBookingStatusesForCheckIn();
   AccommodationBookingResponseDTO cancelBooking(String bookingID);
   AccommodationBookingResponseDTO payBooking(String bookingID);
   AccommodationBookingResponseDTO giveRefund(String bookingID);
   AccommodationBookingResponseDTO getAccommodationBookingById(String id);
   AccommodationBookingResponseDTO updateBooking(String bookingID, AddBookingRequestDTO request);
}
