import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.*;

public class Tests {
	@Test
	public void parkingSuccessFirstAttempt() {
		String[] args = null;
		String cmd = "Park";
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		InputStream stdin = System.in;
		PrintStream stdout = System.out;
		try {
			//setup
			System.setIn(new ByteArrayInputStream(cmd.getBytes()));
			System.setOut(new PrintStream(outContent));
			
			//data initialization
			ImageProcessor imageProcessor = new ImageProcessor();
			Location[] allPossibleLocations = new Location[1];
			allPossibleLocations[0] = new Location(28.628210, 77.211052);
			imageProcessor.setLocations(allPossibleLocations);
			
			Controller.main(args);
			
		}
		finally {
			System.setIn(stdin);
			System.setOut(stdout);
		}
	}

}
