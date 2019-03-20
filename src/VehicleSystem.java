
public class VehicleSystem {
	
	public VehicleSystem() {
		
	}
	
	public void forward(double angle, double speed) {
		System.out.println("Move forward at "+angle+" degrees at "+speed + " km/hr");
		//Accelerate forward in vehicleAngle direction with vehicleSpeed km/hr
		//Comunicate with the hardware system.
	}
	
	public void reverse(double angle, double speed) {
		System.out.println("Move back at "+angle+" degrees at "+speed + " km/hr");
		//Accelerate backward with vehicleAngle direction with vehicleSpeed km/hr.
		//Comunicate with the hardware system.
	}
	
	public void applyBrakes(boolean isStoppedForCollision) {
		if(!isStoppedForCollision) {
			System.out.println("Stopped due to change in direction");
		}
		else {
			System.out.println("Stopped due to collision");
		}
		// Apply brakes
		//Communicate with hardware system
	}
	
	//other methods to turn vehicle system on and off
}
