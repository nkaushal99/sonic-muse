import {
    audio,
    playButton,
    timeSlider,
    volumeSlider,
    volumeButton,
    timePassedDisplay,
    durationDisplay
} from './constants.js';
import {WebSocketWrapper} from './websocket.js';

let partyId;
let webSocket;
let player;

async function handleMessage(message) {
    switch (message.type) {
        case 'JOIN_RESPONSE':
            partyId = message.partyId;
            break;
        case 'sync_song_list':
            await player.syncSongListWithParty();
            break;
        case 'play':
            audio.src = message.url;
            audio.onloadedmetadata = () => {
                timeSlider.value = message.seek;
                audio.currentTime = (timeSlider.value / 100) * audio.duration;
                audio.play();
                playButton.textContent = '⏸';
            };
            break;
        case 'pause':
            audio.pause();
            playButton.textContent = '▶';
            break;
        case 'seek':
            timeSlider.value = message.seek;
            audio.currentTime = (timeSlider.value / 100) * audio.duration;
            break;
        default:
            console.log('Unknown message:', message);
    }
}

export function initializePlayer(websocketUrl) {
    if (!player) {
        player = {
            join: (partyIdInput) => {
                partyId = partyIdInput;
                const joinMsg = {
                    type: 'join',
                    partyId: partyId
                };
                webSocket = new WebSocketWrapper(websocketUrl, handleMessage, joinMsg);
            },
            playPause: () => {
                if (!audio.currentSrc) {
                    alert("Pick a song first!");
                    return;
                }

                const message = {
                    type: audio.paused ? 'play' : 'pause',
                    partyId: partyId,
                    url: audio.currentSrc,
                    seek: timeSlider.value,
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
                if (audio.muted) {
                    audio.muted = false;
                    volumeButton.textContent = '🔊';
                    volumeSlider.value = audio.volume * 100;
                } else {
                    audio.muted = true;
                    volumeButton.textContent = '🔇';
                    volumeSlider.value = 0;
                }
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
    return player;
}

audio.addEventListener('timeupdate', () => {
    timeSlider.value = (audio.currentTime / audio.duration) * 100;
    timePassedDisplay.textContent = formatTime(audio.currentTime);
});

audio.addEventListener('ended', () => {
    playButton.textContent = '▶';
    timeSlider.value = 0;
    timePassedDisplay.textContent = '0:00';
});

audio.addEventListener('loadedmetadata', () => {
    durationDisplay.textContent = formatTime(audio.duration);
});

function formatTime(time) {
    if (isNaN(time)) {
        return '0:00';
    }
    const minutes = Math.floor(time / 60);
    const seconds = Math.floor(time % 60);
    return `<span class="math-inline">${minutes}:</span>{seconds < 10 ? '0' : ''}${seconds}`;
}