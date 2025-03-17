import {
    audio,
    playBtn,
    progress,
    volumeSlider,
    muteBtn,
    loopBtn,
    pauseBtnSVG,
    playBtnSVG,
    currentTimeEl,
    durationEl,
    nextBtn,
    prevBtn, musicPlayer
} from './constants.js';
import {WebSocketWrapper} from './websocket.js';
import {formatTime} from "./util.js";

class Player {
    constructor(websocketUrl) {
        this.partyId = null;
        this.webSocket = null;
        this.loopMode = 'none';
        this.currentSongUrl = null;
        this.currentSeekValue = 0;
        this.websocketUrl = websocketUrl;
        this.initializeEventListeners();
    }

    initializeEventListeners() {
        audio.addEventListener('ended', () => this.handleAudioEnded());

        audio.addEventListener('timeupdate', () => {
            const audioProgressed = (audio.currentTime / audio.duration) * 100;
            this.currentSeekValue = audioProgressed;
            progress.value = audioProgressed;
            progress.style.setProperty('--slider-value', `${audioProgressed}%`);
            currentTimeEl.textContent = formatTime(audio.currentTime);
        });

        volumeSlider.addEventListener('input', () => {
            this.setVolume(volumeSlider.value);
        });

        muteBtn.addEventListener('click', () => {
            this.toggleMute();
        });

        playBtn.addEventListener('click', () => {
            if (audio.paused)
                this.play();
            else
                this.pause()
        });

        loopBtn.addEventListener('click', () => {
            this.toggleLoop();
        });

        progress.addEventListener('input', () => {
            this.seek(progress.value);
        });

        nextBtn.addEventListener('click', () => this.next());

        prevBtn.addEventListener('click', () => this.prev());

        musicPlayer.addEventListener('play', (message) => {
            this.play(message.url, message.seek, true);
        })
    }

    join(partyIdValue) {
        this.partyId = partyIdValue ? partyIdValue : '';
        const joinMsg = {
            type: 'join',
            partyId: this.partyId
        };
        this.webSocket = !this.websocketUrl ? null : new WebSocketWrapper(this.websocketUrl, this.handleMessage.bind(this), joinMsg);
    }

    playSongForCurrentPlayer(url, seek) {
        audio.src = url;
        this.currentSongUrl = url;
        this.currentSeekValue = seek;
        audio.onloadedmetadata = () => {
            this.currentSeekValue = seek;
            progress.value = seek;
            progress.style.setProperty('--slider-value', `${seek}%`);
            audio.currentTime = (progress.value / 100) * audio.duration;
            currentTimeEl.textContent = formatTime(audio.currentTime);
            durationEl.textContent = formatTime(audio.duration);
            audio.play();
            this.updatePlayButton(false);
        };
    }

    play(url = this.currentSongUrl, seek = this.currentSeekValue, fromWebSocket = false) {
        if (!url) {
            alert("Pick a song first!");
            return;
        }

        if (!fromWebSocket && this.webSocket) {
            const message = {
                type: 'play',
                partyId: this.partyId,
                url: url, //audio.currentSrc,
                seek: seek //progress.value
            };
            this.webSocket.send(message);
            return;
        }

        this.playSongForCurrentPlayer(url, seek);
    }

    pause(fromWebSocket = false) {
        if (!fromWebSocket && this.webSocket) {
            const message = {
                type: 'pause',
                partyId: this.partyId
            };
            this.webSocket.send(message);
            return;
        }

        audio.pause();
        this.updatePlayButton(true);
    }

    updatePlayButton(pause) {
        playBtn.innerHTML = pause ? playBtnSVG : pauseBtnSVG
    }

    seek(seekValue, fromWebSocket = false) {
        if (!fromWebSocket && this.webSocket) {
            const message = {
                type: 'seek',
                partyId: this.partyId,
                seek: seekValue,
            };
            this.webSocket.send(message);
        }
        this.currentSeekValue = seekValue;
        progress.value = seekValue;
        progress.style.setProperty('--slider-value', `${seekValue}%`);
        audio.currentTime = (progress.value / 100) * audio.duration;
    }

    setVolume(volume) {
        audio.volume = volume / 100;
        volumeSlider.style.setProperty('--slider-value', `${volume}%`);
        this.updateVolumeIcon();
    }

    toggleMute() {
        audio.muted = !audio.muted;
        const volume = audio.muted ? 0 : audio.volume * 100;
        volumeSlider.value = volume;
        volumeSlider.style.setProperty('--slider-value', `${volume}%`);
        muteBtn.classList.toggle('active', audio.muted);
        this.updateVolumeIcon();
    }

    updateVolumeIcon() {
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

    toggleLoop() {
        switch (this.loopMode) {
            case 'none':
                this.loopMode = 'one';
                loopBtn.classList.add('active');
                loopBtn.title = 'Don\'t Loop';
                break;
            case 'one':
                this.loopMode = 'none';
                loopBtn.classList.remove('active');
                loopBtn.title = 'Loop';
                break;
        }
    }

    next() {
        if (!this.currentSongUrl) {
            alert("Pick a song first!");
            return;
        }

        if (this.loopMode === 'one') {
            // Stay on current song
            this.play(this.currentSongUrl, 0);
            return;
        } else {
            // go to next song
        }

        // currentSongIndex = (currentSongIndex + 1) % songs.length;
        // if (currentSongIndex === 0 && loopMode !== 'all') {
        //     // Stop at end of playlist if not looping
        //     isPlaying = false;
        //     updatePlayButton();
        //     stopProgressSimulation();
        // }
        // updateSong(currentSongIndex);
    }

    prev() {
        if (!this.currentSongUrl) {
            alert("Pick a song first!");
            return;
        }

        if (progress.value > 5) {
            // If more than 5% into song, restart current song
            this.play(this.currentSongUrl, 0);
            return;
        } else {
            // Go to previous song
            // currentSongIndex = (currentSongIndex - 1 + songs.length) % songs.length;
            // updateSong(currentSongIndex);
        }
    }

    syncSongListWithParty() {
        // if (this.webSocket) {
        //     const message = {
        //         type: 'sync_song_list',
        //         partyId: this.partyId
        //     };
        //     this.webSocket.send(message);
        // }
    }

    handleAudioEnded() {
        this.next();
    }
}

export function initializePlayer(websocketUrl) {
    return new Player(websocketUrl);
}