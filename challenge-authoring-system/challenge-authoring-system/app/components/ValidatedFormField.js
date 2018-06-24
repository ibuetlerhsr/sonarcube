/**
 * Created by jengeler on 30.04.2017.
 */
import React, {Component, PropTypes} from 'react';
import {Input, FormField, Label} from 'semantic-ui-react';

export default class ValidatedFormField extends Component {
    state : {
        value: any,
        message: string,
        color: string,
        isWrong: boolean,
        validated: boolean
    } = {color: 'red'};

    validateMinimumLength = (value: any, length: number) => {
        if(value.length < length) {
            this.setState({message: 'should have more than ' + length + ' characters'});
        }
        return true;
    };

    validateEquality = (value: any, other: any) => {
        if(value !== other.props.value) {
            this.setState({message: 'should be equal to ' + other.props.label});
            return false;
        }
        return true;
    };

    validateValue = (value: any) => {
        if(value === '') {
            this.setState({message: 'should not be empty'});
            return false;
        }
        return true;
    };

    clearMessage = () => {
        this.setState({message: ''});
    };

    executeValidation = (value: any) => {
        let isWrong = false;
        this.clearMessage();
        this.setState({hasError: true});

        const {validations} = this.props;
        if(validations) {
            Object.keys(validations).forEach(function(key) {
                if(key === 'empty' && validations[key] === true) {
                    if(!this.validateValue(value)) {
                        isWrong = true;
                    }
                }

                if(key === 'minLength') {
                    if(!this.validateMinimumLength(value, validations[key])) {
                        isWrong = true;
                    }
                }

                if(key === 'equal' && validations[key] !== undefined) {
                    if(!this.validateEquality(value, validations[key])) {
                        isWrong = true;
                    }
                }
            }, this);
        }

        this.setState({validated: true, isWrong: isWrong});
        return isWrong;
    };

    validationCallback = (message: string, color: string, isWrong: boolean, validated: boolean) => {
        this.setState({validated: validated, isWrong: isWrong, message: message, color: color});
    };

    validate = (value: any) => {
        const {validations} = this.props;
        if (typeof validations === 'function') {
            validations(value, this.validationCallback);
        } else {
            this.executeValidation(value);
        }
    };

    handleOnChange = (event: Event) => {
        const {onChange} = this.props;
        if(event.target instanceof HTMLInputElement) {
            this.setState({value: event.target.value});
            this.validate(event.target.value);

            if(onChange) {
                onChange(event);
            }
        }

        return true;
    };

    render() {
        const {text, ...props} = this.props;
        return (
            <FormField>
                <Input {...props} onChange={this.handleOnChange}
                                  error={Boolean(this.state.validated && this.state.isWrong)}/>
                {
                    this.state.message && (this.state.color ?
                            <Label basic pointing color={this.state.color} content={this.state.message}/>
                            :
                            <Label basic pointing content={this.state.message}/>
                    )
                }
                {
                    !this.state.validated &&
                    text
                }
            </FormField>
        );
    }
}
ValidatedFormField.propTypes = {
    dispatch: PropTypes.func.isRequired,
    onChange: PropTypes.func.isRequired,
    text: PropTypes.string,
    validations: PropTypes.object
};
