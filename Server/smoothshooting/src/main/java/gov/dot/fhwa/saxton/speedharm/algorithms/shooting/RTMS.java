package gov.dot.fhwa.saxton.speedharm.algorithms.shooting;

public class RTMS {

	//RTMS data from SQL Server database
	protected String   rtmsName;				//RTMS name
	protected double   rtmsTimestamp;			//this RTMS data's timestamp, second
	protected double   rtmsMM;					//relative location (milemarker) of the RTMS
	protected Integer  volumn;					//vehicle per one RTMS interval
	protected double   aveSpeed;				//meter/second
	
	//parameters from .properties file
	protected double   rtmsInterval;			//second
	protected double   shockwaveSpeed;			//meter/second
	
	protected double   jamSpace;				//meter
	
	protected double   flow;					//vehicle/second
	protected double   density;					//in units of vehicles per meter	
	protected double   vehIndex;				//the vehicle index at RTMS location
	
	protected double   interectionTimestamp;	//the timestamp at the interaction of the shockwave line and the free-flow speed trajectory
	protected double   interectionLoc;			//the location at the interaction of the shockwave line and the free-flow speed trajectory
	protected double   interectionVehIndex;		//the vehicle index at the interaction of the shockwave line and the free-flow speed trajectory
	protected double   planTimeInterval;		//the frequency of generating the control vehicle's trajectory
		
	//save VehicleIndex of the RTMS and Flow and Density value for the usage of the next iteration. 
	protected double[] rtmsTrafficAtt;
	
	protected int iteration;
	
	public RTMS(String rtmsName, double rtmsMM, double jamSpace, IAppConfig config){
		
		this.rtmsName = rtmsName;
		this.rtmsMM = rtmsMM;
		this.jamSpace = jamSpace;
		this.rtmsInterval = config.getIntValue("rtms.interval");
		this.shockwaveSpeed = config.getDoubleValue("shockwave.speed");
		this.planTimeInterval = config.getIntValue("planning.horizon");
		
		this.rtmsTrafficAtt = new double[4];
	}
	
	public void updateRTMSData(double rtmsTimestamp, int volumn, double aveSpeed, int iteration){
				
		this.rtmsTimestamp = rtmsTimestamp;
		this.volumn = volumn;
		this.aveSpeed = aveSpeed;	//m/s	
				
		this.flow = volumn/rtmsInterval;
		//use flow/speed instead of occupancy/length to calculate the density,
		//because the occupancy is not acturate.
		this.density = flow/aveSpeed;
		//default velue, easy to identify the outlier
		this.vehIndex = calRTMSvehIndex(iteration);
		
		this.iteration = iteration;
	}
	
	public String getRTMSname(){
		return rtmsName;
	}
	
	public double getRTMSMM(){
		return rtmsMM;
	}
	
	public double getRTMSspeed(){
		return aveSpeed;
	}
	
	public double getRTMSflow(){
		return flow;
	}
	
	public double getRTMSdensity(){
		return density;
	}
	
	public double getRTMSvehIndex(){
		return vehIndex;
	}
	
	public double getRTMStimestamp(){
		return rtmsTimestamp;
	}
	
	public double getRTMSjamSpace(){
		return jamSpace;
	}
	
	public double getInterectionTimestamp(){
		return interectionTimestamp;
	}
	
	public double getInterectionLoc(){
		return interectionLoc;
	}
	
	public double getInterectionVehIndex(){
		return interectionVehIndex;
	}
	public void setInterectionInfo(double timestamp, double vehIndex){
		this.interectionTimestamp = timestamp;		
		this.interectionVehIndex = vehIndex;
		this.interectionLoc = shockwaveFunction(timestamp);
	}
//	//if it is congested, this function will be executed.
//	public void setInterectionVehIndex(double interectionVehIndex){
//		this.interectionVehIndex = interectionVehIndex;
//		this.interectionTimestamp = shockwaveTimestamp(interectionVehIndex);
//		this.interectionLoc = shockwaveFunction(interectionTimestamp);
//	}
//	
	//calculate the vehicle index at RTMS location
	protected double calRTMSvehIndex(int iteration){
		//If this is the first iteration, then the vehicleIndex at RTMS 1 is 0.
		//For TOPR 22，we only use 1 RTMS.
		if (iteration == 1){
			vehIndex = 0;
		} else {
			double prevVehIndex = rtmsTrafficAtt[0];
			double prevFlow = rtmsTrafficAtt[1];
			vehIndex = prevVehIndex + (prevFlow + flow) * planTimeInterval / 2;
		}
		return vehIndex;
	}
	
	//shockwave line function
	//return the estimated locations on the shockwave line
	protected double shockwaveFunction(double timestamp){
		//estimated current shockwave location
		double shockwaveLoc = rtmsMM - shockwaveSpeed * (timestamp - rtmsTimestamp);
		return shockwaveLoc;
	}
	
//	//estimate the vehicle index on the shockwave line at the given timestamp
//	public double shockwaveVehIndex(double timestamp){
//		
//		double shockwaveVehIndex = vehIndex + shockwaveSpeed/1000 * (timestamp - rtmsTimestamp)/jamSpace;
//		return shockwaveVehIndex;
//	}
	
//	protected double shockwaveTimestamp(double shockwaveVehIndex){
//		
//		double shockwaveTimestamp = rtmsTimestamp + jamSpace * (shockwaveVehIndex - vehIndex)/shockwaveSpeed;
//		return shockwaveTimestamp;
//	}
	
	//at the end of this iteration, save rtmsVehIndex, flow, density and timestamp values.
	public void updateTrafficAtt(double rtmsVehIndex, double flow, double density, double timestamp){
		
		rtmsTrafficAtt[0] = rtmsVehIndex;
		rtmsTrafficAtt[1] = flow;
		rtmsTrafficAtt[2] = density;
		rtmsTrafficAtt[3] = timestamp;
	}
}
