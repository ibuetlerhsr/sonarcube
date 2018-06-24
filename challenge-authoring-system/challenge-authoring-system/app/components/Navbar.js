import React, { Component, PropTypes } from 'react';
import Login from './Login';
import {redirectToConsumer} from '../actions/actions';

export default class Navbar extends Component {

    render() {
        const { consumerList, consumerURL, selectedConsumer, dispatch, isAuthenticated, errorMessage } = this.props;

        return (
            <nav className="navbar navbar-default">
                <div className="container-fluid">
                    {/* <a className="navbar-brand" href="#">Challenges App</a> */}
                    <div className="navbar-form">
                        {!isAuthenticated &&
                        <Login
                            errorMessage={errorMessage}
                            consumerURL={consumerURL}
                            redirectToConsumer={ (consumerUrl) => dispatch(redirectToConsumer(consumerUrl)) }
                            consumerList={consumerList}
                            selectedConsumer={selectedConsumer}
                            dispatch={dispatch}
                        />
                        }

                    </div>
                </div>
            </nav>
        );
    }
}

Navbar.propTypes = {
    dispatch: PropTypes.func.isRequired,
    isAuthenticated: PropTypes.bool.isRequired,
    errorMessage: PropTypes.string,
    consumerList: PropTypes.array,
    consumerURL: PropTypes.string,
    selectedConsumer: PropTypes.string
};
