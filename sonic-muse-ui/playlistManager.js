import {playlist, playlistItems, restApiUrl} from "./constants.js";

export class PlaylistManager {

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
            const playSongEvent = new CustomEvent('selectSong', {
                detail: song
            });

            document.dispatchEvent(playSongEvent);
        });

        playlistItems.appendChild(songItem);
    }
}