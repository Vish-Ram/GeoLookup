package com.vish.geolookupcache;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoArea {
	private String distance;
	private Location location;
	public GeoArea(String distance, Location location) {
		super();
		this.distance = distance;
		this.location = location;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	
}
