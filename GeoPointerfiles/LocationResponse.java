package com.mkyong.rest.client;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationResponse {
	private ShardData _shards;
	private Hits hits;
	public LocationResponse() {
		super();
	}
	public ShardData get_shards() {
		return _shards;
	}
	public void set_shards(ShardData _shards) {
		this._shards = _shards;
	}
	public Hits getHits() {
		return hits;
	}
	public void setHits(Hits hits) {
		this.hits = hits;
	}
	@Override
	public String toString() {
		return "LocationResponse [ShardData= " + _shards.toString() + ", hits= " + hits.toString() + "]";
	}
}
