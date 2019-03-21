import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.*;
import static org.mockito.Mockito.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Tests {
	@Test
	public void testLocationInitialization() {
		System.out.println("***Test 1***");
		// data initialization
		Controller controller = new Controller();
		Location[] allPossibleLocations = { new Location(28.628210, 77.211052) };
		controller.imageProcessor.setLocations(allPossibleLocations);

		// assert
		assertEquals(controller.imageProcessor.getLocations()[0], allPossibleLocations[0]);
	}

	@Test
	public void testParkingWithOneAttemptSuccess() {
		System.out.println("\n***Test 2***");
		Controller controller = new Controller();
		Location[] allPossibleLocations = { new Location(28.628210, 77.211052) };
		controller.imageProcessor.setLocations(allPossibleLocations);
		controller.cmd = "Park";
		controller.slotEvalAttempt = 1;
		String finalCarState = Controller.park(controller);
		assertEquals("Parked", finalCarState);
	}

	@Test
	public void testParkingWithMoreThanOneAttemptSuccess() {
		System.out.println("\n***Test 3***");
		Controller controller = new Controller();
		Location[] allPossibleLocations = { new Location(28.628210, 77.211052), new Location(27.871, 65.11) };
		controller.imageProcessor.setLocations(allPossibleLocations);
		controller.cmd = "Park";
		controller.slotEvalAttempt = 2;
		String finalCarState = Controller.park(controller);
		assertEquals("Parked", finalCarState);
	}

	@Test
	public void testParkingNoFitSlotFound() {
		System.out.println("\n***Test 4***");
		Controller controller = new Controller();
		Location[] allPossibleLocations = { new Location(28.628210, 77.211052) };
		controller.imageProcessor.setLocations(allPossibleLocations);
		controller.cmd = "Park";
		controller.slotEvalAttempt = -1;
		String finalCarState = Controller.park(controller);
		assertEquals("Parking Failed", finalCarState);
	}

	@Test
	public void testParkingNoSlotFound() {
		System.out.println("\n***Test 5***");
		Controller controller = new Controller();
		Location[] allPossibleLocations = new Location[0];
		controller.imageProcessor.setLocations(allPossibleLocations);
		controller.cmd = "Park";
		controller.slotEvalAttempt = -1;
		String finalCarState = Controller.park(controller);
		assertEquals("Parking Failed", finalCarState);
	}

	@Test
	public void testUnparkingSuccess() {
		System.out.println("\n***Test 6***");
		Controller controller = new Controller();
		controller.cmd = "Unpark";
		controller.finalLocation = new Location(29.11, 67.981);
		String finalCarState = controller.drive("Un-parked", controller.finalLocation);
		assertEquals("Un-parked", finalCarState);
	}

	@Test
	public void testInvalidGUICommand() {
		System.out.println("\n***Test 7***");
		String[] args = { "abcd" };
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream stdout = System.out;
		try {
			// setup
			System.setOut(new PrintStream(outContent));

			Controller.main(args);
			assertEquals("Command invalid. Please try again!", outContent.toString().trim());
		} finally {
			System.setOut(stdout);
		}
	}

	@Test
	public void testForwardMovement() {
		System.out.println("\n***Test 8***");
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream stdout = System.out;
		try {
			// setup
			System.setOut(new PrintStream(outContent));
			VehicleSystem vs = new VehicleSystem();
			vs.forward(26.91, 6.8);
			assertEquals("Move forward at 26.91 degrees at 6.8 km/hr", outContent.toString().trim());
		} finally {
			System.setOut(stdout);
		}
	}

	@Test
	public void testReverseMovement() {
		System.out.println("\n***Test 9***");
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream stdout = System.out;
		try {
			// setup
			System.setOut(new PrintStream(outContent));
			VehicleSystem vs = new VehicleSystem();
			vs.reverse(250.9, 3.4);
			assertEquals("Move back at 250.9 degrees at 3.4 km/hr", outContent.toString().trim());
		} finally {
			System.setOut(stdout);
		}
	}

	@Test
	public void testBrakeDuringCollision() {
		System.out.println("\n***Test 10***");
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream stdout = System.out;
		try {
			// setup
			System.setOut(new PrintStream(outContent));
			VehicleSystem vs = new VehicleSystem();
			vs.applyBrakes(true);
			assertEquals("Stopped due to collision", outContent.toString().trim());
		} finally {
			System.setOut(stdout);
		}
	}

	@Test
	public void testBrakeDueToChangeInDirection() {
		System.out.println("\n***Test 11***");
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream stdout = System.out;
		try {
			// setup
			System.setOut(new PrintStream(outContent));
			VehicleSystem vs = new VehicleSystem();
			vs.applyBrakes(false);
			assertEquals("Stopped due to change in direction", outContent.toString().trim());
		} finally {
			System.setOut(stdout);
		}
	}

	@Test
	public void testSpeedLimit() {
		System.out.println("\n***Test 12***");
		Controller controller = new Controller();
		Status status = controller.getDecision("");
		assertTrue((status.speed >= 0.0) && (status.speed <= 20.0));
		status = controller.getDecision("Forward");
		assertTrue((status.speed >= 0.0) && (status.speed <= 20.0));
		status = controller.getDecision("Reverse");
		assertTrue((status.speed >= 0.0) && (status.speed <= 20.0));
	}

	@Test
	public void testAngleRange() {
		System.out.println("\n***Test 13***");
		Controller controller = new Controller();
		Status status = controller.getDecision("");
		assertTrue((status.angle >= -540.0) && (status.angle <= 540.0));
		status = controller.getDecision("Forward");
		assertTrue((status.angle >= -540.0) && (status.angle <= 540.0));
		status = controller.getDecision("Reverse");
		assertTrue((status.angle >= -540.0) && (status.angle <= 540.0));
	}

	@Test
	public void testGearOnFirstMovement() {
		System.out.println("\n***Test 14***");
		Controller controller = new Controller();
		Status status = controller.getDecision("");
		assertEquals("Forward", status.gear);
	}

	@Test
	public void testDefaultAngleAndSpeed() {
		System.out.println("\n***Test 15***");
		Controller controller = new Controller();
		Status status = controller.getDecision("");
		if (status.gear.isEmpty()) {
			assertTrue((status.angle == 0.0) && (status.speed == 0.0));
		} else {
			assertTrue((status.speed >= 0.0) && (status.speed <= 20.0));
			assertTrue((status.angle >= -540.0) && (status.angle <= 540.0));
		}
	}

	@Test
	public void testSensorTerminationForBothParkingAndUnparking() {
		System.out.println("\n***Test 16***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		controllerSpy.terminate("Parked", new Location(26.87,98.71));
		controllerSpy.terminate("Un-parked", new Location(26.87,98.71));
		verify(controllerSpy, times(2)).terminateSensors();
	}
	
	@Test
	public void testProcessorTerminationForBothParkingAndUnparking() {
		System.out.println("\n***Test 17***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		controllerSpy.terminate("Parked", new Location(26.87,98.71));
		controllerSpy.terminate("Un-parked", new Location(26.87,98.71));
		verify(controllerSpy, times(2)).terminateProcessor();
	}
	
	@Test
	public void testVehicleServiceTerminationForParking() {
		System.out.println("\n***Test 18***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		controllerSpy.terminate("Parked", new Location(26.87,98.71));
		verify(controllerSpy, times(1)).terminateVehicleServices();
	}
	
	@Test
	public void testVehicleServiceNonTerminationForUnParking() {
		System.out.println("\n***Test 19***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		controllerSpy.terminate("Un-parked", new Location(26.87,98.71));
		verify(controllerSpy, times(0)).terminateVehicleServices();
	}
	@Test
	public void testVehicleServiceNonTerminationForFailedParking() {
		System.out.println("\n***Test 20***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		controllerSpy.terminate("Parking failed", new Location(26.87,98.71));
		verify(controllerSpy, times(0)).terminateVehicleServices();
	}
	
	@Test
	public void testEvaluateSlotForParking() {
		System.out.println("\n***Test 21***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87,98.71);
		controllerSpy.drive("Parked", finalLocation);
		verify(controllerSpy, times(1)).evaluateSlot(finalLocation);
	}
	
	@Test
	public void testEvaluateSlotForUnparking() {
		System.out.println("\n***Test 22***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87,98.71);
		controllerSpy.drive("Un-parked", finalLocation);
		verify(controllerSpy, times(0)).evaluateSlot(finalLocation);
	}
	
	@Test
	public void testDriveMethodForParkingFailure() {
		System.out.println("\n***Test 23***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location userLocation = new Location(26.87,98.71);
		controllerSpy.terminate("Parking failed", userLocation);
		verify(controllerSpy, times(1)).drive("Un-Parked",userLocation);
	}
	
	@Test
	public void testParkingSuccessfulNotification() {
		System.out.println("\n***Test 24***");
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream stdout = System.out;
		try {
			// setup
			System.setOut(new PrintStream(outContent));
			Controller controller = new Controller();
			Location finalLocation = new Location(26.87,98.71);
			//Parked successfully notification
			controller.terminate("Parked", finalLocation);
			assertEquals("Parked successfully. Sensors and Car stopped", outContent.toString().trim());

		} finally {
			System.setOut(stdout);
		}
	}	
}
