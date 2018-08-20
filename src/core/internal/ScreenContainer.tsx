import * as React from "react";
import {AppFormer} from "core/Components";
import JsBridge from "core/internal/JsBridge";



interface Props {
    root: { ss: AppFormer.Screen[], ps: AppFormer.Perspective[] }
    screen: AppFormer.Screen;
    onClose: () => void;
    bridge: JsBridge;
}



export default class ScreenContainer extends React.Component<Props, {}> {

    private ref: HTMLDivElement;


    componentDidMount(): void {
        const screen = this.props.screen;
        console.info(`...Rendering ${screen.af_componentId}...`);

        if (this.props.screen.isReact) {
            this.invokeOnOpen();
            return;
        }

        this.props.bridge.render(screen.af_componentRoot(this.props.root),
                                 this.ref,
                                 () => this.invokeOnOpen());
    }


    private invokeOnOpen() {
        console.info(`Rendered ${this.props.screen.af_componentId}`);
        console.info(`...Opening ${this.props.screen.af_componentId}...`);
        this.props.screen.af_onOpen();
        console.info(`Opened ${this.props.screen.af_componentId}.`);
    }


    componentWillUnmount(): void {
        const screen = this.props.screen;
        console.info(`...Closing ${screen.af_componentId}...`);
        screen.af_onClose();
        console.info(`Closed ${screen.af_componentId}.`);
    }


    render() {
        return <div className={"af-screen-container"}
                    onFocus={() => this.props.screen.af_onFocus()}
                    onBlur={() => this.props.screen.af_onLostFocus()}>

            {this.props.screen.af_componentTitle && <div className={"title"}>
                {this.TitleBar(this.props.screen)}
            </div>}

            {/*This is where the screens will be rendered on.*/}
            {/*If it is a ReactElement we can embedded it directly*/}
            {this.props.screen.isReact && this.props.screen.af_componentRoot(this.props.root)}

            {/*If not, we simply add a container div where the component will be rendered on */}
            {/*See: componentDidMount*/}
            {!this.props.screen.isReact && <div ref={e => this.ref = e!}>{/*Container*/}</div>}

        </div>;
    }


    private TitleBar(screen: AppFormer.Screen) {
        return <span>
            <span>{screen.af_componentTitle}</span>
            &nbsp;&nbsp;
            <a href="#" onClick={() => this.props.onClose()}>
                Close
            </a>
        </span>;
    }
}