package com.vish.geolookupcache;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoInfo {
	private String _index;
	private String _type;
	private String _id;
	private Source _source;
	private List<Double> sort;
	public GeoInfo() {
		super();
	}
	public String get_index() {
		return _index;
	}
	public void set_index(String _index) {
		this._index = _index;
	}
	public String get_type() {
		return _type;
	}
	public void set_type(String _type) {
		this._type = _type;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public Source get_source() {
		return _source;
	}
	public void set_source(Source _source) {
		this._source = _source;
	}
	public List<Double> getSort() {
		return sort;
	}
	public void setSort(List<Double> sort) {
		this.sort = sort;
	}
	
}
