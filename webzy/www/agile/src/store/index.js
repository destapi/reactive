import {createStore} from 'zustand/vanilla'

export const initialScrum = () => {
    return ({
        scrumId: "",
        title: "",
        member: {
            screenName: "",
        },
        startTime: "",
        question: "",
        members: {},
        voting: [],
        choices: [],
    });
}

export const initialStatus = () => ({
    history: [],
    started: false,
    userType: "organizer",
    sticky: false,
    vote: {
        screenName: "",
        choice: "",
    },
});

export const emptyUser = () => ({
    "scrumId": "",
    "screenName": "",
    "email": "",
    "city": "",
    "state": ""
});

export const store = createStore((set) => ({
    scrum: initialScrum(),
    progress: initialStatus(),
    createScrum: (newScrum) => set((state) => {
        return ({...state, scrum: {...state.scrum, ...newScrum, member: {...newScrum.member}}})
    }),
    toggleSticky: (sticky) => set((state) => {
        return ({...state, progress: {...state.progress, sticky}})
    }),
    toggleType: (userType) => set((state) => {
        return ({...state, progress: {...state.progress, userType}})
    }),
    joinScrum: (userType, data) => set((state) => {
        if (userType === "organizer") {
            return ({
                ...state,
                scrum: {
                    ...state.scrum, members: {...state.scrum.members, [data.screenName]: data}
                },
                progress: {...state.progress, started: true}
            })
        }
        if (userType === "participant") {
            let {scrumId, title, choices, member} = data;
            return ({
                ...state,
                scrum: {
                    ...state.scrum, scrumId, title, choices, member,
                },
                progress: {...state.progress, started: true}
            })
        }
    }),
    leaveScrum: ({screenName}) => set((state) => {
        return ({
            ...state,
            scrum: {...state.scrum, members: [...state.scrum.members.filter(m => m.screenName === screenName)]}
        })
    }),
    submitQuestion: ({question}) => set((state) => {
        return ({...state, scrum: {...state.scrum, question}})
    }),
    submitVote: ({scrumId, screenName, choice}) => set((state) => {
        return ({
            ...state,
            scrum: {...state.scrum, voting: {...state.scrum.voting, [screenName]: {scrumId, screenName, choice}}}
        })
    }),
    updateVote: (scrum, {screenName, to}) => set((state) => {
        return ({
            ...scrum, voting: [...scrum.voting.map(v => {
                if (v.screenName === screenName) {
                    return ({...v, choice: to})
                }
                return v;
            })]
        })
    }),
}));

export const onToggleUserAction = (e) => {
    store.getState().toggleType(e.target.value);
}

export const onToggleStickyAction = (e) => {
    const state = store.getState();
    const sticky = e.target.value
    const userType = state.progress.userType;
    state.toggleSticky(sticky);
    if (sticky) {
        localStorage.setItem(userType, JSON.stringify(state));
    } else {
        localStorage.removeItem(userType);
    }
}

export const organizeNewScrum = async function (e) {
    e.preventDefault();

    const formData = new FormData(e.target);

    const scrumTitle = formData.get("scrumTitle");
    const screenName = formData.get("screenName");
    const voteChoices = formData.get("voteChoices");

    const newScrum = initialScrum();
    newScrum.title = scrumTitle;
    newScrum.member.screenName = screenName;
    newScrum.choices = voteChoices.split(/[\n,]/);

    await createScrumAction(newScrum, function ({scrumId, screenName}) {
        startEventSource(scrumId, screenName)
    }.bind(this))
        .catch(err => {
            console.log(err.message + "\nDo something more useful than just logging the error");
        });
}

export const startEventSource = (scrumId, screenName) => {

    const evtSource = new EventSource(`http://localhost:9080/join/?scrumId=${scrumId}&screenName=${screenName}`, {
        withCredentials: false
    });

    evtSource.onmessage = function (event) {
        const data = JSON.parse(event.data);
        console.log(data.vote);
    }

    evtSource.addEventListener('submitVote', event => {
        const data = JSON.parse(event.data);
        const state = store.getState();
        if (state.progress.userType !== "organizer") {
            state.submitVote(data)
        } else {
            store.setState({
                ...state, progress: {...state.progress, vote: data}
            })
        }
    });

    evtSource.addEventListener('joinScrum', event => {
        const member = JSON.parse(event.data);
        const state = store.getState();
        if (state.progress.userType === "organizer") {
            state.joinScrum(state.progress.userType, member)
        }
    });

    evtSource.addEventListener('leaveScrum', event => {
        const data = JSON.parse(event.data);
        const state = store.getState();
        if (state.progress.userType === "organizer") {
            state.leaveScrum(data);
        } else {
            store.setState({
                ...state, progress: {
                    ...state.progress, started: false
                }
            })
        }
    });

    evtSource.onerror = function (event) {
        // addMessageLine({data: "figure out why this error happened"})
        console.log(event)
    };
}

/*
* curl -X POST "http://localhost:9080/scrum/" -H "Content-Type: application/json" -d "{\"title\": \"first scrum\", \"member\": {\"screenName\": \"jimmy\"}, \"choices\": [\"1\",\"2\",\"3\",\"4\"]}"
* # sample response
* {
    "title": "first scrum",
    "member": {
        "scrumId": "b523d754-9229-47cf-a9b6-6a18763f893d",
        "screenName": "jimmy",
        "email": "jimmy@gmail.com",
        "city": "chester",
        "state": "WV"
    },
    "choices": [
        "1",
        "2",
        "3",
        "4"
    ],
    "scrumId": "b523d754-9229-47cf-a9b6-6a18763f893d",
    "startTime": "2024-02-22T22:17:41.0594979"
  }
* */
export const createScrumAction = async (newScrum, callback) => await fetch("http://localhost:9080/scrum/", {
    method: "POST",
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(newScrum)
}).then(resp => resp.json())
    .then(data => {
        store.getState().createScrum(data);
        const {scrumId, member: {screenName}} = data;
        callback({scrumId, screenName})
    });

/*
* curl -X POST "http://localhost:9080/join/?scrumId=15cbc0de-e010-4044-b05a-81649c6da0c6" -H "Content-Type: application/json" -d "{\"scrumId\": \"15cbc0de-e010-4044-b05a-81649c6da0c6\", \"screenName\": \"kadzo\"}"
* # sample response
* {
    "scrumId": "15cbc0de-e010-4044-b05a-81649c6da0c6",
    "title": "somesome",
    "choices": [
        "1",
        "2",
        "3",
        "4"
    ],
    "member": {
        "scrumId": "15cbc0de-e010-4044-b05a-81649c6da0c6",
        "screenName": "kadzo",
        "email": "jimmy@gmail.com",
        "city": "san pedro",
        "state": "CA"
    }
  }
* */
export const joinScrumAction = async (joinScrum, callback) => await fetch(`http://localhost:9080/join/?scrumId=${joinScrum.scrumId}`, {
    method: "POST",
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(joinScrum)
}).then(resp => resp.json())
    .then(data => {
        let {scrumId, title, choices, member} = data;
        store.getState().joinScrum("participant", {scrumId, title, choices, member});
        callback({scrumId, screenName: member.screenName});
    });

/*
* curl -X PUT "http://localhost:9080/scrum/?scrumId=15cbc0de-e010-4044-b05a-81649c6da0c6" -H "Content-Type: application/json" -d "{\"scrumId\": \"15cbc0de-e010-4044-b05a-81649c6da0c6\", \"screenName\": \"kadzo\"}"
* */
export const submitVoteAction = async (vote, callback) => await fetch(`http://localhost:9080/scrum/?scrumId${vote.scrumId}`, {
    method: "PUT",
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(vote)
}).then(resp => resp.json())
    .then(data => {
        console.log("submitted vote", data);
    });

/*
* curl -X PUT "http://localhost:9080/scrum/?scrumId=15cbc0de-e010-4044-b05a-81649c6da0c6" -H "Content-Type: application/json" -d "{\"scrumId\": \"15cbc0de-e010-4044-b05a-81649c6da0c6\", \"screenName\": \"kadzo\", \"question\": \"What is the time now?\"}"
* */
export const sendQuestionAction = async (question, callback) => await fetch(`/scrum/?scrumId=${scrumId}`, {
    method: "PUT",
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(question)
}).then(resp => resp.json())
    .then(data => {
        let {scrumId, member: screenName} = data;
        store.getState().submitQuestion(data);
        callback({scrumId, screenName});
    });