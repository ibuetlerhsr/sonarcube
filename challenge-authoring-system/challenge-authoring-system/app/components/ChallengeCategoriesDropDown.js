/* eslint-disable */
import React, { Component, PropTypes } from 'react';
import {
    setSelectedChallengeCategories,
    getChallengeCategory,
    setCategoryFromDropDown,
    postNewChallenge,
    setActualEditorStep
} from '../actions/actions';
import {Dropdown, Card, Button} from 'semantic-ui-react';
import ChallengeCategoryCard from './ChallengeCategoryCard';

if (typeof document !== 'undefined') {
    const $ = require('jquery');
}

export default class ChallengeCategoriesDropDown extends Component {
    state : {
        challengeCategory:string,
        challengeCategoriesList: Array} = {challengeCategoriesList: []};

    componentDidMount() {
        this.updateChallengeCategory();
    }

    updateChallengeCategory = () => {
        const {dispatch} = this.props;
        dispatch(getChallengeCategory());
    };

    createDropDownEntries= (challengeCategories) => {
        const options = [];

        if (challengeCategories !== undefined && challengeCategories.length > 0) {
            challengeCategories.map((category) => {
                options.push({
                    'text': category.categoryName,
                    'value': category.id
                });
            });
        }
        return options;
    };

    removeEntryFromArray = (index) => {
        const newChallengeCategoriesList = [];
        this.state.challengeCategoriesList.forEach(challengeCategory => {
            if(challengeCategory.id !== index) {
                newChallengeCategoriesList.push(challengeCategory);
            }
        });

        return newChallengeCategoriesList;
    };

    removeChallengeCategory = (event: Event, challengeCategory: any) => {
        const {dispatch, challengeId, challengeCategories} = this.props;
        dispatch(setCategoryFromDropDown(challengeCategory));
        const jsonData = {};
        jsonData.challengeId = challengeId;
        jsonData.challengeCategories = challengeCategory.toString();
        this.setState({challengeCategoriesList: this.removeEntryFromArray(challengeCategory)});
        dispatch(postNewChallenge(JSON.stringify(jsonData)));
    };

    selectChallengeCategory = (event: Event, data: any) => {
        const {dispatch, challengeId, challengeCategories} = this.props;
        dispatch(setCategoryFromDropDown(data.value));
        const jsonData = {};
        jsonData.challengeId = challengeId;
        jsonData.challengeCategories = data.value.toString();
        this.state.challengeCategoriesList[this.state.challengeCategoriesList.length] = challengeCategories[data.value.toString()-1];

        dispatch(postNewChallenge(JSON.stringify(jsonData)));
    };

    handleFinishClick = (event: Event) => {
        const {dispatch} = this.props;
        if(event !== undefined) {
            dispatch(setActualEditorStep('challengeKeywordNavItem', false));
            dispatch(setSelectedChallengeCategories(this.state.challengeCategoriesList));
        }
    };

    createCategoryCards = (selectedChallengeCategories) => {
        const {dispatch, challengeId} = this.props;
        const entries = [];

        if (selectedChallengeCategories !== undefined && selectedChallengeCategories.length > 0) {
            if (selectedChallengeCategories[0].id !== undefined) {
                selectedChallengeCategories.map((category) => {
                    entries.push(<ChallengeCategoryCard removeChallengeCategory={this.removeChallengeCategory.bind(this)}
                                                        key={category.categoryName} dispatch={dispatch}
                                                        challengeId={challengeId} categoryId={category.id}
                                                        challengeCategory={category.categoryName}/>);
                });
            }
        }
        return entries;
    };

    render() {
        const {challengeCategories, selectedChallengeCategories} = this.props;
        if(selectedChallengeCategories !== undefined && selectedChallengeCategories.length > 0) {
            this.state.challengeCategoriesList = selectedChallengeCategories;
        }
        return (
            <div>
                <Dropdown
                    placeholder="Select Challenge Category"
                    selection
                    search
                    onChange={this.selectChallengeCategory}
                    options={this.createDropDownEntries(challengeCategories)}
                />
                {this.state.challengeCategoriesList !== undefined && this.state.challengeCategoriesList.length > 0 &&
                    <Card.Group itemsPerRow={5} style={{marginTop: '5%', marginBottom: '1%', marginLeft: '2%'}}>
                        {this.createCategoryCards(this.state.challengeCategoriesList)}
                    </Card.Group>
                }
                <Button style={{marginTop: 10}} primary fluid disabled={this.state.challengeCategoriesList.length === 0} id={'challengeCategoryNavItemButton'} onClick={(event) => this.handleFinishClick(event)}>Submit</Button>
            </div>
        );
    }
}


ChallengeCategoriesDropDown.propTypes = {
    dispatch: PropTypes.func.isRequired,
    challengeId: PropTypes.string,
    challengeCategories: PropTypes.array,
    selectedChallengeCategories: PropTypes.array
};
