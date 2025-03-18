export class WebSocketWrapper {
    webSocket;
    messageHandler;

    constructor(websocketUrl, messageHandler, connectionMsg) {
        this.webSocket = new WebSocket(websocketUrl);
        this.messageHandler = messageHandler;
        this.connect(websocketUrl, messageHandler, connectionMsg);
    }

    connect(websocketUrl, messageHandler, connectionMsg) {
        this.webSocket.onopen = () => {
            console.log('WebSocket connection established');
            this.send(connectionMsg);
        };

        this.webSocket.onmessage = async (event) => {
            const message = JSON.parse(event.data);
            await this.handleMessage(message);
        };

        this.webSocket.onclose = () => {
            console.log('WebSocket connection closed');
        };

        this.webSocket.onerror = (error) => {
            console.error('WebSocket error:', error);
        };
    }

    async handleMessage(message) {
        if (this.messageHandler) {
            // const newMessage = {...message, source: WBS_TO_MESS?.[message.type?.toUpperCase()] };
            await this.messageHandler(message); // Callback
        } else {
            console.log('Received message (no handler):', message);
        }
    }

    send(data) {
        if (this.webSocket.readyState === WebSocket.OPEN) {
            this.webSocket.send(JSON.stringify(data));
        } else {
            console.error('WebSocket is not open.');
        }
    }

    close() {
        this.webSocket.close();
        this.webSocket = null;
    }
}