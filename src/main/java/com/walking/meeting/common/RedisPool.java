package com.walking.meeting.common;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    //如果pool是public的，那么下面的几个return方法是可以直接调用的，但是为了封装安全性所以改成private
    private static JedisPool pool;//jedis连接池

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(8);//默认8
        config.setMaxIdle(8);//默认8
        config.setMinIdle(0);//默认0

        config.setTestOnBorrow(false);//默认false
        config.setTestOnReturn(false);//默认false

        config.setBlockWhenExhausted(true);//在超过最大数量的请求,阻塞时，一直阻塞到最大超时时间，如果是false直接报错

        pool = new JedisPool(config, "localhost", 6379, 1000 * 2);
    }

    static {
        initPool();
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void returnBrokenResource(Jedis jedis) {
        pool.returnBrokenResource(jedis);
    }

    public static void returnResource(Jedis jedis) {
        pool.returnResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.set("key", "value");
        returnResource(jedis);

        pool.destroy();//临时调用,销毁连接池中所有连接
        System.out.println("End");
    }


}