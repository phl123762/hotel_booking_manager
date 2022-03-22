package service.impl;

import dao.GuestDao;
import dao.impl.GuestDaoImpl;
import service.GuestService;

public class GuestServiceImpl implements GuestService {

    private GuestDao guestDao=new GuestDaoImpl();

    @Override
    public String getNewId() {
        return guestDao.getNewId();
    }
}
