import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.*;
import static org.mockito.Mockito.*;

//please note that in all the associations, the numbers signify the line numbers in controller.java file
public class AllDefUseTests {
	@Test
	public void testFinalCarStateDefUse1() {
		// for all-du association (199,213,finalCarState)
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
}
