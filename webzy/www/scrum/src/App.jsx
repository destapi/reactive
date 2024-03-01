import NavigationBar from "./components/NavigationBar";
import {store} from "./store";

function App(props) {

    const {progress} = store.getState();

    return (
        <>
            <NavigationBar userType={progress.userType} sticky={progress.sticky}/>
            {props.children}
        </>
    );
}

export default App;
