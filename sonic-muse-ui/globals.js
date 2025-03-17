import {handleMessage} from "./util.js";
import {websocketUrl} from "./constants.js";
import {WebSocketWrapper} from "./websocket.js";

let partyId = null;
let webSocket = null;

function initializeWebSocket(msg) {
    webSocket = new WebSocketWrapper(websocketUrl, handleMessage, msg);
}

function getPartyId() {
    return partyId;
}

function setPartyId(partyIdValue) {
    partyId = partyIdValue;
}

function getWebSocket() {
    return webSocket;
}

export {
    initializeWebSocket,
    getPartyId,
    setPartyId,
    getWebSocket,
};