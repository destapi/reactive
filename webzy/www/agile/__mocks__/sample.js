const toggleColor = (e) => {
    const color = e.target.style.backgroundColor;
    e.target.style.backgroundColor = color === 'red' ? 'green' : 'red';
}

function render0() {
    const markup = `<div><button style="background-color: orange">Click</button></div>`
    const fragment = document.createRange().createContextualFragment(markup);
    document.getElementById("root").appendChild(fragment.firstChild);
    document.querySelector("button").addEventListener('click', toggleColor);
}

render0()

function render1() {
    const markup = `<div><button style="background-color: aqua" onclick="${toggleColor}">Click</button></div>`
    const fragment = document.createRange().createContextualFragment(markup);
    document.getElementById("root").appendChild(fragment.firstChild);
}

render1()

const parser = new DOMParser();

function render2() {
    const markup = `<div><button style="background-color: red" onclick="${toggleColor}">Click</button></div>`
    const doc = parser.parseFromString(markup, "text/html");
    document.getElementById("root").appendChild(doc.body.firstChild);
}

render2()

function render3() {
    const markup = `<div><button style="background-color: yellowgreen" onclick="${toggleColor}">Click</button></div>`
    const template = document.createElement("template");
    template.innerHTML = markup.trim();
    document.getElementById("root").appendChild(template.content.firstChild);
}

render3()

function render4() {
    const markup = `
        <template>
            <div><button style="background-color: chocolate" onclick="${toggleColor}">Click</button></div>
        </template>`;
    const fragment = document.createRange().createContextualFragment(markup.trim());
    document.getElementById("root").appendChild(fragment.firstChild.content.childNodes[1]);
}

render4()