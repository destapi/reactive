import {store} from "../../src/store";

const {getState, setState, subscribe, getInitialState} = store;

describe('test changes triggered in store', () => {

    test('initial state has empty fields', () => {
        const data = getState();
        expect(data).not.toBeUndefined();
        expect(data.scrum).toEqual({
            "choices": [],
            "member": {"screenName": ""},
            "members": {},
            "question": "",
            "scrumId": "",
            "startTime": "",
            "title": "",
            "voting": {}
        });
        expect(data.progress).toEqual({
            "history": [],
            "started": false,
            "userType": "organizer",
            "sticky": false,
            "vote": {"screenName": "", "choice": ""}
        });
    });

    test('createScrum event will generate a scrum object', () => {

    });
})

// let unsubscribe = store.subscribe(data => {
//     console.log(data)
// })