package org.rgbridge.gameapi;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import org.rgbridge.gameapi.callbacks.InitialisationCallback;
import org.rgbridge.gameapi.entities.Event;
import org.rgbridge.gameapi.exceptions.WebSocketException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameAPI {
	private WebSocketClient webSocketClient;
	private HashMap<String, Event> events;

	public GameAPI() {
		events = new HashMap<String, Event>();
	}

	public void initialise(String gameName, String gameId, ArrayList<Event> events, InitialisationCallback callback) {
		URI uri;

		try {
			uri = new URI("ws://localhost:32230/game");
		} catch(URISyntaxException e) {
			e.printStackTrace();
			return;
		}

		webSocketClient = new WebSocketClient(uri) {
			@Override
			public void onOpen(ServerHandshake serverHandshake) {
				JSONArray eventArray = new JSONArray();

				for(Event event : events) {
					JSONObject eventObj = new JSONObject();
					eventObj.put("name", event.getName());
					eventObj.put("id", event.getId());

					eventArray.put(eventObj);
				}

				JSONObject gameObj = new JSONObject();
				gameObj.put("method", "REG");
				gameObj.put("name", gameName);
				gameObj.put("id", gameId);
				gameObj.put("events", eventArray);

				webSocketClient.send(gameObj.toString());
				callback.onInitialised();
			}

			@Override
			public void onMessage(String message) {}

			@Override
			public void onClose(int i, String message, boolean b) {
				try {
					throw new WebSocketException(message);
				} catch(WebSocketException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Exception e) {
				callback.onFailure("Failed to connect: " + e.getMessage());
			}
		};

		webSocketClient.connect();
	}
}
