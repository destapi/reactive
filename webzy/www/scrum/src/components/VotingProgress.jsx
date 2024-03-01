import {removeMemberAction, store} from "../store";
import {createEffect, createSignal} from "solid-js";

function MemberVote({scrumId, progress, choice, screenName}) {

    const [current, setCurrent] = createSignal(choice);
    const [revealed, setRevealed] = createSignal(progress?.revealed);

    createEffect(() => {
        store.subscribe((state) => {
            let voting = Object.values(state.scrum.voting);
            for (let currVote of voting) {
                if (voting.some(v => v.screenName === screenName && v.choice !== current())) {
                    setCurrent(currVote.choice)
                    break;
                }
            }
        })
    })

    return (
        <article className="pure-u-1-4">
            <div className="title-bar">
                <p className="screenName">{screenName}</p>
                <p className="controls"><i className="fa fa-times-circle" aria-hidden="true"
                                           onClick={() => removeMemberAction({scrumId, screenName})}></i></p>
            </div>
            <p className="vote">
                {!revealed() && !current() && <i className="fa fa-question-circle-o" aria-hidden="true"></i>}
                {!revealed() && current() && <i class="fa fa-check" aria-hidden="true"></i>}
                {revealed() && current()}
            </p>
        </article>
    )
}

export default function VotingProgress() {

    const {scrum, progress} = store.getState();

    const [voting, setVoting] = createSignal(scrum.voting);

    createEffect(() => {
        store.subscribe((state) => {
            if (Object.keys(voting()).length !== Object.keys(state.scrum.voting).length) {
                setVoting(state.scrum.voting)
            }
        })
    })

    return (
        <section className="flex-row" id="voting-progress">
            {Object.entries(voting()).map(([_, currVote]) => (
                <MemberVote scrumId={scrum.scrumId} progress={progress} {...currVote} />
            ))}
        </section>
    );
}
