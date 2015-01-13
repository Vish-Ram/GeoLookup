package com.mkyong.rest.client;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Filter {
	private GeoArea geo_distance;

	public Filter(GeoArea geo_distance) {
		super();
		this.geo_distance = geo_distance;
	}

	public GeoArea getGeo_distance() {
		return geo_distance;
	}

	public void setGeo_distance(GeoArea geo_distance) {
		this.geo_distance = geo_distance;
	}
	
}
