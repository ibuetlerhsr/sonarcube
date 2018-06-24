import React, { Component, PropTypes } from 'react';
import {Segment} from 'semantic-ui-react';
import {setActualEditorStep} from '../actions/actions';

import SimpleNavigationItem from '../components/SimpleNavigationItem';

export default class EditorNavigation extends Component {

    handleClick = (event: Event) => {
        const {dispatch} = this.props;
        if(event.target instanceof HTMLAnchorElement) {
            dispatch(setActualEditorStep(event.target.id, false));
        }

        return true;
    };


    createNavigatorMap = (index, arrayLength, alreadyFilledFields) => {
        const navigatorMap = new Array(arrayLength).fill(true);
        let i = index + 1;
        while(i < arrayLength) {
            if(alreadyFilledFields !== null || alreadyFilledFields !== undefined) {
                navigatorMap[i] = alreadyFilledFields[i];
            } else {
                navigatorMap[i] = false;
            }
            i++;
        }

        return navigatorMap;
    };

    render() {
        const {actualEditorStep, alreadyFilledFields} = this.props;
        const editorStep = ['challengeNameNavItem', 'challengeTitleNavItem', 'challengeTypeNavItem', 'challengeLevelNavItem', 'challengeUsageNavItem', 'challengeCategoryNavItem', 'challengeKeywordNavItem', 'challengePrivateNavItem', 'challengeAbstractNavItem', 'challengeStepNavItem', 'challengeGoldnuggetNavItem', 'challengeSolutionNavItem', 'challengeSummaryNavItem'];
        const stepText = ['Name', 'Title', 'Type', 'Level', 'Usages', 'Categories', 'Keywords', 'Is Private', 'Abstract', 'Sections', 'Goldnugget', 'Teacher Grading', 'Summary'];
        const navigatorMap = this.createNavigatorMap(editorStep.indexOf(actualEditorStep), editorStep.length, alreadyFilledFields);

        return (
            <div>
                <Segment raised>
                    {editorStep.map((id, index) => {
                        if(navigatorMap[index] === true) {
                            return (<SimpleNavigationItem key={index} size={"huge"} active={navigatorMap[index]}
                                                          tag={actualEditorStep === id.toString()} id={id.toString()}
                                                          onClick={this.handleClick} name={id.toString()}
                                                          labelText={stepText[index].toString()}/>);
                        }
                        return null;
                    })}
                </Segment>
            </div>
        );
    }

}

EditorNavigation.propTypes = {
    dispatch: PropTypes.func.isRequired,
    actualEditorStep: PropTypes.string,
    alreadyFilledFields: PropTypes.array
};
