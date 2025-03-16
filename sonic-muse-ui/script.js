document.addEventListener('DOMContentLoaded', () => {
    const playBtn = document.getElementById('play');
    const prevBtn = document.getElementById('prev');
    const nextBtn = document.getElementById('next');
    const shuffleBtn = document.getElementById('shuffle');
    const loopBtn = document.getElementById('loop');
    const muteBtn = document.getElementById('mute');
    const volumeSlider = document.getElementById('volume');
    const progress = document.getElementById('progress');
    const currentTimeEl = document.getElementById('current-time');
    const durationEl = document.getElementById('duration');
    const playlistItems = document.querySelectorAll('.playlist-item');
    const signinBtn = document.getElementById('signin-btn');
    const userMenu = document.getElementById('user-menu');
    const logoutBtn = document.getElementById('logout-btn');

    let isPlaying = false;
    let isShuffled = false;
    let loopMode = 'none'; // none, one, all
    let isMuted = false;
    let previousVolume = 100;
    let currentSongIndex = 0;
    let isAuthenticated = false;

    const songs = [
        {
            title: 'Midnight Rain',
            artist: 'Dream Echoes',
            duration: '3:45',
            cover: 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=300&h=300&fit=crop'
        },
        {
            title: 'Neon Lights',
            artist: 'Urban Pulse',
            duration: '4:20',
            cover: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop'
        },
        {
            title: 'Sunset Drive',
            artist: 'Coastal Waves',
            duration: '3:55',
            cover: 'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=300&h=300&fit=crop'
        }
    ];

    let shuffledIndices = [...Array(songs.length).keys()];

    // function togglePlay() {
    //     isPlaying = !isPlaying;
    //     updatePlayButton();
    //
    //     if (isPlaying) {
    //         startProgressSimulation();
    //     } else {
    //         stopProgressSimulation();
    //     }
    // }
    //
    // function updatePlayButton() {
    //     playBtn.innerHTML = isPlaying
    //         ? '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 4h4v16H6zM14 4h4v16h-4z"/></svg>'
    //         : '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="5 3 19 12 5 21 5 3"/></svg>';
    // }

    function toggleShuffle() {
        isShuffled = !isShuffled;
        shuffleBtn.classList.toggle('active', isShuffled);

        if (isShuffled) {
            shuffledIndices = shuffledIndices.sort(() => Math.random() - 0.5);
        } else {
            shuffledIndices = [...Array(songs.length).keys()];
        }
    }

    // function toggleMute() {
    //     isMuted = !isMuted;
    //     muteBtn.classList.toggle('active', isMuted);
    //
    //     if (isMuted) {
    //         previousVolume = volumeSlider.value;
    //         volumeSlider.value = 0;
    //     } else {
    //         volumeSlider.value = previousVolume;
    //     }
    //
    //     updateVolumeIcon();
    // }
    //
    // function updateVolumeIcon() {
    //     const volume = volumeSlider.value;
    //     let icon;
    //
    //     if (volume == 0 || isMuted) {
    //         icon = '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/><line x1="23" y1="9" x2="17" y2="15"/><line x1="17" y1="9" x2="23" y2="15"/></svg>';
    //     } else if (volume < 50) {
    //         icon = '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/><path d="M15.54 8.46a5 5 0 0 1 0 7.07"/></svg>';
    //     } else {
    //         icon = '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/><path d="M15.54 8.46a5 5 0 0 1 0 7.07"/><path d="M19.07 4.93a10 10 0 0 1 0 14.14"/></svg>';
    //     }
    //
    //     muteBtn.innerHTML = icon;
    // }

    let progressInterval;
    let currentProgress = 0;

    function startProgressSimulation() {
        stopProgressSimulation();
        currentProgress = parseFloat(progress.style.width) || 0;

        progressInterval = setInterval(() => {
            currentProgress = (currentProgress + 1) % 100;
            progress.style.width = `${currentProgress}%`;

            // Update current time based on progress
            const duration = 225; // 3:45 in seconds
            const currentSeconds = Math.floor((duration * currentProgress) / 100);
            currentTimeEl.textContent = formatTime(currentSeconds);

            // Handle auto-next when song ends
            if (currentProgress === 0) {
                if (loopMode === 'one') {
                    // Just reset progress for same song
                    currentProgress = 0;
                } else {
                    nextSong();
                }
            }
        }, 1000);
    }

    function stopProgressSimulation() {
        clearInterval(progressInterval);
    }

    function formatTime(seconds) {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
    }

    function updateSong(index) {
        const actualIndex = isShuffled ? shuffledIndices[index] : index;
        const song = songs[actualIndex];
        document.getElementById('track-title').textContent = song.title;
        document.getElementById('track-artist').textContent = song.artist;
        document.getElementById('cover-art').src = song.cover;
        durationEl.textContent = song.duration;

        // Update active playlist item
        playlistItems.forEach((item, i) => {
            item.classList.toggle('active', i === actualIndex);
        });

        // Reset progress
        currentProgress = 0;
        progress.style.width = '0%';
        currentTimeEl.textContent = '0:00';

        if (isPlaying) {
            startProgressSimulation();
        }
    }

    function toggleAuth() {
        isAuthenticated = !isAuthenticated;
        if (isAuthenticated) {
            signinBtn.style.display = 'none';
            userMenu.classList.remove('hidden');
        } else {
            signinBtn.style.display = 'block';
            userMenu.classList.add('hidden');
        }
    }

    // Event Listeners
    playBtn.addEventListener('click', togglePlay);
    nextBtn.addEventListener('click', nextSong);
    prevBtn.addEventListener('click', prevSong);
    shuffleBtn.addEventListener('click', toggleShuffle);
    loopBtn.addEventListener('click', toggleLoop);
    muteBtn.addEventListener('click', toggleMute);

    volumeSlider.addEventListener('input', () => {
        isMuted = volumeSlider.value == 0;
        muteBtn.classList.toggle('active', isMuted);
        updateVolumeIcon();
    });

    playlistItems.forEach((item, index) => {
        item.addEventListener('click', () => {
            currentSongIndex = index;
            updateSong(index);
            if (!isPlaying) {
                togglePlay();
            }
        });
    });

    signinBtn.addEventListener('click', toggleAuth);
    logoutBtn.addEventListener('click', toggleAuth);

    // Initialize first song and volume
    updateSong(currentSongIndex);
    updateVolumeIcon();
});