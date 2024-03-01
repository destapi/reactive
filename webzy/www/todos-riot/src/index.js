import * as riot from 'riot';
import App from '../riot/todos-list.riot';
import {store} from "./store";
import {addTodoAction, removeTodoAction, startEventSource, toggleDoneAction} from "./events";

const {subscribe,} = store;

riot.register('todos-list', App);

const mountApp = riot.component(App)
const app = mountApp(
    document.getElementById('root'),
    {
        title: 'Awesome Todos',
        todos: store.getState(),
        subscribe,
        addTodoAction,
        toggleDoneAction,
        removeTodoAction,
        startEventSource,
    }
)