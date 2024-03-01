import * as riot from 'riot';
import App from '../riot/todos-list.riot';
import {store} from "./store";

const {getState, setState, subscribe, getInitialState} = store;

riot.register('todos-list', App);

const mountApp = riot.component(App)
const app = mountApp(
    document.getElementById('root'),
    {
        title: 'Awesome Todos',
        todos: [
            { task: 'Avoid excessive caffeine', completed: true },
            { task: 'Be less provocative'  },
            { task: 'Be nice to people' }
        ]
    }
)