import {playlist, playlistItems, restApiUrl} from "./constants.js";
import {playSong, showErrorScreenOn, showLoadingScreenOn} from "./util.js";

class PlaylistManager {

    constructor() {
        this.setupEventListeners();
    }

    setupEventListeners() {
        playlist.addEventListener('update-active-song', (event) => {
            this.updateActiveSong(event.detail);
        })
    }

    async syncPlaylistWithServer() {
        let songs;

        try {
            songs = await fetch(restApiUrl + '/song')
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                });
        } catch (uploadError) {
            console.error('Error fetching songs:', uploadError);
        }

        this.populatePlaylist(songs);
    }

    populatePlaylist(songs) {
        playlistItems.innerHTML = "";
        if (songs) {
            songs.forEach(song => {
                this.addSongToList(song);
            });
        }
    }

    updateActiveSong(song) {
        if (this.activeSong)
        {
            this.activeSong.classList.remove('active');
        }
        this.activeSong = document.getElementById(song.id);
        this.activeSong.classList.add('active');
    }

    addSongToList(song) {
        const songItem = document.createElement('li');
        songItem.id = song.id;
        songItem.classList.add('playlist-item');

        const songThumbnail = document.createElement('img');
        songThumbnail.src = 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=50&h=50&fit=crop';
        songThumbnail.alt = song.name;
        songItem.appendChild(songThumbnail);

        const songInfo = document.createElement('div');
        songInfo.classList.add('song-info');
        songItem.appendChild(songInfo);

        const songTitle = document.createElement('span');
        songTitle.classList.add('song-title');
        songTitle.textContent = song.title;
        songInfo.appendChild(songTitle);

        const songArtist = document.createElement('span');
        songArtist.classList.add('song-artist');
        songArtist.textContent = song.artist;
        songInfo.appendChild(songArtist);

        const songDuration = document.createElement('span');
        songDuration.classList.add('song-duration');
        songDuration.textContent = song.duration;
        songItem.appendChild(songDuration);

        songItem.dataset.url = song.url;

        songItem.addEventListener('click', (event) => {
            event.stopPropagation();

            // this.updateActiveSong(event.target);
            playSong(song);

        });

        playlistItems.appendChild(songItem);
    }
}

export async function initializePlaylistManager() {
    showLoadingScreenOn(playlistItems);
    try {
        const player = new PlaylistManager();
        await player.syncPlaylistWithServer();

        // Simulate asynchronous operation with setTimeout
        // await new Promise(resolve => {
        //     setTimeout(async () => {
        //         resolve();
        //     }, 2000); // Simulate a 2-second delay
        // });
    } catch (error) {
        console.error('Error during initialization: ', error);
        showErrorScreenOn(playlistItems);
    }
}