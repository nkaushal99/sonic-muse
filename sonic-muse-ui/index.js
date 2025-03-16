import {
    joinButton,
    restApiUrl,
    websocketUrl,
    createButton,
    playlistItems,
    mainContent
} from './constants.js';

import {initializePlayer} from "./player.js";
import {PlaylistManager} from "./playlistManager.js";
import {showJoinRoomModal, warmupJoinModal} from "./joinModal.js";

let player;
let currentSong;

document.addEventListener('DOMContentLoaded', async () => {

    createButton.addEventListener('click', () => {
        player.join();
    });

    warmupJoinModal();

    joinButton.addEventListener('click', () => {
        showJoinRoomModal();
    });

    const loadingContainer = mainContent;
    const originalContent = loadingContainer.innerHTML;

    // Show loading screen
    loadingContainer.innerHTML = '<div class="loading-screen">Loading...</div>';

    try {
        const playlistManager = new PlaylistManager(restApiUrl);
        // await playlistManager.syncPlaylistWithServer();

        // Simulate asynchronous operation with setTimeout
        await new Promise(resolve => {
            setTimeout(async () => {
                resolve();
            }, 2000); // Simulate a 2-second delay
        });

        // Restore original content (remove loading screen)
        loadingContainer.innerHTML = originalContent;
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
    playlistItems.forEach(item => {
        item.classList.toggle('active', item.id === songId);
    });

    // update player
    const songUrl = event.detail.url;
    player.play(songUrl, 0);
})