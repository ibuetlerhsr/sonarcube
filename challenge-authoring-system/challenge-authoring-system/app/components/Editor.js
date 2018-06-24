import { Component} from 'react';
import uniqueId from 'lodash/uniqueId';

import MarkdownConverter, {transformToMarkdown}from './MarkdownConverter';

export class Editor extends Component {
    constructor(context) {
        super();

        this.state = {
            id: uniqueId(),
            config: Object,
            markdown: Object,
            prevContent: String,
            selection: Object,
            range: Object,
            toolbar: Object,
            menu: Object,
            inputBar: Object,
            inputActive: Boolean,
            stylesheet: undefined,
            isDestroyed: Boolean,
            events: Object,
            debugMode: Boolean,
            eventTargets: Array,
            eventsCache: Array,
            markdownEditorComponent: context,
            regularExpressions: {
                a: [/<a\b[^>]*href=["']([^"]+|[^']+)\b[^>]*>(.*?)<\/a>/ig, '[$2]($1)'],
                br: [/<br>/ig, '\n'],
                img: [/<img\b[^>]*src=["']([^\"+|[^']+)[^>]*>/ig, '![]($1)'],
                b: [/<b\b[^>]*>(.*?)<\/b>/ig, '**$1**'],
                i: [/<i\b[^>]*>(.*?)<\/i>/ig, '*$1*'],
                h: [/<h([1-6])\b[^>]*>(.*?)<\/h\1>/ig, function slice(a, b, c) {
                    return '\n' + ('######'.slice(0, b)) + ' ' + c + '\n';
                }],
                li: [/<(li)\b[^>]*>(.*?)<\/\1>/ig, '* $2\n'],
                blockquote: [/<(blockquote)\b[^>]*>(.*?)<\/\1>/ig, '\n> $2\n'],
                pre: [/<pre\b[^>]*>((.|\r\n?|\n)*?)<\/pre>/ig, '\n```\n$1\n```\n'],
                code: [/<code\b[^>]*>(.*?)<\/code>/ig, '\n`\n$1\n`\n'],
                p: [/<p\b[^>]*>((.|\r\n?|\n)*?)<\/p>/ig, '\n$1\n'],
                div: [/<div\b[^>]*>((.|\r\n?|\n)*?)<\/div>/ig, '\n$1\n'],
                hr: [/<hr\b[^>]*>/ig, '\n---\n']
            }
        };

        this.state.markdown = new MarkdownConverter();
    }

    on = (type, listener) => {
        this.state.markdownEditorComponent.addListener(this, this.state.config.editor, type, listener);
        return this;
    };

    trim = (str) => {
        return (str || '').replace(/^\s+|\s+$/g, '');
    };

    addOnSubmitListener = (inputElement) =>{
        const form = inputElement.form;
        form.addEventListener('submit', function addListener() {
            inputElement.value = this.state.config.saveAsMarkdown ? this.toMarkdown(this.state.config.editor.innerHTML) : this.state.config.editor.innerHTML;
        });
    };

    isEmpty = (node) => {
        let editorNode = null;
        if(node === null || node === undefined) {
            editorNode = this.state.config.editor;
        }
        return !(editorNode.querySelector('img')) && !(editorNode.querySelector('blockquote')) &&
            !(editorNode.querySelector('li')) && !this.trim(editorNode.textContent);
    };

    getEditorContent = () => {
        return this.isEmpty() ?  '' : this.trim(this.state.config.editor.innerHTML);
    };

    setEditorContent = (html) => {
        let modifiedHtml = html;
        if(!modifiedHtml.includes('/api/media')) {
            modifiedHtml = modifiedHtml.replace('/media/', '/api/media/');
        }
        this.state.config.editor.innerHTML = modifiedHtml;
        this.cleanContent({cleanAttrs: ['style']});
        return this;
    };

    checkEditorContentChange = () => {
        const prevContent = this.state.prevContent;
        const currentContent = this.getEditorContent();
        if (prevContent === currentContent) return;
        this.state.prevContent = currentContent;
        this.state.markdownEditorComponent.triggerListener(this, 'change', currentContent, prevContent);
    };

    getRange = () => {
        let range = this.state.selection.rangeCount && this.state.selection.getRangeAt(0);
        if (!range) range = document.createRange();
        if (!this.state.markdownEditorComponent.containsNode(this.state.config.editor, range.commonAncestorContainer)) {
            range.selectNodeContents(this.state.config.editor);
            range.collapse(false);
        }
        return range;
    };

    setRange = (range) => {
        let contextRange = range || this.state.range;
        if (!contextRange) {
            contextRange = this.getRange();
            contextRange.collapse(false); // set to end
        }
        try {
            this.state.selection.removeAllRanges();
            this.state.selection.addRange(contextRange);
        } catch (e) {/* IE throws error sometimes*/}
        return this;
    };

    focus = (focusStart) => {
        if (!focusStart) this.setRange();
        this.state.config.editor.focus();
        return this;
    };

    executeCommand = (name, value) => {
        const commandName = name.toLowerCase();
        this.setRange();

        if (this.state.markdownEditorComponent.state.commandsRegex.block.test(commandName)) {
            this.state.markdownEditorComponent.createCommandBlock(commandName);
        } else if (this.state.markdownEditorComponent.state.commandsRegex.inline.test(commandName)) {
            this.state.markdownEditorComponent.commandOverall(commandName, value);
        } else if (this.state.markdownEditorComponent.state.commandsRegex.source.test(commandName)) {
            this.state.markdownEditorComponent.createCommandLink(commandName, value);
        } else if (this.state.markdownEditorComponent.state.commandsRegex.insert.test(commandName)) {
            this.state.markdownEditorComponent.insertCommand(commandName, value);
        } else if (this.state.markdownEditorComponent.state.commandsRegex.wrap.test(commandName)) {
            this.state.markdownEditorComponent.createCommandWrap(commandName, value);
        } else {
            this.state.markdownEditorComponent.createLog('can not find command function for name: ' + commandName + (value ? (', value: ' + value) : ''), true);
        }
        if (commandName === 'indent') this.checkEditorContentChange();
        else this.cleanContent({cleanAttrs: ['style']});
    };

    cleanContent = (options) => {
        const editor = this.state.config.editor;
        const thisContext = this;
        let contextOption = null;
        if (options) contextOption = this.state.config;
        this.state.markdownEditorComponent.forEach(contextOption.cleanAttrs, function cleanAttr(attr) {
            thisContext.state.markdownEditorComponent.forEach(editor.querySelectorAll('[' + attr + ']'), function removeAttribute(item) {
                item.removeAttribute(attr);
            }, true);
        }, true);
        this.state.markdownEditorComponent.forEach(contextOption.cleanTags, function cleanTag(tag) {
            thisContext.state.markdownEditorComponent.forEach(editor.querySelectorAll(tag), function removeChild(item) {
                item.parentNode.removeChild(item);
            }, true);
        }, true);

        this.state.markdownEditorComponent.checkPlaceholder(this);
        this.checkEditorContentChange();
        return this;
    };

    autoLink = () => {
        this.autoLink(this.state.config.editor);
        return this.getContent();
    };

    highlight = () => {
        const toolbar = this.state.toolbar || this.state.menu;
        const node = this.state.markdownEditorComponent.getNode(this);
        this.state.markdownEditorComponent.forEach(toolbar.querySelectorAll('.active'), function removeHighlight(el) {
            el.classList.remove('active');
        }, true);

        if (!node) return this;

        const effects = this.state.markdownEditorComponent.effectNode(node);
        const inputBar = this.state.inputBar;
        const highlight = function addHighlight(str) {
            if (!str) return null;
            const el = toolbar.querySelector('[data-action=' + str + ']');
            return el && el.classList.add('active');
        };

        if (inputBar && toolbar === this.state.menu) {
            // display link input if createlink enabled
            inputBar.style.display = 'none';
            // reset link input value
            inputBar.value = '';
        }

        this.state.markdownEditorComponent.forEach(effects, function createTag(item) {
            let tag = item.nodeName.toLowerCase();
            switch(tag) {
                case 'a':
                    if (inputBar) inputBar.value = item.getAttribute('href');
                    tag = 'createlink';
                    break;
                case 'img':
                    if (inputBar) inputBar.value = item.getAttribute('src');
                    tag = 'insertimage';
                    break;
                case 'i':
                    tag = 'italic';
                    break;
                case 'u':
                    tag = 'underline';
                    break;
                case 'b':
                    tag = 'bold';
                    break;
                case 'pre':
                case 'code':
                    tag = 'code';
                    break;
                case 'ul':
                    tag = 'insertunorderedlist';
                    break;
                case 'ol':
                    tag = 'insertorderedlist';
                    break;
                case 'li':
                    tag = 'indent';
                    break;
                default:
                    null;
            }
            highlight(tag);
        }, true);

        return this;
    };

    showMenu = () => {
        if (!this.state.menu) return this;
        if (this.state.selection.isCollapsed) {
            this.state.menu.style.display = 'none';
            this.state.inputActive = false;
            return this;
        }
        if (this.state.toolbar) {
            if (!this.state.inputBar || !this.state.inputActive) return this;
        }
        const offset = this.state.range.getBoundingClientRect();
        const menuPadding = 10;
        const top = offset.top - menuPadding;
        const left = offset.left + (offset.width / 2);
        const menu = this.state.menu;
        const menuOffset = {x: 0, y: 0};
        let stylesheet = this.state.stylesheet;

        // fixes some browser double click visual discontinuity
        // if the offset has no width or height it should not be used
        if (offset.width === 0 && offset.height === 0) return this;

        // store the stylesheet used for positioning the menu horizontally
        if (this.state.stylesheet === undefined) {
            const style = document.createElement('style');
            document.head.appendChild(style);
            this.state.stylesheet = stylesheet = style.sheet;
        }
        // display block to caculate its width & height
        menu.style.display = 'block';

        menuOffset.x = left - (menu.clientWidth / 2);
        menuOffset.y = top - menu.clientHeight;

        // check to see if menu has over-extended its bounding box. if it has,
        // 1) apply a new class if overflowed on top;
        // 2) apply a new rule if overflowed on the left
        if (stylesheet.cssRules.length > 0) {
            stylesheet.deleteRule(0);
        }
        if (menuOffset.x < 0) {
            menuOffset.x = 0;
            stylesheet.insertRule('.editor-menu:after {left: ' + left + 'px;}', 0);
        } else {
            stylesheet.insertRule('.editor-menu:after {left: 50%; }', 0);
        }
        if (menuOffset.y < 0) {
            menu.classList.add('editor-menu-below');
            menuOffset.y = offset.top + offset.height + menuPadding;
        } else {
            menu.classList.remove('editor-menu-below');
        }

        menu.style.top = menuOffset.y + 'px';
        menu.style.left = menuOffset.x + 'px';
        return this;
    };

    stay = (config) => {
        if (!window.onbeforeunload) {
            window.onbeforeunload = function getStayMsg() {
                if (!this.state.isDestroyed) return config.stayMsg;
                return null;
            };
        }
    };

    destroy = (isOkay) => {
        const destroy = !isOkay;
        const attr = isOkay ? 'setAttribute' : 'removeAttribute';

        if (!isOkay) {
            this.state.markdownEditorComponent.removeAllListeners();
            try {
                this.state.selection.removeAllRanges();
                if (this.state.menu) this.state.menu.parentNode.removeChild(this.state.menu);
            } catch (e) {/* IE throws error sometimes*/}
        } else {
            this.state.markdownEditorComponent.initializeToolbar(this);
            this.state.markdownEditorComponent.initializeEvents(this);
        }
        this.state.isDestroyed = destroy;
        this.state.config.editor[attr]('contenteditable', '');

        return this;
    };

    rebuild = () => {
        return this.destroy('it\'s a joke');
    };

    fallbackForOldBrowsers = (config) => {
        if (!config) return this.state.markdownEditorComponent.createLog('can\'t find config', true);

        const defaults = this.state.markdownEditorComponent.merge(config);
        let editorClass = defaults.editor.getAttribute('class');

        editorClass = editorClass ? editorClass.replace(/\beditor\b/g, '') + ' editor-textarea ' + defaults.class : 'editor editor-textarea';
        defaults.editor.setAttribute('class', editorClass);
        defaults.editor.innerHTML = defaults.textarea;
        return defaults.editor;
    };

    toMarkdown = () => {
        /* let html = this.getEditorContent()
            .replace(/\n+/g, '')
            .replace(/<([uo])l\b[^>]*>(.*?)<\/\1l>/ig, '$2');

        for(const param in this.state.regularExpressions) {
            if (this.state.regularExpressions.hasOwnProperty(param)) {
                html = html.replace.apply(html, this.state.regularExpressions[param]);
            }
        }
        console.log(html.replace(/\*{5}/g, '**'));
        return html.replace(/\*{5}/g, '**');*/
        const md = transformToMarkdown(this.getEditorContent());
        return md;
    };
}
