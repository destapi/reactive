import {organizeNewScrum} from "../store";

export default function OrganizerForm() {
    return (
        <section className="pure-g">
            <form className="pure-form organizer-form" onSubmit={organizeNewScrum}>
                <fieldset className="pure-group">
                    <label>
                        <input className="pure-input-1-2" name="screenName" placeholder="Screen Name" type="text"/>
                    </label>
                </fieldset>
                <fieldset className="pure-group">
                    <label>
                        <input className="pure-input-1-2" name="scrumTitle" placeholder="Scrum Title" type="text"/>
                    </label>
                </fieldset>
                <fieldset className="pure-group">
                    <label>
                    <textarea className="pure-input-1-2" name="voteChoices"
                              placeholder="Add choices, comma-seperated or one line per choice"
                              value="1,2,3,4"></textarea>
                    </label>
                </fieldset>
                <button className="pure-button pure-input-1-2 pure-button-primary" type="submit">Organize</button>
            </form>
        </section>
    );
}
