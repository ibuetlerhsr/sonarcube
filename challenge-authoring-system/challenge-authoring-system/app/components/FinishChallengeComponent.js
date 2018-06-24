import React, { Component, } from 'react';
import {Image} from 'semantic-ui-react';
import ImageHelper from '../helper/ImageHelper';

export default class FinishChallengeComponent extends Component {

    handleClick() {
        window.location = 'https://ccs_mit.idocker.hacking-lab.com';
    }

    render() {
        return (
            <Image size={"large"} src={ImageHelper('challengeSuccess')} onClick={() => this.handleClick(event)}/>
        );
    }
}
