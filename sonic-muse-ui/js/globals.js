import {handleMessage} from "./util.js";
import {websocketUrl} from "./constants.js";
import {WebSocketWrapper} from "./websocket.js";

let userName = null;

let partyId = null;
let webSocket = null;
// let sync = false;

function initializeWebSocket(msg) {
    webSocket = new WebSocketWrapper(websocketUrl, handleMessage, msg);
}

function getUserName() {
    return userName;
}

function getPartyId() {
    return partyId;
}

function setPartyId(partyIdValue) {
    partyId = partyIdValue;
}

function getWebSocket() {
    return webSocket;
}

function generateRandomName() {
    const nameList = [
        'Time','Past','Future','Dev','Fly','Flying','Soar','Soaring','Power','Falling', 'Fall','Jump','Cliff',
        'Mountain','Rend','Red','Blue','Green','Yellow','Gold','Demon','Demonic','Panda','Cat', 'Kitty','Kitten',
        'Zero','Memory','Trooper','XX','Bandit', 'Fear','Light','Glow','Tread','Deep','Deeper','Deepest', 'Mine',
        'Your','Worst','Enemy','Hostile','Force','Video','Game','Donkey','Mule','Colt','Cult','Cultist','Magnum',
        'Gun','Assault','Recon','Trap','Trapper','Redeem','Code', 'Script','Writer','Near','Close','Open','Cube',
        'Circle','Geo','Genome','Germ','Spaz','Shot','Echo','Beta','Alpha','Gamma','Omega','Seal','Squid','Money',
        'Cash','Lord','King','Duke','Rest','Fire','Flame','Morrow','Break','Breaker','Numb', 'Ice','Cold','Rotten',
        'Sick','Sickly','Janitor','Camel','Rooster','Sand','Desert','Dessert','Hurdle','Racer','Eraser','Erase','Big',
        'Small','Short','Tall','Sith','Bounty','Hunter','Cracked','Broken', 'Sad','Happy','Joy','Joyful','Crimson',
        'Destiny','Deceit','Lies','Lie','Honest','Destined','Boxer','Hawk','Eagle','Hawker','Walker', 'Zombie',
        'Sarge','Capt','Captain','Punch','One','Two','Uno','Slice','Slash','Melt','Melted','Melting','Fell','Wolf',
        'Hound','Legacy','Sharp','Dead','Mew','Chuckle','Bubba','Bubble','Sandwich','Smasher','Extreme','Multi',
        'Universe','Ultimate','Death','Ready','Monkey','Elevator','Wrench','Grease','Head','Theme','Grand','Cool',
        'Kid','Boy','Girl','Vortex','Paradox'
    ];

    function pickName() {
        return nameList[Math.floor(Math.random() * nameList.length)];
    }

    return `${pickName()} ${pickName()}`;
}

function initializeGlobals()
{
    userName = generateRandomName();
    document.querySelector(".user-name").textContent = userName;
}

// function getSync()
// {
//     return sync;
// }
//
// function setSync(bool) {
//     sync = bool;
// }

export {
    initializeWebSocket,
    getUserName,
    getPartyId,
    setPartyId,
    getWebSocket,
    initializeGlobals,
    // getSync,
    // setSync,
};