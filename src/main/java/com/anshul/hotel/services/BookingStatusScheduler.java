package com.anshul.hotel.services;

import com.anshul.hotel.model.Booking;
import com.anshul.hotel.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class BookingStatusScheduler {

    @Autowired
    private BookingRepository bookingRepository;
    @Scheduled(cron = "0 0 */2 * * *")
    public void completeCheckedInBookings() {
        System.out.println("Running Booking Status Cron: " + new Date());
        List<Booking> bookings = bookingRepository.findByStatus("CHECKED_IN");
        for (Booking booking : bookings) {
            Date checkOutDate = booking.getCheckOut();
            String checkoutTimeStr = booking.getCheckOutTime();
            if (checkoutTimeStr == null) checkoutTimeStr = "12:00 PM";
            try {
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                timeFormat.setTimeZone(TimeZone.getDefault());
                Date checkoutTime = timeFormat.parse(checkoutTimeStr);
                // 3️⃣ Merge checkout date + checkout time
                Calendar calDate = Calendar.getInstance();
                calDate.setTime(checkOutDate);
                Calendar calTime = Calendar.getInstance();
                calTime.setTime(checkoutTime);
                calDate.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
                calDate.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));
                calDate.set(Calendar.SECOND, 0);
                calDate.set(Calendar.MILLISECOND, 0);
                Date bookingCheckoutDateTime = calDate.getTime();
                Date now = new Date();
                if (!now.before(bookingCheckoutDateTime)) {
                    booking.setStatus("COMPLETED");
                    bookingRepository.save(booking);
                    System.out.println("Booking " + booking.getId() + " marked as COMPLETED");
                }


            } catch (ParseException e) {
                System.out.println("Error parsing checkout time for booking " + booking.getId() + ": " + e.getMessage());
            }
        }
    }
}
