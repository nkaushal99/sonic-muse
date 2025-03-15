import {
    joinButton,
    fileUpload,
    volumeSlider,
    muteBtn,
    restApiUrl,
    playlist,
    websocketUrl, playBtn, progress, createButton,
    audio, durationEl, currentTimeEl, playlistItems
} from './constants.js';

import {initializePlayer} from "./player.js";
import {PlaylistManager} from "./playlistManager.js";

let player;

document.addEventListener('DOMContentLoaded', async () => {
    const playlistManager = new PlaylistManager(restApiUrl);
    await playlistManager.syncPlaylistWithServer();

    player = initializePlayer(websocketUrl);

    createButton.addEventListener('click', () => {
        player.join();
    });

    joinButton.addEventListener('click', () => {
        // showJoinRoomModal();
    });

    volumeSlider.addEventListener('input', () => {
        player.setVolume(volumeSlider.value);
    });

    muteBtn.addEventListener('click', () => {
        player.toggleMute();
    });

    playBtn.addEventListener('click', () => {
        player.togglePlay();
    });

    progress.addEventListener('input', () => {
        player.seek(progress.value);
    });

    audio.addEventListener('timeupdate', () => {
        const audioProgressed = (audio.currentTime / audio.duration) * 100;
        progress.value = audioProgressed;
        progress.style.setProperty('--slider-value', `${audioProgressed}%`);
        currentTimeEl.textContent = formatTime(audio.currentTime);
    });

    audio.addEventListener('ended', () => {
        player.updatePlayButton();
        progress.style.setProperty('--slider-value', '0%');
        currentTimeEl.textContent = '0:00';
    });

    audio.addEventListener('loadedmetadata', () => {
        currentTimeEl.textContent = formatTime(audio.currentTime);
        durationEl.textContent = formatTime(audio.duration);
    });

    function formatTime(time) {
        if (isNaN(time)) {
            return '0:00';
        }
        const minutes = Math.floor(time / 60);
        const seconds = Math.floor(time % 60);
        return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
    }
});

document.addEventListener('selectSong', async (event) => {
    // Update active playlist item
    const songId = event.detail.id;
    playlistItems.forEach(item => {
        item.classList.toggle('active', item.id === songId);
    });

    // update player
    const songUrl = event.detail.url;
    player.play(songUrl);

    // Reset progress
    progress.value = 0;
    progress.style.setProperty('--slider-value', '0%');
    currentTimeEl.textContent = '0:00';
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