import {
    membersList,
    musicPlayer,
    playlist,
    roomPanel
} from "./constants.js";

function formatTime(time) {
    if (isNaN(time)) {
        return '0:00';
    }
    const minutes = Math.floor(time / 60);
    const seconds = Math.floor(time % 60);
    return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
}

function handleMessage(message = {}) {
    message.type = message.type.toLowerCase();

    const event = {
        ...message,
        type: message.type
    };

    switch (message.type) {
        case 'create':
            event.target = roomPanel;
            break;

        case 'member_join':
        case 'member_leave':
            event.target = membersList;
            break;

        case 'play':
        case 'pause':
        case 'seek':
            event.target = musicPlayer;
            break;

        // case 'sync_song_list':
        //     event.target = playlist;
        //     // await this.syncSongListWithParty();
        //     break;
        default:
            console.log('Unknown message:', message);
            return;
    }

    sendEvent(event);
}

function sendEvent(payload) {
    const event = new CustomEvent(payload.type, {
        detail: payload,
    });

    const target = payload.target;
    target.dispatchEvent(event);
    console.log('Triggered event:', event);
}

function showLoadingScreenOn(container) {
    container.innerHTML = '<div class="loading-screen">Loading...</div>';
}

function showErrorScreenOn(container) {
    container.innerHTML = '<div class="loading-error">Error loading. Please try again.</div>';
}

function updateActiveSong(song)
{
    const event = {
        ...song,
        type: 'update-active-song',
        target: playlist
    }
    sendEvent(event);
}

function playSong(song) {
    const event = {
        ...song,
        type: 'play-song',
        target: musicPlayer
    }
    sendEvent(event);
}

export {formatTime, handleMessage, sendEvent, showLoadingScreenOn, showErrorScreenOn, updateActiveSong, playSong};