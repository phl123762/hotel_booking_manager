package controller.servlet;

import domain.Booking;
import domain.Guest;
import domain.Room;
import jdk.nashorn.internal.parser.JSONParser;
import service.BookingService;
import service.GuestService;
import service.impl.BookingServiceImpl;
import service.impl.GuestServiceImpl;
import utils.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class AddBookingServlet extends HttpServlet {

    private BookingService  bookingService=new BookingServiceImpl();
    SimpleDateFormat simpleFormatter=new SimpleDateFormat("yyyy-MM-dd");
    private GuestService guestService=new GuestServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        String res="{\"res\":\"add fail!\"}";
        Map<String,Object> resMap = new HashMap<>();

        try{
//            Random random = new Random(10000000);//指定种子数字

            //set value to guest object
            String  guestName=req.getParameter("guestName");

//            String  guestId=random.nextInt(10000000)+"";
            String guestId=guestService.getNewId();
            Guest guest=new Guest();
            guest.setGuestName(guestName);
            guest.setGuestId(guestId);

            //set value to booking object
            Booking booking=new Booking();
            int roomNumber=Integer.parseInt(req.getParameter("roomNumber")) ;
//            String bookingId=random.nextInt(10000000)+"";
            String bookingId=bookingService.getNewId();

            List<String> roomIds= new ArrayList<>();
            //get qualified room
            List<Room>rooms= null;
            try {
                rooms = bookingService.findRoomByDate(simpleFormatter.parse(req.getParameter("bookingDate")));
            } catch (ParseException e) {
                res="{\"res\":\"add fail!\"}";
            }
            for(int i=0;i<roomNumber;i++){
                roomIds.add(rooms.get(i).getRomeId());
            }

            Date bookingDate = new Date();
            try {
                bookingDate=simpleFormatter.parse(req.getParameter("bookingDate")) ;
            } catch (ParseException e) {
                res="{\"res\":\"add fail!\"}";
            }
            booking.setRoomIds(roomIds);
            booking.setRoomNumber(roomNumber);
            booking.setGuestId(guestId);
            booking.setBookingDate(bookingDate);
            booking.setBookingId(bookingId);

            if(bookingService.addBooking(booking,guest)==true){
//                res="{\"res\":\"add success!\"}";
            }else{
                res="{\"res\":\"add fail!\"}";
            }

            try {
                res = JsonUtil.getJsonString(resMap);
            } catch (InstantiationException e) {
                res="{\"res\":\"add fail!\"}";
            } catch (IllegalAccessException e) {
                res="{\"res\":\"add fail!\"}";
            }

            res="{\"res\":\"add success!\"}";
        }catch (Exception e){
            res="{\"res\":\"add fail!\"}";
        }finally {
            out.println(res);
            out.flush();
            out.close();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

}
