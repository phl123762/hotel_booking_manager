package service;

import domain.Booking;
import domain.Guest;
import domain.Room;
import org.junit.Test;
import service.impl.BookingServiceImpl;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BookingServiceTest {
    private BookingService  bookingService=new BookingServiceImpl();
    SimpleDateFormat simpleFormatter=new SimpleDateFormat("yyyy-MM-dd");


    @Test
    public void testaddBooking() throws ParseException {
        int roomNumber=2;

        Guest guest=new Guest();
        guest.setGuestId("1");
        guest.setGuestName("shasha");


        List<Room>rooms=bookingService.findRoomByDate(simpleFormatter.parse("2022-03-23"));
        List<String> roomIds=new ArrayList<>();
        for(int i=0;i<roomNumber;i++){
            roomIds.add(rooms.get(i).getRomeId());
        }

        Booking booking=new Booking();
        booking.setBookingId(4+"");
        booking.setBookingDate(simpleFormatter.parse("2022-03-23"));
        booking.setGuestId(1+"");
        booking.setRoomNumber(roomNumber);
        booking.setRoomIds(roomIds);

        System.out.println(bookingService.addBooking(booking, guest));
    }

    @Test
    public void  testfindRoomByDate() throws ParseException {
        List<Room>rooms=bookingService.findRoomByDate(simpleFormatter.parse("2022-03-23"));
        for (Room room:rooms)
            System.out.println(room);
    }


}
