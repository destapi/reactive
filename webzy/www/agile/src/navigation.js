export default function NavigationBar({toggleUserType}){

    return (`<template id="navigation-template">
            <nav class="navigation">
                <div class="logo"><i aria-hidden="true" class="fa fa-cogs"></i></div>
                <div class="title">Scrum's Up</div>
                <label class="pure-checkbox" for="organizer">
                    <input checked id="organizer" name="user-type" type="radio"
                           value="organizer" onclick="toggleUserType() "/> Scrum Master
                </label>
                <label class="pure-checkbox" for="participant">
                    <input id="participant" name="user-type" type="radio" value="participant" onclick="toggleUserType()" />
                    Participant
                </label>
                <div class="menu">
                    <i aria-hidden="true" class="fa fa-github"></i>
                </div>
            </nav>
        </template>`);
}