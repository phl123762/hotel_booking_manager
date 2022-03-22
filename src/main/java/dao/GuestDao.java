package dao;

import domain.Guest;

public interface GuestDao {

    boolean insertGuest(Guest guest);

    Guest getGuest(String id);

    String getNewId();
}
