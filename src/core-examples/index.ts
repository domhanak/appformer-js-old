import {FirstScreen} from "core-screens/Screens"
import * as ReactDOM from "react-dom";

ReactDOM.render(new FirstScreen().af_componentRoot(), document.getElementById("appformer-react-example-root"));

(<any>window).$goToPlace = (path : string) => alert(`Go to ${path} called!`);
