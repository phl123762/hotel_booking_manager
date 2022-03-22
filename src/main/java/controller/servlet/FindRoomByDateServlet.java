package controller.servlet;

import domain.Room;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindRoomByDateServlet extends HttpServlet {

    private BookingService bookingService=new BookingServiceImpl();
    SimpleDateFormat simpleFormatter=new SimpleDateFormat("yyyy-MM-dd");


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();
        String res="{\"res\":\"query fail!\"}";

        try {
            String  sdate=req.getParameter("date");
            Date date=simpleFormatter.parse(sdate);
            List<Room> rooms=bookingService.findRoomByDate(date);
            Map<String,Object> resMap = new HashMap<>();
            resMap.put("res","query success!");
            resMap.put("data", JsonUtil.getJsonString(rooms));
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
