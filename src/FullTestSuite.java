import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class FullTestSuite {
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
	@Test
	// verifying gear decision system when lastDecision = "Reverse" and random
	// variable is 0/1/2
	public void testGetDecisionReverse() {
		System.out.println("\n***Test 25***");
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream stdout = System.out;
		try {
			// setup
			System.setOut(new PrintStream(outContent));
			Controller controller = new Controller();
			Status status = controller.getDecision("Reverse");
			String expected1 = "Random int is 0 and gear is Stop";
			String expected2 = "Random int is 1 and gear is Reverse";
			String expected3 = "Random int is 2 and gear is";
			assertTrue(outContent.toString().trim().equalsIgnoreCase(expected1)
					|| outContent.toString().trim().equalsIgnoreCase(expected2)
					|| outContent.toString().trim().equalsIgnoreCase(expected3));
		} finally {
			System.setOut(stdout);
		}
	}

	@Test
	// verifying gear decision system when lastDecision = "Forward" and random
	// variable is 0/1/2
	public void testGetDecisionForward() {
		System.out.println("\n***Test 26***");
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream stdout = System.out;
		try {
			// setup
			System.setOut(new PrintStream(outContent));
			Controller controller = new Controller();
			Status status = controller.getDecision("Forward");
			String expected1 = "Random int is 0 and gear is Forward";
			String expected2 = "Random int is 1 and gear is Stop";
			String expected3 = "Random int is 2 and gear is";
			assertTrue(outContent.toString().trim().equalsIgnoreCase(expected1)
					|| outContent.toString().trim().equalsIgnoreCase(expected2)
					|| outContent.toString().trim().equalsIgnoreCase(expected3));
		} finally {
			System.setOut(stdout);
		}
	}

	@Test
	// verifying evaluateSlot method call when expectedCarState = "Parked" and
	// slotEvaluated = false
	public void testDriveParkedAndSlotEvaluated() {
		System.out.println("\n***Test 27***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87, 98.71);
		controllerSpy.drive("Parked", finalLocation);
		verify(controllerSpy, times(1)).evaluateSlot(finalLocation);
	}

	@Test
	// verifying evaluateSlot method call when expectedCarState = "Un-parked" and slotEvaluated is dont care
	public void testDriveUnParkedAndSlotEvaluated() {
		System.out.println("\n***Test 28***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87, 98.71);
		controllerSpy.drive("Un-parked", finalLocation);
		verify(controllerSpy, times(0)).evaluateSlot(finalLocation);
	}

	@Test
	public void testMaxSlot0() {
		System.out.println("\n***Test 29***");
		Controller controller = new Controller();
		Location[] allPossibleLocations = new Location[0];
		controller.imageProcessor.setLocations(allPossibleLocations);
		Controller controllerSpy = spy(controller);
		Controller.park(controllerSpy);
		verify(controllerSpy, times(0)).drive("Parked", new Location());

	}

	@Test
	public void testMaxSlot1() {
		System.out.println("\n***Test 30***");
		Controller controller = new Controller();
		Location[] allPossibleLocations = { new Location(26.87, 98.71) };
		controller.imageProcessor.setLocations(allPossibleLocations);
		controller.slotEvalAttempt = -1;
		Controller controllerSpy = spy(controller);
		Controller.park(controllerSpy);
		verify(controllerSpy, times(3)).drive("Parked", new Location(26.87, 98.71));

	}
	
	@Test
	// verifying gear decision system when lastDecision = "Reverse" and random
	// variable is 0/1/2
	public void testGetDecisionReverse1() {
		System.out.println("\n***Test 31***");
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream stdout = System.out;
		try {
			// setup
			System.setOut(new PrintStream(outContent));
			Controller controller = new Controller();
			Status status = controller.getDecision("Reverse");
			String expected1 = "Random int is 0 and gear is Stop";
			String expected2 = "Random int is 1 and gear is Reverse";
			String expected3 = "Random int is 2 and gear is";
			assertTrue(outContent.toString().trim().equalsIgnoreCase(expected1)
					|| outContent.toString().trim().equalsIgnoreCase(expected2)
					|| outContent.toString().trim().equalsIgnoreCase(expected3));
		} finally {
			System.setOut(stdout);
		}
	}

	@Test
	// verifying gear decision system when lastDecision = "Forward" and random
	// variable is 0/1/2
	public void testGetDecisionForward1() {
		System.out.println("\n***Test 32***");
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream stdout = System.out;
		try {
			// setup
			System.setOut(new PrintStream(outContent));
			Controller controller = new Controller();
			Status status = controller.getDecision("Forward");
			String expected1 = "Random int is 0 and gear is Forward";
			String expected2 = "Random int is 1 and gear is Stop";
			String expected3 = "Random int is 2 and gear is";
			assertTrue(outContent.toString().trim().equalsIgnoreCase(expected1)
					|| outContent.toString().trim().equalsIgnoreCase(expected2)
					|| outContent.toString().trim().equalsIgnoreCase(expected3));
		} finally {
			System.setOut(stdout);
		}
	}
	
	@Test
	// verifying evaluateSlot method call when expectedCarState = "Parked" and
	// slotEvaluated = false
	public void testDriveParkedAndSlotEvaluated1() {
		System.out.println("\n***Test 33***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87, 98.71);
		controllerSpy.drive("Parked", finalLocation);
		verify(controllerSpy, times(1)).evaluateSlot(finalLocation);
	}

	@Test
	// verifying evaluateSlot method call when expectedCarState = "Un-parked" and slotEvaluated is dont care
	public void testDriveUnParkedAndSlotEvaluated1() {
		System.out.println("\n***Test 34***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87, 98.71);
		controllerSpy.drive("Un-parked", finalLocation);
		verify(controllerSpy, times(0)).evaluateSlot(finalLocation);
	}
	@Test
	public void lastDecisionTest(){
		//for all-def (74,(77,T),lastDecision)
		System.out.println("\n***Test 35***");
		Controller controller = new Controller();
		Status status = controller.getDecision("");
		assertEquals("Forward", status.gear);
	}
	
	@Test
	public void statusTest() {
		//for all-def (75,(107,T),status)
		System.out.println("\n***Test 36***");
		Controller controller = new Controller();
		Status status = controller.getDecision("");
		assertTrue((status.angle >= -540.0) && (status.angle <= 540.0));
	}
	@Test
	public void testFinalCarStateDefUse1() {
		// for all-du association (199,213,finalCarState)
		System.out.println("\n***Test 37***");
		Controller controller = new Controller();
		Location[] allPossibleLocations = new Location[0];
		controller.imageProcessor.setLocations(allPossibleLocations);
		controller.cmd = "Park";
		controller.slotEvalAttempt = 1;
		String finalCarState = Controller.park(controller);
		assertEquals("Parking Failed", finalCarState);
	}

	@Test
	public void testFinalCarStateDefUse2() {
		// for all-du association (207,208,finalCarState)
		System.out.println("\n***Test 38***");
		Controller controller = new Controller();
		Location[] allPossibleLocations = { new Location(27.123, 54.871) };
		controller.imageProcessor.setLocations(allPossibleLocations);
		controller.cmd = "Park";
		controller.slotEvalAttempt = 1;
		String finalCarState = Controller.park(controller);
		assertEquals("Parked", finalCarState);
	}
	
	@Test
	public void testMaxSlotsDefUse() {
		// for all-du association (203,204,maxSlots)
		System.out.println("\n***Test 39***");
		Controller controller = new Controller();
		Location[] allPossibleLocations = { new Location(27.123, 54.871), new Location(67.91,18.76) };
		controller.imageProcessor.setLocations(allPossibleLocations);
		controller.cmd = "Park";
		controller.slotEvalAttempt = 2; //so that evaluation is successful in 2nd attempt
		Controller spy = spy(controller);
		String finalCarState = Controller.park(spy);
		verify(spy, times(1)).drive("Parked", new Location(27.123, 54.871));
		verify(spy, times(1)).drive("Parked", new Location(67.91,18.76));
	}
	
	@Test
	public void testMaxAttemptsDefUse() {
		// for all-du association (200,201,maxAttempts)
		System.out.println("\n***Test 40***");
		Controller controller = new Controller();
		Location[] allPossibleLocations = { new Location(27.123, 54.871) };
		controller.imageProcessor.setLocations(allPossibleLocations);
		controller.cmd = "Park";
		controller.slotEvalAttempt = -1; //so that evaluation is never successful
		Controller spy = spy(controller);
		String finalCarState = Controller.park(spy);
		verify(spy, times(3)).drive("Parked", new Location(27.123, 54.871));
	}
	
	@Test
	public void testMDefUse() {
		//for all-du association (200,205,m)
		System.out.println("\n***Test 41***");
		Controller controller = new Controller();
		Location[] allPossibleLocations = { new Location(27.123, 54.871), new Location(67.91,18.76) };
		controller.imageProcessor.setLocations(allPossibleLocations);
		controller.cmd = "Park";
		controller.slotEvalAttempt = 2; //so that evaluation is successful in 2nd attempt
		String finalCarState = Controller.park(controller);
		assertEquals(new Location(67.91,18.76), controller.finalLocation);
	}
	
	@Test
	public void testNDefUse() {
		// for all-du association (202,205,locations)
		// for all-du association (202,207,locations)
		System.out.println("\n***Test 42***");
		Controller controller = new Controller();
		Location[] allPossibleLocations = { new Location(27.123, 54.871), new Location(28.11,76.87), new Location(19.87,67.86) };
		controller.imageProcessor.setLocations(allPossibleLocations);
		controller.cmd = "Park";
		controller.slotEvalAttempt = 3;
		Controller spy = spy(controller);
		String finalCarState = Controller.park(spy);
		assertEquals(new Location(19.87,67.86), spy.finalLocation);
		verify(spy, times(1)).drive("Parked", new Location(27.123, 54.871));
		verify(spy, times(1)).drive("Parked", new Location(28.11,76.87));
		verify(spy, times(1)).drive("Parked", new Location(19.87,67.86));
	}
	@Test
	public void testExpectedCarStateUse1() {
		// for all-use of association (121,(158,T),expectedCarState)
		System.out.println("\n***Test 43***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87,98.71);
		controllerSpy.drive("Parked", finalLocation);
		verify(controllerSpy, times(1)).evaluateSlot(finalLocation);
	}
	
	@Test
	public void testExpectedCarStateUse2() {
		// for all-use of association (121,(158,F),expectedCarState)
		System.out.println("\n***Test 44***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87,98.71);
		controllerSpy.drive("Un-Parked", finalLocation);
		verify(controllerSpy, times(0)).evaluateSlot(finalLocation);
	}
	
	@Test
	public void testSlotEvaluatedUse() {
		//for all-use of association (124,158,slotEvaluated)
		System.out.println("\n***Test 45***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87,98.71);
		controllerSpy.drive("Un-Parked", finalLocation);
		verify(controllerSpy, times(0)).evaluateSlot(finalLocation);
	}
	
	@Test
	public void testLastDecisionUse() {
		// for all-use of association (126,131,lastDecision)
		System.out.println("\n***Test 46***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87,98.71);
		controllerSpy.drive("Parked", finalLocation);
		verify(controllerSpy).getDecision("");
	}
	
	@Test
	public void testCurrentCarLocation1() {
		//for all-use of association (123,(127,T),currentCarLocation)
		System.out.println("\n***Test 47***");
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87,98.71);
		controllerSpy.drive("Parked", finalLocation);
		verify(controllerSpy).getDecision("");
	}
	
	@Test
	public void testCurrentCarLocation2() {
		//for all-use of association (123,(127,F),currentCarLocation)
		System.out.println("\n***Test 48***");
		Controller controller = new Controller();
		Location finalLocation = new Location(27.628210, 76.181052);
		String finalCarState = controller.drive("Parked", finalLocation);
		assertEquals("",finalCarState);
	}
}
