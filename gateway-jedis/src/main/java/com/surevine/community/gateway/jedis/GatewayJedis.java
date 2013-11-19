package com.surevine.community.gateway.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

public class GatewayJedis {

	private static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
	
	private GatewayJedis() {}
	
	public static void put(final String projectId, final boolean isEnabled) {
		final Jedis jedis = pool.getResource();
		
		final Transaction t = jedis.multi();
		
		try {
			t.sadd("g:projects", projectId);
			t.set("g:project:" +projectId +":enabled", String.valueOf(isEnabled));
			
			t.exec();
		} catch (final Exception e) {
			t.discard();
			
			throw e;
		} finally {
			pool.returnResource(jedis);
		}
	}
	
	public static boolean isEnabled(final String projectId) {
		final Jedis jedis = pool.getResource();
		
		try {
			return Boolean.valueOf(jedis.get("g:project:" +projectId +":enabled"));
		} finally {
			pool.returnResource(jedis);
		}
	}
}
