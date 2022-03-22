package domain;

import org.omg.PortableInterceptor.INACTIVE;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class Room implements Serializable {

//    private static final long serialVersionUID = -4887162060057966226L;
    private String romeId;
//    private Date beginDate;
//    private Date endDate;
    private String status;

//    public static long getSerialVersionUID() {
//        return serialVersionUID;
//    }

    public String getRomeId() {
        return romeId;
    }

    public void setRomeId(String romeId) {
        this.romeId = romeId;
    }

//    public Date getBeginDate() {
//        return beginDate;
//    }

//    public void setBeginDate(Date beginDate) {
//        this.beginDate = beginDate;
//    }

//    public Date getEndDate() {
//        return endDate;
//    }

//    public void setEndDate(Date endDate) {
//        this.endDate = endDate;
//    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Room{" +
                "romeId='" + romeId + '\'' +
//                ", beginDate=" + beginDate +
//                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                '}';
    }
}
