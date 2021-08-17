package org.rgbridge.gameapi.entities;

public class Event {
	private String id;
	private String name;

	public Event(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public void startEvent() {

	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
