import {
    currentRoomIdEl,
    joinCancelBtn,
    joinConfirmBtn,
    joinInput,
    joinModal,
    roomInfo,
    createButton,
    joinButton,
    leaveButton,
    membersList
} from "./constants.js";

export class RoomManager {
    constructor() {
        this.currentRoom = null;
        this.isHost = false;

        this.setupEventListeners();
    }

    generateRoomId() {
        return Math.random().toString(36).substring(2, 8).toUpperCase();
    }

    displayNotification(message, type = "primary") {
        const notification = document.createElement("div");
        notification.classList.add("notification", type);
        notification.textContent = message;
        document.body.appendChild(notification);

        setTimeout(() => {
            notification.style.animation = "slideOut 0.3s ease-in";
            setTimeout(() => notification.remove(), 300);
        }, 3000);
    }

    updateRoomInfo(roomId) {
        currentRoomIdEl.textContent = roomId;
        roomInfo.classList.add("active");
    }

    updateMembersList(members) {
        membersList.innerHTML = members
            .map(
                (member) => `
            <div class="member ${member.isHost ? "host" : ""}">
                <div class="member-avatar primary">${member.name[0]}</div>
                <span class="member-name">${member.name}</span>
            </div>
        `
            )
            .join("");
    }

    joinRoom(roomId, asHost = false) {
        this.currentRoom = roomId;
        this.isHost = asHost;

        this.updateRoomInfo(roomId);
        this.updateMembersList([
            { name: "You", isHost: this.isHost },
            { name: "Alice", isHost: false },
            { name: "Bob", isHost: false },
        ]);
        this.hideJoinRoomModal();
        this.displayNotification(`Joined room: ${roomId}`);
    }

    leaveRoom() {
        this.currentRoom = null;
        this.isHost = false;
        roomInfo.classList.remove("active");
        membersList.innerHTML = "";
    }

    showJoinRoomModal() {
        joinModal.classList.add("visible");
        setTimeout(() => joinInput.focus(), 300);
    }

    hideJoinRoomModal() {
        joinModal.classList.remove("visible");
        joinInput.value = "";
        joinInput.classList.remove("error");
    }

    handleJoinConfirm() {
        const roomId = joinInput.value.trim().toUpperCase();
        if (roomId) {
            this.joinRoom(roomId);
        } else {
            joinInput.classList.add("error");
        }
    }

    handleJoinInput() {
        joinInput.classList.remove("error");
    }

    handleKeyDown(event) {
        if (event.key === "Enter") {
            joinConfirmBtn.click();
        } else if (event.key === "Escape") {
            joinCancelBtn.click();
        }
    }

    createRoom() {
        // player.join();
        this.joinRoom(this.generateRoomId(), true);
    }

    setupEventListeners() {
        createButton.addEventListener("click", () => this.createRoom());
        joinButton.addEventListener("click", () => this.showJoinRoomModal());
        leaveButton.addEventListener("click", () => this.leaveRoom());

        joinCancelBtn.addEventListener("click", () => this.hideJoinRoomModal());
        joinConfirmBtn.addEventListener("click", () => this.handleJoinConfirm());
        joinInput.addEventListener("input", () => this.handleJoinInput());
        joinInput.addEventListener("keydown", (event) => this.handleKeyDown(event));
    }
}