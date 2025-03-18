// import {restApiUrl} from './constants.js';
// import {initializePlayer} from './player-1.js';
//
// export async function handleUpload(file) {
//     if (file) {
//         const song = {
//             title: file.name,
//         };
//
//         try {
//             await uploadSong(file, song);
//             initializePlayer().syncSongListWithParty();
//
//         } catch (uploadError) {
//             console.error('Error uploading file:', uploadError);
//         }
//     } else {
//         alert("Please select a file!");
//     }
// }
//
// async function uploadSong(file, song) {
//     try {
//         const urlResponse = await fetch(restApiUrl + '/song/upload', {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json',
//             },
//             body: JSON.stringify(song),
//         });
//
//         if (!urlResponse.ok) {
//             throw new Error('Failed to get upload URL: ' + urlResponse.statusText);
//         }
//
//         const s3UploadUrl = await urlResponse.text();
//
//         const s3Response = await fetch(s3UploadUrl, {
//             method: 'PUT',
//             body: file,
//             headers: {
//                 'Content-Type': file.type,
//             },
//         });
//
//         if (!s3Response.ok) {
//             throw new Error('Failed to upload to S3: ' + s3Response.statusText);
//         }
//
//         console.log('Upload to S3 successful!');
//     } catch (error) {
//         console.error('Upload failed:', error);
//         alert('Upload failed. Please try again.');
//     }
// }