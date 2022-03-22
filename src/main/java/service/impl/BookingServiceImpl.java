package service.impl;

import dao.BookingDao;
import dao.GuestDao;
import dao.RoomDao;
import dao.impl.BookingDaoImpl;
import dao.impl.GuestDaoImpl;
import dao.impl.RoomDaoImpl;
import domain.Booking;
import domain.Guest;
import domain.Room;
import service.BookingService;

import java.text.ParseException;
import java.util.*;

public class BookingServiceImpl implements BookingService {

    public BookingDao bookingDao=new BookingDaoImpl();
    private GuestDao guestDao=new GuestDaoImpl();
    public RoomDao roomDao=new RoomDaoImpl();


    @Override
    public boolean addBooking(Booking booking, Guest guest) {
        try {
          if(guestDao.insertGuest(guest)==false)  return false;
          if(bookingDao.addBooking(booking)==false)  return false;
          List<String> roomIds= Arrays.asList(booking.getRoomIds().split(","));
           if(roomDao.updateRoom(roomIds)==false) return false;
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public List<Booking> findBookingByGuest(Guest guest) throws ParseException {
        return bookingDao.findBookingByGuest(guest);
    }

    @Override
    public List<Room> findRoomByDate(Date date) throws ParseException {
        List<String> allRoomIds=roomDao.findAllRoomIdsByStatus("0");
        List<String> dateRoomIds=bookingDao.findRoomByDate(date);
        List<String> roomIds=new ArrayList<>();
        List<Room> rooms=new ArrayList<>();
        for (String r:allRoomIds){
            if(!dateRoomIds.contains(r))
                roomIds.add(r);
        }

        for (String r:roomIds){
            Room room=roomDao.getRoomById(r);
            rooms.add(room);
        }

        return rooms;
    }

    @Override
    public String getNewId() {
        return bookingDao.getNewId();
    }
}
