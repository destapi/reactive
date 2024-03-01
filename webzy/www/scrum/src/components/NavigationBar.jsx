import {onToggleType} from "../store";
import {createSignal, Show} from 'solid-js';

function toggleTypeIcon(checked, setChecked, userType,) {
    return (
        <Show when={checked() === userType}
              fallback={<i className="fa fa-circle-o radio" aria-hidden="true"
                           onClick={() => onToggleType({target: {value: userType}})}></i>}
        >
            <i className="fa fa-check-circle radio" aria-hidden="true"></i>
        < /Show>)
}

// function toggleStickyIcon(remember, ) {
//     return (
//         <Show when={remember()}
//               fallback={<i className="fa fa-square-o check" aria-hidden="true"
//                            onClick={() => onToggleSticky({target: {value: remember()}})}></i>}
//         >
//             <i className="fa fa-check-square-o check" aria-hidden="true"
//                onClick={() => onToggleSticky({target: {value: remember()}})}></i>
//         < /Show>)
// }

export default function NavigationBar({userType, sticky}) {
    const {pathname} = window.location;
    const [checked, setChecked] = createSignal(pathname.includes("participant") ? "participant" : userType);
    // const [remember, setRemember] = createSignal(false);

    return (
        <nav className="navigation">
            <div className="logo"><i aria-hidden="true" className="fa fa-cogs"></i></div>
            <div className="title">Scrum's Up</div>
            <label className="pure-checkbox" htmlFor="organizer" onClick={() => setChecked("organizer")}>
                <a href="/">
                    {toggleTypeIcon(checked, setChecked, "organizer")}
                    Scrum Master</a>
            </label>
            <label className="pure-checkbox" htmlFor="participant" onClick={() => setChecked("participant")}>
                <a href="/participant">
                    {toggleTypeIcon(checked, setChecked, "participant")}
                    Participant</a>
            </label>

            {/*<label className="pure-checkbox" htmlFor="participant" onClick={() => setRemember(!remember())}>*/}
            {/*    {toggleStickyIcon(remember)} Sticky*/}
            {/*</label>*/}
            <div className="menu">
                <i aria-hidden="true" className="fa fa-github"></i>
            </div>
        </nav>
    );
}
