import {createStore} from 'zustand/vanilla';
import {v4 as uuidv4} from 'uuid';

export const store = createStore((set) => ({
    todos: [],
    addTodo: (task) => set((state) => {
        return ({...state, todos: [...state.todos, {id: uuidv4(), task, completed: false}]})
    }),
    editTodo: (id, task) => set((state) => {
        return ({
            ...state, todos: state.todos.map(t => {
                if (i.id === id) {
                    return ({...t, task})
                }
                return t;
            })
        })
    }),
    toggleDone: (id) => set((state) => {
        return ({
            ...state, todos: state.todos.map(t => {
                if (i.id === id) {
                    return ({...t, completed: !t.completed})
                }
                return t;
            })
        })
    }),
    removeTodo: (id) => set((state) => {
        return ({
            ...state, todos: state.todos.filter(t => t.id !== id)
        })
    }),
}));
