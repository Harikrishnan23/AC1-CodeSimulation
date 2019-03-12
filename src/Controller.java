import java.io.InputStreamReader;
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
		signalProcessor= new SignalProcessor();
		postProcessor =  new PostProcessor();
	}
	
	private String drive(String expectedCarState, Location location) {
		return "";
	}
	
	private void terminateProcessor(){
		// code to turn off the processors.
	}

	private void terminateVehicleServices(){
		// code to turn off the Vehicle system.
	}

	private void terminateSensors(){
		// code to turn off the sensor
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String cmd = "", finalCarState="";
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the command(Park/Unpark)");
		cmd = scanner.nextLine();
		Controller controller = new Controller();
		Location userLocation = new Location();
		
		if(cmd == "Park") {
			int maxSlots = 0,maxAttempts = 5, m,n;
			outerloop:
				for(n=0; n<maxAttempts; n++) {
					Location[] locations = controller.imageProcessor.getLocations();
					maxSlots = locations.length;
					for(m=0; m<maxSlots; m++) {
						finalCarState = controller.drive("Parked", locations[m]);
						if(finalCarState == "Parked") {
							break outerloop;
						}
					}
				}
				finalCarState = finalCarState != "Parked" ? "Parking Failed" : finalCarState;
		}
		else {
			
			System.out.println("Enter pickup location (latitude): ");
			userLocation.latitude  = scanner.nextDouble();
			System.out.println("Enter pickup location (longitude): ");
			userLocation.longitude = scanner.nextDouble();
			//initialize vehicle system only when its parked (off) and is about to unpark
			controller.vehicleSystem = new VehicleSystem();
			finalCarState = controller.drive("Un-parked", userLocation);
		}
		
		switch (finalCarState) {
		case "Parked":
			//success notification
			controller.terminateProcessor();
			controller.terminateVehicleServices();
			controller.terminateSensors();
			break;
		
		case "Parking failed":
			//failure notification
			controller.drive("Un-Parked",userLocation);
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
