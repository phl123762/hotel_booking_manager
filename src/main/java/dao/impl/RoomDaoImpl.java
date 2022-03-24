package dao.impl;

import dao.RoomDao;
import domain.Guest;
import domain.Room;
import redis.clients.jedis.Transaction;
import utils.JsonUtil;
import utils.RedisHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

public class RoomDaoImpl implements RoomDao {

    private static   int number;


    @Override
    public boolean updateRoom(List<String> roomIds) {
//        Transaction transaction=RedisHelper.multi();
        try{
            for(String roomId :roomIds){
                Room room=new Room();
                room.setStatus(1+"");
                room.setRomeId(roomId);
                if (addRoom(room)==false)  return false;
            }
//            RedisHelper.exec(transaction);
        }catch (Exception e){
//            RedisHelper.discard(transaction);
            return  false;
        }
        return true;
    }

    @Override
    public boolean addRoom(Room room) {
        Transaction transaction=RedisHelper.multi();
        try{
            RedisHelper.multiSetObject(transaction,"room:"+ room.getRomeId(),"romeId",room.getRomeId());
//            RedisHelper.setObject("room:"+ room.getRomeId(),"romeId",room.getRomeId());

            if(room.getStatus()!=null&&room.getStatus().isEmpty()==false){
                RedisHelper.multiSetObject(transaction,"room:"+ room.getRomeId(),"status",room.getStatus());
                //RedisHelper.setObject("room:"+ room.getRomeId(),"status",room.getStatus());
                RedisHelper.exec(transaction);
            }
            else throw new Exception("state is null");

        }catch (Exception e){
            RedisHelper.discard(transaction);
            return false;
        }
        return true;
    }

    @Override
    public boolean InitRoom(int number) {
        RedisHelper.flush();
        this.number=number;
        try{
            for(int i=0;i<number;i++){
                Room room = new Room();
                room.setRomeId(i+1+"");
                room.setStatus("0");
                if(addRoom(room)==false) return  false;
            }
        }catch (Exception e){
            return  false;
        }
        return true;
    }

    @Override
    public Room getRoomById(String id) {
        Room room = new Room();
        try{
            room.setRomeId(RedisHelper.getObject("room:"+id,"romeId"));
            room.setStatus(RedisHelper.getObject("room:"+id,"status"));
//            if(RedisHelper.getObject("room:"+id,"beginDate")!=null)
//            room.setBeginDate(simpleFormatter.parse(RedisHelper.getObject("room:"+id,"beginDate")));

//            if(RedisHelper.getObject("room:"+id,"endDate")!=null)
//            room.setEndDate(simpleFormatter.parse(RedisHelper.getObject("room:"+id,"endDate")));
        }catch (Exception e){
            return null;
        }
        return room;
    }

    @Override
    public List<Room> findAllRoom() {
        List<Room> rooms=new ArrayList<>();
        int i=1;

        while (RedisHelper.getObject("room:"+i,"romeId")!=null){
            Room room=getRoomById(String.valueOf(i));
            rooms.add(room);
            i++;
        }

        return rooms;
    }

    @Override
    public List<String> findAllRoomIds() {
        List<String> roomIds=new ArrayList<>();
        int i=1;
        while (RedisHelper.getObject("room:"+i,"romeId")!=null){
            String id=RedisHelper.getObject("room:"+i,"romeId");
            roomIds.add(id);
            i++;
        }
        return roomIds;
    }

    @Override
    public List<String> findAllRoomIdsByStatus(String status) {
        List<String> roomIds=new ArrayList<>();
        int i=1;
        while (RedisHelper.getObject("room:"+i,"romeId")!=null){
            String id=RedisHelper.getObject("room:"+i,"romeId");
            String room_status=RedisHelper.getObject("room:"+id,"status");
            if(status.equals(room_status))
                roomIds.add(id);
            i++;
        }
        return roomIds;
    }
}
