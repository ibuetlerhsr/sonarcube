import expect from 'expect';
import Enzyme from 'enzyme';
import Adapter from 'enzyme-adapter-react-15';
import {transformToHtml} from './MarkdownConverter';

Enzyme.configure({ adapter: new Adapter() });

test('Transform to HTML', () => {
    const output = transformToHtml("## This is an english abstract\r\n\r\nIt's a challenge in which you should implement a function\r\n\r\n##### A small title\r\n\r\nBlabla Test\r\n\r\n![Globus](/media/png/globus.png)\r\n");
    console.log(output);
    expect(output).toEqual('Off');
});
