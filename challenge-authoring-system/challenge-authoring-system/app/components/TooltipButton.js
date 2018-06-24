import React, {Component, PropTypes} from 'react';
import CustomIcon from './CustomIcon';
import {Button, Icon} from 'semantic-ui-react';
import ReactTooltip from 'react-tooltip';

export default class TooltipButton extends Component {
    render = () => {
        const {dispatch, divStyle, handleClick, hintCount, instructionCount, sectionCount, showText, type, tooltip, id, buttonStyle, iconStyle, isCustomIcon, dataId} = this.props;
        if(buttonStyle === 'normal' && divStyle === undefined) {
            return (
                <div>
                    {type !== 'section' &&
                    <Button primary fluid data-tip data-for={id} onClick={(event) => handleClick(event, dispatch, dataId, hintCount, instructionCount, type)}>{showText}</Button>
                    }
                    {(type === 'section') &&
                    <Button primary fluid data-tip data-for={id} onClick={(event) => handleClick(event, dispatch, sectionCount)}>{showText}</Button>
                    }
                    <ReactTooltip id={id} place="top" type="dark" effect="float">{tooltip}</ReactTooltip>
                </div>);
        } else if(buttonStyle === 'normal' && divStyle !== undefined) {
            return (
                <div style={divStyle}>
                    {type !== 'section' &&
                    <Button primary fluid data-tip data-for={id} onClick={(event) => handleClick(event, dispatch, dataId, hintCount, instructionCount, type)}>{showText}</Button>
                    }
                    {(type === 'section') &&
                    <Button primary fluid data-tip data-for={id} onClick={(event) => handleClick(event, dispatch, sectionCount)}>{showText}</Button>
                    }
                    <ReactTooltip id={id} place="top" type="dark" effect="float">{tooltip}</ReactTooltip>
                </div>);
        } else if(buttonStyle === 'icon' && divStyle === undefined) {
            return (
                <div>
                    {(type !== 'section' && isCustomIcon) &&
                    <Button icon compact data-tip data-for={id}
                            onClick={(event) => handleClick(event, dispatch, dataId, hintCount, instructionCount, type)}>
                        <CustomIcon icon={iconStyle}/>
                    </Button>
                    }
                    {(type !== 'section' && !isCustomIcon) &&
                    <Button icon data-tip data-for={id}
                            onClick={(event) => handleClick(event, dispatch, dataId, hintCount, instructionCount, type)}>
                        <Icon name={iconStyle} />
                    </Button>
                    }
                    {(type === 'section' && !isCustomIcon) &&
                    <Button icon data-tip data-for={id} style={{marginLeft: 14, marginBottom: 25}} onClick={(event) => handleClick(event, dispatch, sectionCount)}>
                        <Icon name={iconStyle} />
                    </Button>
                    }
                    {(type === 'section' && isCustomIcon) &&
                    <Button icon compact data-tip data-for={id} onClick={(event) => handleClick(event, dispatch, sectionCount)}>
                        <CustomIcon icon={iconStyle}/>
                    </Button>
                    }
                    <ReactTooltip id={id} place="top" type="dark" effect="float">{tooltip}</ReactTooltip>
                </div>);
        } else if(buttonStyle === 'icon' && divStyle !== undefined) {
            return (
                <div style={divStyle}>
                    {(type !== 'section' && isCustomIcon) &&
                    <Button icon compact data-tip data-for={id}
                            onClick={(event) => handleClick(event, dispatch, dataId, hintCount, instructionCount, type)}>
                        <CustomIcon icon={iconStyle}/>
                    </Button>
                    }
                    {(type !== 'section' && !isCustomIcon) &&
                    <Button icon data-tip data-for={id}
                            onClick={(event) => handleClick(event, dispatch, dataId, hintCount, instructionCount, type)}>
                        <Icon name={iconStyle} />
                    </Button>
                    }
                    {(type === 'section' && !isCustomIcon) &&
                    <Button icon data-tip data-for={id} style={{marginLeft: 14, marginBottom: 25}} onClick={(event) => handleClick(event, dispatch, sectionCount)}>
                        <Icon name={iconStyle} />
                    </Button>
                    }
                    {(type === 'section' && isCustomIcon) &&
                    <Button icon compact data-tip data-for={id} onClick={(event) => handleClick(event, dispatch, sectionCount)}>
                        <CustomIcon icon={iconStyle}/>
                    </Button>
                    }
                    <ReactTooltip id={id} place="top" type="dark" effect="float">{tooltip}</ReactTooltip>
                </div>);
        }
        return null;
    }
}
TooltipButton.defaultProps = {
    isCustomIcon: false
};

TooltipButton.propTypes = {
    dispatch: PropTypes.func.isRequired,
    handleClick: PropTypes.func.isRequired,
    showText: PropTypes.string.isRequired,
    tooltip: PropTypes.string.isRequired,
    type: PropTypes.string.isRequired,
    buttonStyle: PropTypes.string.isRequired,
    iconStyle: PropTypes.string,
    isCustomIcon: PropTypes.bool,
    hintCount: PropTypes.number,
    instructionCount: PropTypes.number,
    sectionCount: PropTypes.number,
    id: PropTypes.string,
    dataId: PropTypes.string,
    divStyle: PropTypes.object
};
