const partyIdInput = document.getElementById('partyId');
const joinButton = document.getElementById('joinButton');

let webSocket;
let partyId;

joinButton.addEventListener('click', () => {
    partyId = partyIdInput.value;
    connectWebSocket();
});

function connectWebSocket() {
    webSocket = new WebSocket(`ws://localhost:8080/audio-stream`);

    webSocket.onopen = () => {
        console.log('WebSocket connection established');
        window.myWebSocket = webSocket; // Store the connection in the window object
        sendJoinMessage();
    };

    webSocket.onmessage = (event) => {
        const message = JSON.parse(event.data);
        handleMessage(message);
    };

    webSocket.onclose = () => {
        console.log('WebSocket connection closed');
    };

    webSocket.onerror = (error) => {
        console.error('WebSocket error:', error);
    };
}

function sendJoinMessage() {
    if (webSocket && webSocket.readyState === WebSocket.OPEN) {
        const message = {
            type: 'join',
            partyId: partyId
        };
        webSocket.send(JSON.stringify(message));
    }
}

// function handleMessage(message) {
//     switch (message.type) {
//         case 'play':
//             audioPlayer.src = message.songUrl;
//             audioPlayer.play();
//             break;
//         case 'pause':
//             audioPlayer.pause();
//             break;
//         case 'seek':
//             audioPlayer.currentTime = message.position;
//             break;
//         case 'volume':
//             audioPlayer.volume = message.volumeLevel;
//             break;
//         case 'sync':
//             audioPlayer.currentTime = message.position;
//             break;
//         default:
//             console.log('Unknown message:', message);
//     }
// }