export const initialScrum = () => ({
    scrumId: "",
    title: "",
    member: {
        screenName: "jimmy",
    },
    startTime: "",
    question: "",
    members: {},
    voting: {"kadzo": {"screenName": "kadzo", "choice": "1"}, "simba": {"screenName": "simba", "choice": "2"}},
    choices: ["1", "2", "3", "5", "8"],
});

export const initialStatus = () => ({
    history: [],
    started: true,
    userType: "organizer",
    vote: {
        screenName: "jimmy",
        choice: "8",
    },
});

export const scrumData = () => {
    let storage;
    if (localStorage) {
        storage = localStorage.getItem("organizer");
        if (!storage) {
            storage = localStorage.getItem("participant");
        }
    }
    return storage ? JSON.parse(storage) : ({scrum: initialScrum(), progress: initialStatus()})
}