import React, {Component, PropTypes} from 'react';
import { Button, Card} from 'semantic-ui-react';

import ChallengeMetadataCard from './ChallengeMetadataCard';
import {setActualEditorStep, setSelectedMetadata} from '../actions/actions';

export default class ChallengeMetadataDisplay extends Component {
    state : {
        challengeMetadataList: Array} = {challengeMetadataList: []};

    componentDidMount() {
        this.updateChallengeMetadata();
    }

    updateChallengeMetadata = () => {
        const {dispatch, getChallengeMetadata} = this.props;
        dispatch(getChallengeMetadata());
    };

    getMetadataIndex = (metadataValue) => {
        const {challengeMetadataList} = this.props;
        let index = 0;
        let returnValue = 0;
        challengeMetadataList.forEach(metadata => {
            if(metadata.text === metadataValue) {
                returnValue = index;
            }
            index++;
        });
        return returnValue;
    };

    setMetadata = (metadataValue) => {
        const metadata = this.state.challengeMetadataList;
        const index = metadata.indexOf(metadataValue);
        if(index > -1) {
            metadata[index] = null;
        } else {
            metadata[this.getMetadataIndex(metadataValue)] = metadataValue;
        }
        return metadata;
    };

    handleAddClick(metadataValue) {
        const {metadataName, dispatch} = this.props;
        if(metadataValue !== undefined) {
            this.setState({challengeMetadataList: this.setMetadata(metadataValue)});
        }
        dispatch(setSelectedMetadata(metadataName, this.state.challengeMetadataList));
    }

    handleRemoveClick(metadataValue) {
        const {metadataName, dispatch} = this.props;
        this.setState({challengeMetadataList: this.setMetadata(metadataValue)});
        dispatch(setSelectedMetadata(metadataName, this.state.challengeMetadataList));
    }

    handleFinishClick = (event: Event) => {
        const {dispatch, metadataName} = this.props;
        if(event !== undefined) {
            dispatch(setActualEditorStep('challengeCategoryNavItem', false));
            dispatch(setSelectedMetadata(metadataName, this.state.challengeMetadataList));
        }
    };

    proveIsSelected = (index, metadata, selectedIndex, selectedValue) => {
        if(selectedIndex === -1) {
            if(selectedValue[index] === undefined  || selectedValue[index] === -1 || selectedValue[index] === '-1') {
                return 'none';
            }
            return selectedValue[index] === metadata.text ? 'true' : 'false';
        }
        return metadata.id === selectedIndex ? 'true' : 'false';
    };

    createMetadataCards = () => {
        const {challengeMetadataList, multiselectable, selectedValue, challengeMetadataName, nextEditorStep, dispatch, challengeId} = this.props;
        const entries = [];
        let index = 0;
        let selectedIndex = -1;

        if (challengeMetadataList !== undefined && challengeMetadataList.length > 0) {
            challengeMetadataList.map((metadata) => {
                if(Number.isInteger(selectedValue[index])) {
                    selectedIndex = selectedValue[index];
                } else if(Number.isInteger(parseInt(selectedValue[index], 10))) {
                    selectedIndex = parseInt(selectedValue[index], 10);
                }
                const selected = this.proveIsSelected(index, metadata, selectedIndex, selectedValue);
                entries.push(<ChallengeMetadataCard multiSelectAddClick={multiselectable ? this.handleAddClick.bind(this) : undefined} multiSelectRemoveClick={multiselectable ? this.handleRemoveClick.bind(this) : undefined} key={metadata.text} selected={selected} dispatch={dispatch} challengeId={challengeId} challengeMetadataValue={metadata.text} challengeMetaDataName={challengeMetadataName} nextEditorStep={nextEditorStep}/>);
                index++;
            });
        }
        return entries;
    };

    render = () => {
        const {multiselectable, selectedValue, actualEditorStep, itemsPerRow} = this.props;
        if(multiselectable && selectedValue !== undefined && selectedValue.length > 0) {
            this.state.challengeMetadataList = selectedValue;
        }
        return (<div>
            <Card.Group itemsPerRow={itemsPerRow}>
                {this.createMetadataCards()}
            </Card.Group>
            {multiselectable &&
                <Button style={{marginTop: 10}} primary fluid disabled={this.state.challengeMetadataList.length === 0} id={actualEditorStep + 'Button'} onClick={(event) => this.handleFinishClick(event)}>Submit</Button>
            }
        </div>);
    }
}

ChallengeMetadataDisplay.propTypes = {
    dispatch: PropTypes.func.isRequired,
    multiselectable: PropTypes.bool,
    metadataName: PropTypes.string.isRequired,
    actualEditorStep: PropTypes.string,
    challengeId: PropTypes.string.isRequired,
    challengeMetadataList: PropTypes.array.isRequired,
    challengeMetadataName: PropTypes.string.isRequired,
    nextEditorStep: PropTypes.string.isRequired,
    itemsPerRow: PropTypes.number.isRequired,
    selectedValue: PropTypes.array,
    getChallengeMetadata: PropTypes.func.isRequired
};
