import {
    createScrumAction,
    emptyUser,
    initialScrum,
    joinScrumAction,
    onToggleStickyAction,
    onToggleUserAction,
    sendQuestionAction,
    startEventSource,
    store,
    submitVoteAction,
} from './store';

import * as riot from 'riot';
import AppLandingPage from '../riot/app-landing-page.riot';
import OrganizerPage from "../riot/organizer-page.riot";
import ParticipantPage from "../riot/participant-page.riot";
import OrganizerForm from "../riot/organizer-form.riot";
import NavigationBar from "../riot/navigation-bar.riot";
import ParticipantForm from '../riot/participant-form.riot';
import ChoicesAvailable from '../riot/choices-available.riot';
import VoteQuestionForm from '../riot/vote-question-form.riot';
import VotingInProgress from '../riot/voting-in-progress.riot';

const {getState, setState, subscribe, getInitialState} = store;

riot.register('navigation-bar', NavigationBar);
riot.register('organizer-page', OrganizerPage);
riot.register('organizer-form', OrganizerForm);
riot.register('vote-question-form', VoteQuestionForm);
riot.register('voting-in-progress', VotingInProgress);
riot.register('participant-page', ParticipantPage);
riot.register('participant-form', ParticipantForm);
riot.register('choices-available', ChoicesAvailable);
riot.register('app-landing-page', AppLandingPage);

const mountApp = riot.component(AppLandingPage)
const app = mountApp(
    document.getElementById('root'),
    {
        getState,
        setState,
        subscribe,
        emptyUser,
        initialScrum,
        joinScrumAction,
        submitVoteAction,
        startEventSource,
        createScrumAction,
        sendQuestionAction,
        onToggleUserAction,
        onToggleStickyAction,
    }
)