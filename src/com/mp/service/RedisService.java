package com.mp.service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class RedisService {
    //    private static String HOST = "127.0.0.1";
    private static String HOST = "172.30.135.90";
    private static int PORT = 6379;
    private static int MAX_ACTIVE = 1024;
    private static int MAX_IDLE = 200;

    private static int MAX_WAIT = 10000;
    private static int TIMEOUT = 10000;
    private static JedisPool jedisPool = null;

    /**
     * 初始化连接池
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            jedisPool = new JedisPool(config, HOST, PORT, TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    public static void close(final Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
