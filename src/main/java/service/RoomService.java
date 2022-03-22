package service;

import domain.Room;

import java.util.List;

public interface RoomService {

    boolean InitRoom(int number);
    List<Room> findAllRoom();

}
