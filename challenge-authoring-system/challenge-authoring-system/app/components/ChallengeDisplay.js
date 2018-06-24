import React, {Component, PropTypes} from 'react';
import { Card, Image } from 'semantic-ui-react';

export default class ChallengeDisplay extends Component {
    renderChallenges = () => {
        const {challengeList} = this.props;
        challengeList.map(challenge => {
            return (<Card raised>
                <Image src={challenge.image}/>
                <Card.Content extra>
                    <div className="ui star rating" data-rating={challenge.rating}/>
                </Card.Content>
            </Card>);
        });
    };

    render = () => {
        return (<Card.Group itemsPerRow={4}>

            </Card.Group>);
    }
}

ChallengeDisplay.propTypes = {
    dispatch: PropTypes.func.isRequired,
    challengeList: PropTypes.array.isRequired
};
