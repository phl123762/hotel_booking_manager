package utils;

import domain.Booking;
import domain.json.User;
import domain.json.Work;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.Test;

import java.util.*;

public class JsonUtilsTest {

    @Test
    public  void testresolver() {
        try {
            String userStr = "{\n"
                    + "\t\t\"name\": \"小明\",\n"
                    + "\t\t\"age\": 12,\n"
                    + "\t\t\"work\": {\n"
                    + "\t\t\t\"place\": \"地点1\"\n"
                    + "\t\t}\n"
                    + "\t}";
            User user = JsonUtil.resolver(userStr, new TypeReference<User>(){});
            System.out.println(user);
            //User{name='小明', age=12, work=Work{place='地点1'}}
            String ListUserStr = "[\n"
                    + "\t{\n"
                    + "\t\t\"name\": \"小明\",\n"
                    + "\t\t\"age\": 12,\n"
                    + "\t\t\"work\": {\n"
                    + "\t\t\t\"place\": \"地点1\"\n"
                    + "\t\t}\n"
                    + "\t},\n"
                    + "\t{\n"
                    + "\t\t\"name\": \"小红\",\n"
                    + "\t\t\"age\": 13,\n"
                    + "\t\t\"work\": {\n"
                    + "\t\t\t\"place\": \"地点2\"\n"
                    + "\t\t}\n"
                    + "\t}\n"
                    + "]";
            List<User> list = JsonUtil.resolver(ListUserStr, new TypeReference<ArrayList<User>>(){});
            System.out.println(list);
            //[User{name='小明', age=12, work=Work{place='地点1'}}, User{name='小红', age=13, work=Work{place='地点2'}}]
            //用map解析
            List<Map> listMap = JsonUtil.resolver(ListUserStr, new TypeReference<ArrayList<Map>>(){});
            System.out.println(listMap);
            //[{name=小明, age=12, work={place=地点1}}, {name=小红, age=13, work={place=地点2}}]
            String MapUserStr = "{\n"
                    + "\t\"user1\": {\n"
                    + "\t\t\"name\": \"小明\",\n"
                    + "\t\t\"age\": 12,\n"
                    + "\t\t\"work\": {\n"
                    + "\t\t\t\"place\": \"地点1\"\n"
                    + "\t\t}\n"
                    + "\t},\n"
                    + "\t\"user2\": {\n"
                    + "\t\t\"name\": \"小红\",\n"
                    + "\t\t\"age\": 13,\n"
                    + "\t\t\"work\": {\n"
                    + "\t\t\t\"place\": \"地点2\"\n"
                    + "\t\t}\n"
                    + "\t}\n"
                    + "}";
            Map<String, User> mapUser = JsonUtil.resolver(MapUserStr, new TypeReference<Map<String, User>>(){});
            System.out.println(mapUser);
            //[User{name='小明', age=12, work=Work{place='地点1'}}, User{name='小红', age=13, work=Work{place='地点2'}}]
            String listMapUserStr = "[\n"
                    + "\t{\n"
                    + "\t\t\"user1\": {\n"
                    + "\t\t\t\"name\": \"小明\",\n"
                    + "\t\t\t\"age\": 12,\n"
                    + "\t\t\t\"work\": {\n"
                    + "\t\t\t\t\"place\": \"地点1\"\n"
                    + "\t\t\t}\n"
                    + "\t\t},\n"
                    + "\t\t\"user2\": {\n"
                    + "\t\t\t\"name\": \"小红\",\n"
                    + "\t\t\t\"age\": 13,\n"
                    + "\t\t\t\"work\": {\n"
                    + "\t\t\t\t\"place\": \"地点2\"\n"
                    + "\t\t\t}\n"
                    + "\t\t}\n"
                    + "\t},\n"
                    + "\t{\n"
                    + "\t\t\"admin1\": {\n"
                    + "\t\t\t\"name\": \"大明\",\n"
                    + "\t\t\t\"age\": 13,\n"
                    + "\t\t\t\"work\": {\n"
                    + "\t\t\t\t\"place\": \"大地点1\"\n"
                    + "\t\t\t}\n"
                    + "\t\t},\n"
                    + "\t\t\"admin2\": {\n"
                    + "\t\t\t\"name\": \"大红\",\n"
                    + "\t\t\t\"age\": 14,\n"
                    + "\t\t\t\"work\": {\n"
                    + "\t\t\t\t\"place\": \"大地点2\"\n"
                    + "\t\t\t}\n"
                    + "\t\t}\n"
                    + "\t}\n"
                    + "]";
            List<Map<String, User>> listMapUser = JsonUtil.resolver(listMapUserStr, new TypeReference<List<Map<String, User>>>(){});
            System.out.println(listMapUser);
            //[{user1=User{name='小明', age=12, work=Work{place='地点1'}}, user2=User{name='小红', age=13,
            // work=Work{place='地点2'}}}, {admin1=User{name='大明', age=13, work=Work{place='大地点1'}},
            // admin2=User{name='大红', age=14, work=Work{place='大地点2'}}}]
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public  void  teststringifyMap(){
//        Map<String, Object> map = new HashMap<>();
////        map.put("foo", "11");
////        map.put("bar", 2222);
////        map.put("bar2", null);
////        map.put("bar3", true);
////
////        String jsonStr = JsonHelper.stringify(map);
////        System.out.println(jsonStr);
//
//
//    }

    @Test
    public void testgetJsonString() throws IllegalAccessException, InstantiationException {
        User user=new User();
        user.setAge(20);
        user.setName("xiaohong");
        Work work=new Work();
        work.setPlace("shanghai");
        user.setWork(work);

//        System.out.println(JsonUtil.getJsonString(user));

        Map<String, Object> map = new HashMap<>();
        map.put("foo", "11");
        map.put("bar", 2222);
        map.put("bar2", 4444);
        map.put("bar3", true);

//        String jsonStr = JsonUtil.getJsonString(map);
//        System.out.println(jsonStr);

        Booking booking=new Booking();
        booking.setBookingId(1+"");
        booking.setBookingDate(new Date());
        booking.setGuestId(1+"");
        booking.setRoomNumber(2);
        booking.setRoomIds(Arrays.asList("1,2"));

        List<Booking> bookings=new ArrayList<>();
        bookings.add(booking);

        System.out.println(JsonUtil.getJsonString(bookings));


    }






}
