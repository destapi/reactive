const express = require('express')
const webpack = require('webpack');
const config = require('../webpack.config.js');

const app = express();
const port = 3000;
const compiler = webpack(config);

const webpackDevMiddleware = require('webpack-dev-middleware');
const webpackHotMiddleware = require('webpack-hot-middleware');

app.use(express.static('public'));
app.use(webpackHotMiddleware(compiler));
app.use(webpackDevMiddleware(compiler, {
        publicPath: config.output.publicPath,
    })
);

app.listen(port, () => {
    console.log(`Listening on port ${port}`)
});