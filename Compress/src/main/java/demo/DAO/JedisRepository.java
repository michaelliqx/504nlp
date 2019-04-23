package demo.DAO;

import demo.config.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisRepository {
    private JedisUtil jedisUtil = new JedisUtil();
    private JedisPool jedisPool = jedisUtil.getJedisPool();
    public synchronized Jedis getJedis() {
        try {
            if (jedisPool != null) {
                return jedisPool.getResource();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // return Jedis resource
    public void returnResource(final Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    // put key-value into redis
    public void put(String key, String value) {
        Jedis jedis = null;
        // get an instance from the pool
        try {
            jedis = getJedis();
            jedis.set(key, value);
        } catch (Exception e) {
            jedis.close();
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }

    // get value from redis
    public String get(String key) {
        String value = "";
        Jedis jedis = null;
        // get an instance from the pool
        try {
            jedis = getJedis();
            value = jedis.get(key);
        } catch (Exception e) {
            jedis.close();
            e.printStackTrace();
        } finally {
            jedis.close();
        }

        return value;
    }

    // whether a key exists
    public boolean exists(String key) {
        boolean bool = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            bool = jedis.exists(key);
        } catch (Exception e) {
            jedis.close();
            e.printStackTrace();
        } finally {
            jedis.close();
        }

        return bool;
    }

    public void incr(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.incr(key);
        } catch (Exception e) {
            jedis.close();
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }
}
