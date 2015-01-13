package com.mkyong.rest.client;

public class NearestGeoPoint {
	private Location location;
	private String state;
	private String zip;
	private String city;
	private String index;
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public NearestGeoPoint(Location location, String state, String zip,
			String city, String index) {
		super();
		this.location = location;
		this.state = state;
		this.zip = zip;
		this.city = city;
		this.index = index;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	@Override 
	public String toString() {
		return "Index: " + index + ", Lat: " + location.getLat() + ", Lon: " + location.getLon() + ", City: "
				+ city + ", State: " + state + ", ZIP: " + zip;
		
	}
	
}
