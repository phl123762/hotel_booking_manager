package dao;

import dao.impl.GuestDaoImpl;
import domain.Guest;
import org.junit.Test;
import utils.RedisHelper;

public class GuestDaoTest {

    private GuestDao guestDao=new GuestDaoImpl();

    @Test
    public void testInsertGuest(){
        Guest guest=new Guest();
        guest.setGuestId("1");
        guest.setGuestName("phla");
        System.out.println(guestDao.insertGuest(guest));

    }

    @Test
    public void testGetGuest(){
//        Guest guest= (Guest) RedisHelper.getObject("user:1cdbcf27-f316-41c7-b4da-52097ed46bb9");
//        System.out.println(guest);
        Guest guest=guestDao.getGuest("000");
        System.out.println(guest);
    }

    @Test
    public void testFindNewId(){
        System.out.println(guestDao.getNewId());
    }
}
