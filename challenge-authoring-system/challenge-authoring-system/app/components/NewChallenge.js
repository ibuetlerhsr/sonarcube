/* eslint-disable no-nested-ternary */
import React, { Component, PropTypes } from 'react';
import {Segment, Button} from 'semantic-ui-react';
import SimpleInputField from './SimpleInputField';
import {
    getNewChallengeId,
    setActualEditorStep,
    postNewChallenge,
    setAddSectionCount,
    getChallengeLevel,
    getChallengeUsage,
    getAbstractIdByChallengeId, getSolutionIdByChallengeId, getNewSectionId
} from '../actions/actions';
import PrivateCheckbox from './PrivateCheckbox';
import ChallengeCategoriesDropDown from './ChallengeCategoriesDropDown';
import SectionComponent from './SectionComponent';
import SortableList from './SortableList';
import TooltipButton from './TooltipButton';
import ChallengeTypeDropDown from './ChallengeTypeDropDown';
import TitelComponent from './TitelComponent';
import ChallengeMetadataDisplay from './ChallengeMetadataDisplay';
import ChallengeKeywordDisplay from './ChallengeKeywordDisplay';
import FinishChallengeComponent from './FinishChallengeComponent';
import ChallengeSummary from './ChallengeSummary';
import SolutionComponent from './SolutionComponent';
import GoldnuggetComponent from './GoldnuggetComponent';
import AbstractComponent from './AbstractComponent';

export default class NewChallenge extends Component {
    constructor() {
        super();
        this.renderNameInputField = this.renderNameInputField.bind(this);
    }

    state : {
        title: string,
        name: string,
        finishedEditing: boolean,
        items: Array,
        type: string} = {finishedEditing: false, items: [], title: '', name: '', type: ''};

    componentDidMount() {
        const {actualEditorStep, challengeId} = this.props;
        if(challengeId === '') {
            this.getNewChallengeId();
        }
        const $ = require('jquery');
        $('.simpleInputField').keyup(function simpleInputOnEnterKeyUp(event) {
            if (event.keyCode === 13) {
                if(actualEditorStep === 'challengeNameNavItem') {
                    $('#challengeNameNavItemButton').click();
                }
                if(actualEditorStep === 'challengeTypeNavItem') {
                    $('#challengeTypeNavItemButton').click();
                }
            }
        });
    }

    componentDidUpdate() {
        const {actualEditorStep} = this.props;
        const $ = require('jquery');
        $('.simpleInputField').keyup(function simpleInputOnEnterKeyUp(event) {
            if (event.keyCode === 13) {
                if(actualEditorStep === 'challengeNameNavItem') {
                    $('#challengeNameNavItemButton').click();
                }
                if(actualEditorStep === 'challengeTypeNavItem') {
                    $('#challengeTypeNavItemButton').click();
                }
            }
        });

        this.getAbstractIdByChallengeId();
        this.getSolutionIdByChallengeId();
    }

    getAbstractIdByChallengeId = () => {
        const {dispatch, challengeId, abstractMD, abstractID} = this.props;
        if(challengeId !== '' && challengeId !== undefined && abstractMD !== '' && abstractID === '') {
            dispatch(getAbstractIdByChallengeId(challengeId));
        }
    };

    getSolutionIdByChallengeId = () => {
        const {dispatch, challengeId, solutionMD, solutionID} = this.props;
        if(challengeId !== '' && challengeId !== undefined && solutionMD !== '' && solutionID === '') {
            dispatch(getSolutionIdByChallengeId(challengeId));
        }
    };

    getNewChallengeId = () => {
        const {dispatch} = this.props;
        dispatch(getNewChallengeId());
    };

    getChallengeMetadata = (challengeMetaDataName) => {
        if(challengeMetaDataName === 'challengeLevel') {
            return getChallengeLevel;
        }
        if(challengeMetaDataName === 'challengeUsage') {
            return getChallengeUsage;
        }

        return undefined;
    };

    renderNameInputField = () => {
        const {actualEditorStep} = this.props;
        return (<Segment raised>
            <SimpleInputField key={'navItem2'} value={this.state !== undefined && this.state !== null ? this.state.name : ''} className={"simpleInputField"} size={"huge"} id={"challengeName"} reference={"two"} onChange={this.handleNameChange} required placeholder={"Please insert challenge name..."} name={"challengeName"} labelText={"Name"}/>
            <Button style={{marginTop: 10}} primary fluid disabled={!this.proveEditorStep()} id={actualEditorStep + 'Button'} onClick={this.submitHandler}>Submit</Button>
        </Segment>);
    };

    renderSectionComponents = () => {
        const {editorArray, stepArray, sectionReferences, sectionItems, dispatch, challengeId, isAuthenticated, errorMessage} = this.props;
        const component = [];
        if(sectionReferences.length > 0 ) {
            sectionReferences.forEach(sectionReference => {
                component.push(<SectionComponent show={sectionReference.show} sectionItems={sectionItems} content={sectionReference.sectionMD} stepReferences={sectionReference.steps} editorArray={editorArray} dataId={sectionReference.id} id={sectionReference.id} stepArray={stepArray} dispatch={dispatch} challengeId={challengeId} isAuthenticated={isAuthenticated}
                                                 errorMessage={errorMessage} nextEditorStep={'challengeStepNavItem'}/>);
            });
        }
        return component;
    };

    getShowFromSectionsToRender = (id, sectionsToRender) => {
        let value = null;
        sectionsToRender.forEach(section => {
            if(section.props.dataId === id) {
                value = section;
            }
        });
        return value;
    };

    concatNewSectionItemToItemsArray(sectionCount) {
        const sectionsToRender = this.renderSectionComponents(sectionCount);
        const newSectionsList = [];
        this.state.items.forEach(item => {
            if(sectionsToRender.filter(section => (item.props.id === section.props.id)).length > 0) {
                newSectionsList.push(item);
            }
        });

        sectionsToRender.forEach(item => {
            if(newSectionsList.filter(section => (item.props.id === section.props.id)).length === 0) {
                newSectionsList.push(item);
            }
        });
        const thisContext = this;
        newSectionsList.forEach(function(value, index) {
            newSectionsList[index] = thisContext.getShowFromSectionsToRender(value.props.dataId, sectionsToRender);
        });
        return newSectionsList;
    }


    handleTitleSubmit = (event, challengeId, dispatch) => {
        if(event.target instanceof HTMLButtonElement) {
            const jsonData = {};
            jsonData.challengeId = challengeId;
            if(this.state !== undefined && this.state !== null) {
                jsonData.challengeTitle = this.state.title;
                if(this.state.title !== '' && this.state.title !== undefined) {
                    dispatch(setActualEditorStep('challengeTypeNavItem', false));
                    dispatch(postNewChallenge(JSON.stringify(jsonData)));
                }
            }
        }
    };

    handleNameSubmit = (event, challengeId, dispatch) => {
        if(event.target instanceof HTMLButtonElement) {
            const jsonData = {};
            jsonData.challengeId = challengeId;
            if(this.state !== undefined && this.state !== null) {
                jsonData.challengeName = this.state.name;
                if(this.state.name !== '' && this.state.name !== undefined) {
                    dispatch(setActualEditorStep('challengeTitleNavItem', false));
                    dispatch(postNewChallenge(JSON.stringify(jsonData)));
                }
            }
        }
    };

    handleTypeSubmit = (event, challengeId, dispatch) => {
        if(event.target instanceof HTMLButtonElement) {
            const jsonData = {};
            jsonData.challengeId = challengeId;
            if(this.state !== undefined && this.state !== null) {
                jsonData.challengeType = this.state.type;
                if(this.state.type !== '' && this.state.type !== undefined) {
                    dispatch(setActualEditorStep('challengeLevelNavItem', false));
                    dispatch(postNewChallenge(JSON.stringify(jsonData)));
                }
            }
        }
    };
    submitHandler = (event: Event) => {
        const {challengeId, dispatch, actualEditorStep} = this.props;
        event.preventDefault();
        if(actualEditorStep === 'challengeTitleNavItem') {
            this.handleTitleSubmit(event, challengeId, dispatch);
        }
        if(actualEditorStep === 'challengeNameNavItem') {
            this.handleNameSubmit(event, challengeId, dispatch);
        }
        if(actualEditorStep === 'challengeTypeNavItem') {
            this.handleTypeSubmit(event, challengeId, dispatch);
        }
    };

    handleTitleChange = (event: Event) => {
        if(event.target instanceof HTMLInputElement) {
            this.setState({title: event.target.value});
        }

        return true;
    };

    handleNameChange = (event: Event) => {
        if(event.target instanceof HTMLInputElement) {
            this.setState({name: event.target.value});
        }

        return true;
    };

    handleTypeChange = (event: Event) => {
        if(event.target instanceof HTMLInputElement) {
            this.setState({type: event.target.value});
        }

        return true;
    };

    handleAddSectionClick = (event, dispatch, sectionCount) => {
        event.preventDefault();
        let sections = sectionCount;
        sections = sections + 1;
        dispatch(setAddSectionCount(sections));
        this.getNewSectionId();
    };

    getNewSectionId = () => {
        const {dispatch, challengeId} = this.props;
        dispatch(getNewSectionId(challengeId));
    };

    handleCheckSummaryClick = (event, dispatch) => {
        event.preventDefault();
        dispatch(setActualEditorStep('finishChallengeNavItem'));
    };

    handleFinishChallengeClick = (event, dispatch) => {
        event.preventDefault();
        dispatch(setActualEditorStep('challengeGoldnuggetNavItem'));
    };

    proveEditorStep = () => {
        const {actualEditorStep} = this.props;
        if(this.state === null || this.state === undefined) {
            return false;
        }
        if(actualEditorStep === 'challengeTitleNavItem') {
            return this.state.title !== null && this.state.title !== undefined && this.state.title !== '';
        }
        if(actualEditorStep === 'challengeNameNavItem') {
            return this.state.name !== null && this.state.name !== undefined && this.state.name !== '';
        }
        if(actualEditorStep === 'challengeTypeNavItem') {
            return this.state.type !== null && this.state.type !== undefined && this.state.type !== '';
        }
        return false;
    };

    filterArrayItem = (orderItem) => {
        const filteredValue = this.state.items.filter(item => item.props.id === orderItem);
        if(filteredValue.length === 1) {
            return filteredValue[0];
        }
        return null;
    };

    changeOrder = (items) => {
        const tempArray = [];
        items.forEach(orderItem => {
            tempArray.push(this.filterArrayItem(orderItem));
        });
        this.setState({items: tempArray});
    };

    render() {
        const { dispatch, fatChallenge, sectionReferences, abstractID, solutionID, abstractMD, solutionMD, goldnuggetType, staticGoldnuggetSecret, uploadedFiles, mediaIds, languageIsoCode, titleImageId, titleImageFile, challengeTypes, selectedKeywords, challengeKeywords, challengeUsages, challengeTitle, challengeName, challengeType, selectedChallengeUsages, editorArray, sectionCount, selectedChallengeCategories, actualEditorStep, isAuthenticated, challengeLevels, errorMessage, challengeId, challengeCategories, challengeCategory, challengeLevel,  isPrivate} = this.props;
        if(sectionCount > 0 && actualEditorStep === 'challengeStepNavItem') {
            if(this.state.items.length === 0 && challengeId !== '' && challengeId !== undefined && sectionReferences.length === 0) {
                this.getNewSectionId(challengeId);
            } else if(this.state.items.length < sectionReferences.length) {
                this.state.items = this.concatNewSectionItemToItemsArray(sectionCount);
            } else {
                for(let index = 0; index < sectionReferences.length; index++) {
                    if(this.state.items[index].props.show !== sectionReferences[index].show) {
                        this.state.items = this.concatNewSectionItemToItemsArray(sectionCount);
                    }
                }
            }
        }
        let levelArray = null;
        if(challengeTitle !== undefined && challengeTitle !== null && challengeTitle !== '' && this.state.title === '') {
            this.state.title = challengeTitle;
        }
        if(challengeName !== undefined && challengeName !== null && challengeName !== '' && this.state.name === '') {
            this.state.name = challengeName;
        }
        if(challengeType !== undefined && challengeType !== null && challengeType !== '' && this.state.type === '') {
            this.state.type = challengeType;
        }
        if(challengeLevel !== undefined && challengeLevel !== null) {
            levelArray = [challengeLevel];
        }

        return (
            <div>
                {(actualEditorStep === 'challengeTitleNavItem' || actualEditorStep === 'challengeNameNavItem') &&
                <Segment raised>
                    {actualEditorStep === 'challengeTitleNavItem' &&
                    <TitelComponent languageIsoCode={languageIsoCode} dispatch={dispatch} challengeId={challengeId} titleImageId={titleImageId} titleImageFile={titleImageFile} handleTitleChange={this.handleTitleChange} submitHandler={this.submitHandler} disabled={!this.proveEditorStep()} challengeTitle={this.state !== undefined && this.state !== null ? this.state.title : ''}/>
                    }
                    {actualEditorStep === 'challengeNameNavItem' &&
                    this.renderNameInputField()
                    }
                </Segment>
                }
                {actualEditorStep === 'challengeTypeNavItem' &&
                <Segment raised>
                    <ChallengeTypeDropDown dispatch={dispatch} challengeId={challengeId} value={challengeType} challengeTypes={challengeTypes}/>
                </Segment>
                }
                {actualEditorStep === 'challengeLevelNavItem' &&
                <Segment raised>
                    <ChallengeMetadataDisplay metadataName={'level'} selectedValue={levelArray} dispatch={dispatch} challengeId={challengeId} challengeMetadataList={challengeLevels} challengeMetadataName={'challengeLevel'} nextEditorStep={'challengeUsageNavItem'} itemsPerRow={5} getChallengeMetadata={this.getChallengeMetadata('challengeLevel')}/>
                </Segment>
                }
                {actualEditorStep === 'challengeUsageNavItem' &&
                <Segment raised>
                    <ChallengeMetadataDisplay metadataName={'usages'} actualEditorStep={'challengeUsageNavItem'} multiselectable selectedValue={selectedChallengeUsages} dispatch={dispatch} challengeId={challengeId} challengeMetadataList={challengeUsages} challengeMetadataName={'challengeUsages'} nextEditorStep={'challengeCategoryNavItem'} itemsPerRow={3} getChallengeMetadata={this.getChallengeMetadata('challengeUsage')}/>
                </Segment>
                }
                {actualEditorStep === 'challengeCategoryNavItem' &&
                <Segment raised>
                    <ChallengeCategoriesDropDown
                        dispatch={dispatch}
                        challengeId={challengeId}
                        challengeCategories={challengeCategories}
                        value={challengeCategory}
                        selectedChallengeCategories={selectedChallengeCategories}
                    />
                </Segment>
                }
                {actualEditorStep === 'challengeKeywordNavItem' &&
                <ChallengeKeywordDisplay dispatch={dispatch} selectedKeywords={selectedKeywords} challengeKeywords={challengeKeywords} challengeId={challengeId}/>
                }
                {actualEditorStep === 'challengePrivateNavItem' &&
                <PrivateCheckbox challengeId={challengeId} dispatch={dispatch} isPrivate={isPrivate} />
                }
                {actualEditorStep === 'challengeAbstractNavItem' &&
                <Segment raised>
                    <AbstractComponent valueId={abstractID} content={abstractMD} editorArray={editorArray} uploadedFiles={uploadedFiles} mediaIds={mediaIds} challengeId={challengeId} isAuthenticated={isAuthenticated}
                                       errorMessage={errorMessage} dispatch={dispatch}/>
                </Segment>
                }
                {actualEditorStep === 'challengeStepNavItem' &&
                <Segment raised>
                    <Button.Group>
                        <TooltipButton id={'addSectionButton'} buttonStyle={'icon'} iconStyle={'plus'} dispatch={dispatch} handleClick={this.handleAddSectionClick.bind(this)} showText={'+'} tooltip={'Add Section'} sectionCount={sectionCount} type={'section'}/>
                        <TooltipButton id={'finishChallengeButton'} buttonStyle={'icon'} iconStyle={'check'} dispatch={dispatch} handleClick={this.handleFinishChallengeClick.bind(this)} showText={''} tooltip={'Finish Section'} sectionCount={sectionCount} type={'section'}/>
                    </Button.Group>
                    {this.state !== null && this.state !== undefined &&
                    <div style={{height: '80vh', overflowX: 'auto'}}>
                        <SortableList items={this.state.items} onChange={(order) => this.changeOrder(order)}/>
                    </div>
                    }
                </Segment>
                }
                {actualEditorStep === 'challengeGoldnuggetNavItem' &&
                <GoldnuggetComponent goldnuggetType={goldnuggetType} staticGoldnuggetSecret={staticGoldnuggetSecret} challengeId={challengeId} dispatch={dispatch} nextEditorStep={'challengeSolutionNavItem'}/>
                }
                {actualEditorStep === 'challengeSolutionNavItem' &&
                <SolutionComponent valueId={solutionID} content={solutionMD} editorArray={editorArray} uploadedFiles={uploadedFiles} mediaIds={mediaIds} challengeId={challengeId} isAuthenticated={isAuthenticated}
                                   errorMessage={errorMessage} dispatch={dispatch}/>
                }
                {actualEditorStep === 'challengeSummaryNavItem' &&
                <Segment raised>
                    <ChallengeSummary dispatch={dispatch} sectionReferences={sectionReferences} challengeLevel={challengeLevel} challengeTitle={challengeTitle} titleImage={fatChallenge.languageComponents !== undefined ? fatChallenge.languageComponents[0].titlePNG : undefined} challengeName={challengeName} challengeType={challengeType}
                                      solutionMD={solutionMD} abstractMD={abstractMD} selectedCategories={selectedChallengeCategories} goldnuggetType={goldnuggetType} staticGoldnuggetSecret={staticGoldnuggetSecret}
                                      selectedKeywords={selectedKeywords} selectedUsages={selectedChallengeUsages} challengeId={challengeId}/>
                    <TooltipButton id={'checkSummaryButton'} buttonStyle={'icon'} iconStyle={'check'} dispatch={dispatch} handleClick={this.handleCheckSummaryClick} showText={''} tooltip={'Check Summary'} type={'summary'}/>
                </Segment>
                }
                {actualEditorStep === 'finishChallengeNavItem' &&
                <Segment raised>
                    <FinishChallengeComponent/>
                </Segment>
                }
            </div>
        );
    }
}

NewChallenge.propTypes = {
    dispatch: PropTypes.func.isRequired,
    isAuthenticated: PropTypes.bool.isRequired,
    errorMessage: PropTypes.string,
    fatChallenge: PropTypes.object,
    challengeLevels: PropTypes.array,
    challengeUsages: PropTypes.array,
    uploadedFiles: PropTypes.array,
    mediaIds: PropTypes.array,
    titleImageFile: PropTypes.object,
    titleImageId: PropTypes.string,
    challengeTypes: PropTypes.array,
    challengeList: PropTypes.array,
    languageIsoCode: PropTypes.string,
    challengeId: PropTypes.string,
    goldnuggetType: PropTypes.string,
    staticGoldnuggetSecret: PropTypes.string,
    challengeName: PropTypes.string,
    challengeTitle: PropTypes.string,
    challengeType: PropTypes.string,
    challengeLevel: PropTypes.string,
    selectedChallengeUsages: PropTypes.array,
    challengeCategories: PropTypes.array,
    challengeCategory: PropTypes.string,
    challengeKeywords: PropTypes.array,
    selectedKeywords: PropTypes.array,
    isPrivate: PropTypes.string,
    actualEditorStep: PropTypes.string,
    lastEditorStep: PropTypes.string,
    selectedChallengeCategories: PropTypes.array,
    stepArray: PropTypes.array,
    sectionCount: PropTypes.number,
    editorArray: PropTypes.array,
    sectionItems: PropTypes.array,
    sectionId: PropTypes.string,
    instructionId: PropTypes.string,
    hintId: PropTypes.string,
    abstractID: PropTypes.string,
    solutionID: PropTypes.string,
    abstractMD: PropTypes.string,
    solutionMD: PropTypes.string,
    sectionReferences: PropTypes.array
};
