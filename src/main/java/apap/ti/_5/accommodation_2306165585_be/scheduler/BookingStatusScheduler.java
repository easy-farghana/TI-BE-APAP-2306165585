package apap.ti._5.accommodation_2306165585_be.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import apap.ti._5.accommodation_2306165585_be.service.booking.AccommodationBookingService;

@Component
@RequiredArgsConstructor
public class BookingStatusScheduler {

    private final AccommodationBookingService bookingService;

    /**
     * Run every day at 14:00 (2 PM)
     * Cron format: second minute hour day month day-of-week
     */
    @Scheduled(cron = "0 0 14 * * *") // 14:00 every day
    public void updateBookingsAfterCheckIn() {
        bookingService.updateBookingStatusesForCheckIn();
    }
}
