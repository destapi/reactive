import {onSubmitQuestion,} from "../store";

export default function QuestionForm({scrumId, screenName}) {
    return (
        <section className="pure-g">
            <form className="pure-form form-row" id="vote-form" onsubmit={onSubmitQuestion}>
                <div className="text-area">
                    <i aria-hidden="true" class="fa fa-eye-slash reveal"></i>
                    <i aria-hidden="true" className="fa fa-eye hide"></i>
                    <button type="submit">
                        <i aria-hidden="true" className="fa fa-arrow-circle-up send"></i>
                    </button>
                    <label>
                        <textarea name="question" className="pure-input-1" placeholder="Estimate task"></textarea>
                    </label>
                    <input type="hidden" name="scrumId" value={scrumId}/>
                    <input type="hidden" name="screenName" value={screenName}/>
                </div>
            </form>
        </section>
    );
}
