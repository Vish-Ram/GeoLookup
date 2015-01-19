package com.vish.geolookupcache;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Query {
	private Filtered filtered;

	public Query(Filtered filtered) {
		super();
		this.filtered = filtered;
	}

	public Filtered getFiltered() {
		return filtered;
	}

	public void setFiltered(Filtered filtered) {
		this.filtered = filtered;
	}
	
}
