import java.io.InputStreamReader;
import java.util.Random;
import java.util.Scanner;

public class Controller {
	private ImageProcessor imageProcessor;
	private SignalProcessor signalProcessor;
	private PostProcessor postProcessor;
	private VehicleSystem vehicleSystem;
	private LIDAR lidar;
	private Cameras cameras;
	private ProximitySensors proximitySensors;
	private String carState;
	// created for simulation
	private Location finalLocation;

	public Controller() {
		this.initialiseSensors();
		this.initialiseProcessors();
		this.carState = "Idle";
	}

	private void initialiseSensors() {
		lidar = new LIDAR();
		cameras = new Cameras();
		proximitySensors = new ProximitySensors();
	}

	private void initialiseProcessors() {
		imageProcessor = new ImageProcessor();
		signalProcessor = new SignalProcessor();
		postProcessor = new PostProcessor();
	}

	public Location getCurrentCarLocation(int index) {
		/*return some random location if index not equal to 20. If its 20 return final location*/
		Location location = index == 20 ? finalLocation : new Location(27.628210, 76.181052);
		return location;
	}

	private void setCarState(int index) {
		/*In reality, this state setting will be done on the basis of inputs from sensors and 
		 car's current location. For simulation, we are fixing the range of index within which we
		 set specific values to drive/park the car*/
		if(index < 14) {
			this.carState = "Driving";
		}
		else if(index == 14) {
			this.carState = "Slot_Reached";
		}
		else if(index > 14 && index <20) {
			this.carState = "Parking";
		}
		else {
			this.carState = "Parked";
		}
	}
	
	private Status getDecision(String lastDecision) {
		Status status = new Status();
		//if lastDecision was empty then move forward
		if(lastDecision.isEmpty()) {
			status.gear = "Forward";
		}
		else{
			/*doing the following because the vehicle can't move from forward to reverse(or vice versa)
			 directly. It has to move to stop before moving in other direction*/
			Random randomGenerator = new Random();
			int randomInt = randomGenerator.nextInt(2);
			/*0 - forward, 1 - reverse
			if randomInt generates forward then check last decision. If its reverse then stop
			otherwise move forward as per the randomInt*/
			if(randomInt == 0) {
				status.gear = lastDecision == "Reverse" ? "Stop" : "Forward";
			}
			/*if randomInt generates reverse then check last decision. If its forward then stop
			otherwise move forward as per the randomInt*/
			else {
				status.gear = lastDecision == "Forward" ? "Stop" : "Reverse";
			}
		}
		
		double upper = 540.0;
		double lower = -540.0;
		status.angle = Math.random() * (upper - lower) + lower;
		
		upper = 20.0;
		lower = 0.0;
		status.speed = Math.random() * (upper - lower) + lower;
		
		return status;
		
	}
	private String drive(String expectedCarState, Location location) {
		int locationIndex = 0;
		Location currentCarLocation = this.getCurrentCarLocation(locationIndex);
		boolean slotEvaluated = false;
		Status status = new Status();
		while(currentCarLocation != location) {
			
		}
		int stateIndex = 0;
		return "";
	}

	private void terminateProcessor() {
		// code to turn off the processors.
	}

	private void terminateVehicleServices() {
		// code to turn off the Vehicle system.
	}

	private void terminateSensors() {
		// code to turn off the sensor
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String cmd = "", finalCarState = "";
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the command(Park/Unpark)");
		cmd = scanner.nextLine();
		Controller controller = new Controller();
		Location userLocation = new Location();
		// Location[] locations = controller.imageProcessor.getLocations();
		// if (locations.length == 0) {
		// System.out.println("no data");
		// }
		// for(int i=0; i<locations.length; i++) {
		// System.out.println(locations[i]);
		// }

		if (cmd == "Park") {
			int maxSlots = 0, maxAttempts = 3, m, n;
			outerloop: for (n = 0; n < maxAttempts; n++) {
				Location[] locations = controller.imageProcessor.getLocations();
				maxSlots = locations.length;
				for (m = 0; m < maxSlots; m++) {
					controller.finalLocation = locations[m];
					finalCarState = controller.drive("Parked", locations[m]);
					if (finalCarState == "Parked") {
						break outerloop;
					}
				}
			}
			finalCarState = finalCarState != "Parked" ? "Parking Failed" : finalCarState;
		} else {

			System.out.println("Enter pickup location (latitude): ");
			userLocation.latitude = scanner.nextDouble();
			System.out.println("Enter pickup location (longitude): ");
			userLocation.longitude = scanner.nextDouble();
			// initialize vehicle system only when its parked (off) and is about to unpark
			controller.vehicleSystem = new VehicleSystem();
			finalCarState = controller.drive("Un-parked", userLocation);
		}

		switch (finalCarState) {
		case "Parked":
			// success notification
			controller.terminateProcessor();
			controller.terminateVehicleServices();
			controller.terminateSensors();
			break;

		case "Parking failed":
			// failure notification
			controller.drive("Un-Parked", userLocation);
			break;

		case "Un-parked":
			// unpark success notification
			controller.terminateProcessor();
			controller.terminateSensors();
			break;

		default:
			break;
		}

	}

}
