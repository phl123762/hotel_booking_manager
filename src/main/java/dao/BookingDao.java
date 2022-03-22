package dao;

import domain.Booking;
import domain.Guest;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface BookingDao {

    boolean addBooking(Booking booking);

    List<Booking> findBookingByGuest(Guest guest) throws ParseException;

    List<Booking> findAllBooking() throws ParseException;

    Booking findBookingByBid(String bid) throws ParseException;

    List<String> findRoomByDate (Date date) throws ParseException;

    String getNewId();

}
