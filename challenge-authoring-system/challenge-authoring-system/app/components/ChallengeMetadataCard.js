import React, {Component, PropTypes} from 'react';
import { Dimmer, Image, Button, Header, Icon} from 'semantic-ui-react';
import {setActualEditorStep, postNewChallenge} from '../actions/actions';
import ImageHelper from '../helper/ImageHelper';

export default class ChallengeMetadataCard extends Component {

    state = {active: false};

    handleAddClick = (event, challengeMetadataValue) => {
        const {dispatch, challengeId, multiSelectAddClick, challengeMetaDataName, nextEditorStep } = this.props;
        const jsonData = {};
        if(multiSelectAddClick !== undefined) {
            multiSelectAddClick(challengeMetadataValue);
        }
        jsonData.challengeId = challengeId;
        jsonData[challengeMetaDataName] = challengeMetadataValue;
        dispatch(postNewChallenge(JSON.stringify(jsonData)));
        if(multiSelectAddClick === undefined) {
            dispatch(setActualEditorStep(nextEditorStep, false));
        }
    };

    handleRemoveClick = (event, challengeMetadataValue) => {
        const {dispatch, multiSelectRemoveClick, challengeId, challengeMetaDataName } = this.props;
        const jsonData = {};
        if(multiSelectRemoveClick !== undefined) {
            multiSelectRemoveClick(challengeMetadataValue);
        }
        jsonData.challengeId = challengeId;
        jsonData[challengeMetaDataName] = challengeMetadataValue;
        dispatch(postNewChallenge(JSON.stringify(jsonData)));
    };

    handleShow = () => this.setState({ active: true });
    handleHide = () => this.setState({ active: false });

    getDimmerContent = (value, selected) => {
        if(selected === 'true') {
            return (
                <div>
                    <Header as="h2" inverted>{value}</Header>
                    <Button  icon data-tip data-for={value} onClick={(event) => this.handleRemoveClick(event, value)}>
                        <Icon name={'remove'} />
                    </Button>
                </div>
            );
        }
        return (
            <div>
                <Header as="h2" inverted>{value}</Header>
                <Button primary onClick={(event) => this.handleAddClick(event, value)}>Add</Button>
            </div>
        );
    };

    getDimmerObject = (active, content, cardSize, value, selected, multiSelectable) => {
        if(selected === 'true') {
            return (
                <Dimmer.Dimmable
                    as={Image}
                    dimmed={active}
                    dimmer={{active, content}}
                    onMouseEnter={this.handleShow}
                    onMouseLeave={this.handleHide}
                    size={cardSize}
                    src={ImageHelper(value)}
                />
            );
        }
        if(selected === 'false' && multiSelectable) {
            return (
                <Dimmer.Dimmable
                    as={Image}
                    disabled
                    dimmed={active}
                    dimmer={{active, content}}
                    onMouseEnter={this.handleShow}
                    onMouseLeave={this.handleHide}
                    size={cardSize}
                    src={ImageHelper(value)}
                />
            );
        }
        if(selected === 'false' && !multiSelectable) {
            return (
                <Dimmer.Dimmable
                    as={Image}
                    disabled
                    dimmed={active}
                    dimmer={{active, content}}
                    size={cardSize}
                    src={ImageHelper(value)}
                />
            );
        }
        if(!multiSelectable) {
            return (<Dimmer.Dimmable
                as={Image}
                dimmed={active}
                dimmer={{active, content}}
                onMouseEnter={this.handleShow}
                onMouseLeave={this.handleHide}
                size={cardSize}
                src={ImageHelper(value)}
            />);
        }
        return (<Dimmer.Dimmable
            as={Image}
            disabled
            dimmed={active}
            dimmer={{active, content}}
            onMouseEnter={this.handleShow}
            onMouseLeave={this.handleHide}
            size={cardSize}
            src={ImageHelper(value)}
        />);
    };

    render = () => {
        const {challengeMetadataValue, cardSize, selected, multiSelectAddClick, multiSelectRemoveClick} = this.props;
        const { active } = this.state;
        const content = this.getDimmerContent(challengeMetadataValue, selected);
        const multiSelectable = multiSelectAddClick !== undefined && multiSelectRemoveClick !== undefined;
        return this.getDimmerObject(active, content, cardSize, challengeMetadataValue, selected, multiSelectable);
    }
}

ChallengeMetadataCard.propTypes = {
    dispatch: PropTypes.func.isRequired,
    challengeId: PropTypes.string.isRequired,
    multiSelectAddClick: PropTypes.func,
    multiSelectRemoveClick: PropTypes.func,
    challengeMetadataValue: PropTypes.string.isRequired,
    challengeMetaDataName: PropTypes.string.isRequired,
    nextEditorStep: PropTypes.string.isRequired,
    cardSize: PropTypes.string,
    selected: PropTypes.string
};

ChallengeMetadataCard.defaultProps = {
    cardSize: 'small'
};
