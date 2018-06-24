const webpack = require('webpack');
const WebpackDevServer = require('webpack-dev-server');
const config = require('./webpack.dev');
const bodyParser = require('body-parser');
const cors = require('cors');
const store = require('store');

const HOST = process.env.HOST || 'localhost';
const PORT = process.env.PORT || 3001;

const SERVER_ADDRESS = process.env.ADDRESS_CAS_SERVER || 'localhost';
const SERVER_PORT = process.env.SERVER_PORT || 9000;
const SERVER_PROTOCOL = process.env.SERVER_PROTOCOL || 'http';
const LOGIN_BASE_URL = SERVER_PROTOCOL + '://' + SERVER_ADDRESS + ':' + SERVER_PORT + '/api/authorization';

new WebpackDevServer(webpack(config), {
    publicPath: config.output.publicPath,
    hot: true,
    historyApiFallback: true,
    disableHostCheck: true,
    // It suppress error shown in console, so it has to be set to false.
    quiet: false,
    // It suppress everything except error, so it has to be set to false as well
    // to see success build.
    noInfo: false,
    stats: {
        // Config for minimal console.log mess.
        assets: false,
        colors: true,
        version: false,
        hash: false,
        timings: false,
        chunks: false,
        chunkModules: false
    },
    setup: (app) => {
        app.use(bodyParser.json());
        app.use(bodyParser.urlencoded({
            extended: true
        }));

        app.use(cors());
        app.get('/getJWT', function(req, res) {
            const casGui = req.get('CAS-GUI');
            if(casGui === undefined) {
                res.send('failed');
            } else {
                const token = store.get('authToken');
                const consumerId = store.get('consumerId');
                const challengeId = store.get('challengeId');
                const targetLanguage = store.get('targetLanguage');
                const jsonData = {};
                jsonData.token = token;
                jsonData.consumerId = consumerId;
                jsonData.challengeId = challengeId;
                jsonData.targetLanguage = targetLanguage;
                res.json(jsonData);
            }
        });

        app.get('/deleteJWT', function(req, res) {
            const casGui = req.get('CAS-GUI');
            if(casGui === undefined) {
                res.send('failed');
            } else {
                store.remove('authToken');
                store.remove('consumerId');
                store.remove('challengeId');
                store.remove('targetLanguage');
                res.send('deleted');
            }
        });

        app.post('/postJWT', bodyParser.json(), function(req, res) {
            const object = req.body;
            let consumerId = req.get('Consumer-ID');
            if(consumerId === undefined || consumerId === null) {
                consumerId = object.consumerId;
            }
            let challengeId = req.get('Challenge-ID');
            if(challengeId === undefined || challengeId === null) {
                challengeId = object.challengeId;
            }
            const authToken = object.jwt || object.token;
            const XMLHttpRequest = require('xhr2');
            const xhr = new XMLHttpRequest();
            const jsonData = {};
            jsonData.token = authToken;
            jsonData.challengeId = challengeId;
            xhr.open('POST', LOGIN_BASE_URL, true);
            xhr.setRequestHeader('Content-type', 'application/json');
            xhr.setRequestHeader('Consumer-ID', consumerId);
            if(challengeId !== undefined) {
                xhr.setRequestHeader('Challenge-ID', challengeId);
            }

            xhr.onreadystatechange = function() {
                if(xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
                    store.set('authToken', authToken);
                    store.set('consumerId', consumerId);
                    if(challengeId !== undefined) {
                        store.set('challengeId', challengeId);
                    } else {
                        store.set('challengeId', '');
                    }
                    res.send('POST res sent from webpack dev server');
                }
            };

            xhr.send(JSON.stringify(jsonData));
        });

        app.post('/translate', bodyParser.json(), function(req) {
            const object = req.body;
            let challengeId = req.get('Challenge-ID');
            if(challengeId === undefined || challengeId === null) {
                challengeId = object.challengeId;
            }
            let targetLanguage = req.get('Target-Language');
            if(targetLanguage === undefined || targetLanguage === null) {
                targetLanguage = object.targetLanguage;
            }
            const authToken = object.jwt || object.token;
            store.set('targetLanguage', targetLanguage);
            store.set('authToken', authToken);
            if(challengeId !== undefined) {
                store.set('challengeId', challengeId);
            } else {
                store.set('challengeId', '');
            }
        });
    }
}).listen(PORT, HOST, (err) => {
    if (err) {
        console.log(err);
    }

    console.log('Listening at ' + HOST + ':' + PORT);
});
