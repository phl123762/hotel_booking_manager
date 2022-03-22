package dao.impl;

import dao.GuestDao;
import domain.Guest;
import redis.clients.jedis.Transaction;
import utils.RedisHelper;

import java.util.UUID;

public class GuestDaoImpl implements GuestDao {


    @Override
    public boolean insertGuest(Guest guest) {
        Transaction transaction=RedisHelper.multi();
        try{
            RedisHelper.multiSetObject(transaction,"user:"+ guest.getGuestId(),"guestName",guest.getGuestName());
            RedisHelper.multiSetObject(transaction,"user:"+ guest.getGuestId(),"guestId",guest.getGuestId());
            RedisHelper.exec(transaction);
        }catch (Exception e){
            RedisHelper.discard(transaction);
            return false;
        }
        return true;
    }

    @Override
    public Guest getGuest(String id) {
        Guest guest = new Guest();
        try{
            guest.setGuestName(RedisHelper.getObject("user:"+id,"guestName"))  ;
            guest.setGuestId(RedisHelper.getObject("user:"+id,"guestId"));
        }catch (Exception e){
            return null;
        }
        return guest;
    }

    @Override
    public String getNewId() {
        int i=1;
        while (RedisHelper.getObject("guest:"+i,"guestId")!=null) {
            i++;
        }
        return i+"";
    }


}
