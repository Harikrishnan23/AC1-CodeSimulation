import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.*;
//please note that in all the associations, the numbers signify the line numbers in controller.java file
public class AllDefTests {
	@Test
	public void lastDecisionTest(){
		//for all-def (74,(77,T),lastDecision)
		Controller controller = new Controller();
		Status status = controller.getDecision("");
		assertEquals("Forward", status.gear);
	}
	
	@Test
	public void statusTest() {
		//for all-def (75,(107,T),status)
		Controller controller = new Controller();
		Status status = controller.getDecision("");
		assertTrue((status.angle >= -540.0) && (status.angle <= 540.0));
	}
	
	

}
