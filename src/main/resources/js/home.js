const audio = document.getElementById('hidden-audio');
const fileUpload = document.getElementById('song-file');
const playButton = document.getElementById('play-button').querySelector('button');
const previousButton = document.getElementById('previous-button').querySelector('button');
const nextButton = document.getElementById('next-button').querySelector('button');
const volumeButton = document.getElementById('volume-btn');
const volumeSlider = document.getElementById('volume-slider');
const timeSlider = document.getElementById('time-slider');
const timePassedDisplay = document.getElementById('time-passed');
const durationDisplay = document.getElementById('duration');

const partyIdInput = document.getElementById('party-id-input');
const joinButton = document.getElementById('join-btn');

let webSocket;
let partyId;

function connectWebSocket() {
    webSocket = new WebSocket(`ws://localhost:8080/audio-stream`);

    webSocket.onopen = () => {
        console.log('WebSocket connection established');
        sendJoinMessage();
    };

    webSocket.onmessage = (event) => {
        const message = JSON.parse(event.data);
        handleMessage(message);
    };

    webSocket.onclose = () => {
        console.log('WebSocket connection closed');
    };

    webSocket.onerror = (error) => {
        console.error('WebSocket error:', error);
    };
}

function handleMessage(message) {
    switch (message.type) {
        case 'play':
            audio.src = message.songUrl;
            audio.onloadedmetadata = () => {
                timeSlider.value = message.seek;
                audio.currentTime = (timeSlider.value / 100) * audio.duration;
                audio.play();
                playButton.textContent = '⏸';
            };
            break;
        case 'pause':
            audio.pause();
            playButton.textContent = '▶';
            break;
        case 'previous':
            // audio.currentTime = 0;
            // audio.play();
            // playButton.textContent = '⏸';
            // break;
        case 'next':
            // audio.currentTime = audio.duration;
            // audio.play();
            // playButton.textContent = '⏸';
            break;
        case 'seek':
            timeSlider.value = message.seek;
            audio.currentTime = (timeSlider.value / 100) * audio.duration;
            break;
        // case 'volume':
        //     audio.volume = message.volumeLevel;
        //     break;
        // case 'sync':
        //     audio.currentTime = message.position;
        //     break;
        default:
            console.log('Unknown message:', message);
    }
}

function sendJoinMessage() {
    if (webSocket && webSocket.readyState === WebSocket.OPEN) {
        const message = {
            type: 'join',
            partyId: partyId
        };
        webSocket.send(JSON.stringify(message));
    }
}

joinButton.addEventListener('click', () => {
    partyId = partyIdInput.value;
    connectWebSocket();
});

fileUpload.addEventListener('change', (event) => {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = (e) => {
        const songName = file.name; // Or extract name however you want
        const songUrl = e.target.result;

        songs.push({name: songName, url: songUrl});
        renderSongList();
    };

    reader.readAsDataURL(file);
});

function renderSongList() {
    songList.innerHTML = ''; // Clear existing list

    songs.forEach((song, index) => {
        const li = document.createElement('li');
        li.textContent = song.name;
        li.addEventListener('click', () => {
            currentSongIndex = index;
            playSong();
            highlightCurrentSong();
        });
        songList.appendChild(li);
    });
    highlightCurrentSong(); //Highlight initial song if any
}

// Play/Pause functionality
playButton.addEventListener('click', () => {
    if (!audio.src)
    {
        alert("Pick a song first!");
        return;
    }

    if (webSocket && webSocket.readyState === WebSocket.OPEN) {
        const message = {
            type: audio.paused ? 'play' : 'pause',
            partyId: partyId,
            songUrl: audio.currentSrc,
            seek: timeSlider.value,
        };
        webSocket.send(JSON.stringify(message));
    }
});

// Volume control
volumeSlider.addEventListener('input', () => {
    audio.volume = volumeSlider.value / 100;
});

// Volume button toggle mute
volumeButton.addEventListener('click', () => {
    if (audio.muted) {
        audio.muted = false;
        volumeButton.textContent = '🔊';
        volumeSlider.value = audio.volume * 100;
    } else {
        audio.muted = true;
        volumeButton.textContent = '🔇';
        volumeSlider.value = 0;
    }
});

// Time slider functionality
timeSlider.addEventListener('input', () => {
    if (webSocket && webSocket.readyState === WebSocket.OPEN) {
        const message = {
            type: 'seek',
            partyId: partyId,
            seek: timeSlider.value,
        };
        webSocket.send(JSON.stringify(message));
    }
});

// Update time slider and displays
audio.addEventListener('timeupdate', () => {
    timeSlider.value = (audio.currentTime / audio.duration) * 100;
    timePassedDisplay.textContent = formatTime(audio.currentTime);
});

// Update duration display
audio.addEventListener('loadedmetadata', () => {
    durationDisplay.textContent = formatTime(audio.duration);
});

// Function to format time in mm:ss
function formatTime(time) {
    if (isNaN(time)) {
        return '0:00';
    }
    const minutes = Math.floor(time / 60);
    const seconds = Math.floor(time % 60);
    return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
}

// // Previous and Next buttons
// previousButton.addEventListener('click', () => {
//     // Go to the beginning of the track
//     if (webSocket && webSocket.readyState === WebSocket.OPEN) {
//         const message = {
//             type: 'previous',
//             partyId: partyId
//         };
//         webSocket.send(JSON.stringify(message));
//     }
//     // audio.currentTime = 0;
//     // audio.play();
//     // playButton.textContent = '⏸';
//
//     // todo change the audio source here.
// });
//
// nextButton.addEventListener('click', () => {
//     // Example: Go to the end of the track
//     audio.currentTime = audio.duration;
//     audio.pause();
//     playButton.textContent = '▶';
//
//     // todo change the audio source here.
// });

// Update play button on audio end
audio.addEventListener('ended', () => {
    playButton.textContent = '▶';
    timeSlider.value = 0;
    timePassedDisplay.textContent = '0:00';
});
