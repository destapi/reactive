import {createStore} from 'zustand/vanilla';

export const store = createStore((set) => ({
    todos: [],
    initTodos: (todos) => set((state) => {
        return ({...state, todos})
    }),
    addTodo: (todo) => set((state) => {
        return ({...state, todos: [...state.todos, todo]})
    }),
    updateTodo: (todo) => set((state) => {
        return ({
            ...state, todos: state.todos.map(t => {
                if (t.id === todo.id) {
                    return todo
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
