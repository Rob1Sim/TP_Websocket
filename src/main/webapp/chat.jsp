<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>WebSocket Chat</title>
    <script>
        let websocket;

        function connect() {
            const wsUrl = "ws://localhost:8080/TP_Websocket_war_exploded/chat";
            let username = "<%= request.getParameter("username") %>";

            websocket = new WebSocket(wsUrl);

            websocket.onopen = function() {
                websocket.send(JSON.stringify({ type: "join", username: username }));
                appendMessage("Connected to the server.");
            };

            websocket.onmessage = function(event) {
                const message = JSON.parse(event.data);
                appendMessage(message.username + ": " + message.text);
            };

            websocket.onclose = function() {
                appendMessage("Disconnected from the server.");
            };

            websocket.onerror = function(event) {
                appendMessage("Error: " + event);
            };
        }

        function sendMessage() {
            const message = document.getElementById("messageInput").value;
            if (websocket && websocket.readyState === WebSocket.OPEN) {
                websocket.send(JSON.stringify({ type: "message", text: message, username: this.username }));
                document.getElementById("messageInput").value = "";
            } else {
                appendMessage("WebSocket is not connected.");
            }
        }

        function appendMessage(message) {
            const messageArea = document.getElementById("messages");
            const messageElement = document.createElement("p");
            messageElement.textContent = message;
            messageArea.appendChild(messageElement);
        }
    </script>
</head>
<body onload="connect()">
<h1>WebSocket Chat</h1>
    <div id="messages" style="border: 1px solid #000; height: 300px; overflow-y: auto; padding: 10px;">
    </div>
    <input type="text" id="messageInput" placeholder="Enter your message" />
    <button onclick="sendMessage()">Send</button>
</body>
</html>