package com.vish.geolookup;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilteredQuery {
	private MatchAll match_all;

	public FilteredQuery(MatchAll match_all) {
		super();
		this.match_all = match_all;
	}

	public MatchAll getMatch_all() {
		return match_all;
	}

	public void setMatch_all(MatchAll match_all) {
		this.match_all = match_all;
	}
	
}
