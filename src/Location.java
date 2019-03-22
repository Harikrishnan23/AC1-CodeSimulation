
public class Location {
	public double latitude;
	public double longitude;
	
	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Location() {
		
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof Location) {
			Location loc = (Location)obj;
			return (this.latitude == loc.latitude) && (this.longitude == loc.longitude);
		}
		return false;
	}
	
	
}
