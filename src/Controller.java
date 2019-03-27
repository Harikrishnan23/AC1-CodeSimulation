import java.util.Random;

public class Controller {
	public ImageProcessor imageProcessor;
	private SignalProcessor signalProcessor;
	private PostProcessor postProcessor;
	public VehicleSystem vehicleSystem;
	private LIDAR lidar;
	private Cameras cameras;
	private ProximitySensors proximitySensors;
	private String carState;
	// created for simulation
	public Location finalLocation;
	public int locationIndex;
	private int randomVar;
	public String cmd = "";
	public int slotEvalAttempt = -1;
	public boolean isSlotFit = false;

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
		Location location = this.locationIndex == 20 ? this.finalLocation : new Location(27.628210, 76.181052);
		return location;
	}

	private void setCarState() {
		/*
		 * In reality, this state setting will be done on the basis of inputs from
		 * sensors and car's current location. For simulation, we are fixing the range
		 * of index within which we set specific values to drive/park the car
		 */
		
		if(this.locationIndex == 0) {
			this.carState = "";
		}
		else if (this.locationIndex < 14) {
			this.carState = "Driving";
		} else if (this.locationIndex == 14) {
			this.carState = "Slot_Reached";
		} else if (this.locationIndex > 14 && this.locationIndex < 20) {
			this.carState = "Parking";
		} else {
			this.carState = this.cmd == "Park"? "Parked" : "Un-parked";
		}
	}

	public boolean evaluateSlot(Location location) {
//		Random randomGenerator = new Random();
//		int randomInt = randomGenerator.nextInt(2);
//		return randomInt == 1; // if randomInt is 1, return true else return false
		return this.isSlotFit;
	}

	public Status getDecision(String lastDecision) {
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
			System.out.println("Random int is "+randomInt + " and gear is "+status.gear);

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

	public String drive(String expectedCarState, Location location) {
		this.finalLocation = location;
		Location currentCarLocation = this.getCurrentCarLocation();
		boolean slotEvaluated = false;
		Status status = new Status();
		String lastDecision = "";
		while (!currentCarLocation.equals(location)) {
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
			if (slotEvaluated == false && expectedCarState.equalsIgnoreCase("Parked")) {
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
		System.out.println("Car is " + this.carState);
		return this.carState;
	}

	public void terminateProcessor() {
		// code to turn off the processors.
	}

	public void terminateVehicleServices() {
		// code to turn off the Vehicle system.
	}

	public void terminateSensors() {
		// code to turn off the sensor
	}
	
	public static String park(Controller controller) {
		String finalCarState = "Parking failed";
		int maxSlots = 0, maxAttempts = 3, m, n;
		outerloop: for (n = 0; n < maxAttempts; n++) {
			Location[] locations = controller.imageProcessor.getLocations();
			maxSlots = locations.length;
			for (m = 0; m < maxSlots; m++) {
				controller.finalLocation = locations[m];
				controller.isSlotFit = controller.slotEvalAttempt == (m+1) ;
				finalCarState = controller.drive("Parked", locations[m]);
				if (finalCarState == "Parked") {
					break outerloop;
				}
			}
		}
		finalCarState = finalCarState != "Parked" ? "Parking Failed" : finalCarState;
		return finalCarState;	
	}
	
	public void terminate(String finalCarState,Location userLocation) {
		switch (finalCarState) {
		case "Parked":
			// success notification
			this.terminateProcessor();
			this.terminateVehicleServices();
			this.terminateSensors();
			System.out.println("Parked successfully. Sensors and Car stopped");
			break;

		case "Parking failed":
			// failure notification
			this.finalLocation = userLocation;
			System.out.println("Parking failed. Unparking the car to user's intial location");
			this.drive("Un-Parked", userLocation);
			break;

		case "Un-parked":
			// unpark success notification
			this.terminateProcessor();
			this.terminateSensors();
			System.out.println("Car unparked. Meet at the pin location");
			break;

		default:
			break;
		}	
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String finalCarState = "";
		Controller controller = new Controller();	
		Location userLocation = new Location(29.11,11.88); 
		if(args[0].equalsIgnoreCase("park")) {
			controller.cmd = "Park";
			controller.slotEvalAttempt = Integer.parseInt(args[1]);
			finalCarState = park(controller);
		}
		else if(args[0].equalsIgnoreCase("unpark")) {
			controller.cmd = "Unpark";
			controller.finalLocation = new Location(Double.parseDouble(args[1]),Double.parseDouble(args[2]));
			finalCarState = controller.drive("Un-park", controller.finalLocation);
		}
		else {
			System.out.println("Command invalid. Please try again!");
			return;
		}
		controller.terminate(finalCarState,userLocation);
	}

}
