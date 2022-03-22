package utils;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisUtilTest {

//    @Test
//    public void testGetPool(){
//        JedisPool jedisPool= JedisUtil.getPool("127.0.0.1",6379,"");
//        Jedis resource=jedisPool.getResource();
//        resource.set("name","phl");
//        System.out.println(resource.get("name"));
//        resource.close();
//    }

    @Test
    public void testRedis(){
        System.out.println(RedisHelper.getJedis());
    }

    @Test
    public void testSet(){
//        System.out.println(RedisHelper.set("name", "phla"));
        System.out.println(RedisHelper.get("name"));
    }








}
