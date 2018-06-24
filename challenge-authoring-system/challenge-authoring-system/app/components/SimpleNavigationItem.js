import React, { Component, PropTypes } from 'react';
import {Label, FormField, } from 'semantic-ui-react';

export default class SimpleNavigationItem extends Component {
    render() {
        const { className, as, tag, active, size, id, labelText, onClick } = this.props;
        return (
            <FormField>
                {tag === false &&
                <Label
                    as={as}
                    size={size}
                    style={{width: '100%', marginBottom: '6%'}}
                    active={active}
                    className={className}
                    id={id}
                    children={labelText}
                    onClick={onClick}
                    name={name}
                />}
                {tag === true &&
                <Label
                    as={as}
                    tag
                    size={size}
                    style={{width: '90%', marginBottom: '6%'}}
                    active={active}
                    className={className}
                    id={id}
                    children={labelText}
                    onClick={onClick}
                    name={name}
                />}
            </FormField>
        );
    }
}

SimpleNavigationItem.propTypes = {
    className: PropTypes.string,
    as: PropTypes.string,
    tag: PropTypes.bool,
    id: PropTypes.string,
    labelText: PropTypes.string,
    name: PropTypes.string,
    onClick: PropTypes.func.isRequired,
    size: PropTypes.string,
    active: PropTypes.bool
};

SimpleNavigationItem.defaultProps = {
    className: 'simpleNavigationItem',
    as: 'a'
};
