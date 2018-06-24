import React, { Component, PropTypes } from 'react';
import {Button, Grid, Segment, Header} from 'semantic-ui-react';
import CCSDropDown from './CCSDropDown';

export default class Login extends Component {

    constructor() {
        super();
        this.state = {
            login: '',
            password: '',
            error: undefined,
            redirectToReferrer: false
        };
    }

    state: {
        login: string,
        password: string,
        error?: Error,
        redirectToReferrer: boolean
    };

    render() {
        const { consumerList, dispatch, consumerURL, selectedConsumer}  = this.props;
        return (
            <Grid container>
                <Grid.Column>
                    <Segment raised>
                        <Header size="large">Challenge Authoring System</Header>
                            <Header size="medium">Select your Consumer</Header>
                            <CCSDropDown
                                dispatch={dispatch}
                                consumerList={consumerList}
                            />
                                <Button primary fluid disabled={!selectedConsumer} href={consumerURL}>Go to Consumer</Button>
                    </Segment>
                </Grid.Column>
            </Grid>
        );
    }
}

Login.propTypes = {
    redirectToConsumer: PropTypes.func.isRequired,
    dispatch: PropTypes.func.isRequired,
    errorMessage: PropTypes.string,
    consumerList: PropTypes.array,
    consumerURL: PropTypes.string,
    selectedConsumer: PropTypes.string
};
