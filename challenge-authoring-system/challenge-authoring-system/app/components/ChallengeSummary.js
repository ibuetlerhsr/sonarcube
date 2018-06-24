import React, {Component, PropTypes} from 'react';
import {Segment, Header} from 'semantic-ui-react';
const ReactMarkdown = require('react-markdown');
import ImagePreview from '../components/ImagePreview';
import ChallengeMetadataSummary from '../components/ChallengeMetadataSummary';
import {getChallengeDataByLanguage} from '../actions/actions';

export default class ChallengeSummary extends Component {

    componentWillMount() {
        const { dispatch, abstractMD, solutionMD, challengeTitle, challengeId} = this.props;
        if((abstractMD !== '' && abstractMD !== undefined) || (solutionMD !== '' && solutionMD !== undefined) || (challengeTitle !== '' && challengeTitle !== undefined)) {
            dispatch(getChallengeDataByLanguage(challengeId, 'en'));
        }
    }

    createMetadataObject = () => {
        const {challengeLevel, challengeName, challengeType, selectedCategories, selectedKeywords, selectedUsages, goldnuggetType, staticGoldnuggetSecret} = this.props;
        const metadataObject = {};
        metadataObject.name = challengeName;
        metadataObject.type = challengeType;
        metadataObject.level = challengeLevel;
        metadataObject.usages = selectedUsages;
        metadataObject.keywords = selectedKeywords;
        metadataObject.categories = selectedCategories;
        metadataObject.staticGoldnuggetSecret = staticGoldnuggetSecret;
        metadataObject.goldnuggetType = goldnuggetType;
        return metadataObject;
    };

    renderSteps = (steps) => {
        const component = [];
        if(steps === undefined || steps.length === 0) {
            return component;
        }
        for(let i = 0; i < steps.length; i++) {
            let value = '#### No step text defined';
            if(steps[i] === undefined) {
                continue;
            }
            if(steps[i].stepMD !== undefined) {
                value = steps[i].stepMD;
            }

            component.push(<Segment key={steps[i].id}><ReactMarkdown source={value.replace('/media/', '/api/media/')} /></Segment>);
        }
        return component;
    };

    renderSections = () => {
        const {sectionReferences} = this.props;
        const component = [];
        if(sectionReferences === undefined || sectionReferences.length === 0) {
            return <Segment secondary attached>no sections set</Segment>;
        }
        for(let i = 0; i < sectionReferences.length; i++) {
            let value = '#### No section text defined';
            if(sectionReferences[i] === undefined) {
                continue;
            }
            if(sectionReferences[i].sectionMD !== undefined) {
                value = sectionReferences[i].sectionMD;
            }

            component.push(<Segment key={sectionReferences[i].id}><ReactMarkdown source={value.replace('/media/', '/api/media/')} /></Segment>);
            component.push(<Header as="h3" attached="top">{sectionReferences[i].id}</Header>);
            component.push(<Segment.Group vertical>{this.renderSteps(sectionReferences[i].steps)}</Segment.Group>);
        }
        return component;
    };

    render = () => {
        const {abstractMD, solutionMD, challengeTitle, titleImage} = this.props;
        return (
            <div>
                <Header as="h3" attached="top">
                    Title
                </Header>
                <Segment secondary attached>
                    <ReactMarkdown source={challengeTitle}/>
                    {titleImage === undefined &&
                    <ImagePreview uploadedFile={'data:image/png;base64, ' + titleImage} />
                    }
                </Segment>
                <Header as="h3" attached>
                    Challenge Metadata
                </Header>
                <Segment secondary attached>
                    <ChallengeMetadataSummary challengeMetadata={this.createMetadataObject()}/>
                </Segment>
                <Header as="h3" attached>
                    Abstract
                </Header>
                <Segment secondary attached>
                    <ReactMarkdown source={abstractMD !== undefined && abstractMD !== '' ? abstractMD.replace('/media/', '/api/media/') : '##### No abstract defined'} />
                </Segment>
                <Header as="h3" attached>
                    Solution
                </Header>
                <Segment secondary attached>
                    <ReactMarkdown source={solutionMD !== undefined && solutionMD !== '' ? solutionMD.replace('/media/', '/api/media/') : '##### No solution defined'} />
                </Segment>
                <Header as="h3" attached="top">Sections</Header>
                <Segment.Group vertical>
                    {this.renderSections()}
                </Segment.Group>
            </div>
        );
    }
}

ChallengeSummary.propTypes = {
    challengeId: PropTypes.string,
    dispatch: PropTypes.func.isRequired,
    challengeLevel: PropTypes.string,
    challengeTitle: PropTypes.string,
    titleImage: PropTypes.string,
    challengeName: PropTypes.string,
    challengeType: PropTypes.string,
    solutionMD: PropTypes.string,
    abstractMD: PropTypes.string,
    selectedCategories: PropTypes.array,
    selectedKeywords: PropTypes.array,
    selectedUsages: PropTypes.array,
    goldnuggetType: PropTypes.string,
    staticGoldnuggetSecret: PropTypes.string,
    sectionReferences: PropTypes.array
};
