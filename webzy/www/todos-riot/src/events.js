import {v4 as uuidv4} from "uuid";
import {store} from "./store";

export const listId = "testing-todos";
export const listName = "Awesome Todos";
export const listOwner = "jimmy";

export const initialTodos = [
    {listId, id: uuidv4(), task: 'Avoid excessive caffeine', completed: true},
    {listId, id: uuidv4(), task: 'Be less provocative'},
    {listId, id: uuidv4(), task: 'Be nice to people'}
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

export function createNewTodo(task){
    fetch(`http://localhost:9080/todos/?action=create`,{
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

    evtSource.addEventListener('newTask', event => {
        console.log(event.data)
        const data = JSON.parse(event.data);
        store.getState().addTodo(data)
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

