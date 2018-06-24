import React, { Component, PropTypes } from 'react';
import {setActualEditorStep, postNewChallenge, getChallengeLevel} from '../actions/actions';
import {Dropdown} from 'semantic-ui-react';

export default class ChallengeLevelDropDown extends Component {
    componentDidMount() {
        this.updateChallengeLevel();
    }

    updateChallengeLevel = () => {
        const {dispatch} = this.props;
        dispatch(getChallengeLevel());
    };

    createDropDownEntries = (challengeLevelList) => {
        const options = [];

        if (challengeLevelList !== undefined && challengeLevelList.length > 0) {
            challengeLevelList.map((level) => {
                options.push({
                    'text': level.text,
                    'value': level.id
                });
            });
        }
        return options;
    };

    getIndexOfLevelText = (challengeLevelList, value) => {
        let index = 0;
        if (challengeLevelList !== undefined && challengeLevelList.length > 0) {
            while(challengeLevelList.length > index) {
                if(value === challengeLevelList[index].text) {
                    return challengeLevelList[index].id;
                }
                index++;
            }
        }
        return -1;
    };

    selectChallengeLevel = (event: Event, data: any) => {
        const {dispatch, challengeId} = this.props;
        const jsonData = {};
        jsonData.challengeId = challengeId;
        jsonData.challengeLevel = data.value.toString();
        dispatch(postNewChallenge(JSON.stringify(jsonData)));
        dispatch(setActualEditorStep('challengeCategoryNavItem', false));
    };

    render() {
        const {challengeLevels, value} = this.props;
        let index = '';

        if(value !== '') {
            index = this.getIndexOfLevelText(challengeLevels, value);
        }
        return (
            <Dropdown
                placeholder="Select Challenge Level"
                selection
                search
                onChange={this.selectChallengeLevel}
                options={this.createDropDownEntries(challengeLevels)}
                value={index}
            />

        );
    }
}


ChallengeLevelDropDown.propTypes = {
    dispatch: PropTypes.func.isRequired,
    challengeId: PropTypes.string,
    challengeLevels: PropTypes.array,
    value: PropTypes.string
};
