const audio = document.getElementById('hidden-audio');
const playButton = document.getElementById('play-button').querySelector('button');
const previousButton = document.getElementById('previous-button').querySelector('button');
const nextButton = document.getElementById('next-button').querySelector('button');
const volumeButton = document.getElementById('volume-btn');
const volumeSlider = document.getElementById('volume-slider');
const timeSlider = document.getElementById('time-slider');
const timePassedDisplay = document.getElementById('time-passed');
const durationDisplay = document.getElementById('duration');

const webSocket = window.myWebSocket;

webSocket.onmessage = (event) => {
    const message = JSON.parse(event.data);
    handleMessage(message);
};

function handleMessage(message) {
    switch (message.type) {
        case 'play':
            // audio.src = message.songUrl;
            audio.play();
            break;
        case 'pause':
            audio.pause();
            break;
        case 'seek':
            audio.currentTime = message.position;
            break;
        case 'volume':
            audio.volume = message.volumeLevel;
            break;
        case 'sync':
            audio.currentTime = message.position;
            break;
        default:
            console.log('Unknown message:', message);
    }
}

// Play/Pause functionality
playButton.addEventListener('click', () => {
    if (webSocket && webSocket.readyState === WebSocket.OPEN) {
        const message = {
            type: audio.paused ? 'play' : 'pause',
            partyId: partyId
            // ,
            // songUrl: songUrlInput.value
        };
        webSocket.send(JSON.stringify(message));
    }

    if (audio.paused) {
        // audio.src = songUrlInput.value;
        audio.play();
        playButton.textContent = '⏸';
    } else {
        audio.pause();
        playButton.textContent = '▶';
    }
});

// Volume control
volumeSlider.addEventListener('input', () => {
    if (webSocket && webSocket.readyState === WebSocket.OPEN) {
        const message = {
            type: 'volume',
            partyId: partyId,
            volumeLevel: volumeSlider.value
        };
        webSocket.send(JSON.stringify(message));
    }
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
            position: timeSlider.value,
        };
        webSocket.send(JSON.stringify(message));
    }
    audio.currentTime = (timeSlider.value / 100) * audio.duration;
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

// Previous and Next buttons
previousButton.addEventListener('click', () => {
    // Example: Go to the beginning of the track
    audio.currentTime = 0;
    audio.play();
    playButton.textContent = '⏸';

    // todo change the audio source here.
});

nextButton.addEventListener('click', () => {
    // Example: Go to the end of the track
    audio.currentTime = audio.duration;
    audio.pause();
    playButton.textContent = '▶';

    // todo change the audio source here.
});

// Update play button on audio end
audio.addEventListener('ended', () => {
    playButton.textContent = '▶';
    timeSlider.value = 0;
    timePassedDisplay.textContent = '0:00';
});
