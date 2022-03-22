package domain.json;

/**
 * 测试类
 */
public class Work {
    String place;

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    @Override
    public String toString() {
        return "Work{" +
                "place='" + place + '\'' +
                '}';
    }
}

