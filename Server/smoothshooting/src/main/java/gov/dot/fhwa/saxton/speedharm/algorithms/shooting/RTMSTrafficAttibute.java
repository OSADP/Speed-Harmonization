package gov.dot.fhwa.saxton.speedharm.algorithms.shooting;

public class RTMSTrafficAttibute {

	protected double rtmsVehIndex;
	protected double rtmsFlow;
	protected double rtmsDensity;
	protected double timestamp;
	
	public void setRTMSperviousData(double rtmsVehIndex, double rtmsFlow, double rtmsDensity, double timestamp){
		this.rtmsVehIndex = rtmsVehIndex;
		this.rtmsFlow = rtmsFlow;
		this.rtmsDensity = rtmsDensity;
		this.timestamp = timestamp;
	}
	
	public double getRTMSVehIndex(){
		return rtmsVehIndex;
	}
	
	public double getRTMSFlow(){
		return rtmsFlow;
	}
	
	public double getRTMSDensity(){
		return rtmsDensity;
	}
	
	public double getRTMStimestamp(){
		return timestamp;
	}
}
