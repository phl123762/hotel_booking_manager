package dao;

import dao.impl.BookingDaoImpl;
import domain.Booking;
import domain.Guest;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookingDaoTest {

    public BookingDao bookingDao=new BookingDaoImpl();
    SimpleDateFormat simpleFormatter=new SimpleDateFormat("yyyy-MM-dd");


    @Test
    public void testAdd(){
//        Booking booking=new Booking();
//        booking.setBookingId(1+"");
//        booking.setBookingDate(new Date());
//        booking.setGuestId(1+"");
//        booking.setRoomNumber(2);
//        booking.setRoomIds(Arrays.asList("1,2"));

        Booking booking2=new Booking();
        booking2.setBookingId(2+"");
        Calendar cal=Calendar.getInstance();//获取当前日期
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, +2);
        booking2.setBookingDate(cal.getTime());
        booking2.setGuestId(2+"");
        booking2.setRoomNumber(2);
        booking2.setRoomIds(Arrays.asList("4,5"));

        Booking booking3=new Booking();
        booking3.setBookingId(3+"");
//        Calendar cal=Calendar.getInstance();//获取当前日期
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, +2);
        booking3.setBookingDate(cal.getTime());
        booking3.setGuestId(2+"");
        booking3.setRoomNumber(2);
        booking3.setRoomIds(Arrays.asList("8,9"));

//        System.out.println(bookingDao.addBooking(booking));
        System.out.println(bookingDao.addBooking(booking2));
        System.out.println(bookingDao.addBooking(booking3));


    }

    @Test
    public void testfindBookingByBid() throws ParseException {
        System.out.println(bookingDao.findBookingByBid(1 + ""));
    }
    @Test
    public void testfindAllBooking() throws ParseException {
        List<Booking> bookings=bookingDao.findAllBooking();
        for (Booking booking:bookings)
            System.out.println(booking);
    }

    @Test
    public void testfindRoomByDate() throws ParseException {
        List<String> rooms=bookingDao.findRoomByDate(simpleFormatter.parse("2022-03-23"));
        for (String room:rooms)
            System.out.println(room);
    }

    @Test
    public void  testfindBookingByGuest() throws ParseException {
        Guest guest=new Guest();
        guest.setGuestId(2+"");
        guest.setGuestName("phl");
        List<Booking> bookings=bookingDao.findBookingByGuest(guest);
        for (Booking booking:bookings)
            System.out.println(booking);
    }


    @Test
    public void testFindNewId(){
        System.out.println(bookingDao.getNewId());
    }





}
