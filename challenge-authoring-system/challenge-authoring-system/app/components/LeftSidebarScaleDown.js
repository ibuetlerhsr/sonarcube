import React, {Component, PropTypes} from 'react';
import { Sidebar, Segment, Menu, Icon, Message} from 'semantic-ui-react';
const SERVER_PROTOCOL = process.env.SERVER_PROTOCOL || 'https';
const CONSUMER_ADDRESS = process.env.ADDRESS_CONSUMER || 'ccs_mit.idocker.hacking-lab.com';
import '../style/challengeAuthoringSystemStyle.css';
import Dashboard from './Dashboard';
import TranslationComponent from './TranslationComponent';
import ReactTooltip from 'react-tooltip';
const CONSUMER_URL = SERVER_PROTOCOL + '://' + CONSUMER_ADDRESS;
const KEYLCOAK_PROXY_PATH = process.env.KEYCLOAK_PROXY_PATH || '/auth/realms/Hacking-Lab';
const ADDRESS_KEYCLOAK = process.env.ADDRESS_KEYCLOAK || 'auth.hl.ygubler.ch';
const LOGOUT_REDIRECT_URI_PATH = process.env.LOGOUT_REDIRECT_URI_PATH || '/protocol/openid-connect/logout?redirect_uri';

export default class LeftSidebarScaleDown extends Component {
    state = { visible: false, showMessage: false, message: '' };

    toggleVisibility = () => this.setState({ visible: !this.state.visible });
    toggleShowMessage = () => {
        const {message} = this.props;
        if(message === '' && this.state.showMessage) {
            this.setState({showMessage: !this.state.showMessage});
        }
        this.setState({showMessage: true});
        setTimeout(()=> {
            this.setState({showMessage: false});
        }, 4000);
    };

    handleNavigateBack = (consumerURL) => {
        window.location = consumerURL;
    };

    handleAccountClick = () => {
        window.location = SERVER_PROTOCOL + '://' + ADDRESS_KEYCLOAK + KEYLCOAK_PROXY_PATH + '/account';
    };

    logoutUser = () => {
        const splittedProxyPath = KEYLCOAK_PROXY_PATH.split('/');
        window.location = SERVER_PROTOCOL + '://' + ADDRESS_KEYCLOAK + KEYLCOAK_PROXY_PATH + LOGOUT_REDIRECT_URI_PATH + '=' + SERVER_PROTOCOL + '%3A%2F%2F' + ADDRESS_KEYCLOAK + '%2F' + splittedProxyPath[1] + '%2F' + splittedProxyPath[2] + '%2F' + splittedProxyPath[3] + '%2Faccount%2F';
    };

    render() {
        const { visible } = this.state;
        const { targetLanguage, translatedChallenge, solutionID, abstractID, errorContext, hasError, sectionReferences, abstractTranslation, solutionTranslation, stepTranslations, sectionTranslations, nameTranslation, titleTranslation, dispatch, abstractMD, solutionMD, goldnuggetType, staticGoldnuggetSecret, uploadedFiles, mediaIds, languageIsoCode, fatChallenge, titleImageFile, sectionItems, challengeKeywords, selectedKeywords, titleImageId, challengeTypes, challengeUsages, selectedChallengeUsages, editorArray, selectedChallengeCategories, challengeLevels, isAuthenticated, message, challengeId, challengeName, challengeType, challengeTitle, challengeCategories, challengeCategory, challengeLevel, isPrivate, actualEditorStep, stepArray, sectionCount} = this.props;
        if(this.state.message !== message) {
            this.toggleShowMessage();
            this.state.message = message;
        }
        return (
            <div>
                <span onClick={this.toggleVisibility} data-tip data-for={'showMenu'} className="menuHandle">☰</span>
                {this.state.showMessage && hasError &&
                <Message negative>
                    <Message.Header>{errorContext}</Message.Header>
                    <p>{message}</p>
                </Message>
                }
                {this.state.showMessage && !hasError &&
                <Message positive>
                    <Message.Header>{message}</Message.Header>
                </Message>
                }
                <ReactTooltip id={'showMenu'} place="top" type="dark" effect="float">Show Menu</ReactTooltip>
                <Sidebar.Pushable as={Segment} style={{height: '93vh'}}>
                    <Sidebar as={Menu} animation="overlay" width="thin" visible={visible} icon="labeled" vertical inverted>
                        <Menu.Item name="home" onClick={() => this.handleNavigateBack(CONSUMER_URL)}>
                            <Icon name="home" />
                            Home
                        </Menu.Item>
                        <Menu.Item name="account" onClick={() => this.handleAccountClick()}>
                            <Icon name="address card" />
                            Account
                        </Menu.Item>
                        <Menu.Item name="signOut" onClick={() => this.logoutUser()}>
                            <Icon name="sign out" />
                            Sign out
                        </Menu.Item>
                    </Sidebar>
                    <Sidebar.Pusher>
                        <Segment basic>
                            {targetLanguage !== '' &&
                            <TranslationComponent editorArray={editorArray} translatedChallenge={translatedChallenge} fatChallenge={fatChallenge} challengeId={challengeId} targetLanguage={targetLanguage} dispatch={dispatch}
                                                  isAuthenticated={isAuthenticated} errorMessage={message} abstractTranslation={abstractTranslation} solutionTranslation={solutionTranslation}
                                                  titleTranslation={titleTranslation} nameTranslation={nameTranslation} sectionTranslations={sectionTranslations} stepTranslations={stepTranslations}/>
                            }
                            {targetLanguage === '' &&
                            <Dashboard dispatch={dispatch}
                                       isAuthenticated={isAuthenticated}
                                       errorMessage={message}
                                       challengeLevels={challengeLevels}
                                       challengeUsages={challengeUsages}
                                       titleImageFile={titleImageFile}
                                       titleImageId={titleImageId}
                                       uploadedFiles={uploadedFiles}
                                       mediaIds={mediaIds}
                                       challengeTypes={challengeTypes}
                                       languageIsoCode={languageIsoCode}
                                       staticGoldnuggetSecret={staticGoldnuggetSecret}
                                       goldnuggetType={goldnuggetType}
                                       challengeId={challengeId}
                                       challengeName={challengeName}
                                       challengeTitle={challengeTitle}
                                       challengeType={challengeType}
                                       challengeCategories={challengeCategories}
                                       challengeCategory={challengeCategory}
                                       challengeLevel={challengeLevel}
                                       selectedChallengeUsages={selectedChallengeUsages}
                                       isPrivate={isPrivate}
                                       selectedChallengeCategories={selectedChallengeCategories}
                                       challengeKeywords={challengeKeywords}
                                       selectedKeywords={selectedKeywords}
                                       actualEditorStep={actualEditorStep}
                                       stepArray={stepArray}
                                       sectionCount={sectionCount}
                                       editorArray={editorArray}
                                       fatChallenge={fatChallenge}
                                       abstractID={abstractID}
                                       solutionID={solutionID}
                                       abstractMD={abstractMD}
                                       solutionMD={solutionMD}
                                       sectionItems={sectionItems}
                                       sectionReferences={sectionReferences}/>
                            }
                        </Segment>
                    </Sidebar.Pusher>
                </Sidebar.Pushable>
            </div>
        );
    }
}

LeftSidebarScaleDown.propTypes = {
    dispatch: PropTypes.func.isRequired,
    isAuthenticated: PropTypes.bool.isRequired,
    hasError: PropTypes.bool.isRequired,
    message: PropTypes.string,
    errorContext: PropTypes.string,
    fatChallenge: PropTypes.object,
    challengeLevels: PropTypes.array,
    challengeUsages: PropTypes.array,
    uploadedFiles: PropTypes.array,
    mediaIds: PropTypes.array,
    titleImageFile: PropTypes.object,
    titleImageId: PropTypes.string,
    challengeTypes: PropTypes.array,
    challengeList: PropTypes.array,
    languageIsoCode: PropTypes.string,
    challengeId: PropTypes.string,
    challengeName: PropTypes.string,
    goldnuggetType: PropTypes.string,
    staticGoldnuggetSecret: PropTypes.string,
    challengeTitle: PropTypes.string,
    challengeType: PropTypes.string,
    challengeLevel: PropTypes.string,
    selectedChallengeUsages: PropTypes.array,
    challengeCategories: PropTypes.array,
    challengeCategory: PropTypes.string,
    challengeKeywords: PropTypes.array,
    selectedKeywords: PropTypes.array,
    isPrivate: PropTypes.string,
    actualEditorStep: PropTypes.string,
    lastEditorStep: PropTypes.string,
    selectedChallengeCategories: PropTypes.array,
    stepArray: PropTypes.array,
    sectionCount: PropTypes.number,
    editorArray: PropTypes.array,
    sectionItems: PropTypes.array,
    sectionId: PropTypes.string,
    instructionId: PropTypes.string,
    hintId: PropTypes.string,
    abstractID: PropTypes.string,
    solutionID: PropTypes.string,
    abstractMD: PropTypes.string,
    solutionMD: PropTypes.string,
    translatedChallenge: PropTypes.object,
    targetLanguage: PropTypes.string.isRequired,
    abstractTranslation: PropTypes.string.isRequired,
    solutionTranslation: PropTypes.string.isRequired,
    titleTranslation: PropTypes.string.isRequired,
    nameTranslation: PropTypes.string.isRequired,
    sectionTranslations: PropTypes.array.isRequired,
    stepTranslations: PropTypes.array.isRequired,
    sectionReferences: PropTypes.array
};
