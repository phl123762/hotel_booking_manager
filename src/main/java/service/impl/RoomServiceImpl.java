package service.impl;

import dao.RoomDao;
import dao.impl.RoomDaoImpl;
import domain.Room;
import service.RoomService;

import java.util.List;

public class RoomServiceImpl implements RoomService {

    public RoomDao roomDao=new RoomDaoImpl();

    @Override
    public boolean InitRoom(int number) {
        return roomDao.InitRoom(number);
    }

    @Override
    public List<Room> findAllRoom() {
        return roomDao.findAllRoom();
    }
}
