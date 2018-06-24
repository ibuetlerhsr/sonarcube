import uniqueId from 'lodash/uniqueId';
import React, {Component, PropTypes} from 'react';
import Sortable from 'react-sortablejs';

export default class SortableList extends Component {
    state: {
        sortableObject: Object
    } = {sortableObject: {}};

    render() {
        const {onChange, items} = this.props;
        const listItems = items.map(value => {
            let item = {};
            item = React.cloneElement(value, {key: uniqueId()});
            return item;
        });
        return (
            <div>
                <Sortable
                    options={{
                    }}

                    ref={(c) => {
                        if (c) {
                            this.state.sortableObject = c.sortable;
                        }
                    }}

                    tag="div"

                    // [Optional] The onChange method allows you to implement a controlled component and keep
                    // DOM nodes untouched. You have to change state to re-render the component.
                    // @param {Array} order An ordered array of items defined by the `data-id` attribute.
                    // @param {Object} sortable The sortable instance.
                    // @param {Event} evt The event object.
                    onChange={(order) => {
                        onChange(order);
                    }}
                >
                    {listItems}
                </Sortable>
            </div>
        );
    }
}

SortableList.propTypes = {
    items: PropTypes.array,
    onChange: PropTypes.func
};
