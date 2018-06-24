import React, { Component, PropTypes } from 'react';
import {Button, Grid, Segment} from 'semantic-ui-react';
import {translateText, getChallengeDataByLanguage, getTranslationDataByTargetLanguage, setTranslationDataByTargetLanguage} from '../actions/actions';
import {compareOrderedMarkdownItems, extractSectionReferences, extractSectionText} from '../helper/ExtractUtil';
import TranslateMarkdownEditorComponent from '../components/TranslateMarkdownEditorComponent';
import SimpleInputField from './SimpleInputField';
const ReactMarkdown = require('react-markdown');

export default class TranslationComponent extends Component {

    state : {
        modify: boolean,
        translatedTitle: string,
        translatedName: string,
        translatedAbstract: string,
        translatedSolution: string,
        translatedSections: Array,
        translatedSteps: Array} = {translatedTitle: '', translatedName: '', translatedAbstract: '', translatedSolution: '', translatedSections: [], translatedSteps: [], modify: false};

    componentDidMount() {
        const {dispatch, challengeId, targetLanguage} = this.props;
        if(challengeId !== '') {
            dispatch(getChallengeDataByLanguage(challengeId, 'en'));
            dispatch(getTranslationDataByTargetLanguage(challengeId, targetLanguage));
        }
    }

    componentDidUpdate() {
        const {dispatch, challengeId, targetLanguage, fatChallenge, translatedChallenge} = this.props;
        if(challengeId !== '' && fatChallenge.challengeJson === undefined) {
            dispatch(getChallengeDataByLanguage(challengeId, 'en'));
        }
        if(challengeId !== '' && translatedChallenge.challengeId === undefined) {
            dispatch(getTranslationDataByTargetLanguage(challengeId, targetLanguage));
        }
    }

    handleStepTranslation = (steps) => {
        const {targetLanguage, dispatch} = this.props;
        steps.forEach(step => {
            dispatch(translateText(step.stepMD, step.type, step.id, targetLanguage ));
        });
    };

    handleSectionTranslation = (sectionReferences, sectionTexts) => {
        const {targetLanguage, dispatch} = this.props;
        const sectionReferenceArray = extractSectionText(extractSectionReferences(sectionReferences), sectionTexts);
        const context = this;
        sectionReferenceArray.forEach(sectionRef => {
            dispatch(translateText(sectionRef.sectionMD, 'section', sectionRef.id, targetLanguage ));
            context.handleStepTranslation(sectionRef.steps);
        });
    };

    handleTranslateClick = (event) => {
        const {dispatch, fatChallenge, targetLanguage} = this.props;
        event.preventDefault();
        dispatch(translateText(fatChallenge.languageComponents[0].abstractMD, 'abstract', undefined, targetLanguage ));
        dispatch(translateText(fatChallenge.languageComponents[0].solutionMD, 'solution', undefined, targetLanguage ));
        dispatch(translateText(fatChallenge.languageComponents[0].titleMD, 'title', undefined, targetLanguage ));
        dispatch(translateText(fatChallenge.challengeJson.name, 'name', undefined, targetLanguage ));
        dispatch(translateText(fatChallenge.languageComponents[0].titleMD, 'title', undefined, targetLanguage ));
        this.handleSectionTranslation(fatChallenge.challengeJson.sections,  fatChallenge.languageComponents[0].sections);
    };

    fillTranslatedSections = () => {
        const {fatChallenge, sectionTranslations, stepTranslations, translatedChallenge} = this.props;
        let message = '';
        if(sectionTranslations.length > 0 ) {
            const sectionReferenceArray = extractSectionText(extractSectionReferences(fatChallenge.challengeJson.sections),  fatChallenge.languageComponents[0].sections);
            const translatedSections = [];
            sectionTranslations.forEach(section => {
                const foundSection = this.findSectionInArray(sectionReferenceArray, section.sectionId);
                if(stepTranslations.length > 0) {
                    const translatedSteps = [];
                    foundSection.steps.forEach(step => {
                        const foundStep = this.findStepInArray(stepTranslations, step.id);
                        const proveStep = this.findStepInArray(this.extractStepTextsFromSection(translatedChallenge.translatedSections,  section.sectionId), step.id);
                        if(proveStep !== undefined && proveStep.isUserCreated) {
                            message = message + 'Step ' + step.id + ' was already translated by a human and should not be overriden by a Deepl translated value. \n';
                        }
                        if(foundStep !== undefined) {
                            translatedSteps.push({'id': foundStep.stepId, 'stepMD': foundStep.translation, 'type': foundStep.type, 'isUserCreated': false});
                        }
                    });
                    translatedSections.push({'id': section.sectionId, 'sectionMD': section.translation, 'isUserCreated': false, 'steps': translatedSteps});
                } else {
                    translatedSections.push({'id': section.sectionId, 'sectionMD': section.translation, 'isUserCreated': false, 'steps': null});
                }
                const proveSection = this.findSectionInArray(translatedChallenge.translatedSections, section.sectionId);
                if(proveSection !== undefined && proveSection.isUserCreated) {
                    message = message + 'Section ' + section.sectionId + ' was already translated by a human and should not be overriden by a Deepl translated value. \n';
                }
            });
            if(message !== '') {
                translatedSections.push( {'message': message});
            }
            return translatedSections;
        }
        if(this.state.translatedSections.length > 0) {
            const sectionReferenceArray = extractSectionText(extractSectionReferences(fatChallenge.challengeJson.sections),  fatChallenge.languageComponents[0].sections);
            const translatedSections = [];
            this.state.translatedSections.forEach(section => {
                const foundSection = this.findSectionInArray(sectionReferenceArray, section.sectionId);
                if(this.state.translatedSteps.length > 0) {
                    const translatedSteps = [];
                    foundSection.steps.forEach(step => {
                        const foundStep = this.findStepInArray(this.state.translatedSteps, step.id);
                        if(foundStep !== undefined) {
                            translatedSteps.push({'id': foundStep.stepId, 'stepMD': foundStep.translation, 'type': step.type, 'isUserCreated': true});
                        }
                    });
                    translatedSections.push({'id': section.sectionId, 'sectionMD': section.translation, 'isUserCreated': true, 'steps': translatedSteps});
                } else {
                    translatedSections.push({'id': section.sectionId, 'sectionMD': section.translation, 'isUserCreated': true, 'steps': null});
                }
            });
            return translatedSections;
        } else if(this.state.translatedSteps.length > 0) {
            const sectionReferenceArray = extractSectionText(extractSectionReferences(fatChallenge.challengeJson.sections),  fatChallenge.languageComponents[0].sections);
            const translatedSections = [];
            const thisContext = this;
            this.state.translatedSteps.forEach(stepTranslation => {
                const section = thisContext.findSectionWithStep(sectionReferenceArray, stepTranslation.stepId);
                const translatedSteps = [];
                section.steps.forEach(step => {
                    const foundStep = this.findStepInArray(this.state.translatedSteps, step.id);
                    if(foundStep !== undefined) {
                        translatedSteps.push({'id': foundStep.stepId, 'stepMD': foundStep.translation, 'type': step.type, 'isUserCreated': true});
                    }
                });
                translatedSections.push({'id': section.id, 'sectionMD': section.sectionMD, 'isUserCreated': true, 'steps': translatedSteps});
            });
            return translatedSections;
        }
        return null;
    };

    wasChanged = () => {
        const {titleTranslation, solutionTranslation, abstractTranslation, sectionTranslations, stepTranslations} = this.props;
        if(this.state === undefined) {
            return false;
        }
        if(titleTranslation !== '') {
            return true;
        }
        if(this.state.translatedTitle !== '') {
            return true;
        }
        if(abstractTranslation !== '') {
            return true;
        }
        if(this.state.translatedAbstract !== '') {
            return true;
        }
        if(solutionTranslation !== '') {
            return true;
        }
        if(this.state.translatedSolution !== '') {
            return true;
        }
        if(sectionTranslations.length > 0 ) {
            return true;
        }
        if(stepTranslations.length > 0 ) {
            return true;
        }
        if(this.state.translatedSections.length > 0 ) {
            return true;
        }
        if(this.state.translatedSteps.length > 0) {
            return true;
        }
        return false;
    };

    handleSaveClick = () => {
        const {dispatch, challengeId, targetLanguage, titleTranslation, solutionTranslation, abstractTranslation, translatedChallenge} = this.props;
        let message = '';
        let titleObject = {};
        let abstractObject = {};
        let solutionObject = {};
        if(titleTranslation !== '') {
            titleObject = {'isUserCreated': false, 'text': titleTranslation};
            if(translatedChallenge.translatedTitle !== undefined && translatedChallenge.translatedTitle.isUserCreated) {
                message = 'Title was already translated by a human and should not be overriden by a Deepl translated value. \n';
            }
        }
        if(this.state.translatedTitle !== '') {
            titleObject = {'isUserCreated': true, 'text': this.state.translatedTitle};
        }
        if(abstractTranslation !== '') {
            abstractObject = {'isUserCreated': false, 'text': abstractTranslation};
            if(translatedChallenge.translatedAbstract !== undefined && translatedChallenge.translatedAbstract.isUserCreated) {
                message = message + 'Abstract was already translated by a human and should not be overriden by a Deepl translated value. \n';
            }
        }
        if(this.state.translatedAbstract !== '') {
            abstractObject = {'isUserCreated': true, 'text': this.state.translatedAbstract};
        }
        if(solutionTranslation !== '') {
            solutionObject = {'isUserCreated': false, 'text': solutionTranslation};
            if(translatedChallenge.translatedSolution !== undefined && translatedChallenge.translatedSolution.isUserCreated) {
                message = message + 'Solution was already translated by a human and should not be overriden by a Deepl translated value. \n';
            }
        }
        if(this.state.translatedSolution !== '') {
            solutionObject = {'isUserCreated': true, 'text': this.state.translatedSolution};
        }
        let sectionObjects = this.fillTranslatedSections();
        if(message !== '' && sectionObjects[sectionObjects.length - 1].message !== undefined) {
            alert(message + '\n' + sectionObjects[sectionObjects.length - 1].message); // eslint-disable-line no-alert
            // eslint-disable-next-sline no-alert
            if(confirm('If you want to override existing user defined values with the Deepl translations, click OK, or Cancel if you want to change the translations by hand.')) {
                if(sectionObjects[sectionObjects.length - 1].message !== undefined) {
                    sectionObjects = sectionObjects.splice(0, sectionObjects.length - 1);
                }
                const translationObject = {'challengeId': challengeId, 'languageIsoCode': targetLanguage,
                    'translatedTitle': titleObject,
                    'translatedAbstract': abstractObject, 'translatedSolution': solutionObject, 'translatedSections': sectionObjects};
                dispatch(setTranslationDataByTargetLanguage(translationObject));
            }
        } else {
            const translationObject = {'challengeId': challengeId, 'languageIsoCode': targetLanguage,
                'translatedTitle': titleObject,
                'translatedAbstract': abstractObject, 'translatedSolution': solutionObject, 'translatedSections': sectionObjects};
            dispatch(setTranslationDataByTargetLanguage(translationObject));
        }
    };

    handleModifyClick = () => {
        if(this.state !== undefined) {
            this.setState({modify: !this.state.modify});
        }
    };

    extractStepTextsFromSection = (sectionReferences, sectionId) =>{
        let steps = [];
        sectionReferences.forEach(section => {
            if(section.id === sectionId) {
                steps = section.steps;
            }
        });
        return steps.sort(compareOrderedMarkdownItems);
    };

    findSectionInArray = (array, id) => {
        let temp = undefined;
        array.forEach(item => {
            if(item.sectionId === id) {
                temp = item;
            } else if(item.id === id) {
                temp = item;
            }
        });
        return temp;
    };

    removeSectionFromArray = (array, id) => {
        const temp = [];
        array.forEach(item => {
            if(item.sectionId === id) {
                null;
            } else if(item.id === id) {
                null;
            } else {
                temp.push(item);
            }
        });
        return temp;
    };

    findStepInArray = (array, id) => {
        let temp = undefined;
        array.forEach(item => {
            if(item.stepId === id) {
                temp = item;
            } else if(item.id === id) {
                temp = item;
            }
        });
        return temp;
    };

    findSectionWithStep = (array, id) => {
        let temp = undefined;
        array.forEach(item => {
            let stepOuter = null;
            item.steps.forEach(step => {
                if(step.stepId === id) {
                    stepOuter = step;
                } else if(step.id === id) {
                    stepOuter = step;
                }
            });
            if(stepOuter !== null) {
                temp = item;
            }
        });
        return temp;
    };

    removeStepFromArray = (array, id) => {
        const temp = [];
        array.forEach(item => {
            if(item.stepId === id) {
                null;
            } else if(item.id === id) {
                null;
            } else {
                temp.push(item);
            }
        });
        return temp;
    };

    handleChange = (text, valueType, id) => {
        if(valueType === 'ABSTRACT') {
            this.setState({translatedAbstract: text});
        } else if(valueType === 'SOLUTION') {
            this.setState({translatedSolution: text});
        } else if(valueType === 'SECTION') {
            const array = this.removeSectionFromArray(this.state.translatedSections, id);
            array.push({'sectionId': id, 'translation': text});
            this.setState({translatedSections: array});
        } else if(valueType === 'STEP') {
            const array = this.removeStepFromArray(this.state.translatedSteps, id);
            array.push({'stepId': id, 'translation': text});
            this.setState({translatedSteps: array});
        }
    };

    renderStepTranslation = (steps, sectionId) => {
        const {stepTranslations, translatedChallenge, challengeId, dispatch, editorArray } = this.props;
        let stepsTranslated = stepTranslations;
        if(translatedChallenge.challengeId !== undefined) {
            if(stepsTranslated.length === 0) {
                stepsTranslated = this.extractStepTextsFromSection(translatedChallenge.translatedSections, sectionId);
            }
        }
        const components = [];
        let index = 0;
        steps.forEach(stepRef => {
            components.push(<Grid.Row stretched key={stepRef.id}>
                <Grid.Column width={8}>
                    <Segment>
                        <ReactMarkdown source={stepRef.stepMD !== null && stepRef.stepMD !== undefined ? stepRef.stepMD.replace('/media/', '/api/media/') : null}/>
                    </Segment>
                </Grid.Column>
                <Grid.Column width={8}>
                    <Segment style={{height: '100%'}}>
                        {this.state !== undefined && this.state.modify &&
                        <TranslateMarkdownEditorComponent editorArray={editorArray} handleChange={this.handleChange} content={this.findStepInArray(stepsTranslated, stepRef.id) !== undefined && this.findStepInArray(stepsTranslated, stepRef.id).translation === undefined ? stepsTranslated[index].stepMD : this.findStepInArray(stepsTranslated, stepRef.id).translation} dataId={stepRef.id} challengeId={challengeId} valueType={"STEP"} isAuthenticated={true}
                                                          dispatch={dispatch}/>
                        }
                        {(this.state === undefined || !this.state.modify) && stepsTranslated[index] === undefined &&
                        'not translated yet'
                        }
                        {(this.state === undefined || !this.state.modify) && stepsTranslated[index] !== undefined &&
                        <ReactMarkdown source={this.findStepInArray(stepsTranslated, stepRef.id) !== undefined && this.findStepInArray(stepsTranslated, stepRef.id) !== null && this.findStepInArray(stepsTranslated, stepRef.id).translation === undefined ? stepsTranslated[index].stepMD.replace('/media/', '/api/media/') === '' ? 'not translated yet' : stepsTranslated[index].stepMD.replace('/media/', '/api/media/') : this.findStepInArray(stepsTranslated, stepRef.id).translation.replace('/media/', '/api/media/') === '' ? 'not translated yet' : this.findStepInArray(stepsTranslated, stepRef.id).translation.replace('/media/', '/api/media/')}/>
                        }
                    </Segment>
                </Grid.Column>
            </Grid.Row>);
            index++;
        });
        return components;
    };

    renderSectionTranslation = () => {
        const {fatChallenge, sectionTranslations, translatedChallenge, dispatch, challengeId, editorArray} = this.props;
        let sectionTranslated = sectionTranslations;
        if(translatedChallenge.challengeId !== undefined) {
            if(sectionTranslated.length === 0) {
                sectionTranslated = translatedChallenge.translatedSections;
            }
        }
        let components = [];
        let index = 0;
        const context = this;
        const sectionReferenceArray = extractSectionText(extractSectionReferences(fatChallenge.challengeJson.sections),  fatChallenge.languageComponents[0].sections);
        sectionReferenceArray.forEach(sectionRef => {
            components.push(<Grid.Row stretched key={sectionRef.id}>
                <Grid.Column width={8}>
                    <Segment>
                        <ReactMarkdown  source={sectionRef.sectionMD !== null && sectionRef.sectionMD !== undefined ? sectionRef.sectionMD.replace('/media/', '/api/media/') : null}/>
                    </Segment>
                </Grid.Column>
                <Grid.Column width={8}>
                    <Segment style={{height: '100%'}}>
                        {this.state !== undefined && this.state.modify &&
                        <TranslateMarkdownEditorComponent editorArray={editorArray} handleChange={this.handleChange} content={this.findSectionInArray(sectionTranslated, sectionRef.id) !== undefined && this.findSectionInArray(sectionTranslated, sectionRef.id).translation === undefined ? sectionTranslated[index].sectionMD : this.findSectionInArray(sectionTranslated, sectionRef.id).translation} dataId={sectionRef.id} challengeId={challengeId} valueType={"SECTION"} isAuthenticated={true}
                                                          dispatch={dispatch}/>
                        }
                        {(this.state === undefined || !this.state.modify) && sectionTranslated[index] === undefined &&
                        'not translated yet'
                        }
                        {(this.state === undefined || !this.state.modify) && sectionTranslated[index] !== undefined &&
                        <ReactMarkdown source={this.findSectionInArray(sectionTranslated, sectionRef.id) !== undefined && this.findSectionInArray(sectionTranslated, sectionRef.id) !== null && this.findSectionInArray(sectionTranslated, sectionRef.id).translation === undefined ? (sectionTranslated[index].sectionMD.replace('/media/', '/api/media/') === '' ? 'not translated yet' : sectionTranslated[index].sectionMD.replace('/media/', '/api/media/')) : (this.findSectionInArray(sectionTranslated, sectionRef.id).translation.replace('/media/', '/api/media/') === '' ? 'not translated yet' : this.findSectionInArray(sectionTranslated, sectionRef.id).translation.replace('/media/', '/api/media/'))}/>
                        }
                    </Segment>
                </Grid.Column>
            </Grid.Row>);
            components = components.concat(context.renderStepTranslation(sectionRef.steps, sectionRef.id));
            index++;
        });

        return components;
    };

    handleTitleChange = (event: Event) => {
        if(event.target instanceof HTMLInputElement) {
            this.setState({translatedTitle: event.target.value});
        }

        return true;
    };

    render() {
        const {dispatch, challengeId, editorArray, fatChallenge, abstractTranslation, solutionTranslation, titleTranslation, sectionTranslations, stepTranslations, translatedChallenge} = this.props;
        if (fatChallenge.challengeJson === undefined || fatChallenge.challengeJson === null) {
            return null;
        }
        if(abstractTranslation !== '') {
            this.state.translatedAbstract = abstractTranslation;
        }
        if(solutionTranslation !== '') {
            this.state.translatedSolution = solutionTranslation;
        }
        if(titleTranslation !== '') {
            this.state.translatedTitle = titleTranslation;
        }
        if(sectionTranslations.length > 0) {
            this.state.translatedSections = sectionTranslations;
        }
        if(stepTranslations.length > 0) {
            this.state.translatedSteps = stepTranslations;
        }

        let abstractTranslated = abstractTranslation;
        let solutionTranslated = solutionTranslation;
        let titleTranslated = titleTranslation;

        if(translatedChallenge.challengeId !== undefined) {
            if(abstractTranslated === '') {
                abstractTranslated = translatedChallenge.translatedAbstract.text === null ? '' : translatedChallenge.translatedAbstract.text;
            }
            if(solutionTranslated === '') {
                solutionTranslated = translatedChallenge.translatedSolution.text === null ? '' : translatedChallenge.translatedSolution.text;
            }
            if(titleTranslated === '') {
                titleTranslated = translatedChallenge.translatedTitle.text === null ? '' : translatedChallenge.translatedTitle.text;
            }
        }

        return (
            <div>
                <Grid stackable container columns={2} style={{height: '80vh', overflowX: 'auto'}}>
                    <Grid.Row stretched>
                        <Grid.Column computer={8} tablet={16} mobile={16}>
                            <Segment>
                                {fatChallenge.languageComponents[0].titleMD}
                            </Segment>
                        </Grid.Column>
                        <Grid.Column computer={8} tablet={16} mobile={16}>
                            <Segment style={{height: '100%'}}>
                                {this.state !== undefined && this.state.modify &&
                                <SimpleInputField key={'navItem1'} value={this.state.translatedTitle} className={"simpleInputField"} size={"huge"} id={"challengeTitle"} reference={"one"} onChange={(event) => this.handleTitleChange(event)} required placeholder={"Please translate challenge title..."} name={"challengeTitle"}/>
                                }
                                {(this.state === undefined || !this.state.modify) && titleTranslated === '' &&
                                'not translated yet'
                                }
                                {(this.state === undefined || !this.state.modify) && titleTranslated !== '' &&
                                titleTranslated
                                }
                            </Segment>
                        </Grid.Column>
                    </Grid.Row>
                    <Grid.Row stretched>
                        <Grid.Column computer={8} tablet={16} mobile={16}>
                            <Segment>
                                <ReactMarkdown source={fatChallenge.languageComponents[0] !== undefined && fatChallenge.languageComponents[0] !== null && fatChallenge.languageComponents[0].abstractMD !== undefined && fatChallenge.languageComponents[0].abstractMD !== null ? fatChallenge.languageComponents[0].abstractMD.replace('/media/', '/api/media/') : '##### No abstract defined for challenge'}/>
                            </Segment>
                        </Grid.Column>
                        <Grid.Column computer={8} tablet={16} mobile={16}>
                            <Segment style={{height: '100%'}}>
                                {this.state !== undefined && this.state.modify &&
                                <TranslateMarkdownEditorComponent editorArray={editorArray} handleChange={this.handleChange} content={abstractTranslated} dataId={'abstract'} challengeId={challengeId} valueType={"ABSTRACT"} isAuthenticated={true}
                                                                  dispatch={dispatch}/>
                                }
                                {(this.state === undefined || !this.state.modify) && abstractTranslated === '' &&
                                'not translated yet'
                                }
                                {(this.state === undefined || !this.state.modify) && abstractTranslated !== '' &&
                                <ReactMarkdown source={abstractTranslated.replace('/media/', '/api/media/')} />
                                }
                            </Segment>
                        </Grid.Column>
                    </Grid.Row>
                    <Grid.Row stretched>
                        <Grid.Column computer={8} tablet={16} mobile={16}>
                            <Segment>
                                <ReactMarkdown source={fatChallenge.languageComponents[0] !== undefined && fatChallenge.languageComponents[0] !== null && fatChallenge.languageComponents[0].solutionMD !== undefined && fatChallenge.languageComponents[0].solutionMD !== null ? fatChallenge.languageComponents[0].solutionMD.replace('/media/', '/api/media/') : '#### No solution defined for challenge'}/>
                            </Segment>
                        </Grid.Column>
                        <Grid.Column computer={8} tablet={16} mobile={16}>
                            <Segment style={{height: '100%'}}>
                                {this.state !== undefined && this.state.modify &&
                                <TranslateMarkdownEditorComponent editorArray={editorArray} handleChange={this.handleChange} content={solutionTranslated} dataId={'solution'} challengeId={challengeId} valueType={"SOLUTION"} isAuthenticated={true}
                                                                  dispatch={dispatch}/>
                                }
                                {(this.state === undefined || !this.state.modify) && solutionTranslated === '' &&
                                'not translated yet'
                                }
                                {(this.state === undefined || !this.state.modify) && solutionTranslated !== '' &&
                                <ReactMarkdown source={solutionTranslated.replace('/media/', '/api/media/')} />
                                }
                            </Segment>
                        </Grid.Column>
                    </Grid.Row>
                    {this.renderSectionTranslation()}
                </Grid>
                <Grid stackable container columns={2}>
                    <Grid.Row>
                        <Grid.Column computer={5} tablet={10} mobile={16}>
                            <Segment raised>
                                <Button primary fluid onClick={(event) => this.handleTranslateClick(event)}>Translate Challenge</Button>
                            </Segment>
                        </Grid.Column>
                        <Grid.Column computer={6} tablet={12} mobile={16}>
                            <Segment raised>
                                <Button disabled={!this.wasChanged()} primary fluid onClick={(event) => this.handleSaveClick(event)}>Save Translation</Button>
                            </Segment>
                        </Grid.Column>
                        <Grid.Column computer={5} tablet={10} mobile={16}>
                            <Segment raised>
                                <Button primary fluid onClick={(event) => this.handleModifyClick(event)}>Modify Translation</Button>
                            </Segment>
                        </Grid.Column>
                    </Grid.Row>
                </Grid>
            </div>);
    }


}

TranslationComponent.propTypes = {
    fatChallenge: PropTypes.object.isRequired,
    translatedChallenge: PropTypes.object,
    challengeId: PropTypes.string.isRequired,
    targetLanguage: PropTypes.string.isRequired,
    dispatch: PropTypes.func.isRequired,
    isAuthenticated: PropTypes.bool.isRequired,
    errorMessage: PropTypes.string.isRequired,
    abstractTranslation: PropTypes.string.isRequired,
    solutionTranslation: PropTypes.string.isRequired,
    titleTranslation: PropTypes.string.isRequired,
    nameTranslation: PropTypes.string.isRequired,
    sectionTranslations: PropTypes.array.isRequired,
    stepTranslations: PropTypes.array.isRequired,
    editorArray: PropTypes.array
};
