package com.surevine.community.gateway.model;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.surevine.community.gateway.GatewayJedis;

public class Projects {

	private static Map<String, Project> projects = new ConcurrentHashMap<>(10, 0.9f, 1);
	
	private Projects() {
	}
	
	public static Collection<Project> get() {
		return projects.values();
	}
	
	public static Project get(final String name) {
		return projects.get(name);
	}
	
	public static void put(final Project project) {
		GatewayJedis.put(project.getName(),  project);
		
		projects.put(project.getName(), project);
	}
}
