/* eslint-disable */
import React, { Component, PropTypes } from 'react';
import {setSelectedChallengeUsages, getChallengeUsage, setUsageFromDropDown, postNewChallenge, setActualEditorStep} from '../actions/actions';
import {Dropdown} from 'semantic-ui-react';

if (typeof document !== 'undefined') {
    const $ = require('jquery');
}

export default class ChallengeUsagesDropDown extends Component {
    state : {
        challengeUsage:string,
        challengeUsagesList: Array} = {challengeUsagesList: []};

    componentDidMount() {
        this.updateChallengeUsage();
    }

    updateChallengeUsage = () => {
        const {dispatch} = this.props;
        dispatch(getChallengeUsage());
    };

    createDropDownEntries= (challengeUsages) => {
        const options = [];

        if (challengeUsages !== undefined && challengeUsages.length > 0) {
            challengeUsages.map((usage) => {
                options.push({
                    'text': usage.usageName,
                    'value': usage.id
                });
            });
        }
        return options;
    };

    selectChallengeUsage = (event: Event, data: any) => {
        const {dispatch, challengeId, challengeUsages} = this.props;
        dispatch(setUsageFromDropDown(data.value));
        const jsonData = {};
        jsonData.challengeId = challengeId;
        jsonData.challengeUsages = data.value.toString();
        this.state.challengeUsagesList[this.state.challengeUsagesList.length] = challengeUsages[data.value.toString()-1];

        dispatch(postNewChallenge(JSON.stringify(jsonData)));
    };

    handleUsageBlur = (event: Event) => {
        const {dispatch, challengeId} = this.props;
        let value = '';
        const dropDown = $('input[class=search]');
        value = dropDown.prop('value');
        if(event.target instanceof HTMLInputElement) {
            dispatch(setUsageFromDropDown(value));
            const jsonData = {};
            jsonData.challengeId = challengeId;
            if(this.state !== undefined) {
                jsonData.challengeUsages = value;
                if(value !== '' && value !== undefined) {
                    dispatch(postNewChallenge(JSON.stringify(jsonData)));
                    dispatch(setActualEditorStep('challengePrivateNavItem', false));
                    this.state.challengeUsagesList[this.state.challengeUsagesList.length] = value;
                    dispatch(setSelectedChallengeUsages(this.state.challengeUsagesList));
                } else {
                    dispatch(setActualEditorStep('challengePrivateNavItem', false));
                    dispatch(setSelectedChallengeUsages(this.state.challengeUsagesList));
                }
            }
        }

        return true;
    };

    render() {
        const {challengeUsages,selectedChallengeUsages} = this.props;
        if(selectedChallengeUsages !== undefined && selectedChallengeUsages.length > 0) {
            this.state.challengeUsagesList = selectedChallengeUsages;
        }
        return (
            <div>
                <Dropdown
                    placeholder="Select Challenge Usage"
                    selection
                    search
                    onBlur={this.handleUsageBlur}
                    onChange={this.selectChallengeUsage}
                    options={this.createDropDownEntries(challengeUsages)}
                />
                <dl>
                {this.state.challengeUsagesList.length > 0 && this.state.challengeUsagesList.map(usage => {
                    return ( <div key={usage.id}>
                            <dt>{usage.usageName !== undefined ? usage.usageName: usage}</dt>
                            <hr></hr>
                        </div>
                    )
                })}
                </dl>
            </div>
        );
    }
}


ChallengeUsagesDropDown.propTypes = {
    dispatch: PropTypes.func.isRequired,
    challengeId: PropTypes.string,
    challengeUsages: PropTypes.array,
    selectedChallengeUsages: PropTypes.array
};
