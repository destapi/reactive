import {store, initialScrum} from './store'
const { getState, setState, subscribe, getInitialState } = store;
import NavigationBar from "./navigation";

const emptyMessage = () => ({
    "scrumId": "",
    "from": "",
    "vote": "",
    "timeVoted": ""
});

const emptyUser = () => ({
    "scrumId": "",
    "screenName": "",
    "email": "",
    "city": "",
    "state": ""
});

const votingChoiceTemplate = (choice) => `
<article class="pure-u-1-4">
    <p class="choice" onclick="">${choice}</p>
</article>
`;

function newVotingChoice(choice) {
    let fragment = document.createRange().createContextualFragment(
        votingChoiceTemplate(choice).trim());
    document.getElementById("submitting-choice").appendChild(fragment.firstChild);
}

const participantTemplate = (screenName) => `
<article class="pure-u-1-4">
    <p class="controls"><i class="fa fa-times-circle" aria-hidden="true"></i></p>
    <p class="vote">${screenName}</p>
</article>
`;

function newParticipant({screenName, city, state}) {
    let fragment = document.createRange().createContextualFragment(
        participantTemplate(screenName, city, state).trim());
    document.getElementById("voting-progress").appendChild(fragment.firstChild);
}

async function sendVotingPrompt() {
    const {scrumId, screenName, choices} = state.organizer;
    let question = document.getElementById("vote-form").querySelector("textarea").value;
    const response = await fetch(`/scrum/?scrumId=${scrumId}`, {
        method: "PUT",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({scrumId, screenName, question, choices})
    });

    response.json().then(data => {
        console.log(data.status);
    }).catch(err => {
        console.log(err.message + "\nDo something more useful than just logging the error");
    });
}

async function resetVotingPrompt() {

}

async function joinScrumByScrumId(e) {
    e.preventDefault();

    const formData = new FormData(e.target);

    const scrumId = formData.get("scrumId");
    const screenName = formData.get("screenName");

    const joinChat = emptyUser();
    joinChat.scrumId = scrumId;
    joinChat.screenName = screenName;

    const response = await fetch(`http://localhost:9080/join/?scrumId=${scrumId}`, {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(joinChat)
    });

    response.json().then(data => {
        let {screenName} = data;
        const state = getState();
        setState({...state, scrum: {...state.scrum, member: {...state.scrum.member, screenName}}})
        startEventSource(scrumId, screenName)
    }).catch(err => {
        console.log(err.message + "\nDo something more useful than just logging the error");
    });
}

async function organizeNewScrum(e) {
    e.preventDefault();

    const formData = new FormData(e.target);

    const scrumTitle = formData.get("scrumTitle");
    const screenName = formData.get("screenName");
    const voteChoices = formData.get("voteChoices");

    const newScrum = initialScrum();
    newScrum.title = scrumTitle;
    newScrum.member.screenName = screenName;
    newScrum.choices = voteChoices;

    const response = await fetch("http://localhost:9080/scrum/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(newScrum)
    });

    response.json().then(data => {
        let {scrumId, member: {screenName}} = data;
        setState({...getState(), scrum: data})
        startEventSource(scrumId, screenName)
    }).catch(err => {
        console.log(err.message + "\nDo something more useful than just logging the error");
    });
}

function startEventSource(scrumId, screenName) {

    const evtSource = new EventSource(`http://localhost:9080/join/?scrumId=${scrumId}&screenName=${screenName}`, {
        withCredentials: false
    });

    evtSource.onmessage = function(event) {
        const data = JSON.parse(event.data);
        console.log(data.vote);
        const {progress} = getState();
        if (progress.userType === "organizer") {
            if (!progress.started) {
                render();
            } else {
                renderVotingProgress();
            }
        } else {
            renderParticipantPage();
        }
    }

    evtSource.addEventListener('submitVote', event => {
        const data = JSON.parse(event.data);
        const {state} = getState();
        if (state.progress.userType !== "organizer") {
            const {choices} = data;

            emptyNode("#submitting-choice");
            for (let choice of choices) {
                newVotingChoice(choice);
            }
        }
    });

    evtSource.addEventListener('joinScrum', event => {
        const data = JSON.parse(event.data);
        const state = getState();
        if (state.progress.userType === "organizer") {
            setState({...state,
                scrum: {...state.scrum,
                    members: {...state.scrum.members, ...data}},
                progress: {...state.progress, started: true}
            });

            newParticipant(data)
        }
    });

    evtSource.addEventListener('leaveScrum', event => {
        // addMessageLine(event);
        console.log(event)
    });

    evtSource.onerror = function (event) {
        // addMessageLine({data: "figure out why this error happened"})
        console.log(event)
    };
}

// async function sendMessage() {
//     const sender = userType === "organizer" ?
//         document.querySelector("#chat-organizer").screenName.value :
//         document.querySelector("#chat-participant").screenName.value
//     const scrumId = userType === "organizer" ?
//         document.querySelector("#chat-organizer").scrumId.value :
//         document.querySelector("#chat-participant").scrumId.value
//     const message = document.querySelector("#compose-message textarea").value
//     const newMessage = Object.assign({}, emptyMessage)
//     newMessage.scrumId = scrumId;
//     newMessage.from = sender;
//     newMessage.message = message;
//     if (message) {
//         const response = await fetch(`/chat/?scrumId=${scrumId}&screenName=${sender}`, {
//             method: "PUT",
//             headers: {'Content-Type': 'application/json'},
//             body: JSON.stringify(newMessage)
//         });
//
//         response.json().then(data => {
//             console.log("message sent")
//             document.querySelector("#compose-message textarea").focus();
//         });
//     }
// }

function toggleUserType(e) {
    getState().toggleType(e.target.value);
    render();
}

// function renderComponent(definition, props, parent){
//     const template = definition(props);
//     const fragment = template.content.cloneNode(true);
//     parent.appendChild(fragment);
// }

function renderNavigationBar() {

    const template = document.getElementById("navigation-template");
    const fragment = template.content.cloneNode(true);

    const participantNode = fragment.getElementById("participant");
    participantNode.addEventListener('click', toggleUserType);

    const organizerNode = fragment.getElementById("organizer");
    organizerNode.addEventListener('click', toggleUserType);

    if (getState().progress.userType === "participant") {
        participantNode.checked = true
    }
    if (getState().progress.userType === "organizer") {
        organizerNode.checked = true
    }

    document.getElementById("root").appendChild(fragment);
}

function renderParticipantForm() {
    const template = document.getElementById("participant-form-template");
    const fragment = template.content.cloneNode(true);

    const organizerFormNode = fragment.querySelector(".participant-form");
    organizerFormNode.addEventListener('submit', joinScrumByScrumId);

    if (getState().progress.userType !== "participant") {
        fragment.children[0].classList.add("hidden")
    }
    document.getElementById("root").appendChild(fragment);
}

function renderOrganizerForm() {
    const template = document.getElementById("organizer-form-template");
    const fragment = template.content.cloneNode(true);

    const organizerFormNode = fragment.querySelector(".organizer-form");
    organizerFormNode.addEventListener('submit', organizeNewScrum);

    if (getState().progress.userType !== "organizer") {
        fragment.children[0].classList.add("hidden")
    }
    document.getElementById("root").appendChild(fragment);
}

function renderVotingForm() {
    const template = document.getElementById("vote-form-template");
    const fragment = template.content.cloneNode(true);

    const sendButtonNode = fragment.querySelector(".send");
    sendButtonNode.addEventListener('submit', sendVotingPrompt);

    const resetButtonNode = fragment.querySelector(".reset");
    resetButtonNode.addEventListener('submit', resetVotingPrompt);

    document.getElementById("root").appendChild(fragment);
}

function renderVotingProgress() {
    const {scrum, progress} = getState();
    if (progress.userType === "organizer" && scrum.members.length === 0) {
        const template = document.getElementById("voting-progress-template");
        const fragment = template.content.cloneNode(true);
        document.getElementById("root").appendChild(fragment);
    } else {
        emptyNode("#voting-progress");

        for (let [_,{screenName}] in scrum.members) {
            newParticipant({screenName})
        }
    }
}

function renderSubmittingChoice() {
    const template = document.getElementById("submitting-choice-template");
    const fragment = template.content.cloneNode(true);
    document.getElementById("root").appendChild(fragment);
}

function emptyNode(selector) {
    const root = document.querySelector(selector);
    if(root) {
        while (root.firstChild) {
            root.removeChild(root.lastChild);
        }
    }
}

function render() {
    emptyNode("#root")

    renderNavigationBar();

    const {scrum: {member: {screenName}}, progress: {userType}} = getState();

    if (!screenName && userType === 'organizer') {
        renderOrganizerForm();
    }

    if (screenName && userType === 'organizer') {
        renderVotingForm();
        renderVotingProgress();
    }

    if (userType === 'participant') {
        renderParticipantForm();
    }
}

function renderParticipantPage() {
    const root = document.getElementById("root");
    while (root.firstChild) {
        root.removeChild(root.lastChild);
    }

    renderNavigationBar();

    renderSubmittingChoice();
}

render();