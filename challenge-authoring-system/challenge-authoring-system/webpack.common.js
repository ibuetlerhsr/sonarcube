'use strict';

const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const Dotenv = require('dotenv-webpack');

const query={
    mozjpg: {
        progressive: true,
    },
    gifsicle:{
        interlaced: false,
    },
    optipng:{
        optimizationLevel: 4,
    },
    pngquant:{
        quality: '75-90',
        speed: 3,
    },
};

const svgoConfig = JSON.stringify({
    plugins: [
        {removeTitle: true},
        {convertColors: {shorthex: false}},
        {convertPathData: false}
    ]
});

module.exports = {
    resolve: {
        extensions: ['', '.js'],
        alias: {
            'load-image': 'blueimp-load-image/js/load-image.js',
            'load-image-meta': 'blueimp-load-image/js/load-image-meta.js',
            'load-image-exif': 'blueimp-load-image/js/load-image-exif.js',
            'canvas-to-blob': 'blueimp-canvas-to-blob/js/canvas-to-blob.js',
            'jquery-ui/widget': 'blueimp-file-upload/js/vendor/jquery.ui.widget.js',
            'load-image-scale': 'blueimp-load-image/js/load-image-scale.js',
            'jquery.ui.widget': 'node_modules/jquery.ui.widget/jquery.ui.widget.js',
            'jquery-ui':'jquery-ui/ui'
        },
    },
    plugins: [
        new CleanWebpackPlugin(['dist']),
        new HtmlWebpackPlugin({
            title: 'Production',
            template: 'app/index.tpl.html',
            inject: 'body',
            filename: 'index.html'
        }),
        new webpack.optimize.OccurrenceOrderPlugin(),
        new webpack.ProvidePlugin({
            $: "jquery",
            jQuery: "jquery"
        }),
        new Dotenv({
            systemvars: true
        })
    ],
    output: {
        path: path.join(__dirname, '/dist/'),
        filename: '[name].js',
        publicPath: '/'
    },
    module: {
        preLoaders: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                loader: 'eslint'
            }
        ],
        loaders: [
            {
                test: /\.js?$/,
                exclude: /node_modules/,
                loader: 'babel'
            },
            {
                test: /\.json?$/,
                loader: 'json'
            },
            {
                test: /\.scss$/,
                loader: 'style!css?modules&localIdentName=[name]---[local]---[hash:base64:5]!sass'
            },
            {
                test: /\.woff(2)?(\?[a-z0-9#=&.]+)?$/,
                loader: 'url?limit=10000&mimetype=application/font-woff' },
            {
                test: /\.(ttf|eot)(\?[a-z0-9#=&.]+)?$/,
                loader: 'file' },
            {
                test: /\.css$/,
                loader: 'style-loader!css-loader'
            },
            {
                test: /\.svg(\?[a-z0-9#=&.]+)?$/,
                loaders: [
                    'file',
                    'svgo-loader?' + svgoConfig
                ]
            },
            {
                test: /\.(jpe?g|png|gif)$/i,
                loaders: [
                    'file?hash=sha512&digest=hex&name=[hash].[ext]',
                    `image-webpack-loader?${JSON.stringify(query)}`
                ],
            }
        ]
    },
    postcss: [
        require('autoprefixer')
    ]
};
