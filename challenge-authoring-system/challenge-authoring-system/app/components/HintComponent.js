import React, { Component, PropTypes } from 'react';
import MarkdownEditorComponent from '../components/MarkdownEditorComponent';

export default class HintComponent extends Component {

    render() {
        const {dispatch, removeEditor, show, content, sectionId, hintId, editorArray, id, dataId, nextEditorStep, isAuthenticated, errorMessage, challengeId} = this.props;
        if(hintId === '' || hintId === undefined) {
            return null;
        }
        return (
            <div data-id={dataId} id={id}>
                <MarkdownEditorComponent show={show} valueId={hintId} sectionId={sectionId} content={content} removeEditor={removeEditor} editorArray={editorArray} dataId={dataId} dispatch={dispatch} challengeId={challengeId} valueType={"HINT"} isAuthenticated={isAuthenticated}
                                         errorMessage={errorMessage} nextEditorStep={nextEditorStep}/>
            </div>
        );
    }
}

HintComponent.propTypes = {
    isAuthenticated: PropTypes.bool.isRequired,
    dispatch: PropTypes.func.isRequired,
    removeEditor: PropTypes.func.isRequired,
    errorMessage: PropTypes.string,
    challengeId: PropTypes.string,
    nextEditorStep: PropTypes.string,
    dataId: PropTypes.string,
    sectionId: PropTypes.string,
    hintId: PropTypes.string,
    id: PropTypes.string,
    editorArray: PropTypes.array,
    content: PropTypes.string,
    show: PropTypes.bool,
};
