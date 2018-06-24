import React, { Component, PropTypes } from 'react';
import {setActualEditorStep, postNewChallenge, getChallengeType} from '../actions/actions';
import {Dropdown} from 'semantic-ui-react';

export default class ChallengeTypeDropDown extends Component {
    componentDidMount() {
        this.updateChallengeType();
    }

    updateChallengeType = () => {
        const {dispatch} = this.props;
        dispatch(getChallengeType());
    };

    createDropDownEntries = (challengeTypeList) => {
        const options = [];
        let index = 0;
        if (challengeTypeList !== undefined && challengeTypeList.length > 0) {
            challengeTypeList.map((type) => {
                options.push({
                    'text': type.text,
                    'value': index
                });
                index++;
            });
        }
        return options;
    };

    getIndexOfTypeText = (challengeTypes, value) => {
        let index = 0;
        if (challengeTypes !== undefined && challengeTypes.length > 0) {
            while(challengeTypes.length > index) {
                if(value === challengeTypes[index].text) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    };

    selectChallengeType = (event: Event, data: any) => {
        const {dispatch, challengeId} = this.props;
        const jsonData = {};
        jsonData.challengeId = challengeId;
        const typeObject = data.options[data.value];
        if(typeObject !== undefined) {
            jsonData.challengeType = typeObject.text;
        }
        dispatch(postNewChallenge(JSON.stringify(jsonData)));
        dispatch(setActualEditorStep('challengeLevelNavItem', false));
    };

    render() {
        const {challengeTypes, value} = this.props;
        let index = '';

        if(value !== '' && value !== 'null') {
            index = this.getIndexOfTypeText(challengeTypes, value);
        }

        return (
            <Dropdown
                placeholder="Select Challenge Type"
                selection
                search
                onChange={this.selectChallengeType}
                options={this.createDropDownEntries(challengeTypes)}
                value={index}
            />

        );
    }
}


ChallengeTypeDropDown.propTypes = {
    dispatch: PropTypes.func.isRequired,
    challengeId: PropTypes.string,
    challengeTypes: PropTypes.array,
    value: PropTypes.string
};
