/**
 * projectmain - Library of functions for the HMI
 */

//ensure this function will be called after the HTML page is loaded
window.addEventListener('load', initialize);

var canvas;                     //the drawing canvas object
var context;                    //the drawing canvas's context

//all of the geometry here is based on use with the Carma tablets using an html canvas
//with dimensions 1280 x 772 pixels.

var speedScaleX = 220;          //constant x left side of the speed scale image display, pixels
var speedScaleY = 100;          //constant y top edge of the speed scale image display, pixels
var currentSpeed = 0;           //own vehicle's current speed, mph
var currentCmd = 0;             //own vehicle's current speed command, mph
var confidence = 0;             //command confidence level, percent (ranges from 0-100)
var speedometerY = speedScaleY+40; //constant y offset for the own speed rectangle, pixels
var speed0x = speedScaleX+29;   //constant representing the left edge of the speed scale (pixels from left edge)
                                // must be > commandWidth/2

var speedometerHeight = 15;     //constant height of the own-speed rectangle, pixels
var pixelsPerMph = 11.1;        //constant scale factor for moving the speed indicators
var commandY = speedScaleY+65;  //constant y offset for the speed command indicator, pixels
var commandWidth = 70;          //constant width of the command indicator, pixels (should always be even)
var confidenceHeight = 30;      //constant height of the confidence indicator (attached to speed command), pixels
var confXOffset = 20;           //constant x offset of the confidence number inside the speed command indicator, pixels
var confYOffset = 24;           //constant y offset of the confidence number inside the speed command indicator, pixels
var bgndR = 23;                 //constant decimal value of red color in the background (0x17)
var bgndG = 55;                 //constant decimal value of green color in the background (0x37)
var bgndB = 94;                 //constant decimal value of blue color in the background (0x5e)

//geometry of lower half of the screen; all units in pixels
var line1Y = 480;               //constant y offset of the first line of text
var ownLabelX = 120;            //constant x offset of the labels for the own vehicle info
var otherHeadlineX = 770;       //constant x offset of the headline for the other vehicle info
var otherLabelLeftX = ownLabelX+560; //constant x offset of the other vehicle left-hand column of labels
var otherLabelRightX = otherLabelLeftX+300; //constant x offset of the other vehicle right-hand column of labels
var line2Y = line1Y+70;         //constant y offset of the second line of text
var line3Y = line2Y+60;         //constant y offset of the third line of text
var ownNameX = ownLabelX+180;   //constant x offset of the own vehicle's name
var ownConnectionX = ownLabelX+200; //constant x center of own vehicle connection indicator
var ownConnectionY = line2Y-10; //y center of own vehicle connection indicator
var ownAutomationX = ownConnectionX //x center of own vehicle automation indicator
var ownAutomationY = line3Y-10; //y center of own vehicle automation indicator
var ownNameWidth = otherHeadlineX - ownNameX; //width of the own vehicle name field, pixels
var ownCircleRadius = 25;       //constant radius of the indicator circles for own vehicle
var otherCircleRadius = 16;     //radius of indicators for other vehicles
var otherIndicatorXOffset = 150; //constant x offset of other vehicle indicator center from the left edge of its name
var vehicle1AutoX = otherLabelLeftX+otherIndicatorXOffset; //x center of the vehicle 1 automation indicator
var vehicle1AutoY = ownConnectionY; //y center of the vehicle 1 automation indicator
var vehicle2AutoX = vehicle1AutoX; //x center of the vehicle 2 automation indicator
var vehicle2AutoY = ownAutomationY; //y center of the vehicle 2 automation indicator
var vehicle3AutoX = otherLabelRightX+otherIndicatorXOffset; //x center of the vehicle 3 automation indicator
var vehicle3AutoY = vehicle1AutoY; //y center of the vehicle 3 automation indicator
var vehicle4AutoX = vehicle3AutoX; //x center of the vehicle 4 automation indicator
var vehicle4AutoY = vehicle2AutoY; //y center of the vehicle 4 automation indicator
var otherNameWidth = vehicle1AutoX - otherLabelLeftX - otherCircleRadius; //width of other vehicle name fields, pixels

//geometry of version ID display
var versionX = 570;             //constant x offset of the version ID string (Y is set just above canvas bottom)

//sets up the drawing canvas and displays the elements in the background.
//this needs to be the first function called, so it is in the window load function.
function initialize() {
    //set up the canvas and background image
    canvas = document.getElementById("theCanvas");
    context = canvas.getContext("2d");
    displayOwnSpeed(0);

    //display the static text labels for the vehicle states
    context.beginPath();
    context.font = "30px Arial";
    context.fillStyle = "#ffffff";
    context.fillText("This vehicle (", ownLabelX, line1Y);
    context.fillText("Cooperating vehicles", otherHeadlineX, line1Y);

    context.font = "24px Arial";
    context.fillStyle = "#99ffff";
    context.fillText("Connected", ownLabelX, line2Y);
    context.fillText("Automated", ownLabelX, line3Y);

    context.closePath();

    //display own vehicle indicator default states
    setOwnServerConnection(false);
    setOwnAutomation(0); //manual

    // draw a line to the right and bottom edge of the canvas to help scale the screen display
    // keep this block to use when customizing to fit new hardware
    /*----
    context.beginPath();
    context.fillStyle = "#ff3333";
    var w = canvas.width - 1;
    var h = canvas.height - 1;
    context.moveTo(w, h);
    context.lineTo((w-30), h);
    context.moveTo(w, (h-30));
    context.lineTo(w, h);
    context.lineTo((w-600), (h-100));
    //context.closePath();
    context.strokeStyle = "#aaff55";
    context.stroke();
    ----*/
}


//displays the speed bar for the own vehicle's speed in MPH
function displayOwnSpeed(mph) {
    //display rectangle for current speed
    drawSpeedometer(drawOwnSpeed, mph);

    //store this as the current speed for future reference
    currentSpeed = mph;
}

//ensures the speed scale image is laid down first then the own speed meter is drawn on top of it
function drawSpeedometer(overlay, speed) {
    var img = new Image();
    img.onload = function() {
        context.drawImage(img, speedScaleX, speedScaleY);
        overlay(speed);
    };
    img.src = "images/speedscale.png";
}

//draws the rectangle for the own speed indicator
function drawOwnSpeed(speed) {
    if (speed > 0) {
        context.beginPath();
        var width = speed*pixelsPerMph;
        context.rect(speed0x, speedometerY, width, speedometerHeight);
        context.fillStyle = "#0050ff";
        context.fill();
    }
}


//move the speed command widget to the appropriate location on the spedometer scale
// and update the command confidence display
//conf should be an integer percentage (between 0 and 100)
function displaySpeedCmd(newCmdMph, conf) {
    //compute geometric params
    var cmdX = currentCmd*pixelsPerMph + speed0x; //centerline of the indicator in pixels
    var indicatorLeft = cmdX - 0.5*commandWidth;
    var indicatorRight = cmdX + 0.5*commandWidth;

    //erase the existing command widget
    var rectHeight = commandWidth + confidenceHeight;
    context.beginPath();
    context.clearRect(indicatorLeft-1, commandY-1, commandWidth+2, rectHeight+2);

    //display the widget at the new command location
    cmdX = newCmdMph*pixelsPerMph + speed0x;
    indicatorLeft = cmdX - 0.5*commandWidth;
    indicatorRight = cmdX + 0.5*commandWidth;
    var triangleBottom = commandY + commandWidth;
    context.beginPath();
    context.moveTo(cmdX, commandY);     //begin the triangle
    context.lineTo(indicatorRight, triangleBottom);
    context.lineTo(indicatorLeft, triangleBottom);
    context.lineTo(cmdX, commandY);
    var intensityRed = 0.01*(255-bgndR)*conf + bgndR; //compute color fill of the triangle; goes from bgnd to bright yellow
    var intensityGreen = 0.01*(255-bgndG)*conf + bgndG;
    var intensityBlue = bgndB;
    var irs = decimalToHex2(intensityRed);
    var igs = decimalToHex2(intensityGreen);
    var ibs = decimalToHex2(intensityBlue);
    var intensityStr = "#" + irs + igs + ibs;
    context.fillStyle = intensityStr;
    context.fill();
    context.fillStyle = "#ffff00";      //create yellow rectangle for confidence display
    context.fillRect(indicatorLeft, triangleBottom, commandWidth, confidenceHeight);

    //display the current command confidence value in the widget
    context.beginPath();
    context.font = "20px Arial";
    var confStr = conf.toString() + "%";
    var confX = indicatorLeft + confXOffset;
    var confY = triangleBottom + confYOffset;
    context.fillStyle = "#000000";
    context.fillText(confStr, confX, confY);
    context.closePath();

    //store the new command and confidence as current values for future reference
    currentCmd = newCmdMph;
    confidence = conf;
}

function decimalToHex2(dec) {
    var tmp = Number(dec.toFixed());
    var tmpStr = tmp.toString(16);
    if (dec < 16) {
        tmpStr = "0" + tmpStr;
    }
    return tmpStr;
}

//Next several functions control the display of various vehicle info, and should be self-explanatory.
//Note that in the display layout other vehicles 1, 2, 3 and 4 always occupy the same location, and
//the vehicles that are registered are loaded first-come, first-served, so that there is never a gap
//between them.  Only vehicles registered will be displayed.  It is up to the caller to ensure that
//no gaps exist.  States are either on or off.

function setOwnServerConnection(state) {
    displayConnectIndicator(ownConnectionX, ownConnectionY, ownCircleRadius, state);
}


function setOwnAutomation(state) {
    displayAutoIndicator(ownAutomationX, ownAutomationY, ownCircleRadius, state);
}


function setOwnName(name) {
    //clear the previous name
    context.beginPath();
    context.clearRect(ownNameX, line1Y-24, ownNameWidth, 34);

    //display the name, if not too long
    if (name.length > 12) {
        name = name.substring(0,12);
    }

    context.font = "30px Arial";
    context.fillStyle = "#ffffff";
    context.fillText(name + ")", ownNameX, line1Y);
    context.closePath();
}


function setVehicle1Automation(state) {
    displayAutoIndicator(vehicle1AutoX, vehicle1AutoY, otherCircleRadius, state);
}


function setVehicle2Automation(state) {
    displayAutoIndicator(vehicle2AutoX, vehicle2AutoY, otherCircleRadius, state);
}


function setVehicle3Automation(state) {
    displayAutoIndicator(vehicle3AutoX, vehicle3AutoY, otherCircleRadius, state);
}


function setVehicle4Automation(state) {
    displayAutoIndicator(vehicle4AutoX, vehicle4AutoY, otherCircleRadius, state);
}


function setVehicle1Name(name) {
    displayOtherVehicleName(name, otherLabelLeftX, line2Y);
}


function setVehicle2Name(name) {
    displayOtherVehicleName(name, otherLabelLeftX, line3Y);
}


function setVehicle3Name(name) {
    displayOtherVehicleName(name, otherLabelRightX, line2Y);
}


function setVehicle4Name(name) {
    displayOtherVehicleName(name, otherLabelRightX, line3Y);
}


function displayAutoIndicator(centerX, centerY, radius, state) {
    //display color depending on state (0=manual, 1=automated, 2=auto but ignoring commands)
    context.beginPath();
    if (state == 0) {
        context.fillStyle = "#c0c0e0"; //off-white
    }else if (state == 1) {
        context.fillStyle = "#00ff00"; //green
    }else if (state == 2) {
        context.fillStyle = "#ffff00"; //yellow
    }else {
        context.fillStyle = "#17375e"; //background color
    }
    context.arc(centerX, centerY, radius, 0, 2*Math.PI, true);
    context.fill();
    context.closePath();
}


function displayConnectIndicator(centerX, centerY, radius, connected) {
    context.beginPath();
    if (connected) {
        context.fillStyle = "#00ff00"; //green
    }else {
        context.fillStyle = "#ff0000"; //red
    }
    context.arc(centerX, centerY, radius, 0, 2*Math.PI, true);
    context.fill();
    context.closePath();
}


function displayOtherVehicleName(name, x, y) {
    context.beginPath();
    //clear whatever previous text may have been there
    context.clearRect(x, y-22, otherNameWidth, 32);

    //if the name is not null then
    if (name != null) {
        //display the name
        if (name.length > 12) {
            name = name.substring(0,12);
        }
        context.font = "24px Arial";
        context.fillStyle = "#99ffff";
        context.fillText(name, x, y);
    }
    context.closePath();
}


//displays the identification info of the software
function displayVersionId(id) {
    //display the ID info
    context.beginPath();
    context.font = "14px Arial";
    context.fillStyle = "#888888";
    context.fillText(id, versionX, canvas.height - 14);
    context.closePath();
}

//Experiment state variables that only change if displayUiMessage elects to change them
var vehicleName = "Bogus Name";
var ownSpeed = 0;
var ownAutomation = 0;
var versionId = "VERSION UNKNOWN";
var speedCmd = 0;
var confidence = 0;
var serverConnection = false;
var numOthers = 0;
var v1Name = null;
var v2Name = null;
var v3Name = null;
var v4Name = null;
var v1Auto = 0;
var v2Auto = 0;
var v3Auto = 0;
var v4Auto = 0;

/**
 * Receives a message from the Java server and updates the UI accordingly
 */
function displayUiMessage(uiMessage)   {
    "use strict"

    //determine the message source, which tells us which elements are meaningful
    var source = uiMessage.source;

    //if message source is the main loop then
    if (source == 1) {
        //pull out the vehicle name, own speed and own automation state
        vehicleName = uiMessage.ownName;
        ownSpeed = uiMessage.ownSpeed;
        ownAutomation = uiMessage.ownAuto;
        //display these items
        setOwnName(vehicleName);
        displayOwnSpeed(ownSpeed);
        setOwnAutomation(ownAutomation);

        //pull out the speed command, confidence and connection status
        speedCmd = uiMessage.spdCmd;
        confidence = uiMessage.conf;
        serverConnection = uiMessage.ownConnection;
        //display these items
        displaySpeedCmd(speedCmd, confidence);
        setOwnServerConnection(serverConnection);

        //pull out the other vehicle statuses
        var otherStates = uiMessage.otherStates;
        v1Auto = otherStates & 0x00000003;
        v2Auto = (otherStates >> 2) & 0x00000003;
        v3Auto = (otherStates >> 4) & 0x00000003;
        v4Auto = (otherStates >> 6) & 0x00000003;
        numOthers = uiMessage.numPartners;
        //these names should never contain obsolete entries (e.g. if a vehicle has dropped out), since the
        // number of partners is always kept current by the Java server side
        v1Name = uiMessage.v1Name;
        v2Name = uiMessage.v2Name;
        v3Name = uiMessage.v3Name;
        v4Name = uiMessage.v4Name;
        //display these items, making sure to clear the auto indicators if the vehicle doesn't exist
        setVehicle1Name(v1Name);
        setVehicle2Name(v2Name);
        setVehicle3Name(v3Name);
        setVehicle4Name(v4Name);
        if (numOthers >= 1) {
            setVehicle1Automation(v1Auto);
            if (numOthers >= 2) {
                setVehicle2Automation(v2Auto);
                if (numOthers >= 3) {
                    setVehicle3Automation(v3Auto);
                    if (numOthers >= 4) {
                        setVehicle4Automation(v4Auto);
                    }else {
                        setVehicle4Automation(9); //a value > 2 displays the indicator in background color
                    }
                }else {
                    setVehicle3Automation(9);
                    setVehicle4Automation(9);
                }
            }else {
                setVehicle2Automation(9);
                setVehicle3Automation(9);
                setVehicle4Automation(9);
            }
        }else {
            setVehicle1Automation(9);
            setVehicle2Automation(9);
            setVehicle3Automation(9);
            setVehicle4Automation(9);
        }

    //else if message source is initializer then
    }else if (source == 4) {
        versionId = uiMessage.version;
        displayVersionId(versionId);

    //else (should never happen)
    }else {
        //alert the user
        alert("Illegal message type received from secondary computer: " + source);
    }
}


/**
 * playAndroidSound - plays a sound on the Android device
 */
function playAndroidSound() {
    // doesn't work on many android devices
    //document.getElementById("audioId").play();

    playSound('audio-fix');
}


// functions to enable Android audio during window load & unload

$(window).load(function() {
    audioElements = document.getElementsByTagName('audio');

    for (var i = 0; i < audioElements.length; i++) {
        sounds[audioElements[i].className] = audioElements[i];
    }

    // Solves chrome for andriod issue 178297 Require user gesture
    // https://code.google.com/p/chromium/issues/detail?id=178297
    // Fix based on code from http://blog.foolip.org/2014/02/10/media-playback-restrictions-in-blink/
    if (mediaPlaybackRequiresUserGesture()) {
        window.addEventListener('keydown', removeBehaviorsRestrictions);
        window.addEventListener('mousedown', removeBehaviorsRestrictions);
        window.addEventListener('touchstart', removeBehaviorsRestrictions);
    }

    connect();
});


$(window).unload(function() {
    disconnect();
});
