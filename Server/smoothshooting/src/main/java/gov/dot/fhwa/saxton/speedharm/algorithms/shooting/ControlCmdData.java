package gov.dot.fhwa.saxton.speedharm.algorithms.shooting;

public class ControlCmdData {
	
	private double timestamp;
	private double speedCmd;
	private double accelCmd;
	
	public ControlCmdData(double timestamp, double speedCmd, double accelCmd){
		
		this.timestamp = timestamp;
		this.speedCmd = speedCmd;
		this.accelCmd = accelCmd;
	}
	
	public double getTimestamp(){
		return timestamp;
	}
	
	public double getSpeedCmd(){
		return speedCmd;
	}
	
	public double getAccelCmd(){
		return accelCmd;
	}
}