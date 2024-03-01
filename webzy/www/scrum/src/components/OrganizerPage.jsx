import OrganizerForm from "./OrganizerForm";
import QuestionForm from "./QuestionForm";
import VotingProgress from "./VotingProgress";
import {store} from "../store";
import {createEffect, createSignal} from "solid-js";

export default function OrganizerPage() {
    const {scrum: {scrumId, member}} = store.getState();

    const [screenName, setScreenName] = createSignal(member.screenName)
    createEffect(() => {
        store.subscribe((state) => {
            if (state.scrum.member?.screenName !== screenName()) {
                setScreenName(state.scrum.member?.screenName)
            }
        })
    })

    return (
        <>
            {!screenName() && <OrganizerForm />}
            {screenName() && <QuestionForm screenName={screenName} scrumId={scrumId}/>}
            {screenName() && <VotingProgress/>}
        </>
    )
}
