package com.surevine.community.gateway.model;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.surevine.community.gateway.GatewayJedis;

public class Projects {

	/**
	 * IDs need to be namespaced by source system.
	 */
	private static Map<String, Project> projects = new ConcurrentHashMap<>(10, 0.9f, 1);
	
	private Projects() {
	}
	
	public static Collection<Project> get() {
		return projects.values();
	}
	
	public static Project get(final String id) {
		return projects.get(id);
	}
	
	public static void put(final Project project) {
		GatewayJedis.put(project);
		
		projects.put(project.getId(), project);
	}
	
	public static void init(final Project project) {
		GatewayJedis.init(project);
		
		GatewayJedis.put(project);
		
		projects.put(project.getId(), project);
	}
}
