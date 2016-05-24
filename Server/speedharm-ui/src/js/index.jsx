'use strict';

var React = require('react');
var ReactDOM = require('react-dom');
var FlexContainer = require('./FlexBoxes');

document.addEventListener("DOMContentLoaded", function(event) {
    var element = ReactDOM.render(<FlexContainer />, document.getElementById('react-mount-point'));
    element.setState({
        groups: [
            [{title: "Hello World", content: "Test post 1"}, {title: "Hola mundo!", content: "Test post 2"}],
            [{title: "Hello World", content: "Test post 3"}]
        ]
    });
});