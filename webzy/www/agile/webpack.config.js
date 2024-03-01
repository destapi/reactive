const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyPlugin = require("copy-webpack-plugin");

module.exports = (env) => {
    console.log(env)
    return {
        entry: './src/index.js',
        output: {
            filename: 'main.js',
            path: path.resolve(__dirname, 'dist'),
            clean: true,
        },
        mode: 'development',
        devtool: 'inline-source-map',
        devServer: {
            static: './dist',
        },
        plugins: [
            new CopyPlugin({
                patterns: [
                    {from: "public/favicon.ico", to: "favicon.ico"},
                    {from: "public/fa", to: "fa"},
                    {from: "public/reset.css", to: "reset.css"},
                    {from: "public/scrum.css", to: "scrum.css"},
                ],
            }),
            new HtmlWebpackPlugin({
                title: 'Agile Developer',
                template: 'public/index.html'
            }),
        ],
        module: {
            rules: [
                {
                    test: /\.riot$/,
                    exclude: /node_modules/,
                    use: [
                        {
                            loader: '@riotjs/webpack-loader',
                            options: {
                                hot: false, // set it to true if you are using hmr
                                // add here all the other @riotjs/compiler options riot.js.org/compiler
                                // template: 'pug' for example
                            },
                        },
                    ],
                },
            ],
        },
    }
}