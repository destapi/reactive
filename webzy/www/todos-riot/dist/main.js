/******/ (() => { // webpackBootstrap
/******/ 	"use strict";
/******/ 	var __webpack_modules__ = ({

/***/ "./src/store.js":
/*!**********************!*\
  !*** ./src/store.js ***!
  \**********************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   store: () => (/* binding */ store)
/* harmony export */ });
/* harmony import */ var zustand_vanilla__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! zustand/vanilla */ "./node_modules/zustand/esm/vanilla.mjs");


const initialScrum = () => ({
    scrumId: "",
    title: "",
    member: {
        screenName: "",
    },
    startTime: "",
    question: "",
    members: [],
    choices: [],
});

const initialProgress = () => ({
    history: [],
    started: false,
    userType: "organizer",
    vote: {
        screenName: "",
        choice: "",
    },
});

const store = (0,zustand_vanilla__WEBPACK_IMPORTED_MODULE_0__.createStore)((set) => ({
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


/***/ }),

/***/ "./node_modules/zustand/esm/vanilla.mjs":
/*!**********************************************!*\
  !*** ./node_modules/zustand/esm/vanilla.mjs ***!
  \**********************************************/
/***/ ((__unused_webpack___webpack_module__, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   createStore: () => (/* binding */ createStore),
/* harmony export */   "default": () => (/* binding */ vanilla)
/* harmony export */ });
const createStoreImpl = (createState) => {
  let state;
  const listeners = /* @__PURE__ */ new Set();
  const setState = (partial, replace) => {
    const nextState = typeof partial === "function" ? partial(state) : partial;
    if (!Object.is(nextState, state)) {
      const previousState = state;
      state = (replace != null ? replace : typeof nextState !== "object" || nextState === null) ? nextState : Object.assign({}, state, nextState);
      listeners.forEach((listener) => listener(state, previousState));
    }
  };
  const getState = () => state;
  const getInitialState = () => initialState;
  const subscribe = (listener) => {
    listeners.add(listener);
    return () => listeners.delete(listener);
  };
  const destroy = () => {
    if (( false ? 0 : void 0) !== "production") {
      console.warn(
        "[DEPRECATED] The `destroy` method will be unsupported in a future version. Instead use unsubscribe function returned by subscribe. Everything will be garbage-collected if store is garbage-collected."
      );
    }
    listeners.clear();
  };
  const api = { setState, getState, getInitialState, subscribe, destroy };
  const initialState = state = createState(setState, getState, api);
  return api;
};
const createStore = (createState) => createState ? createStoreImpl(createState) : createStoreImpl;
var vanilla = (createState) => {
  if (( false ? 0 : void 0) !== "production") {
    console.warn(
      "[DEPRECATED] Default export is deprecated. Instead use import { createStore } from 'zustand/vanilla'."
    );
  }
  return createStore(createState);
};




/***/ })

/******/ 	});
/************************************************************************/
/******/ 	// The module cache
/******/ 	var __webpack_module_cache__ = {};
/******/ 	
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/ 		// Check if module is in cache
/******/ 		var cachedModule = __webpack_module_cache__[moduleId];
/******/ 		if (cachedModule !== undefined) {
/******/ 			return cachedModule.exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = __webpack_module_cache__[moduleId] = {
/******/ 			// no module.id needed
/******/ 			// no module.loaded needed
/******/ 			exports: {}
/******/ 		};
/******/ 	
/******/ 		// Execute the module function
/******/ 		__webpack_modules__[moduleId](module, module.exports, __webpack_require__);
/******/ 	
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/ 	
/************************************************************************/
/******/ 	/* webpack/runtime/define property getters */
/******/ 	(() => {
/******/ 		// define getter functions for harmony exports
/******/ 		__webpack_require__.d = (exports, definition) => {
/******/ 			for(var key in definition) {
/******/ 				if(__webpack_require__.o(definition, key) && !__webpack_require__.o(exports, key)) {
/******/ 					Object.defineProperty(exports, key, { enumerable: true, get: definition[key] });
/******/ 				}
/******/ 			}
/******/ 		};
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/hasOwnProperty shorthand */
/******/ 	(() => {
/******/ 		__webpack_require__.o = (obj, prop) => (Object.prototype.hasOwnProperty.call(obj, prop))
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/make namespace object */
/******/ 	(() => {
/******/ 		// define __esModule on exports
/******/ 		__webpack_require__.r = (exports) => {
/******/ 			if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 				Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 			}
/******/ 			Object.defineProperty(exports, '__esModule', { value: true });
/******/ 		};
/******/ 	})();
/******/ 	
/************************************************************************/
var __webpack_exports__ = {};
// This entry need to be wrapped in an IIFE because it need to be isolated against other modules in the chunk.
(() => {
/*!**********************!*\
  !*** ./src/index.js ***!
  \**********************/
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _store__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./store */ "./src/store.js");

const { getState, setState, subscribe, getInitialState } = _store__WEBPACK_IMPORTED_MODULE_0__.store;

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

const emptyScrum = () => ({
    scrumId: "",
    title: "",
    userType: "organizer",
    member: {
        screenName: "",
    },
    startTime: "",
    question: "",
    members: [],
    choices: [],
});

// $progress..watch(() => {
//     render();
// })



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

    const response = await fetch(`/join/?scrumId=${scrumId}`, {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(joinChat)
    });

    response.json().then(data => {
        let {screenName} = data;

        state.participant.screenName = screenName;
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

    const newScrum = emptyScrum();
    newScrum.scrumTitle = scrumTitle;
    newScrum.scrumMaster.screenName = screenName;
    newScrum.voteChoices = voteChoices;

    const response = await fetch("/scrum/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(newScrum)
    });

    response.json().then(data => {
        let state = {}
        let {scrumId, scrumTitle, scrumMaster: {screenName}, voteChoices} = data;
        state.organizer.scrumId = scrumId;
        state.organizer.scrumTitle = scrumTitle;
        state.organizer.screenName = screenName;
        state.organizer.choices = voteChoices;
        startEventSource(scrumId, screenName)
    }).catch(err => {
        console.log(err.message + "\nDo something more useful than just logging the error");
    });
}

function startEventSource(scrumId, screenName) {

    const evtSource = new EventSource(`/join/?scrumId=${scrumId}&screenName=${screenName}`, {
        withCredentials: false
    });

    evtSource.onmessage = (event) => {
        // addMessageLine(event);
        console.log(evtSource.readyState)
    };

    evtSource.addEventListener('joined', event => {
        const data = JSON.parse(event.data);
        console.log(data.vote);
        if (state.userType === "organizer") {
            if (!state.playing) {
                render();
            } else {
                renderVotingProgress();
            }
        } else {
            renderParticipantPage();
        }
    });

    evtSource.addEventListener('voting', event => {
        const data = JSON.parse(event.data);
        if (state.userType !== "organizer") {
            const {choices} = data;

            emptyNode("#submitting-choice");
            for (let choice of choices) {
                newVotingChoice(choice);
            }
        }
    });

    evtSource.addEventListener('participants', event => {
        const data = JSON.parse(event.data);
        if (state.userType === "organizer") {
            state.playing = true;
            state.organizer.participants.push(data)
            newParticipant(data)
        }
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

function renderNavigationBar() {
    function toggleUserType(e) {
        getState().toggleType(e.value);
        render();
    }
    const template = document.getElementById("navigation-template");
    const fragment = template.content.cloneNode(true);
    if (getState().progress.userType === "participant") {
        fragment.getElementById("participant").checked = true
        fragment.getElementById("participant").addEventListener('click', toggleUserType);
    }
    if (getState().progress.userType === "organizer") {
        fragment.getElementById("organizer").checked = true
        fragment.getElementById("organizer").addEventListener('click', toggleUserType);
    }

    document.getElementById("root").appendChild(fragment);
}

function renderParticipantForm() {
    const template = document.getElementById("participant-form-template");
    const fragment = template.content.cloneNode(true);
    if (getState().userType !== "participant") {
        fragment.children[0].classList.add("hidden")
    }
    document.getElementById("root").appendChild(fragment);
}

function renderOrganizerForm() {
    const template = document.getElementById("organizer-form-template");
    const fragment = template.content.cloneNode(true);
    if (getState().userType !== "organizer") {
        fragment.children[0].classList.add("hidden")
    }
    document.getElementById("root").appendChild(fragment);
}

function renderVotingForm() {
    const template = document.getElementById("vote-form-template");
    const fragment = template.content.cloneNode(true);
    document.getElementById("root").appendChild(fragment);
}

function renderVotingProgress() {
    if (getState().userType === "organizer" && getState().organizer.participants.length === 0) {
        const template = document.getElementById("voting-progress-template");
        const fragment = template.content.cloneNode(true);
        document.getElementById("root").appendChild(fragment);
    } else {
        emptyNode("#voting-progress");

        for (let {screenName} of state.organizer.participants) {
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
    while (root.firstChild) {
        root.removeChild(root.lastChild);
    }
}

function render() {
    emptyNode("#root")

    renderNavigationBar();

    const {
        userType,
        organizer: {screenName: organizer},
        participant: {
            screenName: participant
        }
    } = getState();

    if (!organizer && userType === 'organizer') {
        renderOrganizerForm();
    }

    if (organizer && userType === 'organizer') {
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
})();

/******/ })()
;
//# sourceMappingURL=data:application/json;charset=utf-8;base64,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YWdpbGUvLi9ub2RlX21vZHVsZXMvenVzdGFuZC9lc20vdmFuaWxsYS5tanMiLCJ3ZWJwYWNrOi8vYWdpbGUvd2VicGFjay9ib290c3RyYXAiLCJ3ZWJwYWNrOi8vYWdpbGUvd2VicGFjay9ydW50aW1lL2RlZmluZSBwcm9wZXJ0eSBnZXR0ZXJzIiwid2VicGFjazovL2FnaWxlL3dlYnBhY2svcnVudGltZS9oYXNPd25Qcm9wZXJ0eSBzaG9ydGhhbmQiLCJ3ZWJwYWNrOi8vYWdpbGUvd2VicGFjay9ydW50aW1lL21ha2UgbmFtZXNwYWNlIG9iamVjdCIsIndlYnBhY2s6Ly9hZ2lsZS8uL3NyYy9pbmRleC5qcyJdLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBjcmVhdGVTdG9yZSB9IGZyb20gJ3p1c3RhbmQvdmFuaWxsYSdcclxuXHJcbmNvbnN0IGluaXRpYWxTY3J1bSA9ICgpID0+ICh7XHJcbiAgICBzY3J1bUlkOiBcIlwiLFxyXG4gICAgdGl0bGU6IFwiXCIsXHJcbiAgICBtZW1iZXI6IHtcclxuICAgICAgICBzY3JlZW5OYW1lOiBcIlwiLFxyXG4gICAgfSxcclxuICAgIHN0YXJ0VGltZTogXCJcIixcclxuICAgIHF1ZXN0aW9uOiBcIlwiLFxyXG4gICAgbWVtYmVyczogW10sXHJcbiAgICBjaG9pY2VzOiBbXSxcclxufSk7XHJcblxyXG5jb25zdCBpbml0aWFsUHJvZ3Jlc3MgPSAoKSA9PiAoe1xyXG4gICAgaGlzdG9yeTogW10sXHJcbiAgICBzdGFydGVkOiBmYWxzZSxcclxuICAgIHVzZXJUeXBlOiBcIm9yZ2FuaXplclwiLFxyXG4gICAgdm90ZToge1xyXG4gICAgICAgIHNjcmVlbk5hbWU6IFwiXCIsXHJcbiAgICAgICAgY2hvaWNlOiBcIlwiLFxyXG4gICAgfSxcclxufSk7XHJcblxyXG5leHBvcnQgY29uc3Qgc3RvcmUgPSBjcmVhdGVTdG9yZSgoc2V0KSA9PiAoe1xyXG4gICAgc2NydW06IGluaXRpYWxTY3J1bSgpLFxyXG4gICAgcHJvZ3Jlc3M6IGluaXRpYWxQcm9ncmVzcygpLFxyXG4gICAgY3JlYXRlU2NydW06ICh1cGRhdGVkKSA9PiBzZXQoKHN0YXRlKSA9PiB7XHJcbiAgICAgICAgcmV0dXJuICh7Li4uc3RhdGUsIHNjcnVtOiB7Li4uc3RhdGUuc2NydW0sIC4uLnVwZGF0ZWQsIG1lbWJlcjogey4uLnVwZGF0ZWQubWVtYmVyfX19KVxyXG4gICAgfSksXHJcbiAgICBzdGFydFNjcnVtOiAoc3RhcnRlZCkgPT4gc2V0KChzdGF0ZSkgPT4ge1xyXG4gICAgICAgIHJldHVybiAoey4uLnN0YXRlLCBwcm9ncmVzczogey4uLnN0YXRlLnByb2dyZXNzLCBzdGFydGVkfX0pXHJcbiAgICB9KSxcclxuICAgIHRvZ2dsZVR5cGU6ICh1c2VyVHlwZSkgPT4gc2V0KChzdGF0ZSkgPT4ge1xyXG4gICAgICAgIHJldHVybiAoey4uLnN0YXRlLCBwcm9ncmVzczogey4uLnN0YXRlLnByb2dyZXNzLCB1c2VyVHlwZX19KVxyXG4gICAgfSksXHJcbn0pKTtcclxuXHJcbi8vIHNjcnVtIGV2ZW50c1xyXG4vLyBleHBvcnQgY29uc3QgY3JlYXRlU2NydW0gPSBjcmVhdGVFdmVudCgpO1xyXG4vLyBleHBvcnQgY29uc3Qgam9pblNjcnVtID0gY3JlYXRlRXZlbnQoKTtcclxuLy8gZXhwb3J0IGNvbnN0IGxlYXZlU2NydW0gPSBjcmVhdGVFdmVudCgpO1xyXG4vLyBleHBvcnQgY29uc3Qgc3VibWl0UXVlc3Rpb24gPSBjcmVhdGVFdmVudCgpO1xyXG4vLyBleHBvcnQgY29uc3Qgc3VibWl0Vm90ZSA9IGNyZWF0ZUV2ZW50KCk7XHJcbi8vIGV4cG9ydCBjb25zdCB1cGRhdGVWb3RlID0gY3JlYXRlRXZlbnQoKTtcclxuLy9cclxuLy8gLy9wcm9ncmVzcyBldmVudHNcclxuLy8gZXhwb3J0IGNvbnN0IHN0YXJ0U2NydW0gPSBjcmVhdGVFdmVudCgpO1xyXG4vLyBleHBvcnQgY29uc3QgdG9nZ2xlVHlwZSA9IGNyZWF0ZUV2ZW50KCk7XHJcblxyXG4vLyAkc2NydW0ub24oY3JlYXRlU2NydW0sIChzY3J1bSwgdXBkYXRlZCkgPT4ge1xyXG4vLyAgICAgcmV0dXJuICh7Li4uc2NydW0sIC4uLnVwZGF0ZWQsIG1lbWJlcjogey4uLnVwZGF0ZWQubWVtYmVyfX0pXHJcbi8vIH0pXHJcbi8vICAgICAub24oam9pblNjcnVtLCAoc2NydW0sIHtzY3J1bUlkLCBzY3JlZW5OYW1lLCBlbWFpbCwgY2l0eSwgc3RhdGV9KSA9PiB7XHJcbi8vICAgICAgICAgcmV0dXJuICh7Li4uc2NydW0sIG1lbWJlcjoge3NjcnVtSWQsIHNjcmVlbk5hbWUsIGVtYWlsLCBjaXR5LCBzdGF0ZSB9fSlcclxuLy8gICAgIH0pXHJcbi8vICAgICAub24obGVhdmVTY3J1bSwgKHNjcnVtLCB7c2NyZWVuTmFtZX0pID0+IHtcclxuLy8gICAgICAgICByZXR1cm4gKHsuLi5zY3J1bSwgbWVtYmVyczogWy4uLnNjcnVtLm1lbWJlcnMuZmlsdGVyKG0gPT4gbS5zY3JlZW5OYW1lID09PSBzY3JlZW5OYW1lKV19KVxyXG4vLyAgICAgfSlcclxuLy8gICAgIC5vbihzdWJtaXRRdWVzdGlvbiwgKHNjcnVtLCB7cXVlc3Rpb259KSA9PiB7XHJcbi8vICAgICAgICAgcmV0dXJuICh7Li4uc2NydW0sIHF1ZXN0aW9ufSlcclxuLy8gICAgIH0pXHJcbi8vICAgICAub24oc3VibWl0Vm90ZSwgKHNjcnVtLCB7c2NydW1JZCwgc2NyZWVuTmFtZSwgY2hvaWNlfSkgPT4ge1xyXG4vLyAgICAgICAgIHJldHVybiAoey4uLnNjcnVtLCB2b3Rpbmc6IFsuLi5zY3J1bS52b3RpbmcsIHtzY3J1bUlkLCBzY3JlZW5OYW1lLCBjaG9pY2V9XX0pXHJcbi8vICAgICB9KVxyXG4vLyAgICAgLm9uKHVwZGF0ZVZvdGUsIChzY3J1bSwge3NjcmVlbk5hbWUsIHRvfSkgPT4ge1xyXG4vLyAgICAgICAgIHJldHVybiAoey4uLnNjcnVtLCB2b3Rpbmc6IFsuLi5zY3J1bS52b3RpbmcubWFwKHYgPT4ge1xyXG4vLyAgICAgICAgICAgICAgICAgaWYgKHYuc2NyZWVuTmFtZSA9PT0gc2NyZWVuTmFtZSkge1xyXG4vLyAgICAgICAgICAgICAgICAgICAgIHJldHVybiAoey4uLnYsIGNob2ljZTogdG99KVxyXG4vLyAgICAgICAgICAgICAgICAgfVxyXG4vLyAgICAgICAgICAgICAgICAgcmV0dXJuIHY7XHJcbi8vICAgICAgICAgICAgIH0pXVxyXG4vLyAgICAgICAgIH0pXHJcbi8vICAgICB9KTtcclxuLy8gJHByb2dyZXNzLm9uKHN0YXJ0U2NydW0sIChwcm9ncmVzcywgc3RhcnRlZCkgPT4ge1xyXG4vLyAgICAgcmV0dXJuICh7Li4ucHJvZ3Jlc3MsIHN0YXJ0ZWR9KVxyXG4vLyB9KVxyXG4vLyAgICAgLm9uKHRvZ2dsZVR5cGUsIChwcm9ncmVzcywgdXNlclR5cGUpID0+IHtcclxuLy8gICAgICAgICByZXR1cm4gKHsuLi5wcm9ncmVzcywgdXNlclR5cGV9KVxyXG4vLyAgICAgfSk7XHJcbiIsImNvbnN0IGNyZWF0ZVN0b3JlSW1wbCA9IChjcmVhdGVTdGF0ZSkgPT4ge1xuICBsZXQgc3RhdGU7XG4gIGNvbnN0IGxpc3RlbmVycyA9IC8qIEBfX1BVUkVfXyAqLyBuZXcgU2V0KCk7XG4gIGNvbnN0IHNldFN0YXRlID0gKHBhcnRpYWwsIHJlcGxhY2UpID0+IHtcbiAgICBjb25zdCBuZXh0U3RhdGUgPSB0eXBlb2YgcGFydGlhbCA9PT0gXCJmdW5jdGlvblwiID8gcGFydGlhbChzdGF0ZSkgOiBwYXJ0aWFsO1xuICAgIGlmICghT2JqZWN0LmlzKG5leHRTdGF0ZSwgc3RhdGUpKSB7XG4gICAgICBjb25zdCBwcmV2aW91c1N0YXRlID0gc3RhdGU7XG4gICAgICBzdGF0ZSA9IChyZXBsYWNlICE9IG51bGwgPyByZXBsYWNlIDogdHlwZW9mIG5leHRTdGF0ZSAhPT0gXCJvYmplY3RcIiB8fCBuZXh0U3RhdGUgPT09IG51bGwpID8gbmV4dFN0YXRlIDogT2JqZWN0LmFzc2lnbih7fSwgc3RhdGUsIG5leHRTdGF0ZSk7XG4gICAgICBsaXN0ZW5lcnMuZm9yRWFjaCgobGlzdGVuZXIpID0+IGxpc3RlbmVyKHN0YXRlLCBwcmV2aW91c1N0YXRlKSk7XG4gICAgfVxuICB9O1xuICBjb25zdCBnZXRTdGF0ZSA9ICgpID0+IHN0YXRlO1xuICBjb25zdCBnZXRJbml0aWFsU3RhdGUgPSAoKSA9PiBpbml0aWFsU3RhdGU7XG4gIGNvbnN0IHN1YnNjcmliZSA9IChsaXN0ZW5lcikgPT4ge1xuICAgIGxpc3RlbmVycy5hZGQobGlzdGVuZXIpO1xuICAgIHJldHVybiAoKSA9PiBsaXN0ZW5lcnMuZGVsZXRlKGxpc3RlbmVyKTtcbiAgfTtcbiAgY29uc3QgZGVzdHJveSA9ICgpID0+IHtcbiAgICBpZiAoKGltcG9ydC5tZXRhLmVudiA/IGltcG9ydC5tZXRhLmVudi5NT0RFIDogdm9pZCAwKSAhPT0gXCJwcm9kdWN0aW9uXCIpIHtcbiAgICAgIGNvbnNvbGUud2FybihcbiAgICAgICAgXCJbREVQUkVDQVRFRF0gVGhlIGBkZXN0cm95YCBtZXRob2Qgd2lsbCBiZSB1bnN1cHBvcnRlZCBpbiBhIGZ1dHVyZSB2ZXJzaW9uLiBJbnN0ZWFkIHVzZSB1bnN1YnNjcmliZSBmdW5jdGlvbiByZXR1cm5lZCBieSBzdWJzY3JpYmUuIEV2ZXJ5dGhpbmcgd2lsbCBiZSBnYXJiYWdlLWNvbGxlY3RlZCBpZiBzdG9yZSBpcyBnYXJiYWdlLWNvbGxlY3RlZC5cIlxuICAgICAgKTtcbiAgICB9XG4gICAgbGlzdGVuZXJzLmNsZWFyKCk7XG4gIH07XG4gIGNvbnN0IGFwaSA9IHsgc2V0U3RhdGUsIGdldFN0YXRlLCBnZXRJbml0aWFsU3RhdGUsIHN1YnNjcmliZSwgZGVzdHJveSB9O1xuICBjb25zdCBpbml0aWFsU3RhdGUgPSBzdGF0ZSA9IGNyZWF0ZVN0YXRlKHNldFN0YXRlLCBnZXRTdGF0ZSwgYXBpKTtcbiAgcmV0dXJuIGFwaTtcbn07XG5jb25zdCBjcmVhdGVTdG9yZSA9IChjcmVhdGVTdGF0ZSkgPT4gY3JlYXRlU3RhdGUgPyBjcmVhdGVTdG9yZUltcGwoY3JlYXRlU3RhdGUpIDogY3JlYXRlU3RvcmVJbXBsO1xudmFyIHZhbmlsbGEgPSAoY3JlYXRlU3RhdGUpID0+IHtcbiAgaWYgKChpbXBvcnQubWV0YS5lbnYgPyBpbXBvcnQubWV0YS5lbnYuTU9ERSA6IHZvaWQgMCkgIT09IFwicHJvZHVjdGlvblwiKSB7XG4gICAgY29uc29sZS53YXJuKFxuICAgICAgXCJbREVQUkVDQVRFRF0gRGVmYXVsdCBleHBvcnQgaXMgZGVwcmVjYXRlZC4gSW5zdGVhZCB1c2UgaW1wb3J0IHsgY3JlYXRlU3RvcmUgfSBmcm9tICd6dXN0YW5kL3ZhbmlsbGEnLlwiXG4gICAgKTtcbiAgfVxuICByZXR1cm4gY3JlYXRlU3RvcmUoY3JlYXRlU3RhdGUpO1xufTtcblxuZXhwb3J0IHsgY3JlYXRlU3RvcmUsIHZhbmlsbGEgYXMgZGVmYXVsdCB9O1xuIiwiLy8gVGhlIG1vZHVsZSBjYWNoZVxudmFyIF9fd2VicGFja19tb2R1bGVfY2FjaGVfXyA9IHt9O1xuXG4vLyBUaGUgcmVxdWlyZSBmdW5jdGlvblxuZnVuY3Rpb24gX193ZWJwYWNrX3JlcXVpcmVfXyhtb2R1bGVJZCkge1xuXHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcblx0dmFyIGNhY2hlZE1vZHVsZSA9IF9fd2VicGFja19tb2R1bGVfY2FjaGVfX1ttb2R1bGVJZF07XG5cdGlmIChjYWNoZWRNb2R1bGUgIT09IHVuZGVmaW5lZCkge1xuXHRcdHJldHVybiBjYWNoZWRNb2R1bGUuZXhwb3J0cztcblx0fVxuXHQvLyBDcmVhdGUgYSBuZXcgbW9kdWxlIChhbmQgcHV0IGl0IGludG8gdGhlIGNhY2hlKVxuXHR2YXIgbW9kdWxlID0gX193ZWJwYWNrX21vZHVsZV9jYWNoZV9fW21vZHVsZUlkXSA9IHtcblx0XHQvLyBubyBtb2R1bGUuaWQgbmVlZGVkXG5cdFx0Ly8gbm8gbW9kdWxlLmxvYWRlZCBuZWVkZWRcblx0XHRleHBvcnRzOiB7fVxuXHR9O1xuXG5cdC8vIEV4ZWN1dGUgdGhlIG1vZHVsZSBmdW5jdGlvblxuXHRfX3dlYnBhY2tfbW9kdWxlc19fW21vZHVsZUlkXShtb2R1bGUsIG1vZHVsZS5leHBvcnRzLCBfX3dlYnBhY2tfcmVxdWlyZV9fKTtcblxuXHQvLyBSZXR1cm4gdGhlIGV4cG9ydHMgb2YgdGhlIG1vZHVsZVxuXHRyZXR1cm4gbW9kdWxlLmV4cG9ydHM7XG59XG5cbiIsIi8vIGRlZmluZSBnZXR0ZXIgZnVuY3Rpb25zIGZvciBoYXJtb255IGV4cG9ydHNcbl9fd2VicGFja19yZXF1aXJlX18uZCA9IChleHBvcnRzLCBkZWZpbml0aW9uKSA9PiB7XG5cdGZvcih2YXIga2V5IGluIGRlZmluaXRpb24pIHtcblx0XHRpZihfX3dlYnBhY2tfcmVxdWlyZV9fLm8oZGVmaW5pdGlvbiwga2V5KSAmJiAhX193ZWJwYWNrX3JlcXVpcmVfXy5vKGV4cG9ydHMsIGtleSkpIHtcblx0XHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBrZXksIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBkZWZpbml0aW9uW2tleV0gfSk7XG5cdFx0fVxuXHR9XG59OyIsIl9fd2VicGFja19yZXF1aXJlX18ubyA9IChvYmosIHByb3ApID0+IChPYmplY3QucHJvdG90eXBlLmhhc093blByb3BlcnR5LmNhbGwob2JqLCBwcm9wKSkiLCIvLyBkZWZpbmUgX19lc01vZHVsZSBvbiBleHBvcnRzXG5fX3dlYnBhY2tfcmVxdWlyZV9fLnIgPSAoZXhwb3J0cykgPT4ge1xuXHRpZih0eXBlb2YgU3ltYm9sICE9PSAndW5kZWZpbmVkJyAmJiBTeW1ib2wudG9TdHJpbmdUYWcpIHtcblx0XHRPYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgU3ltYm9sLnRvU3RyaW5nVGFnLCB7IHZhbHVlOiAnTW9kdWxlJyB9KTtcblx0fVxuXHRPYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgJ19fZXNNb2R1bGUnLCB7IHZhbHVlOiB0cnVlIH0pO1xufTsiLCJpbXBvcnQge3N0b3JlLH0gZnJvbSAnLi9zdG9yZSdcclxuY29uc3QgeyBnZXRTdGF0ZSwgc2V0U3RhdGUsIHN1YnNjcmliZSwgZ2V0SW5pdGlhbFN0YXRlIH0gPSBzdG9yZTtcclxuXHJcbmNvbnN0IGVtcHR5TWVzc2FnZSA9ICgpID0+ICh7XHJcbiAgICBcInNjcnVtSWRcIjogXCJcIixcclxuICAgIFwiZnJvbVwiOiBcIlwiLFxyXG4gICAgXCJ2b3RlXCI6IFwiXCIsXHJcbiAgICBcInRpbWVWb3RlZFwiOiBcIlwiXHJcbn0pO1xyXG5cclxuY29uc3QgZW1wdHlVc2VyID0gKCkgPT4gKHtcclxuICAgIFwic2NydW1JZFwiOiBcIlwiLFxyXG4gICAgXCJzY3JlZW5OYW1lXCI6IFwiXCIsXHJcbiAgICBcImVtYWlsXCI6IFwiXCIsXHJcbiAgICBcImNpdHlcIjogXCJcIixcclxuICAgIFwic3RhdGVcIjogXCJcIlxyXG59KTtcclxuXHJcbmNvbnN0IGVtcHR5U2NydW0gPSAoKSA9PiAoe1xyXG4gICAgc2NydW1JZDogXCJcIixcclxuICAgIHRpdGxlOiBcIlwiLFxyXG4gICAgdXNlclR5cGU6IFwib3JnYW5pemVyXCIsXHJcbiAgICBtZW1iZXI6IHtcclxuICAgICAgICBzY3JlZW5OYW1lOiBcIlwiLFxyXG4gICAgfSxcclxuICAgIHN0YXJ0VGltZTogXCJcIixcclxuICAgIHF1ZXN0aW9uOiBcIlwiLFxyXG4gICAgbWVtYmVyczogW10sXHJcbiAgICBjaG9pY2VzOiBbXSxcclxufSk7XHJcblxyXG4vLyAkcHJvZ3Jlc3MuLndhdGNoKCgpID0+IHtcclxuLy8gICAgIHJlbmRlcigpO1xyXG4vLyB9KVxyXG5cclxuXHJcblxyXG5jb25zdCB2b3RpbmdDaG9pY2VUZW1wbGF0ZSA9IChjaG9pY2UpID0+IGBcclxuPGFydGljbGUgY2xhc3M9XCJwdXJlLXUtMS00XCI+XHJcbiAgICA8cCBjbGFzcz1cImNob2ljZVwiIG9uY2xpY2s9XCJcIj4ke2Nob2ljZX08L3A+XHJcbjwvYXJ0aWNsZT5cclxuYDtcclxuXHJcbmZ1bmN0aW9uIG5ld1ZvdGluZ0Nob2ljZShjaG9pY2UpIHtcclxuICAgIGxldCBmcmFnbWVudCA9IGRvY3VtZW50LmNyZWF0ZVJhbmdlKCkuY3JlYXRlQ29udGV4dHVhbEZyYWdtZW50KFxyXG4gICAgICAgIHZvdGluZ0Nob2ljZVRlbXBsYXRlKGNob2ljZSkudHJpbSgpKTtcclxuICAgIGRvY3VtZW50LmdldEVsZW1lbnRCeUlkKFwic3VibWl0dGluZy1jaG9pY2VcIikuYXBwZW5kQ2hpbGQoZnJhZ21lbnQuZmlyc3RDaGlsZCk7XHJcbn1cclxuXHJcbmNvbnN0IHBhcnRpY2lwYW50VGVtcGxhdGUgPSAoc2NyZWVuTmFtZSkgPT4gYFxyXG48YXJ0aWNsZSBjbGFzcz1cInB1cmUtdS0xLTRcIj5cclxuICAgIDxwIGNsYXNzPVwiY29udHJvbHNcIj48aSBjbGFzcz1cImZhIGZhLXRpbWVzLWNpcmNsZVwiIGFyaWEtaGlkZGVuPVwidHJ1ZVwiPjwvaT48L3A+XHJcbiAgICA8cCBjbGFzcz1cInZvdGVcIj4ke3NjcmVlbk5hbWV9PC9wPlxyXG48L2FydGljbGU+XHJcbmA7XHJcblxyXG5mdW5jdGlvbiBuZXdQYXJ0aWNpcGFudCh7c2NyZWVuTmFtZSwgY2l0eSwgc3RhdGV9KSB7XHJcbiAgICBsZXQgZnJhZ21lbnQgPSBkb2N1bWVudC5jcmVhdGVSYW5nZSgpLmNyZWF0ZUNvbnRleHR1YWxGcmFnbWVudChcclxuICAgICAgICBwYXJ0aWNpcGFudFRlbXBsYXRlKHNjcmVlbk5hbWUsIGNpdHksIHN0YXRlKS50cmltKCkpO1xyXG4gICAgZG9jdW1lbnQuZ2V0RWxlbWVudEJ5SWQoXCJ2b3RpbmctcHJvZ3Jlc3NcIikuYXBwZW5kQ2hpbGQoZnJhZ21lbnQuZmlyc3RDaGlsZCk7XHJcbn1cclxuXHJcbmFzeW5jIGZ1bmN0aW9uIHNlbmRWb3RpbmdQcm9tcHQoKSB7XHJcbiAgICBjb25zdCB7c2NydW1JZCwgc2NyZWVuTmFtZSwgY2hvaWNlc30gPSBzdGF0ZS5vcmdhbml6ZXI7XHJcbiAgICBsZXQgcXVlc3Rpb24gPSBkb2N1bWVudC5nZXRFbGVtZW50QnlJZChcInZvdGUtZm9ybVwiKS5xdWVyeVNlbGVjdG9yKFwidGV4dGFyZWFcIikudmFsdWU7XHJcbiAgICBjb25zdCByZXNwb25zZSA9IGF3YWl0IGZldGNoKGAvc2NydW0vP3NjcnVtSWQ9JHtzY3J1bUlkfWAsIHtcclxuICAgICAgICBtZXRob2Q6IFwiUFVUXCIsXHJcbiAgICAgICAgaGVhZGVyczogeydDb250ZW50LVR5cGUnOiAnYXBwbGljYXRpb24vanNvbid9LFxyXG4gICAgICAgIGJvZHk6IEpTT04uc3RyaW5naWZ5KHtzY3J1bUlkLCBzY3JlZW5OYW1lLCBxdWVzdGlvbiwgY2hvaWNlc30pXHJcbiAgICB9KTtcclxuXHJcbiAgICByZXNwb25zZS5qc29uKCkudGhlbihkYXRhID0+IHtcclxuICAgICAgICBjb25zb2xlLmxvZyhkYXRhLnN0YXR1cyk7XHJcbiAgICB9KS5jYXRjaChlcnIgPT4ge1xyXG4gICAgICAgIGNvbnNvbGUubG9nKGVyci5tZXNzYWdlICsgXCJcXG5EbyBzb21ldGhpbmcgbW9yZSB1c2VmdWwgdGhhbiBqdXN0IGxvZ2dpbmcgdGhlIGVycm9yXCIpO1xyXG4gICAgfSk7XHJcbn1cclxuXHJcbmFzeW5jIGZ1bmN0aW9uIHJlc2V0Vm90aW5nUHJvbXB0KCkge1xyXG5cclxufVxyXG5cclxuYXN5bmMgZnVuY3Rpb24gam9pblNjcnVtQnlTY3J1bUlkKGUpIHtcclxuICAgIGUucHJldmVudERlZmF1bHQoKTtcclxuXHJcbiAgICBjb25zdCBmb3JtRGF0YSA9IG5ldyBGb3JtRGF0YShlLnRhcmdldCk7XHJcblxyXG4gICAgY29uc3Qgc2NydW1JZCA9IGZvcm1EYXRhLmdldChcInNjcnVtSWRcIik7XHJcbiAgICBjb25zdCBzY3JlZW5OYW1lID0gZm9ybURhdGEuZ2V0KFwic2NyZWVuTmFtZVwiKTtcclxuXHJcbiAgICBjb25zdCBqb2luQ2hhdCA9IGVtcHR5VXNlcigpO1xyXG4gICAgam9pbkNoYXQuc2NydW1JZCA9IHNjcnVtSWQ7XHJcbiAgICBqb2luQ2hhdC5zY3JlZW5OYW1lID0gc2NyZWVuTmFtZTtcclxuXHJcbiAgICBjb25zdCByZXNwb25zZSA9IGF3YWl0IGZldGNoKGAvam9pbi8/c2NydW1JZD0ke3NjcnVtSWR9YCwge1xyXG4gICAgICAgIG1ldGhvZDogXCJQT1NUXCIsXHJcbiAgICAgICAgaGVhZGVyczogeydDb250ZW50LVR5cGUnOiAnYXBwbGljYXRpb24vanNvbid9LFxyXG4gICAgICAgIGJvZHk6IEpTT04uc3RyaW5naWZ5KGpvaW5DaGF0KVxyXG4gICAgfSk7XHJcblxyXG4gICAgcmVzcG9uc2UuanNvbigpLnRoZW4oZGF0YSA9PiB7XHJcbiAgICAgICAgbGV0IHtzY3JlZW5OYW1lfSA9IGRhdGE7XHJcblxyXG4gICAgICAgIHN0YXRlLnBhcnRpY2lwYW50LnNjcmVlbk5hbWUgPSBzY3JlZW5OYW1lO1xyXG4gICAgICAgIHN0YXJ0RXZlbnRTb3VyY2Uoc2NydW1JZCwgc2NyZWVuTmFtZSlcclxuICAgIH0pLmNhdGNoKGVyciA9PiB7XHJcbiAgICAgICAgY29uc29sZS5sb2coZXJyLm1lc3NhZ2UgKyBcIlxcbkRvIHNvbWV0aGluZyBtb3JlIHVzZWZ1bCB0aGFuIGp1c3QgbG9nZ2luZyB0aGUgZXJyb3JcIik7XHJcbiAgICB9KTtcclxufVxyXG5cclxuYXN5bmMgZnVuY3Rpb24gb3JnYW5pemVOZXdTY3J1bShlKSB7XHJcbiAgICBlLnByZXZlbnREZWZhdWx0KCk7XHJcblxyXG4gICAgY29uc3QgZm9ybURhdGEgPSBuZXcgRm9ybURhdGEoZS50YXJnZXQpO1xyXG5cclxuICAgIGNvbnN0IHNjcnVtVGl0bGUgPSBmb3JtRGF0YS5nZXQoXCJzY3J1bVRpdGxlXCIpO1xyXG4gICAgY29uc3Qgc2NyZWVuTmFtZSA9IGZvcm1EYXRhLmdldChcInNjcmVlbk5hbWVcIik7XHJcbiAgICBjb25zdCB2b3RlQ2hvaWNlcyA9IGZvcm1EYXRhLmdldChcInZvdGVDaG9pY2VzXCIpO1xyXG5cclxuICAgIGNvbnN0IG5ld1NjcnVtID0gZW1wdHlTY3J1bSgpO1xyXG4gICAgbmV3U2NydW0uc2NydW1UaXRsZSA9IHNjcnVtVGl0bGU7XHJcbiAgICBuZXdTY3J1bS5zY3J1bU1hc3Rlci5zY3JlZW5OYW1lID0gc2NyZWVuTmFtZTtcclxuICAgIG5ld1NjcnVtLnZvdGVDaG9pY2VzID0gdm90ZUNob2ljZXM7XHJcblxyXG4gICAgY29uc3QgcmVzcG9uc2UgPSBhd2FpdCBmZXRjaChcIi9zY3J1bS9cIiwge1xyXG4gICAgICAgIG1ldGhvZDogXCJQT1NUXCIsXHJcbiAgICAgICAgaGVhZGVyczogeydDb250ZW50LVR5cGUnOiAnYXBwbGljYXRpb24vanNvbid9LFxyXG4gICAgICAgIGJvZHk6IEpTT04uc3RyaW5naWZ5KG5ld1NjcnVtKVxyXG4gICAgfSk7XHJcblxyXG4gICAgcmVzcG9uc2UuanNvbigpLnRoZW4oZGF0YSA9PiB7XHJcbiAgICAgICAgbGV0IHN0YXRlID0ge31cclxuICAgICAgICBsZXQge3NjcnVtSWQsIHNjcnVtVGl0bGUsIHNjcnVtTWFzdGVyOiB7c2NyZWVuTmFtZX0sIHZvdGVDaG9pY2VzfSA9IGRhdGE7XHJcbiAgICAgICAgc3RhdGUub3JnYW5pemVyLnNjcnVtSWQgPSBzY3J1bUlkO1xyXG4gICAgICAgIHN0YXRlLm9yZ2FuaXplci5zY3J1bVRpdGxlID0gc2NydW1UaXRsZTtcclxuICAgICAgICBzdGF0ZS5vcmdhbml6ZXIuc2NyZWVuTmFtZSA9IHNjcmVlbk5hbWU7XHJcbiAgICAgICAgc3RhdGUub3JnYW5pemVyLmNob2ljZXMgPSB2b3RlQ2hvaWNlcztcclxuICAgICAgICBzdGFydEV2ZW50U291cmNlKHNjcnVtSWQsIHNjcmVlbk5hbWUpXHJcbiAgICB9KS5jYXRjaChlcnIgPT4ge1xyXG4gICAgICAgIGNvbnNvbGUubG9nKGVyci5tZXNzYWdlICsgXCJcXG5EbyBzb21ldGhpbmcgbW9yZSB1c2VmdWwgdGhhbiBqdXN0IGxvZ2dpbmcgdGhlIGVycm9yXCIpO1xyXG4gICAgfSk7XHJcbn1cclxuXHJcbmZ1bmN0aW9uIHN0YXJ0RXZlbnRTb3VyY2Uoc2NydW1JZCwgc2NyZWVuTmFtZSkge1xyXG5cclxuICAgIGNvbnN0IGV2dFNvdXJjZSA9IG5ldyBFdmVudFNvdXJjZShgL2pvaW4vP3NjcnVtSWQ9JHtzY3J1bUlkfSZzY3JlZW5OYW1lPSR7c2NyZWVuTmFtZX1gLCB7XHJcbiAgICAgICAgd2l0aENyZWRlbnRpYWxzOiBmYWxzZVxyXG4gICAgfSk7XHJcblxyXG4gICAgZXZ0U291cmNlLm9ubWVzc2FnZSA9IChldmVudCkgPT4ge1xyXG4gICAgICAgIC8vIGFkZE1lc3NhZ2VMaW5lKGV2ZW50KTtcclxuICAgICAgICBjb25zb2xlLmxvZyhldnRTb3VyY2UucmVhZHlTdGF0ZSlcclxuICAgIH07XHJcblxyXG4gICAgZXZ0U291cmNlLmFkZEV2ZW50TGlzdGVuZXIoJ2pvaW5lZCcsIGV2ZW50ID0+IHtcclxuICAgICAgICBjb25zdCBkYXRhID0gSlNPTi5wYXJzZShldmVudC5kYXRhKTtcclxuICAgICAgICBjb25zb2xlLmxvZyhkYXRhLnZvdGUpO1xyXG4gICAgICAgIGlmIChzdGF0ZS51c2VyVHlwZSA9PT0gXCJvcmdhbml6ZXJcIikge1xyXG4gICAgICAgICAgICBpZiAoIXN0YXRlLnBsYXlpbmcpIHtcclxuICAgICAgICAgICAgICAgIHJlbmRlcigpO1xyXG4gICAgICAgICAgICB9IGVsc2Uge1xyXG4gICAgICAgICAgICAgICAgcmVuZGVyVm90aW5nUHJvZ3Jlc3MoKTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgIH0gZWxzZSB7XHJcbiAgICAgICAgICAgIHJlbmRlclBhcnRpY2lwYW50UGFnZSgpO1xyXG4gICAgICAgIH1cclxuICAgIH0pO1xyXG5cclxuICAgIGV2dFNvdXJjZS5hZGRFdmVudExpc3RlbmVyKCd2b3RpbmcnLCBldmVudCA9PiB7XHJcbiAgICAgICAgY29uc3QgZGF0YSA9IEpTT04ucGFyc2UoZXZlbnQuZGF0YSk7XHJcbiAgICAgICAgaWYgKHN0YXRlLnVzZXJUeXBlICE9PSBcIm9yZ2FuaXplclwiKSB7XHJcbiAgICAgICAgICAgIGNvbnN0IHtjaG9pY2VzfSA9IGRhdGE7XHJcblxyXG4gICAgICAgICAgICBlbXB0eU5vZGUoXCIjc3VibWl0dGluZy1jaG9pY2VcIik7XHJcbiAgICAgICAgICAgIGZvciAobGV0IGNob2ljZSBvZiBjaG9pY2VzKSB7XHJcbiAgICAgICAgICAgICAgICBuZXdWb3RpbmdDaG9pY2UoY2hvaWNlKTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgIH1cclxuICAgIH0pO1xyXG5cclxuICAgIGV2dFNvdXJjZS5hZGRFdmVudExpc3RlbmVyKCdwYXJ0aWNpcGFudHMnLCBldmVudCA9PiB7XHJcbiAgICAgICAgY29uc3QgZGF0YSA9IEpTT04ucGFyc2UoZXZlbnQuZGF0YSk7XHJcbiAgICAgICAgaWYgKHN0YXRlLnVzZXJUeXBlID09PSBcIm9yZ2FuaXplclwiKSB7XHJcbiAgICAgICAgICAgIHN0YXRlLnBsYXlpbmcgPSB0cnVlO1xyXG4gICAgICAgICAgICBzdGF0ZS5vcmdhbml6ZXIucGFydGljaXBhbnRzLnB1c2goZGF0YSlcclxuICAgICAgICAgICAgbmV3UGFydGljaXBhbnQoZGF0YSlcclxuICAgICAgICB9XHJcbiAgICB9KTtcclxuXHJcbiAgICBldnRTb3VyY2UuYWRkRXZlbnRMaXN0ZW5lcignZGVsZXRlJywgZXZlbnQgPT4ge1xyXG4gICAgICAgIC8vIGFkZE1lc3NhZ2VMaW5lKGV2ZW50KTtcclxuICAgICAgICBjb25zb2xlLmxvZyhldnRTb3VyY2UucmVhZHlTdGF0ZSlcclxuICAgIH0pO1xyXG5cclxuICAgIGV2dFNvdXJjZS5vbmVycm9yID0gZnVuY3Rpb24gKGUpIHtcclxuICAgICAgICAvLyBhZGRNZXNzYWdlTGluZSh7ZGF0YTogXCJmaWd1cmUgb3V0IHdoeSB0aGlzIGVycm9yIGhhcHBlbmVkXCJ9KVxyXG4gICAgICAgIGNvbnNvbGUubG9nKGV2dFNvdXJjZS5yZWFkeVN0YXRlKVxyXG4gICAgfTtcclxufVxyXG5cclxuLy8gYXN5bmMgZnVuY3Rpb24gc2VuZE1lc3NhZ2UoKSB7XHJcbi8vICAgICBjb25zdCBzZW5kZXIgPSB1c2VyVHlwZSA9PT0gXCJvcmdhbml6ZXJcIiA/XHJcbi8vICAgICAgICAgZG9jdW1lbnQucXVlcnlTZWxlY3RvcihcIiNjaGF0LW9yZ2FuaXplclwiKS5zY3JlZW5OYW1lLnZhbHVlIDpcclxuLy8gICAgICAgICBkb2N1bWVudC5xdWVyeVNlbGVjdG9yKFwiI2NoYXQtcGFydGljaXBhbnRcIikuc2NyZWVuTmFtZS52YWx1ZVxyXG4vLyAgICAgY29uc3Qgc2NydW1JZCA9IHVzZXJUeXBlID09PSBcIm9yZ2FuaXplclwiID9cclxuLy8gICAgICAgICBkb2N1bWVudC5xdWVyeVNlbGVjdG9yKFwiI2NoYXQtb3JnYW5pemVyXCIpLnNjcnVtSWQudmFsdWUgOlxyXG4vLyAgICAgICAgIGRvY3VtZW50LnF1ZXJ5U2VsZWN0b3IoXCIjY2hhdC1wYXJ0aWNpcGFudFwiKS5zY3J1bUlkLnZhbHVlXHJcbi8vICAgICBjb25zdCBtZXNzYWdlID0gZG9jdW1lbnQucXVlcnlTZWxlY3RvcihcIiNjb21wb3NlLW1lc3NhZ2UgdGV4dGFyZWFcIikudmFsdWVcclxuLy8gICAgIGNvbnN0IG5ld01lc3NhZ2UgPSBPYmplY3QuYXNzaWduKHt9LCBlbXB0eU1lc3NhZ2UpXHJcbi8vICAgICBuZXdNZXNzYWdlLnNjcnVtSWQgPSBzY3J1bUlkO1xyXG4vLyAgICAgbmV3TWVzc2FnZS5mcm9tID0gc2VuZGVyO1xyXG4vLyAgICAgbmV3TWVzc2FnZS5tZXNzYWdlID0gbWVzc2FnZTtcclxuLy8gICAgIGlmIChtZXNzYWdlKSB7XHJcbi8vICAgICAgICAgY29uc3QgcmVzcG9uc2UgPSBhd2FpdCBmZXRjaChgL2NoYXQvP3NjcnVtSWQ9JHtzY3J1bUlkfSZzY3JlZW5OYW1lPSR7c2VuZGVyfWAsIHtcclxuLy8gICAgICAgICAgICAgbWV0aG9kOiBcIlBVVFwiLFxyXG4vLyAgICAgICAgICAgICBoZWFkZXJzOiB7J0NvbnRlbnQtVHlwZSc6ICdhcHBsaWNhdGlvbi9qc29uJ30sXHJcbi8vICAgICAgICAgICAgIGJvZHk6IEpTT04uc3RyaW5naWZ5KG5ld01lc3NhZ2UpXHJcbi8vICAgICAgICAgfSk7XHJcbi8vXHJcbi8vICAgICAgICAgcmVzcG9uc2UuanNvbigpLnRoZW4oZGF0YSA9PiB7XHJcbi8vICAgICAgICAgICAgIGNvbnNvbGUubG9nKFwibWVzc2FnZSBzZW50XCIpXHJcbi8vICAgICAgICAgICAgIGRvY3VtZW50LnF1ZXJ5U2VsZWN0b3IoXCIjY29tcG9zZS1tZXNzYWdlIHRleHRhcmVhXCIpLmZvY3VzKCk7XHJcbi8vICAgICAgICAgfSk7XHJcbi8vICAgICB9XHJcbi8vIH1cclxuXHJcbmZ1bmN0aW9uIHJlbmRlck5hdmlnYXRpb25CYXIoKSB7XHJcbiAgICBmdW5jdGlvbiB0b2dnbGVVc2VyVHlwZShlKSB7XHJcbiAgICAgICAgZ2V0U3RhdGUoKS50b2dnbGVUeXBlKGUudmFsdWUpO1xyXG4gICAgICAgIHJlbmRlcigpO1xyXG4gICAgfVxyXG4gICAgY29uc3QgdGVtcGxhdGUgPSBkb2N1bWVudC5nZXRFbGVtZW50QnlJZChcIm5hdmlnYXRpb24tdGVtcGxhdGVcIik7XHJcbiAgICBjb25zdCBmcmFnbWVudCA9IHRlbXBsYXRlLmNvbnRlbnQuY2xvbmVOb2RlKHRydWUpO1xyXG4gICAgaWYgKGdldFN0YXRlKCkucHJvZ3Jlc3MudXNlclR5cGUgPT09IFwicGFydGljaXBhbnRcIikge1xyXG4gICAgICAgIGZyYWdtZW50LmdldEVsZW1lbnRCeUlkKFwicGFydGljaXBhbnRcIikuY2hlY2tlZCA9IHRydWVcclxuICAgICAgICBmcmFnbWVudC5nZXRFbGVtZW50QnlJZChcInBhcnRpY2lwYW50XCIpLmFkZEV2ZW50TGlzdGVuZXIoJ2NsaWNrJywgdG9nZ2xlVXNlclR5cGUpO1xyXG4gICAgfVxyXG4gICAgaWYgKGdldFN0YXRlKCkucHJvZ3Jlc3MudXNlclR5cGUgPT09IFwib3JnYW5pemVyXCIpIHtcclxuICAgICAgICBmcmFnbWVudC5nZXRFbGVtZW50QnlJZChcIm9yZ2FuaXplclwiKS5jaGVja2VkID0gdHJ1ZVxyXG4gICAgICAgIGZyYWdtZW50LmdldEVsZW1lbnRCeUlkKFwib3JnYW5pemVyXCIpLmFkZEV2ZW50TGlzdGVuZXIoJ2NsaWNrJywgdG9nZ2xlVXNlclR5cGUpO1xyXG4gICAgfVxyXG5cclxuICAgIGRvY3VtZW50LmdldEVsZW1lbnRCeUlkKFwicm9vdFwiKS5hcHBlbmRDaGlsZChmcmFnbWVudCk7XHJcbn1cclxuXHJcbmZ1bmN0aW9uIHJlbmRlclBhcnRpY2lwYW50Rm9ybSgpIHtcclxuICAgIGNvbnN0IHRlbXBsYXRlID0gZG9jdW1lbnQuZ2V0RWxlbWVudEJ5SWQoXCJwYXJ0aWNpcGFudC1mb3JtLXRlbXBsYXRlXCIpO1xyXG4gICAgY29uc3QgZnJhZ21lbnQgPSB0ZW1wbGF0ZS5jb250ZW50LmNsb25lTm9kZSh0cnVlKTtcclxuICAgIGlmIChnZXRTdGF0ZSgpLnVzZXJUeXBlICE9PSBcInBhcnRpY2lwYW50XCIpIHtcclxuICAgICAgICBmcmFnbWVudC5jaGlsZHJlblswXS5jbGFzc0xpc3QuYWRkKFwiaGlkZGVuXCIpXHJcbiAgICB9XHJcbiAgICBkb2N1bWVudC5nZXRFbGVtZW50QnlJZChcInJvb3RcIikuYXBwZW5kQ2hpbGQoZnJhZ21lbnQpO1xyXG59XHJcblxyXG5mdW5jdGlvbiByZW5kZXJPcmdhbml6ZXJGb3JtKCkge1xyXG4gICAgY29uc3QgdGVtcGxhdGUgPSBkb2N1bWVudC5nZXRFbGVtZW50QnlJZChcIm9yZ2FuaXplci1mb3JtLXRlbXBsYXRlXCIpO1xyXG4gICAgY29uc3QgZnJhZ21lbnQgPSB0ZW1wbGF0ZS5jb250ZW50LmNsb25lTm9kZSh0cnVlKTtcclxuICAgIGlmIChnZXRTdGF0ZSgpLnVzZXJUeXBlICE9PSBcIm9yZ2FuaXplclwiKSB7XHJcbiAgICAgICAgZnJhZ21lbnQuY2hpbGRyZW5bMF0uY2xhc3NMaXN0LmFkZChcImhpZGRlblwiKVxyXG4gICAgfVxyXG4gICAgZG9jdW1lbnQuZ2V0RWxlbWVudEJ5SWQoXCJyb290XCIpLmFwcGVuZENoaWxkKGZyYWdtZW50KTtcclxufVxyXG5cclxuZnVuY3Rpb24gcmVuZGVyVm90aW5nRm9ybSgpIHtcclxuICAgIGNvbnN0IHRlbXBsYXRlID0gZG9jdW1lbnQuZ2V0RWxlbWVudEJ5SWQoXCJ2b3RlLWZvcm0tdGVtcGxhdGVcIik7XHJcbiAgICBjb25zdCBmcmFnbWVudCA9IHRlbXBsYXRlLmNvbnRlbnQuY2xvbmVOb2RlKHRydWUpO1xyXG4gICAgZG9jdW1lbnQuZ2V0RWxlbWVudEJ5SWQoXCJyb290XCIpLmFwcGVuZENoaWxkKGZyYWdtZW50KTtcclxufVxyXG5cclxuZnVuY3Rpb24gcmVuZGVyVm90aW5nUHJvZ3Jlc3MoKSB7XHJcbiAgICBpZiAoZ2V0U3RhdGUoKS51c2VyVHlwZSA9PT0gXCJvcmdhbml6ZXJcIiAmJiBnZXRTdGF0ZSgpLm9yZ2FuaXplci5wYXJ0aWNpcGFudHMubGVuZ3RoID09PSAwKSB7XHJcbiAgICAgICAgY29uc3QgdGVtcGxhdGUgPSBkb2N1bWVudC5nZXRFbGVtZW50QnlJZChcInZvdGluZy1wcm9ncmVzcy10ZW1wbGF0ZVwiKTtcclxuICAgICAgICBjb25zdCBmcmFnbWVudCA9IHRlbXBsYXRlLmNvbnRlbnQuY2xvbmVOb2RlKHRydWUpO1xyXG4gICAgICAgIGRvY3VtZW50LmdldEVsZW1lbnRCeUlkKFwicm9vdFwiKS5hcHBlbmRDaGlsZChmcmFnbWVudCk7XHJcbiAgICB9IGVsc2Uge1xyXG4gICAgICAgIGVtcHR5Tm9kZShcIiN2b3RpbmctcHJvZ3Jlc3NcIik7XHJcblxyXG4gICAgICAgIGZvciAobGV0IHtzY3JlZW5OYW1lfSBvZiBzdGF0ZS5vcmdhbml6ZXIucGFydGljaXBhbnRzKSB7XHJcbiAgICAgICAgICAgIG5ld1BhcnRpY2lwYW50KHtzY3JlZW5OYW1lfSlcclxuICAgICAgICB9XHJcbiAgICB9XHJcbn1cclxuXHJcbmZ1bmN0aW9uIHJlbmRlclN1Ym1pdHRpbmdDaG9pY2UoKSB7XHJcbiAgICBjb25zdCB0ZW1wbGF0ZSA9IGRvY3VtZW50LmdldEVsZW1lbnRCeUlkKFwic3VibWl0dGluZy1jaG9pY2UtdGVtcGxhdGVcIik7XHJcbiAgICBjb25zdCBmcmFnbWVudCA9IHRlbXBsYXRlLmNvbnRlbnQuY2xvbmVOb2RlKHRydWUpO1xyXG4gICAgZG9jdW1lbnQuZ2V0RWxlbWVudEJ5SWQoXCJyb290XCIpLmFwcGVuZENoaWxkKGZyYWdtZW50KTtcclxufVxyXG5cclxuZnVuY3Rpb24gZW1wdHlOb2RlKHNlbGVjdG9yKSB7XHJcbiAgICBjb25zdCByb290ID0gZG9jdW1lbnQucXVlcnlTZWxlY3RvcihzZWxlY3Rvcik7XHJcbiAgICB3aGlsZSAocm9vdC5maXJzdENoaWxkKSB7XHJcbiAgICAgICAgcm9vdC5yZW1vdmVDaGlsZChyb290Lmxhc3RDaGlsZCk7XHJcbiAgICB9XHJcbn1cclxuXHJcbmZ1bmN0aW9uIHJlbmRlcigpIHtcclxuICAgIGVtcHR5Tm9kZShcIiNyb290XCIpXHJcblxyXG4gICAgcmVuZGVyTmF2aWdhdGlvbkJhcigpO1xyXG5cclxuICAgIGNvbnN0IHtcclxuICAgICAgICB1c2VyVHlwZSxcclxuICAgICAgICBvcmdhbml6ZXI6IHtzY3JlZW5OYW1lOiBvcmdhbml6ZXJ9LFxyXG4gICAgICAgIHBhcnRpY2lwYW50OiB7XHJcbiAgICAgICAgICAgIHNjcmVlbk5hbWU6IHBhcnRpY2lwYW50XHJcbiAgICAgICAgfVxyXG4gICAgfSA9IGdldFN0YXRlKCk7XHJcblxyXG4gICAgaWYgKCFvcmdhbml6ZXIgJiYgdXNlclR5cGUgPT09ICdvcmdhbml6ZXInKSB7XHJcbiAgICAgICAgcmVuZGVyT3JnYW5pemVyRm9ybSgpO1xyXG4gICAgfVxyXG5cclxuICAgIGlmIChvcmdhbml6ZXIgJiYgdXNlclR5cGUgPT09ICdvcmdhbml6ZXInKSB7XHJcbiAgICAgICAgcmVuZGVyVm90aW5nRm9ybSgpO1xyXG4gICAgICAgIHJlbmRlclZvdGluZ1Byb2dyZXNzKCk7XHJcbiAgICB9XHJcblxyXG4gICAgaWYgKHVzZXJUeXBlID09PSAncGFydGljaXBhbnQnKSB7XHJcbiAgICAgICAgcmVuZGVyUGFydGljaXBhbnRGb3JtKCk7XHJcbiAgICB9XHJcbn1cclxuXHJcbmZ1bmN0aW9uIHJlbmRlclBhcnRpY2lwYW50UGFnZSgpIHtcclxuICAgIGNvbnN0IHJvb3QgPSBkb2N1bWVudC5nZXRFbGVtZW50QnlJZChcInJvb3RcIik7XHJcbiAgICB3aGlsZSAocm9vdC5maXJzdENoaWxkKSB7XHJcbiAgICAgICAgcm9vdC5yZW1vdmVDaGlsZChyb290Lmxhc3RDaGlsZCk7XHJcbiAgICB9XHJcblxyXG4gICAgcmVuZGVyTmF2aWdhdGlvbkJhcigpO1xyXG5cclxuICAgIHJlbmRlclN1Ym1pdHRpbmdDaG9pY2UoKTtcclxufVxyXG5cclxucmVuZGVyKCk7Il0sIm5hbWVzIjpbXSwic291cmNlUm9vdCI6IiJ9