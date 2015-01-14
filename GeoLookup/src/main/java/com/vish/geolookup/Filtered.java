package com.vish.geolookup;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Filtered {
	private FilteredQuery query;
	private Filter filter;
	public Filtered(FilteredQuery query, Filter filter) {
		super();
		this.query = query;
		this.filter = filter;
	}
	public FilteredQuery getQuery() {
		return query;
	}
	public void setQuery(FilteredQuery query) {
		this.query = query;
	}
	public Filter getFilter() {
		return filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	
}
