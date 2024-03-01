const emptyChat = {
    "id": "",
    "chatName": "",
    "organizer": {
        "screenName": "",
        "email": "",
        "city": "",
        "state": ""
    },
    "privateChats": {},
    "participants": {},
    "chatMessages": {}
}

const emptyMessage = {
    "chatId": "",
    "from": "",
    "to": "",
    "message": "",
    "timeSent": ""
}

const emptyUser = {
    "screenName": "",
    "email": "",
    "city": "",
    "state": ""
}

let userType = "organizer"

function onToggleUserType(e) {
    userType = (e.value);
    document.getElementById("organizer").classList.toggle("hidden")
    document.getElementById("participant").classList.toggle("hidden")
}

const chatMessageTemplate = (sender, time, message, side) => `
<div class="chat-message ${side}">
    <label class="from">${sender}</label>
    <label class="time">${time}</label>
    <div class="content">${message}</div>
</div>
`;

const participantTemplate = (screenName, city, state) => `
<li class="participant-info">
    <div class="screenname">${screenName}</div>
    <div class="location">${city} ${state}</div>
</li>
`;

function newChatMessage({sender, time, message, side}) {
    let fragment = document.createRange().createContextualFragment(
        chatMessageTemplate(sender, time, message, side).trim());
    document.getElementById("messages").appendChild(fragment.firstChild);
}

function newParticipant({screenName, city, state}) {
    let fragment = document.createRange().createContextualFragment(
        participantTemplate(screenName, city, state).trim());
    document.getElementById("participants-list").appendChild(fragment.firstChild);
}

const joinChatForm = document.getElementById("chat-participant");

joinChatForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const chatId = e.target.chatId.value;
    const screenName = e.target.screenName.value;
    const joinChat = Object.assign({}, emptyUser);
    joinChat.chatId = chatId;
    joinChat.screenName = screenName;

    const response = await fetch(`/join/?chatId=${chatId}`, {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(joinChat)
    });

    response.json().then(data => {
        const root = document.getElementById("messages");
        while (root.firstChild) {
            root.removeChild(root.lastChild);
        }
        document.querySelector("#compose-message textarea").focus();
        startEventSource(chatId, screenName)
    });
});

const newChatForm = document.getElementById("chat-organizer");
newChatForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const chatName = e.target.chatName.value;
    const screenName = e.target.screenName.value;
    const newChat = Object.assign({}, emptyChat);
    newChat.chatName = chatName;
    newChat.organizer.screenName = screenName;

    const response = await fetch("/chat/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(newChat)
    });

    response.json().then(data => {
        const root = document.getElementById("messages");
        while (root.firstChild) {
            root.removeChild(root.lastChild);
        }
        document.querySelector("#compose-message textarea").focus();
        let {id: chatId, organizer: {screenName}} = data;
        document.querySelector("#chat-organizer").chatId.value = chatId;
        startEventSource(chatId, screenName)
    });
});

function startEventSource(chatId, screenName) {

    const evtSource = new EventSource(`/join/?chatId=${chatId}&screenName=${screenName}`, {
        withCredentials: false
    });

    evtSource.onmessage = (event) => {
        // addMessageLine(event);
        console.log(evtSource.readyState)
    };

    evtSource.addEventListener('joined', event => {
        const data = JSON.parse(event.data);
        let side = data.from === screenName ? "right" : "left";
        newChatMessage({...data, sender: data.from, time: data.timeSent, side})
    });

    evtSource.addEventListener('messages', event => {
        const data = JSON.parse(event.data);
        let side = data.from === screenName ? "right" : "left";
        newChatMessage({...data, sender: data.from, time: data.timeSent, side})
    });

    evtSource.addEventListener('participants', event => {
        const data = JSON.parse(event.data);
        newParticipant(data)
    });

    evtSource.addEventListener('delete', event => {
        // addMessageLine(event);
        console.log(evtSource.readyState)
    });

    evtSource.onerror = function (e) {
        // addMessageLine({data: "figure out why this error happened"})
        console.log(evtSource.readyState)
    };
}

async function sendMessage() {
    const sender = userType === "organizer" ?
        document.querySelector("#chat-organizer").screenName.value :
        document.querySelector("#chat-participant").screenName.value
    const chatId = userType === "organizer" ?
        document.querySelector("#chat-organizer").chatId.value :
        document.querySelector("#chat-participant").chatId.value
    const message = document.querySelector("#compose-message textarea").value
    const newMessage = Object.assign({}, emptyMessage)
    newMessage.chatId = chatId;
    newMessage.from = sender;
    newMessage.message = message;
    if (message) {
        const response = await fetch(`/chat/?chatId=${chatId}&screenName=${sender}`, {
            method: "PUT",
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(newMessage)
        });

        response.json().then(data => {
            console.log("message sent")
            document.querySelector("#compose-message textarea").focus();
        });
    }
}


