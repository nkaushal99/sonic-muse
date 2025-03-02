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

const baseUrl = 'http://localhost:8080';

document.addEventListener('DOMContentLoaded', async function() {
    await syncSongListWithServer();
});

async function syncSongListWithServer() {
    let songList;

    try {
        songList = await fetch(baseUrl + '/song')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            });
    } catch (uploadError) {
        console.error('Error fetching songs:', uploadError);
    }

    populateSongList(songList);

}

function populateSongList(songList) {
    const songListContainer = document.getElementById('song-list');

    if (!songListContainer) {
        console.error("Song list container not found.");
        return;
    }

    songListContainer.innerHTML = ''; // Clear previous content

    songList.forEach(song => {
        addSongToList(song);
    });
}

let webSocket;
let partyId;

function connectWebSocket() {
    webSocket = new WebSocket(`ws://localhost:8080/audio-stream`);

    webSocket.onopen = () => {
        console.log('WebSocket connection established');
        sendJoinMessage();
    };

    webSocket.onmessage = async (event) => {
        const message = JSON.parse(event.data);
        await handleMessage(message);
    };

    webSocket.onclose = () => {
        console.log('WebSocket connection closed');
    };

    webSocket.onerror = (error) => {
        console.error('WebSocket error:', error);
    };
}

async function handleMessage(message) {
    switch (message.type) {
        case 'sync_song_list':
            await syncSongListWithServer();
            break;
        case 'play':
            audio.src = message.url;
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

fileUpload.addEventListener('change', async function(event) {
    const file = event.target.files[0];
    if (file) {
        await handleUpload(file);
        await syncSongListWithParty();
    }
});

async function handleUpload(file) {
    if (file) {
        const song = {
            title: file.name,
        };

        try {
            await uploadSong(file, song);

        } catch (uploadError) {
            console.error('Error uploading file:', uploadError);
        }
    } else {
        alert("Please select a file!");
    }
}

async function uploadSong(file, song) {
    try {
        // 1. Get the pre-signed URL from the server.
        const urlResponse = await fetch(baseUrl + '/song/upload', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(song),
        });

        if (!urlResponse.ok) {
            throw new Error('Failed to get upload URL: ' + urlResponse.statusText);
        }

        const s3UploadUrl = await urlResponse.text();

        // 2. Upload the file to S3.
        const s3Response = await fetch(s3UploadUrl, {
            method: 'PUT',
            body: file,
            headers: {
                'Content-Type': file.type,
            },
        });

        if (!s3Response.ok) {
            throw new Error('Failed to upload to S3: ' + s3Response.statusText);
        }

        console.log('Upload to S3 successful!');
    } catch (error) {
        console.error('Upload failed:', error);
        alert('Upload failed. Please try again.');
    }
}

function syncSongListWithParty() {
    if (webSocket && webSocket.readyState === WebSocket.OPEN) {
        const message = {
            type: 'sync_song_list',
            partyId: partyId
        };
        webSocket.send(JSON.stringify(message));
    }
}

function addSongToList(song) {
    const songListContainer = document.getElementById('song-list');
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

    // Add a click listener to play the song
    songItem.addEventListener('click', () => {
        playNewSong(song);
    });

    songListContainer.appendChild(songItem);
}

function playNewSong(song) {
    if (webSocket && webSocket.readyState === WebSocket.OPEN) {
        const message = {
            type: 'play',
            partyId: partyId,
            url: song.url,
            seek: 0,
        };
        webSocket.send(JSON.stringify(message));
    }
}

// Play/Pause functionality
playButton.addEventListener('click', () => {
    if (!audio.currentSrc)
    {
        alert("Pick a song first!");
        return;
    }

    if (webSocket && webSocket.readyState === WebSocket.OPEN) {
        const message = {
            type: audio.paused ? 'play' : 'pause',
            partyId: partyId,
            url: audio.currentSrc,
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
