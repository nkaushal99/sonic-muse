import {initializePlayer} from './player.js';

export class SongListManager {
    constructor(restApiBaseUrl, songListContainerId) {
        this.restApiBaseUrl = restApiBaseUrl;
        this.songListContainer = document.getElementById(songListContainerId);

        if (!this.songListContainer) {
            console.error(`Song list container with ID '${songListContainerId}' not found.`);
        }
    }

    async syncSongListWithServer() {
        let songList;

        try {
            songList = await fetch(this.restApiBaseUrl + '/song')
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                });
        } catch (uploadError) {
            console.error('Error fetching songs:', uploadError);
        }

        this.populateSongList(songList);
    }

    populateSongList(songList) {
        if (songList) {
            songList.forEach(song => {
                this.addSongToList(song);
            });
        }
    }

    addSongToList(song) {
        const songItem = document.createElement('div');
        songItem.classList.add('song-list-item');

        const trackInfo = document.createElement('div');
        trackInfo.classList.add('track-info');

        const trackTitle = document.createElement('span');
        trackTitle.classList.add('track-title');
        trackTitle.textContent = song.title;

        const trackDuration = document.createElement('span');
        trackDuration.classList.add('track-duration');
        trackDuration.textContent = song.duration;

        trackInfo.appendChild(trackTitle);
        songItem.appendChild(trackInfo);
        songItem.appendChild(trackDuration);
        songItem.dataset.url = song.url;

        songItem.addEventListener('click', () => {
            const player = initializePlayer();
            player.playPause({url: song.url});
        });

        this.songListContainer.appendChild(songItem);
    }
}