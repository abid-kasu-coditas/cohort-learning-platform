const { Client } = require("@stomp/stompjs");
const SockJS = require("sockjs-client");
const readline = require("readline");

const SESSION_ID = 1;
const TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYmlkQGdtYWlsLmNvbSIsInVzZXJJZCI6NCwicm9sZSI6IlNUVURFTlQiLCJpYXQiOjE3ODI0MzM1NDksImV4cCI6MTc4MjQzNDQ0OX0.JtT1OVPt3MxS7IAnq2PUaNg2COceKGtzLE2-HPosQWo";

const rl = readline.createInterface({ input: process.stdin, output: process.stdout });

const client = new Client({
    webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
    connectHeaders: { Authorization: `Bearer ${TOKEN}` },

    debug: str => {
        if (str.includes("ERROR") || str.includes("RECEIPT") || str.includes("MESSAGE")) {
            console.log("[STOMP]", str);
        }
    },

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

    onWebSocketClose: () => console.error("WebSocket closed — token expired or server down"),
    onDisconnect: () => console.log("Disconnected")
});

client.activate();
