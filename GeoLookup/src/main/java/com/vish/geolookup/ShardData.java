package com.vish.geolookup;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShardData {
	private int total;
	private int successful;
	private int failed;
	public ShardData() {
		super();
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getSuccessful() {
		return successful;
	}
	public void setSuccessful(int successful) {
		this.successful = successful;
	}
	public int getFailed() {
		return failed;
	}
	public void setFailed(int failed) {
		this.failed = failed;
	}
	@Override
	public String toString() {
		return "ShardData [total= " + total + ", successful= " + successful + ", " +
				"failed= " + failed + "]";
	}
}
