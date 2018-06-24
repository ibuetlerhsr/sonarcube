import React, { Component, PropTypes } from 'react';
import {Grid} from 'semantic-ui-react';

import NewChallenge from '../components/NewChallenge';
import EditorNavigation from '../components/EditorNavigation';

export default class Dashboard extends Component {
    proveEditorIsAlreadyUsed = (value) => {
        const {editorArray} = this.props;
        let isInUse = false;
        if(editorArray.length > 0) {
            editorArray.forEach(editor => {
                if(editor.key.includes(value)) {
                    isInUse = true;
                }
            });
        }
        return isInUse;
    };

    render() {
        const { dispatch, sectionReferences, abstractID, solutionID, abstractMD, solutionMD, goldnuggetType, staticGoldnuggetSecret, uploadedFiles, mediaIds, languageIsoCode, fatChallenge, titleImageFile, sectionItems, challengeKeywords, selectedKeywords, titleImageId, challengeTypes, challengeUsages, selectedChallengeUsages, editorArray, selectedChallengeCategories, challengeLevels, isAuthenticated, errorMessage, challengeId, challengeName, challengeType, challengeTitle, challengeCategories, challengeCategory, challengeLevel, isPrivate, actualEditorStep, lastEditorStep, stepArray, sectionCount} = this.props;
        const alreadyFilledFields = [(challengeName !== '' && challengeName !== 'null'),
                                    (challengeTitle !== '' && challengeTitle !== 'null'),
                                    (challengeType !== '' && challengeType !== 'null'),
                                    (challengeLevel !== '' && challengeLevel !== 'null'),
                                    (selectedChallengeUsages !== undefined && selectedChallengeUsages.length !== 0),
                                    (selectedChallengeCategories !== undefined && selectedChallengeCategories.length !== 0),
                                    (selectedKeywords !== undefined && selectedKeywords.length !== 0),
                                    (isPrivate !== '' && isPrivate !== undefined),
                                    (this.proveEditorIsAlreadyUsed('abstract') || abstractMD !== ''),
                                    (this.proveEditorIsAlreadyUsed('section') || (sectionReferences !== undefined && sectionReferences.length > 0)),
                                    (goldnuggetType !== '' && goldnuggetType !== 'null'),
                                    (this.proveEditorIsAlreadyUsed('solution') || solutionMD !== ''),
                                    ((challengeTitle !== '' && challengeTitle !== 'null'))];
        return (
            <div>
                <Grid stackable container>
                    <Grid.Row>
                        <Grid.Column computer={4} tablet={4} mobile={4}>
                            <EditorNavigation dispatch={dispatch}
                                              alreadyFilledFields={alreadyFilledFields}
                                              actualEditorStep={actualEditorStep}
                                              lastEditorStep={lastEditorStep}/>
                        </Grid.Column>
                        <Grid.Column computer={11} tablet={11} mobile={11}>
                            <NewChallenge dispatch={dispatch}
                                          isAuthenticated={isAuthenticated}
                                          challengeLevels={challengeLevels}
                                          challengeTypes={challengeTypes}
                                          challengeUsages={challengeUsages}
                                          uploadedFiles={uploadedFiles}
                                          mediaIds={mediaIds}
                                          titleImageFile={titleImageFile}
                                          titleImageId={titleImageId}
                                          errorMessage={errorMessage}
                                          languageIsoCode={languageIsoCode}
                                          challengeId={challengeId}
                                          goldnuggetType={goldnuggetType}
                                          staticGoldnuggetSecret={staticGoldnuggetSecret}
                                          challengeName={challengeName}
                                          challengeTitle={challengeTitle}
                                          challengeType={challengeType}
                                          challengeCategories={challengeCategories}
                                          challengeCategory={challengeCategory}
                                          challengeKeywords={challengeKeywords}
                                          selectedKeywords={selectedKeywords}
                                          challengeLevel={challengeLevel}
                                          selectedChallengeUsages={selectedChallengeUsages}
                                          isPrivate={isPrivate}
                                          selectedChallengeCategories={selectedChallengeCategories}
                                          actualEditorStep={actualEditorStep}
                                          lastEditorStep={lastEditorStep}
                                          stepArray={stepArray}
                                          sectionCount={sectionCount}
                                          editorArray={editorArray}
                                          fatChallenge={fatChallenge}
                                          abstractMD={abstractMD}
                                          solutionMD={solutionMD}
                                          abstractID={abstractID}
                                          solutionID={solutionID}
                                          sectionItems={sectionItems}
                                          sectionReferences={sectionReferences}/>
                        </Grid.Column>
                    </Grid.Row>
                </Grid >
            </div>
        );
    }
}

Dashboard.propTypes = {
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
    challengeName: PropTypes.string,
    goldnuggetType: PropTypes.string,
    staticGoldnuggetSecret: PropTypes.string,
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
    instructionId: PropTypes.string,
    hintId: PropTypes.string,
    abstractID: PropTypes.string,
    solutionID: PropTypes.string,
    abstractMD: PropTypes.string,
    solutionMD: PropTypes.string,
    sectionReferences: PropTypes.array
};
