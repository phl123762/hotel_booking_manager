package dao;

import domain.Room;

import java.util.Date;
import java.util.List;

public interface RoomDao {


    boolean updateRoom(List<String> roomIds);

    boolean addRoom(Room room);

    boolean InitRoom(int number);

    Room getRoomById(String id);

    List<Room> findAllRoom ();

    List<String> findAllRoomIds ();

    List<String> findAllRoomIdsByStatus (String status);



}
