package com.surevine.community.gateway.history;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Named;

import com.surevine.community.gateway.model.collection.CircularFIFOQueue;

@Named
@Default
@ApplicationScoped
public class History {
	
	private static History history;
	
	static {
		history = new History();
	}
	
	/**
	 * Store last 250 items.
	 */
	private CircularFIFOQueue<Map<String, String>> queue = new CircularFIFOQueue<Map<String, String>>(250);
	
	public History() {
	}
	
	public void add(final String message) {
		final Map<String, String> event = new HashMap<String, String>();
		event.put("message", message);
		event.put("cdate", String.valueOf(new Date().getTime()));
		
		queue.add(event);
	}
	
	public List<Map<String, String>> get() {
		final List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		results.addAll(queue);
		return results;
	}
	
	public static History getInstance() {
		return history;
	}
}
