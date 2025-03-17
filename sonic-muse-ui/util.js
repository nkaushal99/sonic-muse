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

    const event = {...message};

    switch (message.type) {
        case 'create':
            event.target = roomPanel;
            break;
        case 'member_join':
            event.target = membersList;
            break;
        case 'member_leave':
            event.target = membersList;
            break;
        case 'play':
            event.target = musicPlayer;
            // this.play(message.url, message.seek, true);
            break;
        case 'pause':
            event.target = musicPlayer;
            // this.pause(true);
            break;
        case 'seek':
            event.target = musicPlayer;
            // this.seek(message.seek, true);
            break;
        case 'sync_song_list':
            event.target = playlist;
            // await this.syncSongListWithParty();
            break;
        default:
            console.log('Unknown message:', message);
    }

    sendEvent(event);
}

function sendEvent(body) {
    const event = new CustomEvent(body.type, {
        detail: body
    });

    const target = body.target;
    target.dispatchEvent(event);
    console.log('Triggered event:', event);
}

function getLoadingScreen()
{
    return '<div class="loading-screen">Loading...</div>'
}

export {formatTime, handleMessage, getLoadingScreen};