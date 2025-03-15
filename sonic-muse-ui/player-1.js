import {
    audio,
    playBtn,
    progress,
    volumeSlider,
    muteBtn,
    currentTimeEl,
    durationEl,
} from './constants.js';
import {WebSocketWrapper} from './websocket.js';

let partyId;
let webSocket;
let player1;

async function handleMessage(message) {
    switch (message.type) {
        case 'JOIN_RESPONSE':
            partyId = message.partyId;
            break;
        case 'sync_song_list':
            await player1.syncSongListWithParty();
            break;
        case 'play':
            audio.src = message.url;
            audio.onloadedmetadata = () => {
                progress.value = message.seek;
                audio.currentTime = (progress.value / 100) * audio.duration;
                audio.play();
                // playBtn.textContent = '⏸';
                togglePlay();
            };
            break;
        case 'pause':
            audio.pause();
            // playBtn.textContent = '▶';
            togglePlay();
            break;
        case 'seek':
            progress.value = message.seek;
            audio.currentTime = (progress.value / 100) * audio.duration;
            break;
        default:
            console.log('Unknown message:', message);
    }
}

export function initializePlayer() {
    if (!player1) {
        player1 = {
            // join: (partyIdInput) => {
            //     partyId = partyIdInput;
            //     const joinMsg = {
            //         type: 'join',
            //         partyId: partyId
            //     };
            //     webSocket = !websocketUrl ? null : new WebSocketWrapper(websocketUrl, handleMessage, joinMsg);
            // },
            togglePlay: () => {
                if (!audio.currentSrc) {
                    alert("Pick a song first!");
                    return;
                }

                const message = {
                    type: audio.paused ? 'play' : 'pause',
                    partyId: partyId,
                    url: audio.currentSrc,
                    seek: progress.value,
                };
                webSocket.send(message);
            },
            seek: (seekValue) => {
                const message = {
                    type: 'seek',
                    partyId: partyId,
                    seek: seekValue,
                };
                webSocket.send(message);
            },
            setVolume: (volume) => {
                audio.volume = volume / 100;
            },
            toggleMute: () => {
                toggleMute();
            },
            syncSongListWithParty: () => {
                const message = {
                    type: 'sync_song_list',
                    partyId: partyId
                };
                webSocket.send(message);
            }
        };
    }
    return player1;
}

function togglePlay() {
    updatePlayButton();

    if (audio.paused) {
        audio.play();
    } else {
        audio.pause();
    }
}

function updatePlayButton() {
    playBtn.innerHTML = !audio.paused
        ? '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 4h4v16H6zM14 4h4v16h-4z"/></svg>'
        : '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="5 3 19 12 5 21 5 3"/></svg>';
}

function toggleMute() {
    function unMute() {
        audio.unmute();
        volumeSlider.value = audio.volume;
    }

    function mute() {
        audio.mute();
        volumeSlider.value = 0;
    }

    const isMuted = audio.muted;
    isMuted ? unMute() : mute();
    muteBtn.classList.toggle('active', isMuted);

    updateVolumeIcon();
}

function updateVolumeIcon() {
    const volume = volumeSlider.value;
    const isMuted = audio.muted;
    let icon;

    if (volume === 0 || isMuted) {
        icon = '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/><line x1="23" y1="9" x2="17" y2="15"/><line x1="17" y1="9" x2="23" y2="15"/></svg>';
    } else if (volume < 50) {
        icon = '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/><path d="M15.54 8.46a5 5 0 0 1 0 7.07"/></svg>';
    } else {
        icon = '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/><path d="M15.54 8.46a5 5 0 0 1 0 7.07"/><path d="M19.07 4.93a10 10 0 0 1 0 14.14"/></svg>';
    }

    muteBtn.innerHTML = icon;
}

// Event Listeners
playBtn.addEventListener('click', togglePlay);
nextBtn.addEventListener('click', nextSong);
prevBtn.addEventListener('click', prevSong);
shuffleBtn.addEventListener('click', toggleShuffle);
loopBtn.addEventListener('click', toggleLoop);
muteBtn.addEventListener('click', toggleMute);

volumeSlider.addEventListener('input', () => {
    isMuted = volumeSlider.value == 0;
    muteBtn.classList.toggle('active', isMuted);
    updateVolumeIcon();
});

playlistItems.forEach((item, index) => {
    item.addEventListener('click', () => {
        currentSongIndex = index;
        updateSong(index);
        if (!isPlaying) {
            togglePlay();
        }
    });
});

signinBtn.addEventListener('click', toggleAuth);
logoutBtn.addEventListener('click', toggleAuth);

// Initialize first song and volume
updateSong(currentSongIndex);
updateVolumeIcon();

audio.addEventListener('timeupdate', () => {
    progress.value = (audio.currentTime / audio.duration) * 100;
    currentTimeEl.textContent = formatTime(audio.currentTime);
});

audio.addEventListener('ended', () => {
    playBtn.textContent = '▶';
    progress.value = 0;
    currentTimeEl.textContent = '0:00';
});

audio.addEventListener('loadedmetadata', () => {
    durationEl.textContent = formatTime(audio.duration);
});

function formatTime(time) {
    if (isNaN(time)) {
        return '0:00';
    }
    const minutes = Math.floor(time / 60);
    const seconds = Math.floor(time % 60);
    return `<span class="math-inline">${minutes}:</span>{seconds < 10 ? '0' : ''}${seconds}`;
}