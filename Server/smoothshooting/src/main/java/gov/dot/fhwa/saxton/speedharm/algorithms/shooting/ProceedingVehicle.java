package gov.dot.fhwa.saxton.speedharm.algorithms.shooting;

public class ProceedingVehicle {
	
	protected double pVehMM;	//the milemarker of the proceeding vehicle
	protected double pVehIndex;

//	public ProceedingVehicle(double proceedingVehMM){
//		
//		this.proceedingVehMM = proceedingVehMM;
//		//estimate the vehicle index of the proceeding vehicle
////		this.proceedingVehIndex = firstRTMSvehIndex + firstRTMSdensity * (firstRTMSMM - proceedingVehMM);;
//	} 
	
	public double getPVehIndex(){
		return pVehIndex;
	}
	
	public void updatePveh(double pVehMM, double pVehIndex){
		this.pVehMM = pVehMM;
		this.pVehIndex = pVehIndex;
	}
}
