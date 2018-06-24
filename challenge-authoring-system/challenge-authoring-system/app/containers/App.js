/* eslint-disable */
import React, { Component, PropTypes } from 'react';
import { connect } from 'react-redux';
import Navbar from '../components/Navbar';
import LeftSidebarScaleDown from '../components/LeftSidebarScaleDown';
import {setToken, getChallengeDataByLanguage, setTargetTranslation, getServerCookie} from '../actions/actions';
class App extends Component {
    componentWillMount() {
        const {dispatch} = this.props;
        dispatch(getServerCookie());
    }

    componentDidMount() {
        const { dispatch} = this.props;
        const url = window.location.href;
        if(url.includes('?challengeId=')) {
            dispatch(setToken(true));
            const index = url.indexOf('challengeId=');
            const length = url.indexOf('targetLanguage');
            let challengeId = '';
            if(length === -1) {
                challengeId = url.substr(index + 'challengeId='.length);
            } else {
                challengeId = url.substr(index + 'challengeId='.length, length + 1 - index - 'targetLanguage'.length);
            }
            if(challengeId !== '' && challengeId !== undefined) {
                dispatch(getChallengeDataByLanguage(challengeId, 'en'));
            }
        }

        if(url.includes('&targetLanguage=')) {
            dispatch(setToken(true));
            const index = url.indexOf('targetLanguage=');
            let targetLanguage = '';
            targetLanguage = url.substr(index + 'targetLanguage='.length);
            dispatch(setTargetTranslation(targetLanguage));
        }
    }

    render() {
        const { dispatch, hasError, errorContext, translatedChallenge, abstractTranslation, solutionTranslation, titleTranslation, nameTranslation, sectionTranslations, stepTranslations, targetLanguage, abstractID, solutionID, sectionReferences, abstractMD, solutionMD, goldnuggetType, staticGoldnuggetSecret, uploadedFiles, mediaIds, languageIsoCode, fatChallenge, sectionItems, titleImageFile, titleImageId, challengeKeywords,selectedKeywords, challengeTypes, editorArray, selectedChallengeCategories, challengeLevels, challengeUsages, selectedChallengeUsages, consumerList, consumerURL, selectedConsumer, isAuthenticated, message, challengeId, challengeName, challengeType, challengeTitle, challengeCategories, challengeCategory, challengeLevel, isPrivate, actualEditorStep, stepArray, sectionCount} = this.props;
        return (
            <div>
                <Navbar
                    isAuthenticated={isAuthenticated}
                    consumerList={consumerList}
                    consumerURL={consumerURL}
                    errorMessage={message}
                    dispatch={dispatch}
                    selectedConsumer={selectedConsumer}
                />
                {isAuthenticated &&
                    <LeftSidebarScaleDown dispatch={dispatch}
                                          targetLanguage={targetLanguage}
                                          translatedChallenge={translatedChallenge}
                                          abstractTranslation={abstractTranslation}
                                          solutionTranslation={solutionTranslation}
                                          titleTranslation={titleTranslation}
                                          nameTranslation={nameTranslation}
                                          sectionTranslations={sectionTranslations}
                                          stepTranslations={stepTranslations}
                                          isAuthenticated={isAuthenticated}
                                          message={message}
                                          errorContext={errorContext}
                                          hasError={hasError}
                                          challengeLevels={challengeLevels}
                                          challengeUsages={challengeUsages}
                                          titleImageFile={titleImageFile}
                                          titleImageId={titleImageId}
                                          uploadedFiles={uploadedFiles}
                                          mediaIds={mediaIds}
                                          challengeTypes={challengeTypes}
                                          languageIsoCode={languageIsoCode}
                                          staticGoldnuggetSecret={staticGoldnuggetSecret}
                                          goldnuggetType={goldnuggetType}
                                          challengeId={challengeId}
                                          challengeName={challengeName}
                                          challengeTitle={challengeTitle}
                                          challengeType={challengeType}
                                          challengeCategories={challengeCategories}
                                          challengeCategory={challengeCategory}
                                          challengeLevel={challengeLevel}
                                          selectedChallengeUsages={selectedChallengeUsages}
                                          isPrivate={isPrivate}
                                          selectedChallengeCategories={selectedChallengeCategories}
                                          challengeKeywords={challengeKeywords}
                                          selectedKeywords={selectedKeywords}
                                          actualEditorStep={actualEditorStep}
                                          stepArray={stepArray}
                                          sectionCount={sectionCount}
                                          editorArray={editorArray}
                                          fatChallenge={fatChallenge}
                                          abstractID={abstractID}
                                          solutionID={solutionID}
                                          abstractMD={abstractMD}
                                          solutionMD={solutionMD}
                                          sectionItems={sectionItems}
                                          sectionReferences={sectionReferences}/>
                }
            </div>
        );
    }
}

App.propTypes = {
    dispatch: PropTypes.func.isRequired,
    fatChallenge: PropTypes.object,
    translatedChallenge: PropTypes.object,
    challengeList: PropTypes.object,
    isAuthenticated: PropTypes.bool.isRequired,
    uploadedFiles: PropTypes.array,
    mediaIds: PropTypes.array,
    titleImageFile: PropTypes.object,
    titleImageId: PropTypes.string,
    hasError: PropTypes.bool.isRequired,
    errorContext: PropTypes.string,
    message: PropTypes.string,
    consumerList: PropTypes.array,
    challengeLevels: PropTypes.array,
    challengeUsages: PropTypes.array,
    selectedChallengeUsages: PropTypes.array,
    challengeTypes: PropTypes.array,
    selectedConsumer: PropTypes.string,
    consumerURL: PropTypes.string,
    challengeId: PropTypes.string,
    languageIsoCode: PropTypes.string,
    challengeName: PropTypes.string,
    goldnuggetType: PropTypes.string,
    staticGoldnuggetSecret: PropTypes.string,
    challengeTitle: PropTypes.string,
    challengeType: PropTypes.string,
    challengeLevel: PropTypes.string,
    challengeCategories: PropTypes.array,
    challengeCategory: PropTypes.string,
    challengeKeywords: PropTypes.array,
    selectedKeywords: PropTypes.array,
    isPrivate: PropTypes.string,
    actualEditorStep: PropTypes.string,
    stepArray: PropTypes.array,
    sectionCount: PropTypes.number,
    editorArray: PropTypes.array,
    sectionItems: PropTypes.array,
    abstractID: PropTypes.string,
    solutionID: PropTypes.string,
    abstractMD: PropTypes.string,
    solutionMD: PropTypes.string,
    sectionReferences: PropTypes.array,
    targetLanguage: PropTypes.string,
    abstractTranslation: PropTypes.string,
    solutionTranslation: PropTypes.string,
    titleTranslation: PropTypes.string,
    nameTranslation: PropTypes.string,
    sectionTranslations: PropTypes.array,
    stepTranslations: PropTypes.array,
};
function mapStateToProps(state) {
    const { consumers, challenges, auth, challenge, navigator, stepItemCounter, editorHandler, messageHandler, translations } = state;
    const { isAuthenticated } = auth;
    const { hasError, message, errorContext} = messageHandler;
    const { challengeList, authenticated} = challenges;
    const { consumerList, selectedConsumer, consumerURL} = consumers;
    const { sectionReferences, abstractID, solutionID, abstractMD, solutionMD, instructionId, hintId, sectionId, goldnuggetType, staticGoldnuggetSecret, uploadedFiles, mediaIds, languageIsoCode, fatChallenge, challengeLevels, challengeKeywords, selectedKeywords, selectedChallengeUsages, challengeTypes, titleImageFile, titleImageId, challengeName, challengeType, challengeLevel, challengeUsages, challengeCategories, challengeCategory, challengeId, challengeTitle, isPrivate, selectedChallengeCategories} = challenge;
    const { actualEditorStep} = navigator;
    const { translatedChallenge, targetLanguage, abstractTranslation, solutionTranslation, titleTranslation, nameTranslation, sectionTranslations, stepTranslations} = translations;
    const { editorArray, stepArray, sectionCount, sectionItems} = stepItemCounter;

    return {
        consumerURL,
        selectedConsumer,
        consumerList,
        challengeList,
        isAuthenticated,
        authenticated,
        languageIsoCode,
        fatChallenge,
        challengeLevels,
        challengeTypes,
        challengeUsages,
        challengeName,
        challengeType,
        challengeLevel,
        selectedChallengeUsages,
        challengeCategories,
        challengeCategory,
        isPrivate,
        challengeId,
        challengeTitle,
        actualEditorStep,
        selectedChallengeCategories,
        challengeKeywords,
        selectedKeywords,
        uploadedFiles,
        staticGoldnuggetSecret,
        goldnuggetType,
        mediaIds,
        titleImageFile,
        titleImageId,
        stepArray,
        sectionCount,
        editorArray,
        sectionItems,
        instructionId,
        hintId,
        sectionId,
        abstractID,
        solutionID,
        abstractMD,
        solutionMD,
        sectionReferences,
        targetLanguage,
        translatedChallenge,
        abstractTranslation,
        solutionTranslation,
        titleTranslation,
        nameTranslation,
        sectionTranslations,
        stepTranslations,
        hasError,
        errorContext,
        message
    };
}

export default connect(mapStateToProps)(App);
