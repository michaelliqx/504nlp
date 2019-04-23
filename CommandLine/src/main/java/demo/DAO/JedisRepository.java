package demo.DAO;

import demo.config.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

public class JedisRepository {
    private JedisUtil jedisUtil = new JedisUtil();
    private JedisPool jedisPool = jedisUtil.getJedisPool();
    private synchronized Jedis getJedis() {
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
    public void put(String key1, String key2, String value) {
        Jedis jedis = null;
        // get an instance from the pool
        try {
            jedis = getJedis();
            if (jedis != null) jedis.hset(key1, key2, value);
        } catch (Exception e) {
            if (jedis != null) jedis.close();
            e.printStackTrace();
        } finally {
            if (jedis != null) jedis.close();
        }
    }

    // get all keys from redis
    public Map<String, String> getAll(String key) {
        String value = "";
        Jedis jedis = null;
        Map<String, String> res = null;
        // get an instance from the pool
        try {
            jedis = getJedis();
            if (jedis != null) res = jedis.hgetAll(key);
        } catch (Exception e) {
            if (jedis != null) jedis.close();
            e.printStackTrace();
        } finally {
            if (jedis != null) jedis.close();
        }

        return res;
    }

    // get value from redis
    public String get(String key1, String key2) {
        String value = "";
        Jedis jedis = null;
        // get an instance from the pool
        try {
            jedis = getJedis();
            if (jedis != null) value = jedis.hget(key1, key2);
        } catch (Exception e) {
            if (jedis != null) jedis.close();
            e.printStackTrace();
        } finally {
            if (jedis != null) jedis.close();
        }

        return value;
    }

    public String getsum(String key1) {
        String value = "";
        Jedis jedis = null;
        // get an instance from the pool
        try {
            jedis = getJedis();
            if (jedis != null) value = jedis.get(key1);
        } catch (Exception e) {
            if (jedis != null) jedis.close();
            e.printStackTrace();
        } finally {
            if (jedis != null) jedis.close();
        }

        return value;
    }

    // whether a key exists
    public boolean exists(String key) {
        boolean bool = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis != null) bool = jedis.exists(key);
        } catch (Exception e) {
            if (jedis != null) jedis.close();
            e.printStackTrace();
        } finally {
            if (jedis != null) jedis.close();
        }

        return bool;
    }

    public boolean exists(String key1, String key2) {
        boolean bool = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis != null) bool = jedis.hexists(key1, key2);
        } catch (Exception e) {
            if (jedis != null) jedis.close();
            e.printStackTrace();
        } finally {
            if (jedis != null) jedis.close();
        }

        return bool;
    }

    public void incr(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis != null) jedis.incr(key);
        } catch (Exception e) {
            if (jedis != null) jedis.close();
            e.printStackTrace();
        } finally {
            if (jedis != null) jedis.close();
        }
    }

    public void incr(String key1, String key2) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis != null) jedis.hincrBy(key1, key2, 1);
        } catch (Exception e) {
            if (jedis != null) jedis.close();
            e.printStackTrace();
        } finally {
            if (jedis != null) jedis.close();
        }
    }
}
