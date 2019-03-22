import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.*;
public class AllUsesTests {
	@Test
	public void testExpectedCarStateUse1() {
		// for all-use of association (121,(158,T),expectedCarState)
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87,98.71);
		controllerSpy.drive("Parked", finalLocation);
		verify(controllerSpy, times(1)).evaluateSlot(finalLocation);
	}
	
	@Test
	public void testExpectedCarStateUse2() {
		// for all-use of association (121,(158,F),expectedCarState)
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87,98.71);
		controllerSpy.drive("Un-Parked", finalLocation);
		verify(controllerSpy, times(0)).evaluateSlot(finalLocation);
	}
	
	@Test
	public void testSlotEvaluatedUse() {
		//for all-use of association (124,158,slotEvaluated)
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87,98.71);
		controllerSpy.drive("Un-Parked", finalLocation);
		verify(controllerSpy, times(0)).evaluateSlot(finalLocation);
	}
	
	@Test
	public void testLastDecisionUse() {
		// for all-use of association (126,131,lastDecision)
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87,98.71);
		controllerSpy.drive("Parked", finalLocation);
		verify(controllerSpy).getDecision("");
	}
	
	@Test
	public void testCurrentCarLocation1() {
		//for all-use of association (123,(127,T),currentCarLocation)
		Controller controller = new Controller();
		Controller controllerSpy = spy(controller);
		Location finalLocation = new Location(26.87,98.71);
		controllerSpy.drive("Parked", finalLocation);
		verify(controllerSpy).getDecision("");
	}
	
	@Test
	public void testCurrentCarLocation2() {
		//for all-use of association (123,(127,F),currentCarLocation)
		Controller controller = new Controller();
		Location finalLocation = new Location(27.628210, 76.181052);
		String finalCarState = controller.drive("Parked", finalLocation);
		assertEquals("",finalCarState);
	}

}
