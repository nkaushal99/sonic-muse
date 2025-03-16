import {joinCancelBtn, joinConfirmBtn, joinInput, joinModal} from "./constants.js";

export function warmupJoinModal() {
    joinCancelBtn.addEventListener('click', () => {
        joinInput.classList.remove('error');
        joinModal.classList.remove('visible');
    });

    joinConfirmBtn.addEventListener('click', () => {
        const roomId = joinInput.value.trim();
        if (roomId) {
            joinRoom(roomId);
            joinInput.classList.remove('error');
            joinModal.classList.remove('visible');
        } else {
            joinInput.classList.add('error');
        }
    });

    joinInput.addEventListener('joinInput', () => {
        joinInput.classList.remove('error');
    });

    joinInput.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            joinConfirmBtn.click();
        }
    })

    joinInput.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
            joinCancelBtn.click();
        }
    })
}

export function showJoinRoomModal() {
    joinModal.classList.add('visible');
    setTimeout(() => {
        joinInput.focus();
    }, 300);
}

function joinRoom(roomId) {
    console.log('Joining room:', roomId);
    const notification = document.createElement('div');
    notification.classList.add('notification');
    notification.classList.add('primary');
    notification.textContent = `Joined room: ${roomId}`;
    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease-in';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}