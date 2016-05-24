/**
 * projectAjax - module to pass information from the UI back to the server.
 *
 * This code is currently unused in the Speed Harm project, but is left here
 * as this project will serve as a template for future projects.
 */

function _projectAjax() {}

var projectAjax = new _projectAjax();

_projectAjax.prototype.vehicleRole = 1;

/**
 * setParameters ajax
 * Grabs commands from the user that need to go back to the server.
 */
_projectAjax.prototype.ajaxSetParameters = function (arg1) {
    "use strict";

    var request = $.ajax({
        url : "setParameters",
        dataType : "json",
        type : "post",
        data : {
            arg1 : arg1,
        }
    });

    request.done(function(response, textStatus, jqXHR) {
    });

    request.fail(function(jqXHR, textStatus, errorThrown) {
        var prefix = "An error occurred setting project parameters: ";
        var statusMessage = prefix.concat(textStatus);
        //alert(statusMessage);
    });

}

/**
 * logUiEvent ajax
 * passes an event description string to the server for logging.
 */
_projectAjax.prototype.ajaxLogEvent = function(eventDescrip) {
    "use strict";

    var request = $.ajax({
        url : "logUiEvent",
        dataType : "json",
        type : "post",
        data : { eventDescrip : eventDescrip }
    });

    request.done(function(response, textStatus, jqXHR) {
    });

    request.fail(function(jqXHR, textStatus, errorThrown) {
        var prefix = "Error sending UI event logging info: ";
        var statusMessage = prefix.concat(textStatus);
    });
}