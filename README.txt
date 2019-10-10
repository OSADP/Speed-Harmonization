Open Source Overview
============================
Speed Harmonization
Version 1.0.1

Description:
The Speed Harm software is composed of three components.
  * the control software (version 1.1) is written in Simulink and runs on the vehicle's MicroAutobox II (MAB).
  * the DVI software (version 1.0) is written in Java and runs on the vehicle's secondary computer, which is an Ubuntu Linux PC.
  * the server software (version 1.0.1) is written in Java and runs on an independent server intended to simulate a TMC facility.

The two vehicle components set on top of the reusable platform software in the TFHRC's CARMA fleet of Cadillac SRXs (CARMA software). 
Together with the CARMA MAB software, the MAB component measures several vehicle state parameters and reports them to the secondary computer.  It also takes in speed commands from the secondary computer and controls the vehicle to smoothly achieve the current speed command.

Together with the CARMA secondary softwqre, this secondary computer software receives state info from the MAB and passes it to the server; it receives updated speed commands from the server and passes these to the MAB. It passes pertinent pieces of both of these data streams on to the UI tablet, for which it acts as a web server.  It also receives position data from the vehicle's Pinpoint device.

The server component reads infrastructure data from point detectors stationed along the experimental drive route, and takes in state data from any number of vehicles involved in the experiment. It feeds all of this input data to one or more attached vehicle speed algorithms, which calculate the desired speed command for each vehicle. It then provides these speed commands to each vehicle's secondary software.  This version of the server is configured with one algorithm, the shooting heuristic, version 1.0.


Installation and removal instructions
-------------------------------------
Microautobox:  this is a complicated build & install process, which involves the software from the CARMA library as well.  It is described in the document "Speed Harm MAB Software Installation Instructions.docx" in the MAB/docs directory.  Note that it is intended to work with version 2.0 of the CARMA MAB software.

Secondary computer:  the secondary software runs on Java 1.8, which needs to be installed first. All of its functionality and resources are packaged in a single jar file, which needs to be installed in a directory named /opt/speedharm.  It will also need a directory named /opt/speedharm/logs.  To build this jar file, the developer must first add the CarmaSecondary 2.0 library to the local Maven repository, then execute a maven clean install using the project files from this repository; doing so will include the CarmaSecondary library into the final jar file.

Server:  server installation is described in the document "Speed Harmonization Server Installation Guide.docx" in the Server directory.


License information
-------------------
See the accompanying LICENSE file.


System Requirements
-------------------------
Microautobox: dSpace Microautobox II computer

Secondary processor:  
Minimum memory:  2 GB
Processing power:  Intel Core I3 @ 1.6 GHz or equivalent
Connectivity:  ethernet
Operating systems supported:  Ubuntu 14.04

Server:
Minimum memory:  2 GB
Processing power:  Intel Core I3 @ 1.6 GHz or equivalent
Connectivity:  ethernet
Operating systems supported:  Windows 7 or Windows Server 2008

