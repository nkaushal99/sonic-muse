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

let player;
let currentSong;

document.addEventListener('DOMContentLoaded', async () => {
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
                // await playlistManager.syncPlaylistWithServer();
                resolve();
            }, 6000); // Simulate a 6-second delay
        });

        player = initializePlayer(websocketUrl);

        // Restore original content (remove loading screen)
        loadingContainer.innerHTML = originalContent;
    } catch (error) {
        console.error('Error during initialization:', error);
        loadingContainer.innerHTML = '<div class="loading-error">Error loading. Please try again.</div>';
    }

    createButton.addEventListener('click', () => {
        player.join();
    });

    joinButton.addEventListener('click', () => {
        // showJoinRoomModal();
    });
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

// function showJoinRoomModal() {
//     const modalHtml = `
//         <div class="modal-overlay" id="joinRoomModal">
//             <div class="modal">
//                 <h2>Join Room</h2>
//                 <input type="text" class="modal-input" id="roomIdInput" placeholder="Enter Room ID">
//                 <div class="modal-actions">
//                     <button class="modal-btn secondary" id="cancelJoinRoom">Cancel</button>
//                     <button class="modal-btn primary" id="confirmJoinRoom">Join</button>
//                 </div>
//             </div>
//         </div>
//     `;
//
//     document.body.insertAdjacentHTML('beforeend', modalHtml);
//
//     const modal = document.getElementById('joinRoomModal');
//     const cancelBtn = document.getElementById('cancelJoinRoom');
//     const confirmBtn = document.getElementById('confirmJoinRoom');
//     const input = document.getElementById('roomIdInput');
//
//     // Use requestAnimationFrame to ensure the modal is added to the DOM before adding the 'visible' class
//     requestAnimationFrame(() => {
//         modal.classList.add('visible');
//     });
//
//     cancelBtn.addEventListener('click', () => {
//         modal.classList.remove('visible');
//         setTimeout(() => {
//             modal.remove();
//         }, 300); // Wait for the transition to complete
//     });
//
//     confirmBtn.addEventListener('click', () => {
//         const roomId = input.value.trim();
//         if (roomId) {
//             joinRoom(roomId);
//             modal.classList.remove('visible');
//             setTimeout(() => {
//                 modal.remove();
//             }, 300);
//         } else {
//             input.classList.add('error');
//         }
//     });
//
//     input.addEventListener('input', () => {
//         input.classList.remove('error');
//     });
//
//     input.focus();
// }
//
// function joinRoom(roomId) {
//     console.log('Joining room:', roomId);
//     const notification = document.createElement('div');
//     notification.style.cssText = `
//         position: fixed;
//         bottom: 20px;
//         right: 20px;
//         background: #1db954;
//         color: white;
//         padding: 1rem 2rem;
//         border-radius: 8px;
//         animation: slideIn 0.3s ease-out;
//     `;
//     notification.textContent = `Joined room: ${roomId}`;
//     document.body.appendChild(notification);
//
//     setTimeout(() => {
//         notification.style.animation = 'slideOut 0.3s ease-in';
//         setTimeout(() => notification.remove(), 300);
//     }, 3000);
// }