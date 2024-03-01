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
        voting: {},
        choices: [],
    });
}

export const initialStatus = () => ({
    history: [],
    started: false,
    revealed: false,
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
                    ...state.scrum,
                    members: {...state.scrum.members, [data.screenName]: data},
                    voting: {...state.scrum.voting, [data.screenName]: {screenName: data.screenName, vote: ""}}
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
        let removed = Object.entries(state.scrum.members).reduce((acc, [key, value]) => {
            if (screenName !== key) {
                acc[key] = value;
            }
            return acc;
        }, {});

        return ({...state, scrum: {...state.scrum, members: removed}})
    }),
    leaveVoting: ({screenName}) => set((state) => {
        let removed = Object.entries(state.scrum.voting).reduce((acc, [key, value]) => {
            if (screenName !== key) {
                acc[key] = value;
            }
            return acc;
        }, {});

        return ({...state, scrum: {...state.scrum, voting: removed}})
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

store.subscribe((state) => {
    console.log("*****DEBUG ONLY****")
    console.log(state.scrum)
    console.log(state.progress)
})

export function onToggleType(e) {
    const state = store.getState();
    const targetType = e.target.value;
    console.log("from", state.progress.userType, "to", targetType);
    state.toggleType(targetType);
}

export function onToggleSticky(e) {
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

export async function organizeNewScrum(e) {
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

export async function joinScrumByScrumId(e) {
    e.preventDefault();

    const formData = new FormData(e.target);

    const scrumId = formData.get("scrumId");
    const screenName = formData.get("screenName");

    const joinScrum = emptyUser();
    joinScrum.scrumId = scrumId;
    joinScrum.screenName = screenName;

    joinScrumAction(joinScrum, function ({scrumId, screenName}) {
        startEventSource(scrumId, screenName)
    })
        .catch(err => {
            console.log(err.message + "\nDo something more useful than just logging the error");
        });
}

export async function onSubmitQuestion(e) {
    e.preventDefault();

    const formData = new FormData(e.target);

    const scrumId = formData.get("scrumId");
    const screenName = formData.get("screenName");
    const question = formData.get("question");

    sendQuestionAction({scrumId, screenName, question})
        .catch(err => {
            console.log(err.message + "\nDo something more useful than just logging the error");
        });
}

export async function onSelectChoice(choice) {
    const state = store.getState();
    const {scrum: {member}} = state;

    store.setState({
        ...state,
        progress: {
            ...state.progress,
            vote: {
                ...state.progress.vote,
                screenName: member.screenName, choice
            }
        }
    });

    submitVoteAction({
        scrumId: member.scrumId,
        screenName: member.screenName,
        choice
    })
        .catch(err => {
            console.log(err.message + "\nDo something more useful than just logging the error");
        });
}

export async function startEventSource(scrumId, screenName) {

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
        if (state.progress.userType === "organizer") {
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

    evtSource.addEventListener('leaveVoting', event => {
        const data = JSON.parse(event.data);
        const state = store.getState();
        if (state.progress.userType === "organizer") {
            state.leaveVoting(data);
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
export async function createScrumAction(newScrum, callback) {
    await fetch("http://localhost:9080/scrum/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(newScrum)
    }).then(resp => resp.json())
        .then(data => {
            store.getState().createScrum(data);
            const {scrumId, member: {screenName}} = data;
            callback({scrumId, screenName})
        });
}

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
export async function joinScrumAction(joinScrum) {
    await fetch(`http://localhost:9080/join/?scrumId=${joinScrum.scrumId}`, {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(joinScrum)
    }).then(resp => resp.json())
        .then(data => {
            let {scrumId, title, choices, member} = data;
            store.getState().joinScrum("participant", {scrumId, title, choices, member});
            callback({scrumId, screenName: member.screenName});
        });
}

/*
* curl -X PUT "http://localhost:9080/scrum/?action=vote&scrumId=a9d2f9c0-3ccd-4e85-a904-da6138fbb301" -H "Content-Type: application/json" -d "{\"scrumId\": \"a9d2f9c0-3ccd-4e85-a904-da6138fbb301\",  \"screenName\": \"simba\", \"choice\": \"2\"}"
* # sample response
* {
    "status": "ok"
  }
* */
export async function submitVoteAction(vote) {
    await fetch(`http://localhost:9080/scrum/?action=vote&scrumId=${vote.scrumId}`, {
        method: "PUT",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(vote)
    }).then(resp => resp.json())
        .then(data => {
            console.log("vote status", data);
        });
}

/*
* curl -X PUT "http://localhost:9080/scrum/?action=question&scrumId=622077e4-9696-4b29-866a-7a7d35dda2b6" -H "Content-Type: application/json" -d "{\"scrumId\": \"15cbc0de-e010-4044-b05a-81649c6da0c6\", \"scrumId\": \"622077e4-9696-4b29-866a-7a7d35dda2b6\", \"screenName\": \"simba\", \"question\": \"What is the time now?\"}"
*  # sample response
* {
    "status": "ok"
  }
* */
export async function sendQuestionAction({scrumId, screenName, question}) {
    await fetch(`http://localhost:9080/scrum/?action=question&scrumId=${scrumId}&screenName=${screenName}`, {
        method: "PUT",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({scrumId, screenName, question})
    }).then(resp => resp.json())
        .then(data => {
            console.log("question status", data);
        });
}

/*
*
* */
export async function removeMemberAction({scrumId, screenName}) {
    await fetch(`http://localhost:9080/join/?scrumId=${scrumId}&screenName=${screenName}`, {
        method: "DELETE",
        headers: {'Content-Type': 'application/json'},
    }).then(resp => resp.json())
        .then(data => {
            console.log("participant removed", data);
        });
}