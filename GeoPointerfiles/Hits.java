package com.mkyong.rest.client;

import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Hits {
	private int total;
	private int max_score;
	private List<GeoInfo> hits;
	public Hits() {
		super();
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getMax_score() {
		return max_score;
	}
	public void setMax_score(int max_score) {
		this.max_score = max_score;
	}
	public List<GeoInfo> getHits() {
		return hits;
	}
	public void setHits(List<GeoInfo> hits) {
		this.hits = hits;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Hits ");
		sb.append("[total= " + total);
		sb.append(", max_score= " + max_score);
		sb.append(", hits= " );
		
		Iterator<GeoInfo> geoIter = hits.iterator() ;
		while (geoIter.hasNext()) {
			GeoInfo geoInfo = geoIter.next() ;
			sb.append(geoInfo.toString()) ;
			sb.append(", ") ;
		}
		return sb.toString() ;
	}
}
