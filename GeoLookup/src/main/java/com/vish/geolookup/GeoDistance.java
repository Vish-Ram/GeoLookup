package com.vish.geolookup;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoDistance {
	private GeoData _geo_distance;

	public GeoDistance(GeoData _geo_distance) {
		super();
		this._geo_distance = _geo_distance;
	}

	public GeoData get_geo_distance() {
		return _geo_distance;
	}

	public void set_geo_distance(GeoData _geo_distance) {
		this._geo_distance = _geo_distance;
	}
	
	
}
