package dao;

import dao.impl.RoomDaoImpl;
import domain.Guest;
import domain.Room;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

public class RoomDaoTest {

    public RoomDao roomDao=new RoomDaoImpl();

    @Test
    public void testAddRoom(){
//        Room room=new Room();
//        room.setRomeId("1");
//        room.setBeginDate(new Date());
//        Calendar cal=Calendar.getInstance();//获取当前日期
//        cal.setTime(new Date());
//        cal.add(Calendar.DAY_OF_YEAR, +1);
//        room.setEndDate(cal.getTime());
//        System.out.println(roomDao.addRoom(room));

//        Room room2=new Room();
//        room2.setRomeId("2");
//        room2.setBeginDate(new Date());
//        Calendar cal=Calendar.getInstance();//获取当前日期
//        cal.setTime(new Date());
//        cal.add(Calendar.DAY_OF_YEAR, +1);
//        room2.setEndDate(cal.getTime());
//        System.out.println(roomDao.addRoom(room2));


//        Room room3=new Room();
//        room3.setRomeId("3");
//        room3.setBeginDate(new Date());
//        Calendar cal=Calendar.getInstance();//获取当前日期
//        cal.setTime(new Date());
//        cal.add(Calendar.DAY_OF_YEAR, +1);
//        room3.setEndDate(cal.getTime());
//        System.out.println(roomDao.addRoom(room3));

        Room room4=new Room();
        room4.setRomeId("24");
//        room4.setStatus("0");
        System.out.println(roomDao.addRoom(room4));
    }

    @Test
    public void testGetRoomDao(){
        Room room=roomDao.getRoomById("4");
        System.out.println(room);
//        System.out.println(room.getBeginDate()==null);
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(room.getBeginDate()));
    }

    @Test
    public void testUpdateRoom(){
        Room room=new Room();
        room.setRomeId("1");
        room.setStatus("1");
//        room.setBeginDate(new Date());
//        Calendar cal=Calendar.getInstance();//获取当前日期
//        cal.setTime(new Date());
//        cal.add(Calendar.DAY_OF_YEAR, +4);
//        room.setEndDate(cal.getTime());

        Room room2=new Room();
        room2.setRomeId("2");
        room2.setStatus("1");

//        room2.setBeginDate(new Date());
////        Calendar cal=Calendar.getInstance();//获取当前日期
//        cal.setTime(new Date());
//        cal.add(Calendar.DAY_OF_YEAR, +2);
//        room2.setEndDate(cal.getTime());

        Room room3=new Room();
        room3.setRomeId("3");
//        room3.setBeginDate(new Date());
////        Calendar cal=Calendar.getInstance();//获取当前日期
//        cal.setTime(new Date());
//        cal.add(Calendar.DAY_OF_YEAR, +3);
//        room3.setEndDate(cal.getTime());


//        List<Room> rooms=new ArrayList<>();
//        rooms.add(room);
//        rooms.add(room2);
//        rooms.add(room3);
        List<String> roomIds= Arrays.asList("1,2,3".split(","));
        System.out.println(roomDao.updateRoom(roomIds));
    }

    @Test
    public void testInitRoom(){
        System.out.println(roomDao.InitRoom(20));
    }

    @Test
    public void testfindAllRoom(){
        List<Room> list=roomDao.findAllRoom();
        for(Room room:list)
            System.out.println(room);
    }

    @Test
    public void testfindAllRoomId(){
//        roomDao.InitRoom(5);
        List<String> list=roomDao.findAllRoomIds();
        for(String roomid:list)
            System.out.println(roomid);
    }

    @Test
    public void testfindAllRoomIdByStatus(){
//        roomDao.InitRoom(5);
        List<String> list=roomDao.findAllRoomIdsByStatus("0");
        for(String roomid:list)
            System.out.println(roomid);
    }

}
