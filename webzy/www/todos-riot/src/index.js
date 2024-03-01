import * as riot from 'riot';
import App from '../riot/todos-list.riot';
import {store} from "./store";

const {getState, setState, subscribe, getInitialState} = store;

const {todos, addTodo, editTodo, toggleDone, removeTodo} = getState();

riot.register('todos-list', App);

const mountApp = riot.component(App)
const app = mountApp(
    document.getElementById('root'),
    {
        title: 'Awesome Todos',
        todos,
        addTodo,
        editTodo,
        toggleDone,
        removeTodo,
        subscribe,
    }
)