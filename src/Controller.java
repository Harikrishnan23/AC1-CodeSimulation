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
	private int locationIndex;
	private int randomVar;
	private String cmd = "";

	public Controller() {
		this.initialiseSensors();
		this.initialiseProcessors();
		this.vehicleSystem = new VehicleSystem();
		this.carState = "Idle";
		this.locationIndex = 0;
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

	public Location getCurrentCarLocation() {
		/*
		 * return some random location if index not equal to 20. If its 20 return final
		 * location
		 */
		Location location = this.locationIndex == 20 ? finalLocation : new Location(27.628210, 76.181052);
		return location;
	}

	private void setCarState() {
		/*
		 * In reality, this state setting will be done on the basis of inputs from
		 * sensors and car's current location. For simulation, we are fixing the range
		 * of index within which we set specific values to drive/park the car
		 */

		if (this.locationIndex < 14) {
			this.carState = "Driving";
		} else if (this.locationIndex == 14) {
			this.carState = "Slot_Reached";
		} else if (this.locationIndex > 14 && this.locationIndex < 20) {
			this.carState = "Parking";
		} else {
			this.carState = this.cmd == "Park"? "Parked" : "Un-parked";
		}
	}

	private boolean evaluateSlot(Location location) {
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(2);
		return randomInt == 1; // if randomInt is 1, return true else return false
	}

	private Status getDecision(String lastDecision) {
		Status status = new Status();
		// if lastDecision was empty then move forward
		if (lastDecision.isEmpty()) {
			status.gear = "Forward";
		} else {
			/*
			 * doing the following because the vehicle can't move from forward to reverse(or
			 * vice versa) directly. It has to move to stop before moving in other direction
			 */
			Random randomGenerator = new Random();
			int randomInt = randomGenerator.nextInt(3);
			this.randomVar = randomInt;
			/*
			 * 0 - forward, 1 - reverse if randomInt generates forward then check last
			 * decision. If its reverse then stop otherwise move forward as per the
			 * randomInt
			 */
			if (randomInt == 0) {
				status.gear = lastDecision == "Reverse" ? "Stop" : "Forward";
			}
			/*
			 * if randomInt generates reverse then check last decision. If its forward then
			 * stop otherwise move forward as per the randomInt
			 */
			else if (randomInt == 1) {
				status.gear = lastDecision == "Forward" ? "Stop" : "Reverse";
			} else {
				// when randomInt is 2, then stop due to collision
				status.gear = "";
			}

		}
		if (!status.gear.isEmpty()) {
			double upper = 540.0;
			double lower = -540.0;
			status.angle = Math.round((Math.random() * (upper - lower) + lower) * 100.0) / 100.0;

			upper = 20.0;
			lower = 0.0;
			status.speed = Math.round((Math.random() * (upper - lower) + lower) * 100.0) / 100.0;
		}

		return status;

	}

	private String drive(String expectedCarState, Location location) {
		Location currentCarLocation = this.getCurrentCarLocation();
		boolean slotEvaluated = false;
		Status status = new Status();
		String lastDecision = "";
		while (currentCarLocation != location) {
			// send last decision for new status if decisionIndex > 0

			status = (this.locationIndex == 14 || this.locationIndex == 20) ? new Status("Stop", 0.0, 0.0)
					: this.getDecision(lastDecision);
			switch (status.gear) {
			case "Forward":
				this.vehicleSystem.forward(status.angle, status.speed);
				lastDecision = "Forward";
				break;
			case "Reverse":
				this.vehicleSystem.reverse(status.angle, status.speed);
				lastDecision = "Reverse";
				break;
			case "Stop":
				this.vehicleSystem.applyBrakes(false);
				if (this.locationIndex != 14 && this.locationIndex != 20) {
					if (this.randomVar == 0) {
						this.vehicleSystem.forward(status.angle, status.speed);
						lastDecision = "Forward";
					} else {
						this.vehicleSystem.reverse(status.angle, status.speed);
						lastDecision = "Reverse";
					}
				}
				break;
			case "":
				this.vehicleSystem.applyBrakes(true);
				break;
			}
			this.setCarState();
			if (expectedCarState.equalsIgnoreCase("Parked") && slotEvaluated == false) {
				if (this.carState.equalsIgnoreCase("Slot_Reached")) {
					System.out.println("Slot Reached. Stop");
					System.out.println("Evaluate the slot reached");
					slotEvaluated = true;
					boolean slotOk = this.evaluateSlot(location);
					if (!slotOk) {
						System.out.println("Slot not fit. Move to next location");
						break;
						// break out of while loop because slot not ok
						// if slot ok, continue driving using while loop to park car
					} else {
						System.out.println("Slot ok. Park the car.");
					}
				}
			}
			this.locationIndex++;
			currentCarLocation = this.getCurrentCarLocation();
		}
		this.setCarState(); // update car state to latest state
		this.locationIndex = 0; // reinitialize the location index
		return this.carState;
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
		String finalCarState = "";
		Controller controller = new Controller();
		Location userLocation = new Location();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the command(Park/Unpark)");
		controller.cmd = scanner.nextLine();

		if (controller.cmd.equalsIgnoreCase("Park")) {
			int maxSlots = 0, maxAttempts = 3, m, n;
			userLocation = new Location(29.11,11.88); //hardcoded for now but will be taken from GPS in case of app
			finalCarState = "Parking failed";
			outerloop: for (n = 0; n < maxAttempts; n++) {
				Location[] locations = controller.imageProcessor.getLocations();
				maxSlots = locations.length;
				for (m = 0; m < maxSlots; m++) {
					controller.finalLocation = locations[m];
					finalCarState = controller.drive("Parked", locations[m]);
					System.out.println("Final car state for location " + (m + 1) + " is " + finalCarState);
					if (finalCarState == "Parked") {
						break outerloop;
					}
				}
			}
			finalCarState = finalCarState != "Parked" ? "Parking Failed" : finalCarState;
		} else if(controller.cmd.equalsIgnoreCase("unpark")){
			System.out.println("Enter pickup location (latitude): ");
			userLocation.latitude = scanner.nextDouble();
			System.out.println("Enter pickup location (longitude): ");
			userLocation.longitude = scanner.nextDouble();
			controller.finalLocation = userLocation;
			finalCarState = controller.drive("Un-parked", userLocation);
		}
		else {
			System.out.println("Command invalid. Please try again!");
			return;
		}
		switch (finalCarState) {
		case "Parked":
			// success notification
			controller.terminateProcessor();
			controller.terminateVehicleServices();
			controller.terminateSensors();
			System.out.println("Parked successfully. Sensors and Car stopped");
			break;

		case "Parking failed":
			// failure notification
			System.out.println("Parking failed. Unparking the car to user's intial location");
			controller.drive("Un-Parked", userLocation);
			break;

		case "Un-parked":
			// unpark success notification
			controller.terminateProcessor();
			controller.terminateSensors();
			System.out.println("Car unparked. Meet at the pin location");
			break;

		default:
			break;
		}

	}

}
