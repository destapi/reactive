# Declarative State Change Transfer (DeSt)

## PART I - The Specification

Software Engineering is very much a science because it is built upon principles from mathematics, algorithms and abstract data types. 
Software Architecture however, is more of an art than it is a science, and this skill only gets sharper with repeated problem-solving, 
experience and passage of time. The important takeaway here is that having refined skills in software architecture helps in identifying 
solutions and patterns in places where a casual observer or a novice apprentice would fail to easily make those subtle connections. This 
perspective of software architecture becomes even more significant when you have to deal with problems in a domain where creating any 
sound solutions requires introducing non-trivial complexities, since any other seemingly plausible solutions would be either too naive
or would quickly fall apart. The architectural choices for such solutions involves making design decisions that produce the least amount
of "hurt", and require continually reevaluating and moving the goal posts to find that sweet spot.

Consider the case of communication between processes in disparate computers. There are different techniques that have been developed 
over time to solve this problem, and the over-arching design is generally known as the client-server architecture. In this architecture, 
any two computers are physically seperated from each other, and the only link between them is an ethernet connection and the TCP/UDP 
communication protocol for exchanging data. This solution, albeit universally adopted and successful, also has significant challenges 
that it has to overcome, and these challenges are very well articulated in the [CAP theorem](https://en.wikipedia.org/wiki/CAP_theorem). 

The client-server architecture addresses the problem of shuffling data between two processes across a network. However, it does not 
concern itself with making sense of what that data it. This concern is addressed in protocols higher up the TCP/IP protocol stack. 
Originally, the RPC protocol was developed and used to describe the semantics around the transmission of data between a caller and 
the callee. Following its success, more ambitious and rigid protocols like SOAP were developed to introduce a more formal nomenclature
for the data semantics, with nouns such as SOAP Envelop, Headers and Body. Despite its king-size promise, the bloat of SOAP eventually
led to its demise, with more lightweight architectural styles like REST, GraphQL and gRPC stealing the show. Why are there different
styles? Are they all trying to solve the same problem? Have they been successful in meeting their objectives? These are all questions
that may not necessarily preoccupy everyone, but are nonetheless important to reflect upon to understand the "now" and also the 
"what is next". __DeSt__ is a fresh look at the same problem of data and a proposal for the semantics of the structure and transmission 
of this data.

## Lightweight architectural styles

To understand a problem thoroughly, it is important to tailor a solution that attacks each salient issues explicitly, and to do so 
imperatively so that "no stone is left unturned". It's only from creating such an exhaustive solution that finer conclusions can 
then start to be drawn and patterns can also start to emerge. This is the gift which RPC gave to the computing universe - an 
iron-clad formalization for the exchange of data between two processes on different computers. But in the present day, there are 
more ubiquitous architectural styles which will be the target of scrutiny here. In the discussions below, there are two salient 
threads under scrutiny at all times, which form the guiding principles
1. The mechanics of data transfer
2. The treatment of the data in transit

### REST (REpresentational State Transfer)

REST is an architectural style which leans heavily on the already existing semantics of the web (HTTP protocol) to describe the 
mechanics of how data transmission happens between two computers (HTTP methods) and what shape the data in transit takes. REST
is predominantly used alongside JSON as the structural format for the data in transit, although it may be used with other formats 
like XML. REST rose to prominence around the time when the web was heavily saddled and weighed down by SOAP, especially in the 
enterprise space. The goals of SOAP may have been noble, but the implementation details were a new kind of hell. REST stripped 
out all the baggage associated with describing the data payload and transmission mechanics and added _nothing_ new to what the 
HTTP protocol already provided. REST simply formalized the HTTP nomenclature to describe the mechanics of transmitting the data 
and the semantics of the data format while in transit. It was an instant hit, and very deservedly so. 

However, with REST, signs of discomfort will quickly begin to manifest as the size of the backend data grows bigger, and as 
relationships existing in this data continue to increase in their complexity (level of coupling). This phenomenon quickly begins 
to push on the limitations of REST, where requesting one piece of data may require making many more requests for other data 
before the actual target is finally discovered and retrieved. This can quickly eat up the network bandwidth by transmitting 
data which the client does not need at all and will inevitably, and almost always, severely degrade performance.

### gRPC (Google RPC)

There are other variations of RPC, but for this discussion, gRPC will suffice. gRPC pivots back to the traditional RPC way of 
describing intent using verbs, but it takes a sharp turn in how it treats the data in transit. Since it does not rely on the 
HTTP protocol, it is not restricted to the same rules as say, _REST_. It defines the mechanics of transmitting data (ProtoBuff), 
which a client must be familiar with to successfully decode and use this data. The data is also aggressively compressed and very 
efficiently transmitted, which makes gRPC an ideal architectural style for service-to-service API that do not require an
HTTP interface.

### GraphQL

I recall some time back before GraphQL was even a thing, I was working on a Java REST API service having a Node.js frontend client.
At the time, the plethora of tools that exist today which take care of a lot of mundane tasks like testing, did not exist. One of 
the hacks I used was to define all the inputs (method, headers and payload) and expected outputs for a particular REST endpoint 
in JSON format and then saving these definitions in a file. To automate testing, I would read that definitions file using Node.js, 
create a _fetch POST request_, and sent it off to a Java test HTTP client on the server side. The client would then parse the request 
definition, create the corresponding Java HTTP Request, interact with the Java backend services, get results and then return these 
results as a JSON blob to the front end test client. The Node.js test client would then proceed to assert the expected output against 
the definitions in the JSON file. When I look back, I can clearly recognize that the automation hack bears a lot of similarities with
what GraphQL does today, at least in terms of getting any abstract data payload to the backend by way of using just a single HTTP 
endpoint.

Anyway, GraphQL was conceived in the crucibles of Facebook and a specification was formalized and open-source soo thereafter. 
This specification has reference implementations in multiple programming languages and has found massive success in some niche 
use cases. The motivation for GraphQL was primarily to overcome the pain points manifested by the REST architectural style, 
especially the marshalling around of unused data and the unnecessary round trips to the server to fetch more of it. Since the 
largest proportion of data consumers on Facebook were handheld devices, the REST architectural style was a huge turn off. With 
GraphQL, the backend data could now easily be organized in a Graph format which made it easier to navigate and retrieve data 
without making the unnecessary round trips that are ubiquitous with REST. Although GraphQL has solved a lot of the REST problems, 
and despite having a very attractive value proposition, it nonetheless comes with the high risk of an inefficient backend, and 
it also has a significantly steeper learning curve. For most businesses, the additional overhead of adopting GraphQL is not 
always worth the cost of abandoning RESTful APIs, so these two paradigms are always dueling it out in public all the time.

### LiveView

This is another very interesting paradigm which has found great success within the Erlang family of products. The basic tenet is 
that the initial page load will perform a full HTTP GET request, and upon loading in the client browser, the client application
will establish a websocket connection where all subsequence exchange of data will happen. This is actually a fantastic value
proposition, especially if already invested in Erlang-based products. With Erlang having been conceived in the crucibles of 
telecoms, it is not inconceivable by any fetch of imagination, to have a single Erlang server handling connections to the 
millions. So the medium of transmission is Websockets, and the response is in the form of markup (HTML) that is targeted to 
the parts of the UI which require updating.  

LiveView has also been ported to other programming languages like JavaScript, but I don't know how well Javascript can be
able to pull off what Erlang can do. The idea however is excellent

### DeSt

_DeSt_ can be best summarized as an exercise of detecting what pieces of the backend data have changed, isolating these changes 
to create a payload, transmitting the payload to the frontend as an event, and then using JavaScript in the UI to make precision 
updates in only the places where a change represented in the payload data would produce a side effect. It's like having a "laser 
guided data-missile" launched from the backend and its effect is a "very targeted UI update", of course the difference being 
that the "guiding" is done using a "json-path" syntax, something akin to _x-path_ in XML" 

This kind of exercise is already commonplace with frontend frameworks like React, SolidJS, RiotJS, BackboneJS etc., when dealing 
with client-side state. By using a suitable "state-management library", these UI frameworks are able to create a very interactive 
user experience through precision updates to the UI. 

Extending this paradigm to the backend would however require some more serious considerations.
1. Asynchronicity - the request-response model, where one request is fulfilled by one response, would not work here. The idea 
is that a request should dispatch a description of a change that needs to be made in the backend, and the return immediately.
The server will then process the request asynchronously and updating its backed JSON data model, the data model itself will
detect the change, it will calculate the json-path to the locality of the data change, it will identify which output writer
it needs to publish the change to, and write an event describing the data change there. Upon receiving this event, the client 
will its mirror image of the backend data model. This change in the frontend data model should then be picked up by the UI
framework (this is where SolidJS really shines) and produce a side effect in the UI.
2. Transmission medium - since the request is decoupled from the response, the only two viable candidates here are Websockets
and Server Sent Events. In addition, since all interaction with the backend is through an HTTP fetch request which returns 
immediately with only a status (200, 201) code, the best candidate is therefore using Server Sent Events. This approach may 
have had issues prior to HTTP/2.0, but these issues have been implicitly addressed through the evolution of the HTTP spec. 
3. Symmetry - all allow automatic update of the client-side data to reflect the server-side data, it is important to keep the 
data on both sides consistent. The server-side can occasionally offload the backend data into a JSON repository temporarily
(like using a JDBC session handler) or for a longer term basis (like using a JSON database). When a client starts, it should 
use this cached data to initialize itself, Using http event sourcing has the added benefit that SSE will automatically try to 
help with verifying the last update was successfully received by the client using a last id approach. 
4. Json-Path - this is a path which is traced from the object where the change happened, all the way up through the _parent()_
chain till the root node. Since the JSON structure is a tree data structure, it automatically is also a directed acyclic 
graph which converges at a single root node 
5. Reactive JSON store - all this is only possible if the JSON store is build to detect changes and communicate these changes
to a listener.

## Json-Path data change specification

The interface is a collection of default methods which means that providing a custom implementation is not necessary to be able
to use this interface. However, this is the right place to implement custom behavior where calls can be dispatched to connected 
clients via the writers configured in the _Observer_.

```java
/**
 * Observer contains method for accepting events around changes in data for to Object of type JNode. Objects of this type
 * (JArray and JObject) are 'reactive aware', meaning that they are able to know when any attribute inside themselves is
 * changed, either through addition, updating or removal. This capability can be harnessed to using a concrete representation
 * of the Observer, and should be writen to any concrete Writer of interest
 */
public interface Observer {

    /**
     * applies to jarray
     *
     * @param target represents the root of the JSON object whose data is being changed
     * @param path represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param value the value that was added to the array
     */
    default void add(Object target, String path, Object value) {
    }

    /**
     * applies to jobject
     *
     * @param target represents the root of the JSON object whose data is being changed
     * @param path represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param key key used to identify a value in a dictionary
     * @param oldValue  the value identified by the key 'key' in the dictionary before the update
     * @param newValue  the new value that will be identified by the key 'key' in the dictionary
     */
    default void set(Object target, String path, String key, Object oldValue, Object newValue) {
    }

    /**
     * applies to jarray
     *
     * @param target represents the root of the JSON object whose data is being changed
     * @param path represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param index ordinal position of the value in the array
     * @param value value in an array that has been accessed
     */
    default void get(Object target, String path, int index, Object value) {
    }

    /**
     * applies to jobject
     *
     * @param target represents the root of the JSON object whose data is being changed
     * @param path represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param key key used to identify a value in a dictionary
     * @param value value in the dictionary that has been accessed
     */
    default void get(Object target, String path, String key, Object value) {
    }

    /**
     * applies to jarray
     *
     * @param target represents the root of the JSON object whose data is being changed
     * @param path represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param index ordinal position of the value in the array
     * @param value value in the array that has been removed
     */
    default void delete(Object target, String path, int index, Object value) {
    }

    /**
     * applies to jobject
     *
     * @param target represents the root of the JSON object whose data is being changed
     * @param path represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param key key used to identify a value in a dictionary
     * @param value value in the dictionary that has been removed
     */
    default void delete(Object target, String path, String key, Object value) {
    }

    /**
     * applies to jarray - removal of an array using predicate function
     *
     * @param target represents the root of the JSON object whose data is being changed
     * @param path represents the 'json-path' string which follows the path from the element being changed up to the target or root node.
     * @param value a predicate function used to search for a target value for deletion in the array
     */
    default void delete(Object target, String path, Object value) {
    }

    /**
     *
     * @param target  unique identifier associated with the root resource undergoing modification
     * @param data  the stringified version of the change data payload
     */
    default void write(String target, String data) {
    }

    /**
     *
     * @param target unique identifier associated with the root resource undergoing modification
     * @param event  name of event to be associated with the write operation
     * @param data  the stringified version of the change data payload
     */
    default void write(String target, String event, String data) {
    }
}

```

## PART II: Reference implementation

Coming soon!