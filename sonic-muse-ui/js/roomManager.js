import {
    createButton,
    currentRoomIdEl,
    joinButton,
    joinCancelBtn,
    joinConfirmBtn,
    joinInput,
    joinModal,
    leaveButton,
    membersList, restApiUrl,
    roomInfo,
    roomPanel
} from "./constants.js";
import {getPartyId, setPartyId, initializeWebSocket, getWebSocket, getUserName} from "./globals.js";

class RoomManager {
    constructor() {
        this.setupEventListeners();
    }

    async syncMembersWithServer(roomId) {
        let partyDetails;

        try {
            partyDetails = await fetch(restApiUrl + `/party?id=${roomId}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                });
        } catch (uploadError) {
            console.error('Error fetching songs:', uploadError);
        }

        this.populateMembersList(partyDetails.hostId, partyDetails.attendees);
    }

    populateMembersList(hostId, members) {
        membersList.innerHTML = members
            .map(member => {
                member.isHost = member.id === hostId;
                return member;
            })
            .map(member => this.createMember(member))
            .join("");
    }

    createMember(member) {
        return `
            <div id="${member.id}" class="member ${member.isHost ? "host" : ""}">
                <div class="member-avatar primary">${member.name[0]}</div>
                <span class="member-name">${member.name}</span>
            </div>
        `;
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
        setPartyId(roomId);
        currentRoomIdEl.textContent = roomId;
        roomInfo.classList.add("active");
    }

    addMember(member) {
        if (member.name === getUserName())
            member.name += ' (You)';
        membersList.innerHTML += this.createMember(member);
    }

    removeMember(member, newHostId) {
        const memberElement = document.getElementById(member.id);
        memberElement.remove();

        // Check if the removed member was the host
        if (memberElement.classList.contains("host")) {
            this.changeHost(newHostId);
        }
    }

    changeHost(newHostId) {
        document.getElementById(newHostId).classList.add("host");
    }

    hideRoomControls() {
        joinButton.style.display = "none";
        createButton.style.display = "none";
    }

    displayRoomControls() {
        joinButton.style.display = "inline-block";
        createButton.style.display = "inline-block";
    }

    membersHeading()
    {
        const element = document.createElement("h3");
        element.textContent = "Members";
        return element;
    }

    createRoom(host = null, fromWebSocket = false) {
        if (host && fromWebSocket) {
            document.querySelector('.room-members').prepend(this.membersHeading());
            this.updateRoomInfo(getPartyId());
            this.hideRoomControls();
            this.addMember(host)
            return;
        }

        // if the user already part of a room, then can't create/join another room
        if (getPartyId())
            return;

        const msg = buildCreateRoomMsg();
        initializeWebSocket(msg);
    }

    async joinRoom(roomId) {
        // if the user already part of a room, then can't create/join another room
        if (getPartyId())
            return;

        document.querySelector('.room-members').prepend(this.membersHeading());
        await this.syncMembersWithServer(roomId);

        const msg = buildJoinRoomMsg(roomId);
        initializeWebSocket(msg);

        this.updateRoomInfo(roomId);
        this.hideRoomControls();

        this.hideJoinRoomModal();
        this.displayNotification(`Joined room: ${roomId}`);
    }

    leaveRoom() {
        setPartyId(null);
        const msg = buildLeaveRoomMsg();
        getWebSocket().send(msg);
        getWebSocket().close();
        roomInfo.classList.remove("active");
        membersList.innerHTML = "";
        this.displayRoomControls();
        document.querySelector('.room-members h3').remove();
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

    async handleJoinConfirm() {
        const roomId = joinInput.value.trim();
        if (roomId) {
            await this.joinRoom(roomId);
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

    setupEventListeners() {
        roomPanel.addEventListener("create", (event) => {
            setPartyId(event.detail.partyId);
            const host = {
                id: event.detail.hostId,
                name: getUserName() + ' (You)',
                isHost: true,
            }
            this.createRoom(host, true);
        })

        createButton.addEventListener("click", () => this.createRoom());
        joinButton.addEventListener("click", () => this.showJoinRoomModal());
        leaveButton.addEventListener("click", () => this.leaveRoom());

        joinCancelBtn.addEventListener("click", () => this.hideJoinRoomModal());
        joinConfirmBtn.addEventListener("click", async () => this.handleJoinConfirm());
        joinInput.addEventListener("input", () => this.handleJoinInput());
        joinInput.addEventListener("keydown", (event) => this.handleKeyDown(event));

        membersList.addEventListener("member_join", (event) => {
            this.addMember(event.detail.member);
        })
        membersList.addEventListener("member_leave", (event) => {
            this.removeMember(event.detail.member, event.detail.hostId);
        })
    }
}

function buildCreateRoomMsg() {
    return {
        type: 'create',
        member: {
            name: getUserName()
        }
    };
}

function buildJoinRoomMsg(partyIdValue) {
    return {
        type: 'join',
        member: {
            name: getUserName()
        },
        partyId: partyIdValue
    };
}

function buildLeaveRoomMsg() {
    return {
        type: 'leave'
    };
}

export function initializeRoomManager() {
    return new RoomManager();
}