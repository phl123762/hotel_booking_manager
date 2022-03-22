package controller.servlet;


import domain.Booking;
import domain.Guest;
import service.BookingService;
import service.impl.BookingServiceImpl;
import utils.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindBookingByGuestServlet extends HttpServlet {

    private BookingService bookingService=new BookingServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();
        String res="{\"res\":\"query fail!\"}";

        try {
            String  guestName=req.getParameter("guestName");
            String  guestId=req.getParameter("guestId");

            Guest guest=new Guest();
            guest.setGuestName(guestName);
            guest.setGuestId(guestId);

            List<Booking> bookings =bookingService.findBookingByGuest(guest);
            Map<String,Object> resMap = new HashMap<>();
            resMap.put("res","query success!");
            resMap.put("data", JsonUtil.getJsonString(bookings));
            res=JsonUtil.getJsonString(resMap);
        }catch (Exception e){
            res="{\"res\":\"query fail!\"}";
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
