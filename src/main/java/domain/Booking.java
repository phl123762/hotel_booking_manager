package domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Booking implements Serializable {

//    private static final long serialVersionUID = 1564630926975687956L;
    private String bookingId;
    private String guestId;
    private List<String> roomIds;
    private Integer roomNumber;
    private Date bookingDate;

//    public static long getSerialVersionUID() {
//        return serialVersionUID;
//    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public String getRoomIds() {
        return String.join(",", roomIds);
//        return roomIds;
    }

    public void setRoomIds(List<String> roomIds) {
        this.roomIds = roomIds;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", guestId='" + guestId + '\'' +
                ", roomIds=" + roomIds +
                ", roomNumber=" + roomNumber +
                ", bookingDate=" + bookingDate +
                '}';
    }
}
