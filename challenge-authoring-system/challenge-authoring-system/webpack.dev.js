const merge = require('webpack-merge');
const webpack = require('webpack');
const path = require('path');
const common = require('./webpack.common.js');

const SERVER_ADDRESS = process.env.ADDRESS_CAS_CLIENT || 'cas-eu.idocker.hacking-lab.com';
const SERVER_PORT = process.env.SERVER_PORT || 443;
const SERVER_PROTOCOL = process.env.SERVER_PROTOCOL || 'https';
module.exports = merge(common, {
    debug: true,
    entry: [
        'babel-polyfill',
        'webpack-dev-server/client?'+ SERVER_PROTOCOL+'://'+ SERVER_ADDRESS + ':' + SERVER_PORT,
        'webpack/hot/only-dev-server',
        'react-hot-loader/patch',
        path.join(__dirname, 'app/index.js')
    ],
    plugins: [
        new webpack.HotModuleReplacementPlugin(),
        new webpack.NoErrorsPlugin(),
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': JSON.stringify('development')
        }),

    ],
    eslint: {
        configFile: '.eslintrc',
        failOnWarning: false,
        failOnError: false
    },
  devtool: 'inline-source-map',
  devServer: {
    contentBase: './dist'
  }
});
