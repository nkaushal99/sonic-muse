// // const fileUpload = document.getElementById('file-upload');
// // const songList = document.getElementById('song-list');
// // const audioPlayer = document.getElementById('audio-player');
// // const playButton = document.getElementById('play-button');
// // const pauseButton = document.getElementById('pause-button');
// //
// // let songs = []; // Array to store song objects {name, url}
// // let currentSongIndex = 0;
// //
// // fileUpload.addEventListener('change', (event) => {
// //     const file = event.target.files[0];
// //     const reader = new FileReader();
// //
// //     reader.onload = (e) => {
// //         const songName = file.name; // Or extract name however you want
// //         const songUrl = e.target.result;
// //
// //         songs.push({name: songName, url: songUrl});
// //         renderSongList();
// //     };
// //
// //     reader.readAsDataURL(file);
// // });
// //
// // function renderSongList() {
// //     songList.innerHTML = ''; // Clear existing list
// //
// //     songs.forEach((song, index) => {
// //         const li = document.createElement('li');
// //         li.textContent = song.name;
// //         li.addEventListener('click', () => {
// //             currentSongIndex = index;
// //             playSong();
// //             highlightCurrentSong();
// //         });
// //         songList.appendChild(li);
// //     });
// //     highlightCurrentSong(); //Highlight initial song if any
// // }
// //
// // function playSong() {
// //     if (songs.length === 0) return; // No songs to play
// //
// //     audioPlayer.src = songs[currentSongIndex].url;
// //     audioPlayer.play();
// // }
// //
// // function highlightCurrentSong() {
// //     const listItems = songList.querySelectorAll('li');
// //     listItems.forEach((item, index) => {
// //         if (index === currentSongIndex) {
// //             item.classList.add('selected');
// //         } else {
// //             item.classList.remove('selected');
// //         }
// //     });
// // }
// //
// // playButton.addEventListener('click', () => {
// //     if (songs.length > 0) {
// //         audioPlayer.play();
// //     }
// // });
// //
// // pauseButton.addEventListener('click', () => {
// //     if (songs.length > 0) {
// //         audioPlayer.pause();
// //     }
// // });
// //
// // // Optional: Event listener for when the current song finishes playing
// // audioPlayer.addEventListener('ended', () => {
// //     currentSongIndex = (currentSongIndex + 1) % songs.length; // Loop to next song
// //     playSong();
// //     highlightCurrentSong();
// // });
//
//
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//
// const groupIdInput = document.getElementById('groupId');
// const songUrlInput = document.getElementById('songUrl');
// const playButton = document.getElementById('playButton');
// const pauseButton = document.getElementById('pauseButton');
// const seekButton = document.getElementById('seekButton');
// const seekValueInput = document.getElementById('seekValue');
// const volumeButton = document.getElementById('volumeButton');
// const volumeValueInput = document.getElementById('volumeValue');
// const audioPlayer = document.getElementById('audioPlayer');
// const joinButton = document.getElementById('joinButton');
//
// let websocket;
// let groupId;
//
// joinButton.addEventListener('click', () => {
//     groupId = groupIdInput.value;
//     connectWebSocket();
// });
//
// function connectWebSocket() {
//     websocket = new WebSocket(`ws://localhost:8080/audio-stream`); // Adjust URL if needed
//
//     websocket.onopen = () => {
//         console.log('WebSocket connection established');
//         sendJoinMessage();
//     };
//
//     // websocket.onmessage = (event) => {
//     //     const message = JSON.parse(event.data);
//     //     handleMessage(message);
//     // };
//
//     websocket.onclose = () => {
//         console.log('WebSocket connection closed');
//     };
//
//     websocket.onerror = (error) => {
//         console.error('WebSocket error:', error);
//     };
// }
//
// function sendJoinMessage() {
//     if (websocket && websocket.readyState === WebSocket.OPEN) {
//         const message = {
//             type: 'join',
//             groupId: groupId
//         };
//         websocket.send(JSON.stringify(message));
//     }
// }
//
// playButton.addEventListener('click', () => {
//     if (websocket && websocket.readyState === WebSocket.OPEN) {
//         const message = {
//             type: 'play',
//             groupId: groupId,
//             songUrl: songUrlInput.value
//         };
//         websocket.send(JSON.stringify(message));
//         audioPlayer.src = songUrlInput.value;
//         audioPlayer.play();
//     }
// });
//
// pauseButton.addEventListener('click', () => {
//     if (websocket && websocket.readyState === WebSocket.OPEN) {
//         const message = {
//             type: 'pause',
//             groupId: groupId
//         };
//         websocket.send(JSON.stringify(message));
//         audioPlayer.pause();
//     }
// });
//
// seekButton.addEventListener('click', () => {
//     if (websocket && websocket.readyState === WebSocket.OPEN) {
//         const message = {
//             type: 'seek',
//             groupId: groupId,
//             position: parseFloat(seekValueInput.value)
//         };
//         websocket.send(JSON.stringify(message));
//         audioPlayer.currentTime = parseFloat(seekValueInput.value);
//     }
// });
//
// volumeButton.addEventListener('click', () => {
//     if (websocket && websocket.readyState === WebSocket.OPEN) {
//         const message = {
//             type: 'volume',
//             groupId: groupId,
//             volumeLevel: parseFloat(volumeValueInput.value)
//         };
//         websocket.send(JSON.stringify(message));
//         audioPlayer.volume = parseFloat(volumeValueInput.value);
//     }
// });
//
// function handleMessage(message) {
//     switch (message.type) {
//         case 'play':
//             audioPlayer.src = message.songUrl;
//             audioPlayer.play();
//             break;
//         case 'pause':
//             audioPlayer.pause();
//             break;
//         case 'seek':
//             audioPlayer.currentTime = message.position;
//             break;
//         case 'volume':
//             audioPlayer.volume = message.volumeLevel;
//             break;
//         case 'sync':
//             audioPlayer.currentTime = message.position;
//             break;
//         default:
//             console.log('Unknown message:', message);
//     }
// }