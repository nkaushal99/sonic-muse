import {
    joinButton,
    fileUpload,
    volumeSlider,
    volumeButton,
    restApiUrl,
    songListContainerId,
    websocketUrl,
} from './constants.js';

import {initializePlayer} from './player.js';
import {handleUpload} from './upload.js';
import {SongListManager} from './songListManager.js';

let player;
let songListManager;

document.addEventListener('DOMContentLoaded', async () => {
    songListManager = new SongListManager(restApiUrl, songListContainerId);
    await songListManager.syncSongListWithServer();

    player = initializePlayer(websocketUrl);

    joinButton.addEventListener('click', () => {
        const partyIdInput = document.getElementById('party-id-input').value;
        player.join(partyIdInput);
    });

    fileUpload.addEventListener('change', async function (event) {
        const file = event.target.files[0];
        if (file) {
            await handleUpload(file);
        }
    });

    volumeSlider.addEventListener('input', () => {
        player.setVolume(volumeSlider.value);
    });

    volumeButton.addEventListener('click', () => {
        player.toggleMute();
    });

    document.getElementById('play-button').addEventListener('click', () => {
        player.playPause();
    });

    document.getElementById('time-slider').addEventListener('input', () => {
        player.seek(document.getElementById('time-slider').value);
    });

});