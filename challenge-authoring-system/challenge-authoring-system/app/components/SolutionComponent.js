import React, {Component, PropTypes} from 'react';
import {Segment} from 'semantic-ui-react';
import MarkdownEditorComponent from './MarkdownEditorComponent';
import FileUpload from './FileUpload';
import {getNewSolutionId} from '../actions/actions';

export default class SolutionComponent extends Component {
    componentWillMount() {
        const {dispatch, valueId, challengeId} = this.props;
        if(valueId === '' || valueId === undefined) {
            dispatch(getNewSolutionId(challengeId));
        }
    }

    render = () => {
        const {editorArray, valueId, content, uploadedFiles, mediaIds, languageIsoCode, challengeId, isAuthenticated, errorMessage, dispatch} = this.props;
        if(valueId === '' || valueId === undefined) {
            return null;
        }
        return (
            <div>
                <Segment raised>
                    <MarkdownEditorComponent valueId={valueId} content={content} editorArray={editorArray} dataId={'solution'} challengeId={challengeId} valueType={"SOLUTION"} isAuthenticated={isAuthenticated}
                                             errorMessage={errorMessage} nextEditorStep={'challengeSummaryNavItem'} dispatch={dispatch}/>
                    <FileUpload languageIsoCode={languageIsoCode} challengeId={challengeId} dispatch={dispatch} uploadedFiles={uploadedFiles} mediaIds={mediaIds}/>
                </Segment>

            </div>);
    }
}

SolutionComponent.propTypes = {
    isAuthenticated: PropTypes.bool.isRequired,
    dispatch: PropTypes.func.isRequired,
    uploadedFiles: PropTypes.array,
    mediaIds: PropTypes.array,
    removeEditor: PropTypes.func,
    errorMessage: PropTypes.string,
    valueType: PropTypes.string,
    valueId: PropTypes.string,
    languageIsoCode: PropTypes.string,
    challengeId: PropTypes.string,
    nextEditorStep: PropTypes.string,
    dataId: PropTypes.string,
    editorArray: PropTypes.array,
    content: PropTypes.string
};
