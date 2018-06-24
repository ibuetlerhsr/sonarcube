import React, {Component, PropTypes} from 'react';
import { Dimmer, Image, Button, Header, Icon } from 'semantic-ui-react';
import ReactTooltip from 'react-tooltip';
import CategoryIconHelper from '../helper/CategoryIconhelper';


export default class ChallengeCategoryCard extends Component {

    state = {};

    handleShow = () => this.setState({ active: true });
    handleHide = () => this.setState({ active: false });

    render = () => {
        const {challengeCategory, categoryId, cardSize, removeChallengeCategory} = this.props;
        const { active } = this.state;
        const content = (
            <div>
                <Header as="h2" inverted>{challengeCategory}</Header>
                <Button icon data-tip data-for={challengeCategory}  primary onClick={(event) => removeChallengeCategory(event, categoryId)}>
                    <Icon name={'remove'}/>
                </Button>
                <ReactTooltip id={challengeCategory} place="top" type="dark" effect="float">{'Remove Category'}</ReactTooltip>
            </div>
        );
        return (
            <Dimmer.Dimmable
                as={Image}
                dimmed={active}
                dimmer={{ active, content }}
                onMouseEnter={this.handleShow}
                onMouseLeave={this.handleHide}
                size={cardSize}
                src={CategoryIconHelper(categoryId)}
            />
        );
    }
}

ChallengeCategoryCard.propTypes = {
    dispatch: PropTypes.func.isRequired,
    challengeId: PropTypes.string.isRequired,
    challengeCategory: PropTypes.string.isRequired,
    cardSize: PropTypes.string,
    categoryId: PropTypes.number,
    removeChallengeCategory: PropTypes.func.isRequired
};


ChallengeCategoryCard.defaultProps = {
    cardSize: 'small'
};
