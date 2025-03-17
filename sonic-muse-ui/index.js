import {initializePlayer} from "./player.js";
import {initializePlaylistManager} from "./playlistManager.js";
import {initializeRoomManager} from "./roomManager.js";
import {initializeGlobals} from "./globals.js";

document.addEventListener('DOMContentLoaded', () => {
    initializeGlobals();
    initializeRoomManager();
    initializePlaylistManager();
    initializePlayer();
});