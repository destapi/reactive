import {lazy} from "solid-js";
import {render} from "solid-js/web";
import {Route, Router} from "@solidjs/router";
import App from './App';

const root = document.getElementById('root');

if (import.meta.env.DEV && !(root instanceof HTMLElement)) {
    throw new Error(
        'Root element not found. Did you forget to add it to your index.html? Or maybe the id attribute got misspelled?',
    );
}

const ParticipantPage = lazy(() => import("./components/ParticipantPage"));
const OrganizerPage = lazy(() => import("./components/OrganizerPage"));

render(() => (
    <Router root={App}>
        <Route path="/participant" component={ParticipantPage}/>
        <Route path="/" component={OrganizerPage}/>
    </Router>
), root);
