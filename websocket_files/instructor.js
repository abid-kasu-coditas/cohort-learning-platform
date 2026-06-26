const { Client } = require("@stomp/stompjs");
const SockJS = require("sockjs-client");
const readline = require("readline");

const SESSION_ID = 1;
const TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpbnN0cnVjdG9yQGNvaG9ydC5jb20iLCJ1c2VySWQiOjIsInJvbGUiOiJJTlNUUlVDVE9SIiwiaWF0IjoxNzgyNDMzNTA3LCJleHAiOjE3ODI0MzQ0MDd9.6f8m2EKzKCpdvma6ojXb5WNW_9Iu2Cx8bB1LoM7rI6U";

const rl = readline.createInterface({ input: process.stdin, output: process.stdout });

const client = new Client({
    webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
    connectHeaders: { Authorization: `Bearer ${TOKEN}` },

    onConnect: () => {
        client.subscribe(`/topic/session/${SESSION_ID}`, msg => {
            const event = JSON.parse(msg.body);
            if (event.type === "CHAT_MESSAGE") {
                const m = event.message;
                console.log(`\n[${m.sentBy.firstName}]: ${m.text}`);
            } else {
                console.log(`\n[EVENT] ${event.type}`);
            }
        });

        rl.on("line", input => {
            if (input.trim()) {
                client.publish({
                    destination: `/app/session/${SESSION_ID}/chat`,
                    body: JSON.stringify({ messageText: input.trim() })
                });
            }
        });
    },

    onStompError: frame => {
        console.error("STOMP Error:", frame.headers["message"]);
        console.error(frame.body);
    },

    onWebSocketError: error => {
        console.error("WebSocket Error:", error);
    },

    onDisconnect: () => {
        console.log("Disconnected");
    }
});

client.activate();
