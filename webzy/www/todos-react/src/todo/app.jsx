import { Header } from "./components/header";
import { Main } from "./components/main";
import { Footer } from "./components/footer";

import "./app.css";
import {startEventSource, todoReducer} from "./store";
import {useEffect, useReducer} from "react";

export function App() {

    const [todos, dispatch] = useReducer(todoReducer, []);

    useEffect(() => {
        startEventSource(todos, dispatch)
    }, []);

    return (
        <>
            <Header dispatch={dispatch} />
            <Main todos={todos} dispatch={dispatch} />
            <Footer todos={todos} dispatch={dispatch} />
        </>
    );
}
