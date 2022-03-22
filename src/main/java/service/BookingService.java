package service;

import domain.Booking;
import domain.Guest;
import domain.Room;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface BookingService {

    boolean addBooking(Booking booking,Guest guest);

    List<Booking> findBookingByGuest(Guest guest) throws ParseException;

    List<Room> findRoomByDate (Date date) throws ParseException;

    String getNewId();


}
