/* eslint-disable */
import React, { Component, PropTypes } from 'react';
import {Segment, Checkbox, Label} from 'semantic-ui-react';
import {setActualEditorStep, receiveIsPrivate, postNewChallenge} from '../actions/actions'

if (typeof document !== 'undefined') {
    const $ = require('jquery');
}

export default class PrivateCheckbox extends Component{

    setIsPrivate = (data: any) => {
        const {dispatch} = this.props;
        dispatch(receiveIsPrivate(data));
    };

    componentDidMount() {
        const context = this;
        $('#checkBoxLabelYes').click(function(e){
            const checkBoxes = $("input[class=hidden]");
            if(!checkBoxes.prop("checked")) {
                checkBoxes.prop("checked", true);
                context.changeIsPrivateByLabel(true);
            }
        });

        $('#checkBoxLabelNo').click(function(e){
            const checkBoxes = $("input[class=hidden]");
            if(checkBoxes.prop("checked")) {
                checkBoxes.prop("checked", false);
                context.changeIsPrivateByLabel(false);
            }
        });
    }

    changeIsPrivateByLabel = (data: any) => {
        const {dispatch, challengeId} = this.props;
        const jsonData = {};
        jsonData.challengeId = challengeId;
        jsonData.isPrivate = data.toString();
        dispatch(postNewChallenge(JSON.stringify(jsonData)));
        dispatch(setActualEditorStep('challengeAbstractNavItem', false));
    };

    changeIsPrivate = (event: Event, data: any) => {
        const {dispatch, challengeId} = this.props;
        const jsonData = {};
        jsonData.challengeId = challengeId;
        jsonData.isPrivate = data.checked.toString();
        dispatch(postNewChallenge(JSON.stringify(jsonData)));
        dispatch(setActualEditorStep('challengeAbstractNavItem', false));

    };

    render() {
        const {isPrivate} = this.props;
        return(
            <Segment raised>
                <Label> Is private challenge?</Label>
                <div>
                    <Label id="checkBoxLabelNo">No</Label>
                    {(isPrivate === undefined || isPrivate === 'false') &&
                        <Checkbox id="privateCheckbox" toggle onChange={this.changeIsPrivate}/>
                    }
                    {isPrivate === 'true' &&
                        <Checkbox id="privateCheckbox" checked toggle onChange={this.changeIsPrivate}/>
                    }

                    <Label id="checkBoxLabelYes">Yes</Label>
                </div>
            </Segment>)
    };
}

PrivateCheckbox.propTypes = {
    dispatch: PropTypes.func.isRequired,
    isPrivate: PropTypes.string,
    challengeId: PropTypes.string,
    counter: PropTypes.number,
};
