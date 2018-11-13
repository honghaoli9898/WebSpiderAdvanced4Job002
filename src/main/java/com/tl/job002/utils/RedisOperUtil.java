package com.tl.job002.utils;

import redis.clients.jedis.Jedis;

/**
 * redis工具类
 * 
 * @author lihonghao
 * @date 2018年11月13日
 */
public class RedisOperUtil {
	private Jedis jedis;
	private static RedisOperUtil redisOperUtil = null;

	public static RedisOperUtil getInstance() {
		if (redisOperUtil == null) {
			return new RedisOperUtil(SystemConfigParas.redis_ip, SystemConfigParas.redis_passpost,
					SystemConfigParas.redis_auth);
		}
		return redisOperUtil;
	}

	public Jedis getJedis() {
		return jedis;
	}

	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}

	private RedisOperUtil(String ip, int port, String password) {
		jedis = new Jedis(ip, port);
		jedis.auth(password);
	}

	public String get(String key) {
		String val = jedis.get(key);
		if (val == null) {
			val = "跑到太空去了!";
		}
		return val;
	}

	public void set(String key, String value) {
		jedis.set(key, value);
	}

	public static void main(String[] args) {
		RedisOperUtil redisOperUtil = new RedisOperUtil(SystemConfigParas.redis_ip, SystemConfigParas.redis_passpost,
				SystemConfigParas.redis_auth);
		redisOperUtil.getJedis().flushAll();
	}
}
