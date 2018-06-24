const SERVER_ADDRESS = process.env.ADDRESS_CAS_SERVER || 'cas-eu.idocker.hacking-lab.com';
const SERVER_PORT = process.env.SERVER_PORT || 443;
const SERVER_PROTOCOL = process.env.SERVER_PROTOCOL || 'https';
const SERVER_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api';
const CONSUMER_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/consumers';
const GET_CHALLENGE_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/challenges/';
const POST_NEW_CHALLENGE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/challenge/createNewChallenge';
const GET_TRANSLATED_CHALLENGE_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/translatedChallenges/';
const GET_NEW_CHALLENGE_ID_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/challenge/getNewChallengeId';
const GET_NEW_SECTION_ID_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/section/getNewSectionId';
const GET_NEW_INSTRUCTION_ID_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/step/getNewInstructionId';
const GET_NEW_SOLUTION_ID_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/challenge/getNewSolutionId';
const GET_NEW_ABSTRACT_ID_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/challenge/getNewAbstractId';
const GET_SOLUTION_ID_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/challenge/getSolutionId';
const GET_ABSTRACT_ID_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/challenge/getAbstractId';
const GET_NEW_HINT_ID_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/step/getNewHintId';
const CHALLENGE_LEVEL_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/levels';
const CHALLENGE_USAGE_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/usages';
const CHALLENGE_KEYWORD_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/keywords';
const CHALLENGE_TYPE_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/types';
const CHALLENGE_CATEGORY_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/categories';
const IMAGE_UPLOAD_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/upload_image_b64';
const IMAGE_REMOVE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/removeImage';
const REMOVE_STEP_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/step/removeStep';
const DEEPL_URL = 'https://api.deepl.com/v1/translate';

import {extractNonTranslatableParts, fillTranslatedText} from '../helper/ExtractUtil';

// Three possible states for our logout process as well.
// Since we are using JWTs, we just need to remove the token
// from localStorage. These actions are more useful if we
// were calling the API to log the user out
export const ERROR_OCCURRED = 'ERROR_OCCURRED';
export const CHALLENGE_REQUEST = 'CHALLENGE_REQUEST';
export const CHALLENGE_SUCCESS = 'CHALLENGE_SUCCESS';
export const CHALLENGE_FAILURE = 'CHALLENGE_FAILURE';
export const STEP_REQUEST = 'STEP_REQUEST';
export const STEP_SUCCESS = 'STEP_SUCCESS';
export const STEP_FAILURE = 'STEP_FAILURE';
export const CONSUMER_REQUEST = 'CONSUMER_REQUEST';
export const CONSUMER_SUCCESS = 'CONSUMER_SUCCESS';
export const CONSUMER_FAILURE = 'CONSUMER_FAILURE';
export const NAVIGATION_REQUEST = 'NAVIGATION_REQUEST';
export const NAVIGATION_SUCCESS = 'NAVIGATION_SUCCESS';
export const NAVIGATION_FAILURE = 'NAVIGATION_FAILURE';
export const TRANSLATION_REQUEST = 'TRANSLATION_REQUEST';
export const TRANSLATION_SUCCESS = 'TRANSLATION_SUCCESS';
export const TRANSLATION_FAILURE = 'TRANSLATION_FAILURE';
export const LOGIN_REQUEST = 'LOGIN_REQUEST';
export const LOGIN_SUCCESS = 'LOGIN_SUCCESS';
export const LOGIN_FAILURE = 'LOGIN_FAILURE';

function receiveToken(value) {
    return {
        type: LOGIN_SUCCESS,
        isAuthenticated: value
    };
}

function receiveEditorNavigationRequest(actualStep, lastStep) {
    return {
        type: NAVIGATION_SUCCESS,
        actualEditorStep: actualStep,
        lastEditorStep: lastStep
    };
}

function receiveTranslation(translation, type, id) {
    return {
        type: TRANSLATION_SUCCESS,
        translation: translation,
        markdownType: type,
        markdownId: id
    };
}

function receiveConsumer(consumerId, consumerURL) {
    return {
        type: CONSUMER_SUCCESS,
        selectedConsumer: consumerId,
        consumerURL: consumerURL
    };
}

function receiveConsumers(consumerList) {
    return {
        type: CONSUMER_SUCCESS,
        consumerList: consumerList
    };
}

function receiveStepAddItemRequest(stepArray, editorArray) {
    return {
        type: STEP_SUCCESS,
        stepArray: stepArray,
        editorArray: editorArray
    };
}

export function setAddItemCount(stepArray, editorArray) {
    return dispatch => {
        dispatch(receiveStepAddItemRequest(stepArray, editorArray));
    };
}

function receiveSectionAddItemRequest(sectionCount) {
    return {
        type: STEP_SUCCESS,
        sectionCount: sectionCount
    };
}

export function setAddSectionCount(sectionCount) {
    return dispatch => {
        dispatch(receiveSectionAddItemRequest(sectionCount));
    };
}

function receiveSectionItemsRequest(sectionItems) {
    return {
        type: STEP_SUCCESS,
        sectionItems: sectionItems
    };
}

export function setAddSectionItems(sectionItems) {
    return dispatch => {
        dispatch(receiveSectionItemsRequest(sectionItems));
    };
}

function receiveEditorArray(editorArray) {
    return {
        type: STEP_SUCCESS,
        editorArray: editorArray
    };
}

export function setEditorArray(editorArray) {
    return dispatch => {
        dispatch(receiveEditorArray(editorArray));
    };
}

function receiveMessageFromServer(context, message, isError) {
    return {
        type: ERROR_OCCURRED,
        message: message,
        errorContext: context,
        hasError: isError
    };
}

function receiveErrorFromCatch(context, message) {
    return {
        type: ERROR_OCCURRED,
        message: message,
        errorContext: context,
        hasError: true
    };
}

function receiveSpecificChallenge(challenge) {
    return {
        type: CHALLENGE_SUCCESS,
        fatChallenge: challenge
    };
}

function receiveNewChallengeId(challengeId) {
    return {
        type: CHALLENGE_SUCCESS,
        challengeId: challengeId
    };
}

function receiveNewSectionId(sectionId) {
    return {
        type: CHALLENGE_SUCCESS,
        section: sectionId,
        stepObject: null
    };
}

function receiveNewInstructionId(sectionId, instructionId, order) {
    return {
        type: CHALLENGE_SUCCESS,
        section: sectionId,
        stepObject: {id: instructionId, order: order, type: 'instruction'}
    };
}

function receiveNewHintId(sectionId, hintId, order) {
    return {
        type: CHALLENGE_SUCCESS,
        section: sectionId,
        stepObject: {id: hintId, order: order, type: 'hint'}
    };
}

function receiveShowSection(id, show) {
    return {
        type: CHALLENGE_SUCCESS,
        showSection: id,
        show: show
    };
}

function receiveNewAbstractId(abstractId) {
    return {
        type: CHALLENGE_SUCCESS,
        abstractId: abstractId
    };
}

function receiveNewSolutionId(solutionId) {
    return {
        type: CHALLENGE_SUCCESS,
        solutionId: solutionId
    };
}

function receiveAbstractIdByChallengeId(abstractId) {
    return {
        type: CHALLENGE_SUCCESS,
        abstractId: abstractId
    };
}

function receiveSolutionIdByChallengeId(solutionId) {
    return {
        type: CHALLENGE_SUCCESS,
        solutionId: solutionId
    };
}

function receiveChallengeLevels(levelList) {
    return {
        type: CHALLENGE_SUCCESS,
        challengeLevels: levelList
    };
}

function receiveChallengeUsages(usageList) {
    return {
        type: CHALLENGE_SUCCESS,
        challengeUsages: usageList
    };
}

function receiveChallengeTypes(typeList) {
    return {
        type: CHALLENGE_SUCCESS,
        challengeTypes: typeList
    };
}

function receiveTitleImageFile(file, mediaId) {
    return {
        type: CHALLENGE_SUCCESS,
        titleImageFile: file,
        titleImageId: mediaId
    };
}

function receiveUploadedFiles(files, mediaId) {
    return {
        type: CHALLENGE_SUCCESS,
        uploadedFiles: files,
        mediaIds: mediaId
    };
}

function receiveChallengeCategory(challengeCategory) {
    return {
        type: CHALLENGE_SUCCESS,
        challengeCategory: challengeCategory
    };
}

function receiveChallengeCategories(categoryList) {
    return {
        type: CHALLENGE_SUCCESS,
        challengeCategories: categoryList
    };
}

function receiveChallengeKeywords(keywords) {
    return {
        type: CHALLENGE_SUCCESS,
        challengeKeywords: keywords
    };
}

function receiveSelectedChallengeUsages(usageList) {
    return {
        type: CHALLENGE_SUCCESS,
        selectedChallengeUsages: usageList
    };
}

function receiveSelectedChallengeCategories(categoryList) {
    return {
        type: CHALLENGE_SUCCESS,
        selectedChallengeCategories: categoryList
    };
}

function receiveSelectedKeywords(keywords) {
    return {
        type: CHALLENGE_SUCCESS,
        selectedKeywords: keywords
    };
}

function receiveRemoveImage() {
    return {
        type: CHALLENGE_SUCCESS,
        titleImageId: ''
    };
}

function receiveTargetTranslation(targetTranslation) {
    return {
        type: TRANSLATION_SUCCESS,
        languageIsoCode: targetTranslation
    };
}

function receiveTranslatedChallenge(challenge) {
    return {
        type: TRANSLATION_SUCCESS,
        translatedChallenge: challenge
    };
}

export function receiveIsPrivate(isPrivate) {
    return {
        type: CHALLENGE_SUCCESS,
        isPrivate: isPrivate
    };
}

export function receiveNewChallengeData(jsonObject) {
    return {
        type: CHALLENGE_SUCCESS,
        response: jsonObject
    };
}

export function setShowToSectionReferences(id, show) {
    return dispatch => {
        dispatch(receiveShowSection(id, show));
    };
}

export function postNewChallenge(jsonData) {
    const config = {
        method: 'POST',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'},
        body: jsonData
    };
    return dispatch => {
        return fetch(POST_NEW_CHALLENGE_URL, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response.ok) {
                        if(json !== null) {
                            dispatch(receiveNewChallengeData(json.jsonObject));
                        }
                    } else {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while POST create new challenge request!', json.jsonObject.message, true));
                    }
                }
            })
            .catch( err => {
                if (!(err instanceof Error) &&  err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge data by language request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge data by language request!', err));
            });
    };
}


export function getChallengeDataByLanguage(challengeId, isoLangCode) {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: { 'Challenge-Language': isoLangCode, 'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(GET_CHALLENGE_BASE_URL + challengeId, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && json.response.ok) {
                        if(json.jsonObject !== null) {
                            dispatch(receiveSpecificChallenge(json.jsonObject));
                        }
                    } else {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET challenge data by language request!', json.jsonObject.message, true));
                    }
                }
            })
            .catch( err => {
                if (!(err instanceof Error) &&  err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge data by language request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge data by language request!', err));
            });
    };
}

export function getNewChallengeId() {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(GET_NEW_CHALLENGE_ID_BASE_URL, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET new challenge id request!', json.jsonObject.message, true));
                    } else {
                        dispatch(receiveNewChallengeId(json.jsonObject.challengeId));
                    }
                }
            })
            .catch( err => {
                if (!(err instanceof Error) &&  err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new challenge id request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new challenge id request!', err));
            });
    };
}

export function getNewSectionId(challengeId) {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(GET_NEW_SECTION_ID_BASE_URL + '?challengeId=' + challengeId, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET new section id request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveNewSectionId(json.jsonObject.sectionId));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new section id request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new section id request!', err));
            });
    };
}

export function getNewInstructionId(challengeId, sectionId) {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(GET_NEW_INSTRUCTION_ID_BASE_URL + '?challengeId=' + challengeId + '&sectionId=' + sectionId, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET new instruction id request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveNewInstructionId(json.jsonObject.sectionId, json.jsonObject.instructionId, json.jsonObject.order));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new instruction id request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new instruction id request!', err));
            });
    };
}

export function getNewAbstractId(challengeId) {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(GET_NEW_ABSTRACT_ID_BASE_URL + '?challengeId=' + challengeId, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET new abstract id request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveNewAbstractId(json.jsonObject.abstractId));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new abstract id request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new abstract id request!', err));
            });
    };
}

export function getAbstractIdByChallengeId(challengeId) {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(GET_ABSTRACT_ID_BASE_URL + '?challengeId=' + challengeId, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET new abstract id by challenge request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveAbstractIdByChallengeId(json.jsonObject.abstractId));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new abstract id by challenge request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new abstract id by challenge request!', err));
            });
    };
}

export function getNewSolutionId(challengeId) {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(GET_NEW_SOLUTION_ID_BASE_URL + '?challengeId=' + challengeId, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET new solution id request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveNewSolutionId(json.jsonObject.solutionId));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new solution id request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new solution id request!', err));
            });
    };
}

export function getSolutionIdByChallengeId(challengeId) {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(GET_SOLUTION_ID_BASE_URL + '?challengeId=' + challengeId, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET new solution id by challenge request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveSolutionIdByChallengeId(json.jsonObject.solutionId));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new solution id by challenge request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new solution id by challenge request!', err));
            });
    };
}

export function getNewHintId(challengeId, sectionId) {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(GET_NEW_HINT_ID_BASE_URL + '?challengeId=' + challengeId + '&sectionId=' + sectionId, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET new hint id request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveNewHintId(json.jsonObject.sectionId, json.jsonObject.hintId, json.jsonObject.order));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new hint id request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET new hint id request!', err));
            });
    };
}

export function getChallengeLevel() {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(CHALLENGE_LEVEL_BASE_URL, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET challenge levels request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveChallengeLevels(json.jsonObject));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge levels request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge levels request!', err));
            });
    };
}

export function getChallengeUsage() {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(CHALLENGE_USAGE_BASE_URL, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET challenge usages request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveChallengeUsages(json.jsonObject));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge usages request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge usages request!', err));
            });
    };
}

export function getChallengeType() {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(CHALLENGE_TYPE_BASE_URL, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET challenge types request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveChallengeTypes(json.jsonObject));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge types request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge types request!', err));
            });
    };
}

export function getChallengeCategory() {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(CHALLENGE_CATEGORY_BASE_URL, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET challenge categories request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveChallengeCategories(json.jsonObject));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge categories request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge categories request!', err));
            });
    };
}

export function getChallengeKeywords() {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(CHALLENGE_KEYWORD_BASE_URL, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET challenge keywords request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveChallengeKeywords(json.jsonObject));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge keywords request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge keywords request!', err));
            });
    };
}

export function setTitleImageFile(file, mediaId) {
    return dispatch => {
        dispatch(receiveTitleImageFile(file, mediaId));
    };
}

export function removeTitleImageFile(file, mediaId) {
    const config = {
        method: 'DELETE',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(IMAGE_REMOVE_URL + '?mediaId=' + mediaId, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while DELETE title image request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveRemoveImage());
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while DELETE title image request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while DELETE title image request!', err));
            });
    };
}

export function removeStep(stepId) {
    const config = {
        method: 'DELETE',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(REMOVE_STEP_URL + '?stepId=' + stepId, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && json.response.ok) {
                        dispatch(receiveMessageFromServer('', 'Successfull DELETE step ' + stepId + ' request!', false));
                    } else {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while DELETE step request!', 'Step ' + stepId + ' could not be removed', true));
                    }
                    dispatch(receiveRemoveImage());
                }
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while DELETE step request request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while DELETE step request!', err));
            });
    };
}

export function setUploadedFiles(files, mediaId) {
    return dispatch => {
        dispatch(receiveUploadedFiles(files, mediaId));
    };
}

// Sets category from dropdown
export function setCategoryFromDropDown(category) {
    return dispatch => {
        dispatch(receiveChallengeCategory(category));
    };
}

export function setSelectedChallengeCategories(categoryList) {
    return dispatch => {
        dispatch(receiveSelectedChallengeCategories(categoryList));
    };
}

export function setSelectedMetadata(metadataName, metadataList) {
    return dispatch =>  {
        if(metadataName === 'usages') {
            dispatch(receiveSelectedChallengeUsages(metadataList));
        }
    };
}

export function setSelectedKeywords(keywords) {
    return dispatch => {
        dispatch(receiveSelectedKeywords(keywords));
    };
}

export function getServerCookie() {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'}
    };


    return dispatch => { // eslint-disable-line no-unused-vars
        return fetch(SERVER_URL, config)
            .then().catch(err => dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET server cookie request!', err.message)));
    };
}

export function getConsumer() {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json' },
    };
    return dispatch => {
        return fetch(CONSUMER_BASE_URL, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET consumers request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    } dispatch(receiveConsumers(json.jsonObject));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET consumers request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET consumers request!', err));
            });
    };
}

// Sets consumer from dropdown
export function setConsumer(consumerId, consumerURL) {
    return dispatch => {
        dispatch(receiveConsumer(consumerId, consumerURL));
    };
}

export function setToken(value) {
    return dispatch => {
        dispatch(receiveToken(value));
    };
}

export function setActualEditorStep(actualStep, lastStep) {
    return dispatch => {
        dispatch(receiveEditorNavigationRequest(actualStep, lastStep));
    };
}

export function getTranslationDataByTargetLanguage(challengeId, targetLanguage) {
    const config = {
        method: 'GET',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Challenge-Language': targetLanguage, 'Content-Type': 'application/json'},
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(GET_TRANSLATED_CHALLENGE_BASE_URL + challengeId, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while GET challenge translation data request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveTranslatedChallenge(json.jsonObject));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge translation data request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while GET challenge translation data request!', err));
            });
    };
}

export function setTargetTranslation(targetTranslation) {
    return dispatch => {
        dispatch(receiveTargetTranslation(targetTranslation));
    };
}

export function uploadImage(challengeId, languageIsoCode, markdownId, markdownType, filePath, image) {
    const config = {
        method: 'POST',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({'base64Image': image, 'filePath': filePath})
    };

    let queryString = '?challengeId=' + challengeId;
    if(languageIsoCode !== undefined && languageIsoCode !== null) {
        queryString = queryString + '&languageCode=' + languageIsoCode;
    }
    if(markdownId !== undefined && markdownId !== null) {
        queryString = queryString + '&markdownId=' + markdownId;
    }
    if(markdownType !== undefined && markdownType !== null) {
        queryString = queryString + '&markdownType=' + markdownType;
    }
    // eslint-disable-next-line
    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        return fetch(IMAGE_UPLOAD_URL + queryString, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while POST image for ' + markdownType + ' request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveMessageFromServer('', 'Successfull POST image for ' + markdownType + ' request!', false));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while POST image for ' + markdownType + ' request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while POST image for ' + markdownType + ' request!', err));
            });
    };
}

export function translateText(text, type, id, toLanguageIsoCode) {
    const extractedObject = extractNonTranslatableParts(text);
    const config = {
        method: 'POST',
        credentials: 'include',
        mode: 'cors',
        redirect: 'follow',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'auth_key=bc9179b1-59b1-76d8-820d-a42cd2522a08&text=' + encodeURIComponent(extractedObject.text) + '&target_lang=' + toLanguageIsoCode
    };

    return dispatch => {
        // We dispatch requestLogin to kickoff the call to the API
        // return fetch(DEEPL_URL + '?auth_key=bc9179b1-59b1-76d8-820d-a42cd2522a08&text=' + encodeURIComponent(text) + '&target_lang=' + toLanguageIsoCode, config)
        return fetch(DEEPL_URL, config)
            .then( response =>
                response.text().then(responseBodyAsText => ({ responseBodyAsText, response }))
            )
            .then( ({responseBodyAsText, response}) => {
                if(response.status === 401) {
                    dispatch(receiveMessageFromServer('Unauthorized Request!', 'You have no permission to access this functionality. Please contact your Keycloak admin', true));
                    return null;
                }
                try {
                    const bodyAsJson = JSON.parse(responseBodyAsText);
                    return {jsonObject: bodyAsJson, response: response};
                } catch (e) {
                    // eslint-disable-next-line no-undef
                    return Promise.reject({message: 'Returned JSON is not parsable', type: 'unparsable'});
                }
            })
            .then( (json) => {
                if(json !== null) {
                    if(json.response !== undefined && !json.response.ok) {
                        dispatch(receiveMessageFromServer('We`re sorry an error occurred while POST text to translate request!', json.jsonObject.message, true));
                        // eslint-disable-next-line no-undef
                        return Promise.reject(json.jsonObject);
                    }
                    dispatch(receiveTranslation(fillTranslatedText(json.jsonObject.translations[0].text, extractedObject.array), type, id));
                    return null;
                }
                return null;
            })
            .catch( err => {
                if (!(err instanceof Error) && err.type && err.type === 'unparsable') {
                    dispatch(receiveErrorFromCatch('We`re sorry an error occurred while POST text to translate request!', err.message));
                    return;
                }
                dispatch(receiveErrorFromCatch('We`re sorry an error occurred while POST text to translate request!', err));
            });
    };
}
