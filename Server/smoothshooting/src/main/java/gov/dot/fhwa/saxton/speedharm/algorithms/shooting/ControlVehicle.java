package gov.dot.fhwa.saxton.speedharm.algorithms.shooting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class ControlVehicle {
	private Logger log = LogManager.getLogger();
	protected double exitMM;
	protected double cVehMM;
	protected double cVehSpeed;
	protected double currentTimestamp;
	protected double speedCmdInterval;
	protected double controlSpeedCmd;
	protected double maxAccel;
	protected double minAccel;
	
	//decision variables
	protected double accel;
	protected double accelTimestamp;
	protected double accelLoc;
	protected double accelSpeed;
	protected double stopTimestamp;
	protected double cruiseTimestamp;
	protected double cruiseLoc;
	
	//checking fesibility variables
	protected final static double gamma   = 0.618;
	protected static double alpha_max 	  = 0.3;
	protected static double alpha_min 	  = 0;
	protected final static double epsilon = 0.05;
	protected int feasible = 1;
	protected double alpha = 0;
	
	protected int caseNo = 0;
	
	//save speed command data for all points on the control vehicle's trajectory
	ArrayList<ControlCmdData> controlSpdCmdList = new ArrayList<ControlCmdData>();
	
	public ControlVehicle(IAppConfig config){
		
		this.exitMM = config.getDoubleValue("exit.milemarker");
		this.speedCmdInterval = config.getDoubleValue("speedCmd.frequency");
		this.maxAccel = config.getDoubleValue("max.acceleration");
		this.minAccel = config.getDoubleValue("min.acceleration");
	}
	
	public void updateControlVehicleData(double controlVehMM, double controlVehSpeed, double currentTimestamp){
		
		this.cVehMM = controlVehMM;
		this.cVehSpeed = controlVehSpeed;
		this.currentTimestamp = currentTimestamp;
	}
	
	//generate the trajectory of the control vehicle
	public double calControlSpeedCmd(double exitTimestamp, double exitSpeed, double lastInterectionLoc, double pVehMM){
		
//		alpha = (exitMM - lastInterectionLoc)/(exitMM - pVehMM);
				
		calDecisionVariable(exitTimestamp, exitSpeed, pVehMM);
		
		//Check feasibility of the control vehicle's trajectory
		while (accel < minAccel || accel > maxAccel){
			alpha_min = alpha;
			if (alpha_max - alpha_min > epsilon){
				alpha = alpha + gamma * (alpha_max - alpha_min);
				calDecisionVariable(exitTimestamp, exitSpeed, pVehMM);
			}	
		}
		
		//calcualte the speed input for each future timestamp
		log.info("Entering case # " + caseNo);
			if (caseNo == 1){
				for (int i = 1; i <= (exitTimestamp - currentTimestamp)/speedCmdInterval; i++){
					double nextTimestamp = currentTimestamp + i * speedCmdInterval;
					if (nextTimestamp < accelTimestamp){
						controlSpeedCmd = cVehSpeed - accel * i * speedCmdInterval; 
					} else if (nextTimestamp >= accelTimestamp && nextTimestamp < cruiseTimestamp){
						controlSpeedCmd = accelSpeed + accel * (nextTimestamp - accelTimestamp);
					} else if (nextTimestamp >= cruiseTimestamp && nextTimestamp <= exitTimestamp){
						controlSpeedCmd = exitSpeed;
					}
					controlSpdCmdList.add(new ControlCmdData(nextTimestamp, controlSpeedCmd, accel));
				}
			} else if (caseNo == 2){
				for (int i = 1; i <= (exitTimestamp - currentTimestamp)/speedCmdInterval; i++){
					double nextTimestamp = currentTimestamp + i * speedCmdInterval;
					if (nextTimestamp < accelTimestamp){
						controlSpeedCmd = cVehSpeed - accel * i * speedCmdInterval; 
					} else if (nextTimestamp >= accelTimestamp && nextTimestamp < stopTimestamp){
						controlSpeedCmd = 0;
					} else if (nextTimestamp >= stopTimestamp && nextTimestamp < cruiseTimestamp){
						controlSpeedCmd = accelSpeed + accel * (nextTimestamp - accelTimestamp);
					} else if (nextTimestamp >= cruiseTimestamp && nextTimestamp <= exitTimestamp){
						controlSpeedCmd = exitSpeed;
					}
					controlSpdCmdList.add(new ControlCmdData(nextTimestamp, controlSpeedCmd, accel));
				}
			} else if (caseNo == 3){
				for (int i = 1; i <= (exitTimestamp - currentTimestamp)/speedCmdInterval; i++){
					double nextTimestamp = currentTimestamp + i * speedCmdInterval;
					if (nextTimestamp < cruiseTimestamp){
						controlSpeedCmd = accelSpeed + accel * (nextTimestamp - accelTimestamp);
					} else {
						controlSpeedCmd = exitSpeed;
					}
					controlSpdCmdList.add(new ControlCmdData(nextTimestamp, controlSpeedCmd, accel));
				}
			} else if (caseNo == 4){
				for (int i = 1; i <= (exitTimestamp - currentTimestamp)/speedCmdInterval; i++){
					double nextTimestamp = currentTimestamp + i * speedCmdInterval;
					controlSpeedCmd = exitSpeed;
					controlSpdCmdList.add(new ControlCmdData(nextTimestamp, controlSpeedCmd, accel));
				}
			}

		return controlSpdCmdList.get(0).getSpeedCmd();
	}

	protected void calDecisionVariable(double exitTimestamp, double exitSpeed, double pVehMM){
		/** case 1: decelerate first, then accelerate, then cruise, no stop segment **/
		caseNo = 1;
		cruiseTimestamp = exitTimestamp - alpha * (exitMM - cVehMM)/exitSpeed;
		cruiseLoc = exitMM - alpha * (exitMM - cVehMM);
		
		//calculate four decision variables (case 1, without teh stop segment)		
		double timeDiff = cruiseTimestamp - currentTimestamp;
		double speedSum = cVehSpeed + exitSpeed;
		double locDiff = cruiseLoc - cVehMM;
		
		//calculate the acceleration of the control vehicle
//		accel =(timeDiff * speedSum + Math.sqrt(2 * (cVehSpeed * cVehSpeed +
//				exitSpeed * exitSpeed) * timeDiff * timeDiff - 4 * speedSum * timeDiff * locDiff + 
//				4 * locDiff * locDiff))/(timeDiff * timeDiff);
		accel = 0.7;
		//the timestamp when the control vehicle starts accelerating
		accelTimestamp = (cruiseTimestamp + currentTimestamp)/2 + (cVehSpeed - exitSpeed)/(2 * accel);
		//the location when the control vehicle starts accelerating
		accelLoc = - accel * Math.pow(accelTimestamp - currentTimestamp, 2) + 
				cVehSpeed * (accelTimestamp - currentTimestamp) + cVehMM;
		//the speed at the point when the control vehicle starts accelerating
		accelSpeed = cVehSpeed - accel * (accelTimestamp - currentTimestamp);
		stopTimestamp = 0;
		
		//when accelSpeed < 0, case 1 is not feasible, go to case 2
		/** case 2: decelerate first, then a stop segment, then accelerate, then cruise **/
		if (accelSpeed < 0){
			caseNo = 2;
			accel = (cVehSpeed * cVehSpeed + exitSpeed * exitSpeed)/(cruiseLoc - cVehMM)/2; 
			accelTimestamp = currentTimestamp + cVehSpeed / accel;
			accelLoc = cVehMM + cVehSpeed * cVehSpeed * locDiff/(cVehSpeed * cVehSpeed + exitSpeed * exitSpeed);
			//the timestop when the control vehicle stops after decelerating
			stopTimestamp = cruiseTimestamp - exitSpeed/accel - accelTimestamp;
			accelSpeed = 0;
		}
		
		//when accelTimestamp < currentTimestamp, case 1 and 2 are not feasible, go to case 3
		/** case 3: accelerate, then cruise **/
		if (accelTimestamp < currentTimestamp){
			caseNo = 3; 
//			accel = Math.pow(cVehSpeed - exitSpeed, 2)
//					/(exitSpeed * (exitTimestamp -currentTimestamp) - (exitMM - cVehMM))/2;
			accel = 0.7;
//			cruiseTimestamp = (currentTimestamp * speedSum - 2 * exitTimestamp * exitSpeed + 
//					2 * (exitMM - cVehMM))/(cVehSpeed - exitSpeed);
			//after the control vehicle accelerates to the exitSpeed, then go cruise control
			cruiseTimestamp = currentTimestamp + (exitSpeed - cVehSpeed)/accel;
			cruiseLoc = accel * timeDiff * timeDiff/2 + cVehSpeed * timeDiff + cVehMM;
//			accelTimestamp = 0;
			accelTimestamp = currentTimestamp;
			accelLoc = cVehMM;
			accelSpeed = cVehSpeed;
			stopTimestamp = 0;
		}
		
		//when cruiseTimestamp < currentTimestamp, go to case 4
		/** case 4: cruise control **/
		if (cruiseTimestamp < currentTimestamp){
			caseNo = 4;
			accel = 0;
			accelTimestamp = 0;
			accelLoc = cVehMM;
			accelSpeed = cVehSpeed;
			stopTimestamp = 0;
			cruiseTimestamp = exitTimestamp;
			cruiseLoc = exitMM;
		}
	}

}
