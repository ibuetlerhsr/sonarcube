import React, {Component, PropTypes} from 'react';
import { Card, Button } from 'semantic-ui-react';

export default class KeywordComponent extends Component {
    renderChallenges = () => {
        const {keywordList} = this.props;
        keywordList.map(keyword => {
            return (<Card>
                <Card.Content>
                    <Image floated="right" size="mini" src={} />
                    <Card.Header>
                        keyword.text
                    </Card.Header>
                    <Card.Description>
                        Steve wants to add you to the group <strong>best friends</strong>
                    </Card.Description>
                </Card.Content>
                <Card.Content extra>
                    <div className="ui two buttons">
                        <Button basic color="green">Add</Button>
                        <Button basic color="red">Remove</Button>
                    </div>
                </Card.Content>
            </Card>);
        });
    };

    render = () => {
        return (<Card.Group itemsPerRow={4}>

        </Card.Group>);
    }
}

KeywordComponent.propTypes = {
    dispatch: PropTypes.func.isRequired,
    keywordList: PropTypes.array.isRequired
};