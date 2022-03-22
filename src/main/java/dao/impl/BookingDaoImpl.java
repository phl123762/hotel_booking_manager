package dao.impl;

import dao.BookingDao;
import domain.Booking;
import domain.Guest;
import redis.clients.jedis.Transaction;
import utils.RedisHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BookingDaoImpl implements BookingDao {

    SimpleDateFormat simpleFormatter=new SimpleDateFormat("yyyy-MM-dd");


    @Override
    public boolean addBooking(Booking booking) {
        Transaction transaction=RedisHelper.multi();
        try{
            RedisHelper.multiSetObject(transaction,"booking:"+ booking.getBookingId(),"bookingId",booking.getBookingId());
            RedisHelper.multiSetObject(transaction,"booking:"+ booking.getBookingId(),"guestId",booking.getGuestId());
            RedisHelper.multiSetObject(transaction,"booking:"+ booking.getBookingId(),"roomIds",booking.getRoomIds());
            RedisHelper.multiSetObject(transaction,"booking:"+ booking.getBookingId(),"roomNumber",booking.getRoomNumber()+"");
            RedisHelper.multiSetObject(transaction,"booking:"+ booking.getBookingId(),"bookingDate",simpleFormatter.format(booking.getBookingDate()));
            RedisHelper.exec(transaction);
        }catch (Exception e){
            RedisHelper.discard(transaction);
            return false;
        }
        return true;
    }

    @Override
    public List<Booking> findBookingByGuest(Guest guest) throws ParseException {
        int i=1;
        List<Booking> bookings=new ArrayList<>();
        while (RedisHelper.getObject("booking:"+i,"bookingId")!=null){
            String bookingId=RedisHelper.getObject("booking:"+i,"bookingId");
            String gid=RedisHelper.getObject("booking:"+i,"guestId");
            String gname=RedisHelper.getObject("user:"+gid,"guestName");
            if(guest.getGuestId().equals(gid)&&guest.getGuestName().equals(gname)){
                bookings.add(findBookingByBid(bookingId));
            }
            i++;
        }
        return bookings;
    }


    @Override
    public List<Booking> findAllBooking() throws ParseException {
        int i=1;
        List<Booking> bookings=new ArrayList<>();
        while (RedisHelper.getObject("booking:"+i,"bookingId")!=null){
            String bookingId=RedisHelper.getObject("booking:"+i,"bookingId");
            bookings.add(findBookingByBid(bookingId));
            i++;
        }
        return bookings;
    }

    @Override
    public Booking findBookingByBid(String bid) throws ParseException {
        Booking booking=new Booking();
        String bookingId=RedisHelper.getObject("booking:"+bid,"bookingId");
        String guestId=RedisHelper.getObject("booking:"+bid,"guestId");
        String roomIds=RedisHelper.getObject("booking:"+bid,"roomIds");
        String roomNumber=RedisHelper.getObject("booking:"+bid,"roomNumber");
        String bookingDate=RedisHelper.getObject("booking:"+bid,"bookingDate");

        booking.setBookingDate(simpleFormatter.parse(bookingDate));
        booking.setRoomNumber(Integer.valueOf(roomNumber));
        booking.setRoomIds(Arrays.asList(roomIds.split(",")));
        booking.setGuestId(guestId);
        booking.setBookingId(bookingId);

        return booking;
    }

    @Override
    public List<String> findRoomByDate(Date date) throws ParseException {
        int i=1;
        List<String> rooms=new ArrayList<>();
        while (RedisHelper.getObject("booking:"+i,"bookingId")!=null){
            String bookingId=RedisHelper.getObject("booking:"+i,"bookingId");
            String bookingDate=RedisHelper.getObject("booking:"+i,"bookingDate");
            String bookingDate2=simpleFormatter.format(date);
            if(bookingDate2.equals(bookingDate)){
                rooms.addAll(Arrays.asList(findBookingByBid(bookingId).getRoomIds().split(",")));
            }
            i++;
        }
        return rooms;
    }

    @Override
    public String getNewId() {
        int i=1;
        while (RedisHelper.getObject("booking:"+i,"bookingId")!=null) {
            i++;
        }
        return i+"";

    }
}
