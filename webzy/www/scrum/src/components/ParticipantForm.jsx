import {joinScrumByScrumId} from "../store";

export default function ParticipantForm() {
    return (
        <section className="pure-g">
            <form className="pure-form participant-form" onSubmit={joinScrumByScrumId}>
                <fieldset className="pure-group">
                    <label>
                        <input className="pure-input-1-2" name="screenName" placeholder="Screen Name" type="text"/>
                    </label>
                </fieldset>
                <fieldset className="pure-group">
                    <label>
                        <input className="pure-input-1-2" name="scrumId" placeholder="Scrum Id" type="text"/>
                    </label>
                </fieldset>
                <button className="pure-button pure-input-1-2 pure-button-primary" type="submit">Join</button>
            </form>
        </section>
    );
}
