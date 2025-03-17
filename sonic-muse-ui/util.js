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
// export const MESSAGE_TYPES = {
//     CREATE: "CREATE"
// }

function handleMessage(message = {}) {
    message.type = message?.type?.toLowerCase?.();

    const event = {...message};
    // const event = {
    //     type: messageType,
    //     target: message?.source ?  document.querySelector(`#${message?.source}`): undefined,
    // };

    switch (message.type) {
        // case MESSAGE_TYPES.CREATE:
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

export {formatTime, handleMessage};