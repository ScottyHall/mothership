// Create WebSocket connection
const socket = new WebSocket('ws://localhost:9001');

// Connection opened
socket.addEventListener('open', function (event) {
    console.log('Connected to WebSocket server');
});

// Listen for messages from the server
socket.addEventListener('message', function (event) {
    console.log('Message from server:', event.data);
});

// Example: Join the game
socket.send('join');
