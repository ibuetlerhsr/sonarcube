import React, { Component, PropTypes } from 'react';
import {Segment, Button} from 'semantic-ui-react';
import {setActualEditorStep, setEditorArray, setShowToSectionReferences, uploadImage} from '../actions/actions';
import TooltipButton from './TooltipButton';
import SmallCustomIcon from './SmallCustomIcon';
import {Editor} from './Editor';
import {transformToHtml} from './MarkdownConverter';

import '../css/MarkdownEditor.css';
import '../css/medium-editor.css';

const SERVER_ADDRESS = process.env.ADDRESS_CAS_SERVER || 'cas-eu.idocker.hacking-lab.com';
const SERVER_PORT = process.env.SERVER_PORT || 443;
const SERVER_PROTOCOL = process.env.SERVER_PROTOCOL || 'https';
const WEBSOCKET = SERVER_PROTOCOL === 'https' ? 'wss' : 'ws';

export default class MarkdownEditorComponent extends Component {
    state: {
        editor: Object,
        closed: boolean,
        defaults: Object,
        myWebSocket: WebSocket,
        isOpen: boolean,
        debugMode: Object,
        selection: Object,
        utils: Object,
        commandsRegex: Object,
        lineBreakRegex: RegExp,
        effectNodeRegex: RegExp,
        stringRegex: Object,
        autoLinkRegex: Object,
        allowedTypes: Array,
    } = { myWebSocket: new WebSocket(WEBSOCKET + '://echo.websocket.org'), isOpen: false,
        defaults: {},
        closed: false,
        allowedTypes: ['images'],
        commandsRegex: {block: /^(?:p|h[1-6]|blockquote|pre)$/,
            inline: /^(?:bold|italic|underline|insertorderedlist|insertunorderedlist|indent|outdent)$/,
            source: /^(?:createlink|unlink)$/,
            insert: /^(?:inserthorizontalrule|insertimage|insert)$/,
            wrap: /^(?:code)$/},
        lineBreakRegex: /^(?:blockquote|pre|div)$/i,
        effectNodeRegex: /(?:[pubia]|h[1-6]|blockquote|[uo]l|li)/i,
        stringRegex: {
            whiteSpace: /(^\s+)|(\s+$)/g,
            mailTo: /^(?!mailto:|.+\/|.+#|.+\?)(.*@.*\..+)$/,
            http: /^(?!\w+?:\/\/|mailto:|\/|\.\/|\?|#)(.*)$/
        },
        autoLinkRegex: {
            url: /((https?|ftp):\/\/|www\.)[^\s<]{3,}/gi,
            prefix: /^(?:https?|ftp):\/\//i,
            notLink: /^(?:img|a|input|audio|video|source|code|pre|script|head|title|style)$/i,
            maxLength: 100
        }};

    componentDidMount() {
        const {dataId, removeEditor, content} = this.props;
        const options = {
            editor: document.querySelector('[data-toggle="' + dataId + '"]'),
            debug: true,
            list: [
                'insertimage', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'pre', 'insertorderedlist', 'insertunorderedlist', 'inserthorizontalrule',
                'bold', 'italic', 'createlink'
            ]
        };
        if (document.getSelection) {
            this.state.selection = document.getSelection();
        }
        let editor = this.getEditorFromEditorArray();
        if(editor === null) {
            if(content !== '' && content !== undefined) {
                editor = this.createEditor(options, true, undefined, content);
            } else {
                editor = this.createEditor(options, true);
            }
            this.fillEditorArray(editor);
        } else {
            if(options.editor !== null && options.editor !== undefined) {
                editor = this.createEditor(options, false, editor);
            }
        }
        // root.Editor = editor.fallbackForOldBrowsers(editor.config);
        if (typeof document !== 'undefined') {
            const $ = require('jquery');
            const context = this;
            const id = dataId + 'Remove';
            $('#' + id).click(() => {
                context.removeEditor(dataId, removeEditor);
            });
        }
        this.connectToWebsocket();
        if(options.editor !== null && options.editor !== undefined) {
            editor.focus();
            this.replaceEditorArrayItem(editor);
        }
    }

    removeEditor = (dataId, removeEditor) => {
        const $ = require('jquery');
        $('#' + dataId).remove();
        removeEditor(dataId);
    };

    createEditorArrayItem = (id, editorObject) => {
        return {key: id, editor: editorObject };
    };

    getEditorFromEditorArray = () => {
        const {editorArray, dataId} = this.props;
        const filteredObject = editorArray.filter(editorObject =>(editorObject.key === dataId));
        if(filteredObject.length > 0) {
            return filteredObject[0].editor;
        }
        return null;
    };

    replaceEditorArrayItem = (editorObject) => {
        const {editorArray, dataId, dispatch} = this.props;
        editorArray.forEach((item, i) => {
            if(item.key === dataId) {
                editorArray[i] = this.createEditorArrayItem(dataId, editorObject);
            }});
        dispatch(setEditorArray(editorArray));
    };

    fillEditorArray = (editor) => {
        const {editorArray, dataId, dispatch} = this.props;
        const filteredObject = editorArray.filter(editorObject =>(editorObject.key === dataId));
        if(filteredObject.length > 0) {
            this.replaceEditorArrayItem(editor);
        } else {
            editorArray.push(this.createEditorArrayItem(dataId, editor));
        }

        dispatch(setEditorArray(editorArray));
    };

    connectToWebsocket() {
        const {valueType, challengeId, valueId, sectionId} = this.props;
        let valueID = 'valueId';
        let sectionID = 'sectionId';
        if(valueId !== '') {
            valueID = valueId;
        }
        if(sectionId !== undefined && sectionId !== '') {
            sectionID = sectionId;
        }
        const context = this;
        const endpoint = WEBSOCKET + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/challenge/wsCreateNew/' + challengeId + '-;-' + valueID + '-;-' + valueType + '-;-' + sectionID + '';
        if (this.state.myWebSocket !== undefined) {
            this.state.myWebSocket.close();
        }
        this.state.myWebSocket = new WebSocket(endpoint);
        this.state.myWebSocket.onmessage = event => {
            let leng;
            if (event.data.size === undefined) {
                leng = event.data.length;
            } else {
                leng = event.data.size;
            }
            console.log('onmessage. size: ' + leng + ', content: ' + event.data);// eslint-disable-line no-console
        };
        this.state.myWebSocket.onopen = () => {
            context.state.isOpen = true;
            console.log('onopen.');// eslint-disable-line no-console
        };
        this.state.myWebSocket.onclose = () => {
            context.state.isOpen = false;
            console.log('onclose.');// eslint-disable-line no-console
        };
        this.state.myWebSocket.onerror = () => {
            console.log('Error!');// eslint-disable-line no-console
        };

        setInterval(() => {
            this.handleChange();
        }, 1000);
    }

    closeConn() {
        if(this.state.isOpen) {
            this.state.myWebSocket.close();
        }
    }

    handleChange = () => {
        if(this.state.isOpen) {
            this.state.myWebSocket.send(this.getEditorFromEditorArray().toMarkdown());
        }
    };

    getType = (obj, type) => {
        if(obj === null)return false;
        return obj.toString().slice(8, -1) === type;
    };

    forEach = (obj, iterator, arrayType) => {
        const tempArrayType = arrayType === null ? this.getType(obj, 'Array') : null;
        if (!obj) return;
        if (tempArrayType) {
            for (let i = 0, l = obj.length; i < l; i++) iterator(obj[i], i, obj);
        } else {
            for (const key in obj) {
                if (obj.hasOwnProperty(key)) iterator(obj[key], key, obj);
            }
        }
    };

    copyPropsFromObject = (defaults, source) =>{
        const context = this;
        this.forEach(source, function getType(value, key) {
            if(context.getType(value, 'Object')) {
                defaults[key] = context.copyPropsFromObject({}, value);
            } else {
                if(context.getType(value, 'Array')) {
                    defaults[key] = context.copyPropsFromObject([], value);
                } else {
                    defaults[key] = value;
                }
            }
        });
        return defaults;
    };

    createLog = (message, force) => {
        if (this.state.debugMode || force) {
            // eslint-disable-next-line no-console
            console.log('%cEDITOR DEBUGGER: %c' + message, 'font-family:arial,sans-serif;color:#1abf89;line-height:2em;', 'font-family:cursor,monospace;color:#333;');
        }
    };

    delayExecution = (func) => {
        let timer = null;
        return function createDelay(delay) {
            clearTimeout(timer);
            timer = setTimeout(function execute() {
                func();
            }, delay || 1);
        };
    };

    merge = (config) => {
        let defaultSettings = {
            class: 'editor',
            debug: false,
            toolbar: null,
            stay: config.stay || !config.debug,
            stayMsg: 'Are you going to leave here?',
            textarea: '<textarea name="editorContent"></textarea>',
            list: [
                'blockquote', 'h2', 'h3', 'p', 'pre', 'insertorderedlist', 'insertunorderedlist', 'inserthorizontalrule',
                'indent', 'outdent', 'bold', 'italic', 'underline', 'createlink', 'insertimage'
            ],
            titles: {
                'blockquote': 'Blockquote', 'h1': 'H1', 'h2': 'H2', 'h3': 'H3', 'h4': 'H4', 'h5': 'H5', 'h6': 'H6', 'p': 'New Line', 'pre': 'Codeblock', 'insertorderedlist': 'Ordered List', 'insertunorderedlist': 'Unordered List', 'inserthorizontalrule': 'Horizontal Rule',
                'indent': 'Indent', 'outdent': 'Outdent', 'bold': 'Bold', 'italic': 'Italic', 'underline': 'Underline', 'createlink': 'Hyperlink', 'insertimage': 'Image'
            },
            cleanAttrs: ['id', 'class', 'style', 'name'],
            cleanTags: ['script'],
            linksInNewWindow: false
        };

        if (config.nodeType === 1) {
            defaultSettings.editor = config;
        } else if (config.match && config.match(/^#[\S]+$/)) {
            defaultSettings.editor = document.getElementById(config.slice(1));
        } else {
            defaultSettings = this.copyPropsFromObject(defaultSettings, config);
        }

        return defaultSettings;
    };

    commandOverall = (cmd, val) => {
        const message = ' to exec 「' + cmd + '」 command' + (val ? (' with value: ' + val) : '');

        try {
            document.execCommand(cmd, false, val);
        } catch(err) {
            // TODO: there's an error when insert a image to document, but not a bug
            return this.createLog('fail' + message, true);
        }

        this.createLog('success' + message);
        return null;
    };

    insertCommand = (name, val) =>{
        const editor = this.getEditorFromEditorArray();
        const node = this.getNode();
        if (!node) return null;
        editor.state.range.selectNode(node);
        editor.state.range.collapse(false);

        if(name === 'insertimage' && editor.state.menu) this.toggleNode(editor.state.menu, true);
        this.replaceEditorArrayItem(editor);
        return this.commandOverall(name, val);
    };

    createCommandBlock = (name) => {
        let commandName = name;
        const list = this.effectNode(this.getNode(), true);
        if (list.indexOf(commandName) !== -1) commandName = 'p';
        return this.commandOverall('formatblock', commandName);
    };

    createCommandWrap = (tag, value) => {
        const commandValue = '<' + tag + '>' + (value || this.state.selection.toString()) + '</' + tag + '>';
        return this.commandOverall('insertHTML', commandValue);
    };

    createCommandLink = (tag, value) => {
        let commandValue = null;
        if (this.getEditorFromEditorArray().state.config.linksInNewWindow) {
            commandValue = '< a href="' + value + '" target="_blank">' + (this.state.selection.toString()) + '</a>';
            return this.commandOverall('insertHTML', commandValue);
        }
        return this.commandOverall(tag, value);
    };

    initializeToolbar = (context) => {
        let icons = '';
        const inputStr = '<input class="editor-input" placeholder="http://" />';

        context.state.toolbar = context.state.config.toolbar;
        if (!context.state.toolbar) {
            const toolList = context.state.config.list;
            this.forEach(toolList, function createIcon(name) {
                const iconClass = 'editor-icon icon-' + name;
                const title = context.state.config.titles[name] || '';
                icons += '<i class="' + iconClass + '" data-action="' + name + '" title="' + title + '"></i>';
            }, true);
            if (toolList.indexOf('createlink') >= 0 || toolList.indexOf('insertimage') >= 0) {
                icons += inputStr;
            }
        } else if (context.state.toolbar.querySelectorAll('[data-action=createlink]').length ||
            context.state.toolbar.querySelectorAll('[data-action=insertimage]').length) {
            icons += inputStr;
        }

        if (icons) {
            context.state.menu = document.createElement('div');
            context.state.menu.setAttribute('class', context.state.config.class + '-menu editor-menu');
            context.state.menu.innerHTML = icons;
            context.state.inputBar = context.state.menu.querySelector('input');
            this.toggleNode(context.state.menu, true);
            document.body.appendChild(context.state.menu);
        }
        if (context.state.toolbar && context.state.inputBar) this.toggleNode(context.state.inputBar);
    };

    initializeEvents = (context) => {
        const toolbar = context.state.toolbar || context.state.menu;
        const editor = context.state.config.editor;

        const toggleMenu = this.delayExecution(function highlight() {
            context.highlight().showMenu();
        });
        let outsideClick = () => {};

        function updateStatus(delay) {
            context.state.range = context.getRange();
            toggleMenu(delay);
        }
        const thisContext = this;
        if (context.state.menu) {
            /* const setPosition = () => {
                if (context.state.menu.style.display === 'block') context.showMenu();
            };

            this.addListener(context, root, 'resize', setPosition);
            this.addListener(context, root, 'scroll', setPosition); */

            let selecting = false;
            this.addListener(context, editor, 'mousedown', () => {
                selecting = true;
            });
            this.addListener(context, editor, 'mouseleave', () => {
                if (selecting) updateStatus(800);
                selecting = false;
            });
            this.addListener(context, editor, 'mouseup', () => {
                if (selecting) updateStatus(100);
                selecting = false;
            });

            outsideClick = (e) => {
                if (context.state.menu && !thisContext.containsNode(editor, e.target) && !thisContext.containsNode(context.state.menu, e.target)) {
                    thisContext.removeListener(document, 'click', outsideClick);
                    toggleMenu(100);
                }
            };
        } else {
            this.addListener(context, editor, 'click', () => {
                updateStatus(0);
            });
        }

        this.addListener(context, editor, 'keyup', function keyUpImplementation(e) {
            if (e.which === 8 && context.isEmpty()) return thisContext.lineBreak(true);
            // toggle toolbar on key select
            if (e.which !== 13 || e.shiftKey) return updateStatus(400);
            const node = thisContext.getNode(true);
            if (!node || !node.nextSibling || !thisContext.state.lineBreakRegex.test(node.nodeName)) return null;
            if (node.nodeName !== node.nextSibling.nodeName) return null;
            // hack for webkit, make 'enter' behavior like as firefox.
            if (node.lastChild.nodeName !== 'BR') node.appendChild(document.createElement('br'));
            thisContext.forEach(node.nextSibling.childNodes, function appendNodeChild(child) {
                if (child) node.appendChild(child);
            }, true);
            node.parentNode.removeChild(node.nextSibling);
            thisContext.focusNode(node.lastChild, context.getRange());
            return null;
        });

        this.addListener(context, editor, 'keydown', function keyDownImplementation(e) {
            editor.classList.remove('editor-placeholder');
            if (e.which !== 13 || e.shiftKey) return;
            const node = thisContext.getNode(true);
            if (!node || !thisContext.state.lineBreakRegex.test(node.nodeName)) return;
            const lastChild = node.lastChild;
            if (!lastChild || !lastChild.previousSibling) return;
            if (lastChild.previousSibling.textContent || lastChild.textContent) return;
            // quit block mode for 2 'enter'
            e.preventDefault();
            const p = document.createElement('p');
            p.innerHTML = '<br>';
            node.removeChild(lastChild);
            if (!node.nextSibling) node.parentNode.appendChild(p);
            else node.parentNode.insertBefore(p, node.nextSibling);
            thisContext.focusNode(p, context.getRange());
        });

        const menuApply = function highlightMenu(action, value) {
            context.executeCommand(action, value);
            context.state.range = context.getRange();
            context.highlight().showMenu();
        };

        this.addListener(context, toolbar, 'click', function clickImplementation(event) {
            let node = event.target;
            let action = node.getAttribute('data-action');

            while (node !== toolbar && !action) {
                node = node.parentNode;
                action = node.getAttribute('data-action');
            }

            if (!action) return null;
            if (!/(?:createlink)|(?:insertimage)/.test(action)) return menuApply(action);
            if (!context.state.inputBar) return null;

            const input = context.state.inputBar;
            if (toolbar === context.state.menu) thisContext.toggleNode(input);
            else {
                context.state.inputActive = true;
                context.showMenu();
            }
            if (context.state.menu.style.display === 'none') return null;
            setTimeout(function setFocus() {
                input.focus();
            }, 400);
            const createLink = function replaceLinkValue() {
                let inputValue = input.value;

                if (!inputValue) action = 'unlink';
                else {
                    inputValue = input.value
                        .replace(thisContext.state.stringRegex.whiteSpace, '')
                        .replace(thisContext.state.stringRegex.mailTo, 'mailto:$1')
                        .replace(thisContext.state.stringRegex.http, 'http://$1');
                }
                menuApply(action, inputValue);
                if (toolbar === context.state.menu) thisContext.toggleNode(input, false);
                else thisContext.toggleNode(context.state.menu, true);
            };

            input.onkeypress = function keyPressImplementation(e) {
                if (e.which === 13) return createLink();
                return null;
            };

            return null;
        });

        this.addListener(context, editor, 'focus', () => {
            if (context.isEmpty()) thisContext.lineBreak(true);
            thisContext.addListener(context, document, 'click', outsideClick);
            thisContext.connectToWebsocket();
        });

        this.addListener(context, editor, 'blur', () => {
            thisContext.checkPlaceholder(context);
            context.checkEditorContentChange();
            thisContext.closeConn();
        });

        // listen for paste and clear style
        this.addListener(context, editor, 'paste', () => {
            setTimeout(() => {
                context.cleanContent();
            });
        });
        this.addListener(context, editor, 'dragover', (event) => {
            thisContext.checkClassNamesForDrag(context);
            thisContext.handleDrag(event);
        });
        this.addListener(context, editor, 'drop', (event) => {
            thisContext.checkClassNamesForDrag(context);
            thisContext.handleDrop(event);
        });
    };

    addListener = (context, target, type, listener) =>{
        if (context.state.events.hasOwnProperty(type)) {
            context.state.events[type].push(listener);
        } else {
            context.state.eventTargets = context.state.eventTargets || [];
            context.state.eventsCache = context.state.eventsCache || [];
            let index = context.state.eventTargets.indexOf(target);
            if (index < 0) index = context.state.eventTargets.push(target) - 1;
            context.state.eventsCache[index] = context.state.eventsCache[index] || {};
            context.state.eventsCache[index][type] = context.state.eventsCache[index][type] || [];
            context.state.eventsCache[index][type].push(listener);

            target.addEventListener(type, listener, false);
        }
        return context;
    };

    triggerListener = (editorContext, type) => {
        let editor = this.getEditorFromEditorArray();
        if(editor === null) {
            editor = editorContext;
        }
        if (!editor.state.events.hasOwnProperty(type)) return;
        if(arguments.length > 2) {
            const args = arguments.slice(2);
            this.forEach(editor.state.events[type], function applyListener(listener) {
                listener.apply(editor, args);
            });
        } else {
            this.forEach(editor.state.events[type], function applyListener(listener) {
                listener.apply(editor, null);
            });
        }
        this.replaceEditorArrayItem(editor);
    };

    removeListener = (target, type, listener) => {
        const editor = this.getEditorFromEditorArray();
        let events = editor.state.events[type];
        if (!events) {
            const _index = editor.state.eventTargets.indexOf(target);
            if (_index >= 0) events = editor.state.eventsCache[_index][type];
        }
        if (!events) return editor;
        const index = events.indexOf(listener);
        if (index >= 0) events.splice(index, 1);
        target.removeEventListener(type, listener, false);
        this.replaceEditorArrayItem(editor);
        return editor;
    };

    removeAllListeners = () => {
        const editor = this.getEditorFromEditorArray();
        this.forEach(editor.state.events, function cutEvent(events) {
            events.length = 0;
        }, false);
        const thisContext = this;
        if (!editor.state.eventsCache) return editor;
        this.forEach(editor.state.eventsCache, (events, index) => {
            const target = thisContext.state.editor.state.eventTargets[index];
            thisContext.forEach(events, (listeners, type) => {
                thisContext.forEach(listeners, (listener) => {
                    target.removeEventListener(type, listener, false);
                }, true);
            }, false);
        }, true);
        editor.state.eventTargets = [];
        editor.state.eventsCache = [];
        this.replaceEditorArrayItem(editor);
        return editor;
    };

    handleDrag = (event) => {
        event.preventDefault();
        event.dataTransfer.dropEffect = 'copy';

        const target = event.target.classList ? event.target : event.target.parentElement;

        // Ensure the class gets removed from anything that had it before

        if (event.type === 'dragover') {
            target.classList.add('medium-editor-dragover');
        }
    };

    handleDrop =  (event) => {
        // Prevent file from opening in the current window
        event.preventDefault();
        event.stopPropagation();
        if (event.dataTransfer.files) {
            Array.prototype.slice.call(event.dataTransfer.files).forEach((file) => {
                if (file.type.match('image')) {
                    this.insertImageFile(file);
                }
            }, this);
        }
        // target.classList.add('medium-editor-dragover');
        event.target.classList.remove('medium-editor-dragover');
    };

    isAllowedFile =  (file)  => {
        return this.state.allowedTypes.some((fileType) => {
            return !!file.type.match(fileType);
        });
    };

    insertImageFile =  (file) => {
        const {dispatch, challengeId, valueType, valueId} = this.props;
        if (typeof FileReader !== 'function') {
            return;
        }
        const fileReader = new FileReader();

        const thisContext = this;
        const fileContext = file;
        fileReader.addEventListener('load', (e) => {
            dispatch(uploadImage(challengeId, 'en', valueId, valueType, '/media/' + this.extractFileExtentsion(fileContext.type) + '/' + file.name,  e.target.result));
            setTimeout(() => {
                thisContext.createImageTag(fileContext);
            }, 400);
        });
        fileReader.readAsDataURL(file);
    };

    checkPlaceholder = (context) => {
        context.state.config.editor.classList[context.isEmpty() ? 'add' : 'remove']('editor-placeholder');
    };

    checkClassNamesForDrag = (context) => {
        context.state.config.editor.classList[context.isEmpty() ? 'add' : 'remove']('medium-editor-dragover');
    };

    checkPlaceholderWithPrevContent = (context) => {
        context.state.config.editor.classList[context.state.prevContent !== '' ? 'add' : 'remove']('editor-placeholder');
    };

    checkPlaceholderWithContent = (context, content) => {
        context.state.config.editor.classList[content === '' || content === undefined ? 'add' : 'remove']('editor-placeholder');
    };

    containsNode = (parent, child) => {
        let nodeChild;
        if (parent === child) return true;
        if(child === null) return false;
        nodeChild = child.parentNode;
        while (nodeChild) {
            if (nodeChild === parent) return true;
            nodeChild = nodeChild.parentNode;
        }
        return false;
    };

    getNode = (byRoot) => {
        let node;
        const editor = this.getEditorFromEditorArray();
        const root = editor.state.config.editor;
        editor.state.range = editor.state.range || editor.getRange();
        node = editor.state.range.commonAncestorContainer;
        if (!node || node === root) return null;
        while (node && (node.nodeType !== 1) && (node.parentNode !== root)) node = node.parentNode;
        while (node && byRoot && (node.parentNode !== root)) node = node.parentNode;
        this.replaceEditorArrayItem(editor);
        return this.containsNode(root, node) ? node : null;
    };

    effectNode = (el, returnAsNodeName) => {
        const editor = this.getEditorFromEditorArray();
        const nodes = [];
        let element = el;
        element = element || editor.state.config.editor;
        while (element && element !== editor.state.config.editor) {
            if (element.nodeName.match(this.state.effectNodeRegex)) {
                nodes.push(returnAsNodeName ? element.nodeName.toLowerCase() : element);
            }
            element = element.parentNode;
        }
        this.replaceEditorArrayItem(editor);
        return nodes;
    };

    lineBreak = (empty) => {
        const editor = this.getEditorFromEditorArray();
        const range = editor.state.range = editor.getRange();
        const node = document.createElement('p');
        if (empty) editor.state.config.editor.innerHTML = '';
        node.innerHTML = '<br>';
        range.insertNode(node);
        this.replaceEditorArrayItem(editor);
        this.focusNode(node.childNodes[0], range);
    };

    focusNode = (node, range) => {
        const editor = this.getEditorFromEditorArray();
        range.setStartAfter(node);
        range.setEndBefore(node);
        range.collapse(false);
        editor.setRange(range);
        this.replaceEditorArrayItem(editor);
    };

    createAutoLink = (node) => {
        const thisContext = this;
        if (node.nodeType === 1) {
            if (this.state.autoLinkRegex.notLink.test(node.tagName)) return;
            this.forEach(node.childNodes, (child) => {
                thisContext.state.autoLinkRegex(child);
            }, true);
        } else if (node.nodeType === 3) {
            const result = this.urlToLink(node.nodeValue || '');
            if (!result.links) return;
            const fragment = document.createDocumentFragment();
            const div = document.createElement('div');
            div.innerHTML = result.text;
            while (div.childNodes.length) fragment.appendChild(div.childNodes[0]);
            node.parentNode.replaceChild(fragment, node);
        }
    };

    createImageTag = (file) => {
        const editor = this.getEditorFromEditorArray();
        const range = editor.state.range = editor.getRange();
        const node = document.createElement('p');
        node.innerHTML = this.fileToImageTag(file);
        range.insertNode(node);
        this.replaceEditorArrayItem(editor);
        this.focusNode(node.childNodes[0], range);
    };

    urlToLink = (str) => {
        const context = this;
        let count = 0;
        const urlString = str.replace(this.state.autoLinkRegex.url, (url) => {
            let realUrl = url;
            let displayUrl = url;
            count++;
            if (url.length > context.state.autoLinkRegex.maxLength) displayUrl = url.slice(0, context.state.autoLinkRegex.maxLength) + '...';
            if (!context.state.autoLinkRegex.prefix.test(realUrl)) realUrl = 'http://' + realUrl;
            return '<a href="' + realUrl + '">' + displayUrl + '</a>';
        });
        return {links: count, text: urlString};
    };

    extractFileExtentsion = (fileType)  => {
        return fileType.replace('image/', '');
    };

    extractFileName = (file) => {
        const index = file.name.indexOf('.');
        return file.name.substr(0, index);
    };

    fileToImageTag = (file) => {
        return '<img src="/api/media/' + this.extractFileExtentsion(file.type) + '/' + file.name + '" alt="' + this.extractFileName(file) + '"/>';
    };

    toggleNode = (node, hide) => {
        node.style.display = hide ? 'none' : 'block';
    };

    createEditor = (config, initEditor, oldEditor, content) => {
        let markdownEditor;
        if(initEditor) {
            markdownEditor = new Editor(this);
        } else {
            markdownEditor = oldEditor;
        }
        if (!config) throw new Error('Can\'t find config');
        markdownEditor.state.debugMode = config.debug;

        const defaults = this.merge(config);

        const editor = defaults.editor;

        if (!editor || editor.nodeType !== 1) throw new Error('Can\'t find editor');

        editor.classList.add(defaults.class);

        editor.setAttribute('contenteditable', 'true');

        markdownEditor.state.config = defaults;

        if (defaults.placeholder) editor.setAttribute('data-placeholder', defaults.placeholder);
        if(initEditor && (content === '' || content === undefined)) {
            this.checkPlaceholder(markdownEditor);
        } else{
            if(content !== '' && content !== undefined) {
                this.checkPlaceholderWithContent(markdownEditor, content);
            } else {
                this.checkPlaceholderWithPrevContent(markdownEditor);
            }
            this.checkPlaceholderWithPrevContent(markdownEditor);
        }

        markdownEditor.state.selection = this.state.selection;

        markdownEditor.state.events = {change: []};

        this.initializeToolbar(markdownEditor);

        this.initializeEvents(markdownEditor);

        if(initEditor && (content === '' || content === undefined)) {
            markdownEditor.state.prevContent = markdownEditor.getEditorContent();
        } else if(initEditor && content !== '' && content !== undefined) {
            markdownEditor.setEditorContent(transformToHtml(content));
        } else {
            markdownEditor.setEditorContent(markdownEditor.state.prevContent);
        }

        if (markdownEditor.state.markdown) markdownEditor.state.markdown.registerKeyPress(markdownEditor);

        if (markdownEditor.state.config.stay) this.stay(markdownEditor.state.config);

        if(markdownEditor.state.config.input) {
            markdownEditor.addOnSubmitListener(markdownEditor.state.config.input);
        }

        return markdownEditor;
    };

    handleCompress = () => {
        const{ dispatch, dataId } = this.props;
        if(!dataId.includes('abstract') && !dataId.includes('solution')) {
            dispatch(setShowToSectionReferences(dataId, false));
        }
    };

    handleExpandClick = (event) => {
        const{ dispatch, dataId } = this.props;
        event.preventDefault();
        this.connectToWebsocket();
        if(!dataId.includes('abstract') && !dataId.includes('solution')) {
            dispatch(setShowToSectionReferences(dataId, true));
        }
    };

    finishButtonClick = (event) => {
        const{ dispatch, nextEditorStep, dataId } = this.props;
        event.preventDefault();
        this.closeConn();
        if(dataId.includes('abstract') || dataId.includes('solution')) {
            dispatch(setActualEditorStep(nextEditorStep, false));
        } else {
            this.handleCompress();
        }
    };

    render = () => {
        const {dataId, dispatch, show} = this.props;
        let showable = show;
        if(showable === undefined) {
            showable = true;
        }
        const divHeight = 460;
        const dragSymbolHeight = divHeight / 2 + 5;
        /* let order = undefined;
        if(dataId.indexOf('_') > -1) {
            order = dataId.substr(dataId.indexOf('_') + 1).replace(/\D/g, '');
        } else if(dataId.includes('section')) {
            order = dataId.replace(/\D/g, '');
        } */
        return (
            <div>
                {showable &&
                <Segment raised style={{clear: 'both'}}>
                    {(dataId !== 'abstract' && dataId !== 'solution') &&
                    <span className="drag-handle" style={{
                        marginRight: '2%',
                        marginTop: dragSymbolHeight,
                        marginBottom: dragSymbolHeight,
                        fontSize: 20,
                        fontFamily: 'Sans-Serif',
                        fontWeight: 'bold',
                        color: '#5F9EDF',
                        display: 'inline-block',
                        float: 'left',
                        cursor: 'move'
                    }}>☰</span>
                    }
                    {dataId.includes('hint') &&
                    <div style={{
                        marginRight: '2%',
                        display: 'inline-block',
                        float: 'left'
                    }}>
                        <SmallCustomIcon icon={'hintAdd'}/>
                    </div>
                    }
                    {dataId.includes('instruction') &&
                    <div style={{
                        display: 'inline-block',
                        float: 'left'
                    }}>
                        <SmallCustomIcon icon={'instructionAdd'}/>
                    </div>
                    }
                    {(dataId === 'abstract' || dataId === 'solution') &&
                    <div data-toggle={dataId} data-placeholder="im a placeholder" style={{borderLeftWidth: 'thin', borderLeftStyle: 'dashed', borderLeftColor: '#b9b9b9', borderTopWidth: 'thin', borderTopStyle: 'dashed', borderTopColor: '#b9b9b9', padding: 10, float: 'left', overflowX: 'auto', height: divHeight, width: '100%'}}>
                    </div>
                    }
                    {(dataId !== 'abstract' && dataId !== 'solution') &&
                    <div data-toggle={dataId} data-placeholder="im a placeholder" style={{borderLeftWidth: 'thin', borderLeftStyle: 'dashed', borderLeftColor: '#b9b9b9', borderTopWidth: 'thin', borderTopStyle: 'dashed', borderTopColor: '#b9b9b9', padding: 10, float: 'left', overflowX: 'auto', height: divHeight, width: '89%'}}>
                    </div>
                    }
                    {(dataId !== 'abstract' && dataId !== 'solution') &&
                    <span id={dataId + 'Remove'} style={{
                        fontSize: 20,
                        fontFamily: 'Sans-Serif',
                        fontWeight: 'bold',
                        color: 'black',
                        display: 'inline-block',
                        float: 'left',
                        cursor: 'pointer'
                    }}>&#x2716;</span>
                    }
                    <Button primary fluid onClick={(event) => this.finishButtonClick(event)}>Finish</Button>
                </Segment>
                }
                {!showable &&
                <Segment raised style={{float: 'left', width: '100vh'}}>
                    <div style={{float: 'left', width: '95vh'}}>
                        <TooltipButton dataId={dataId} buttonStyle={'icon'} isCustomIcon iconStyle={'expand'}
                                       id={'expand' + dataId + 'Button'} dispatch={dispatch} type={'expand'}
                                       showText={''} tooltip={'Expand'}
                                       handleClick={this.handleExpandClick} divStyle={{float: 'left'}}/>
                        <h2 style={{display: 'inline-block', float: 'right'}}>{dataId.substr(dataId.indexOf('_') + 1)}</h2>
                    </div>
                </Segment>
                }
            </div>);
    }
}

MarkdownEditorComponent.propTypes = {
    isAuthenticated: PropTypes.bool.isRequired,
    dispatch: PropTypes.func.isRequired,
    removeEditor: PropTypes.func,
    errorMessage: PropTypes.string,
    valueType: PropTypes.string,
    valueId: PropTypes.string,
    sectionId: PropTypes.string,
    challengeId: PropTypes.string,
    nextEditorStep: PropTypes.string,
    dataId: PropTypes.string,
    editorArray: PropTypes.array,
    content: PropTypes.string,
    show: PropTypes.bool
};
