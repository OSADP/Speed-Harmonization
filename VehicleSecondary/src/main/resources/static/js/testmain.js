/**
 * testmain - functions for unit testing the HMI
 */

//---FOR TESTING ONLY!

var test1Count = 0;
var test2Count = 0;
var test3Count = 0;
var test4Count = 0;
var test5Count = 0;

//case 1 - verify correct display of own speed at different values
function unitTest1() {
    if (test1Count == 0) {
        testSpeed = 18;
    }else if (test1Count == 1) {
        testSpeed = 0;
    }else if (test1Count == 2) {
        testSpeed = 40;
    }else {
        testSpeed = 71;
    }
    ++test1Count;
    if (test1Count > 3) test1Count = 0;
    displayOwnSpeed(testSpeed);
}

//case 2 - verify correct display of the commanded speed & confidence widget
function unitTest2() {
    if (test2Count == 0) {
        testCmd = 0;
        testConf = 0;
    }else if (test2Count == 1) {
        testCmd = 25;
        testConf = 99;
    }else if (test2Count == 2) {
        testCmd = 25;
        testConf = 81;
    }else if (test2Count == 3) {
        testCmd = 49;
        testConf = 81;
    }else if (test2Count == 4) {
        testCmd = 38;
        testConf = 50;
    }else if (test2Count == 5) {
        testCmd = 37;
        testConf = 49;
    }else if (test2Count == 6) {
        testCmd = 36;
        testConf = 24;
    }
    ++test2Count;
    if (test2Count > 6) test2Count = 0;

    displaySpeedCmd(testCmd, testConf);
}

//case 3 - own vehicle name
function unitTest3() {
    if (test3Count == 0) {
        testName = "";
    }else if (test3Count == 1) {
        testName = "Black";
    }else if (test3Count == 2) {
        testName = "WEIRD-LONG-NAME";
    }else if (test3Count == 3) {
        testName = "13 chars long";
    }
    ++test3Count;
    if (test3Count > 3) test3Count = 0;

    setOwnName(testName);
}

//case 4 - own vehicle statuses
function unitTest4() {
    if (test4Count == 0) {
        conn = false;
        auto = 0;
    }else if (test4Count == 1) {
        conn = true;
        auto = 2;
    }else if (test4Count == 2) {
        conn = false;
        auto = 1;
    }else if (test4Count == 3) {
        conn = true;
        auto = 1;
    }
    ++test4Count;
    if (test4Count > 3) test4Count = 0;

    setOwnServerConnection(conn);
    setOwnAutomation(auto);
}

//case 5 - other vehicle names & states
function unitTest5() {
    name1 = "";
    name2 = "";
    name3 = "";
    name4 = "";
    auto1 = 0;
    auto2 = 0;
    auto3 = 0;
    auto4 = 0;

    if (test5Count == 0) {
        name1 = "Yellow";
        auto1 = 1;
    }else if (test5Count == 1) {
        name1 = "Yellow";
        name2 = "Purple";
        auto1 = 1;
    }else if (test5Count == 2) {
        name1 = "Yellow";
        name2 = "Purple";
        name3 = "Hideous";
        auto2 = 1;
        auto3 = 2;
    }else if (test5Count == 3) {
        name1 = "Yellow";
        name2 = "Violet";
        name3 = "Worse";
        name4 = "Mauve";
        auto1 = 1;
        auto2 = 1;
        auto4 = 1;
    }else if (test5Count == 4) {
        name1 = "Blue";
        name2 = "Orange";
    }else if (test5Count == 5) {
        name1 = "Blue";
        name2 = "Orange";
        auto1 = 1;
        auto2 = 1;
        auto3 = 2;
        auto4 = 1;
    }
    ++test5Count;
    if (test5Count > 5) test5Count = 0;

    setVehicle1Name(name1);
    setVehicle2Name(name2);
    setVehicle3Name(name3);
    setVehicle4Name(name4);
    setVehicle1Automation(auto1);
    setVehicle2Automation(auto2);
    setVehicle3Automation(auto3);
    setVehicle4Automation(auto4);
}

function unitTest6() {
    displayVersionId("Speed Harm unit test 8395");
}