import {v4 as uuidv4} from "uuid";
import {store} from "./store";

export const userCoordinates = {
    listId: "testing-todos",
    listName: "Awesome Todos",
    listOwner: "jimmy",
    userType: "organizer",
    guestName: null
};

// for organizer, use - http://localhost:8080/?listId=ijummaa&listOwner=jimmy&listName=alhamisi
// for guest, use - http://localhost:8080/?listId=ijummaa&listOwner=jimmy&listName=alhamisi&userType=guest&guestName=kadzo

const regex = /\??((\w+)=(\w+))/gm
const matches = window.location.search.match(regex);
if (matches) {
    matches.map(m => m.startsWith("?") ? m.substring(1) : m).reduce((acc, curr) => {
        const [key, value] = curr.split("=");
        acc[key] = value;
        return acc;
    }, userCoordinates)
}

const {listId, listName, listOwner, userType, guestName} = userCoordinates;

export const initialTodos = [
    {listId, id: uuidv4(), task: 'Avoid excessive caffeine', completed: true},
    {listId, id: uuidv4(), task: 'Be less provocative'},
    {listId, id: uuidv4(), task: 'Be nice to people'}
]

export function startTodosAction(data) {
    fetch(`http://localhost:9080/todos/`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(res => res.json())
        .then(data => {
            console.log(data);
        })
}

export function joinTodosAction(data) {
    fetch(`http://localhost:9080/join/`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(res => res.json())
        .then(data => {
            console.log(data);
        })
}

export function addTodoAction(task) {
    fetch(`http://localhost:9080/todos/?action=create`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({listId, id: uuidv4(), task, completed: false})
    })
        .then(res => res.json())
        .then(data => {
            console.log("on create todo", data)
        })
}

export function toggleDoneAction(id) {
    fetch(`http://localhost:9080/todos/?action=toggle`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({listId, id})
    })
        .then(res => res.json())
        .then(data => {
            console.log("on toggle todo", data)
        })
}

export function removeTodoAction(id) {
    fetch(`http://localhost:9080/todos/?listId=${listId}&id=${id}`, {
        method: 'DELETE',
    })
        .then(res => res.json())
        .then(data => {
            console.log("on remove todo", data)
        })
}

export function startEventSource() {

    const evtSource = new EventSource(`http://localhost:9080/join/?listId=${listId}&listName=${listName}&listOwner=${listOwner}`, {
        withCredentials: false
    });

    evtSource.onmessage = (event) => {
        if(userType === "organizer") {
            startTodosAction({listId, listName, listOwner, todos: initialTodos});
        }
        else{
            joinTodosAction({listId, guestName})
        }
    };

    evtSource.addEventListener('initialized', event => {
        console.log(event.data)
        const data = JSON.parse(event.data);
        store.getState().initTodos(data)
    });

    evtSource.addEventListener('newTask', event => {
        console.log(event.data)
        const data = JSON.parse(event.data);
        store.getState().addTodo(data)
    });

    evtSource.addEventListener('updateTask', event => {
        console.log(event.data)
        const data = JSON.parse(event.data);
        store.getState().updateTodo(data)
    });

    evtSource.addEventListener('removeTask', event => {
        console.log(event.data)
        const data = JSON.parse(event.data);
        store.getState().removeTodo(data.id)
    });

    evtSource.addEventListener('newShare', event => {
        console.log(event.data)
    });

    evtSource.onerror = function (e) {
        console.error(e)
    };
}

