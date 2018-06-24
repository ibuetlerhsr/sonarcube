import React, { Component, PropTypes } from 'react';
import '../style/ReactTag.css';
import { WithContext as ReactTags } from 'react-tag-input';
import {setSelectedKeywords, getChallengeKeywords, postNewChallenge, setActualEditorStep} from '../actions/actions';
import {Button} from 'semantic-ui-react';

export default class ChallengeKeywordDisplay extends Component {
    constructor(props) {
        super(props);
        this.handleRemoveClick = this.handleRemoveClick.bind(this);
        this.handleAddClick = this.handleAddClick.bind(this);
    }

    state : {
        challengeKeyword:string,
        challengeKeywordsList: Array} = {challengeKeywordsList: []};

    componentDidMount() {
        this.updateKeywordSuggestions();
    }

    updateKeywordSuggestions = () => {
        const {dispatch} = this.props;
        dispatch(getChallengeKeywords());
    };

    selectChallengeKeyword = (keyword: String) => {
        const {dispatch, challengeId} = this.props;
        const jsonData = {};
        jsonData.challengeId = challengeId;
        jsonData.challengeKeywords = keyword;
        // this.state.challengeKeywordsList[this.state.challengeKeywordsList.length] = challengeKeywords[data.value.toString() - 1];

        dispatch(postNewChallenge(JSON.stringify(jsonData)));
    };

    handleFinishClick = (event: Event) => {
        const {dispatch} = this.props;
        if(event !== undefined) {
            dispatch(setActualEditorStep('challengePrivateNavItem', false));
            dispatch(setSelectedKeywords(this.state.challengeKeywordsList));
        }
    };

    handleRemoveClick(indexToRemove) {
        if(indexToRemove !== undefined) {
            this.setState({
                challengeKeywordsList: this.state.challengeKeywordsList.filter((tag, index) => index !== indexToRemove),
            });
        }
    }

    handleAddClick(keyword) {
        const keywords = this.state.challengeKeywordsList;
        if(keyword !== undefined) {
            keywords.push({
                text: keyword
            });
            this.selectChallengeKeyword(keyword);
            this.setState({challengeKeywordsList: keywords});
        }
    }

    createSuggestions = () => {
        const {challengeKeywords} = this.props;
        const suggestions = [];
        challengeKeywords.map((keyword) => {
            suggestions.push(keyword.text);
        });
        return suggestions;
    };

    render() {
        const {selectedKeywords, actualEditorStep} = this.props;
        if(selectedKeywords !== undefined && selectedKeywords.length > 0) {
            this.state.challengeKeywordsList = selectedKeywords;
        }
        return (
            <div>
                <ReactTags tags={this.state.challengeKeywordsList}
                           suggestions={this.createSuggestions()}
                           handleDelete={this.handleRemoveClick}
                           handleAddition={this.handleAddClick}
                           placeholder={'Add new keyword'}/>
                <Button style={{marginTop: 10}} primary fluid disabled={this.state.challengeKeywordsList.length === 0} id={actualEditorStep + 'Button'} onClick={(event) => this.handleFinishClick(event)}>Submit</Button>
            </div>
        );
    }
}


ChallengeKeywordDisplay.propTypes = {
    dispatch: PropTypes.func.isRequired,
    challengeId: PropTypes.string,
    challengeKeywords: PropTypes.array,
    selectedKeywords: PropTypes.array,
    actualEditorStep: PropTypes.string,
    nextEditorStep: PropTypes.string
};
