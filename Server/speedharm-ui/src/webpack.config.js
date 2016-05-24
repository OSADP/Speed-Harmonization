// Webpack.JS Config File
module.exports = {
    entry: {
        index: "./js/index.jsx",
    },
    output: {
        // Make sure to use [name] or [id] in output.filename
        //  when using multiple entry points
        filename: "../build/js/[name].bundle.js",
        chunkFilename: "../build/js/[id].bundle.js"
    },
    module: {
        loaders: [
            {
                test: /\.jsx?$/,
                exclude: /(node_modules|bower_components)/,
                loader: 'babel', // 'babel-loader' is also a legal name to reference
                query: {
                    presets: ['react', 'es2015']
                }
            }
        ]
    },
    resolve: {
      extensions: ['', '.js', '.jsx']
    }
};