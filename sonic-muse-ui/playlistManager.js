import {allPlaylistItems, musicPlayer, playlistItems, restApiUrl} from "./constants.js";
import {sendEvent, showErrorScreenOn, showLoadingScreenOn} from "./util";

class PlaylistManager {

    async constructor() {
        this.setupEventListeners();
        // await this.syncPlaylistWithServer();
        // Simulate asynchronous operation with setTimeout (optional, for testing)
        await new Promise(resolve => {
            setTimeout(async () => {
                resolve();
            }, 2000); // Simulate a 2-second delay
        });
    }

    setupEventListeners() {
        playlistItems.addEventListener('select-song', async (event) => {

            // Update playlist active-item
            const songId = event.detail.id;
            allPlaylistItems.forEach(item => {
                item.classList.toggle('active', item.id === songId);
            });

            // todo update player
            const changeEvent = {
                ...event.detail,
                type: 'change',
                target: musicPlayer
            }
            sendEvent(changeEvent);
            // player.play(songUrl, 0);
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

    addSongToList(song) {
        const songItem = document.createElement('li');
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

        songItem.dataset.id = song.id;
        songItem.dataset.url = song.url;

        songItem.addEventListener('click', () => {
            if (this.activeSong)
            {
                this.activeSong.classList.remove('active');
            }
            this.activeSong = songItem;
            this.activeSong.classList.add('active');
            const playSongEvent = new CustomEvent('select-song', {
                detail: songItem
            });

            playlistItems.dispatchEvent(playSongEvent);
        });

        playlistItems.appendChild(songItem);
    }
}

export function initializePlaylistManager() {
    showLoadingScreenOn(playlistItems);
    try {
        return new PlaylistManager();
    } catch (error) {
        console.error('Error during initialization: ', error);
        showErrorScreenOn(playlistItems);
    }
}