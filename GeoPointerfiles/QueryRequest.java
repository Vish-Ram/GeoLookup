package com.mkyong.rest.client;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryRequest {
	private List<GeoDistance> sort;
	private Query query;
	private int size;
	public QueryRequest(List<GeoDistance> sort, Query query, int size) {
		super();
		this.sort = sort;
		this.query = query;
		this.size = size;
	}
	public List<GeoDistance> getSort() {
		return sort;
	}
	public void setSort(List<GeoDistance> sort) {
		this.sort = sort;
	}
	public Query getQuery() {
		return query;
	}
	public void setQuery(Query query) {
		this.query = query;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
}
