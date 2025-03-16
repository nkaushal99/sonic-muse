// endpoint
const restApiUrl = 'https://9gk2hizebe.execute-api.ap-south-1.amazonaws.com/prod';
const websocketUrl = 'wss://kxmi2kpbfb.execute-api.ap-south-1.amazonaws.com/prod';

// auth
const signinBtn = document.getElementById('signin-btn');
const userMenu = document.getElementById('user-menu');
const logoutBtn = document.getElementById('logout-btn');

// main content
const mainContent = document.querySelector('.main-content')

// playlist
const playlist = document.querySelector('.playlist');
const playlistItems = document.querySelectorAll('.playlist-item');

// party
const createButton = document.getElementById('create-btn');
const joinButton = document.getElementById('join-btn');
const fileUpload = document.getElementById('song-file');

// player
const audio = document.getElementById('hidden-audio');
const playBtn = document.getElementById('play');
const prevBtn = document.getElementById('prev');
const nextBtn = document.getElementById('next');
const shuffleBtn = document.getElementById('shuffle');
const loopBtn = document.getElementById('loop');
const muteBtn = document.getElementById('mute');
const volumeSlider = document.getElementById('volume');
const progress = document.querySelector('.progress');
const currentTimeEl = document.querySelector('.current-time');
const durationEl = document.querySelector('.duration');
const pauseBtnSVG = '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 4h4v16H6zM14 4h4v16h-4z"/></svg>';
const playBtnSVG = '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="5 3 19 12 5 21 5 3"/></svg>';

export {
    restApiUrl,
    websocketUrl,
    signinBtn,
    userMenu,
    logoutBtn,
    mainContent,
    playlist,
    playlistItems,
    createButton,
    joinButton,
    fileUpload,
    audio,
    playBtn,
    prevBtn,
    nextBtn,
    shuffleBtn,
    loopBtn,
    muteBtn,
    volumeSlider,
    progress,
    currentTimeEl,
    durationEl,
    pauseBtnSVG,
    playBtnSVG,
};
