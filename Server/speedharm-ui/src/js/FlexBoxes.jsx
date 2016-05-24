var React = require('react');
var ReactDOM = require('react-dom');


var FlexItem = React.createClass({
    displayName: 'FlexItem',

    render: function() {
        return <div className="flex-item-wrapper">
                    <div className="flex-item-title">{this.props.title}</div>
                    <div className="flex-item-body">{this.props.content}</div>
                </div>
    }
});

var FlexGroup = React.createClass({
    displayName: 'FlexGroup',

    render: function () {
        return <div className="flexgroup">
            <div className="flex-group-header"></div>
            <div className="flex-group-content">
                {
                    this.props.items.map(function (elem, i) {
                        return <FlexItem title={elem.title} content={elem.content} key={i}/>
                    })
                }
            </div>
        </div>
    }
});

var FlexContainer = React.createClass({
    displayName: 'Flex Container',

    getInitialState: function() {
        return {
            groups: []
        };
    },

    render: function () {
        return <div className="flexwrapper">
            {
                this.state.groups.map(function (group, i) {
                    return <FlexGroup items={group} key={i}/>
                })
            }
        </div>
    }
});

module.exports = FlexContainer;