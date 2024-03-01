import { Input } from "./input";

import { ADD_ITEM } from "../constants";
import {addItemAction} from "../store";

export function Header() {
    const addItem = (title) => addItemAction({ type: ADD_ITEM, payload: { title } });

    return (
        <header className="header" data-testid="header">
            <h1>todos</h1>
            <Input onSubmit={addItem} label="New Todo Input" placeholder="What needs to be done?" />
        </header>
    );
}
