import React, { Component, PropTypes } from 'react';
import MarkdownEditorComponent from '../components/MarkdownEditorComponent';

export default class InstructionComponent extends Component {
    render() {
        const {dispatch, removeEditor, show, instructionId, sectionId, content, editorArray, id, dataId, nextEditorStep, isAuthenticated, errorMessage, challengeId} = this.props;
        if(instructionId === '' || instructionId === undefined) {
            return null;
        }
        return (
            <div data-id={dataId} id={id}>
                <MarkdownEditorComponent show={show} valueId={instructionId} sectionId={sectionId} content={content} removeEditor={removeEditor} editorArray={editorArray} dataId={dataId} dispatch={dispatch} challengeId={challengeId} valueType={"INSTRUCTION"} isAuthenticated={isAuthenticated}
                                         errorMessage={errorMessage} nextEditorStep={nextEditorStep}/>
            </div>
        );
    }
}

InstructionComponent.propTypes = {
    isAuthenticated: PropTypes.bool.isRequired,
    dispatch: PropTypes.func.isRequired,
    removeEditor: PropTypes.func.isRequired,
    errorMessage: PropTypes.string,
    challengeId: PropTypes.string,
    nextEditorStep: PropTypes.string,
    dataId: PropTypes.string,
    sectionId: PropTypes.string,
    instructionId: PropTypes.string,
    id: PropTypes.string,
    editorArray: PropTypes.array,
    content: PropTypes.string,
    show: PropTypes.bool,
};
