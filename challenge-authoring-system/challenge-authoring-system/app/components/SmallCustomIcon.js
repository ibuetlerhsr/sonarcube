import React from 'react';
const {PropTypes} = React;
import instructionAddIcon from '../ressources/images/instructionAdd.png';
import hintAddIcon from '../ressources/images/hintAdd.png';
import compress from '../ressources/images/compress.png';
import expand from '../ressources/images/expand.png';

const icons = {
    'instructionAdd': instructionAddIcon,
    'hintAdd': hintAddIcon,
    'compress': compress,
    'expand': expand
};

const SmallCustomIcon = props => (
    <img width="20" height="20" src={icons[props.icon]}>
    </img>
);

SmallCustomIcon.propTypes = {
    icon: PropTypes.string.isRequired,
};

export default SmallCustomIcon;
