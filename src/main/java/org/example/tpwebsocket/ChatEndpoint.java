package org.example.tpwebsocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/chat")
public class ChatEndpoint {
    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();
    private static final ConcurrentHashMap<Session, String> userMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Nouvelle connexion : " + session.getId());
        sessions.add(session);

    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            // Parse le message JSON avec Gson
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();
            String type = json.get("type").getAsString();

            if ("join".equals(type)) {
                String username = json.get("username").getAsString();
                userMap.put(session, username);
                broadcast("Server", username + " a rejoint le chat !");
            } else if ("message".equals(type)) {
                String username = userMap.get(session);
                String text = json.get("text").getAsString();
                broadcast(username, text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClose
    public void onClose(Session session) {
        String username = userMap.remove(session);
        sessions.remove(session);
        broadcast("Server", username + " a quitté le chat.");
        System.out.println("Connexion fermée : " + session.getId());
    }

    private void broadcast(String username, String message) {
        for (Session session : sessions) {
            if (session.isOpen()) {
                try {
                    JsonObject json = new JsonObject();
                    json.addProperty("username", username);
                    json.addProperty("text", message);
                    session.getBasicRemote().sendText(json.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
