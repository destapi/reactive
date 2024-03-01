import {onSelectChoice, store} from "../store";
import {createEffect, createSignal} from "solid-js";

export default function ChoicesAvailable() {

    const {scrum, progress} = store.getState();

    const [choice, setChoice] = createSignal(progress.vote?.choice)
    const [choices, setChoices] = createSignal(scrum.choices)
    createEffect(() => {
        store.subscribe((state) => {
            if (progress.vote?.choice !== choice()) {
                setChoice(progress.vote?.choice)
            }
            if (state.scrum.choices !== choices()) {
                setChoices(state.scrum.choices)
            }
        })
    })

    return (
        <section className="flex-row" id="submitting-choice">
            {choices().map(value => (
                <article className="pure-u-1-4" classList={{'selected-choice': value === choice()}}>
                    <p className="choice" onClick={() => onSelectChoice(value)}>{value}</p>
                </article>
            ))}
        </section>
    );
}
