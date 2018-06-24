import { Component } from 'react';
import marked from 'marked';

export default class MarkdownConverter extends Component {
    constructor() {
        super();
        this.state = {
            markdownKeys: ['#', '*', '-', '+', '~', '.', '1', '>', '[', '_', ']', '(', ')', '`', '|'],
            stack: []
        };
    }

    action = (editor, cmd) => {
        if (editor.state.selection.focusOffset > cmd[1]) return;

        const node = editor.state.selection.focusNode;
        node.textContent = node.textContent.slice(cmd[1]);
        editor.executeCommand(cmd[0]);
    };


    registerKeyPress = (editor: any) => {
        const thisContext = this;
        editor.on('keypress', function keyPress(event: Event) {
            const markdownCommand = thisContext.parseKey(event);
            if (markdownCommand) return thisContext.action(editor, markdownCommand);
            return null;
        });
    };

    parseKey = (event: Event) => {
        const pressedKeyCode = event.keyCode || event.which;
        const pressedKey = String.fromCharCode(pressedKeyCode);

        if (pressedKey === ' ' || pressedKey === '\n') {
            const markdownSyntax = this.state.stack.join('');

            this.state.stack = [];

            const markdownCommand = this.transformToMarkdown(markdownSyntax);
            if (markdownCommand) {
                event.preventDefault();
                return markdownCommand;
            }
        }
        const thisContext = this;
        this.state.markdownKeys.find(function addToStack(element) {
            if(element === pressedKey) {
                thisContext.state.stack.push(pressedKey);
            }
        });
        return false;
    };

    transformToMarkdown = (stringToTransform : string) => {
        const stringlength = stringToTransform.length;

        if (stringToTransform.match(/[#]{1,6}/)) {
            return ['h' + stringlength, stringlength];
        } else if (stringToTransform === '```') {
            return ['pre', stringlength];
        } else if (stringToTransform === '>') {
            return ['blockquote', stringlength];
        } else if (stringToTransform === '1.') {
            return ['insertorderedlist', stringlength];
        } else if (stringToTransform === '-' || stringToTransform === '*' || stringToTransform === '+' ) {
            return ['insertunorderedlist', stringlength];
        } else if (stringToTransform.match(/(?:\.|\*|-){3,}/)) {
            return ['inserthorizontalrule', stringlength];
        } else if (stringToTransform.match(/\[(?:[[:alnum:]]|[[:blank:]]|\.|\/|'|-)*\]\((?:[[:alnum:]]|[[:blank:]]|\.|\/|\:)*\)/)) {
            return ['createlink', stringlength];
        } else if (stringToTransform.match(/!\[(?:[[:alnum:]]|[[:blank:]]|\.|\/|'|-)*\]\((?:[[:alnum:]]|[[:blank:]]|\.|\/|\:)*\)/)) {
            return ['insertimage', stringlength];
        } return null;
    };
}

export const transformToHtml = (markdownString: string) => {
    const regularExpression = {
        id: [/ id=".*"/ig, '']};
    const find = '/media/';
    const regex = new RegExp(find, 'g');

    const markdownStringReplaced = markdownString.replace(regex, '/api/media/');
    let html = marked(markdownStringReplaced);
    html = html.replace.apply(html, regularExpression.id);
    return html;
};

export const transformToMarkdown = (htmlString: string) => {
    const TurndownService = require('turndown');
    const find = '/api/media/';
    const regex = new RegExp(find, 'g');
    const htmlReplaced = htmlString.replace(regex, '/media/');
    const turndownService = new TurndownService();
    return turndownService.turndown(htmlReplaced);
};

