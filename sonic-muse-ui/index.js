import {
    restApiUrl,
    websocketUrl,
    allPlaylistItems,
    mainContent, playlist
} from './constants.js';

import {initializePlayer} from "./player.js";
import {PlaylistManager} from "./playlistManager.js";
import {RoomManager} from "./roomManager.js";

let player;
let currentSong;

document.addEventListener('DOMContentLoaded', async () => {

    const roomManager = new RoomManager();

    const loadingContainer = mainContent;
    const originalContent = loadingContainer.innerHTML;

    // Show loading screen
    loadingContainer.innerHTML = '<div class="loading-screen">Loading...</div>';

    try {
        const playlistManager = new PlaylistManager();
        const playlistItems = await playlistManager.syncPlaylistWithServer();

        // Simulate asynchronous operation with setTimeout
        // await new Promise(resolve => {
        //     setTimeout(async () => {
        //         resolve();
        //     }, 2000); // Simulate a 2-second delay
        // });

        // Restore original content (remove loading screen)
        loadingContainer.innerHTML = originalContent;

        // Replace the playlist container's content
        const playlistContainer = loadingContainer.getElementById('playlist-items'); // Select the playlist container
        if (playlistContainer) {
            playlistContainer.innerHTML = playlistHTML;
        } else {
            console.error("Playlist container not found.");
        }
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