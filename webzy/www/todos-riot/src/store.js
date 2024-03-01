import {createStore} from 'zustand/vanilla';

export const store = createStore((set) => ({
    todoList: {listId: '', listName: '', listOwner: '', members: {}, todos: []},
    initTodos: ({listId, listName, listOwner, todos}) => set((state) => {
        return ({...state, todoList: {...state.todoList, todos, listId, listName, listOwner}})
    }),
    addTodo: (todo) => set((state) => {
        return ({...state, todoList: {...state.todoList, todos: [...state.todoList.todos, todo]}})
    }),
    updateTodo: (todo) => set((state) => {
        return ({
            ...state, todoList: {...state.todoList, todos: state.todoList.todos.map(t => {
                if (t.id === todo.id) {
                    return todo
                }
                return t;
            })
        }})
    }),
    removeTodo: (id) => set((state) => {
        return ({
            ...state, todoList: {...state.todoList, todos: state.todoList.todos.filter(t => t.id !== id)
        }})
    }),
}));
