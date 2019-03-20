
public class ImageProcessor {
	Location[] allPossibleLocations;

	public Location[] getLocations() {
		return allPossibleLocations;
	}
	
	//to be called from Junit test case
	public void setLocations(Location[] locations) {
		allPossibleLocations = locations;
	}
}
