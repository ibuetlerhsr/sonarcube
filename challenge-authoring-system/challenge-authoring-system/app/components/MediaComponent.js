import React, { Component, PropTypes } from 'react';
import {Segment} from 'semantic-ui-react';

const SERVER_ADDRESS = process.env.ADDRESS_CAS_SERVER || 'cas-eu.idocker.hacking-lab.com';
const SERVER_PORT = process.env.SERVER_PORT || 443;
const SERVER_PROTOCOL = process.env.SERVER_PROTOCOL || 'https';

export default class MediaComponent extends Component {

    componentDidMount() {
        // Initialize Drag&Drop EventListener
        const dropZone = document.getElementById('dropzone');
        dropZone.addEventListener('dragover', this.handleDragOver, false);
        dropZone.addEventListener('drop', this.fileChoice, false);
    }

    fileChoice = (event) => {
        const {challengeId} = this.props;
        const dateien = event.target.files; // FileList object
        // eslint-disable-next-line no-cond-assign
        const uploadDatei = dateien[0];

        // Ein Objekt um Dateien einzulesen
        const reader = new FileReader();

        const senddata = {};
        senddata.name = uploadDatei.name;
        senddata.date = uploadDatei.lastModified;
        senddata.size = uploadDatei.size;
        senddata.type = uploadDatei.type;

        reader.onload = function load(theFileData) {
            senddata.fileData = theFileData.target.result;
            const XMLHttpRequest = require('xhr2');
            const xhr = new XMLHttpRequest();
            xhr.open('POST', SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/upload_image?imageId=', true);
            // xhr.setRequestHeader('Content-type', 'application/json');
            xhr.setRequestHeader('Challenge-ID', challengeId );

            xhr.send(senddata);
        };
        reader.readAsDataURL(uploadDatei);
    };

    handleDragOver = (event) => {
        event.stopPropagation();
        event.preventDefault();
        event.dataTransfer.dropEffect = 'copy';
    };

    render() {
        return (
            <div>
                <Segment raised>
                    <div id="dropzone"></div>
                </Segment>
            </div>
        );
    }
}

MediaComponent.propTypes = {
    dispatch: PropTypes.func.isRequired,
    isAuthenticated: PropTypes.bool.isRequired,
    errorMessage: PropTypes.string,
    challengeId: PropTypes.string,
    nextEditorStep: PropTypes.string
};


