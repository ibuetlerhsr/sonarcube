import { combineReducers } from 'redux';
import {fillSelectedCategories, fillSectionReferencesWithShow, fillSelectedChallengeUsages, fillSelectedKeywords, fillSectionReferences, fillStepTranslation, fillSectionTranslation, getSpecificValueFromFatChallenge, parseJsonData} from '../helper/ExtractUtil';
import {
    LOGIN_REQUEST, LOGIN_SUCCESS, LOGIN_FAILURE, LOGOUT_SUCCESS,
    CHALLENGES_REQUEST, CHALLENGES_SUCCESS, CHALLENGES_FAILURE,
    CHALLENGE_SUCCESS, CHALLENGE_FAILURE, CHALLENGE_REQUEST,
    CONSUMER_REQUEST, CONSUMER_SUCCESS, CONSUMER_FAILURE,
    NAVIGATION_REQUEST, NAVIGATION_SUCCESS, NAVIGATION_FAILURE,
    STEP_REQUEST, STEP_SUCCESS, STEP_FAILURE,
    TRANSLATION_REQUEST, TRANSLATION_SUCCESS, TRANSLATION_FAILURE,
    ERROR_OCCURRED
} from '../actions/actions';

// The auth reducer. The starting state sets authentication
// based on a token being in local storage. In a real app,
// we would also want a util to check if the token is expired.

function auth(state = {
    isFetching: false,
    isAuthenticated: false
}, action) {
    switch (action.type) {
        case LOGIN_REQUEST:
            return Object.assign({}, state, {
                isFetching: true,
                isAuthenticated: false,
                user: action.credentials
            });
        case LOGIN_SUCCESS:
            return Object.assign({}, state, {
                isFetching: false,
                isAuthenticated: action.isAuthenticated,
                errorMessage: '',
            });
        case LOGIN_FAILURE:
            return Object.assign({}, state, {
                isFetching: false,
                isAuthenticated: false,
                errorMessage: action.message
            });
        case LOGOUT_SUCCESS:
            return Object.assign({}, state, {
                isFetching: true,
                isAuthenticated: false
            });
        default:
            return state;
    }
}

function messageHandler(state = {
    hasError: false,
    errorContext: '',
    message: ''
}, action) {
    switch (action.type) {
        case ERROR_OCCURRED:
            return Object.assign({}, state, {
                message: action.message === undefined ? state.message : action.message,
                errorContext: action.errorContext === undefined ? state.errorContext : action.errorContext,
                hasError: action.hasError === undefined ? state.hasError : action.hasError,
            });
        default:
            return state;
    }
}

// The challenges reducer
function challenges(state = {
    isFetching: false,
    challengeList: {},
    authenticated: false
}, action) {
    switch (action.type) {
        case CHALLENGES_REQUEST:
            return Object.assign({}, state, {
                isFetching: true
            });
        case CHALLENGES_SUCCESS:
            return Object.assign({}, state, {
                isFetching: false,
                challengeList: JSON.parse(action.response),
                authenticated: action.authenticated || false
            });
        case CHALLENGES_FAILURE:
            return Object.assign({}, state, {
                isFetching: false
            });
        default:
            return state;
    }
}

function challenge(state = {
    isFetching: false,
    fatChallenge: {},
    challengeLevels: [],
    challengeTypes: [],
    challengeUsages: [],
    selectedChallengeUsages: [],
    challengeId: '',
    challengeTitle: '',
    languageIsoCode: '',
    titleImageFile: undefined,
    uploadedFiles: [],
    mediaIds: [],
    titleImageId: '',
    challengeName: '',
    challengeType: '',
    challengeLevel: '',
    challengeUsage: '',
    goldnuggetType: '',
    staticGoldnuggetSecret: '',
    challengeCategories: [],
    selectedChallengeCategories: [],
    challengeKeywords: [],
    selectedKeywords: [],
    challengeCategory: -1,
    isPrivate: undefined,
    authenticated: false,
    sectionReferences: [],
    abstractID: '',
    abstractMD: '',
    solutionID: '',
    solutionMD: ''
}, action) {
    switch (action.type) {
        case CHALLENGE_REQUEST:
            return Object.assign({}, state, {
                isFetching: true
            });
        case CHALLENGE_SUCCESS:
            return Object.assign({}, state, {
                isFetching: false,
                fatChallenge: action.fatChallenge !== undefined ? action.fatChallenge : state.fatChallenge,
                challengeId: action.challengeId !== undefined ? action.challengeId : getSpecificValueFromFatChallenge('challengeId', action.fatChallenge) === null ? state.challengeId : getSpecificValueFromFatChallenge('challengeId', action.fatChallenge),
                languageIsoCode: action.languageIsoCode !== undefined ? action.languageIsoCode : state.languageIsoCode,
                challengeLevels: action.challengeLevels !== undefined ? action.challengeLevels : state.challengeLevels,
                challengeTypes: action.challengeTypes !== undefined ? action.challengeTypes : state.challengeTypes,
                challengeUsages: action.challengeUsages !== undefined ? action.challengeUsages : state.challengeUsages,
                challengeCategories: action.challengeCategories !== undefined ? action.challengeCategories : state.challengeCategories,
                selectedChallengeCategories: fillSelectedCategories(action.selectedChallengeCategories, state.selectedChallengeCategories, action.challengeCategories, state.challengeCategories, action.fatChallenge),
                challengeKeywords: action.challengeKeywords !== undefined ? action.challengeKeywords : state.challengeKeywords,
                selectedKeywords: fillSelectedKeywords(action.selectedKeywords, state.selectedKeywords, action.challengeKeywords, state.challengeKeywords, action.fatChallenge),
                selectedChallengeUsages: fillSelectedChallengeUsages(action.selectedChallengeUsages, state.selectedChallengeUsages, action.challengeUsages, state.challengeUsages, action.fatChallenge),
                titleImageFile: action.titleImageFile !== undefined ? action.titleImageFile : getSpecificValueFromFatChallenge('titlePNG', action.fatChallenge) === null ? state.titleImageFile : getSpecificValueFromFatChallenge('titlePNG', action.fatChallenge),
                titleImageId: action.titleImageId !== undefined ? action.titleImageId : state.titleImageId,
                uploadedFiles: action.uploadedFiles !== undefined ? action.uploadedFiles : state.uploadedFiles,
                mediaIds: action.mediaIds !== undefined ? state.mediaIds.add(action.mediaIds) : state.mediaIds,
                sectionReferences: action.section !== undefined  && action.stepObject !== undefined ? fillSectionReferences(state.sectionReferences, action.section, action.stepObject) : action.showSection === undefined ? getSpecificValueFromFatChallenge('sectionReferences', action.fatChallenge) === null ? state.sectionReferences : getSpecificValueFromFatChallenge('sectionReferences', action.fatChallenge) : fillSectionReferencesWithShow(state.sectionReferences, action.showSection, action.show),
                challengeTitle: parseJsonData('title', action.response) !== '' ? parseJsonData('title', action.response) : getSpecificValueFromFatChallenge('title', action.fatChallenge) === null ? state.challengeTitle : getSpecificValueFromFatChallenge('title', action.fatChallenge),
                challengeName: parseJsonData('name', action.response) !== '' ? parseJsonData('name', action.response) : getSpecificValueFromFatChallenge('name', action.fatChallenge) === null ? state.challengeName : getSpecificValueFromFatChallenge('name', action.fatChallenge),
                challengeType: parseJsonData('type', action.response) !== '' ? parseJsonData('type', action.response) : getSpecificValueFromFatChallenge('type', action.fatChallenge) === null ? state.challengeType : getSpecificValueFromFatChallenge('type', action.fatChallenge),
                challengeLevel: parseJsonData('level', action.response)  !== '' ? parseJsonData('level', action.response) : getSpecificValueFromFatChallenge('level', action.fatChallenge) === null ? state.challengeLevel : getSpecificValueFromFatChallenge('level', action.fatChallenge),
                goldnuggetType: parseJsonData('goldnuggetType', action.response)  !== '' ? parseJsonData('goldnuggetType', action.response) : getSpecificValueFromFatChallenge('goldnuggetType', action.fatChallenge) === null ? state.goldnuggetType : getSpecificValueFromFatChallenge('goldnuggetType', action.fatChallenge),
                staticGoldnuggetSecret: parseJsonData('staticGoldnuggetSecret', action.response)  !== '' ? parseJsonData('staticGoldnuggetSecret', action.response) : getSpecificValueFromFatChallenge('staticGoldnuggetSecret', action.fatChallenge) === null ? state.staticGoldnuggetSecret : getSpecificValueFromFatChallenge('staticGoldnuggetSecret', action.fatChallenge),
                challengeCategory: action.challengeCategory !== undefined ? action.challengeCategory : state.challengeCategory,
                abstractID: action.abstractId !== undefined ? action.abstractId : state.abstractID,
                abstractMD: getSpecificValueFromFatChallenge('abstract', action.fatChallenge) === null ? state.abstractMD : getSpecificValueFromFatChallenge('abstract', action.fatChallenge),
                solutionID: action.solutionId !== undefined ? action.solutionId : state.solutionID,
                solutionMD: getSpecificValueFromFatChallenge('solution', action.fatChallenge) === null ? state.solutionMD : getSpecificValueFromFatChallenge('solution', action.fatChallenge),
                isPrivate: parseJsonData('isPrivate', action.response) !== '' ? parseJsonData('isPrivate', action.response) : getSpecificValueFromFatChallenge('isPrivate', action.fatChallenge) === null ? state.isPrivate : getSpecificValueFromFatChallenge('isPrivate', action.fatChallenge),
                authenticated: action.authenticated || false
            });
        case CHALLENGE_FAILURE:
            return Object.assign({}, state, {
                isFetching: false
            });
        default:
            return state;
    }
}

// The consumer reducer
function consumers(state = {
    isFetching: false,
    consumerList: [],
    authenticated: false,
    selectedConsumer: '',
    consumerURL: ''
}, action) {
    switch (action.type) {
        case CONSUMER_REQUEST:
            return Object.assign({}, state, {
                isFetching: true
            });
        case CONSUMER_SUCCESS:
            return Object.assign({}, state, {
                isFetching: false,
                consumerList: action.consumerList !== undefined ? action.consumerList : state.consumerList,
                authenticated: action.authenticated || false,
                selectedConsumer: action.selectedConsumer !== undefined ? action.selectedConsumer : state.selectedConsumer,
                consumerURL: action.consumerURL !== undefined ? action.consumerURL : state.consumerURL
            });
        case CONSUMER_FAILURE:
            return Object.assign({}, state, {
                isFetching: false
            });
        default:
            return state;
    }
}

function navigator(state = {
    isFetching: false,
    actualEditorStep: 'challengeNameNavItem',
    lastEdtitorStep: '',
}, action) {
    switch (action.type) {
        case NAVIGATION_REQUEST:
            return Object.assign({}, state, {
                isFetching: true
            });
        case NAVIGATION_SUCCESS:
            return Object.assign({}, state, {
                isFetching: false,
                actualEditorStep: action.actualEditorStep !== undefined ? action.actualEditorStep : state.actualEditorStep
            });
        case NAVIGATION_FAILURE:
            return Object.assign({}, state, {
                isFetching: false
            });
        default:
            return state;
    }
}

function stepItemCounter(state = {
    isFetching: false,
    stepArray: [],
    sectionCount: 1,
    editorArray: [],
    sectionItems: []
}, action) {
    switch(action.type) {
        case STEP_REQUEST:
            return Object.assign({}, state, {
                isFetching: true
            });
        case STEP_SUCCESS :
            return Object.assign({}, state, {
                isFetching: false,
                stepArray: action.stepArray !== undefined ? action.stepArray : state.stepArray,
                editorArray: action.editorArray !== undefined ? action.editorArray : state.editorArray,
                sectionCount: action.sectionCount !== undefined ? action.sectionCount : state.sectionCount,
                sectionItems: action.sectionItems !== undefined ? action.sectionItems : state.sectionItems
            });
        case STEP_FAILURE:
            return Object.assign({}, state, {
                isFetching: false
            });
        default:
            return state;
    }
}

function translations(state = {
    isFetching: false,
    targetLanguage: '',
    abstractTranslation: '',
    solutionTranslation: '',
    titleTranslation: '',
    nameTranslation: '',
    sectionTranslations: [],
    stepTranslations: [],
    translatedChallenge: {}
}, action) {
    switch(action.type) {
        case TRANSLATION_REQUEST:
            return Object.assign({}, state, {
                isFetching: true
            });
        case TRANSLATION_SUCCESS :
            return Object.assign({}, state, {
                isFetching: false,
                translatedChallenge: action.translatedChallenge !== undefined ? action.translatedChallenge : state.translatedChallenge,
                targetLanguage: action.languageIsoCode !== undefined ? action.languageIsoCode : state.targetLanguage,
                abstractTranslation: action.markdownType !== undefined && action.markdownType === 'abstract' ? action.translation : state.abstractTranslation,
                solutionTranslation: action.markdownType !== undefined && action.markdownType === 'solution' ? action.translation : state.solutionTranslation,
                titleTranslation: action.markdownType !== undefined && action.markdownType === 'title' ? action.translation : state.titleTranslation,
                nameTranslation: action.markdownType !== undefined && action.markdownType === 'name' ? action.translation : state.nameTranslation,
                sectionTranslations: action.markdownType !== undefined && action.markdownId !== undefined && action.markdownType === 'section' ? fillSectionTranslation(state.sectionTranslations, {sectionId: action.markdownId, translation: action.translation}) : state.sectionTranslations,
                stepTranslations: action.markdownType !== undefined && action.markdownId !== undefined && (action.markdownType === 'hint' || action.markdownType === 'instruction') ? fillStepTranslation(state.stepTranslations, {stepId: action.markdownId, type: action.markdownType, translation: action.translation}) : state.stepTranslations
            });
        case TRANSLATION_FAILURE:
            return Object.assign({}, state, {
                isFetching: false
            });
        default:
            return state;
    }
}

// We combine the reducers here so that they
// can be left split apart above
const challengesApp = combineReducers({
    auth,
    challenges,
    consumers,
    challenge,
    navigator,
    stepItemCounter,
    translations,
    messageHandler
});

export default challengesApp;
