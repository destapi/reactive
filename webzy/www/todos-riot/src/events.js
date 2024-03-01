function startEventSource() {

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
}

// startEventSource();

// startWebSocket();

function startWebSocket() {
    // Create a WebSocket connection.
    const socket = new WebSocket("ws://localhost:9080/events/ws/10", "protocolOne");
    const ping_interval = 90 * 1000; // it's in milliseconds, which equals to 2 minutes
    let interval;

    // Connection opened
    socket.addEventListener("open", (event) => {
        socket.send(JSON.stringify({message: "Hello Server!"}));
        const ping = JSON.stringify({"ping": 1});

        interval = setInterval(() => {
            socket.send(ping);
        }, ping_interval);

        //attach submit listener for form
        document.querySelector("#data-form").addEventListener('submit', e => {
            e.preventDefault();
            socket.send(JSON.stringify({"message": e.currentTarget.name.value}));
            e.currentTarget.name.value = "";
        });
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

        // dispose interval
        clearInterval(interval);
    });
}

function toggleForm(e) {
    document.getElementById("data-form").classList.toggle('hidden')
    let text = e.innerHTML;
    e.innerHTML = text.startsWith("Show") ? "Hide WS Form" : "Show WS Form";
}