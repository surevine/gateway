package com.surevine.community.gateway;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import com.surevine.community.gateway.model.Project;

public class GatewayJedis {

	private static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
	
	private GatewayJedis() {}
	
	public static void put(final Project project) {
		final Jedis jedis = pool.getResource();
		
		final Transaction t = jedis.multi();
		
		try {
			t.sadd("g:projects", project.getId());
			t.set("g:project:" +project.getId() +":enabled", String.valueOf(project.isEnabled()));
			
			t.exec();
		} catch (final Exception e) {
			t.discard();
			
			throw e;
		} finally {
			pool.returnResource(jedis);
		}
	}
	
	public static void init(final Project project) {
		final Jedis jedis = pool.getResource();
		
		try {
			project.setEnabled(Boolean.valueOf(jedis.get("g:project:" +project.getId() +":enabled")));
		} finally {
			pool.returnResource(jedis);
		}
	}
}
