import React, { Component, PropTypes } from 'react';
import {Segment, Button, } from 'semantic-ui-react';
import FileUpload from './FileUpload';
import SimpleInputField from './SimpleInputField';

export default class TitelComponent extends Component {
    componentDidMount = () => {
        const $ = require('jquery');
        $('.simpleInputField').keyup(function simpleInputOnEnterKeyUp(event) {
            if (event.keyCode === 13) {
                $('#challengeTitleNavItemButton').click();
            }
        });
    };

    componentDidUpdate = () => {
        const $ = require('jquery');
        $('.simpleInputField').keyup(function simpleInputOnEnterKeyUp(event) {
            if (event.keyCode === 13) {
                $('#challengeTitleNavItemButton').click();
            }
        });
    };

    render() {
        const { titleImageId, languageIsoCode, dispatch, challengeId, challengeTitle, titleImageFile, handleTitleChange, submitHandler, disabled} = this.props;

        return (
            <Segment raised>
                <SimpleInputField key={'navItem1'} value={challengeTitle} className={"simpleInputField"} size={"huge"} id={"challengeTitle"} reference={"one"} onChange={handleTitleChange} required placeholder={"Please insert challenge title..."} name={"challengeTitle"} labelText={"Title"}/>
                <FileUpload titleImageId={titleImageId} languageIsoCode={languageIsoCode} maxFiles={1} challengeId={challengeId} dispatch={dispatch} titleImageFile={titleImageFile}/>
                <Button primary fluid disabled={disabled} id={'challengeTitleNavItemButton'} onClick={submitHandler}>Submit</Button>
            </Segment>
        );
    }
}

TitelComponent.propTypes = {
    handleTitleChange: PropTypes.func.isRequired,
    languageIsoCode: PropTypes.string,
    dispatch: PropTypes.func.isRequired,
    titleImageFile: PropTypes.object,
    titleImageId: PropTypes.string,
    submitHandler: PropTypes.func.isRequired,
    disabled: PropTypes.bool.isRequired,
    challengeTitle: PropTypes.string.isRequired,
    challengeId: PropTypes.string.isRequired
};

