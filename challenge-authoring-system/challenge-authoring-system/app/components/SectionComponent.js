import React, { Component, PropTypes } from 'react';
import {Grid, Button, Segment} from 'semantic-ui-react';

import InstructionComponent from '../components/InstructionComponent';
import HintComponent from '../components/HintComponent';
import {setAddItemCount, setAddSectionItems, removeStep, getNewHintId, getNewInstructionId, setShowToSectionReferences} from '../actions/actions';
import SortableList from './SortableList';
import TooltipButton from './TooltipButton';

export default class SectionComponent extends Component {
    constructor(props) {
        super(props);
        this.removeEditor = this.removeEditor.bind(this);
    }

    state: {
        items: Array
    } = {items: []};

    componentWillUnmount = () => {
        this.replaceSectionItemsArray(this.state.items);
    };

    handleAddStepClick = (event, dispatch, dataId, hintCount, instructionCount, type) => {
        const {challengeId} = this.props;
        event.preventDefault();
        let instructions = instructionCount;
        let hints = hintCount;
        if(type === 'instruction') {
            instructions = instructions + 1;
            dispatch(getNewInstructionId(challengeId, dataId));
        } else if(type === 'hint') {
            hints = hints + 1;
            dispatch(getNewHintId(challengeId, dataId));
        }
        this.fillStepArray(dataId, instructions, hints);
    };

    handleAddMediaClick = (event) => {
        event.preventDefault();
        this.state.medias++;
    };

    handleCompressClick = (event) => {
        const {dataId, dispatch} = this.props;
        event.preventDefault();
        dispatch(setShowToSectionReferences(dataId, false));
    };

    handleExpandClick = (event) => {
        const {dataId, dispatch} = this.props;
        event.preventDefault();
        dispatch(setShowToSectionReferences(dataId, true));
    };

    extractSectionId = (dataId) => {
        const index = dataId.indexOf('_');
        return dataId.substring(0, index);
    };

    extractStepId = (dataId) => {
        const index = dataId.indexOf('_');
        return dataId.substring(index + 1);
    };

    getStepType = (stepId) => {
        if(stepId.substring(0, 1) === 's') {
            return 'step';
        }
        if(stepId.substring(0, 1) === 'h') {
            return 'hint';
        }
        if(stepId.substring(0, 1) === 'i') {
            return 'instruction';
        }
        return '';
    };

    decreaseCount = (dataId) => {
        const {stepArray} = this.props;
        if(dataId !== undefined && dataId !== '') {
            const sectionId = this.extractSectionId(dataId);
            const stepId = this.extractStepId(dataId);
            const stepType = this.getStepType(stepId);
            stepArray.forEach((item, i) => {
                if (item.key === sectionId) {
                    if (stepType === 'hint') {
                        stepArray[i] = this.createStepArrayItem(sectionId, stepArray[i].instructionCount, stepArray[i].hintCount - 1);
                    }
                    if (stepType === 'instruction') {
                        stepArray[i] = this.createStepArrayItem(sectionId, stepArray[i].instructionCount - 1, stepArray[i].hintCount);
                    }
                }
            });
            return stepArray;
        }
        return null;
    };

    removeEntryFromEditorArray = (dataId) => {
        const {editorArray} = this.props;
        if(dataId !== undefined && dataId !== '') {
            const newEditorArray = [];
            editorArray.forEach(editor => {
                if (editor.key !== dataId) {
                    newEditorArray.push(editor);
                }
            });
            return newEditorArray;
        }
        return null;
    };

    removeEditor = (dataId) => {
        const {dispatch, stepReferences} = this.props;
        if(dataId !== undefined && dataId !== '') {
            const newItemsList = [];
            this.state.items.forEach(item => {
                if(item.props.id !== dataId) {
                    newItemsList.push(item);
                }
            });
            if(stepReferences !== null && stepReferences !== undefined && stepReferences.length > 0) {
                const editorArray = this.removeEntryFromEditorArray(dataId);
                const stepArray = this.decreaseCount(dataId);
                dispatch(setAddItemCount(stepArray, editorArray));
            } else {
                dispatch(removeStep(dataId));
            }
            // this.setState({items: newItemsList});
        }
    };

    renderInstructionComponents = () => {
        const { dispatch, editorArray, stepReferences, isAuthenticated, challengeId, dataId} = this.props;
        const component = [];
        if(stepReferences !== null && stepReferences !== undefined && stepReferences.length > 0) {
            stepReferences.forEach(stepReference => {
                if(stepReference.type === 'instruction') {
                    component.push(<InstructionComponent show={stepReference.show} content={stepReference.stepMD} instructionId={stepReference.id} sectionId={dataId} removeEditor={this.removeEditor} editorArray={editorArray} dataId={stepReference.id} id={stepReference.id} nextEditorStep={'challengeStepNavItem'} challengeId={challengeId} dispatch={dispatch} isAuthenticated={isAuthenticated}/>);
                }
            });
        }
        return component;
    };

    renderHintComponents = () => {
        const { dispatch, editorArray, stepReferences, isAuthenticated, challengeId, dataId} = this.props;
        const component = [];
        if(stepReferences !== null && stepReferences !== undefined && stepReferences.length > 0) {
            stepReferences.forEach(stepReference => {
                if (stepReference.type === 'hint') {
                    component.push(<HintComponent show={stepReference.show} content={stepReference.stepMD} hintId={stepReference.id} sectionId={dataId} removeEditor={this.removeEditor} editorArray={editorArray} dataId={stepReference.id} id={stepReference.id} nextEditorStep={'challengeStepNavItem'} challengeId={challengeId} dispatch={dispatch} isAuthenticated={isAuthenticated}/>);
                }
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

    concatNewStepItemToItemsArray(instructionCount, hintCount) {
        const instructionsToRender = this.renderInstructionComponents(instructionCount);
        const hintsToRender = this.renderHintComponents(hintCount);
        const newItemsList = [];
        this.state.items.forEach(item => {
            if(instructionsToRender.filter(instruction => (item.props.id === instruction.props.id)).length > 0) {
                newItemsList.push(item);
            } else if (hintsToRender.filter(hint => (item.props.id === hint.props.id)).length > 0) {
                newItemsList.push(item);
            }
        });

        instructionsToRender.forEach(item => {
            if(newItemsList.filter(instruction => (item.props.id === instruction.props.id)).length === 0) {
                newItemsList.push(item);
            }
        });
        hintsToRender.forEach(item => {
            if (newItemsList.filter(hint => (item.props.id === hint.props.id)).length === 0) {
                newItemsList.push(item);
            }
        });
        const thisContext = this;
        if(hintsToRender.length > 0) {
            newItemsList.forEach(function(value, index) {
                if(value !== null) {
                    const step = thisContext.getShowFromSectionsToRender(value.props.dataId, hintsToRender);
                    if(step !== null) {
                        newItemsList[index] = step;
                    }
                }
            });
        }
        if(instructionsToRender.length > 0) {
            newItemsList.forEach(function(value, index) {
                if(value !== null) {
                    const step = thisContext.getShowFromSectionsToRender(value.props.dataId, instructionsToRender);
                    if(step !== null) {
                        newItemsList[index] = step;
                    }
                }
            });
        }
        return newItemsList;
    }

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

    createStepArrayItem = (id, instructionCount, hintCount) => {
        return {key: id, instructionCount: instructionCount, hintCount: hintCount };
    };

    createSectionArrayItem = (sectionId, items) => {
        return {key: sectionId, items: items };
    };

    getStepCountObject = (dataId) => {
        const {stepArray} = this.props;
        const stepCountArray = stepArray.filter(entry => (entry.key === dataId));
        if(stepCountArray.length === 1) {
            return stepCountArray[0];
        }
        return null;
    };

    getSectionItemsPerId = (dataId) => {
        const {sectionItems} = this.props;
        const sectionArray = sectionItems.filter(entry => (entry.key === dataId));
        if(sectionArray.length === 1) {
            return sectionArray[0].items;
        }
        return null;
    };

    replaceStepArrayItem = (instructionCount, hintCount) => {
        const {stepArray, dataId, dispatch} = this.props;
        stepArray.forEach((item, i) => {
            if(item.key === dataId) {
                stepArray[i] = this.createStepArrayItem(dataId, instructionCount, hintCount);
            }});
        dispatch(setAddItemCount(stepArray));
    };

    replaceSectionItemsArray = (items) => {
        if(items.length !== 0) {
            const {sectionItems, dataId, dispatch} = this.props;
            const arrayItem = this.createSectionArrayItem(dataId, items);
            if(sectionItems.length === 0) {
                sectionItems.push(arrayItem);
            } else {
                sectionItems.forEach((item, i) => {
                    if(item.key === dataId) {
                        sectionItems[i] = arrayItem;
                    }});
            }
            dispatch(setAddSectionItems(sectionItems));
        }
    };

    fillStepArray = (dataId, instructionCount, hintCount) => {
        const {stepArray, dispatch} = this.props;
        const filteredObject = stepArray.filter(step =>(step.key === dataId));
        if(filteredObject.length > 0) {
            this.replaceStepArrayItem(instructionCount, hintCount);
        } else {
            stepArray.push(this.createStepArrayItem(dataId, instructionCount, hintCount));
        }

        dispatch(setAddItemCount(stepArray));
        this.forceUpdate();
    };

    render() {
        const { dispatch, dataId, stepReferences, show} = this.props;
        const context = this;
        const stepCountObject = this.getStepCountObject(dataId);
        const items = this.getSectionItemsPerId(dataId);
        if(stepCountObject !== null && (stepCountObject.hintCount > 0 || stepCountObject.instructionCount > 0)) {
            if(items === null) {
                if(this.state !== null && this.state !== undefined && this.state.items.length === 0) {
                    this.state.items = this.state.items.concat(...this.renderHintComponents(stepCountObject.hintCount));
                    this.state.items = this.state.items.concat(...this.renderInstructionComponents(stepCountObject.instructionCount));
                } else {
                    this.state.items = this.concatNewStepItemToItemsArray(stepCountObject.instructionCount, stepCountObject.hintCount);
                }
            } else {
                if(this.state !== null && this.state !== undefined && this.state.items.length === 0) {
                    this.state.items = items;
                } else {
                    this.state.items = this.concatNewStepItemToItemsArray(stepCountObject.instructionCount, stepCountObject.hintCount);
                }
            }
        }

        if(stepReferences !== undefined && stepReferences.length > 0) {
            if(items === null) {
                if(this.state !== null && this.state !== undefined && this.state.items.length === 0) {
                    this.state.items = this.state.items.concat(...this.renderHintComponents());
                    this.state.items = this.state.items.concat(...this.renderInstructionComponents());
                } else {
                    this.state.items = this.concatNewStepItemToItemsArray();
                }
            } else {
                if(this.state !== null && this.state !== undefined && this.state.items.length === 0) {
                    this.state.items = items;
                } else {
                    this.state.items = this.concatNewStepItemToItemsArray(undefined, undefined);
                }
            }
        }

        return (
            <Grid stackable container columns={1} style={{borderLeftWidth: 'thin', borderLeftStyle: 'dashed', borderLeftColor: '#b9b9b9', borderTopWidth: 'thin', borderTopStyle: 'dashed', borderTopColor: '#b9b9b9', padding: 10}}>

                <Grid.Row>
                    {show &&
                    <Grid.Column>
                        <Button.Group>
                            <TooltipButton dataId={dataId} buttonStyle={'icon'} isCustomIcon={true}
                                           iconStyle={'instructionAdd'} id={'addInstructionButton'}
                                           dispatch={dispatch}
                                           instructionCount={stepCountObject !== null ? stepCountObject.instructionCount : 0}
                                           hintCount={stepCountObject !== null ? stepCountObject.hintCount : 0}
                                           type={'instruction'} showText={'+'} tooltip={'Add Instruction'}
                                           handleClick={context.handleAddStepClick}/>
                            <TooltipButton dataId={dataId} buttonStyle={'icon'} isCustomIcon={true}
                                           iconStyle={'hintAdd'} id={'addHintButton'} dispatch={dispatch}
                                           instructionCount={stepCountObject !== null ? stepCountObject.instructionCount : 0}
                                           hintCount={stepCountObject !== null ? stepCountObject.hintCount : 0}
                                           type={'hint'} showText={'+'} tooltip={'Add Hint'}
                                           handleClick={context.handleAddStepClick}/>
                            <TooltipButton dataId={dataId} buttonStyle={'icon'} isCustomIcon iconStyle={'compress'}
                                           id={'compressSectionButton'} dispatch={dispatch} type={'compress'}
                                           showText={''} tooltip={'Compress'}
                                           handleClick={context.handleCompressClick}/>
                        </Button.Group>
                        {this.state !== null && this.state !== undefined &&
                        <SortableList items={this.state.items} onChange={(order) => this.changeOrder(order)}/>
                        }
                    </Grid.Column>
                    }
                    {!show &&
                    <Segment raised style={{float: 'left', width: '100vh'}}>
                        <div style={{float: 'left', width: '95vh'}}>
                            <TooltipButton dataId={dataId} buttonStyle={'icon'} isCustomIcon iconStyle={'expand'}
                                           id={'expandSectionButton'} dispatch={dispatch} type={'expand'}
                                           showText={''} tooltip={'Expand'}
                                           handleClick={context.handleExpandClick} divStyle={{float: 'left'}}/>
                            <h2 style={{display: 'inline-block', float: 'right'}}>{dataId}</h2>
                        </div>
                    </Segment>
                    }
                </Grid.Row>
            </Grid>
        );
    }
}

SectionComponent.propTypes = {
    dispatch: PropTypes.func.isRequired,
    isAuthenticated: PropTypes.bool.isRequired,
    dataId: PropTypes.string.isRequired,
    sectionId: PropTypes.string,
    errorMessage: PropTypes.string,
    challengeId: PropTypes.string,
    stepArray: PropTypes.array,
    editorArray: PropTypes.array,
    sectionItems: PropTypes.array,
    instructionId: PropTypes.string,
    hintId: PropTypes.string,
    stepReferences: PropTypes.array,
    content: PropTypes.string,
    show: PropTypes.bool
};
