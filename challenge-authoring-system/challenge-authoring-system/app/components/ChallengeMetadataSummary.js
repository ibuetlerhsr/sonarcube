import React, {Component, PropTypes} from 'react';
import {Segment, Header} from 'semantic-ui-react';

export default class ChallengeMetadataSummary extends Component {

    renderArray = (array) => {
        const component = [];
        if(array === undefined) {
            return component;
        }
        for(let i = 0; i < array.length; i++) {
            let value;
            if(array[i] === undefined) {
                continue;
            }
            if(array[i].text === undefined) {
                value = array[i].toString();
            } else {
                value = array[i].text.toString();
            }

            component.push(<Segment key={array[i] + i}>{value}</Segment>);
        }
        return component;
    };

    render = () => {
        const {challengeMetadata} = this.props;
        return (
            <div>
                <Segment.Group>
                    <Header as="h3" attached="top">
                        Name
                    </Header>
                    <Segment attached>
                        {challengeMetadata !== undefined ? challengeMetadata.name !== undefined  && challengeMetadata.name !== '' ? challengeMetadata.name : 'no name set' : 'challenge metadata is undefined'}
                    </Segment>
                    <Header as="h3" attached="top">
                        Level
                    </Header>
                    <Segment attached>
                        {challengeMetadata !== undefined ? challengeMetadata.level !== ''  && challengeMetadata.level !== -1 ? challengeMetadata.level : 'no level set' : 'challenge metadata is undefined'}
                    </Segment>
                    <Header as="h3" attached="top">
                        Type
                    </Header>
                    <Segment attached>
                        {challengeMetadata !== undefined ? challengeMetadata.type !== undefined  && challengeMetadata.type !== '' ? challengeMetadata.type : 'no type set' : 'challenge metadata is undefined'}
                    </Segment>
                    <Header as="h3" attached="top">Usages</Header>
                    <Segment.Group horizontal>
                        {challengeMetadata !== undefined && challengeMetadata.usages !== undefined && challengeMetadata.usages.length > 0 &&
                        this.renderArray(challengeMetadata.usages)
                        }
                        {challengeMetadata !== undefined && (challengeMetadata.usages === undefined || challengeMetadata.usages.length === 0) &&
                        'no usages set'
                        }
                        {challengeMetadata === undefined &&
                        'challenge metadata is undefined'
                        }
                    </Segment.Group>
                    <Header as="h3" attached="top">Keywords</Header>
                    <Segment.Group horizontal>
                        {challengeMetadata !== undefined && challengeMetadata.keywords !== undefined && challengeMetadata.keywords.length > 0 &&
                        this.renderArray(challengeMetadata.keywords)
                        }
                        {challengeMetadata !== undefined && (challengeMetadata.keywords === undefined || challengeMetadata.keywords.length === 0) &&
                        'no keywords set'
                        }
                        {challengeMetadata === undefined &&
                        'challenge metadata is undefined'
                        }
                    </Segment.Group>
                    <Header as="h3" attached="top">Categories</Header>
                    <Segment.Group horizontal>
                        {challengeMetadata !== undefined && challengeMetadata.categories !== undefined && challengeMetadata.categories.length > 0 &&
                        this.renderArray(challengeMetadata.categories)
                        }
                        {challengeMetadata !== undefined && (challengeMetadata.categories === undefined || challengeMetadata.categories.length === 0) &&
                        'no categories set'
                        }
                        {challengeMetadata === undefined &&
                        'challenge metadata is undefined'
                        }
                    </Segment.Group>
                    <Header as="h3" attached="top">
                        Goldnugget-Type
                    </Header>
                    <Segment>
                        {challengeMetadata !== undefined ? challengeMetadata.goldnuggetType !== undefined && challengeMetadata.goldnuggetType !== '' ? challengeMetadata.goldnuggetType : 'no goldnugget type set' : 'challenge metadata is undefined'}
                    </Segment>
                    <Header as="h3" attached="top">
                        Static Goldnugget
                    </Header>
                    <Segment>
                        {challengeMetadata !== undefined ? challengeMetadata.staticGoldnuggetSecret !== undefined && challengeMetadata.staticGoldnuggetSecret !== '' ? challengeMetadata.staticGoldnuggetSecret : 'no static goldnugget secret set' : 'challenge metadata is undefined'}
                    </Segment>
                </Segment.Group>
            </div>
        );
    }
}

ChallengeMetadataSummary.propTypes = {
    challengeMetadata: PropTypes.object.isRequired
};
