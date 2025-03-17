import {
    websocketUrl,
    allPlaylistItems,
    playlistItems
} from './constants.js';

import {initializePlayer} from "./player.js";
import {PlaylistManager} from "./playlistManager.js";
import {RoomManager} from "./roomManager.js";
import {getLoadingScreen} from "./util.js";
import {initializeGlobals} from "./globals.js";

let player;
let currentSong;

document.addEventListener('DOMContentLoaded', async () => {

    initializeGlobals();

    const roomManager = new RoomManager();

    // Show loading screen
    const loadingContainer = playlistItems;
    loadingContainer.innerHTML = getLoadingScreen();

    try {
        const playlistManager = new PlaylistManager();
        // await playlistManager.syncPlaylistWithServer();

        // Simulate asynchronous operation with setTimeout (optional, for testing)
        await new Promise(resolve => {
            setTimeout(async () => {
                resolve();
            }, 2000); // Simulate a 2-second delay
        });

        // Replace the playlist container's content
        loadingContainer.innerHTML = playlistItems.innerHTML;
    } catch (error) {
        console.error('Error during initialization:', error);
        loadingContainer.innerHTML = '<div class="loading-error">Error loading. Please try again.</div>';
    }

    player = initializePlayer(websocketUrl);
});

document.addEventListener('selectSong', async (event) => {
    // Update the global var
    currentSong = event.detail;

    // Update active playlist item
    const songId = event.detail.id;
    allPlaylistItems.forEach(item => {
        item.classList.toggle('active', item.id === songId);
    });

    // update player
    const songUrl = event.detail.url;
    player.play(songUrl, 0);
})