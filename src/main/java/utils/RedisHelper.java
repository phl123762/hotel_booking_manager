package utils;


import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.*;

public class RedisHelper {

    /**
     * 分布式 jedisPoolResource
     */
    private static final ShardedJedis jedisPoolResource;

    /**
     * 单台 redis
     */
    private static final Jedis jedis;

    static {
        // 1.加载配置文件获取配置信息
        Properties properties = new Properties();
        try {
            properties.load(RedisHelper.class.getClassLoader().getResourceAsStream("redis.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* -------------------------------- 2.常规 redis 配置 ------------------------------------ */
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(10);
        config.setMaxTotal(20);
        config.setMinIdle(5);
        JedisPool jedisPool = new JedisPool(config, properties.getProperty("host"),
                Integer.parseInt(properties.getProperty("port")),
                Integer.parseInt(properties.getProperty("timeout")),
                properties.getProperty("password"));
        jedis = jedisPool.getResource(); // 从连接池中获取一个 Jedis 对象


        /* -------------------------------- 3.分布式 redis 配置 ------------------------------------ */
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(20);  // 设置最大对象数
        jedisPoolConfig.setMaxIdle(10);  // 最大能够保持空闲状态的对象数
        jedisPoolConfig.setMaxWaitMillis(10000);  // 超时时间
        jedisPoolConfig.setTestOnBorrow(true);  // 在获取连接的时候检查有效性, 默认false
        jedisPoolConfig.setTestOnReturn(true);  // 在返回Object时, 对返回的connection进行validateObject校验

        List<JedisShardInfo> shardInfos = new ArrayList<>();  // 如果是集群，创建一个list
        JedisShardInfo shardInfo1 = new JedisShardInfo(properties.getProperty("host"),
                Integer.parseInt(properties.getProperty("port")));  // 单台 redis 信息
        shardInfo1.setPassword(properties.getProperty("password"));  // 如果需要密码则设置
        shardInfos.add(shardInfo1);  // 添加进list

        ShardedJedisPool shardedJedisPool = new ShardedJedisPool(jedisPoolConfig, shardInfos);  // ShardedJedisPool
        jedisPoolResource = shardedJedisPool.getResource();  // 从连接池中获取一个ShardedJedis对象


    }

    /**
     * 从连接池中获取一个 ShardedJedis 对象
     */
    public static ShardedJedis getShardedJedis() {
        return jedisPoolResource;
    }

    /**
     * 从连接池中获取一个 Jedis 对象
     */
    public static Jedis getJedis(){
        return jedis;
    }

    /* ------------------------------------- String 相关操作 -------------------------------------------*/

    /**
     * 用于设置给定 key 的值。如果 key 已经存储其他值， SET 就覆写旧值，且无视类型
     */
    public static String set(String key, String value){
        return jedis.set(key, value);
    }

    /**
     * 用于获取指定 key 的值。如果 key 不存在，返回 nil 。如果key 储存的值不是字符串类型，返回一个错误
     */
    public static String get(String key){
        return jedis.get(key);
    }

    public static Long setObject(String hset,String key,String value){
//        return jedis.set(key.getBytes(), SerializeUtil.serizlize(object));
        return jedis.hset(hset,key, value);
    }

    public static String getObject(String hset,String key){
        return jedis.hget(hset,key);
    }


    public static Transaction multi(){
        return jedis.multi();
    }

    public static List<Object> exec(Transaction transaction){
       return transaction.exec();
    }

    public static String discard(Transaction transaction){
        return transaction.discard();
    }


    public static Response<String> multiSet(Transaction transaction,String key, String value){
        return transaction.set(key,value);
    }

    public static Response<Long> multiSetObject(Transaction transaction,String hset, String key, String value){
        return transaction.hset(hset,key, value);
    }

    public static String watch( String key){
        return jedis.watch(key);
    }




}
