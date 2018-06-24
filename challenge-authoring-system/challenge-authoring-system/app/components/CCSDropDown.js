import React, { Component, PropTypes } from 'react';
import {getConsumer, setConsumer} from '../actions/actions';
import {Dropdown} from 'semantic-ui-react';

export default class CCSDropDown extends Component {
    componentDidMount() {
        this.updateConsumer();
    }

    updateConsumer = () => {
        const {dispatch} = this.props;
        dispatch(getConsumer());
    };

    createDropDownEntries= (consumerList) => {
        const options = [];

        if (consumerList !== undefined && consumerList.length > 0) {
            consumerList.map((consumer) => {
                options.push({
                    'text': consumer.ConsumerName,
                    'value': consumer._id
                });
            });
        }
        return options;
    };

    getConsumerUrl = (consumerId) => {
        const {consumerList} = this.props;

        return consumerList.find((element) => {
            if(element._id === consumerId) {
                return element;
            }
            return null;
        }).ConsumerURL;
    };

    selectConsumer = (event: Event, data: any) => {
        const {dispatch} = this.props;
        const consumerURL = this.getConsumerUrl(data.value);
        dispatch(setConsumer(data.value, consumerURL));
    };

    render() {
        const {consumerList} = this.props;
        return (
            <Dropdown
                placeholder="Select Customer"
                selection
                search
                onChange={this.selectConsumer}
                options={this.createDropDownEntries(consumerList)}
            />

        );
    }
}


CCSDropDown.propTypes = {
    dispatch: PropTypes.func.isRequired,
    consumerList: PropTypes.array,
};
