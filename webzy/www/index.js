const evtSource = new EventSource("/sse", {
    withCredentials: false
});

evtSource.onmessage = (event) => {
    addMessageLine(event);
    console.log(evtSource.readyState)
};

evtSource.addEventListener('connected', event => {
    addMessageLine(event);
    console.log(evtSource.readyState)
});

evtSource.addEventListener('set', event => {
    addMessageLine(event);
    console.log(evtSource.readyState)
});

evtSource.addEventListener('get', event => {
    addMessageLine(event);
    console.log(evtSource.readyState)
});

evtSource.addEventListener('delete', event => {
    addMessageLine(event);
    console.log(evtSource.readyState)
});

evtSource.onerror = function (e) {
    addMessageLine({data: "figure out why this error happened"})
    console.log(evtSource.readyState)
};

// fetch("/data")

// startWebsocket();

function toggleForm() {
    document.getElementById("data-form").classList.toggle('hidden')
}

function addMessageLine(event) {
    const newElement = document.createElement("li");
    const eventList = document.getElementById("list");

    newElement.textContent = `message: ${event.data}`;
    eventList.appendChild(newElement);
}

function startWebsocket() {
    // Create a WebSocket connection.
    const socket = new WebSocket(
        "ws://localhost:9080/events",
        "protocolOne",);

    // Connection opened
    socket.addEventListener("open", (event) => {
        socket.send("Hello Server!");
        addMessageLine(event)
    });

    // Listen for messages
    socket.addEventListener("message", (event) => {
        console.log("Message from server ", event.data);
        addMessageLine(event)
    });

    socket.addEventListener("error", (event) => {
        console.log("error occurred with the socket ", event)
        addMessageLine(event)
    });

    socket.addEventListener("close", (event) => {
        console.log("socket has been closed by the server ", event)
        addMessageLine(event)
    });
}