import React, {Component, PropTypes} from 'react';
import {Segment, Message, Icon, Header, Button} from 'semantic-ui-react';
import SimpleInputField from './SimpleInputField';
import {postNewChallenge, setActualEditorStep} from '../actions/actions';

export default class GoldnuggetComponent extends Component {
    state = {
        activeStatic: false,
        activeDynamic: false,
        activeNone: false,
        staticGoldnugget: ''};

    componentWillMount = () => {
        const {goldnuggetType, staticGoldnuggetSecret} = this.props;
        if(goldnuggetType !== '' && !this.isGoldnuggetTypeStateSet()) {
            this.setGoldnuggetTypeState(goldnuggetType);
        }
        if(staticGoldnuggetSecret !== '' && this.state.staticGoldnugget === '') {
            this.setState({staticGoldnugget: staticGoldnuggetSecret});
        }
    }
    handleStaticClick = () => {
        this.setState({ activeStatic: !this.state.activeStatic });
        if(this.state.activeDynamic) {
            this.setState({ activeDynamic: !this.state.activeDynamic });
        }
        if(this.state.activeNone) {
            this.setState({ activeNone: !this.state.activeNone });
        }
    };

    handleDynamicClick = () => {
        this.setState({ activeDynamic: !this.state.activeDynamic });
        if(this.state.activeStatic) {
            this.setState({ activeStatic: !this.state.activeStatic });
        }
        if(this.state.activeNone) {
            this.setState({ activeNone: !this.state.activeNone });
        }
    };

    handleNoneClick = () => {
        this.setState({ activeNone: !this.state.activeNone });
        if(this.state.activeStatic) {
            this.setState({ activeStatic: !this.state.activeStatic });
        }
        if(this.state.activeDynamic) {
            this.setState({ activeDynamic: !this.state.activeDynamic });
        }
    };

    handleGoldnuggetChange = (event: Event) => {
        if(event.target instanceof HTMLInputElement) {
            this.setState({staticGoldnugget: event.target.value});
        }

        return true;
    };

    getActiveGoldnuggetType = () => {
        if(this.state.activeNone) {
            return 'none';
        }
        if(this.state.activeDynamic) {
            return 'dynamic';
        }
        if(this.state.activeStatic) {
            return 'static';
        }
        return '';
    };

    handleSubmitGoldnugget = (event, challengeId, dispatch) => {
        const {nextEditorStep} = this.props;
        const jsonData = {};
        jsonData.challengeId = challengeId;
        if(this.state !== undefined && this.state !== null) {
            const goldnuggetType = this.getActiveGoldnuggetType();
            jsonData.staticGoldnuggetSecret = this.state.staticGoldnugget;
            jsonData.goldnuggetType = goldnuggetType;
            if(goldnuggetType !== '') {
                dispatch(setActualEditorStep(nextEditorStep, false));
                dispatch(postNewChallenge(JSON.stringify(jsonData)));
            }
        }
    };

    handleSubmitClick = () => {
        const {challengeId, dispatch} = this.props;
        event.preventDefault();
        this.handleSubmitGoldnugget(event, challengeId, dispatch);
    };

    setGoldnuggetTypeState = (goldnuggetType) => {
        if(goldnuggetType === 'static') {
            this.setState({ activeStatic: true });
        } else if(goldnuggetType === 'dynamic') {
            this.setState({ activeDynamic: true });
        } else if(goldnuggetType === 'none') {
            this.setState({ activeNone: true });
        }
    };

    isGoldnuggetTypeStateSet = () => {
        return this.state.activeStatic || this.state.activeDynamic || this.state.activeNone;
    };

    render = () => {
        let enabled;
        if(this.state !== null && this.state !== undefined) {
            if(this.state.activeStatic && this.state.staticGoldnugget !== '') {
                enabled = true;
            } else enabled = this.state.activeDynamic || this.state.activeNone;
        }
        return (
            <Segment>
                <Header as="h5" attached="top">
                    Static Goldnugget
                </Header>
                <Segment secondary attached>
                    {!this.state.activeStatic &&
                        <Button toggle active={this.state.activeStatic} onClick={this.handleStaticClick}>
                            Select Static
                        </Button>
                    }
                    {this.state.activeStatic &&
                    <Button toggle active={this.state.activeStatic} onClick={this.handleStaticClick}>
                        Static Selected
                    </Button>
                    }
                    {this.state.activeStatic &&
                        <SimpleInputField key={'navItem1'} value={this.state.staticGoldnugget} className={"simpleInputField"} size={"huge"} id={"challengeGoldnugget"} reference={"one"} onChange={this.handleGoldnuggetChange} required placeholder={"Please insert static goldnugget value..."} name={"challengeTitle"} labelText={"Static Goldnugget"}/>
                    }
                </Segment>
                <Header as="h3" attached>
                    Dynamic
                </Header>
                <Message warning attached="bottom">
                    <Icon name="warning" />
                    Dynamic should only be used by experienced authors which can make their own resources
                </Message>
                <Segment secondary attached>
                    {!this.state.activeDynamic &&
                    <Button toggle active={this.state.activeDynamic} onClick={this.handleDynamicClick}>
                        Select Dynamic
                    </Button>
                    }
                    {this.state.activeDynamic &&
                    <Button toggle active={this.state.activeDynamic} onClick={this.handleDynamicClick}>
                        Dynamic Selected
                    </Button>
                    }
                </Segment>
                <Header as="h3" attached>
                    None
                </Header>
                <Segment secondary attached>
                    {!this.state.activeNone &&
                    <Button toggle active={this.state.activeNone} onClick={this.handleNoneClick}>
                        Select None
                    </Button>
                    }
                    {this.state.activeNone &&
                    <Button toggle active={this.state.activeNone} onClick={this.handleNoneClick}>
                        None Selected
                    </Button>
                    }
                </Segment>
                <Button primary fluid disabled={!enabled} id={'challengeTitleNavItemButton'} onClick={this.handleSubmitClick}>Submit</Button>
            </Segment>);
    }
}

GoldnuggetComponent.propTypes = {
    challengeId: PropTypes.string.isRequired,
    dispatch: PropTypes.func.isRequired,
    nextEditorStep: PropTypes.string.isRequired,
    staticGoldnuggetSecret: PropTypes.string,
    goldnuggetType: PropTypes.string.isRequired
};
