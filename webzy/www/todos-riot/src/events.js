import {v4 as uuidv4} from "uuid";
import {store} from "./store";

export const listId = "testing-todos";
export const listName = "Awesome Todos";
export const listOwner = "jimmy";

export const initialTodos = [
    {id: uuidv4(), task: 'Avoid excessive caffeine', completed: true},
    {id: uuidv4(), task: 'Be less provocative'},
    {id: uuidv4(), task: 'Be nice to people'}
]

export function startTodos(todos)
{
    fetch(`http://localhost:9080/todos/?listId=${listId}&listName=${listName}&listOwner=${listOwner}`,{
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(todos)
    })
        .then(res => res.json())
        .then(data => {
            console.log(data);
        })
}

export function startEventSource() {

    const evtSource = new EventSource(`http://localhost:9080/join/?listId=${listId}&listName=${listName}&listOwner=${listOwner}`, {
        withCredentials: false
    });

    evtSource.onmessage = (event) => {
        startTodos(initialTodos);
    };

    evtSource.addEventListener('initialized', event => {
        console.log(event.data)
        const data = JSON.parse(event.data);
        store.getState().initTodos(data)
    });

    evtSource.addEventListener('set', event => {
        console.log(evtSource.readyState)
    });

    evtSource.addEventListener('get', event => {
        console.log(evtSource.readyState)
    });

    evtSource.addEventListener('delete', event => {
        console.log(evtSource.readyState)
    });

    evtSource.onerror = function (e) {
        console.log(evtSource.readyState)
    };
}

