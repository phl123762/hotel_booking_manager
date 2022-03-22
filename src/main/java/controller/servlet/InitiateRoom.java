package controller.servlet;

import domain.Room;
import service.RoomService;
import service.impl.RoomServiceImpl;
import utils.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitiateRoom extends HttpServlet {
    private RoomService roomService=new RoomServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();
        String res="{\"res\":\"Initiate fail!\"}";

        try{
            int number=Integer.parseInt(req.getParameter("number")) ;
            Map<String,Object> resMap = new HashMap<>();
            if(roomService.InitRoom(number)==false) {
                res="{\"res\":\"Initiate fail!\"}";
                throw new Exception("Initiate fail");
            }else resMap.put("res","Initiate success!");

            List<Room> rooms=roomService.findAllRoom();

            resMap.put("data", JsonUtil.getJsonString(rooms));
            res=JsonUtil.getJsonString(resMap);
        }catch (Exception e){
            res="{\"res\":\"Initiate fail!\"}";
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
