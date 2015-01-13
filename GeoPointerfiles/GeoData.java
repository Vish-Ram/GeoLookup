package com.mkyong.rest.client;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoData {
	private Location location;
	private String order;
	private String unit;
	
	public GeoData(Location location, String order, String unit) {
		super();
		this.location = location;
		this.order = order;
		this.unit = unit;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
}
