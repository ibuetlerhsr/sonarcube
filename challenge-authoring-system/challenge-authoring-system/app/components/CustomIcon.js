import React from 'react';
const {PropTypes} = React;
import instructionAddIcon from '../ressources/images/instructionAdd.png';
import hintAddIcon from '../ressources/images/hintAdd.png';
import compress from '../ressources/images/compress.png';
import expand from '../ressources/images/expand.png';
import home from '../ressources/icons/001-internet.svg';
import logout from '../ressources/icons/004-exit.svg';

const icons = {
    'instructionAdd': instructionAddIcon,
    'hintAdd': hintAddIcon,
    'compress': compress,
    'expand': expand,
    'home': home,
    'logout': logout
};

const CustomIcon = props => (
    <img width="35" height="35" src={icons[props.icon]}>
    </img>
);

CustomIcon.propTypes = {
    icon: PropTypes.string.isRequired,
};

export default CustomIcon;
