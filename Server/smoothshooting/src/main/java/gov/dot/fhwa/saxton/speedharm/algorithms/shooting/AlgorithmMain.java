package gov.dot.fhwa.saxton.speedharm.algorithms.shooting;

import gov.dot.fhwa.saxton.speedharm.algorithms.AbstractAlgorithm;
import gov.dot.fhwa.saxton.speedharm.api.objects.*;
import gov.dot.fhwa.saxton.speedharm.util.milemarkers.MilemarkerCSVLoader;
import gov.dot.fhwa.saxton.speedharm.util.milemarkers.MilemarkerConverter;
import gov.dot.fhwa.saxton.speedharm.util.milemarkers.MilemarkerPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AlgorithmMain extends AbstractAlgorithm {
	
	private Logger log = LogManager.getLogger();
	
	AppConfig config = (AppConfig)AppConfig.getInstance();
	private IAppConfig appConfig = AppConfig.getInstance();
	
	private LocalDateTime startTimestamp;
	private int interation = 1;	//count the interaction
	private boolean running = false;

	ProceedingVehicle pVeh;
	ControlVehicle cVeh;
	private List<RTMS> rtmsList = new ArrayList<RTMS>();

	MilemarkerCSVLoader loadCVS;
	MilemarkerConverter mmConverter;
	
	//parameters from .properties file
	private double downstreamJamSpace;
	private double upstreamJamSpace;
	private double freeFlowSpeed;
	private double shockwaveSpeed;
	private double bottleneckMM;
	private double exitMM;
	private double reactionTime;
	private int    rtmsNumber;
	//rtms parameters
	private String rtmsName;
	private double rtmsMM;
	private double aveSpeed;
	private double rtmsTimestamp;
	private int	   volumn;
	//proceeding vehicle paramters
	private double pVehMM;
	//control vechile paramters
	private double cVehMM;
	private double cVehSpeed;
	private double currentTimestamp;
	private double controlVehSpeedCmd;
	//parameters for bottleneck
	private double bottleneckVehIndex;
	private double bottleneckTimestamp;
	//the vehicle index at the intersection of the shockwave and backwave
	private double interectionVehIndex;
	private boolean hasRtmsData = false;
	private boolean hasVehicleData = false;
	
	public void initialize(){
		
		//read the vehicle info (e.g., reaction, max acceleration),
    	//segment traffic info (e.g., downstream/upstream jam spacing),
    	//RTMS info (e.g., RTMS number, RTMS locations)  	
        try {
            config.loadFile("traffic.properties");
        } catch (Exception e) {
        	log.error("Unable to read the properties file: " + e.toString());
            throw new RuntimeException("Unable to read properties file.");
        }
        
        //get the Algorithm Thread start date and time
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        startTimestamp = LocalDateTime.of(date, time);
        
    	bottleneckVehIndex = Double.POSITIVE_INFINITY;
    	bottleneckTimestamp = Double.POSITIVE_INFINITY;
    	interectionVehIndex = Double.POSITIVE_INFINITY;
        
		//get data from properties file
		downstreamJamSpace = appConfig.getDoubleValue("downstream.jam.spacing");
		upstreamJamSpace = appConfig.getDoubleValue("upstream.jam.spacing");
		freeFlowSpeed = appConfig.getDoubleValue("freeflow.speed");
		shockwaveSpeed = appConfig.getDoubleValue("shockwave.speed");
		bottleneckMM = appConfig.getDoubleValue("bottleneck.location");
		exitMM = appConfig.getDoubleValue("exit.milemarker");
		reactionTime = appConfig.getDoubleValue("reation.time");
		rtmsNumber = appConfig.getIntValue("rtms.number");
        
        //initialize the proceeding vehicle
		pVeh = new ProceedingVehicle();
		//initialize the control vehicle
		cVeh = new ControlVehicle(appConfig);
		//create a new array to hold the RTMS data,and initialize all RTMS
		for (int i = 1; i < rtmsNumber + 1; ++i){	
			rtmsMM = appConfig.getIntValue("rtms" + i + ".location");
			rtmsList.add(new RTMS("USDOT_FHWA_T" + i, rtmsMM, downstreamJamSpace, appConfig));						
		}
		
		//load milemarker csv file into a list
		String csvPath = appConfig.getProperty("csv.path");
		String csvName = appConfig.getProperty("csv.name");
		loadCVS = new MilemarkerCSVLoader();		
		List<MilemarkerPoint> points = new ArrayList<>();
		try {
			points = loadCVS.load(new File(csvPath + csvName));
			log.info("Sucessfully load milemarker csv file from: " + csvPath + csvName);
		} catch (IOException e) {
			log.error("Failed to load milemarker csv file from: " + csvPath + csvName + 
					".  error: " + e.toString());
		}
		mmConverter = new MilemarkerConverter(points);
	}

	public void run() {
		
		initialize();
		running = true;
				
        new Thread(() -> {
            while (running) {
				upodateData();
				if (hasRtmsData && hasVehicleData) {
					//Kelli determined we only use 1 RTMS for TOPR 22 (fake RTMS)
					//the location of this RTMS is the location of the exit point
					RTMS downRTMS = null;
					int downRTMSindex = -1;
					for (int i = 0; i < rtmsNumber; i++) {
						downRTMS = rtmsList.get(i);
						//check whether the control vehicle is below one of any RTMS
						double lowestPoint = downRTMS.getRTMSMM() - shockwaveSpeed * currentTimestamp;
						if (pVehMM <= lowestPoint) {
							downRTMSindex = i;
							break;
						}
					}
					//predict the proceeding vehicle's index according to the vehcleIndex of all RTMS
					if (downRTMSindex == -1) {
						//To be determined
						//when proceeding vehicle passes the exit point, stop the algorithm
						log.warn("Proceeding vehicle passed exit point.");
                        continue;
					}

					double pVehIndex = downRTMS.getRTMSvehIndex() +
							downRTMS.getRTMSdensity() * (downRTMS.getRTMSMM() - pVehMM);
					pVeh.updatePveh(pVehMM, pVehIndex);

					//for esch RTMS, calculate the interactions of shockwave lines and the free-flow speed trajectory
					//then calculate the vehicle index at interactions according to shockwave line functions
					for (int j = downRTMSindex; j < rtmsNumber; j++) {
						RTMS rtms = rtmsList.get(j);
						double interectionTimestamp = interectionTimestamp(rtms.getRTMSMM(), pVehMM, freeFlowSpeed,
								shockwaveSpeed, currentTimestamp, rtms.getRTMStimestamp());
						if (pVehMM < bottleneckMM) {
							bottleneckVehIndex = rtms.getRTMSvehIndex() + (rtms.getRTMSMM() - bottleneckMM) / downstreamJamSpace;
							bottleneckTimestamp = rtms.getRTMStimestamp() + (rtms.getRTMSMM() - bottleneckMM) / shockwaveSpeed;
							interectionVehIndex = bottleneckVehIndex +
									shockwaveSpeed * (interectionTimestamp - bottleneckTimestamp) / upstreamJamSpace;
						} else {
							interectionVehIndex = rtms.getRTMSvehIndex() +
									shockwaveSpeed * (interectionTimestamp - rtms.getRTMStimestamp()) / upstreamJamSpace;
						}
						//when interectionVehIndex < proceedingVehIndex, it is congested;
						//else, it is not congested ,using ProceedingVehIndex as interectionVehIndex
						if (interectionVehIndex < pVeh.getPVehIndex()) {
							if (pVehMM < bottleneckMM) {
								interectionTimestamp = bottleneckTimestamp + upstreamJamSpace *
										(pVeh.getPVehIndex() - bottleneckVehIndex) / shockwaveSpeed;
							} else {
								interectionTimestamp = rtms.getRTMStimestamp() +
										downstreamJamSpace * (pVeh.getPVehIndex() - rtms.getRTMSvehIndex()) / shockwaveSpeed;
							}
						}
						//update intersection info: timestamp, vehicle index, location (MM)
						rtms.setInterectionInfo(interectionTimestamp, pVeh.getPVehIndex());
					}

					RTMS lastRTMS = rtmsList.get(0);
					//exit timestamp of the proceeding vehicle
					double pExitSpeed = lastRTMS.getRTMSspeed();
					double pExitTimestamp = lastRTMS.getInterectionTimestamp() +
							(exitMM - lastRTMS.getInterectionLoc()) / pExitSpeed;
					//calculate the exit timestamp of the shadow trajectory
					double sExitTimestamp = lastRTMS.getRTMSjamSpace() / pExitSpeed + reactionTime + pExitTimestamp;

					//if the proceeding vehicle does not reach the exit point where we end the control
					if (pVehMM < exitMM) {
						cVeh.updateControlVehicleData(cVehMM, cVehSpeed, currentTimestamp);
						controlVehSpeedCmd = cVeh.calControlSpeedCmd(sExitTimestamp, pExitSpeed,
								lastRTMS.getInterectionLoc(), pVehMM);
					}

					for (VehicleSession vs : getVehicles()) {
						VehicleCommand vc = new VehicleCommand();

						vc.setCommandConfidence(99.99);
						vc.setSpeed(controlVehSpeedCmd);
						vc.setTimestamp(LocalDateTime.now());
						vc.setVehId(vs.getId());
						vc.setId((long) interation);

						fireOutputCallbacks(vc);
						log.info("Outputting Command: " + vc);
					}

					try {
						interation = interation + 1;
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
            }
        }).start();	
	}
	
	public void upodateData(){
		//get RTMS data
        for (InfrastructureStatusUpdate isu : getPendingInfrastructureStatusUpdates()) {
			if (!hasRtmsData) {
				hasRtmsData = true;
			}

        	//get updated RTMS data from Infrastructure
        	rtmsName = isu.getInfrastructure().getName();			               			
        	volumn = isu.getVolume();				
        	aveSpeed = isu.getSpeed();				
        	//get RTMS timestamp, duration from the Algorithm Thread start date and time
        	Duration between = Duration.between(startTimestamp, isu.getTimestamp());
        	rtmsTimestamp = between.toMillis()/1000;
        	
        	//find the updated RTMS in rtmsList
        	for (int i = 0; i < rtmsList.size(); i++){
        		if (isu.getInfrastructure().getName().equals(rtmsList.get(i).getRTMSname())){
        			rtmsList.get(i).updateRTMSData(rtmsTimestamp, volumn, aveSpeed, interation);
        		}
        	}             	
        	log.info("Received update: " + isu);
        }
		//get Proceeding vehicle and Control vehicle's data
        for (VehicleStatusUpdate vsu : getPendingVehicleStatusUpdates()) {
			if (!hasVehicleData) {
				hasVehicleData = true;
			}

        	//get the timestamp of the latest control vehicle speed from the server
        	Duration currentTime = Duration.between(startTimestamp, vsu.getNetworkLatencyInformation().getCorrectedTxTimestamp());
        	currentTimestamp = currentTime.toMillis()/1000;
            log.info("Algorithm current timestamp: " + currentTimestamp + " Start timstamp: " + startTimestamp);
        	//get proceeding vehicle data from the server
    		cVehMM = mmConverter.convert(vsu.getLat(), vsu.getLon());		//meter
            log.info("Vehicle recorded at " + cVehMM);
    		cVehSpeed = vsu.getSpeed();	//m/s
    		//get control vehicle data from the server
    		pVehMM = cVehMM + 20;		//meter
    		
    		log.info("Received update: " + vsu);
    	}
		
	}
	public double interectionTimestamp(double rtmsMM, double pVehMM, double freeFlowSpeed,
			double shockwaveSpeed, double currentTimeStamp, double rtmsTimeStamp){
		
		double interectionTimestamp = (rtmsMM - pVehMM + shockwaveSpeed * rtmsTimeStamp + 
				freeFlowSpeed * currentTimeStamp)/(freeFlowSpeed + shockwaveSpeed);
		return interectionTimestamp;
	}
	
	 //convert time (format: yyyy-MM-dd HH:mm:ss.SSS) to millsecond
	 public static double timeConvertion(String inputTime){
		 
		 SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
		 sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		 Date timeInMillsecond = null;
		 try {
			 timeInMillsecond = sdf.parse(inputTime);
		 } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 return timeInMillsecond.getTime();
	 }

	@Override
	public String getAlgorithmName() {
		return "shooting-algorithm";
	}


	@Override
	public String getAlgorithmVersion() {
		return "v1.0";
	}

	@Override
	public int getMaxNumVehiclesPerInstance() {
		return 1;
	}

	@Override
	public List<InfrastructureDataSource> getRequiredInfrastructureDataSources() {
        List<InfrastructureDataSource> sources = new ArrayList<>();
        sources.add(InfrastructureDataSource.RTMS);

        return sources;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub		
	}
}
