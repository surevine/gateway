package com.surevine.community.gateway.util;

import redis.clients.jedis.Jedis;

public class Redis {
	
	private String connection;
	
	public Redis(final String connection) {
		this.connection = connection;
	}

	public long getIncr(final String key) {
		try (final Jedis jedis = new Jedis(connection)) {
			return jedis.incr("gateway:" +key);
		}
	}
}
