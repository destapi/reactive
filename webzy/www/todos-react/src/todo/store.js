import {
    ADD_ITEM,
    REMOVE_ALL_ITEMS,
    REMOVE_COMPLETED_ITEMS,
    REMOVE_ITEM,
    TOGGLE_ALL,
    TOGGLE_ITEM,
    UPDATE_ITEM
} from "./constants";

let urlAlphabet = "useandom-26T198340PX75pxJACKVERYMINDBUSHWOLF_GQZbfghjklqvwyzrict";

function nanoid(size = 21) {
    let id = "";
    // A compact alternative for `for (var i = 0; i < step; i++)`.
    let i = size;
    while (i--) {
        // `| 0` is more compact and faster than `Math.floor()`.
        id += urlAlphabet[(Math.random() * 64) | 0];
    }
    return id;
}

export const todoReducer = async (state, action) => {
    switch (action.type) {
        case ADD_ITEM:
            return state.concat({id: nanoid(), title: action.payload.title, completed: false});
        case UPDATE_ITEM:
            return state.map((todo) => (todo.id === action.payload.id ? {...todo, title: action.payload.title} : todo));
        case REMOVE_ITEM:
            return state.filter((todo) => todo.id !== action.payload.id);
        case TOGGLE_ITEM:
            return state.map((todo) => (todo.id === action.payload.id ? {...todo, completed: !todo.completed} : todo));
        case REMOVE_ALL_ITEMS:
            return [];
        case TOGGLE_ALL:
            return state.map((todo) => (todo.completed !== action.payload.completed ? {
                ...todo,
                completed: action.payload.completed
            } : todo));
        case REMOVE_COMPLETED_ITEMS:
            return state.filter((todo) => !todo.completed);
    }

    throw Error(`Unknown action: ${action.type}`);
};

/*
* curl -X POST "http://localhost:9080/todos/?listId=testlist&listName=fav-list&listOwner=jimmy" -H "Content-Type: application/json" -d "[]"
{
    "listId": "testlist",
    "listName": "fav-list",
    "listOwner": {
        "listId": "testlist",
        "name": "jimmy",
        "email": null,
        "phone": null
    },
    "dateCreated": "2024-02-26T16:46:04.3374546",
    "sharedTo": {
    },
    "todos": [
    ]
* */
async function createTodosAction() {
    await fetch(`http://localhost:9080/todos/?listId=${listId}&listName=${listName}&listOwner=${listOwner}`, {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(todos)
    }).then(resp => resp.json())
        .then(data => {
            console.log("creating initial todos", data)
        });
}

/*
*  curl -X PUT "http://localhost:9080/todos/?action=create" -H "Content-Type: application/json" -d "{\"listId\": \"testlist\", \"id\": "1", \"title\": \"wake up\", \"completed\": false }"
  {
    "status": "ok"
  }
* */
export async function addItemAction(title) {
    await fetch(`http://localhost:9080/todos/?action=create`, {
        method: "PUT",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({listId, id: nanoid(), title, completed: false})
    }).then(resp => resp.json())
        .then(data => {
            console.log("creating initial todos", data)
        });
}

export async function toggleAllAction() {
    await fetch("http://localhost:9080/scrum/", {
        method: "PUT",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(newScrum)
    }).then(resp => resp.json())
        .then(data => {
            console.log("creating todo", data)
        });
}

export async function removeCompletedAction() {
    await fetch("http://localhost:9080/scrum/", {
        method: "PUT",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(newScrum)
    }).then(resp => resp.json())
        .then(data => {
            console.log("creating todo", data)
        });
}

export async function startEventSource() {

    let searchParams = {};
    const regexp = /&?((\w+)=(\w+))/gm
    window.location.search.match(regexp)
        .map(v => v.startsWith("&") ? v.substring(1) : v)
        .reduce((acc, curr) => {
            let kv = curr.split("=");
            acc[kv[0]] = kv[1];
            return acc;
        }, searchParams);

    let {server, listId, listOwner, listName} = searchParams;

    if (String(server) === "true") {
        // http://127.0.0.1:8080/join/?server=true&listId=testlist&listName=fav-list&listOwner=jimmy
        const evtSource = new EventSource(`http://localhost:9080/join/?listId=${listId}&listOwner=${listOwner}&listName=${listName}`, {
            withCredentials: false
        });

        evtSource.onmessage = function (event) {
            const data = JSON.parse(event.data);
            console.log(data.message);
            createTodosAction();
        }

        evtSource.addEventListener('initialized', event => {
            const data = JSON.parse(event.data);
            console.log(data);  //no update to state necessary
        });

        evtSource.addEventListener('newTask', event => {
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
}