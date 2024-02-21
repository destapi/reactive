import { createStore } from 'zustand/vanilla'

export const initialScrum = () => ({
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

export const initialProgress = () => ({
    history: [],
    started: false,
    userType: "organizer",
    vote: {
        screenName: "",
        choice: "",
    },
});

export const store = createStore((set) => ({
    scrum: initialScrum(),
    progress: initialProgress(),
    createScrum: (updated) => set((state) => {
        return ({...state, scrum: {...state.scrum, ...updated, member: {...updated.member}}})
    }),
    startScrum: (started) => set((state) => {
        return ({...state, progress: {...state.progress, started}})
    }),
    toggleType: (userType) => set((state) => {
        return ({...state, progress: {...state.progress, userType}})
    }),
}));

// scrum events
// export const createScrum = createEvent();
// export const joinScrum = createEvent();
// export const leaveScrum = createEvent();
// export const submitQuestion = createEvent();
// export const submitVote = createEvent();
// export const updateVote = createEvent();
//
// //progress events
// export const startScrum = createEvent();
// export const toggleType = createEvent();

// $scrum.on(createScrum, (scrum, updated) => {
//     return ({...scrum, ...updated, member: {...updated.member}})
// })
//     .on(joinScrum, (scrum, {scrumId, screenName, email, city, state}) => {
//         return ({...scrum, member: {scrumId, screenName, email, city, state }})
//     })
//     .on(leaveScrum, (scrum, {screenName}) => {
//         return ({...scrum, members: [...scrum.members.filter(m => m.screenName === screenName)]})
//     })
//     .on(submitQuestion, (scrum, {question}) => {
//         return ({...scrum, question})
//     })
//     .on(submitVote, (scrum, {scrumId, screenName, choice}) => {
//         return ({...scrum, voting: [...scrum.voting, {scrumId, screenName, choice}]})
//     })
//     .on(updateVote, (scrum, {screenName, to}) => {
//         return ({...scrum, voting: [...scrum.voting.map(v => {
//                 if (v.screenName === screenName) {
//                     return ({...v, choice: to})
//                 }
//                 return v;
//             })]
//         })
//     });
// $progress.on(startScrum, (progress, started) => {
//     return ({...progress, started})
// })
//     .on(toggleType, (progress, userType) => {
//         return ({...progress, userType})
//     });
