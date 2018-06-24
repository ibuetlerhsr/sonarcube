/* eslint-disable */

import React, { Component, PropTypes } from 'react';
import {Input, FormField, } from 'semantic-ui-react';

export default class SimpleInputField extends Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
        this.textInput.focus();
    }

    render() {
        const { className, size, type, reference, id, labelText, onChange, placeholder, required, onBlur, value} = this.props;
        return (
            <FormField>
                <Input
                    fluid
                    value={value}
                    size={size}
                    className={className}
                    id={id}
                    label={labelText}
                    ref={ input => {
                        this.textInput = input;
                    }}
                    placeholder={placeholder}
                    onChange={onChange}
                    name={name}
                    required={required}
                    type={type}
                />
            </FormField>
        );
    }
}

SimpleInputField.propTypes = {
    className: PropTypes.string,
    id: PropTypes.string,
    labelText: PropTypes.string,
    name: PropTypes.string,
    reference: PropTypes.string,
    onChange: PropTypes.func.isRequired,
    placeholder: PropTypes.string,
    required: PropTypes.bool,
    type: PropTypes.string,
    size: PropTypes.string,
    value: PropTypes.string
};

SimpleInputField.defaultProps = {
    className: 'simpleInputfield',
    required: true,
    type: 'text',
    value: ''
};
