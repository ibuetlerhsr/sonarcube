/* eslint-disable */
import React, { Component, PropTypes } from 'react';
import DropzoneComponent from 'react-dropzone-component';
import '../style/filepicker.css';
import '../../node_modules/dropzone/dist/min/dropzone.min.css';
const SERVER_ADDRESS = process.env.ADDRESS_CAS_SERVER || 'cas-eu.idocker.hacking-lab.com';
import {setTitleImageFile, setUploadedFiles, removeTitleImageFile} from '../actions/actions';
const SERVER_PORT = process.env.SERVER_PORT || 443;
const SERVER_PROTOCOL = process.env.SERVER_PROTOCOL || 'https';
const BASE_POST_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/upload_image';

if (typeof document !== 'undefined') {
    const $ = require('jquery');
}

export default class FileUpload extends Component {

    state = {
        elementFileIcons: [],
        elementMess: {}
    };
    success = (file, response) => {
        const{dispatch, maxFiles} = this.props;
        console.log('uploaded', file);
        console.log(response);
        if(this.dropzone !== undefined && this.dropzone.files[0] !== null && this.dropzone.files[0] !== undefined) {
            if(maxFiles === 1 ) {
                dispatch(setTitleImageFile(this.dropzone.files[0], response));
            } else {
                dispatch(setUploadedFiles(this.dropzone.files, response));
            }
        }
    };

    removedfile = (file) => {
        const {dispatch, titleImageId} = this.props;
        dispatch(removeTitleImageFile(titleImageId));
        console.log('removing...', file);
        if(this.dropzone.files.length === 0) {
            for(let i = 0; i < 4; i++) {
                this.dropzone.element.appendChild(this.state.elementFileIcons[i]);
            }
            this.dropzone.element.appendChild(this.state.elementMess[0]);
        }
    };

    handleUploadedFile = (ds, file) => {
        ds.emit('addedfile', file);
        ds.createThumbnail(file, ds.options.thumbnailWidth, ds.options.thumbnailHeight, ds.options.thumbnailMethod, true, (dataUrl) => {
            ds.emit('thumbnail', file, dataUrl);
        });
        ds.emit('complete', file);
        file.previewElement.classList.add('dz-processing');
        file.previewElement.classList.add('dz-complete');
        file.previewElement.classList.add('dz-success');
        ds.files.push(file);
    };

    render = () => {
        const{challengeId, acceptedFiles, iconFiletypes, languageIsoCode, titleImageFile, uploadedFiles, mediaIds, maxFiles} = this.props;
        let setMaxFiles = undefined;
        setMaxFiles = maxFiles !== -1;
        let postURL = BASE_POST_URL;
        if(challengeId !== '') {
            postURL = postURL + '?challengeId=' + challengeId;
        }
        if(languageIsoCode !== '') {
            postURL = postURL + '&languageCode=' + languageIsoCode;
        }
        const componentConfig = {
            iconFiletypes: iconFiletypes,
            showFiletypeIcon: true,
            postUrl: postURL
        };
        const djsConfig = {
            addRemoveLinks: true,
            acceptedFiles: acceptedFiles
        };
        const eventHandlers = {
            init: (dropzone) => {
                this.dropzone = dropzone;
                if(setMaxFiles) {
                    this.dropzone.options.maxFiles = maxFiles;
                }
                if(titleImageFile !== undefined || (uploadedFiles !== undefined && uploadedFiles.length !== 0)) {
                    const files = [];
                    if(titleImageFile !== undefined) {
                        files[0] = titleImageFile;
                    } else {
                        files.concat(uploadedFiles);
                    }

                    const ds = this.dropzone;
                    const fileIcons = $('.filepicker-file-icon');
                    this.state.elementFileIcons = fileIcons;
                    fileIcons.remove();
                    const message = $('.dz-default.dz-message');
                    this.state.elementMess = message;
                    message.remove();
                    if(titleImageFile !== undefined) {
                        this.handleUploadedFile(ds, titleImageFile);
                    } else {
                        for(let i = 0; i < uploadedFiles.length; i++) {
                            this.handleUploadedFile(ds, uploadedFiles[i]);
                        }
                    }
                }
            },
            success: this.success,
            removedfile: this.removedfile
        };

        return (<DropzoneComponent config={componentConfig}
                          eventHandlers={eventHandlers}
                          djsConfig={djsConfig} />);
    }
}

FileUpload.propTypes = {
    challengeId: PropTypes.string,
    iconFiletypes: PropTypes.array,
    acceptedFiles: PropTypes.string,
    languageIsoCode: PropTypes.string,
    dispatch: PropTypes.func.isRequired,
    titleImageFile: PropTypes.object,
    titleImageId: PropTypes.string,
    uploadedFiles: PropTypes.array,
    mediaIds: PropTypes.array,
    maxFiles: PropTypes.number
};

FileUpload.defaultProps = {
    maxFiles: -1,
    iconFiletypes: ['.jpg', '.png', '.gif', '.jpeg'],
    acceptedFiles: 'image/jpeg,image/png,image/gif, image/jpg'
};
