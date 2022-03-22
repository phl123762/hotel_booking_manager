package domain;


import java.io.Serializable;

public class Guest implements Serializable {

//    private static final long serialVersionUID = -5127161591338561709L;
    private String guestId;
    private String guestName;

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    @Override
    public String toString() {
        return "Guest{" +
                "guestId='" + guestId + '\'' +
                ", guestName='" + guestName + '\'' +
                '}';
    }
}
