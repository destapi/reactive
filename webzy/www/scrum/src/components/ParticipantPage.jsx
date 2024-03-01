import ParticipantForm from "./ParticipantForm";
import ChoicesAvailable from "./ChoicesAvailable";
import {createEffect, createSignal} from "solid-js";
import {store} from "../store";

export default function ParticipantPage() {

    const {scrum, progress} = store.getState();

    const [started, setStarted] = createSignal(progress.started)
    const [screenName, setScreenName] = createSignal(scrum.member?.screenName)
    createEffect(() => {
        store.subscribe((state) => {
            if (state.progress.started !== started()) {
                setStarted(state.progress.started)
            }
            if (state.scrum.member?.screenName !== screenName()) {
                setScreenName(state.scrum.member?.screenName)
            }
        })
    })

    return (
        <>
            {!started() && <ParticipantForm/>}
            {screenName() && <ChoicesAvailable/>}
        </>
    );
}
