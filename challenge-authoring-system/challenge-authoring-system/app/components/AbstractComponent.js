import React, {Component, PropTypes} from 'react';
import {Segment} from 'semantic-ui-react';
import MarkdownEditorComponent from './MarkdownEditorComponent';
import {getNewAbstractId} from '../actions/actions';

export default class AbstractComponent extends Component {
    componentWillMount() {
        const {dispatch, valueId, challengeId} = this.props;
        if(valueId === '' || valueId === undefined) {
            dispatch(getNewAbstractId(challengeId));
        }
    }

    render = () => {
        const {editorArray, valueId, content, challengeId, isAuthenticated, errorMessage, dispatch} = this.props;
        if(valueId === '' || valueId === undefined) {
            return null;
        }
        return (
            <div>
                <Segment raised>
                    <MarkdownEditorComponent valueId={valueId} content={content} editorArray={editorArray} dataId={'abstract'} challengeId={challengeId} valueType={"ABSTRACT"} isAuthenticated={isAuthenticated}
                                             errorMessage={errorMessage} nextEditorStep={'challengeStepNavItem'} dispatch={dispatch}/>
                </Segment>

            </div>);
    }
}

AbstractComponent.propTypes = {
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
