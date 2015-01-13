package com.mkyong.rest.client;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
	private String lat;
	private String lon;
	public Location() {
		super();
	}
	
	
	public Location(String lat, String lon) {
		super();
		this.lat = lat;
		this.lon = lon;
	}


	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	
}
